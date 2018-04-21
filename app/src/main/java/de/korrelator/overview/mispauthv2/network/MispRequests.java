package de.korrelator.overview.mispauthv2.network;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.korrelator.overview.mispauthv2.R;

public class MispRequests {

    private Context context;
    private RequestQueue queue;
    private String baseURL;


    public MispRequests(Context context, String baseURL){

        this.context = context.getApplicationContext();
        this.baseURL = baseURL;

        queue = Volley.newRequestQueue(this.context);
    }


    public void GetSyncUsers(final GetSyncUsersResponse resp){

        String currentURL = baseURL + "/admin/users";

        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                int userCount = response.length();

                List<Integer> userIDList = new ArrayList<>();

                for(int i = 0; i < userCount; i++){
                    try{
                        JSONObject root = response.getJSONObject(i);
                        JSONObject role = root.getJSONObject("Role");
                        JSONObject user = root.getJSONObject("User");

                        if(role.getInt("id") == 5){
                            userIDList.add(user.getInt("id"));
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                resp.result(userIDList.toArray(new Integer[userIDList.size()]));
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp.error(error);
            }
        };

        queue.add(arrayRequest(Request.Method.GET, currentURL, null, responseListener, errorListener));
    }

    public void AddUser(JSONObject body, final AddUserResponse resp){

        String currentURL = baseURL + "/admin/users/add";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject user = response.getJSONObject("User");
                    resp.result(user.getInt("id"), user.getString("authkey"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp.error(error);
            }
        };

        queue.add(objectRequest(Request.Method.POST, currentURL, body, responseListener, errorListener));
    }

    public void EditUser(int userID, JSONObject body, final EditUserResponse resp){
        String currentURL = baseURL + "/admin/users/edit/" + userID;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resp.result(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp.error(error);
            }
        };

        queue.add(objectRequest(Request.Method.POST, currentURL, body, responseListener, errorListener));
    }

    public void AddOrganisation(JSONObject body, final AddOrganisationResponse response){

        String currentURL = baseURL + "/admin/organisations/add";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                try {
                    JSONObject org = resp.getJSONObject("Organisation");
                    response.result(org.getInt("id"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                response.error(error);
            }
        };

        queue.add(objectRequest(Request.Method.POST, currentURL, body, responseListener, errorListener));
    }

    public void EditOrganisation(int orgID, JSONObject updateBody, final EditOrganisationResponse resp){

        String currentURL = baseURL + "/admin/organisations/edit/" + orgID;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resp.result(true);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp.error(error);
            }
        };

        queue.add(objectRequest(Request.Method.POST, currentURL, updateBody, responseListener, errorListener));
    }

    public void AddServer(JSONObject body, final AddServerResponse resp){
        String currentURL = baseURL + "/servers/add";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resp.result(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp.error(error);
            }
        };

        queue.add(objectRequest(Request.Method.POST, currentURL, body, responseListener, errorListener));
    }


    private JsonObjectRequest objectRequest(int method, String url, @Nullable JSONObject body, Response.Listener listener, Response.ErrorListener errorListener){
        return new JsonObjectRequest(
                method,
                url,
                body,
                listener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();

                params.put("Authorization", context.getString(R.string.api_key));
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json; utf-8");

                return params;
            }
        };
    }

    private JsonArrayRequest arrayRequest(int method, String url, @Nullable JSONArray body, Response.Listener listener, Response.ErrorListener errorListener){
        return new JsonArrayRequest(
                method,
                url,
                body,
                listener,
                errorListener){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();

                params.put("Authorization", context.getString(R.string.api_key));
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json; utf-8");

                return params;
            }
        };
    }


    public interface GetSyncUsersResponse{
        void result(Integer[] userIDs);
        void error(VolleyError volleyError);
    }

    public interface AddOrganisationResponse{
        void result(int orgID);
        void error(VolleyError error);
    }

    public interface EditOrganisationResponse{
        void result(boolean success);
        void error(VolleyError volleyError);
    }

    public interface AddUserResponse {
        void result(int id, String authKey);
        void error(VolleyError volleyError);
    }

    public interface EditUserResponse{
        void result(JSONObject msg);
        void error(VolleyError volleyError);
    }

    public interface AddServerResponse{
        void result(JSONObject result);
        void error(VolleyError volleyError);
    }
}
