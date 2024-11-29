import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientMasterControl implements Mediator {
	private Client client;
	private ClientServerObjectStream clientServerStream;

	public ClientMasterControl(String[] messages) throws IOException {
		Socket clientSocket = new Socket("localhost", 5000);
		this.client = new Client(this, messages);
		this.clientServerStream = new ClientServerObjectStream(clientSocket);

	}

	public static void main(String[] args) {
		try {
			String[] clientOneMessages = { "DESCENT OF MAN", "THE ASCENT OF MAN", "THE OLD MAN AND THE SEA" };
			String[] clientTwoMessages = { "first little blob ", "second one to test", "i love se575!",
					"hemingway is also pretty good." };

			ClientMasterControl mediator1 = new ClientMasterControl(clientOneMessages);
			ClientMasterControl mediator2 = new ClientMasterControl(clientTwoMessages);

			// https://stackoverflow.com/questions/31416784/thread-with-lambda-expression
			Thread clientOneThread = new Thread(() -> {
				try {
					mediator1.client.sendMessagesToServer();
					mediator1.listenForServerResponses();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			Thread clientTwoThread = new Thread(() -> {
				try {
					mediator2.client.sendMessagesToServer();
					mediator2.listenForServerResponses();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			clientOneThread.start();
			clientTwoThread.start();
			clientOneThread.join();
			clientTwoThread.join();

		} catch (IOException | InterruptedException error) {
			error.printStackTrace();
		}
	}

	// send message to server
	@Override
	public void sendMessage(Message message, Colleague colleague) {
		try {
			clientServerStream.sendMessageObjectOutputStream(message);
		} catch (IOException error) {
			throw new RuntimeException(error);
		}
	}

	// get message back from server
	@Override
	public void receiveMessage(Message message, Colleague colleague) {
		((Client) colleague).receive(message);
	}

	private void listenForServerResponses() {
		try {
			while (!clientServerStream.objectStreamIsClosed()) {
				Message object = clientServerStream.receiveMessageObjectInputSteam();
				if (object.getIsFinished()) {
					receiveMessage(new Message(true), client);
					break;
				}
				receiveMessage(object, client);
			}
		} catch (EOFException ignored) {

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			clientServerStream.closeObjectStreams();
		}
	}

}
