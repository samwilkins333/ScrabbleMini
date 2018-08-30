package Scrabble;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import static java.lang.System.out;

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
	private ScrabbleGame _scrabbleGame;
	private PlayerNumber _playerNumber;
	private Orientation _orientation;
	private int _firstLetterX;
	private int _firstLetterY;
	private String _newBoardLetters;
	private boolean _prefixExtendedStatus;
	private boolean _suffixExtendedStatus;

	Word(String letters, String newBoardLetters, int value, int originalValue, Orientation orientation, int firstLetterX, int firstLetterY, boolean prefixExtendedStatus, boolean suffixExtendedStatus) {
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
		_prefixExtendedStatus = prefixExtendedStatus;
		_suffixExtendedStatus = suffixExtendedStatus;
	}

    Word(String letters, int value, int firstLetterX, int firstLetterY, Orientation orientation) {
		_letters = letters;
		_value = value;
		_originalValue = value;
		_firstLetterX = firstLetterX;
		_firstLetterY = firstLetterY;
		_tiles = new ArrayList<>();
		_orientation = orientation;
	}
	
	void addScrabbleGame(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
	}

    public Orientation getOrientation() {
		return _orientation;
	}
	
	String getNewBoardLetters() {
		return _newBoardLetters;
	}
	
	int getOriginalValue() {
		return _originalValue;
	}
	
	void printInfo() {
		out.printf("Oriented %s at %s, %s, %s yields a score of %s\n", _orientation, _firstLetterX, _firstLetterY, _letters, _value);
	}

	Word(ScrabbleGame scrabbleGame, PlayerNumber playerNumber) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
		_tiles = new ArrayList<>();
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

    Boolean containsTileAt(Tile tile, int x, int y) {
        for (Tile thisTile : _tiles) {
            if (thisTile != tile && thisTile.getXIndex() == x && thisTile.getYIndex() == y) {
                return true;
            }
        }
		return false;
	}

    Boolean containsOnlyAddedTiles() {
        for (Tile thisTile : _tiles) if (!thisTile.isAddedToBoard()) return false;
		return true;
	}

	private Boolean isConnected() {
		if (!_scrabbleGame.getReferee().firstMoveMade()) return true;

        Tile[][] tileArray = _scrabbleGame.getTileArray();
        for (Tile thisTile : _tiles) {
            int x = thisTile.getXIndex();
            int y = thisTile.getYIndex();
            if (thisTile.isAddedToBoard()) {
                return true;
            }
            if (this.isAlignedHorizontally()) {
                if ((y + 1 <= 14 && tileArray[x][y + 1] != null) || (y - 1 >= 0 && tileArray[x][y - 1] != null)) {
                    return true;
                }
            } else if (this.isAlignedVertically()) {
                if ((x + 1 <= 14 && tileArray[x + 1][y] != null) || (x - 1 >= 0 && tileArray[x - 1][y] != null)) {
                    return true;
                }
            }
        }
        return false;
    }

	void checkAddedTiles() {
		ArrayList<Tile> temp = new ArrayList<>();
		ArrayList<Tile> tempVert = new ArrayList<>();
		ArrayList<Tile> tempHoriz = new ArrayList<>();
		compileWordCharacteristics();
		
		int size = _tiles.size();
		for (int i = 0; i < size; i++) {
			Tile thisTile = _tiles.get(0);
			if (!thisTile.isAddedToBoard()) {
				temp.add(thisTile);
			}
			_tiles.remove(thisTile);
		}
		_tiles.addAll(temp);
		
		//Check for alignment
		if (this.isAligned()) {
			if (this.isAlignedVertically()) {
				
				//For each tile in _tiles, collect new vertically adjacent tiles for the main word ArrayList, _tiles
                for (Tile thisTile : _tiles) {
                    tempVert.add(thisTile);
                    _scrabbleGame.collectAdjacent(Orientation.Vertical, thisTile, tempVert);
                    tempVert.remove(thisTile);
                }
				_tiles.addAll(tempVert); //Add
				this.readTiles(Orientation.Vertical); //Update string based on vertical placement of tiles
			}
			
			if (this.isAlignedHorizontally()) {
				
				//For each tile in _tiles, collect new horizontally adjacent tiles for the main word ArrayList, _tiles
				for (Tile thisTile : _tiles) {
					tempHoriz.add(thisTile);
					_scrabbleGame.collectAdjacent(Orientation.Horizontal, thisTile, tempHoriz);
					tempHoriz.remove(thisTile);
				}
				_tiles.addAll(tempHoriz); //Add
				this.readTiles(Orientation.Horizontal); //Update string based on horizontal placement of tiles
			}
		} else {
//			System.out.println("WORD NOT ALIGNED");
		}
		this.compileWordCharacteristics();
		this.addCrosses();
	}
	
	private void addCrosses() {
		//System.out.println("199" + _letters);
		_horizontalCrosses = new ArrayList<>();
		_verticalCrosses = new ArrayList<>();
		if (isAlignedVertically()) {
			//System.out.println("204" + _letters);
			//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
			for (Tile thisTile : _tiles) {
				//System.out.println("207" + _letters);
				if (!thisTile.isAddedToBoard()) { //If the tile is involved in the current turn
					Word cross = new Word(_scrabbleGame);
					_scrabbleGame.collectAdjacent(Orientation.Horizontal, thisTile, cross.getTiles());
					if (cross.getTiles().size() > 1) {
						cross.compileWordCharacteristics();
						cross.updateValue(0);
						_horizontalCrosses.add(cross);
					}

				}
			}
		}
		if (isAlignedHorizontally()) {
			//System.out.println("223" + _letters);
			//For each tile in _tiles, collect new horizontally adjacent tiles for the corollary "cross" word ArrayList, _horizontalCrosses
			for (Tile thisTile : _tiles) {
				//System.out.println("226" + _letters);
				if (!thisTile.isAddedToBoard()) { //If the tile is involved in the current turn
					Word cross = new Word(_scrabbleGame);
					_scrabbleGame.collectAdjacent(Orientation.Vertical, thisTile, cross.getTiles());
					if (cross.getTiles().size() > 1) {
						cross.compileWordCharacteristics();
						cross.updateValue(0);
						_verticalCrosses.add(cross);
					}
				}
			}
		}
		_allCrosses = new ArrayList<>();
		_allCrosses.addAll(_horizontalCrosses);
		_allCrosses.addAll(_verticalCrosses);
		//System.out.println("242" + _letters);
		if (_scrabbleGame.getReferee().getCurrentPlayer().getPlayerType() == PlayerType.Human) {
			this.checkValidCrosses();
		}
		//System.out.println("244" + _letters);
	}
	
	ArrayList<Word> getAllCrosses() {
		return _allCrosses;
	}

	void playFlashes(Outcome outcome) {
		for (Tile _tile : _tiles) _tile.playFlash(outcome);

		if (_kernelTiles != null && !_kernelTiles.isEmpty())
			for (Tile _kernelTile : _kernelTiles) _kernelTile.playFlash(outcome);

		if (_adjacentTiles != null && !_adjacentTiles.isEmpty())
			for (Tile _adjacentTile : _adjacentTiles) _adjacentTile.playFlash(outcome);
	}

	void printTileListContents(ArrayList<Tile> tileList) {
		if (tileList == _tiles) {
			out.print("\n_word...");
		} else {
			out.print("\nCross...");
		}
		if (tileList.size() == 0) {
			out.println("...has no tiles\n");
		} else {
			for (Tile tile : tileList) {
				out.printf("%s", tile.getLetter());
			}
		}
		if (this.isAlignedVertically() && tileList == _tiles) {
			out.print("\nVERTICAL");
		} 
		if (isAlignedHorizontally() && tileList == _tiles) {
			out.print("\nHORIZONTAL");
		} 
	}

	void addTileToWord(Tile tile) {
		_tiles.add(tile);
		tile.setIsPartOfNewestWord(true);
	}

	private void readTiles(Orientation orientation) {
		ToIntFunction<Tile> index = orientation == Orientation.Horizontal? Tile::getXIndex : Tile::getYIndex;
		_tiles = (ArrayList<Tile>) _tiles.stream().sorted(Comparator.comparingInt(index)).collect(Collectors.toList());
		_letters = _scrabbleGame.tilesToString(_tiles);
	}

	void addToBoard() {
		if (!isPlayable()) return;

		int numNewTiles = 0;
		for (Tile thisTile : _tiles) {
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();

			_scrabbleGame.addTileToBoardArrayAt(thisTile, x, y);
			_scrabbleGame.getRackFor(_playerNumber).remove(thisTile);

			if (!thisTile.isAddedToBoard()) numNewTiles++;

			thisTile.isAddedToBoard(true);
		}

		this.updateValue(numNewTiles == 7 ? 50 : 0);
		_scrabbleGame.getReferee().addToScore(_value);
		out.printf("%s, with a value of %s, has been added to board\n", _letters, _value);
	}

	private void checkValidCrosses() {
		isPlayable();
	}
	
	private void compileWordCharacteristics() {
		isFormatted();
		isInDictionary();
		firstMoveSatisfied();
	}

