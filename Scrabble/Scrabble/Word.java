package Scrabble;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

class Word {
	private String _letters;
	private int _value;
	private int _originalValue;
	private ArrayList<Tile> _tiles;
	private ArrayList<Tile> _kernelTiles;
	private ArrayList<Tile> _adjacentTiles;
	private ArrayList<Word> _horizontalCrosses;
	private ArrayList<Word> _verticalCrosses;
	private ArrayList<Word> _allCrosses;
	private ScrabbleGame _scrabbleGame;
	private PlayerNum _playerNumber;
	private WordOrientation _orientation;
	private int _firstLetterX;
	private int _firstLetterY;
	private String _newBoardLetters;

	Word(String letters, String newBoardLetters, int value, int originalValue, WordOrientation orientation, int firstLetterX, int firstLetterY) {
		_letters = letters;
		_newBoardLetters = newBoardLetters;

		_value = value;
		_originalValue = originalValue;

		_orientation = orientation;

		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;

		_tiles = new ArrayList<>();
		_kernelTiles = new ArrayList<>();
		_adjacentTiles = new ArrayList<>();

	}

	Word(String letters, int value, int firstLetterX, int firstLetterY, WordOrientation orientation) {
		_letters = letters;

		_value = value;
		_originalValue = value;

		_orientation = orientation;

		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;

		_tiles = new ArrayList<>();
	}

