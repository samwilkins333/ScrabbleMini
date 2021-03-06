package Scrabble;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.layout.*;

import java.io.*;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.event.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import static Scrabble.Constants.*;

class ScrabbleGame {
	private final PaneOrganizer _organizer;
	private final Pane _root;
	private final Pane _boardPane;
	private final Pane _labelPane;
	private final TileBag _tileBag;
	private ArrayList<Tile> _playerOneRack;
	private ArrayList<Tile> _playerTwoRack;
	private final HashSet<String> _dictionary;
	private final BoardSquare[][] _boardArray;
	private final ArrayList<BoardSquare> _doubleLetterScores;
	private final ArrayList<BoardSquare> _doubleWordScores;
	private final ArrayList<BoardSquare> _tripleLetterScores;
	private final ArrayList<BoardSquare> _tripleWordScores;
	private final Tile[][] _tileArray;
	private final ArrayList<Tile> _tilesOnBoard;
	private final ArrayList<BoardSquare> _specialSquares;
	private ImageView _diamondViewer;
	private Boolean _gameIsPlaying;
	private Referee _referee;
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

	Pane getRoot() {
		return _root;
	}

	String getRackAsString() {
		return this.tilesToString(_referee.getCurrentPlayer() == PlayerNum.One ? _playerOneRack : _playerTwoRack);
	}
	
	boolean tileBagIsEmpty() {
		return _tileBag.isEmpty();
	}
	
	void setAutoReset(boolean status) {
		_autoReset = status;
	}

