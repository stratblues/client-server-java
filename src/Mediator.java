import java.io.IOException;

public interface Mediator {
	void sendOriginalClientMessageToServer(Message message, Colleague colleague) throws IOException;

	void receiveProcessedMessageAndPassToClient(Message message, Colleague colleague);

	void listenForServerResponses(Client client);
}