	Word(ScrabbleGame scrabbleGame, PlayerNum playerNumber) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
		_tiles = new ArrayList<>();
	}
	
	void addScrabbleGame(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
	}

	String getNewBoardLetters() {
		return _newBoardLetters;
	}
	
	int getOriginalValue() {
		return _originalValue;
	}

	private Word(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_tiles = new ArrayList<>();
	}

	void addTiles(ArrayList<Tile> tiles) {
		_tiles.addAll(tiles);
	}
	
	void addKernelTiles(ArrayList<Tile> kernelTiles) {
		_kernelTiles.addAll(kernelTiles);
	}

	Boolean containsATileAt(Tile tile, int x, int y) {
		for (Tile thisTile : _tiles)
			if (thisTile != tile && thisTile.getXIndex() == x && thisTile.getYIndex() == y) return true;
		return false;
	}

	Boolean containsOnlyAddedTiles() {
		for (Tile thisTile : _tiles) if (!thisTile.hasBeenPlayed()) return false;
		return true;
	}

	private Boolean isConnected() {
		if (!_scrabbleGame.getReferee().isFirstMoveMade()) return true;

		Tile[][] tileArray = _scrabbleGame.getTileArray();
		for (Tile thisTile : _tiles) {
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();

			if (thisTile.hasBeenPlayed()) return true;

			if (this.isAlignedHorizontally()) {
				if ((y + 1 <= 14 && tileArray[x][y + 1] != null) || (y - 1 >= 0 && tileArray[x][y - 1] != null))
					return true;
			} else if (this.isAlignedVertically())
				if ((x + 1 <= 14 && tileArray[x + 1][y] != null) || (x - 1 >= 0 && tileArray[x - 1][y] != null))
					return true;
		}
		return false;
	}

	void checkAddedTiles() {
		ArrayList<Tile> temp = new ArrayList<>();
		ArrayList<Tile> tempVertical = new ArrayList<>();
		ArrayList<Tile> tempHorizontal = new ArrayList<>();
		this.compileWordCharacteristics();
		
		int size = _tiles.size();
		for (int i = 0; i < size; i++) {
			Tile thisTile = _tiles.get(0);
			if (!thisTile.hasBeenPlayed()) temp.add(thisTile);
			_tiles.remove(thisTile);
		}
		_tiles.addAll(temp);
		
		//Check for alignment
		if (this.isAligned()) {
			if (this.isAlignedVertically()) {
				
				//For each tile in _tiles, collect new vertically adjacent tiles for the main word ArrayList, _tiles
				for (Tile thisTile : _tiles) {
					tempVertical.add(thisTile);
					_scrabbleGame.collectAdjacents(CollectionOrientation.Vertical, thisTile, tempVertical);
					tempVertical.remove(thisTile);
				}
				_tiles.addAll(tempVertical); //Add
				this.readTiles(WordOrientation.Vertical); //Update string based on vertical placement of tiles
			}
			
			if (this.isAlignedHorizontally()) {
				
				//For each tile in _tiles, collect new horizontally adjacent tiles for the main word ArrayList, _tiles
				for (Tile thisTile : _tiles) {
					tempHorizontal.add(thisTile);
					_scrabbleGame.collectAdjacents(CollectionOrientation.Horizontal, thisTile, tempHorizontal);
					tempHorizontal.remove(thisTile);
				}
				_tiles.addAll(tempHorizontal); //Add
				this.readTiles(WordOrientation.Horizontal); //Update string based on horizontal placement of tiles
			}
		}

		this.compileWordCharacteristics();
		this.addCrosses();
	}
	
	private void addCrosses() {
		_horizontalCrosses = new ArrayList<>();
		_verticalCrosses = new ArrayList<>();
		//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
		if (this.isAlignedVertically()) for (Tile thisTile : _tiles)
			if (!thisTile.hasBeenPlayed()) { //If the tile is involved in the current turn
				Word cross = new Word(_scrabbleGame);
				_scrabbleGame.collectAdjacents(CollectionOrientation.Horizontal, thisTile, cross.getTiles());
				if (cross.getTiles().size() > 1) {
					cross.compileWordCharacteristics();
					cross.updateValue(0);
					_horizontalCrosses.add(cross);
				}

			}
		//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
		if (this.isAlignedHorizontally()) for (Tile thisTile : _tiles)
			if (!thisTile.hasBeenPlayed()) { //If the tile is involved in the current turn
				Word cross = new Word(_scrabbleGame);
				_scrabbleGame.collectAdjacents(CollectionOrientation.Vertical, thisTile, cross.getTiles());
				if (cross.getTiles().size() > 1) {
					cross.compileWordCharacteristics();
					cross.updateValue(0);
					_verticalCrosses.add(cross);
				}
			}
		_allCrosses = new ArrayList<>();
		_allCrosses.addAll(_horizontalCrosses);
		_allCrosses.addAll(_verticalCrosses);

		if (_scrabbleGame.getReferee().getCurrentPlayerInstance().isHuman()) this.checkValidCrosses();
	}
	
	ArrayList<Word> getAllCrosses() {
		return _allCrosses;
	}

	void playFlashes(WordAddition outcome) {
		for (Tile _tile : _tiles) _tile.playFlash(outcome);

		if (_kernelTiles != null && _kernelTiles.size() > 0)
			for (Tile _kernelTile : _kernelTiles) _kernelTile.playFlash(outcome);

		if (_adjacentTiles != null && _adjacentTiles.size() > 0)
			for (Tile _adjacentTile : _adjacentTiles) _adjacentTile.playFlash(outcome);
	}

	void addTileToWord(Tile tile) {
		_tiles.add(tile);
	}

	private void readTiles(WordOrientation orientation) {
		ArrayList<Tile> temp = new ArrayList<>();
		int size = _tiles.size();
		for (int i = 0; i < size; i++) {
			Tile thisTile = _tiles.get(0);
			temp.add(thisTile);
			_tiles.remove(thisTile);
		}
		size = temp.size();
		if (orientation == WordOrientation.Vertical) for (int i = 0; i < size; i++) {
			Tile firstTile = null;

			for (int j = 0; j < temp.size(); j++) {
				Tile thisTile = temp.get(j);
				if (j == 0 || thisTile.getYIndex() < firstTile.getYIndex())
					firstTile = thisTile;
			}

			_tiles.add(firstTile);
			temp.remove(firstTile);
		}
		else for (int i = 0; i < size; i++) {
			Tile firstTile = null;

			for (int j = 0; j < temp.size(); j++) {
				Tile thisTile = temp.get(j);
				if (j == 0 || thisTile.getYIndex() < firstTile.getYIndex())
					firstTile = thisTile;
			}

			_tiles.add(firstTile);
			temp.remove(firstTile);
		}
		_letters = _scrabbleGame.tilesToString(_tiles);
	}

	void addToBoard() {
		if (this.isPlayable()) {
			int numNewTiles = 0;
			for (Tile thisTile : _tiles) {
				int x = thisTile.getXIndex();
				int y = thisTile.getYIndex();

				_scrabbleGame.addTileToBoardArrayAt(thisTile, x, y);

				ArrayList<Tile> rack = _scrabbleGame.getPlayerRack(_playerNumber);
				rack.remove(thisTile);

				if (!thisTile.hasBeenPlayed()) numNewTiles++;
				thisTile.declareHasBeenPlayed();
			}

			int bonus = 0;
			if (numNewTiles == 7) bonus = 50;
			this.updateValue(bonus);

			_scrabbleGame.getReferee().addToScore(_value);
		}
	}

	private void checkValidCrosses() {
		this.isPlayable();
	}
	
	private void compileWordCharacteristics() {
		this.isFormatted();
		this.isInDictionary();
		this.isValidFirstMove();
	}

