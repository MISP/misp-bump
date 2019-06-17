package lu.circl.mispbump.restful_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lu.circl.mispbump.auxiliary.PreferenceManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementation of the RetroFit2 Misp client.
 * In order to conveniently use this api some wrapper interfaces are implemented to return the requested API endpoint as java object.
 */
public class MispRestClient {

    private static final String TAG = "restClient";

    public interface AvailableCallback {
        void available();

        void unavailable(String error);
    }

    public interface UserCallback {
        void success(User user);

        void failure(String error);
    }

    public interface OrganisationCallback {
        void success(Organisation organisation);

        void failure(String error);
    }

    public interface OrganisationsCallback {
        void success(Organisation[] organisations);
        void failure(String error);
    }

    public interface ServerCallback {
        void success(List<MispServer> servers);

        void success(MispServer server);

        void success(Server server);

        void failure(String error);
    }


    private PreferenceManager preferenceManager;
    private MispRestService mispRestService;

    /**
     * Initializes the rest client to communicate with a MISP instance.
     *
     * @param context needed to access the preferences for loading credentials
     */
    public MispRestClient(Context context) {
        preferenceManager = PreferenceManager.getInstance(context);

        String url = preferenceManager.getServerUrl();

        Log.i(TAG, "URL: " + url);

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build();

            mispRestService = retrofit.create(MispRestService.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * NOTE: for development only!
     * <p>
     * Accepts all certificates including self signed.
     *
     * @return {@link OkHttpClient} which accepts all certificates
     */
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // create logging interceptor
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);

            // create authorization interceptor
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json");
                    ongoing.addHeader("Content-Type", "application/json");
                    ongoing.addHeader("Authorization", preferenceManager.getAutomationKey());
                    return chain.proceed(ongoing.build());
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // status routes

    /**
     * Check via pyMispRoute if server is available
     *
     * @param callback {@link AvailableCallback}
     */
    public void isAvailable(final AvailableCallback callback) {
        Call<Version> call = mispRestService.pyMispVersion();
        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 403) {
                        callback.available();
                        return;
                    }

                    callback.unavailable(extractError(response));
                } else {
                    callback.available();
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                callback.unavailable(extractError(t));
            }
        });
    }

    // user routes

    /**
     * Fetches information about the user that is associated with saved auth key.
     *
     * @param callback {@link UserCallback} wrapper to return user directly
     */
    public void getMyUser(final UserCallback callback) {
        Call<MispUser> call = mispRestService.getMyUserInformation();

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    if (response.body() != null) {
                        callback.success(response.body().user);
                    } else {
                        callback.failure("response body was null");
                    }
                }
            }

            @Override
            public void onFailure(Call<MispUser> call, Throwable t) {
                t.printStackTrace();
                callback.failure(t.getMessage());
            }
        });
    }

    /**
     * Get an user with specific ID.
     *
     * @param userId   user identifier
     * @param callback {@link UserCallback} wrapper to return user directly
     */
    public void getUser(int userId, final UserCallback callback) {
        Call<MispUser> call = mispRestService.getUser(userId);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    if (response.body() != null) {
                        callback.success(response.body().user);
                    } else {
                        callback.failure("response body was null");
                    }
                }
            }

            @Override
            public void onFailure(Call<MispUser> call, Throwable t) {
                t.printStackTrace();
                callback.failure(t.getMessage());
            }
        });
    }

    /**
     * Add a given user to the MISP instance referenced by url in preferences.
     *
     * @param user     user to add
     * @param callback {@link UserCallback} wrapper to return the created user directly
     */
    public void addUser(User user, final UserCallback callback) {
        Call<MispUser> call = mispRestService.addUser(user);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    assert response.body() != null;
                    callback.success(response.body().user);
                }
            }

            @Override
            public void onFailure(Call<MispUser> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }


    // organisation routes

    /**
     * Get an organisation by a given organisation id.
     *
     * @param orgId    organisation identifier
     * @param callback {@link OrganisationCallback} wrapper to return a organisation directly
     */
    public void getOrganisation(int orgId, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestService.getOrganisation(orgId);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(Call<MispOrganisation> call, Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    if (response.body() != null) {
                        callback.success(response.body().organisation);
                    } else {
                        callback.failure("Response body was nul");
                    }
                }
            }

            @Override
            public void onFailure(Call<MispOrganisation> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    public Organisation[] getAllOrganisations() throws IOException {
        Call<List<MispOrganisation>> call = mispRestService.getAllOrganisations();
        Response<List<MispOrganisation>> response = call.execute();

        List<MispOrganisation> mispOrganisations = response.body();
        Organisation[] organisations = new Organisation[mispOrganisations.size()];

        for (int i = 0; i < mispOrganisations.size(); i++) {
            organisations[i] = mispOrganisations.get(i).organisation;
        }

        return organisations;

//        call.enqueue(new Callback<List<MispOrganisation>>() {
//            @Override
//            public void onResponse(Call<List<MispOrganisation>> call, Response<List<MispOrganisation>> response) {
//                if (!response.isSuccessful()) {
//                    // TODO handle
//                    return;
//                }
//
//                List<MispOrganisation> mispOrganisations = response.body();
//
//                assert mispOrganisations != null;
//
//                Organisation[] organisations = new Organisation[mispOrganisations.size()];
//
//                for (int i = 0; i < mispOrganisations.size(); i++) {
//                    organisations[i] = mispOrganisations.get(i).organisation;
//                }
//
//                callback.success(organisations);
//            }
//
//            @Override
//            public void onFailure(Call<List<MispOrganisation>> call, Throwable t) {
//                callback.failure(extractError(t));
//            }
//        });
    }

    /**
     * Add a given organisation to the MISP instance referenced by url in preferences.
     *
     * @param organisation organisation to add
     * @param callback     {@link OrganisationCallback} wrapper to return the created organisation directly
     */
    public void addOrganisation(Organisation organisation, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestService.addOrganisation(organisation);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(Call<MispOrganisation> call, Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    assert response.body() != null;
                    callback.success(response.body().organisation);
                }
            }

            @Override
            public void onFailure(Call<MispOrganisation> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    // server routes

    /**
     * Get all servers on MISP instance.
     *
     * @param callback {@link OrganisationCallback} wrapper to return a list of servers directly
     */
    public void getServers(final ServerCallback callback) {
        Call<List<MispServer>> call = mispRestService.getServers();

        call.enqueue(new Callback<List<MispServer>>() {
            @Override
            public void onResponse(Call<List<MispServer>> call, Response<List<MispServer>> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    callback.success(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MispServer>> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    /**
     * Add a server to the MISP instance
     *
     * @param server   the server to create
     * @param callback {@link ServerCallback} wrapper to return the created server directly
     */
    public void addServer(Server server, final ServerCallback callback) {
        Call<Server> call = mispRestService.addServer(server);

        call.enqueue(new Callback<Server>() {
            @Override
            public void onResponse(Call<Server> call, Response<Server> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    callback.success(response.body());
                }
            }

            @Override
            public void onFailure(Call<Server> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    // error parsing

    /**
     * Converts error {@link Response}s to human readable info.
     * @param response erroneous response
     * @param <T> type of response
     * @return human readable String that describes the error
     */
    private <T> String extractError(Response<T> response) {
        switch (response.code()) {
            // bad request (malformed)
            case 400:
                return "Bad request";

            // unauthorized
            case 401:
                return "Unauthorized";

            // forbidden
            case 403:
                try {
                    assert response.errorBody() != null;
                    JSONObject jsonError = new JSONObject(response.errorBody().string());

                    String name = jsonError.getString("name") + "\n";

                    if (name.startsWith("Authentication failed")) {
                        return "Authentication failed";
                    }

                    String reasons = "";
                    JSONObject errorReasons = jsonError.getJSONObject("errors");

                    Iterator<String> errorKeys = errorReasons.keys();

                    while (errorKeys.hasNext()) {
                        reasons = reasons.concat(errorReasons.getString(errorKeys.next()) + "\n");
                    }

                    return name + reasons;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "Could not parse (403) error";

            // not found
            case 404:
                return "Not found";
        }

        return "Unknown error";
    }

    /**
     * Converts a {@link Throwable} to a human readable error message.
     * @param t throwable
     * @return human readable String that describes the error.
     */
    private String extractError(Throwable t) {

        if (t.getCause() instanceof CertificateException) {
            return "Trust anchor for certification path not found.\nSelf signed certificates are not supported.";
        }

        if (t instanceof SSLHandshakeException) {
            return "SSL Handshake Error";
        }

        if (t instanceof NoRouteToHostException) {
            return "Server is not available (no route to host)";
        }

        return t.getMessage();
    }
}