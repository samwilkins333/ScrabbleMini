package Scrabble;

public class DummyTile {
	private String _letter;
	private int _x;
	private int _y;
	private int _value;

	public DummyTile(String letter, int x, int y) {
		_letter = letter;
		_x = x;
		_y = y;
		_value = Constants.VALUES.get(_letter);
	}

	public int getXIndex() {
		return _x;
	}

	public int getYIndex() {
		return _y;
	}

	public String getLetter() {
		return _letter;
	}

	public int getValue() {
		return _value;
	}

}
