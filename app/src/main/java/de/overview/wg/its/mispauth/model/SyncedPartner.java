package de.overview.wg.its.mispauth.model;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class SyncedPartner {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private String name;
	private String url;
	private String syncDate;

	public SyncedPartner(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public void generateTimeStamp() {
		syncDate = dateFormat.format(new Timestamp(System.currentTimeMillis()));
	}

	// GETTER & SETTER

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getSyncDate() {
		return syncDate;
	}
	public void setSyncDate(String syncDate) {
		this.syncDate = syncDate;
	}
}
