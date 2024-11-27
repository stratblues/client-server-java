import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Filter {
	private ServerSocket serverSocket;
	private ClientServerConfiguration clientServerConfig;

	public Server(Pipe inPipe, Pipe outPipe, int port) throws IOException {
		super(inPipe, outPipe);
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	void filter() {
		this.startServer();
	}

	public void startServer() {
		System.out.println("Server started on port " + serverSocket.getLocalPort());
		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connection established with: " + clientSocket.getRemoteSocketAddress());
				clientServerConfig = new ClientServerConfiguration(clientSocket);

				// multiple client support
				new Thread(() -> processDataFromClient(clientServerConfig)).start();
			}
		} catch (IOException e) {
			throw new RuntimeException("error: ", e);
		}
	}

	private void processDataFromClient(ClientServerConfiguration clientServerConfig) {
		try {
			readObjectsFromClient(clientServerConfig);
			sendProcessedObjectsBackToClient(clientServerConfig);
		} finally {
			clientServerConfig.closeObjectStreams();
		}
	}

	private void readObjectsFromClient(ClientServerConfiguration clientServerConfig) {
		try {
			Message obj;
			while ((obj = clientServerConfig.receiveMessageObjectInputSteam()) != null) {
				if (obj.getIsFinished()) {
					break;
				} else {
					String line = obj.getString();
					outPipe.write(line);
				}
			}
			outPipe.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Error reading from client: ", e);
		}
	}

	private void sendProcessedObjectsBackToClient(ClientServerConfiguration clientServerConfig) {
		try {
			while (inPipe.isNotEmptyOrIsNotClosed()) {
				while (inPipe.hasNext()) {
					String processedLine = inPipe.read();
					Message responseMessage = new Message(processedLine);
					clientServerConfig.sendMessageObjectOutputStream(responseMessage);
				}
				Thread.sleep(100);
			}
			clientServerConfig.sendMessageObjectOutputStream(new Message(true));
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Error sending to client: ", e);
		}
	}

}