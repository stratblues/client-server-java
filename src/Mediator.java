import java.io.IOException;

public interface Mediator {
	void sendMessage(Message message, Colleague colleague) throws IOException;

	void receiveMessage(Message message, Colleague colleague);
}
