import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMasterControl implements Mediator {
	private Client client;
	private Socket clientSocket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	public ClientMasterControl(String[] messages) throws IOException {
		this.client = new Client(this, messages);
		this.clientSocket = new Socket("localhost", 5000);
		this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
	}

	public static void main(String[] args) {
		try {
			String[] clientMessages = { "Descent of Man", "The Ascent of Man", "The Old Man and The Sea" };
			ClientMasterControl mediator = new ClientMasterControl(clientMessages);
			mediator.client.sendMessagesToServer();
			mediator.listenForServerResponses();

		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	// send message to server
	@Override
	public void sendMessage(Message message, Colleague colleague) {
		try {
			outputStream.writeObject(message);
			outputStream.flush();
			if (message.getIsFinished()) {
				clientSocket.shutdownOutput();
			}
		} catch (IOException error) {
			throw new RuntimeException(error);
		}
	}

	// get message back from server
	@Override
	public void receiveMessage(Message message, Colleague colleague) {
		if (colleague instanceof Client) {
			((Client) colleague).receive(message);
		}
	}

	private void listenForServerResponses() throws IOException {
		Object object;
		try {
			while (true) {
				object = inputStream.readObject();
				if (object instanceof Message) {
					Message message = (Message) object;
					receiveMessage(message, client);
				}
				// end of stream
				receiveMessage(new Message(true), client);
			}
		} catch (EOFException ignored) {

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			inputStream.close();
			outputStream.close();
			clientSocket.close();
		}

	}

}
