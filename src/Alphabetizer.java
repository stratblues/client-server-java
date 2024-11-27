import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Alphabetizer extends Filter {

	TreeSet<String> treeSet;

	public Alphabetizer(Pipe inPipe, Pipe outPipe) {
		super(inPipe, outPipe);
		this.treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}

	public List<String> getAlphabetizedLines() {
		return new ArrayList<>(treeSet);
	}

	@Override
	void filter() {
		if (!inPipe.isNotEmptyOrIsNotClosed()) {
			for (String line : getAlphabetizedLines()) {
				outPipe.write(line);
			}
			this.stop();
			outPipe.close();
			inPipe.close();
		}
		while (inPipe.isNotEmptyOrIsNotClosed()) {
			if (inPipe.hasNext()) {
				treeSet.add(inPipe.read());
			}
		}
	}
}
