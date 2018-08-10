package de.overview.wg.its.mispbump.network;

import android.content.Context;
import android.support.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import de.overview.wg.its.mispbump.auxiliary.PreferenceManager;
import de.overview.wg.its.mispbump.model.Organisation;
import de.overview.wg.its.mispbump.model.Server;
import de.overview.wg.its.mispbump.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple JSON based API to communicate with MISP-Instances
 */
public class MispRequest {

	private static MispRequest instance;

	private RequestQueue requestQueue;
	private PreferenceManager preferenceManager;
	private String serverUrl, automationKey;

	/**
	 * @param context for Volley and PreferenceManager
	 */
	private MispRequest(Context context, boolean loadSavedCredentials) {
		requestQueue = Volley.newRequestQueue(context);

		if (loadSavedCredentials) {
			preferenceManager = PreferenceManager.Instance(context);
			loadSavedCredentials();
		}
	}

	private void loadSavedCredentials() {
		serverUrl = preferenceManager.getMyServerUrl();
		automationKey = preferenceManager.getMyServerAutomationKey();
	}

	public void testConnection(final ConnectionCallback callback) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onResult(true);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onResult(false);
            }
        };

        Request r = objectRequest(Request.Method.GET,
                serverUrl + "/servers/getPyMISPVersion.json",
                null,
                listener,
                errorListener);

        requestQueue.add(r);
    }

	/**
	 * @param orgId    organisation ID on the MISP-Instance
	 * @param callback returns a single Organisation-JSON
	 */
	public void getOrganisation(int orgId, final OrganisationCallback callback) {

		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					callback.onResult(response.getJSONObject(Organisation.ROOT_KEY));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};


		Request r = objectRequest(Request.Method.GET,
				serverUrl + "/organisations/view/" + orgId,
				null,
				listener,
				errorListener);

		requestQueue.add(r);
	}

	/**
	 * Typically used to get the organisation linked with this user
	 *
	 * @param callback return user associated with this API-Key
	 */
	public void getMyUser(final UserCallback callback) {

		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				try {
					callback.onResult(response.getJSONObject(User.ROOT_KEY));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}

				callback.onResult(response);
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};

		if (serverUrl.isEmpty() || automationKey.isEmpty()) {
			return;
		}

		Request r = objectRequest(
				Request.Method.GET,
				serverUrl + "/users/view/me",
				null,
				listener,
				errorListener);

		requestQueue.add(r);
	}

	/**
	 * @param organisation The organisation that will be added
	 * @param callback     returns complete organisation JSON
	 */
	public void addOrganisation(Organisation organisation, final OrganisationCallback callback) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					callback.onResult(response.getJSONObject(Organisation.ROOT_KEY));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};

		Request r = objectRequest(
				Request.Method.POST,
				serverUrl + "/admin/organisations/add",
				organisation.toJSON(),
				listener,
				errorListener
		);

		requestQueue.add(r);
	}

	public void getOrganisations(final OrganisationsCallback callback) {
		Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {

				JSONArray resultArray = new JSONArray();

				int orgCount = response.length();

				for(int i = 0; i < orgCount; i++) {
					try {
						resultArray.put(response.getJSONObject(i).getJSONObject("Organisation"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				callback.onResult(resultArray);
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};

		Request r = arrayRequestWithJsonObject(
				Request.Method.GET,
				serverUrl + "/organisations/index",
				null,
				listener,
				errorListener);

		requestQueue.add(r);
	}

	public void addUser(User user, final UserCallback callback) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					callback.onResult(response.getJSONObject(User.ROOT_KEY));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}

				callback.onResult(response);
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};

		Request r = objectRequest(
				Request.Method.POST,
				serverUrl + "/admin/users/add",
				user.toJSON(),
				listener,
				errorListener
		);

		requestQueue.add(r);
	}

	public void addServer(Server server, final ServerCallback callback) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					callback.onResult(response.getJSONObject(Server.ROOT_KEY));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}

				callback.onResult(response);
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};

		Request r = objectRequest(
				Request.Method.POST,
				serverUrl + "/servers/add",
				server.toJSON(),
				listener,
				errorListener
		);

		requestQueue.add(r);
	}


	private JsonArrayRequestWithJsonObject arrayRequestWithJsonObject(int method, String url,
	                                                                  @Nullable JSONObject body,
	                                                                  Response.Listener<JSONArray> listener,
	                                                                  Response.ErrorListener errorListener) {

		return new JsonArrayRequestWithJsonObject(method, url, body, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();

				params.put("Authorization", automationKey);
				params.put("Accept", "application/json");
				params.put("Content-Type", "application/json; utf-8");

				return params;
			}
		};
	}

	private JsonObjectRequest objectRequest(int method, String url,
	                                        @Nullable JSONObject body,
	                                        Response.Listener<JSONObject> listener,
	                                        Response.ErrorListener errorListener) {

		return new JsonObjectRequest(method, url, body, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();

				params.put("Authorization", automationKey);
				params.put("Accept", "application/json");
				params.put("Content-Type", "application/json; utf-8");

				return params;
			}
		};
	}

	public void setServerCredentials(String serverUrl, String automationKey) {
		this.serverUrl = serverUrl;
		this.automationKey = automationKey;
	}

	public static MispRequest Instance(Context context, boolean loadSavedCredentials) {

		if (instance == null) {
			instance = new MispRequest(context, loadSavedCredentials);
		}

		return instance;
	}


	public interface ConnectionCallback {
	    void onResult(boolean connected);
    }
	public interface OrganisationsCallback {
		void onResult(JSONArray organisations);
		void onError(VolleyError volleyError);
	}
	public interface OrganisationCallback {
		void onResult(JSONObject organisationInformation);
		void onError(VolleyError volleyError);
	}
	public interface UserCallback {
		void onResult(JSONObject userInformation);

		void onError(VolleyError volleyError);
	}
	public interface ServerCallback {
		void onResult(JSONObject server);
		void onError(VolleyError volleyError);
	}
}