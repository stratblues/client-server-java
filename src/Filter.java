import java.io.IOException;

public abstract class Filter implements Runnable {

	Pipe inPipe;
	Pipe outPipe;
	private boolean running = false;

	public Filter(Pipe inPipe, Pipe outPipe) {
		this.inPipe = inPipe;
		this.outPipe = outPipe;
	}

	abstract void filter() throws IOException, ClassNotFoundException, InterruptedException;

	public void start() throws IOException, ClassNotFoundException, InterruptedException {
		new Thread(this).start();
	}

	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				filter();
			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
