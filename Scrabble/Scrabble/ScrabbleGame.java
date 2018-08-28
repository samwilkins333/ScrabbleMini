package Scrabble;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.layout.*;

import java.io.*;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.event.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ScrabbleGame {
	private PaneOrganizer _organizer;
	private Pane _root;
	private Pane _boardPane;
	private Pane _labelPane;
	private TileBag _tileBag;
	private ArrayList<Tile> _playerOneRack;
	private ArrayList<Tile> _playerTwoRack;
	private HashSet<String> _dictionary;
	private BoardSquare[][] _boardArray;
	private ArrayList<BoardSquare> _doubleLetterScores;
	private ArrayList<BoardSquare> _doubleWordScores;
	private ArrayList<BoardSquare> _tripleLetterScores;
	private ArrayList<BoardSquare> _tripleWordScores;
	private Word _playerOneBest;
	private Word _playerTwoBest;
	private ArrayList<FadeTransition> _fadeIns;
	private ArrayList<FadeTransition> _fadeOuts;
	private Tile[][] _tileArray;
	private ArrayList<Tile> _tilesOnBoard;
	private ArrayList<BoardSquare> _specialSquares;
	private ImageView _diamondViewer;
	private Boolean _gameIsPlaying;
	private Referee _referee;
	private ArrayList<Tile> _endGame;
	private boolean _autoReset;

	ScrabbleGame(PaneOrganizer organizer, Pane root, Pane boardPane) {
		_organizer = organizer;
		_root = root;
		_boardPane = boardPane;
		_labelPane = new Pane();
		_tileBag = new TileBag(this);
		_tileArray = new Tile[15][15];
		_playerOneRack = new ArrayList<>();
		_playerTwoRack = new ArrayList<>();
		_boardArray = new BoardSquare[15][15];
		_dictionary = new HashSet<>();
		_fadeOuts = new ArrayList<>();
		_fadeIns = new ArrayList<>();
		_tilesOnBoard = new ArrayList<>();
		_doubleLetterScores = new ArrayList<>();
		_doubleWordScores = new ArrayList<>();
		_tripleLetterScores = new ArrayList<>();
		_tripleWordScores = new ArrayList<>();
		_specialSquares = new ArrayList<>();
		_gameIsPlaying = false;
		_autoReset = false;
		this.setUpBoard();
		this.setUpTiles();
		this.setUpDictionary();
	}

	public Pane getRoot() {
		return _root;
	}

	String getRackAsString() {
		if (_referee.currentPlayer() == "PLAYER ONE") {
			return this.tilesToString(_playerOneRack);
		} else {
			return this.tilesToString(_playerTwoRack);
		}
	}
	
	boolean tileBagIsEmpty() {
		return _tileBag.isEmpty();
	}
	
	void setAutoReset(boolean status) {
		_autoReset = status;
	}

    Boolean isBagEmpty() {
		return _tileBag.isEmpty();
	}

    ArrayList<Tile> getRackFor(PlayerNumber number) {
		return number == PlayerNumber.One ? _playerOneRack : _playerTwoRack;
	}

	void manageDraw(String id) {
		_organizer.manageDraw(id);
	}
	
	Boolean gameIsPlaying() {
		return _gameIsPlaying;
	}

	void startGamePlay() {
		_gameIsPlaying = true;
	}

	void pauseGamePlay() {
		_gameIsPlaying = false;
	}

	void fadeRacks(int player, String direction) {
		ArrayList<Tile> tileList = null;
		double from = 0;
		double to = 0;
		if (player == 1) {
			tileList = _playerOneRack;
		} else if (player == 2) {
			tileList = _playerTwoRack;
		}
		if (direction == "IN") {
			from = Constants.FADED_TILE_OPACITY;
			to = 1.0;
		} else if (direction == "OUT") {
			from = 1.0;
			to = Constants.FADED_TILE_OPACITY;
		}
		for (int i = 0; i < tileList.size(); i++) {
			Tile thisTile = tileList.get(i);
			FadeTransition fade = new FadeTransition(Duration.seconds(0.5), thisTile.getTileViewer());
			fade.setFromValue(from);
			fade.setToValue(to);
			fade.play();
		}
	}

	private void setUpBoard() {
		for (int i = 0; i < _boardArray.length; i++) {
			for (int j = 0; j < _boardArray[1].length; j++) {
				int xLayout = (i + Constants.ZEROETH_COLUMN_OFFSET) * Constants.GRID_FACTOR;
				int yLayout = (j + Constants.ZEROETH_ROW_OFFSET) * Constants.GRID_FACTOR;
				BoardSquare boardSquare = new BoardSquare(xLayout, yLayout, "NORMAL", _boardPane, _labelPane);
				_boardArray[i][j] = boardSquare;
			}
		}
		_boardPane.getChildren().add(_labelPane);
		this.setUpSpecialBoardSquares();
	}

	void addReferee(Referee referee) {
		_referee = referee;
	}

	public Referee getReferee() {
		return _referee;
	}

	Boolean getIsShiftDown() {
		return _organizer.getIsShiftDown();
	}

	void resetRack(PlayerNumber number) {
		ArrayList<Tile> rack = number == PlayerNumber.One ? _playerOneRack : _playerTwoRack;
		List<Tile> toMove = rack.stream().filter(Tile::isOnBoard).collect(Collectors.toList());
		for (Tile tile : toMove) tile.reset(true);
	}

	private void setUpSpecialBoardSquares() {

		// DOUBLE LETTER SCORES (BLUE)

		// Central square
		_boardArray[6][6].setID("DOUBLE LETTER SCORE");
		_boardArray[6][8].setID("DOUBLE LETTER SCORE");
		_boardArray[8][6].setID("DOUBLE LETTER SCORE");
		_boardArray[8][8].setID("DOUBLE LETTER SCORE");

		// Horizontal Outer solos
		_boardArray[3][0].setID("DOUBLE LETTER SCORE");
		_boardArray[11][0].setID("DOUBLE LETTER SCORE");
		_boardArray[3][14].setID("DOUBLE LETTER SCORE");
		_boardArray[11][14].setID("DOUBLE LETTER SCORE");

		// Vertical Outer solos
		_boardArray[0][3].setID("DOUBLE LETTER SCORE");
		_boardArray[0][11].setID("DOUBLE LETTER SCORE");
		_boardArray[14][3].setID("DOUBLE LETTER SCORE");
		_boardArray[14][11].setID("DOUBLE LETTER SCORE");

		// Lower blue trio
		_boardArray[7][11].setID("DOUBLE LETTER SCORE");
		_boardArray[8][12].setID("DOUBLE LETTER SCORE");
		_boardArray[6][12].setID("DOUBLE LETTER SCORE");

		// Left blue trio
		_boardArray[3][7].setID("DOUBLE LETTER SCORE");
		_boardArray[2][6].setID("DOUBLE LETTER SCORE");
		_boardArray[2][8].setID("DOUBLE LETTER SCORE");

		// Right blue trio
		_boardArray[11][7].setID("DOUBLE LETTER SCORE");
		_boardArray[12][6].setID("DOUBLE LETTER SCORE");
		_boardArray[12][8].setID("DOUBLE LETTER SCORE");

		// Upper blue trio
		_boardArray[7][3].setID("DOUBLE LETTER SCORE");
		_boardArray[6][2].setID("DOUBLE LETTER SCORE");
		_boardArray[8][2].setID("DOUBLE LETTER SCORE");

		_doubleLetterScores.add(_boardArray[6][6]);
		_doubleLetterScores.add(_boardArray[6][8]);
		_doubleLetterScores.add(_boardArray[8][6]);
		_doubleLetterScores.add(_boardArray[8][8]);

		_doubleLetterScores.add(_boardArray[3][0]);
		_doubleLetterScores.add(_boardArray[11][0]);
		_doubleLetterScores.add(_boardArray[3][14]);
		_doubleLetterScores.add(_boardArray[11][14]);

		_doubleLetterScores.add(_boardArray[0][3]);
		_doubleLetterScores.add(_boardArray[0][11]);
		_doubleLetterScores.add(_boardArray[14][3]);
		_doubleLetterScores.add(_boardArray[14][11]);

		_doubleLetterScores.add(_boardArray[7][11]);
		_doubleLetterScores.add(_boardArray[8][12]);
		_doubleLetterScores.add(_boardArray[6][12]);

		_doubleLetterScores.add(_boardArray[3][7]);
		_doubleLetterScores.add(_boardArray[2][6]);
		_doubleLetterScores.add(_boardArray[2][8]);

		_doubleLetterScores.add(_boardArray[11][7]);
		_doubleLetterScores.add(_boardArray[12][6]);
		_doubleLetterScores.add(_boardArray[12][8]);

		_doubleLetterScores.add(_boardArray[7][3]);
		_doubleLetterScores.add(_boardArray[6][2]);
		_doubleLetterScores.add(_boardArray[8][2]);

		// DOUBLE WORD SCORES (RED)

		// Northwest
		_boardArray[1][1].setID("DOUBLE WORD SCORE");
		_boardArray[2][2].setID("DOUBLE WORD SCORE");
		_boardArray[3][3].setID("DOUBLE WORD SCORE");
		_boardArray[4][4].setID("DOUBLE WORD SCORE");

		// Southeast
		_boardArray[10][10].setID("DOUBLE WORD SCORE");
		_boardArray[11][11].setID("DOUBLE WORD SCORE");
		_boardArray[12][12].setID("DOUBLE WORD SCORE");
		_boardArray[13][13].setID("DOUBLE WORD SCORE");

		// Southwest
		_boardArray[1][13].setID("DOUBLE WORD SCORE");
		_boardArray[2][12].setID("DOUBLE WORD SCORE");
		_boardArray[3][11].setID("DOUBLE WORD SCORE");
		_boardArray[4][10].setID("DOUBLE WORD SCORE");

		// Northeast
		_boardArray[13][1].setID("DOUBLE WORD SCORE");
		_boardArray[12][2].setID("DOUBLE WORD SCORE");
		_boardArray[11][3].setID("DOUBLE WORD SCORE");
		_boardArray[10][4].setID("DOUBLE WORD SCORE");

		_doubleWordScores.add(_boardArray[1][1]);
		_doubleWordScores.add(_boardArray[2][2]);
		_doubleWordScores.add(_boardArray[3][3]);
		_doubleWordScores.add(_boardArray[4][4]);

		_doubleWordScores.add(_boardArray[10][10]);
		_doubleWordScores.add(_boardArray[11][11]);
		_doubleWordScores.add(_boardArray[12][12]);
		_doubleWordScores.add(_boardArray[13][13]);

		_doubleWordScores.add(_boardArray[1][13]);
		_doubleWordScores.add(_boardArray[2][12]);
		_doubleWordScores.add(_boardArray[3][11]);
		_doubleWordScores.add(_boardArray[4][10]);

		_doubleWordScores.add(_boardArray[13][1]);
		_doubleWordScores.add(_boardArray[12][2]);
		_doubleWordScores.add(_boardArray[11][3]);
		_doubleWordScores.add(_boardArray[10][4]);

		// TRIPLE LETTER SCORES (GREEN)

		// Central square
		_boardArray[5][5].setID("TRIPLE LETTER SCORE");
		_boardArray[5][9].setID("TRIPLE LETTER SCORE");
		_boardArray[9][5].setID("TRIPLE LETTER SCORE");
		_boardArray[9][9].setID("TRIPLE LETTER SCORE");

		// Horizontal Outer Solos
		_boardArray[1][5].setID("TRIPLE LETTER SCORE");
		_boardArray[1][9].setID("TRIPLE LETTER SCORE");
		_boardArray[13][5].setID("TRIPLE LETTER SCORE");
		_boardArray[13][9].setID("TRIPLE LETTER SCORE");

		// Vertical Outer Solos
		_boardArray[5][1].setID("TRIPLE LETTER SCORE");
		_boardArray[9][1].setID("TRIPLE LETTER SCORE");
		_boardArray[5][13].setID("TRIPLE LETTER SCORE");
		_boardArray[9][13].setID("TRIPLE LETTER SCORE");

		_tripleLetterScores.add(_boardArray[5][5]);
		_tripleLetterScores.add(_boardArray[5][9]);
		_tripleLetterScores.add(_boardArray[9][5]);
		_tripleLetterScores.add(_boardArray[9][9]);

		_tripleLetterScores.add(_boardArray[1][5]);
		_tripleLetterScores.add(_boardArray[1][9]);
		_tripleLetterScores.add(_boardArray[13][5]);
		_tripleLetterScores.add(_boardArray[13][9]);

		_tripleLetterScores.add(_boardArray[5][1]);
		_tripleLetterScores.add(_boardArray[9][1]);
		_tripleLetterScores.add(_boardArray[5][13]);
		_tripleLetterScores.add(_boardArray[9][13]);

		// TRIPLE WORD SCORES (ORANGE)

		// Middle
		_boardArray[7][7].setID("DOUBLE WORD SCORE");
		// _boardArray[7][7].removeLabel();
		_diamondViewer = new ImageView(new Image("Images/Main Theme and GUI/diamond.png"));
		_diamondViewer.setCache(true);
		_diamondViewer.setPreserveRatio(true);
		_diamondViewer.setFitWidth(Constants.GRID_FACTOR - 2 * Constants.TILE_PADDING);
		_diamondViewer.setLayoutX(20 * Constants.GRID_FACTOR + 4);
		_diamondViewer.setLayoutY(10 * Constants.GRID_FACTOR + 13);
		_labelPane.getChildren().add(_diamondViewer);
		int xLayout = Constants.X7 * Constants.GRID_FACTOR;
		int yLayout = Constants.Y7 * Constants.GRID_FACTOR;
		BoardSquare ghostSquare = new BoardSquare(xLayout, yLayout, "NORMAL", _boardPane, _labelPane);
		ghostSquare.setID("GHOST");
		ghostSquare.setUpHoverResponse(this);

		// Corners
		_boardArray[0][0].setID("TRIPLE WORD SCORE");
		_boardArray[14][14].setID("TRIPLE WORD SCORE");
		_boardArray[14][0].setID("TRIPLE WORD SCORE");
		_boardArray[0][14].setID("TRIPLE WORD SCORE");

		// Edge midpoints
		_boardArray[7][0].setID("TRIPLE WORD SCORE");
		_boardArray[7][14].setID("TRIPLE WORD SCORE");
		_boardArray[0][7].setID("TRIPLE WORD SCORE");
		_boardArray[14][7].setID("TRIPLE WORD SCORE");

		_tripleWordScores.add(_boardArray[7][7]);

		_tripleWordScores.add(_boardArray[0][0]);
		_tripleWordScores.add(_boardArray[14][14]);
		_tripleWordScores.add(_boardArray[14][0]);
		_tripleWordScores.add(_boardArray[0][14]);

		_tripleWordScores.add(_boardArray[7][0]);
		_tripleWordScores.add(_boardArray[7][14]);
		_tripleWordScores.add(_boardArray[0][7]);
		_tripleWordScores.add(_boardArray[14][7]);

		for (int i = 0; i < _doubleLetterScores.size(); i++) {
			_specialSquares.add(_doubleLetterScores.get(i));
		}

		for (int i = 0; i < _doubleWordScores.size(); i++) {
			_specialSquares.add(_doubleWordScores.get(i));
		}

		for (int i = 0; i < _tripleLetterScores.size(); i++) {
			_specialSquares.add(_tripleLetterScores.get(i));
		}

		for (int i = 0; i < _tripleWordScores.size(); i++) {
			_specialSquares.add(_tripleWordScores.get(i));
		}
		this.setUpHovers();
	}

	private void setUpHovers() {
		for (int i = 0; i < _specialSquares.size(); i++) {
			_specialSquares.get(i).setUpHoverResponse(this);
		}
	}

	void fadeOutOtherSquares(String id) {
		if (_organizer.getDisplayMultipliers() == false) {
			if (id == "GHOST") {
				for (int i = 0; i < _specialSquares.size(); i++) {
					_specialSquares.get(i).showText();
				}
			} else {
				for (int i = 0; i < _specialSquares.size(); i++) {
					BoardSquare thisSquare = _specialSquares.get(i);
					if (thisSquare.getID() != id) {
						Color fromColor = null;
						if (thisSquare.getID() == "DOUBLE LETTER SCORE") {
							fromColor = Constants.DOUBLE_LETTER_SCORE;
						} else if (thisSquare.getID() == "DOUBLE WORD SCORE") {
							fromColor = Constants.DOUBLE_WORD_SCORE;
						} else if (thisSquare.getID() == "TRIPLE LETTER SCORE") {
							fromColor = Constants.TRIPLE_LETTER_SCORE;
						} else if (thisSquare.getID() == "TRIPLE WORD SCORE") {
							fromColor = Constants.TRIPLE_WORD_SCORE;
						}
						FillTransition fadeWhite = new FillTransition(Duration.seconds(Constants.LABEL_ANIMATION),
								thisSquare.getSquare(), fromColor, Constants.BOARD_FILL);
						fadeWhite.play();
					} else {
						thisSquare.showText();
					}
				}
			}
		}
	}

	void addDiamond() {
		if (!_labelPane.getChildren().contains(_diamondViewer)) {
			_labelPane.getChildren().add(_diamondViewer);
		}
		FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _diamondViewer);
		fadeDiamond.setFromValue(0.0);
		fadeDiamond.setToValue(1.0);
		fadeDiamond.play();
	}

	void removeDiamond() {
		FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _diamondViewer);
		fadeDiamond.setFromValue(1.0);
		fadeDiamond.setToValue(0.0);
		fadeDiamond.play();
		fadeDiamond.setOnFinished(new RemoveDiamondHandler());
	}

	private class RemoveDiamondHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_labelPane.getChildren().remove(_diamondViewer);
			event.consume();
		}

	}

	void fadeInOtherSquares(String id) {
		if (_organizer.getDisplayMultipliers() == false) {
			if (id == "GHOST") {
				for (int i = 0; i < _specialSquares.size(); i++) {
					_specialSquares.get(i).concealText();
				}
			} else {
				for (int i = 0; i < _specialSquares.size(); i++) {
					BoardSquare thisSquare = _specialSquares.get(i);
					if (thisSquare.getID() != id) {
						Color toColor = null;
						if (thisSquare.getID() == "DOUBLE LETTER SCORE") {
							toColor = Constants.DOUBLE_LETTER_SCORE;
						} else if (thisSquare.getID() == "DOUBLE WORD SCORE") {
							toColor = Constants.DOUBLE_WORD_SCORE;
						} else if (thisSquare.getID() == "TRIPLE LETTER SCORE") {
							toColor = Constants.TRIPLE_LETTER_SCORE;
						} else if (thisSquare.getID() == "TRIPLE WORD SCORE") {
							toColor = Constants.TRIPLE_WORD_SCORE;
						}
						FillTransition restoreColors = new FillTransition(Duration.seconds(Constants.LABEL_ANIMATION),
								thisSquare.getSquare(), Constants.BOARD_FILL, toColor);
						restoreColors.play();
					} else {
						thisSquare.concealText();
					}
					if (id != "TRIPLE WORD SCORE") {
						FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION),
								_diamondViewer);
						fadeDiamond.setFromValue(0.0);
						fadeDiamond.setToValue(1.0);
						fadeDiamond.play();
					}
				}
			}
		}
	}

	private void setUpTiles() {
		for (int i = 0; i < 7; i++) {
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, Constants.COLLECTION_ONE_HORIZONTAL_OFFSET, i + Constants.COLLECTION_VERTICAL_OFFSET,
					this, PlayerNumber.One);
			_playerOneRack.add(tile);
			tile.getTileViewer().setOpacity(0);
		}
		for (int i = 0; i < 7; i++) {
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, Constants.COLLECTION_TWO_HORIZONTAL_OFFSET, i + Constants.COLLECTION_VERTICAL_OFFSET,
					this, PlayerNumber.Two);
			_playerTwoRack.add(tile);
			tile.getTileViewer().setOpacity(0);
		}
		// _tileBag.printSize();
	}

	void refillRackOne() {
		int i = 0;
		while (_playerOneRack.size() < 7 && !_tileBag.isEmpty()) {
			i++;
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, Constants.COLLECTION_ONE_HORIZONTAL_OFFSET,
					_playerOneRack.size() + Constants.COLLECTION_VERTICAL_OFFSET, this, PlayerNumber.One);
			_playerOneRack.add(tile);
			tile.hide();
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION),
					tile.getTileViewer());
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.setAutoReverse(false);
			fadeIn.setCycleCount(1);
			PauseTransition delayFade = new PauseTransition();
			delayFade.setDuration(Duration.seconds(Constants.DRAW_INTERVAL * i));
			delayFade.setOnFinished(new PlayFadeHandler(fadeIn));
			delayFade.play();
		}
		if (_tileBag.isEmpty()) {
			_organizer.removeBag();
		}
	}
	
	void refillRackTwo() {
		int i = 0;
		while (_playerTwoRack.size() < 7 && _tileBag.isEmpty() != true) {
			i++;
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, Constants.COLLECTION_TWO_HORIZONTAL_OFFSET,
					_playerTwoRack.size() + Constants.COLLECTION_VERTICAL_OFFSET, this, PlayerNumber.Two);
			_playerTwoRack.add(tile);
			tile.hide();
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION),
					tile.getTileViewer());
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.setAutoReverse(false);
			fadeIn.setCycleCount(1);
			PauseTransition delayFade = new PauseTransition();
			delayFade.setDuration(Duration.seconds(Constants.DRAW_INTERVAL * i));
			delayFade.setOnFinished(new PlayFadeHandler(fadeIn));
			delayFade.play();
		}
		if (_tileBag.isEmpty()) {
			_organizer.removeBag();
		}
	}

	private class PlayFadeHandler implements EventHandler<ActionEvent> {
		private FadeTransition _fade;

		PlayFadeHandler(FadeTransition fade) {
			_fade = fade;
		}

		@Override
		public void handle(ActionEvent event) {
			_fade.play();
			event.consume();
		}

	}

	private void setUpDictionary() {
		InputStream dictionaryText = this.getClass().getResourceAsStream("dictionary.txt");
		Scanner scanner = new Scanner(dictionaryText);
		while (scanner.hasNext()) _dictionary.add(scanner.next().toUpperCase());
	}

	String tilesToString(ArrayList<Tile> playerRack) {
		return playerRack.stream().map(Tile::getLetter).collect(Collectors.joining());
	}

	Boolean tilesToDictBool(ArrayList<Tile> tiles) {
		Boolean status = false;
		String word = this.tilesToString(tiles);
		if (_dictionary.contains(word)) {
			status = true;
		}
		return status;
	}

	ArrayList<String> validPermutationsOf(String word) {
		ArrayList<String> validPermutations = new ArrayList<>();
		if (word.isEmpty()) return validPermutations;

		ArrayList<Integer> indices =  new ArrayList<>();
		for (int i = 0; i < word.length(); i++) { indices.add(i); }

		permutationHelper("", word, indices, validPermutations);
		return validPermutations;
	}

	private void permutationHelper(String accumulated, String word, ArrayList<Integer> indices, ArrayList<String> collector) {
		if (indices.isEmpty()) return;

		for (int i = 0; i < indices.size(); i++) {
			String concat = accumulated + String.valueOf(word.charAt(indices.get(i)));

			if (_dictionary.contains(concat) && !collector.contains(concat))
				collector.add(concat);

			ArrayList<Integer> remainder = new ArrayList<>(indices);
			remainder.remove(i);

			permutationHelper(concat, word, remainder, collector);
		}
	}

	void collectPrefixes(String word, ArrayList<String> validWords, int permutationCap, HashMap<Integer, ArrayList<String>> invalidCross) {
		//_permutations = 0;
		for (int i = 0; i < word.length(); i++) {
			String oneLetter = String.valueOf(word.charAt(i));
			boolean crossOne = false;
			if (invalidCross.size() > 0) {
				crossOne = invalidCross.get(1).contains(oneLetter);
			}
			if (crossOne == false && !validWords.contains(oneLetter)) {
				validWords.add(oneLetter);
			}
			if (word.length() > 1 && permutationCap > 1 && crossOne == false) {
				
				for (int j = 0; j < word.length(); j++) {
					if (j != i) {
						String twoLetter = String.valueOf(word.charAt(j)) + oneLetter;
						boolean crossTwo = false;
						if (invalidCross.size() > 1) {
							crossTwo = invalidCross.get(2).contains(String.valueOf(word.charAt(j)));
						}
						if (crossTwo == false && !validWords.contains(twoLetter)) {
							validWords.add(twoLetter);
						}
						if (word.length() > 2 && permutationCap > 2 && crossTwo == false) {
							
							for (int k = 0; k < word.length(); k++) {
								if (k != i && k != j) {
									String threeLetter = String.valueOf(word.charAt(k)) + twoLetter;
									boolean crossThree = false;
									if (invalidCross.size() > 2) {
										crossThree = invalidCross.get(3).contains(String.valueOf(word.charAt(k)));
									}
									if (crossThree == false && !validWords.contains(threeLetter)) {
										validWords.add(threeLetter);
									}
									if (word.length() > 3 && permutationCap > 3 && crossThree == false) {
										
										for (int l = 0; l < word.length(); l++) {
											if (l != i && l != j && l != k) {
												String fourLetter = String.valueOf(word.charAt(l)) + threeLetter;
												boolean crossFour = false;
												if (invalidCross.size() > 3) {
													crossFour = invalidCross.get(4).contains(String.valueOf(word.charAt(l)));
												}
												if (crossFour == false && !validWords.contains(fourLetter)) {
													validWords.add(fourLetter);
												}
												if (word.length() > 4 && permutationCap > 4 && crossFour == false) {
													
													for (int m = 0; m < word.length(); m++) {
														if (m != i && m != j && m != k && m != l) {
															String fiveLetter = String.valueOf(word.charAt(m)) + fourLetter;
															boolean crossFive = false;
															if (invalidCross.size() > 4) {
																crossFive = invalidCross.get(5).contains(String.valueOf(word.charAt(m)));
															}
															if (crossFive == false && !validWords.contains(fiveLetter)) {
																validWords.add(fiveLetter);
															}
															if (word.length() > 5 && permutationCap > 5 && crossFive == false) {
																
																for (int n = 0; n < word.length(); n++) {
																	if (n != i && n != j && n != k && n != l && n != m) {
																		String sixLetter = String.valueOf(word.charAt(n)) + fiveLetter;
																		boolean crossSix = false;
																		if (invalidCross.size() > 5) {
																			crossSix = invalidCross.get(6).contains(String.valueOf(word.charAt(n)));
																		}
																		if (crossSix == false && !validWords.contains(sixLetter)) {
																			validWords.add(sixLetter);
																		}
																		if (word.length() > 6 && permutationCap > 6 && crossSix == false) {
																			
																			for (int o = 0; o < word.length(); o++) {
																				if (o != i && o != j && o != k && o != l && o != m && o != n) {
																					String sevenLetter = String.valueOf(word.charAt(o)) + sixLetter;
																					boolean crossSeven = false;
																					if (invalidCross.size() > 6) {
																						crossSeven = invalidCross.get(7).contains(String.valueOf(word.charAt(o)));
																					}
																					if (crossSeven == false && !validWords.contains(sevenLetter)) {
																						validWords.add(sevenLetter);
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	
	void collectSuffixes(String word, ArrayList<String> validWords, int permutationCap, HashMap<Integer, ArrayList<String>> invalidCross) {
		//_permutations = 0;
		for (int i = 0; i < word.length(); i++) {
			String oneLetter = String.valueOf(word.charAt(i));
			boolean crossOne = false;
			if (invalidCross.size() > 0) {
				crossOne = invalidCross.get(1).contains(oneLetter);
			}
			if (crossOne == false && !validWords.contains(oneLetter)) {
				validWords.add(oneLetter);
			}
			if (word.length() > 1 && permutationCap > 1 && crossOne == false) {
				
				for (int j = 0; j < word.length(); j++) {
					if (j != i) {
						String twoLetter = oneLetter + String.valueOf(word.charAt(j));
						boolean crossTwo = false;
						if (invalidCross.size() > 1) {
							crossTwo = invalidCross.get(2).contains(String.valueOf(word.charAt(j)));
						}
						if (crossTwo == false && !validWords.contains(twoLetter)) {
							validWords.add(twoLetter);
						}
						if (word.length() > 2 && permutationCap > 2 && crossTwo == false) {
							
							for (int k = 0; k < word.length(); k++) {
								if (k != i && k != j) {
									String threeLetter = twoLetter + String.valueOf(word.charAt(k));
									boolean crossThree = false;
									if (invalidCross.size() > 2) {
										crossThree = invalidCross.get(3).contains(String.valueOf(word.charAt(k)));
									}
									if (crossThree == false && !validWords.contains(threeLetter)) {
										validWords.add(threeLetter);
									}
									if (word.length() > 3 && permutationCap > 3 && crossThree == false) {
										
										for (int l = 0; l < word.length(); l++) {
											if (l != i && l != j && l != k) {
												String fourLetter = threeLetter + String.valueOf(word.charAt(l));
												boolean crossFour = false;
												if (invalidCross.size() > 3) {
													crossFour = invalidCross.get(4).contains(String.valueOf(word.charAt(l)));
												}
												if (crossFour == false && !validWords.contains(fourLetter)) {
													validWords.add(fourLetter);
												}
												if (word.length() > 4 && permutationCap > 4 && crossFour == false) {
													
													for (int m = 0; m < word.length(); m++) {
														if (m != i && m != j && m != k && m != l) {
															String fiveLetter = fourLetter + String.valueOf(word.charAt(m));
															boolean crossFive = false;
															if (invalidCross.size() > 4) {
																crossFive = invalidCross.get(5).contains(String.valueOf(word.charAt(m)));
															}
															if (crossFive == false && !validWords.contains(fiveLetter)) {
																validWords.add(fiveLetter);
															}
															if (word.length() > 5 && permutationCap > 5 && crossFive == false) {
																
																for (int n = 0; n < word.length(); n++) {
																	if (n != i && n != j && n != k && n != l && n != m) {
																		String sixLetter = fiveLetter + String.valueOf(word.charAt(n));
																		boolean crossSix = false;
																		if (invalidCross.size() > 5) {
																			crossSix = invalidCross.get(6).contains(String.valueOf(word.charAt(n)));
																		}
																		if (crossSix == false && !validWords.contains(sixLetter)) {
																			validWords.add(sixLetter);
																		}
																		if (word.length() > 6 && permutationCap > 6 && crossSix == false) {
																			
																			for (int o = 0; o < word.length(); o++) {
																				if (o != i && o != j && o != k && o != l && o != m && o != n) {
																					String sevenLetter = sixLetter + String.valueOf(word.charAt(o));
																					boolean crossSeven = false;
																					if (invalidCross.size() > 6) {
																						crossSeven = invalidCross.get(7).contains(String.valueOf(word.charAt(o)));
																					}
																					if (crossSeven == false && !validWords.contains(sevenLetter)) {
																						validWords.add(sevenLetter);
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

    // *** @AI ***
	ArrayList<Tile> transferTilesFromRack(String word, ArrayList<Tile> rack) {
		ArrayList<Tile> result = new ArrayList<>();
		for (int i = 0; i < word.length(); i++) {
			for (int j = 0; j < rack.size(); j++) {
				Tile thisTile = rack.get(j);
				String thisWordLetter = String.valueOf(word.charAt(i));
				String thisTileLetter = thisTile.getLetter();
//				//system.out.printf("Checking word letter %s against tile letter %s\n", thisWordLetter, thisTileLetter);
				if (thisWordLetter.equals(thisTileLetter)) {
//					//system.out.printf("Yay! A %s / %s tile added to transfers\n", thisTileLetter, thisWordLetter);
					result.add(thisTile);
					rack.remove(thisTile);
					break;
				}
			}
		}
		return result;
	}
	
	public PaneOrganizer getOrganizer() {
		return _organizer;
	}

	void printBagSize() {
		_tileBag.printSize();
	}

	int getWordValue(ArrayList<Tile> tiles) {
		int isOnADoubleWordScore = 1;
		int isOnATripleWordScore = 1;
		int wordValue = 0;
		for (int i = 0; i < tiles.size(); i++) {
			Tile thisTile = tiles.get(i);
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();
			int letterValue = 0;
			BoardSquare thisSquare = _boardArray[x][y];
			if (thisSquare.is2W()) {
				if (thisSquare.isAlreadyPlayed() == false) {
					isOnADoubleWordScore *= 2;
					//system.out.println("IS ON A DOUBLE WORD SCORE");
				}
				letterValue = thisTile.getValue();
			} else if (thisSquare.is3W()) {
				if (thisSquare.isAlreadyPlayed() == false) {
					//system.out.println("IS ON A TRIPLE WORD SCORE");
					isOnATripleWordScore *= 3;
				}
				letterValue = thisTile.getValue();
			} else if (thisSquare.isNormal()) {
				//system.out.println("IS NORMAL");
				letterValue = thisTile.getValue();
			} else if (thisSquare.is2L()) {
				if (thisSquare.isAlreadyPlayed() == false) {
					//system.out.println("IS ON A DOUBLE LETTER SCORE");
					letterValue = thisTile.getValue() * 2;
				} else {
					letterValue = thisTile.getValue();
				}
			} else if (thisSquare.is3L()) {
				if (thisSquare.isAlreadyPlayed() == false) {
					//system.out.println("IS ON A TRIPLE LETTER SCORE");
					letterValue = thisTile.getValue() * 3;
				} else {
					letterValue = thisTile.getValue();
				}
			}
			wordValue = wordValue + letterValue;
		}
		wordValue = wordValue * isOnADoubleWordScore * isOnATripleWordScore;
		return wordValue;
	}

	void updateAlreadyPlayed() {
		for (int i = 0; i < _tilesOnBoard.size(); i++) {
			Tile thisTile = _tilesOnBoard.get(i);
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();
			_boardArray[x][y].setAlreadyPlayed(true);
		}
	}

	void addTileToBoardArrayAt(Tile tile, int x, int y) {
		if (this.isBetween(0, x, y, 14)) {
			_tileArray[x][y] = tile;
		} else {
			//system.out.printf("Cannot add %s tile at %s, %s\n", tile.getLetter(), x, y);
		}
	}

	ArrayList<Tile> getTilesOnBoard() {
		return _tilesOnBoard;
	}

	Boolean boardSquareOccupiedAt(int x, int y) {
		if (this.isBetween(0, x, y, 14)) {
			if (_tileArray[x][y] != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private Boolean isBetween(int lowerBounds, int check, int check2, int upperBounds) {
		Boolean result = false;
		if (check >= lowerBounds && check <= upperBounds && check2 >= lowerBounds && check2 <= upperBounds) {
			result = true;
		}
		return result;
	}

	Tile getTileFromArrayAt(int x, int y) {
		Tile selection = _tileArray[x][y];
		return selection;
	}

	Tile[][] getTileArray() {
		return _tileArray;
	}

	public void animateWords() {
		if (_playerOneBest != null) {
			for (int i = 0; i < _playerOneBest.getTiles().size(); i++) {
				Tile thisTile = _playerOneBest.getTiles().get(i);
				FadeTransition fadeOut = new FadeTransition(Duration.seconds(Constants.FADE_OUT_DURATION),
						thisTile.getTileViewer());
				fadeOut.setFromValue(1.0);
				fadeOut.setToValue(0.0);
				FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.FADE_IN_DURATION),
						thisTile.getTileViewer());
				fadeIn.setFromValue(0.0);
				fadeIn.setToValue(1.0);
				_fadeOuts.add(fadeOut);
				_fadeIns.add(fadeIn);
			}
		}
		if (_playerTwoBest != null) {
			for (int i = 0; i < _playerTwoBest.getTiles().size(); i++) {
				Tile thisTile = _playerTwoBest.getTiles().get(i);
				FadeTransition fadeOut = new FadeTransition(Duration.seconds(Constants.FADE_OUT_DURATION),
						thisTile.getTileViewer());
				fadeOut.setFromValue(1.0);
				fadeOut.setToValue(0.0);
				FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.FADE_IN_DURATION),
						thisTile.getTileViewer());
				fadeIn.setFromValue(0.0);
				fadeIn.setToValue(1.0);
				_fadeOuts.add(fadeOut);
				_fadeIns.add(fadeIn);
			}
		}
	}

//	public void addWordsToBoard() {
//		if (_playerOneBest != null) {
//			for (int i = 0; i < _playerOneBest.getTiles().size(); i++) {
//				Tile thisTile = _playerOneBest.getTiles().get(i);
//				thisTile.placeAtSquare(i, 0);
//				thisTile.addTo(_tileArray);
//				thisTile.addTo(_tilesOnBoard);
//			}
//		}
//		if (_playerTwoBest != null) {
//			for (int i = 0; i < _playerTwoBest.getTiles().size(); i++) {
//				Tile thisTile = _playerTwoBest.getTiles().get(i);
//				thisTile.placeAtSquare(i, 1);
//				thisTile.addTo(_tileArray);
//				thisTile.addTo(_tilesOnBoard);
//			}
//		}
//	}
	
	void aiSequence(int func, String id) {
		switch (func) {
			case 1:
				_organizer.fadeInAI();
				break;
			case 2:
				_organizer.showAIOutcome(id);
				break;
			case 3:
				_organizer.fadeOutAI();
				break;
		}
	}

	public void setEnterable(Boolean status) {
		_organizer.setEnterable(status);
	}

	public void hideTiles() {
		for (int i = 0; i < _tilesOnBoard.size(); i++) {
			_tilesOnBoard.get(i).hide();
		}
	}

	void shiftTiles(String id) {
		if (id == "PLAYER ONE" && _playerOneRack.size() > 0) {
			for (int i = 0; i < _playerOneRack.size(); i++) {
				Tile thisTile = _playerOneRack.get(i);
				thisTile.setLoc(Constants.COLLECTION_ONE_HORIZONTAL_OFFSET, i + Constants.COLLECTION_VERTICAL_OFFSET);
			}
		} else if (id == "PLAYER TWO" && _playerTwoRack.size() > 0) {
			for (int i = 0; i < _playerTwoRack.size(); i++) {
				Tile thisTile = _playerTwoRack.get(i);
				thisTile.setLoc(Constants.COLLECTION_TWO_HORIZONTAL_OFFSET, i + Constants.COLLECTION_VERTICAL_OFFSET);
			}
		}
	}

	public void playFadeOuts() {
		for (int i = 0; i < _fadeOuts.size(); i++) {
			_fadeOuts.get(i).play();
		}
	}

	public void playFadeIns() {
		for (int i = 0; i < _fadeIns.size(); i++) {
			_fadeIns.get(i).play();
		}
	}

	// *** @AI ***
	public void printValues() {
		if (_playerOneBest != null) {
			//system.out.printf("%s yields %s points\n", _playerOneBest.getLetters(), _playerOneBest.getValue());
		}
		if (_playerTwoBest != null) {
			//system.out.printf("%s yields %s points\n", _playerTwoBest.getLetters(), _playerTwoBest.getValue());
		}
	}

	void collectAdjacents(String id, Tile thisTile, ArrayList<Tile> tiles) {
		int x = thisTile.getXIndex();
		int y = thisTile.getYIndex();
		int i = 1;
		if (!tiles.contains(thisTile)) {
			tiles.add(thisTile);
		}
		// //system.out.printf("Added %s tile to %s cross\n", thisTile.getLetter(), id);
		if (id == "HORIZONTALLY") {
			while (x - i >= 0 && _tileArray[x - i][y] != null && _tileArray[x - i][y].isAddedToBoard()) {
				// //system.out.printf("Adding %s tile to horizontal cross\n", _tileArray[x -
				// i][y].getLetter());
				if (!tiles.contains(_tileArray[x - i][y])) {
					tiles.add(_tileArray[x - i][y]);
				}
				i++;
			}
			i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null && _tileArray[x + i][y].isAddedToBoard()) {
				// //system.out.printf("Adding %s tile to horizontal cross\n", _tileArray[x +
				// i][y].getLetter());
				if (!tiles.contains(_tileArray[x + i][y])) {
					tiles.add(_tileArray[x + i][y]);
				}
				i++;
			}
		} else if (id == "VERTICALLY") {
			i = 1;
			while (y - i >= 0 && _tileArray[x][y - i] != null && _tileArray[x][y - i].isAddedToBoard()) {
				// //system.out.printf("Adding %s tile to vertical cross\n", _tileArray[x][y -
				// i].getLetter());
				if (!tiles.contains(_tileArray[x][y - i])) {
					tiles.add(_tileArray[x][y - i]);
				}
				i++;
			}
			i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null && _tileArray[x][y + i].isAddedToBoard()) {
				// //system.out.printf("Adding %s tile to vertical cross\n", _tileArray[x][y +
				// i].getLetter());
				if (!tiles.contains(_tileArray[x][y + i])) {
					tiles.add(_tileArray[x][y + i]);
				}
				i++;
			}
		}
	}

	void resetEnterInt() {
		_organizer.resetEnterInt();
	}

	boolean dictionaryContains(String string) {
		return _dictionary.contains(string);
	}

	int getValueFromString(String validWord, int firstXIndex, int firstYIndex, String id) {
		int wordValue = 0;
		int x = 0;
		int y = 0;
		int isOnADoubleWordScore = 1;
		int isOnATripleWordScore = 1;
		for (int i = 0; i < validWord.length(); i++) {
			String currentLetter = String.valueOf(validWord.charAt(i));
			int letterValue = Constants.VALUES.get(currentLetter);
			if (id == "HORIZONTAL") {
				x = firstXIndex + i;
				y = firstYIndex;
			} else if (id == "VERTICAL") {
				x = firstXIndex;
				y = firstYIndex + i;
			}
			if (this.isBetween(0, x, y, 14)) {
				BoardSquare thisSquare = _boardArray[x][y];
				if (thisSquare.is2W() && thisSquare.isAlreadyPlayed() == false) {
					isOnADoubleWordScore = isOnADoubleWordScore * 2;
				} else if (thisSquare.is3W() && thisSquare.isAlreadyPlayed() == false) {
					isOnATripleWordScore = isOnATripleWordScore * 3;
				} else if (thisSquare.isNormal()) {
				} else if (thisSquare.is2L() && thisSquare.isAlreadyPlayed() == false) {
					letterValue = letterValue * 2;
				} else if (thisSquare.is3L() && thisSquare.isAlreadyPlayed() == false) {
					letterValue = letterValue * 3;
				}
			} else {
				return 0;
			}
			wordValue = wordValue + letterValue;
		}
		wordValue = wordValue * isOnADoubleWordScore * isOnATripleWordScore;
		return wordValue;
	}
	
	int getNumPrefixSlots(int x, int y, String direction) {
		if (direction == "VERTICAL") {
			int numPrefixSlots = 1;
			int i = 1;
			while (y - i >= 0 && _tileArray[x][y - i] == null) {
				numPrefixSlots = numPrefixSlots + 1;
				i = i + 1;
			}
			return numPrefixSlots;
		} else if (direction == "HORIZONTAL") {
			int numPrefixSlots = 1;
			int i = 1;
			while (x - i >= 0 && _tileArray[x - i][y] == null) {
				numPrefixSlots = numPrefixSlots + 1;
				i = i + 1;
			}
			return numPrefixSlots;
		} else {
			return 0;
		}
	}
	
	int getNumSuffixSlots(int x, int y, String direction) {
		if (direction == "VERTICAL") {
			if (y > 14) {
				return 0;
			}
			int numSuffixSlots = 1;
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] == null) {
				numSuffixSlots = numSuffixSlots + 1;
				i = i + 1;
			}
			return numSuffixSlots;
		} else if (direction == "HORIZONTAL") {
			if (x > 14) {
				return 0;
			}
			int numSuffixSlots = 1;
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] == null) {
				numSuffixSlots = numSuffixSlots + 1;
				i = i + 1;
			}
			return numSuffixSlots;
		} else {
			return 0;
		}
	}
	
	ArrayList<Tile> getKernelTiles(int x, int y, String direction) {
		if (direction == "VERTICAL") {
			ArrayList<Tile> kernelTiles = new ArrayList<>();
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null) {
				kernelTiles.add(_tileArray[x][y + i]);
				i = i + 1;
			}
			return kernelTiles;
		} else if (direction == "HORIZONTAL") {
			ArrayList<Tile> kernelTiles = new ArrayList<>();
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null) {
				kernelTiles.add(_tileArray[x + i][y]);
				i = i + 1;
			}
			return kernelTiles;
		} else {
			return null;
		}
	}

	String getKernelFor(int x, int y, String direction) {
		if (direction == "VERTICAL") {
			String kernel = "";
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null) {
				kernel = kernel + _tileArray[x][y + i].getLetter();
				i = i + 1;
			}
			return kernel;
		} else if (direction == "HORIZONTAL") {
			String kernel = "";
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null) {
				kernel = kernel + _tileArray[x + i][y].getLetter();
				i = i + 1;
			}
			return kernel;
		} else {
			return "";
		}
	}

	void mapKernelsForRow(int y, HashMap<String, String> precedingKernels, HashMap<String, String> succeedingKernels) {
		System.out.printf("Mapping row %s\n", y);
		HashMap<Integer, String> kernels = new HashMap<>();
		int read = 0;
		int kernelNum = 0;
		String kernel = "";
		kernels.put(kernelNum, kernel);
		for (int x = 0; x <= 14; x++) {
			if (_tileArray[x][y] != null) {
				read = 0;
				kernel = "";
				while (x + read <= 14 && _tileArray[x + read][y] != null) {
					Tile thisTile = _tileArray[x + read][y];
					kernel = kernel + thisTile.getLetter();
					read++;
				}
				kernelNum++;
				kernels.put(kernelNum, kernel);
				x = x + read;
			}
		}
		kernels.put(kernelNum + 1, "");
		for (int i = 0; i < kernels.size(); i++) {
			System.out.printf("Kernel %s = %s\n", i, kernels.get(i));
		}
		if (kernels.size() > 1) {
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i + 1);
				String precedingKernel = kernels.get(i);
				System.out.printf("%s precedes %s\n", precedingKernel, thisKernel);
				precedingKernels.put(thisKernel, precedingKernel);
			}
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i);
				String succeedingKernel = kernels.get(i + 1);
				System.out.printf("%s succeeds %s\n", succeedingKernel, thisKernel);
				succeedingKernels.put(thisKernel, succeedingKernel);
			}
		}
	}

	void mapKernelsForColumn(int x, HashMap<String, String> precedingKernels, HashMap<String, String> succeedingKernels) {
		System.out.printf("Mapping column %s\n", x);
		HashMap<Integer, String> kernels = new HashMap<>();
		int read = 0;
		int kernelNum = 0;
		String kernel = "";
		kernels.put(kernelNum, kernel);
		for (int y = 0; y <= 14; y++) {
			if (_tileArray[x][y] != null) {
				read = 0;
				kernel = "";
				while (y + read <= 14 && _tileArray[x][y + read] != null) {
					kernel = kernel + _tileArray[x][y + read].getLetter();
					read++;
				}
				kernelNum++;
				kernels.put(kernelNum, kernel);
				y = y + read;
			}
		}
		kernels.put(kernelNum + 1, "");
		for (int i = 0; i < kernels.size(); i++) {
			//System.out.printf("Kernel %s = %s\n", i, kernels.get(i));
		}
		if (kernels.size() > 1) {
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i + 1);
				String precedingKernel = kernels.get(i);
				//system.out.printf("%s precedes %s\n", precedingKernel, thisKernel);
				precedingKernels.put(thisKernel, precedingKernel);
			}
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i);
				String succeedingKernel = kernels.get(i + 1);
				//system.out.printf("%s succeeds %s\n", succeedingKernel, thisKernel);
				succeedingKernels.put(thisKernel, succeedingKernel);
			}
		}
	}

	void displayOutcome(String winner) {
		_organizer.moveHi(winner);
	}
	
//	private class ClearHandler implements EventHandler<ActionEvent> {
//
//		@Override
//		public void handle(ActionEvent event) {
//			ScrabbleGame.this.clearPath();
//			event.consume();
//		}
//
//	}
	
	private class BeginPlacementHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			ScrabbleGame.this.placeTiles();
			event.consume();
		}

	}
	
	private class ResetHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (_autoReset) {
				_organizer.reset();
			}
			event.consume();
		}

	}
	
	public void clearPath() {
		int move = 1;
		for (int loc = 1; loc < 14; loc++) {
			move = move * -1;
			for (int j = 0; j < _endGame.size(); j++) {
				Tile thisTile = _endGame.get(j);
				int x = thisTile.getXIndex();
				int y = thisTile.getYIndex();
				if (x == loc && y == loc) {
					if (move == 1) {
						for (int k = 0; k < 15; k++) {
							if (_tileArray[k][y] == null) {
								thisTile.placeAtSquare(k, y);
								break;
							}
						}
					} else if (move == -1) {
						for (int k = 0; k < 15; k++) {
							if (_tileArray[x][k] == null) {
								thisTile.placeAtSquare(x, k);
								break;
							}
						}
					}
				}
			}
		}
		PauseTransition delayPlacement = new PauseTransition(Duration.seconds(2));
		delayPlacement.setOnFinished(new BeginPlacementHandler());
		delayPlacement.play();
	}
	
	private void placeTiles() {
		double delay = 0;
		for (int i = 0; i < _endGame.size(); i++) {
			Tile thisTile = _endGame.get(i);
			delay = delay + Constants.PLACEMENT_DURATION;
			PauseTransition delayPlacement = new PauseTransition(Duration.seconds(delay));
			delayPlacement.setOnFinished(new PlaceHandler(i + 1, i + 1, thisTile));
			delayPlacement.play();
		}
		PauseTransition delayReset = new PauseTransition(Duration.seconds(10));
		delayReset.setOnFinished(new ResetHandler());
		delayReset.play();
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
			_thisTile.getTileViewer().toFront();
			_thisTile.placeAtSquare(_x, _y);
			event.consume();
		}

	}

	void rotateBag() {
		_organizer.rotateBag();
	}

	boolean getAutoReset() {
		return _autoReset;
	}

	void shuffleRack(int id) {
		ArrayList<Tile> rack = null;
		switch (id) {
			case 1:
				rack = _playerOneRack;
				break;
			case 2:
				rack = _playerTwoRack;
				break;
		}
		int size = rack.size();
		int checkSize = 0;
		for (int i = 0; i < rack.size(); i++) {
			if (!rack.get(i).isOnBoard()) {
				checkSize = checkSize + 1;
			}
		}
		if (checkSize == 7) {
			ArrayList<Tile> temp = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				Tile firstTile = null;
				for (int j = 0; j < rack.size(); j++) {
					Tile thisTile = rack.get(j);
					if (j == 0) {
						firstTile = thisTile;
					} else {
						if (thisTile.getY() < firstTile.getY()) {
							firstTile = thisTile;
						}
					}
				}
				temp.add(firstTile);
				rack.remove(firstTile);
//				FadeTransition fade = new FadeTransition(Duration.seconds(0.2), firstTile.getTileViewer());
//				fade.setFromValue(1.0);
//				fade.setToValue(0.0);
//				fade.play();
			}
			rack.addAll(temp);
			for (int i = 0; i < rack.size() - 1; i++) {
				Tile upperTile = rack.get(i);
				Tile lowerTile = rack.get(i + 1);
				int shiftRand = (int) (Math.random() * 3);
				switch (shiftRand) {
					case 0: case 1:
						upperTile.moveDown();
						lowerTile.moveUp();
						rack.set(i + 1, upperTile);
						rack.set(i, lowerTile);
						break;
					case 2:
						break;
				}
			}
//			for (int i = 0; i < rack.size(); i++) {
//				Tile thisTile = rack.get(i);
//				FadeTransition fade = new FadeTransition(Duration.seconds(0.2), thisTile.getTileViewer());
//				fade.setFromValue(0.0);
//				fade.setToValue(1.0);
//				fade.play();
//			}
		}
	}

}