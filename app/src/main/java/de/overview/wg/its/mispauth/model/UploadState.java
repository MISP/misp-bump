package de.overview.wg.its.mispauth.model;

public class UploadState {

	public enum State {
		PENDING,
		IN_PROGRESS,
		DONE,
		ERROR
	}
	private State currentState = State.PENDING;
	private String title, error;


	public UploadState(String title) {
		this.title = title;
	}


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

}
