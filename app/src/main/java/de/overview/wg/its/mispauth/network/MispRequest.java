package de.overview.wg.its.mispauth.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.model.Organisation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple JSON based API to communicate with MISP-Instances
 */
public class MispRequest {

	private static final String TAG = "DEBUG";
	private static MispRequest instance;

	private RequestQueue requestQueue;
	private PreferenceManager preferenceManager;
	private String serverUrl, apiKey;

	/**
	 * @param context for Volley and PreferenceManager
	 */
	private MispRequest(Context context) {
		requestQueue = Volley.newRequestQueue(context);
		preferenceManager = PreferenceManager.Instance(context);
		loadSavedCredentials();
	}

	private void loadSavedCredentials() {
		serverUrl = preferenceManager.getMyServerUrl();
		apiKey = preferenceManager.getMyServerApiKey();
	}

	/**
	 * @param orgId    organisation ID on the MISP-Instance
	 * @param callback returns a single Organisation-JSON
	 */
	public void OrganisationInformation(int orgId, final OrganisationCallback callback) {

		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					callback.onResult(response.getJSONObject("Organisation"));
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
	public void myUserInformation(final UserCallback callback) {

		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				try {
					callback.onResult(response.getJSONObject("User"));
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
				Log.e(TAG, "onErrorResponse: " + error.toString());
				callback.onError(error);
			}
		};

		if (serverUrl.isEmpty() || apiKey.isEmpty()) {
			Log.e(TAG, "myUserInformation: server or api key is empty!");
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
					callback.onResult(response.getJSONObject("Organisation"));
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
				serverUrl + "/admin/organisations/add",
				organisation.toJSON(),
				listener,
				errorListener
		);

		requestQueue.add(r);
	}


	private JsonObjectRequest objectRequest(int method,
	                                        String url,
	                                        @Nullable JSONObject body,
	                                        Response.Listener<JSONObject> listener,
	                                        Response.ErrorListener errorListener) {

		return new JsonObjectRequest(method, url, body, listener, errorListener) {

			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();

				params.put("Authorization", apiKey);
				params.put("Accept", "application/json");
				params.put("Content-Type", "application/json; utf-8");

				return params;
			}

		};
	}


	public void setServerCredentials(String serverUrl, String apiKey) {
		this.serverUrl = serverUrl;
		this.apiKey = apiKey;
	}

	public static MispRequest Instance(Context context) {
		if (instance == null) {
			instance = new MispRequest(context);
		}

		return instance;
	}


	public interface IntegerCallback {
		void onResult(int result);

		void onError(VolleyError volleyError);
	}

	public interface OrganisationCallback {
		void onResult(JSONObject organisationInformation);

		void onError(VolleyError volleyError);
	}

	public interface UserCallback {
		void onResult(JSONObject myOrganisationInformation);

		void onError(VolleyError volleyError);
	}
}
