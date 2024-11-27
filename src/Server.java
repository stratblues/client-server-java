import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Filter {
	private ServerSocket serverSocket;
	private Socket currentClientSocket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	public Server(Pipe inPipe, Pipe outPipe, int port) throws IOException {
		super(inPipe, outPipe);
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	void filter() throws IOException, ClassNotFoundException, InterruptedException {
		this.startServer();
	}

	private void readObjectFromClient() throws IOException, ClassNotFoundException {
		try {
			Object obj;
			while (true) {
				obj = inputStream.readObject();
				if (obj instanceof Message) {
					Message message = (Message) obj;
					if (message.getIsFinished()) {
						break;
					} else {
						String line = message.getString();
						outPipe.write(line);
					}
				}
			}
			outPipe.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendProcessedObjectBackToClient() throws IOException, InterruptedException {
		try {
			while (inPipe.isNotEmptyOrIsNotClosed()) {
				while (inPipe.hasNext()) {
					String processedLine = inPipe.read();
					Message responseMessage = new Message(processedLine);
					outputStream.writeObject(responseMessage);
				}
				Thread.sleep(100);
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void connectServerToClientSocket() {
		try {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connection established with: " + clientSocket.getRemoteSocketAddress());
			this.currentClientSocket = clientSocket;
			this.outputStream = new ObjectOutputStream(currentClientSocket.getOutputStream());
			this.inputStream = new ObjectInputStream(currentClientSocket.getInputStream());
		} catch (IOException error) {
			System.out.println("Error connecting: " + error.getMessage());
			error.printStackTrace();
		}
	}

	private void closePipesAndDataStreams() {
		try {
			inPipe.close();
			outputStream.close();
			inputStream.close();
			currentClientSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void startServer() throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println("Server started on port " + serverSocket.getLocalPort());
		while (true) {
			connectServerToClientSocket();
			readObjectFromClient();
			sendProcessedObjectBackToClient();
			closePipesAndDataStreams();

		}
	}

}