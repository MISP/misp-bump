package de.overview.wg.its.mispauth.auxiliary;

import com.android.volley.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ReadableError {

	public static String toReadable(VolleyError volleyError) {

		if (volleyError.networkResponse != null) {
			try {
				JSONObject response = new JSONObject(new String(volleyError.networkResponse.data, StandardCharsets.UTF_8));
				JSONObject error = response.getJSONObject("errors");

				String name = response.getString("name");
				String errorName = error.getJSONArray("name").get(0).toString();

				if (!errorName.equals("")) {
					return errorName;
				} else if (!name.equals("")) {
					return name;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (volleyError instanceof NoConnectionError) {
			return "Connection failed";
		} else if (volleyError instanceof TimeoutError) {
			return "Connection timed out";
		} else if (volleyError instanceof NetworkError) {
			return "Network error";
		} else if (volleyError instanceof AuthFailureError) {
			return "Authentication failed";
		} else if (volleyError instanceof ServerError) {
			return "Server error";
		} else if (volleyError instanceof ParseError) {
			return "Parsing error";
		}

		return volleyError.toString();
	}

}
