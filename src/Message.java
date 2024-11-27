import java.io.Serializable;

public class Message implements Serializable {
	private String message;
	private Boolean isFinished;

	public Message(String string) {
		this.message = string;
		this.isFinished = false;
	}

	public Message(Boolean bool) {
		this.isFinished = bool;
		this.message = null;
	}

	public String getString() {
		if (this.message == null || this.isFinished) {
			throw new IllegalStateException();
		} else {
			return message;
		}
	}

	public Boolean getIsFinished() {
		return isFinished;
	}
}
