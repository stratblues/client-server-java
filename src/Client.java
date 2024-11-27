import java.io.IOException;

public class Client extends Colleague {

	String[] messages;

	public Client(Mediator mediator, String[] messages) {
		super(mediator);
		this.messages = messages;
	}

	public void sendMessagesToServer() throws IOException {
		for (String line : messages) {
			mediator.sendMessage(new Message(line), this);
		}
		mediator.sendMessage(new Message(true), this);
	}

	// get message back from mediator and sys out
	public void receive(Message message) {
		if (!message.getIsFinished()) {
			System.out.println(message.getString());
		}
	}
}
