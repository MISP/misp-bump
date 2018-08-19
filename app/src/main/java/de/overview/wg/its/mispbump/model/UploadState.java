package de.overview.wg.its.mispbump.model;

public class UploadState {

	public enum State {
		PENDING,
		IN_PROGRESS,
		DONE,
		ERROR,
        FOLLOW_ERROR
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

	public String getErrorMessage() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
		this.currentState = State.ERROR;
	}

	public void setDone() {
	    this.currentState = State.DONE;
    }

    public void setInProgress() {
	    this.currentState = State.IN_PROGRESS;
    }

    public void setPending() {
	    this.currentState = State.PENDING;
    }

    public void setFollowError () {
        this.currentState = State.FOLLOW_ERROR;
    }

	public State getCurrentState() {
		return currentState;
	}

}
