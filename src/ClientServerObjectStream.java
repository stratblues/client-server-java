import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientServerObjectStream {
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	ClientServerObjectStream(Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		this.inputStream = new ObjectInputStream(socket.getInputStream());
	}

	public void sendMessageObjectOutputStream(Message message) throws IOException {
		outputStream.writeObject(message);
		outputStream.flush();
		if (message.getIsFinished()) {
			socket.shutdownOutput();
		}
	}

	public Message receiveMessageObjectInputSteam() throws IOException, ClassNotFoundException {
		return (Message) inputStream.readObject();
	}

	public void closeObjectStreams() {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean objectStreamIsClosed() {
		return socket.isClosed();
	}
}
