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
		for (int i = 0; i < 26; i++) Add(i + 1, Constants.TILE_INFO.get(i).getFrequency());
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
		Tile selection = _bag.get(drawInt);
		_bag.remove(drawInt);
		
		return selection;
	}

	boolean isEmpty() {
		return _bag.isEmpty();
	}

}