//BOOLEAN HIERARCHY

	Boolean isPlayable() {
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
				out.printf("Cross %s is NOT in dictionary\n", thisCross.getLetters());
				return false;
			} else {
				out.printf("Cross %s IS in dictionary\n", thisCross.getLetters());
			}
		}
		out.println("All horizontal crosses valid");
		return true;
	}
	
	private boolean hasValidVerticalCrosses() {
		if (_verticalCrosses.size() == 0) {
			return true;
		}
		for (int i = 0; i < _verticalCrosses.size(); i++) {
			Word thisCross = _verticalCrosses.get(i);
			if (!thisCross.isInDictionary()) {
				out.printf("Cross %s is NOT in dictionary\n", thisCross.getLetters());
				return false;
			} else {
				out.printf("Cross %s IS in dictionary\n", thisCross.getLetters());
			}
		}
		out.println("All vertical crosses valid");
		return true;
	}

	boolean isInDictionary() {
		if (_scrabbleGame.tilesToDictBool(_tiles)) {
			//System.out.println("WORD IS IN DICTIONARY");
			return true;
		}
		return false;
	}

	public boolean isFormatted() {
		return this.isAligned() && this.isCompact() && this.isConnected();
	}

	public boolean isAligned() {
		if (_tiles.size() <= 1 || _scrabbleGame.getReferee().getCurrentPlayer().getPlayerType() == PlayerType.AI) return true;
		return this.isAlignedVertically() || this.isAlignedHorizontally();
	}

	private boolean isAlignedVertically() {
		if (_tiles.size() < 1) return false;
		if (_tiles.size() == 1) return true;
		Tile firstTile = _tiles.get(0);
		for (int i = 1; i < _tiles.size(); i++) {
			if (_tiles.get(i).getXIndex() != firstTile.getXIndex()) return false;
		}
		//System.out.println("WORD IS ALIGNED VERTICALLY");
		return true;
	}

	private boolean isAlignedHorizontally() {
		if (_tiles.size() < 1) return false;
		if (_tiles.size() == 1) return true;
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
		if (_tiles.size() <= 1) return true;
		return this.isCompactVertically() || this.isCompactHorizontally();
	}

	private boolean isCompactVertically() {
		readTiles(Orientation.Vertical);

		Tile firstTile = _tiles.get(0);
		int firstY = firstTile.getYIndex();

		for (int i = 1; i < _tiles.size(); i++) {
			if (_tiles.get(i).getYIndex() - i != firstY) return false;
		}
		//System.out.println("WORD IS COMPACT VERTICALLY");
		return true;
	}

	private boolean isCompactHorizontally() {
		this.readTiles(Orientation.Horizontal);
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

	private boolean firstMoveSatisfied() {
		if (_scrabbleGame.getReferee().firstMoveMade()) return true;

		for (Tile thisTile : _tiles) {
			if (thisTile.getXIndex() == 7 && thisTile.getYIndex() == 7) return true;
		}

		return false;
	}

	public void clear() {
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		for (Tile thisTile : _tiles) {
			if (thisTile.isAddedToBoard()) continue;

			thisTile.setIsOnBoard(false);
			thisTile.setIsPartOfNewestWord(false);
			thisTile.stopOverlapFlash();
			thisTile.getTileViewer().setOpacity(1);
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
		tile.setIsPartOfNewestWord(false);
	}

	private void updateValue(int bonus) {
		if (!this.isFormatted()) return;
		_value = _scrabbleGame.getWordValue(_tiles) + bonus;
	} 
	
	public ArrayList<Tile> getTiles() {
		return _tiles;
	}

	public String getLetters() {
		return _letters;
	}

	public int getValue() {
		return _value;
	}

	private void printContents() {
		out.printf("Current letters = %s\n", _letters);
		this.printTileListContents(_tiles);
	}

	void arrangeTilesOnBoard() {
		double delay = 0;
		ArrayList<Tile> tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		_tiles.forEach(tile -> { if (!tilesOnBoard.contains(tile)) tilesOnBoard.add(tile); });

		int x;
		int y;
		int tileIndex = -1;

		for (int i = 0; i < _letters.length(); i++) {
			boolean isHorizontal = _orientation == Orientation.Horizontal;
			x = isHorizontal ? _firstLetterX + i : _firstLetterX;
			y = isHorizontal ? _firstLetterY : _firstLetterY + i;

			if (_scrabbleGame.boardSquareOccupiedAt(x, y)) {
				Tile thisTile = _scrabbleGame.getTileFromArrayAt(x, y);
				if (!_kernelTiles.contains(thisTile)) _adjacentTiles.add(thisTile);
			} else {
				tileIndex++;

				Tile thisTile = _tiles.get(tileIndex);
				delay += Constants.PLACEMENT_DURATION;

				int targetX = x;
				int targetY = y;

				PauseTransition delayPlacement = new PauseTransition(Duration.seconds(delay));
				delayPlacement.setOnFinished(event -> thisTile.placeAtSquare(targetX, targetY));
				delayPlacement.play();
			}
		}
		PauseTransition delayCrosses = new PauseTransition(Duration.seconds(delay + Constants.PLACEMENT_DURATION/2));
		delayCrosses.setOnFinished(event -> addCrosses());
		delayCrosses.play();

		PauseTransition delayAddition = new PauseTransition(Duration.seconds(delay + Constants.PLACEMENT_DURATION));
		delayAddition.setOnFinished(event -> _scrabbleGame.getOrganizer().addWordAI(this));
		delayAddition.play();
	}

	void addToBoardAI() {
		printContents();

		for (Tile thisTile : _tiles) {
			_scrabbleGame.addTileToBoardArrayAt(thisTile, thisTile.getXIndex(), thisTile.getYIndex());
			thisTile.isAddedToBoard(true);
		}

		_scrabbleGame.getReferee().addToScore(_originalValue);
	}

}