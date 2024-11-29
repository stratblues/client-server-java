import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Filter {
	private ServerSocket serverSocket;
	private ClientServerObjectStream clientServerStream;

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
				clientServerStream = new ClientServerObjectStream(clientSocket);

				// multiple client support
				new Thread(() -> processDataFromClient(clientServerStream)).start();
			}
		} catch (IOException e) {
			throw new RuntimeException("error: ", e);
		}
	}

	private void processDataFromClient(ClientServerObjectStream clientServerStream) {
		try {
			readObjectsFromClient(clientServerStream);
			sendProcessedObjectsBackToClient(clientServerStream);
		} finally {
			clientServerStream.closeObjectStreams();
		}
	}

	private void readObjectsFromClient(ClientServerObjectStream clientServerStream) {
		try {
			Message messsage;
			while ((messsage = clientServerStream.receiveMessageObjectInputSteam()) != null) {
				if (messsage.getIsFinished()) {
					break;
				} else {
					String line = messsage.getString();
					outPipe.write(line);
				}
			}
			outPipe.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Error reading from client: ", e);
		}
	}

	private void sendProcessedObjectsBackToClient(ClientServerObjectStream clientServerStream) {
		try {
			while (inPipe.isNotEmptyOrIsNotClosed()) {
				while (inPipe.hasNext()) {
					String processedLine = inPipe.read();
					Message responseMessage = new Message(processedLine);
					clientServerStream.sendMessageObjectOutputStream(responseMessage);
				}
				Thread.sleep(100);
			}
			clientServerStream.sendMessageObjectOutputStream(new Message(true));
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Error sending to client: ", e);
		}
	}

}