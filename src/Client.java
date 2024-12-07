import java.io.IOException;

public class Client extends Colleague {

	private String[] messages;

	public Client(Mediator mediator, String[] messages) {
		super(mediator);
		this.messages = messages;
	}

	public void sendMessagesToServer() throws IOException {
		for (String line : messages) {
			mediator.sendOriginalClientMessageToServer(new Message(line), this);
		}
		mediator.sendOriginalClientMessageToServer(new Message(true), this);
	}

	// clients get message back from mediator and sys out
	public void receiveProcessedMessageBackFromServer(Message message) {
		if (!message.getIsFinished()) {
			System.out.println(message.getString());
		}
	}
}