//BOOLEAN HIERARCHY

	Boolean isPlayable() {
		if (_tiles.size() > 0)
			return this.isFormatted() && this.isInDictionary() && this.isValidFirstMove() && this.hasValidCrosses();
		return false;
	}
	
	private boolean hasValidCrosses() {
		return this.hasValidHorizontalCrosses() && this.hasValidVerticalCrosses();
	}

	private boolean hasValidHorizontalCrosses() {
		if (_horizontalCrosses.size() == 0) return true;

		for (Word thisCross : _horizontalCrosses)
			if (!thisCross.isInDictionary()) return false;

		return true;
	}
	
	private boolean hasValidVerticalCrosses() {
		if (_verticalCrosses.size() == 0) return true;

		for (Word thisCross : _verticalCrosses)
			if (!thisCross.isInDictionary()) return false;
		return true;
	}

	boolean isInDictionary() {
		return _scrabbleGame.tilesToDictBool(_tiles);
	}

	boolean isFormatted() {
		return this.isAligned() && this.isCompact() && this.isConnected();
	}

	private boolean isAligned() {
		if (_tiles.size() <= 1) return true;
		return this.isAlignedVertically() || this.isAlignedHorizontally();
	}

	private boolean isAlignedVertically() {
		if (_tiles.size() < 1) return false;
		if (_tiles.size() == 1) return true;

		if (!_scrabbleGame.getReferee().getCurrentPlayerInstance().isHuman())
			return _orientation == WordOrientation.Vertical;

		Tile firstTile = _tiles.get(0);
		for (int i = 1; i < _tiles.size(); i++)
			if (_tiles.get(i).getXIndex() != firstTile.getXIndex()) return false;

		return true;
	}

	private boolean isAlignedHorizontally() {
		if (_tiles.size() < 1) return false;
		if (_tiles.size() == 1) return true;

		if (!_scrabbleGame.getReferee().getCurrentPlayerInstance().isHuman())
			return _orientation == WordOrientation.Horizontal;

		Tile firstTile = _tiles.get(0);
		for (int i = 1; i < _tiles.size(); i++)
			if (_tiles.get(i).getYIndex() != firstTile.getYIndex()) return false;

		return true;
	}

	private boolean isCompact() {
		if (_tiles.size() <= 1) return true;
		return this.isCompactVertically() || this.isCompactHorizontally();
	}

	private boolean isCompactVertically() {
		this.readTiles(WordOrientation.Vertical);

		Tile firstTile = _tiles.get(0);
		int firstY = firstTile.getYIndex();

		for (int i = 1; i < _tiles.size(); i++) {
			int thisY = _tiles.get(i).getYIndex();
			if (thisY - i != firstY) return false;
		}
		return true;
	}

	private boolean isCompactHorizontally() {
		this.readTiles(WordOrientation.Horizontal);

		Tile firstTile = _tiles.get(0);
		int firstX = firstTile.getXIndex();

		for (int i = 1; i < _tiles.size(); i++) {
			int thisX = _tiles.get(i).getXIndex();
			if (thisX - i != firstX) return false;
		}
		return true;
	}

	private boolean isValidFirstMove() {
		if (_scrabbleGame.getReferee().isFirstMoveMade()) return true;

		for (Tile thisTile : _tiles)
			if (thisTile.getXIndex() == 7 && thisTile.getYIndex() == 7) return true;

		return false;
	}

	void clear() {
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		for (Tile thisTile : _tiles) {
			if (thisTile.hasBeenPlayed()) continue;

			thisTile.declareNotPlaced();
			thisTile.declareNotInNewestWord();
			thisTile.stopOverlapFlash();
			thisTile.setToOpaque();
			thisTile.resetShadow();
			tilesOnBoard.remove(thisTile);
		}

		if (_tiles != null) _tiles.clear();
		if (_horizontalCrosses != null) _horizontalCrosses.clear();
		if (_verticalCrosses != null) _verticalCrosses.clear();
		if (_allCrosses != null) _allCrosses.clear();
	}

	void removeTileFromWord(Tile tile) {
		_tiles.remove(tile);
	}

	private void updateValue(int bonus) {
		if (!this.isFormatted()) return;
		_value = _scrabbleGame.getWordValue(_tiles) + bonus;
	}
	
	private ArrayList<Tile> getTiles() {
		return _tiles;
	}

	String getLetters() {
		return _letters;
	}

	int getValue() {
		return _value;
	}

	void arrangeTilesOnBoard() {
		double delay = 0;
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		for (Tile thisTile : _tiles) if (!tilesOnBoard.contains(thisTile)) tilesOnBoard.add(thisTile);

		int x;
		int y;
		int tileInt = -1;

		for (int i = 0; i < _letters.length(); i++) {
			boolean isHorizontal = _orientation == WordOrientation.Horizontal;

			x = isHorizontal ? _firstLetterX + i : _firstLetterX;
			y = isHorizontal ? _firstLetterY : _firstLetterY + i;

			if (!_scrabbleGame.boardSquareOccupiedAt(x, y)) {
				tileInt++;
				Tile thisTile = _tiles.get(tileInt);
				delay += Constants.PLACEMENT_DURATION;

				PauseTransition delayPlacement = new PauseTransition(Duration.seconds(delay));
				delayPlacement.setOnFinished(new PlaceHandler(x, y, thisTile));
				delayPlacement.play();
			} else {
				Tile thisTile = _scrabbleGame.getTileFromArrayAt(x, y);
				if (!_kernelTiles.contains(thisTile)) _adjacentTiles.add(thisTile);
			}
		}

		PauseTransition delayCrosses = new PauseTransition(Duration.seconds(delay + Constants.PLACEMENT_DURATION/2));
		delayCrosses.setOnFinished(new CrossHandler());
		delayCrosses.play();

		PauseTransition delayAddition = new PauseTransition(Duration.seconds(delay + Constants.PLACEMENT_DURATION));
		delayAddition.setOnFinished(new AddHandler());
		delayAddition.play();
	}
	
	private class PlaceHandler implements EventHandler<ActionEvent> {
		private int _x;
		private int _y;
		private Tile _thisTile;
		
		PlaceHandler(int x, int y, Tile thisTile) {
			_x = x;
			_y = y;
			_thisTile = thisTile;
		}
		
		@Override
		public void handle(ActionEvent event) {
			_thisTile.placeAtSquare(_x, _y);
			event.consume();
		}

	}
	
	private class CrossHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			Word.this.addCrosses();
			event.consume();
		}

	}
	
	private class AddHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			_scrabbleGame.getOrganizer().addWordAI(Word.this);
			event.consume();
		}

	}
	
	void addToBoardLogicallyAI() {
		for (Tile thisTile : _tiles) {
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();
			_scrabbleGame.addTileToBoardArrayAt(thisTile, x, y);
			thisTile.declareHasBeenPlayed();
		}
		_scrabbleGame.getReferee().addToScore(_originalValue);
	}

}