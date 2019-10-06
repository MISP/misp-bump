package lu.circl.mispbump.interfaces;


import java.util.List;

import lu.circl.mispbump.models.restModels.MispOrganisation;
import lu.circl.mispbump.models.restModels.MispRole;
import lu.circl.mispbump.models.restModels.MispServer;
import lu.circl.mispbump.models.restModels.MispUser;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.models.restModels.Version;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * RetroFit2 interface for communication with misp instances
 */
public interface MispService {

    // settings routes

    @GET("servers/getPyMISPVersion")
    Call<Version> pyMispVersion();

    @GET("admin/roles")
    Call<List<MispRole>> getRoles();

    // user routes

    @GET("users/view/me")
    Call<MispUser> getMyUserInformation();

    @GET("users/view/{value}")
    Call<MispUser> getUser(@Path("value") int userId);

    @GET("admin/users")
    Call<List<MispUser>> getAllUsers();

    @POST("admin/users/add")
    Call<MispUser> addUser(@Body User user);

    // organisation routes

    @GET("organisations/view/{value}")
    Call<MispOrganisation> getOrganisation(@Path("value") int orgId);

    @GET("organisations")
    Call<List<MispOrganisation>> getAllOrganisations();

    @POST("admin/organisations/add")
    Call<MispOrganisation> addOrganisation(@Body Organisation organisation);

    // server routes

    @GET("servers/index")
    Call<List<MispServer>> getAllServers();

    @POST("servers/add")
    Call<Server> addServer(@Body Server server);
}
