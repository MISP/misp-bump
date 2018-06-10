package de.overview.wg.its.mispauth.auxiliary;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

public class ReadableError {

	public static String toReadable(VolleyError volleyError) {

		if(volleyError instanceof NoConnectionError) {
			return "Connection failed";
		} else if(volleyError instanceof AuthFailureError) {
			return "Authentication failed";
		}

		return "Unknown error";
	}
}
