package lu.circl.mispbump.restful_client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * RetroFit2 interface for communication with misp instances
 */
public interface MispRestService {

    // settings routes

    @GET("servers/getPyMISPVersion")
    Call<Version> pyMispVersion();

    // user routes

    @GET("users/view/me")
    Call<MispUser> getMyUserInformation();

    @GET("users/view/{value}")
    Call<MispUser> getUser(@Path("value") int userId);

    @POST("admin/users/add")
    Call<MispUser> addUser(@Body User user);

    // organisation routes

    @GET("organisations/view/{value}")
    Call<MispOrganisation> getOrganisation(@Path("value") int orgId);

    @POST("admin/organisations/add")
    Call<MispOrganisation> addOrganisation(@Body Organisation organisation);

    // server routes

    @GET("servers/index")
    Call<List<MispServer>> getServers();

//    @POST("servers/add")
//    Call<MispServer> addServer(@Body MispServer server);

    @POST("servers/add")
    Call<Server> addServer(@Body Server server);
}