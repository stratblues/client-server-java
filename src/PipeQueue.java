import java.util.concurrent.ConcurrentLinkedQueue;

public class PipeQueue extends UsedPipe<ConcurrentLinkedQueue<String>> {

	public PipeQueue() {
		super(new ConcurrentLinkedQueue<>());
	}

	@Override
	public void write(String string) {
		collection.offer(string);
	}

	@Override
	public String read() {
		return collection.poll();
	}

	@Override
	public boolean hasNext() {
		return !collection.isEmpty();
	}

}