	void manageDraw(PlayerNum num) {
		_organizer.manageDraw(num);
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

	void fadeRacks(PlayerNum player, Direction direction) {
		ArrayList<Tile> tileList = player == PlayerNum.One ? _playerOneRack : _playerTwoRack;

		boolean in = direction == Direction.In;
		double from = in ? FADED_TILE_OPACITY : 1.0;
		double to = in ? 1.0 : FADED_TILE_OPACITY;

		for (Tile thisTile : tileList) {
			FadeTransition fade = new FadeTransition(Duration.seconds(0.5), thisTile.getTileViewer());
			fade.setFromValue(from);
			fade.setToValue(to);
			fade.play();
		}
	}

	private void setUpBoard() {
		for (int i = 0; i < _boardArray.length; i++) {
			for (int j = 0; j < _boardArray[1].length; j++) {
				int xLayout = (i + ZEROETH_COLUMN_OFFSET) * GRID_FACTOR;
				int yLayout = (j + ZEROETH_ROW_OFFSET) * GRID_FACTOR;
				BoardSquare boardSquare = new BoardSquare(xLayout, yLayout, _boardPane, _labelPane);
				_boardArray[i][j] = boardSquare;
			}
		}
		_boardPane.getChildren().add(_labelPane);
		this.setUpSpecialBoardSquares();
	}

	void addReferee(Referee referee) {
		_referee = referee;
	}

	Referee getReferee() {
		return _referee;
	}

	Boolean getIsShiftDown() {
		return _organizer.getIsShiftDown();
	}

	void resetRackOne() {
		for (Tile a_playerOneRack : _playerOneRack) a_playerOneRack.reset();
	}

	void resetRackTwo() {
		for (Tile a_playerTwoRack : _playerTwoRack) a_playerTwoRack.reset();
	}

	private void setUpSpecialBoardSquares() {

		// DOUBLE LETTER SCORES (BLUE)

		// Central square
		_boardArray[6][6].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[6][8].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[8][6].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[8][8].setID(SquareIdentity.DoubleLetterScore);

		// Horizontal Outer solos
		_boardArray[3][0].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[11][0].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[3][14].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[11][14].setID(SquareIdentity.DoubleLetterScore);

		// Vertical Outer solos
		_boardArray[0][3].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[0][11].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[14][3].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[14][11].setID(SquareIdentity.DoubleLetterScore);

		// Lower blue trio
		_boardArray[7][11].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[8][12].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[6][12].setID(SquareIdentity.DoubleLetterScore);

		// Left blue trio
		_boardArray[3][7].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[2][6].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[2][8].setID(SquareIdentity.DoubleLetterScore);

		// Right blue trio
		_boardArray[11][7].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[12][6].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[12][8].setID(SquareIdentity.DoubleLetterScore);

		// Upper blue trio
		_boardArray[7][3].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[6][2].setID(SquareIdentity.DoubleLetterScore);
		_boardArray[8][2].setID(SquareIdentity.DoubleLetterScore);

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
		_boardArray[1][1].setID(SquareIdentity.DoubleWordScore);
		_boardArray[2][2].setID(SquareIdentity.DoubleWordScore);
		_boardArray[3][3].setID(SquareIdentity.DoubleWordScore);
		_boardArray[4][4].setID(SquareIdentity.DoubleWordScore);

		// Southeast
		_boardArray[10][10].setID(SquareIdentity.DoubleWordScore);
		_boardArray[11][11].setID(SquareIdentity.DoubleWordScore);
		_boardArray[12][12].setID(SquareIdentity.DoubleWordScore);
		_boardArray[13][13].setID(SquareIdentity.DoubleWordScore);

		// Southwest
		_boardArray[1][13].setID(SquareIdentity.DoubleWordScore);
		_boardArray[2][12].setID(SquareIdentity.DoubleWordScore);
		_boardArray[3][11].setID(SquareIdentity.DoubleWordScore);
		_boardArray[4][10].setID(SquareIdentity.DoubleWordScore);

		// Northeast
		_boardArray[13][1].setID(SquareIdentity.DoubleWordScore);
		_boardArray[12][2].setID(SquareIdentity.DoubleWordScore);
		_boardArray[11][3].setID(SquareIdentity.DoubleWordScore);
		_boardArray[10][4].setID(SquareIdentity.DoubleWordScore);

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
		_boardArray[5][5].setID(SquareIdentity.TripleLetterScore);
		_boardArray[5][9].setID(SquareIdentity.TripleLetterScore);
		_boardArray[9][5].setID(SquareIdentity.TripleLetterScore);
		_boardArray[9][9].setID(SquareIdentity.TripleLetterScore);

		// Horizontal Outer Solos
		_boardArray[1][5].setID(SquareIdentity.TripleLetterScore);
		_boardArray[1][9].setID(SquareIdentity.TripleLetterScore);
		_boardArray[13][5].setID(SquareIdentity.TripleLetterScore);
		_boardArray[13][9].setID(SquareIdentity.TripleLetterScore);

		// Vertical Outer Solos
		_boardArray[5][1].setID(SquareIdentity.TripleLetterScore);
		_boardArray[9][1].setID(SquareIdentity.TripleLetterScore);
		_boardArray[5][13].setID(SquareIdentity.TripleLetterScore);
		_boardArray[9][13].setID(SquareIdentity.TripleLetterScore);

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
		_boardArray[7][7].setID(SquareIdentity.DoubleWordScore);

		// Create and graphically add diamond image view
		_diamondViewer = new ImageView(new Image("Images/Main Theme and GUI/diamond.png"));
		_diamondViewer.setCache(true);
		_diamondViewer.setPreserveRatio(true);
		_diamondViewer.setFitWidth(GRID_FACTOR - 2 * TILE_PADDING);
		_diamondViewer.setLayoutX(20 * GRID_FACTOR + 4);
		_diamondViewer.setLayoutY(10 * GRID_FACTOR + 13);
		_labelPane.getChildren().add(_diamondViewer);

		// Create and add ghost or transparent overlay square for middle of board
		int xLayout = X7 * GRID_FACTOR;
		int yLayout = Y7 * GRID_FACTOR;
		BoardSquare ghostSquare = new BoardSquare(xLayout, yLayout, _boardPane, _labelPane);
		ghostSquare.setID(SquareIdentity.Ghost);
		ghostSquare.setUpHoverResponse(this);

		// Corners
		_boardArray[0][0].setID(SquareIdentity.TripleWordScore);
		_boardArray[14][14].setID(SquareIdentity.TripleWordScore);
		_boardArray[14][0].setID(SquareIdentity.TripleWordScore);
		_boardArray[0][14].setID(SquareIdentity.TripleWordScore);

		// Edge midpoints
		_boardArray[7][0].setID(SquareIdentity.TripleWordScore);
		_boardArray[7][14].setID(SquareIdentity.TripleWordScore);
		_boardArray[0][7].setID(SquareIdentity.TripleWordScore);
		_boardArray[14][7].setID(SquareIdentity.TripleWordScore);

		_tripleWordScores.add(_boardArray[7][7]);

		_tripleWordScores.add(_boardArray[0][0]);
		_tripleWordScores.add(_boardArray[14][14]);
		_tripleWordScores.add(_boardArray[14][0]);
		_tripleWordScores.add(_boardArray[0][14]);

		_tripleWordScores.add(_boardArray[7][0]);
		_tripleWordScores.add(_boardArray[7][14]);
		_tripleWordScores.add(_boardArray[0][7]);
		_tripleWordScores.add(_boardArray[14][7]);

		_specialSquares.addAll(_doubleLetterScores);
		_specialSquares.addAll(_doubleWordScores);
		_specialSquares.addAll(_tripleLetterScores);
		_specialSquares.addAll(_tripleWordScores);

		this.setUpHovers();
	}

	private void setUpHovers() {
		for (BoardSquare _specialSquare : _specialSquares) _specialSquare.setUpHoverResponse(this);
	}

	void fadeInOtherSquares(SquareIdentity identity) {
		if (_organizer.getDisplayMultipliers()) return;

		if (identity.equals(SquareIdentity.Ghost)) {
			for (BoardSquare _specialSquare : _specialSquares) _specialSquare.concealText();
		} else {
			for (BoardSquare thisSquare : _specialSquares) {
				if (thisSquare.getIdentity().equals(identity)) thisSquare.concealText();
				else {
					Color toColor = null;
					switch (thisSquare.getIdentity()) {
						case DoubleLetterScore:
							toColor = DOUBLE_LETTER_SCORE;
							break;
						case TripleLetterScore:
							toColor = TRIPLE_LETTER_SCORE;
							break;
						case DoubleWordScore:
							toColor = DOUBLE_WORD_SCORE;
							break;
						case TripleWordScore:
							toColor = TRIPLE_WORD_SCORE;
							break;
						default:
							break;
					}
					FillTransition restoreColors = new FillTransition(Duration.seconds(LABEL_ANIMATION), thisSquare.getSquare(), BOARD_FILL, toColor);
					restoreColors.play();
					
				}
				if (identity != SquareIdentity.TripleWordScore) {
					FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(LABEL_ANIMATION), _diamondViewer);
					fadeDiamond.setFromValue(0.0);
					fadeDiamond.setToValue(1.0);
					fadeDiamond.play();
				}
			}
		}
	}

