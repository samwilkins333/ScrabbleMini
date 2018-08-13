package Scrabble;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Word {
	private String _letters;
	private int _value;
	private int _originalValue;
	private ArrayList<Tile> _tiles;
	private ArrayList<Tile> _kernelTiles;
	private ArrayList<Tile> _adjacentTiles;
	private ArrayList<Word> _horizontalCrosses;
	private ArrayList<Word> _verticalCrosses;
	private ArrayList<Word> _allCrosses;
//	private Boolean _isHumanGenerated;
	private ScrabbleGame _scrabbleGame;
	private String _playerNumber;
	private String _orientation;
	private int _firstLetterX;
	private int _firstLetterY;
	private String _newBoardLetters;
	private boolean _prefixExtendedStatus;
	private boolean _suffixExtendedStatus;

	public Word(String letters, String newBoardLetters, int value, int originalValue, String orientation, int firstLetterX, int firstLetterY, boolean prefixExtendedStatus, boolean suffixExtendedStatus) {
		_letters = letters;
		_newBoardLetters = newBoardLetters;
		_value = value;
		_originalValue = originalValue;
		_orientation = orientation;
		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;
		_tiles = new ArrayList<Tile>();
		_kernelTiles = new ArrayList<Tile>();
		_adjacentTiles = new ArrayList<Tile>();
		_prefixExtendedStatus = prefixExtendedStatus;
		_suffixExtendedStatus = suffixExtendedStatus;
	}
	
	public Word(String letters, int value, int firstLetterX, int firstLetterY) {
		_letters = letters;
		_value = value;
		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;
		_tiles = new ArrayList<Tile>();
	}
	
	public Word(String letters, int value, int firstLetterX, int firstLetterY, String orientation) {
		_letters = letters;
		_value = value;
		_originalValue = value;
		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;
		_tiles = new ArrayList<Tile>();
		_orientation = orientation;
	}
	
	public void addScrabbleGame(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
	}
	
	public Word(String letters, int value) {
		_letters = letters;
		_value = value;
//		_isHumanGenerated = false;
	}
	
	public boolean isPrefixExtended() {
		return _prefixExtendedStatus;
	}
	
	public boolean isSuffixExtended() {
		return _suffixExtendedStatus;
	}
	
	public int getFirstLetterX() {
		return _firstLetterX;
	}
	
	public int getFirstLetterY() {
		return _firstLetterY;
	}
	
	public String getOrientation() {
		return _orientation;
	}
	
	public String getNewBoardLetters() {
		return _newBoardLetters;
	}
	
	public int getOriginalValue() {
		return _originalValue;
	}
	
	public void printInfo() {
		System.out.printf("Oriented %s at %s, %s, %s yields a score of %s\n", _orientation, _firstLetterX, _firstLetterY, _letters, _value);
	}

	public Word(ScrabbleGame scrabbleGame, String playerNumber) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
		_tiles = new ArrayList<Tile>();
//		_isHumanGenerated = true;
	}
	
	public Word(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_tiles = new ArrayList<Tile>();
	}

	public void removeHorizontalAdjacents() {

	}

	public void addTiles(ArrayList<Tile> tiles) {
		_tiles.addAll(tiles);
	}
	
	public void addKernelTiles(ArrayList<Tile> kernelTiles) {
		_kernelTiles.addAll(kernelTiles);
	}
	
	public void addAdjacentTiles(ArrayList<Tile> kernelTiles) {
		_adjacentTiles.addAll(kernelTiles);
	}

	public Boolean containsATileAt(Tile tile, int x, int y) {
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile != tile && thisTile.getXIndex() == x && thisTile.getYIndex() == y) {
				return true;	
			}
		}
		return false;
	}

	public Tile getTileAt(int x, int y) {
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile.getXIndex() == x && thisTile.getYIndex() == y) {
				return thisTile;	
			}
		}
		return null;
	}

	public Boolean containsOnlyAddedTiles() {
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile.isAddedToBoard() == false) {
				return false;
			}
		}
		return true;
	}

	public Boolean isConnected() {
		if (_scrabbleGame.getReferee().firstMoveMade() == false) {
			return true;
		} else {
			Tile[][] tileArray = _scrabbleGame.getTileArray();
			for (int i = 0; i < _tiles.size(); i++) {
				Tile thisTile = _tiles.get(i);
				int x = thisTile.getXIndex();
				int y = thisTile.getYIndex();
				if (thisTile.isAddedToBoard()) {
					return true;
				}
				if (this.isAlignedHorizontally()) {
					if ( (y + 1 <= 14 && tileArray[x][y + 1] != null) || (y - 1 >= 0 && tileArray[x][y - 1] != null) ) {
						return true;
					}
				} else if (this.isAlignedVertically()) {
					if ( (x + 1 <= 14 && tileArray[x + 1][y] != null) || (x - 1 >= 0 && tileArray[x - 1][y] != null) ) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public void checkAddedTiles() {
		ArrayList<Tile> temp = new ArrayList<Tile>();
		ArrayList<Tile> tempVert = new ArrayList<Tile>();
		ArrayList<Tile> tempHoriz = new ArrayList<Tile>();
		this.compileWordCharacteristics();
		
		int size = _tiles.size();
		for (int i = 0; i < size; i++) {
			Tile thisTile = _tiles.get(0);
			if (thisTile.isAddedToBoard() == false) {
				temp.add(thisTile);
			}
			_tiles.remove(thisTile);
		}
		_tiles.addAll(temp);
		
		//Check for alignment
		if (this.isAligned()) {
			if (this.isAlignedVertically()) {
				
				System.out.println("CHECKING VERTICAL TILES");
				
				//For each tile in _tiles, collect new vertically adjacent tiles for the main word ArrayList, _tiles
				for (int i = 0; i < _tiles.size(); i++) {
					Tile thisTile = _tiles.get(i);
					tempVert.add(thisTile);
					_scrabbleGame.collectAdjacents("VERTICALLY", thisTile, tempVert);
					tempVert.remove(thisTile);
				}
				_tiles.addAll(tempVert); //Add
				this.readTiles("VERTICALLY"); //Update string based on vertical placement of tiles
			}
			
			if (this.isAlignedHorizontally()) {
				
				System.out.println("CHECKING HORIZONTAL TILES");
				
				//For each tile in _tiles, collect new horizontally adjacent tiles for the main word ArrayList, _tiles
				for (int i = 0; i < _tiles.size(); i++) {
					Tile thisTile = _tiles.get(i);
					tempHoriz.add(thisTile);
					_scrabbleGame.collectAdjacents("HORIZONTALLY", thisTile, tempHoriz);
					tempHoriz.remove(thisTile);
				}
				_tiles.addAll(tempHoriz); //Add
				this.readTiles("HORIZONTALLY"); //Update string based on horizontal placement of tiles
			}
		} else {
//			System.out.println("WORD NOT ALIGNED");
		}
		this.compileWordCharacteristics();
		this.addCrosses();
	}
	
	public void addCrosses() {
		//System.out.println("199" + _letters);
		_horizontalCrosses = new ArrayList<Word>();
		_verticalCrosses = new ArrayList<Word>();
		if (this.isAlignedVertically()) {
			System.out.println("CHECKING HORIZONTAL CROSSES");
			//System.out.println("204" + _letters);
			//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
			for (int i = 0; i < _tiles.size(); i++) {
				//System.out.println("207" + _letters);
				Tile thisTile = _tiles.get(i);
				if (!thisTile.isAddedToBoard()) { //If the tile is involved in the current turn
						Word cross = new Word(_scrabbleGame);
						_scrabbleGame.collectAdjacents("HORIZONTALLY", thisTile, cross.getTiles());
						if (cross.getTiles().size() > 1) {
							cross.compileWordCharacteristics();
							cross.updateValue(0);
							_horizontalCrosses.add(cross);	
						}
						
				}
			}
		}
		if (this.isAlignedHorizontally()) {
			System.out.println("CHECKING VERTICAL CROSSES");
			//System.out.println("223" + _letters);
			//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
			for (int i = 0; i < _tiles.size(); i++) {
				//System.out.println("226" + _letters);
				Tile thisTile = _tiles.get(i);
				if (!thisTile.isAddedToBoard()) { //If the tile is involved in the current turn
					Word cross = new Word(_scrabbleGame);
					_scrabbleGame.collectAdjacents("VERTICALLY", thisTile, cross.getTiles());
					if (cross.getTiles().size() > 1) {
						cross.compileWordCharacteristics();
						cross.updateValue(0);
						_verticalCrosses.add(cross);	
					}	
				}
			}
		}
		_allCrosses = new ArrayList<Word>();
		_allCrosses.addAll(_horizontalCrosses);
		_allCrosses.addAll(_verticalCrosses);
		//System.out.println("242" + _letters);
		if (_scrabbleGame.getReferee().getCurrentPlayer().getPlayerType() == "HUMAN") {
			this.checkValidCrosses();
		}
		//System.out.println("244" + _letters);
	}
	
	public ArrayList<Word> getAllCrosses() {
		return _allCrosses;
	}

	public void playFlashes(String id) {
		for (int i = 0; i < _tiles.size(); i++) {
			_tiles.get(i).playFlash(id);
		}
		if (_kernelTiles != null && _kernelTiles.size() > 0) {
			for (int i = 0; i < _kernelTiles.size(); i++) {
				_kernelTiles.get(i).playFlash(id);
			}
		}
		if (_adjacentTiles != null && _adjacentTiles.size() > 0) {
			for (int i = 0; i < _adjacentTiles.size(); i++) {
				_adjacentTiles.get(i).playFlash(id);
			}
		}
	}
	
	public int getAdjacentTilesSize() {
		return _adjacentTiles.size();
	}
	
	public ArrayList<Tile> getAdjacentTiles() {
		return _adjacentTiles;
	}

	public void printAllTileLists() {
		if (this.isAligned()) {
			this.printTileListContents(_tiles);	
			for (int i = 0; i < _allCrosses.size(); i++) {
				this.printTileListContents(_allCrosses.get(i).getTiles());
			}				
		}
		//this.printTileListContents(_scrabbleGame.getTilesOnBoard());
	}
	
	public void printWord() {
		for (int i = 0; i < _letters.length(); i++) {
			System.out.printf("%s", String.valueOf(_letters.charAt(i)));
		}
		System.out.println("");
	}
	
	public void printValue() {
		System.out.printf("Value = %s\n", _value);
	}

	public void printTileListContents(ArrayList<Tile> tileList) {
		if (tileList == _tiles) {
			System.out.printf("\n_word...");
		} else {
			System.out.printf("\nCross...");
		}
		if (tileList.size() == 0) {
			System.out.println("...has no tiles\n");
		} else if (tileList.size() > 0) {
			for (int i = 0; i < tileList.size(); i++) {
				System.out.printf("%s", tileList.get(i).getLetter());
			}	
		}
		if (this.isAlignedVertically() && tileList == _tiles) {
			System.out.printf("\nVERTICAL");
		} 
		if (this.isAlignedHorizontally() == true && tileList == _tiles) {
			System.out.printf("\nHORIZONTAL");
		} 
	}

	public Tile getTile(String letter) {
		boolean flag = false;
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile.getLetter() == letter && flag == false) {
				flag = true;
				return thisTile;
			} else {
//				System.out.printf("Still looking for a(n) %s tile\n", letter);
			}
		}
//		System.out.printf("Word has no %s tiles\n", letter);
		return null;
	}

	public void addTileToWord(Tile tile) {
		_tiles.add(tile);
	}

	public void readTiles(String id) {
		ArrayList<Tile> temp = new ArrayList<Tile>();
		int size = _tiles.size();
		for (int i = 0; i < size; i++) {
			Tile thisTile = _tiles.get(0);
			temp.add(thisTile);
			_tiles.remove(thisTile);
		}
		size = temp.size();
		if (id == "VERTICALLY") {
			for (int i = 0; i < size; i++) {
				Tile firstTile = null;
				for (int j = 0; j < temp.size(); j++) {
					Tile thisTile = temp.get(j);
					if (j == 0) {
						firstTile = thisTile;
					} else {
						if (thisTile.getYIndex() < firstTile.getYIndex()) {
							firstTile = thisTile;
						}
					}
				}	
				_tiles.add(firstTile);
				temp.remove(firstTile);
			}
		} else if (id == "HORIZONTALLY") {
			for (int i = 0; i < size; i++) {
				Tile firstTile = null;
				for (int j = 0; j < temp.size(); j++) {
					Tile thisTile = temp.get(j);
					if (j == 0) {
						firstTile = thisTile;
					} else {
						if (thisTile.getXIndex() < firstTile.getXIndex()) {
							firstTile = thisTile;
						}
					}
				}
				_tiles.add(firstTile);
				temp.remove(firstTile);
			}
		}
		_letters = _scrabbleGame.tilesToString(_tiles);
//		System.out.printf("Word after reading is %s...\n", _letters);
	}

	public void addToBoard() {
		if (this.isPlayable()) {
			int numNewTiles = 0;
			for (int i = 0; i < _tiles.size(); i++) {
				Tile thisTile = _tiles.get(i);
				int x = thisTile.getXIndex();
				int y = thisTile.getYIndex();
//				System.out.printf("%s tile added to board at %s, %s\n", thisTile.getLetter(), x, y);
				_scrabbleGame.addTileToBoardArrayAt(thisTile, x, y);
				if (_playerNumber == "PLAYER ONE") {
					_scrabbleGame.getPlayerOneRack().remove(thisTile);	
				} else if (_playerNumber == "PLAYER TWO") {
					_scrabbleGame.getPlayerTwoRack().remove(thisTile);	
				}
				if (!thisTile.isAddedToBoard()) {
					numNewTiles++;
				}
				thisTile.isAddedToBoard(true);
			}
			int bonus = 0;
			if (numNewTiles == 7) {
				bonus = 50;
			}
			this.updateValue(bonus);
			_scrabbleGame.getReferee().addToScore(_value);
			System.out.printf("%s, with a value of %s, has been added to board\n", _letters, _value);
		}
	}

	public void checkValidCrosses() {
		this.isPlayable();
	}
	
	public void compileWordCharacteristics() {
		this.isFormatted();
		this.isInDictionary();
		this.firstMoveSatisfied();
	}

//BOOLEAN HIERARCHY

	public Boolean isPlayable() {
//		System.out.printf("\n\n\n\n");
		if (_tiles.size() > 0) {
			if (this.isFormatted() && this.isInDictionary() && this.firstMoveSatisfied() && this.hasValidCrosses()) {
//				System.out.printf("%s...IS formatted, IS in dictionary and DOES have valid crosses - PLAYABLE\n", _letters);
				return true;
			} else {
				if (this.isFormatted() && !this.isInDictionary()) {
//					System.out.printf("%s...is formatted, but not in dictionary - not playable\n", _letters);
				} else if (!this.isFormatted() && this.isInDictionary()) {
//					System.out.printf("%s...is in dictionary, but not formatted - not playable\n", _letters);
				} else if  (this.isFormatted() && this.isInDictionary() && !this.hasValidCrosses());
//					System.out.printf("%s...is in dictionary, is formatted, but invalid crosses - not playable\n", _letters);
				return false;
			}
		} else {
//			System.out.println("No word placed - not playable");
			return false;
		}
	}
	
	private boolean hasValidCrosses() {
		if (this.hasValidHorizontalCrosses() && this.hasValidVerticalCrosses()) {
			return true;
		}
		return false;
	}

	private boolean hasValidHorizontalCrosses() {
		if (_horizontalCrosses.size() == 0) {
			return true;
		}
		for (int i = 0; i < _horizontalCrosses.size(); i++) {
			Word thisCross = _horizontalCrosses.get(i);
			if (!thisCross.isInDictionary()) {
				System.out.printf("Cross %s is NOT in dictionary\n", thisCross.getLetters());
				return false;
			} else {
				System.out.printf("Cross %s IS in dictionary\n", thisCross.getLetters());
			}
		}
		System.out.println("All horizontal crosses valid");
		return true;
	}
	
	private boolean hasValidVerticalCrosses() {
		if (_verticalCrosses.size() == 0) {
			return true;
		}
		for (int i = 0; i < _verticalCrosses.size(); i++) {
			Word thisCross = _verticalCrosses.get(i);
			if (!thisCross.isInDictionary()) {
				System.out.printf("Cross %s is NOT in dictionary\n", thisCross.getLetters());
				return false;
			} else {
				System.out.printf("Cross %s IS in dictionary\n", thisCross.getLetters());
			}
		}
		System.out.println("All vertical crosses valid");
		return true;
	}

	public boolean isInDictionary() {
		if (_scrabbleGame.tilesToDictBool(_tiles)) {
			//System.out.println("WORD IS IN DICTIONARY");
			return true;
		}
		return false;
	}

	public boolean isFormatted() {
		if (this.isAligned() && this.isCompact() && this.isConnected()) {
			return true;
		} 
		return false;
	}

	public boolean isAligned() {
		if (_tiles.size() <= 1) {
			return true;
		} else {
			if (this.isAlignedVertically() || this.isAlignedHorizontally()) {
				return true;
			}
			return false;
		}
	}

	public boolean isAlignedVertically() {
		if (_scrabbleGame.getReferee().getCurrentPlayer().getPlayerType() == "COMPUTER") {
			if (_orientation == "VERTICAL") {
				return true;
			} else if (_orientation == "HORIZONTAL")
				return false;
		}
		if (_tiles.size() < 1) {
			return false;
		}
		if (_tiles.size() == 1) {
			return true;
		}
		Tile firstTile = _tiles.get(0);
		for (int i = 1; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile.getXIndex() != firstTile.getXIndex()) {
				return false;
			}
		}
		//System.out.println("WORD IS ALIGNED VERTICALLY");
		return true;
	}

	public boolean isAlignedHorizontally() {
		if (_scrabbleGame.getReferee().getCurrentPlayer().getPlayerType() == "COMPUTER") {
			if (_orientation == "VERTICAL") {
				return false;
			} else if (_orientation == "HORIZONTAL")
				return true;
		}
		if (_tiles.size() < 1) {
			return false;
		}
		if (_tiles.size() == 1) {
			return true;
		}
		Tile firstTile = _tiles.get(0);
		for (int i = 1; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (thisTile.getYIndex() != firstTile.getYIndex()) {
				return false;
			}
		}
		//System.out.println("WORD IS ALIGNED HORIZONTALLY");
		return true;
	}

	public boolean isCompact() {
		if (_tiles.size() <= 1) {
			return true;
		} else {
			if (this.isCompactVertically() || this.isCompactHorizontally()) {
				return true;
			}
			return false;
		}
	}

	public boolean isCompactVertically() {
		this.readTiles("VERTICALLY");
		Tile firstTile = _tiles.get(0);
		int firstY = firstTile.getYIndex();
		for (int i = 1; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			int thisY = thisTile.getYIndex();
			if (thisY - i != firstY) {
				return false;
			}
		}
		//System.out.println("WORD IS COMPACT VERTICALLY");
		return true;
	}

	public boolean isCompactHorizontally() {
		this.readTiles("HORIZONTALLY");
		Tile firstTile = _tiles.get(0);
		int firstX = firstTile.getXIndex();
		for (int i = 1; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			int thisX = thisTile.getXIndex();
			if (thisX - i != firstX) {
				return false;
			}
		}
		//System.out.println("WORD IS COMPACT HORIZONTALLY");
		return true;
	}

	public boolean firstMoveSatisfied() {
		if (_scrabbleGame.getReferee().firstMoveMade() == false) {
			for (int i = 0; i < _tiles.size(); i++) {
				Tile thisTile = _tiles.get(i);
				if (thisTile.getXIndex() == 7 && thisTile.getYIndex() == 7) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}
	
//

	public void clear() {
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (!thisTile.isAddedToBoard()) {
				thisTile.setIsOnBoard(false);
				thisTile.setIsPartOfNewestWord(false);
				thisTile.stopOverlapFlash();
				thisTile.setToOpaque();
				thisTile.resetShadow();
				tilesOnBoard.remove(thisTile);
			}
		}
//		System.out.printf("\n%s tiles on board\n\n", tilesOnBoard.size());
		if (_tiles != null) {
			_tiles.clear();
		}
		if (_horizontalCrosses != null) {
			_horizontalCrosses.clear();
		}
		if (_verticalCrosses != null) {
			_verticalCrosses.clear();
		}
		if (_allCrosses != null) {
			_allCrosses.clear();
		}
	}

	public void removeTileFromWord(Tile tile) {
		_tiles.remove(tile);
	}

	public void updateValue(int bonus) {
		if (this.isFormatted()) {
			_value = _scrabbleGame.getWordValue(_tiles) + bonus;
//			System.out.printf("UV: %s has %s letters and a value of %s...\n", _letters, _tiles.size(), _value);	
		} else {
//			System.out.println("WORD NOT FORMATTED");
		}
	} 
	
	public ArrayList<Tile> getTiles() {
		return _tiles;
	}

	public int getNumTiles() {
		return _tiles.size();
	}

	public String getLetters() {
		return _letters;
	}

	public int getValue() {
		return _value;
	}

	public int getNumLetters() {
		return _letters.length();
	}

	public void printData() {
		System.out.printf("Word %s has a value of %s and has %s tiles...\n", _letters, _value, _tiles.size());
	}

	public void printContents() {
		System.out.printf("Current letters = %s\n", _letters);
		this.printTileListContents(_tiles);
	}

	public void arrangeTilesOnBoard() {
		double delay = 0;
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			if (!tilesOnBoard.contains(thisTile)) {
				tilesOnBoard.add(thisTile);
			}
		}
		int x = 0;
		int y = 0;
		int tileInt = -1;
		for (int i = 0; i < _letters.length(); i++) {
			if (_orientation == "HORIZONTAL") {
				x = _firstLetterX + i;
				y = _firstLetterY; 
			} else if (_orientation == "VERTICAL") {
				x = _firstLetterX;
				y = _firstLetterY + i; 
			}
			System.out.printf("Checking for placement at %s, %s\n", x, y);
			if (!_scrabbleGame.boardSquareOccupiedAt(x, y)) {
				tileInt = tileInt + 1;
				Tile thisTile = _tiles.get(tileInt);
				delay = delay + Constants.PLACEMENT_DURATION;
				PauseTransition delayPlacement = new PauseTransition(Duration.seconds(delay));
				delayPlacement.setOnFinished(new PlaceHandler(x, y, thisTile));
				delayPlacement.play();
			} else {
				Tile thisTile = _scrabbleGame.getTileFromArrayAt(x, y);
				if (!_kernelTiles.contains(thisTile)) {
					_adjacentTiles.add(thisTile);
				}
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
		
		public PlaceHandler(int x, int y, Tile thisTile) {
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
	
	public void addToBoardAI() {
		this.printContents();
		for (int i = 0; i < _tiles.size(); i++) {
			Tile thisTile = _tiles.get(i);
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();
			_scrabbleGame.addTileToBoardArrayAt(thisTile, x, y);
			thisTile.isAddedToBoard(true);
		}
		_scrabbleGame.getReferee().addToScore(_originalValue);
	}

}