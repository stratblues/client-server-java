import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class MediatorClient implements Mediator {
	private ClientServerObjectStream clientServerStream;

	public MediatorClient(String host, int port) throws IOException {
		Socket clientSocket = new Socket(host, port);
		this.clientServerStream = new ClientServerObjectStream(clientSocket);
	}

	@Override
	public void sendOriginalClientMessageToServer(Message message, Colleague colleague) throws IOException {
		clientServerStream.sendMessageObjectOutputStream(message);
	}

	@Override
	public void receiveProcessedMessageAndPassToClient(Message message, Colleague colleague) {
		((Client) colleague).receiveProcessedMessageBackFromServer(message);
	}

	@Override
	public void listenForServerResponses(Client client) {
		try {
			while (!clientServerStream.objectStreamIsClosed()) {
				Message message = clientServerStream.receiveMessageObjectInputSteam();
				if (message.getIsFinished()) {
					receiveProcessedMessageAndPassToClient(new Message(true), client);
					break;
				}
				receiveProcessedMessageAndPassToClient(message, client);
			}
		} catch (EOFException ignored) {

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			clientServerStream.closeObjectStreams();
		}
	}
}
