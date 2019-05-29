package lu.circl.mispbump.restful_client;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
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

    public interface UserCallback {
        void success(User user);
        void failure(String error);
    }

    public interface OrganisationCallback {
        void success(Organisation organisation);
        void failure(String error);
    }

    public interface ServerCallback {
        void success(List<MispServer> servers);
        void success(MispServer server);
        void failure(String error);
    }


    private PreferenceManager preferenceManager;
    private MispRestService mispRestService;

    /**
     * Initializes the rest client to communicate with a MISP instance.
     * @param context needed to access the preferences for loading credentials
     */
    public MispRestClient(Context context) {
        preferenceManager = PreferenceManager.getInstance(context);

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(preferenceManager.getServerUrl())
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
     * Accepts all certificates so self signed certs are also accepted.
     * @return OkHttpClient which accepts all certificates
     */
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
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

    // user routes

    /**
     * Fetches information about the user that is associated with saved auth key.
     * @param callback wrapper to return a user directly
     */
    public void getMyUser(final UserCallback callback) {
        Call<MispUser> call = mispRestService.getMyUserInformation();

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if(!response.isSuccessful()) {
                    callback.failure("" + response.code());
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
     * @param userId user identifier
     * @param callback wrapper to return user directly
     */
    public void getUser(int userId, final UserCallback callback) {
        Call<MispUser> call = mispRestService.getUser(userId);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if(!response.isSuccessful()) {
                    callback.failure("" + response.code());
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
     * @param user user to add
     * @param callback wrapper to return the created user directly
     */
    public void addUser(User user, final UserCallback callback) {
        Call<MispUser> call = mispRestService.addUser(user);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(Call<MispUser> call, Response<MispUser> response) {
                if (!response.isSuccessful()) {
                    callback.failure("" + response.code());
                    return;
                }

                callback.success(response.body().user);
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
     * @param orgId organisation identifier
     * @param callback wrapper to return a organisation directly
     */
    public void getOrganisation(int orgId, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestService.getOrganisation(orgId);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(Call<MispOrganisation> call, Response<MispOrganisation> response) {
                if(!response.isSuccessful()) {
                    callback.failure("" + response.code());
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

    /**
     * Add a given organisation to the MISP instance referenced by url in preferences.
     * @param organisation organisation to add
     * @param callback wrapper to return the created organisation directly
     */
    public void addOrganisation(Organisation organisation, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestService.addOrganisation(organisation);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(Call<MispOrganisation> call, Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    callback.failure("" + response.code());
                    return;
                }

                callback.success(response.body().organisation);
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
     * @param callback wrapper to return a list of servers directly
     */
    public void getServers(final ServerCallback callback) {
        Call<List<MispServer>> call = mispRestService.getServers();

        call.enqueue(new Callback<List<MispServer>>() {
            @Override
            public void onResponse(Call<List<MispServer>> call, Response<List<MispServer>> response) {
                if (!response.isSuccessful()) {
                    callback.failure("" + response.code());
                    return;
                }

                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<List<MispServer>> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    /**
     * Add a server to the MISP instance
     * @param server the server to create
     * @param callback wrapper to return the created server directly
     */
    public void addServer(MispServer server, final ServerCallback callback) {
        Call<MispServer> call = mispRestService.addServer(server);

        call.enqueue(new Callback<MispServer>() {
            @Override
            public void onResponse(Call<MispServer> call, Response<MispServer> response) {
                if (!response.isSuccessful()) {
                    callback.failure("" + response.code());
                    return;
                }

                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<MispServer> call, Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }
}