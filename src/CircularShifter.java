public class CircularShifter extends Filter {

	private String[] wordsInLine;
	private String shiftedLine;

	CircularShifter(Pipe inPipe, Pipe outPipe) {
		super(inPipe, outPipe);
	}

	private String[] getWordsInLine(String line) {
		return line.split(" ");
	}

	private String rotateWordsInLine(String[] words) {
		int howManyWords = words.length;
		String tempFirstWord = words[0];
		for (int i = 0; i < howManyWords - 1; i++) {
			words[i] = words[i + 1];
		}
		words[howManyWords - 1] = tempFirstWord;
		return concatenateWordsIntoArray(words);
	}

	private String concatenateWordsIntoArray(String[] words) {
		String concatenatedString = "";
		int howManyWordsToConcatenate = words.length;
		for (int i = 0; i < howManyWordsToConcatenate; i++) {
			concatenatedString = concatenatedString.trim() + " " + words[i];
		}
		return concatenatedString;
	}

	@Override
	void filter() {
		while (inPipe.isNotEmptyOrIsNotClosed()) {
			if (inPipe.hasNext()) {
				wordsInLine = getWordsInLine(inPipe.read());
				for (int i = 0; i < wordsInLine.length; ++i) {
					shiftedLine = rotateWordsInLine(wordsInLine);
					outPipe.write(shiftedLine);
				}
			}
		}
		this.stop();
		inPipe.close();
		outPipe.close();
	}
}