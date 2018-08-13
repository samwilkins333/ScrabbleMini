package Scrabble;

import java.util.ArrayList;

public class TileBag {
	private ArrayList<Tile> _bag;
	private ScrabbleGame _scrabbleGame;
	private boolean _empty;

	public TileBag(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_bag = new ArrayList<Tile>();
		_empty = false;
		this.fillBag();
	}

	public void fillBag() {
		// for (int blank = 0; blank < 2; blank++) {
		// 	Tile tile = new Tile(0);
		// 	_bag.add(tile);
		// }
		for (int a = 0; a < 9; a++) {
			Tile tile = new Tile(1);
			_bag.add(tile);
		}
		for (int b = 0; b < 2; b++) {
			Tile tile = new Tile(2);
			_bag.add(tile);
		}
		for (int c = 0; c < 2; c++) {
			Tile tile = new Tile(3);
			_bag.add(tile);
		}
		for (int d = 0; d < 4; d++) {
			Tile tile = new Tile(4);
			_bag.add(tile);
		}
		for (int e = 0; e < 12; e++) {
			Tile tile = new Tile(5);
			_bag.add(tile);
		}
		for (int f = 0; f < 2; f++) {
			Tile tile = new Tile(6);
			_bag.add(tile);
		}
		for (int g = 0; g < 3; g++) {
			Tile tile = new Tile(7);
			_bag.add(tile);
		}
		for (int h = 0; h < 2; h++) {
			Tile tile = new Tile(8);
			_bag.add(tile);
		}
		for (int i = 0; i < 9; i++) {
			Tile tile = new Tile(9);
			_bag.add(tile);
		}
		for (int j = 0; j < 1; j++) {
			Tile tile = new Tile(10);
			_bag.add(tile);
		}
		for (int k = 0; k < 1; k++) {
			Tile tile = new Tile(11);
			_bag.add(tile);
		}
		for (int l = 0; l < 4; l++) {
			Tile tile = new Tile(12);
			_bag.add(tile);
		}
		for (int m = 0; m < 2; m++) {
			Tile tile = new Tile(13);
			_bag.add(tile);
		}
		for (int n = 0; n < 6; n++) {
			Tile tile = new Tile(14);
			_bag.add(tile);
		}
		for (int o = 0; o < 8; o++) {
			Tile tile = new Tile(15);
			_bag.add(tile);
		}
		for (int p = 0; p < 2; p++) {
			Tile tile = new Tile(16);
			_bag.add(tile);
		}
		for (int q = 0; q < 1; q++) {
			Tile tile = new Tile(17);
			_bag.add(tile);
		}
		for (int r = 0; r < 6; r++) {
			Tile tile = new Tile(18);
			_bag.add(tile);
		}
		for (int s = 0; s < 4; s++) {
			Tile tile = new Tile(19);
			_bag.add(tile);
		}
		for (int t = 0; t < 6; t++) {
			Tile tile = new Tile(20);
			_bag.add(tile);
		}
		for (int u = 0; u < 4; u++) {
			Tile tile = new Tile(21);
			_bag.add(tile);
		}
		for (int v = 0; v < 2; v++) {
			Tile tile = new Tile(22);
			_bag.add(tile);
		}
		for (int w = 0; w < 2; w++) {
			Tile tile = new Tile(23);
			_bag.add(tile);
		}
		for (int x = 0; x < 1; x++) {
			Tile tile = new Tile(24);
			_bag.add(tile);
		}
		for (int y = 0; y < 2; y++) {
			Tile tile = new Tile(25);
			_bag.add(tile);
		}
		for (int z = 0; z < 1; z++) {
			Tile tile = new Tile(26);
			_bag.add(tile);
		}
		this.printSize();
	}

	public Tile draw() {
		if (_empty == false) {
			Tile selection = null;
			if (_bag.size() > 0) {
				int drawInt = (int) (Math.random() * _bag.size());
				selection = _bag.get(drawInt);
				_bag.remove(drawInt);	
			} else {
				_empty = true;
				_scrabbleGame.getOrganizer().removeBag();
				System.out.println("IN TILE BAG, BAG EMPTY");
				return null;
			}
			return selection;
		} else {
			return null;
		}
	}

	public int size() {
		return _bag.size();
	}

	public Boolean isEmpty() {
		Boolean status = false;
		if (_bag.size() == 0) {
			status = true;
		}
		return status;
	}

	public void printSize() {
		System.out.printf("Tile Bag has %s tiles\n\n", _bag.size());
	}

}