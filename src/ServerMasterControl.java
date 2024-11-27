import java.io.IOException;

public class ServerMasterControl {
	Server server;
	CircularShifter circularShifter;
	Alphabetizer alphabetizer;

	Pipe circularShifterPipe;
	Pipe alphabetizerPipe;
	Pipe outputPipe;

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		ServerMasterControl main = new ServerMasterControl();
		main.circularShifterPipe = new PipeQueue();
		main.alphabetizerPipe = new PipeQueue();
		main.outputPipe = new PipeQueue();

		main.server = new Server(main.outputPipe, main.circularShifterPipe, 5000);

		main.circularShifter = new CircularShifter(main.circularShifterPipe, main.alphabetizerPipe);
		main.alphabetizer = new Alphabetizer(main.alphabetizerPipe, main.outputPipe);

		main.circularShifter.start();
		main.alphabetizer.start();

		main.server.start();

	}

}
