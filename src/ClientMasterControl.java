import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientMasterControl implements Runnable {
	private Client client;
	private MediatorClient mediator;

	public ClientMasterControl(String host, int port, String[] messages) throws IOException {
		this.mediator = new MediatorClient(host, port);
		this.client = new Client(mediator, messages);
	}

	public static void main(String[] args) {
		// set host and port for socket connections
		String host = "localhost";
		int port = 5000;

		String[][] clientsMessages = { { "DESCENT OF MAN", "THE ASCENT OF MAN", "THE OLD MAN AND THE SEA" },
				{ "first little blob ", "second one to test", "i love se575!", "hemingway is also pretty good." }

		};

		List<Thread> threads = new ArrayList<>();

		for (String[] messages : clientsMessages) {
			try {
				ClientMasterControl clientControl = new ClientMasterControl(host, port, messages);
				Thread thread = new Thread(clientControl);
				thread.start();
				threads.add(thread);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// let threads finish and then join

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void run() {
		try {
			client.sendMessagesToServer();
			mediator.listenForServerResponses(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
