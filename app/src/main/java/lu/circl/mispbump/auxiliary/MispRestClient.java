package lu.circl.mispbump.auxiliary;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lu.circl.mispbump.interfaces.MispRestInterface;
import lu.circl.mispbump.models.restModels.MispOrganisation;
import lu.circl.mispbump.models.restModels.MispRole;
import lu.circl.mispbump.models.restModels.MispServer;
import lu.circl.mispbump.models.restModels.MispUser;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.models.restModels.Version;
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


    private static MispRestClient instance;
    private MispRestInterface mispRestInterface;

    public static MispRestClient getInstance(String url, String authkey) {
        if (instance == null) {
            instance = new MispRestClient();
        }

        instance.initMispRestInterface(url, authkey);

        return instance;
    }

    private void initMispRestInterface(String url, String authkey) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getCustomClient(true, true, authkey))
                    .build();

            mispRestInterface = retrofit.create(MispRestInterface.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param unsafe  whether to accept all certificates or only trusted ones
     * @param logging whether to log Retrofit calls (for debugging)
     * @return {@link OkHttpClient}
     */
    private OkHttpClient getCustomClient(boolean unsafe, boolean logging, final String authkey) {
        try {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (unsafe) {
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

                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            if (logging) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }

            // create authorization interceptor
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json");
                    ongoing.addHeader("Content-Type", "application/json");
                    ongoing.addHeader("Authorization", authkey);
                    return chain.proceed(ongoing.build());
                }
            });

            return builder.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Check via pyMispRoute if server is available
     *
     * @param callback {@link AvailableCallback}
     */
    public void isAvailable(final AvailableCallback callback) {
        Call<Version> call = mispRestInterface.pyMispVersion();
        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(@NonNull Call<Version> call, @NonNull Response<Version> response) {
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
            public void onFailure(@NonNull Call<Version> call, @NonNull Throwable t) {
                callback.unavailable(extractError(t));
            }
        });
    }

    public void getRoles(final AllRolesCallback callback) {
        Call<List<MispRole>> call = mispRestInterface.getRoles();
        call.enqueue(new Callback<List<MispRole>>() {
            @Override
            public void onResponse(Call<List<MispRole>> call, Response<List<MispRole>> response) {

                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                    return;
                }

                List<MispRole> mispRoles = response.body();
                assert mispRoles != null;

                Role[] roles = new Role[mispRoles.size()];

                for (int i = 0; i < roles.length; i++) {
                    roles[i] = mispRoles.get(i).role;
                }

                callback.success(roles);
            }

            @Override
            public void onFailure(Call<List<MispRole>> call, Throwable t) {
                callback.failure(extractError(t));
            }
        });
    }


    /**
     * Fetches information about the user that is associated with saved auth key.
     *
     * @param callback {@link UserCallback} wrapper to return user directly
     */
    public void getMyUser(final UserCallback callback) {
        Call<MispUser> call = mispRestInterface.getMyUserInformation();

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(@NonNull Call<MispUser> call, @NonNull Response<MispUser> response) {
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
            public void onFailure(@NonNull Call<MispUser> call, @NonNull Throwable t) {
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
        Call<MispUser> call = mispRestInterface.getUser(userId);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(@NonNull Call<MispUser> call, @NonNull Response<MispUser> response) {
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
            public void onFailure(@NonNull Call<MispUser> call, @NonNull Throwable t) {
                t.printStackTrace();
                callback.failure(t.getMessage());
            }
        });
    }

    public void getUser(final String emailAddress, final UserCallback callback) {
        getAllUsers(new AllUsersCallback() {
            @Override
            public void success(User[] users) {
                for (User user : users) {
                    if (user.getEmail().equals(emailAddress)) {
                        callback.success(user);
                        return;
                    }
                }

                callback.failure("Could not find user with email address {" + emailAddress + "}");
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getAllUsers(final AllUsersCallback callback) {
        Call<List<MispUser>> call = mispRestInterface.getAllUsers();

        call.enqueue(new Callback<List<MispUser>>() {
            @Override
            public void onResponse(@NonNull Call<List<MispUser>> call, @NonNull Response<List<MispUser>> response) {
                if (!response.isSuccessful()) {
                    callback.failure("Failed onResponse");
                    return;
                }

                List<MispUser> mispUsers = response.body();
                assert mispUsers != null;

                User[] users = new User[mispUsers.size()];

                for (int i = 0; i < users.length; i++) {
                    users[i] = mispUsers.get(i).user;
                }

                callback.success(users);
            }

            @Override
            public void onFailure(@NonNull Call<List<MispUser>> call, @NonNull Throwable t) {
                callback.failure(extractError(t));
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
        Call<MispUser> call = mispRestInterface.addUser(user);

        call.enqueue(new Callback<MispUser>() {
            @Override
            public void onResponse(@NonNull Call<MispUser> call, @NonNull Response<MispUser> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    assert response.body() != null;
                    callback.success(response.body().user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MispUser> call, @NonNull Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }


    // --- organisation routes ---

    /**
     * Get an organisation by a given organisation id.
     *
     * @param orgId    organisation identifier
     * @param callback {@link OrganisationCallback} wrapper to return a organisation directly
     */
    public void getOrganisation(int orgId, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestInterface.getOrganisation(orgId);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(@NonNull Call<MispOrganisation> call, @NonNull Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    if (response.body() != null) {
                        callback.success(response.body().organisation);
                    } else {
                        callback.failure("Response was empty");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MispOrganisation> call, @NonNull Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    public void getOrganisation(final String uuid, final OrganisationCallback callback) {
        getAllOrganisations(new AllOrganisationsCallback() {
            @Override
            public void success(Organisation[] organisations) {
                for (Organisation organisation : organisations) {
                    if (organisation.getUuid().equals(uuid)) {
                        callback.success(organisation);
                        return;
                    }
                }

                callback.failure("Could not find organisation with UUID {" + uuid + "}");
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getAllOrganisations(final AllOrganisationsCallback callback) {
        Call<List<MispOrganisation>> call = mispRestInterface.getAllOrganisations();

        call.enqueue(new Callback<List<MispOrganisation>>() {
            @Override
            public void onResponse(@NonNull Call<List<MispOrganisation>> call, @NonNull Response<List<MispOrganisation>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                List<MispOrganisation> mispOrganisations = response.body();

                assert mispOrganisations != null;

                Organisation[] organisations = new Organisation[mispOrganisations.size()];

                for (int i = 0; i < mispOrganisations.size(); i++) {
                    organisations[i] = mispOrganisations.get(i).organisation;
                }

                callback.success(organisations);
            }

            @Override
            public void onFailure(@NonNull Call<List<MispOrganisation>> call, @NonNull Throwable t) {
                callback.failure(extractError(t));
            }
        });
    }

    /**
     * Add a given organisation to the MISP instance referenced by url in preferences.
     *
     * @param organisation organisation to add
     * @param callback     {@link OrganisationCallback} wrapper to return the created organisation directly
     */
    public void addOrganisation(Organisation organisation, final OrganisationCallback callback) {
        Call<MispOrganisation> call = mispRestInterface.addOrganisation(organisation);

        call.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(@NonNull Call<MispOrganisation> call, @NonNull Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    assert response.body() != null;
                    callback.success(response.body().organisation);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MispOrganisation> call, @NonNull Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    // --- server routes ---

    /**
     * Get all servers on MISP instance.
     *
     * @param callback {@link OrganisationCallback} wrapper to return a list of servers directly
     */
    public void getAllServers(final AllServersCallback callback) {
        Call<List<MispServer>> call = mispRestInterface.getAllServers();

        call.enqueue(new Callback<List<MispServer>>() {
            @Override
            public void onResponse(@NonNull Call<List<MispServer>> call, @NonNull Response<List<MispServer>> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    List<MispServer> mispServers = response.body();
                    assert mispServers != null;

                    Server[] servers = new Server[mispServers.size()];

                    for (int i = 0; i < servers.length; i++) {
                        servers[i] = mispServers.get(i).getServer();
                    }
                    callback.success(servers);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MispServer>> call, @NonNull Throwable t) {
                callback.failure(t.getMessage());
            }
        });
    }

    public void getAllServers(final AllRawServersCallback callback) {
        Call<List<MispServer>> call = mispRestInterface.getAllServers();

        call.enqueue(new Callback<List<MispServer>>() {
            @Override
            public void onResponse(@NonNull Call<List<MispServer>> call, @NonNull Response<List<MispServer>> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    callback.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MispServer>> call, @NonNull Throwable t) {
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
        Call<Server> call = mispRestInterface.addServer(server);

        call.enqueue(new Callback<Server>() {
            @Override
            public void onResponse(@NonNull Call<Server> call, @NonNull Response<Server> response) {
                if (!response.isSuccessful()) {
                    callback.failure(extractError(response));
                } else {
                    callback.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Server> call, @NonNull Throwable t) {
                callback.failure(t.getMessage());
                throw new RuntimeException(t);
            }
        });
    }

    // --- error parsing ---

    /**
     * Converts error {@link Response}s to human readable info.
     *
     * @param response erroneous response
     * @param <T>      type of response
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
                return "Authentification failed";

            // not found
            case 404:
                return "Not found";

            // No permission (method not allowed)
            case 405:
                return "No admin permission";

            default:
                return response.message();
        }
    }

    /**
     * Converts a {@link Throwable} to a human readable error message.
     *
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

        try {
            throw new Exception(t);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t.getMessage();
    }

    // interfaces

    public interface AvailableCallback {
        void available();

        void unavailable(String error);
    }

    public interface UserCallback {
        void success(User user);

        void failure(String error);
    }

    public interface AllUsersCallback {
        void success(User[] users);

        void failure(String error);
    }

    public interface OrganisationCallback {
        void success(Organisation organisation);

        void failure(String error);
    }

    public interface AllOrganisationsCallback {
        void success(Organisation[] organisations);

        void failure(String error);
    }

    public interface ServerCallback {
        void success(Server server);

        void failure(String error);
    }

    public interface AllServersCallback {
        void success(Server[] servers);

        void failure(String error);
    }

    public interface AllRawServersCallback {
        void success(List<MispServer> mispServers);

        void failure(String error);
    }

    public interface AllRolesCallback {
        void success(Role[] roles);

        void failure(String error);
    }
}
