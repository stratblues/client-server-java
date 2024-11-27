import java.util.Collection;

//using generics to abstract collection
// https://stackoverflow.com/questions/9893481/generics-inheriting-from-an-abstract-class-that-implements-an-interface
public abstract class UsedPipe<C extends Collection<String>> implements Pipe {

	C collection;
	private boolean closed;

	UsedPipe(C collection) {
		closed = false;
		this.collection = collection;
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public boolean isNotEmptyOrIsNotClosed() {
		return hasNext() || !closed;
	}

	@Override
	public abstract void write(String string);

	@Override
	public abstract String read();

	@Override
	public abstract boolean hasNext();
}
