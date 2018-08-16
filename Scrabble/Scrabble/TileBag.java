package Scrabble;

import java.util.ArrayList;

class TileBag {
	private ArrayList<Tile> _bag;
	private ScrabbleGame _scrabbleGame;

	TileBag(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_bag = new ArrayList<>();
		this.fillBag();
	}

	private void fillBag() {
		int[] count = new int[] {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2};
		for (int i = 0; i < 26; i++) Add(i + 1, count[i]);
	}

	private void Add(int letter, int count) {
		for (int i = 0; i < count; i++) _bag.add(new Tile(letter));
	}

	Tile draw() {
		if (isEmpty()) {
			_scrabbleGame.getOrganizer().removeBag();
			return null;
		}

		int drawInt = (int) (Math.random() * _bag.size());
		_bag.remove(drawInt);

		return _bag.get(drawInt);
	}

	boolean isEmpty() {
		return _bag.isEmpty();
	}

}