	void fadeOutOtherSquares(SquareIdentity identity) {
		if (_organizer.getDisplayMultipliers()) return;

		if (identity.equals(SquareIdentity.Ghost))
			for (BoardSquare _specialSquare : _specialSquares) _specialSquare.showText();
		else {
			for (BoardSquare thisSquare : _specialSquares) {
				if (thisSquare.getIdentity().equals(identity)) thisSquare.showText();
				else {
					Color fromColor = null;
					switch (thisSquare.getIdentity()) {
						default:
							break;
						case DoubleLetterScore:
							fromColor = DOUBLE_LETTER_SCORE;
							break;
						case TripleLetterScore:
							fromColor = TRIPLE_LETTER_SCORE;
							break;
						case DoubleWordScore:
							fromColor = DOUBLE_WORD_SCORE;
							break;
						case TripleWordScore:
							fromColor = TRIPLE_WORD_SCORE;
							break;
					}
					FillTransition fadeWhite = new FillTransition(Duration.seconds(LABEL_ANIMATION), thisSquare.getSquare(), fromColor, BOARD_FILL);
					fadeWhite.play();
				}
			}
		}
	}

	void addDiamond() {
		if (!_labelPane.getChildren().contains(_diamondViewer)) {
			_labelPane.getChildren().add(_diamondViewer);
		}
		FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(LABEL_ANIMATION), _diamondViewer);
		fadeDiamond.setFromValue(0.0);
		fadeDiamond.setToValue(1.0);
		fadeDiamond.play();
	}

	void removeDiamond() {
		FadeTransition fadeDiamond = new FadeTransition(Duration.seconds(LABEL_ANIMATION), _diamondViewer);
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

	private void setUpTiles() {
		for (int i = 0; i < 7; i++) {
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, COLLECTION_ONE_HORIZONTAL_OFFSET, i + COLLECTION_VERTICAL_OFFSET, this, PlayerNum.One);
			_playerOneRack.add(tile);
			tile.getTileViewer().setOpacity(0);
		}
		for (int i = 0; i < 7; i++) {
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, COLLECTION_TWO_HORIZONTAL_OFFSET, i + COLLECTION_VERTICAL_OFFSET, this, PlayerNum.Two);
			_playerTwoRack.add(tile);
			tile.getTileViewer().setOpacity(0);
		}
	}

	void refillRack(PlayerNum num) {
		boolean isPlayerOne = num == PlayerNum.One;
		ArrayList<Tile> rack = isPlayerOne ? _playerOneRack : _playerTwoRack;
		int hOffset = isPlayerOne ? COLLECTION_ONE_HORIZONTAL_OFFSET : COLLECTION_TWO_HORIZONTAL_OFFSET;

		int i = 0;
		while (rack.size() < 7 && !_tileBag.isEmpty()) {
			i++;

			// Generate and add new tile
			Tile tile = _tileBag.draw();
			tile.add(_boardPane, hOffset,rack.size() + COLLECTION_VERTICAL_OFFSET, this, num);
			rack.add(tile);
			tile.hide();

			// Fades in each tile
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), tile.getTileViewer());
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.setAutoReverse(false);
			fadeIn.setCycleCount(1);

			// Creates a staggered refilling
			PauseTransition delayFade = new PauseTransition();
			delayFade.setDuration(Duration.seconds(DRAW_INTERVAL * i));
			delayFade.setOnFinished(new PlayFadeHandler(fadeIn));
			delayFade.play();
		}

		// If all tiles have been drawn, update GUI
		if (_tileBag.isEmpty()) _organizer.removeBag();
	}

	private class PlayFadeHandler implements EventHandler<ActionEvent> {
		private final FadeTransition _fade;

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
		Scanner _scanner = new Scanner(dictionaryText);
		while (_scanner.hasNext()) _dictionary.add(_scanner.next().toUpperCase());
		_scanner.close();
	}

	String tilesToString(ArrayList<Tile> playerRack) {
		StringBuilder resultant = new StringBuilder();
		for (Tile aPlayerRack : playerRack) resultant.append(aPlayerRack.getLetter());
		return resultant.toString();
	}

	Boolean tilesToDictBool(ArrayList<Tile> tiles) {
		boolean status = false;

		String word = this.tilesToString(tiles);
		if (_dictionary.contains(word))
			status = true;

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
		for (int i = 0; i < word.length(); i++) {
			String oneLetter = String.valueOf(word.charAt(i));
			boolean crossOne = false;
			if (invalidCross.size() > 0) {
				crossOne = invalidCross.get(1).contains(oneLetter);
			}
			if (!crossOne && !validWords.contains(oneLetter)) {
				validWords.add(oneLetter);
			}
			if (word.length() > 1 && permutationCap > 1 && !crossOne) {
				
				for (int j = 0; j < word.length(); j++) {
					if (j != i) {
						String twoLetter = String.valueOf(word.charAt(j)) + oneLetter;
						boolean crossTwo = false;
						if (invalidCross.size() > 1) {
							crossTwo = invalidCross.get(2).contains(String.valueOf(word.charAt(j)));
						}
						if (!crossTwo && !validWords.contains(twoLetter)) {
							validWords.add(twoLetter);
						}
						if (word.length() > 2 && permutationCap > 2 && !crossTwo) {
							
							for (int k = 0; k < word.length(); k++) {
								if (k != i && k != j) {
									String threeLetter = String.valueOf(word.charAt(k)) + twoLetter;
									boolean crossThree = false;
									if (invalidCross.size() > 2) {
										crossThree = invalidCross.get(3).contains(String.valueOf(word.charAt(k)));
									}
									if (!crossThree && !validWords.contains(threeLetter)) {
										validWords.add(threeLetter);
									}
									if (word.length() > 3 && permutationCap > 3 && !crossThree) {
										
										for (int l = 0; l < word.length(); l++) {
											if (l != i && l != j && l != k) {
												String fourLetter = String.valueOf(word.charAt(l)) + threeLetter;
												boolean crossFour = false;
												if (invalidCross.size() > 3) {
													crossFour = invalidCross.get(4).contains(String.valueOf(word.charAt(l)));
												}
												if (!crossFour && !validWords.contains(fourLetter)) {
													validWords.add(fourLetter);
												}
												if (word.length() > 4 && permutationCap > 4 && !crossFour) {
													
													for (int m = 0; m < word.length(); m++) {
														if (m != i && m != j && m != k && m != l) {
															String fiveLetter = String.valueOf(word.charAt(m)) + fourLetter;
															boolean crossFive = false;
															if (invalidCross.size() > 4) {
																crossFive = invalidCross.get(5).contains(String.valueOf(word.charAt(m)));
															}
															if (!crossFive && !validWords.contains(fiveLetter)) {
																validWords.add(fiveLetter);
															}
															if (word.length() > 5 && permutationCap > 5 && !crossFive) {
																
																for (int n = 0; n < word.length(); n++) {
																	if (n != i && n != j && n != k && n != l && n != m) {
																		String sixLetter = String.valueOf(word.charAt(n)) + fiveLetter;
																		boolean crossSix = false;
																		if (invalidCross.size() > 5) {
																			crossSix = invalidCross.get(6).contains(String.valueOf(word.charAt(n)));
																		}
																		if (!crossSix && !validWords.contains(sixLetter)) {
																			validWords.add(sixLetter);
																		}
																		if (word.length() > 6 && permutationCap > 6 && !crossSix) {
																			
																			for (int o = 0; o < word.length(); o++) {
																				if (o != i && o != j && o != k && o != l && o != m && o != n) {
																					String sevenLetter = String.valueOf(word.charAt(o)) + sixLetter;
																					boolean crossSeven = false;
																					if (invalidCross.size() > 6) {
																						crossSeven = invalidCross.get(7).contains(String.valueOf(word.charAt(o)));
																					}
																					if (!crossSeven && !validWords.contains(sevenLetter)) {
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
		for (int i = 0; i < word.length(); i++) {
			String oneLetter = String.valueOf(word.charAt(i));
			boolean crossOne = false;
			if (invalidCross.size() > 0) {
				crossOne = invalidCross.get(1).contains(oneLetter);
			}
			if (!crossOne && !validWords.contains(oneLetter)) {
				validWords.add(oneLetter);
			}
			if (word.length() > 1 && permutationCap > 1 && !crossOne) {
				
				for (int j = 0; j < word.length(); j++) {
					if (j != i) {
						String twoLetter = oneLetter + String.valueOf(word.charAt(j));
						boolean crossTwo = false;
						if (invalidCross.size() > 1) {
							crossTwo = invalidCross.get(2).contains(String.valueOf(word.charAt(j)));
						}
						if (!crossTwo && !validWords.contains(twoLetter)) {
							validWords.add(twoLetter);
						}
						if (word.length() > 2 && permutationCap > 2 && !crossTwo) {
							
							for (int k = 0; k < word.length(); k++) {
								if (k != i && k != j) {
									String threeLetter = twoLetter + String.valueOf(word.charAt(k));
									boolean crossThree = false;
									if (invalidCross.size() > 2) {
										crossThree = invalidCross.get(3).contains(String.valueOf(word.charAt(k)));
									}
									if (!crossThree && !validWords.contains(threeLetter)) {
										validWords.add(threeLetter);
									}
									if (word.length() > 3 && permutationCap > 3 && !crossThree) {
										
										for (int l = 0; l < word.length(); l++) {
											if (l != i && l != j && l != k) {
												String fourLetter = threeLetter + String.valueOf(word.charAt(l));
												boolean crossFour = false;
												if (invalidCross.size() > 3) {
													crossFour = invalidCross.get(4).contains(String.valueOf(word.charAt(l)));
												}
												if (!crossFour && !validWords.contains(fourLetter)) {
													validWords.add(fourLetter);
												}
												if (word.length() > 4 && permutationCap > 4 && !crossFour) {
													
													for (int m = 0; m < word.length(); m++) {
														if (m != i && m != j && m != k && m != l) {
															String fiveLetter = fourLetter + String.valueOf(word.charAt(m));
															boolean crossFive = false;
															if (invalidCross.size() > 4) {
																crossFive = invalidCross.get(5).contains(String.valueOf(word.charAt(m)));
															}
															if (!crossFive && !validWords.contains(fiveLetter)) {
																validWords.add(fiveLetter);
															}
															if (word.length() > 5 && permutationCap > 5 && !crossFive) {
																
																for (int n = 0; n < word.length(); n++) {
																	if (n != i && n != j && n != k && n != l && n != m) {
																		String sixLetter = fiveLetter + String.valueOf(word.charAt(n));
																		boolean crossSix = false;
																		if (invalidCross.size() > 5) {
																			crossSix = invalidCross.get(6).contains(String.valueOf(word.charAt(n)));
																		}
																		if (!crossSix && !validWords.contains(sixLetter)) {
																			validWords.add(sixLetter);
																		}
																		if (word.length() > 6 && permutationCap > 6 && !crossSix) {
																			
																			for (int o = 0; o < word.length(); o++) {
																				if (o != i && o != j && o != k && o != l && o != m && o != n) {
																					String sevenLetter = sixLetter + String.valueOf(word.charAt(o));
																					boolean crossSeven = false;
																					if (invalidCross.size() > 6) {
																						crossSeven = invalidCross.get(7).contains(String.valueOf(word.charAt(o)));
																					}
																					if (!crossSeven && !validWords.contains(sevenLetter)) {
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

				if (thisWordLetter.equals(thisTileLetter)) {
					result.add(thisTile);
					rack.remove(thisTile);
					break;
				}
			}
		}

		return result;
	}
	
	PaneOrganizer getOrganizer() {
		return _organizer;
	}

	int getWordValue(ArrayList<Tile> tiles) {
		int isOnADoubleWordScore = 1;
		int isOnATripleWordScore = 1;
		int wordValue = 0;
		
		for (Tile thisTile : tiles) {
			int x = thisTile.getXIndex();
			int y = thisTile.getYIndex();

			int letterValue = 0;

			BoardSquare thisSquare = _boardArray[x][y];
			int value = thisTile.getValue();

			if (thisSquare.is2W()) {
				if (thisSquare.isAlreadyPlayed()) isOnADoubleWordScore *= 2;
				letterValue = value;
			} else if (thisSquare.is3W()) {
				if (thisSquare.isAlreadyPlayed()) isOnATripleWordScore *= 3;
				letterValue = value;
			} else if (thisSquare.isNormal()) {
				letterValue = value;
			} else if (thisSquare.is2L()) {
				if (thisSquare.isAlreadyPlayed()) letterValue = value * 2;
				else letterValue = value;
			} else if (thisSquare.is3L()) {
				if (thisSquare.isAlreadyPlayed()) letterValue = value * 3;
				else letterValue = value;
			}
			wordValue += letterValue;
		}
		wordValue = wordValue * isOnADoubleWordScore * isOnATripleWordScore;
		return wordValue;
	}

	void updateAlreadyPlayed() {
		for (Tile thisTile : _tilesOnBoard) {
			_boardArray[thisTile.getXIndex()][thisTile.getYIndex()].setAlreadyPlayed();
		}
	}

	void addTileToBoardArrayAt(Tile tile, int x, int y) {
		if (!isBetween(x, y)) return;
		_tileArray[x][y] = tile;
	}

	ArrayList<Tile> getTilesOnBoard() {
		return _tilesOnBoard;
	}

	boolean boardSquareOccupiedAt(int x, int y) {
		if (!this.isBetween(x, y)) return false;
		return _tileArray[x][y] != null;
	}

	private boolean isBetween(int check, int check2) {
		boolean result = false;
		if (check >= 0 && check <= 14 && check2 >= 0 && check2 <= 14)
			result = true;
		return result;
	}

	Tile getTileFromArrayAt(int x, int y) {
		return _tileArray[x][y];
	}

	Tile[][] getTileArray() {
		return _tileArray;
	}

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

	void declareEnterable() {
		_organizer.DeclareEnterable();
	}

	void shiftTiles(PlayerNum identity) {
		boolean isPlayerOne = identity == PlayerNum.One;
		ArrayList<Tile> rack = isPlayerOne ? _playerOneRack : _playerTwoRack;
		int x = isPlayerOne ? COLLECTION_ONE_HORIZONTAL_OFFSET : COLLECTION_TWO_HORIZONTAL_OFFSET;
		if (rack.isEmpty()) return;

		for (int i = 0; i < rack.size(); i++) {
			Tile thisTile = rack.get(i);
			thisTile.setLoc(x, i + COLLECTION_VERTICAL_OFFSET);
		}
	}

	ArrayList<Tile> getPlayerRack(PlayerNum num) {
		return num == PlayerNum.One ? _playerOneRack : _playerTwoRack;
	}

	void collectAdjacents(CollectionOrientation orientation, Tile thisTile, ArrayList<Tile> tiles) {
		int x = thisTile.getXIndex();
		int y = thisTile.getYIndex();
		int i = 1;

		if (!tiles.contains(thisTile)) tiles.add(thisTile);

		if (orientation == CollectionOrientation.Horizontal) {
			while (x - i >= 0 && _tileArray[x - i][y] != null && _tileArray[x - i][y].hasBeenPlayed()) {
				Tile t = _tileArray[x - i][y];
				if (!tiles.contains(t)) tiles.add(t);
				i++;
			}
			i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null && _tileArray[x + i][y].hasBeenPlayed()) {
				Tile t = _tileArray[x + i][y];
				if (!tiles.contains(t)) tiles.add(t);
				i++;
			}
		} else if (orientation == CollectionOrientation.Vertical) {
			i = 1;
			while (y - i >= 0 && _tileArray[x][y - i] != null && _tileArray[x][y - i].hasBeenPlayed()) {
				Tile t = _tileArray[x][y - i];
				if (!tiles.contains(t)) tiles.add(t);
				i++;
			}
			i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null && _tileArray[x][y + i].hasBeenPlayed()) {
				Tile t = _tileArray[x][y + i];
				if (!tiles.contains(t)) tiles.add(t);
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

	int getValueFromString(String validWord, int firstXIndex, int firstYIndex, WordOrientation orientation) {
		int wordValue = 0;
		int x;
		int y;

		int isOnADoubleWordScore = 1;
		int isOnATripleWordScore = 1;

		for (int i = 0; i < validWord.length(); i++) {
			String currentLetter = String.valueOf(validWord.charAt(i));
			int letterValue = TILE_INFO.get(A.indexOf(currentLetter)).getValue();

			boolean horizontal = orientation == WordOrientation.Horizontal;
			x = horizontal ? firstXIndex + i : firstXIndex;
			y = horizontal ? firstYIndex : firstYIndex + i;

			if (this.isBetween(x, y)) {
				BoardSquare thisSquare = _boardArray[x][y];
				if (thisSquare.is2W() && !thisSquare.isAlreadyPlayed()) {
					isOnADoubleWordScore *= 2;
				} else if (thisSquare.is3W() && !thisSquare.isAlreadyPlayed()) {
					isOnATripleWordScore *= 3;
				} else if (thisSquare.is2L() && !thisSquare.isAlreadyPlayed()) {
					letterValue *= 2;
				} else if (thisSquare.is3L() && !thisSquare.isAlreadyPlayed()) {
					letterValue *= 3;
				}
			} else return 0;

			wordValue += letterValue;
		}

		wordValue *= (isOnADoubleWordScore * isOnATripleWordScore);
		return wordValue;
	}
	
	int getNumPrefixSlots(int x, int y, CollectionOrientation orientation) {
		if (orientation == CollectionOrientation.Vertical) {
			int numPrefixSlots = 1;
			int i = 1;
			while (y - i >= 0 && _tileArray[x][y - i] == null) {
				numPrefixSlots++;
				i++;
			}
			return numPrefixSlots;
		} else {
			int numPrefixSlots = 1;
			int i = 1;
			while (x - i >= 0 && _tileArray[x - i][y] == null) {
				numPrefixSlots++;
				i++;
			}
			return numPrefixSlots;
		}
	}
	
	int getNumSuffixSlots(int x, int y, CollectionOrientation orientation) {
		if (orientation == CollectionOrientation.Vertical) {
			if (y > 14) return 0;
			int numSuffixSlots = 1;
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] == null) {
				numSuffixSlots++;
				i++;
			}
			return numSuffixSlots;
		} else {
			if (x > 14) return 0;
			int numSuffixSlots = 1;
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] == null) {
				numSuffixSlots++;
				i++;
			}
			return numSuffixSlots;
		}
	}
	
	ArrayList<Tile> getKernelTiles(int x, int y, CollectionOrientation orientation) {
		if (orientation == CollectionOrientation.Vertical) {
			ArrayList<Tile> kernelTiles = new ArrayList<>();
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null) {
				kernelTiles.add(_tileArray[x][y + i]);
				i++;
			}
			return kernelTiles;
		} else {
			ArrayList<Tile> kernelTiles = new ArrayList<>();
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null) {
				kernelTiles.add(_tileArray[x + i][y]);
				i++;
			}
			return kernelTiles;
		}
	}

	String getKernelFor(int x, int y, CollectionOrientation orientation) {
		if (orientation == CollectionOrientation.Vertical) {
			StringBuilder kernel = new StringBuilder();
			int i = 1;
			while (y + i <= 14 && _tileArray[x][y + i] != null) {
				kernel.append(_tileArray[x][y + i].getLetter());
				i++;
			}
			return kernel.toString();
		} else {
			StringBuilder kernel = new StringBuilder();
			int i = 1;
			while (x + i <= 14 && _tileArray[x + i][y] != null) {
				kernel.append(_tileArray[x + i][y].getLetter());
				i++;
			}
			return kernel.toString();
		}
	}

	void mapKernelsForRow(int y, HashMap<String, String> precedingKernels, HashMap<String, String> succeedingKernels) {
		HashMap<Integer, String> kernels = new HashMap<>();

		int read;
		int kernelNum = 0;
		StringBuilder kernel = new StringBuilder();

		kernels.put(kernelNum, kernel.toString());

		for (int x = 0; x <= 14; x++) {
			if (_tileArray[x][y] != null) {
				read = 0;
				kernel = new StringBuilder();

				while (x + read <= 14 && _tileArray[x + read][y] != null) {
					Tile thisTile = _tileArray[x + read][y];
					kernel.append(thisTile.getLetter());
					read++;
				}

				kernelNum++;
				kernels.put(kernelNum, kernel.toString());
				x += read;
			}
		}
		kernels.put(kernelNum + 1, "");

		if (kernels.size() > 1) {
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i + 1);
				String precedingKernel = kernels.get(i);

				precedingKernels.put(thisKernel, precedingKernel);
			}
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i);
				String succeedingKernel = kernels.get(i + 1);

				succeedingKernels.put(thisKernel, succeedingKernel);
			}
		}
	}

	void mapKernelsForColumn(int x, HashMap<String, String> precedingKernels, HashMap<String, String> succeedingKernels) {
		HashMap<Integer, String> kernels = new HashMap<>();

		int read;
		int kernelNum = 0;
		StringBuilder kernel = new StringBuilder();

		kernels.put(kernelNum, kernel.toString());

		for (int y = 0; y <= 14; y++) {
			if (_tileArray[x][y] != null) {
				read = 0;
				kernel = new StringBuilder();

				while (y + read <= 14 && _tileArray[x][y + read] != null) {
					kernel.append(_tileArray[x][y + read].getLetter());
					read++;
				}

				kernelNum++;
				kernels.put(kernelNum, kernel.toString());
				y += read;
			}
		}
		kernels.put(kernelNum + 1, "");

		if (kernels.size() > 1) {
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i + 1);
				String precedingKernel = kernels.get(i);

				precedingKernels.put(thisKernel, precedingKernel);
			}
			for (int i = 0; i < kernels.size(); i++) {
				String thisKernel = kernels.get(i);
				String succeedingKernel = kernels.get(i + 1);

				succeedingKernels.put(thisKernel, succeedingKernel);
			}
		}
	}

	void displayOutcome(Winner winner) {
		_organizer.moveHi(winner);
	}

	void rotateBag() {
		_organizer.rotateBag();
	}

	boolean getAutoReset() {
		return _autoReset;
	}

	void shuffleRack(PlayerNum num) {
		boolean isPlayerOne = num == PlayerNum.One;
		ArrayList<Tile> rack = isPlayerOne ? _playerOneRack : _playerTwoRack;

		if ((int) rack.stream().filter(t -> !t.isOnBoard()).count() != 7) return;

		List<Tile> sorted = new ArrayList<>(rack);
		sorted.sort(Comparator.comparing(Tile::getY));

		for (int i = 0; i < sorted.size() - 1; i++) {
			Tile upperTile = sorted.get(i);
			Tile lowerTile = sorted.get(i + 1);

			switch ((int) (Math.random() * 3)) {
				case 0:
				case 1:
					upperTile.moveDown(upperTile.getLetter());
					lowerTile.moveUp(lowerTile.getLetter());

					sorted.set(i + 1, upperTile);
					sorted.set(i, lowerTile);
					break;
				case 2:
					break;
			}
		}

		if (isPlayerOne) _playerOneRack = new ArrayList<>(sorted);
		else _playerTwoRack = new ArrayList<>(sorted);
	}

}