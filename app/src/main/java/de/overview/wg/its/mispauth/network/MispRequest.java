package de.overview.wg.its.mispauth.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.auxiliary.ReadableError;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.model.Server;
import de.overview.wg.its.mispauth.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
				Log.e(TAG, "onErrorResponse: " + error.toString());
				callback.onError(error);
			}
		};

		if (serverUrl.isEmpty() || apiKey.isEmpty()) {
			Log.e(TAG, "getMyUser: server or api key is empty!");
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
				Log.d(TAG, "onResponse: " + resultArray.toString());
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "onErrorResponse: " + ReadableError.toReadable(error));
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

				params.put("Authorization", apiKey);
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

//	private SSLSocketFactory getSocketFactory(Context context) {
//
//		CertificateFactory cf = null;
//		try {
//			cf = CertificateFactory.getInstance("X.509");
//			InputStream caInput = context.getResources().openRawResource(R.raw.server);
//			Certificate ca;
//			try {
//				ca = cf.generateCertificate(caInput);
//				Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
//			} finally {
//				caInput.close();
//			}
//
//
//			String keyStoreType = KeyStore.getDefaultType();
//			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//			keyStore.load(null, null);
//			keyStore.setCertificateEntry("ca", ca);
//
//
//			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//			tmf.init(keyStore);
//
//
//			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//				@Override
//				public boolean verify(String hostname, SSLSession session) {
//
//					Log.e("CipherUsed", session.getCipherSuite());
//					return hostname.compareTo("192.168.1.10")==0; //The Hostname of your server
//
//				}
//			};
//
//
//			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//			SSLContext context = null;
//			context = SSLContext.getInstance("TLS");
//
//			context.init(null, tmf.getTrustManagers(), null);
//			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
//
//			SSLSocketFactory sf = context.getSocketFactory();
//
//
//			return sf;
//
//		} catch (CertificateException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		}
//
//		return  null;
//	}

	public static MispRequest Instance(Context context) {
		if (instance == null) {
			instance = new MispRequest(context);
		}

		return instance;
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
