public interface Pipe {
	void write(String string);

	String read();

	boolean isNotEmptyOrIsNotClosed();

	boolean hasNext();

	void close();
}
