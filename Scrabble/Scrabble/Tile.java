package Scrabble;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.event.*;
import javafx.scene.input.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.util.Duration;

import static Scrabble.Constants.*;

public class Tile {
	private int _value;
	private int _x;
	private int _y;
	private String _letter;
	private ImageView _tileViewer;
	private Pane _boardPane;
	private double _currentNodeX;
	private double _currentNodeY;
	private double _mouseDragX;
	private double _mouseDragY;
	private Boolean _snappedX;
	private Boolean _snappedY;
	private Boolean _isOnBoard;
	private int _xIndex;
	private int _yIndex;
	private ScrabbleGame _scrabbleGame;
	private Word _newestWord;
	private Boolean _partOfNewestWord;
	private PlayerNumber _playerAffiliation;
	private Playable _currentPlayer;
	private Boolean _isAddedToBoard;
	private ImageView _checkViewer;
	private ImageView _xViewer;
	private ImageView _minusViewer;
	private FadeTransition _addedFlash;
	private FadeTransition _failedFlash;
	private FadeTransition _partialFlash;
	private FadeTransition _overlapFlash;
	private ScaleTransition _overlapScale;
	private Pane _root;
	private Boolean _flashable;
	private Boolean _isFirstLetter;
	private DropShadow _pieceShadow;
	private ArrayList<Tile> _tilesOnBoard;

	Tile(int letter) {
		_tileViewer = new ImageView();
		_tileViewer.setFitWidth(GRID_FACTOR - (TILE_PADDING * 2));
		_tileViewer.setPreserveRatio(true);
		_tileViewer.setCache(true);
		_snappedX = false;
		_snappedY = false;
		_isOnBoard = false;
		_xIndex = -1;
		_yIndex = -1;
		_partOfNewestWord = false;
		_playerAffiliation = null;
		_currentPlayer = null;
		_isAddedToBoard = false;
		_flashable = false;
		_isFirstLetter = false;
		switch (letter) {
		case 0:
			_letter = "BLANK";
			_value = 0;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/blank.png"));
			break;
		case 1:
			_letter = "A";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/a.png"));
			break;
		case 2:
			_letter = "B";
			_value = 3;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/b.png"));
			break;
		case 3:
			_letter = "C";
			_value = 3;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/c.png"));
			break;
		case 4:
			_letter = "D";
			_value = 2;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/d.png"));
			break;
		case 5:
			_letter = "E";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/e.png"));
			break;
		case 6:
			_letter = "F";
			_value = 4;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/f.png"));
			break;
		case 7:
			_letter = "G";
			_value = 2;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/g.png"));
			break;
		case 8:
			_letter = "H";
			_value = 4;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/h.png"));
			break;
		case 9:
			_letter = "I";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/i.png"));
			break;
		case 10:
			_letter = "J";
			_value = 8;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/j.png"));
			break;
		case 11:
			_letter = "K";
			_value = 5;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/k.png"));
			break;
		case 12:
			_letter = "L";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/l.png"));
			break;
		case 13:
			_letter = "M";
			_value = 3;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/m.png"));
			break;
		case 14:
			_letter = "N";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/n.png"));
			break;
		case 15:
			_letter = "O";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/o.png"));
			break;
		case 16:
			_letter = "P";
			_value = 3;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/p.png"));
			break;
		case 17:
			_letter = "Q";
			_value = 10;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/q.png"));
			break;
		case 18:
			_letter = "R";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/r.png"));
			break;
		case 19:
			_letter = "S";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/s.png"));
			break;
		case 20:
			_letter = "T";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/t.png"));
			break;
		case 21:
			_letter = "U";
			_value = 1;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/u.png"));
			break;
		case 22:
			_letter = "V";
			_value = 4;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/v.png"));
			break;
		case 23:
			_letter = "W";
			_value = 4;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/w.png"));
			break;
		case 24:
			_letter = "X";
			_value = 8;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/x.png"));
			break;
		case 25:
			_letter = "Y";
			_value = 4;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/y.png"));
			break;
		case 26:
			_letter = "Z";
			_value = 10;
			_tileViewer.setImage(new Image("Images/Scrabble Tiles/z.png"));
			break;
		case 27:
			_letter = "TRANSPARENT";
			_value = 0;
			_tileViewer.setImage(new Image("blank.png"));
			_tileViewer.setOpacity(0.5);
			break;
		}
		this.addShadow();
		this.setUpOverlapFlash();
		this.setUpDraggable();
	}
	
	void fadeOut() {
		FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), _tileViewer);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();
	}

	private void addRoot() {
		_root = _scrabbleGame.getRoot();
	}

	void isAddedToBoard(Boolean status) {
		_isAddedToBoard = status;
	}

	Boolean isAddedToBoard() {
		return _isAddedToBoard;
	}

	void setIsPartOfNewestWord(Boolean status) {
		_partOfNewestWord = status;
	}

	void setIsOnBoard(Boolean status) {
		_isOnBoard = status;
	}

	private double getCenterX() {
		return _tileViewer.getLayoutX() + _tileViewer.getFitWidth() / 2;
	}

	private double getCenterY() {
		return _tileViewer.getLayoutY() + _tileViewer.getFitWidth() / 2;
	}

	private void setUpDraggable() {
		_tileViewer.setOnMousePressed(this.pressMouse());
		_tileViewer.setOnMouseDragged(this.dragMouse());
		_tileViewer.setOnMouseReleased(this.releaseMouse());
	}

	private Boolean areValidIndices(int[] numbers) {
		return Arrays.stream(numbers).allMatch(n -> n >= 0 && n <= 15);
	}

	void toFront() {
		_boardPane.getChildren().remove(_tileViewer);
		_boardPane.getChildren().add(_tileViewer);
	}

	private Boolean isDraggable() {
		if (!_scrabbleGame.gameIsPlaying() || _scrabbleGame.getReferee().isThinking()) return false;

		refreshPlayerInfo();
		if (_currentPlayer.getPlayerNumber() == _playerAffiliation) {
			return !isAddedToBoard();
		} else {
			return false;
		}
	}

	private void setUpOverlapFlash() {
		_overlapFlash = new FadeTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), _tileViewer);
		_overlapFlash.setFromValue(1.0);
		_overlapFlash.setToValue(0.0);
		_overlapFlash.setAutoReverse(true);
		_overlapFlash.setCycleCount(Animation.INDEFINITE);
		_overlapScale = new ScaleTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), _tileViewer);
		_overlapScale.setByX(0.2);
		_overlapScale.setByY(0.2);
		_overlapScale.setAutoReverse(true);
	}

	private EventHandler<MouseEvent> pressMouse() {
		return event -> {
			if (Tile.this.isDraggable()) {
				Tile.this.toFront();
				_overlapFlash.stop();
				_overlapScale.stop();
				_tileViewer.setScaleX(1);
				_tileViewer.setScaleY(1);
				_tileViewer.setOpacity(1.0);
				_tileViewer.setEffect(_pieceShadow);
				_snappedX = false;
				_snappedY = false;
				if (event.getButton() == MouseButton.PRIMARY) {
					// _tileViewer.setFitWidth(Constants.GRID_FACTOR);
					// get the current mouse coordinates according to the scene.
					_mouseDragX = event.getSceneX();
					_mouseDragY = event.getSceneY();

					// get the current coordinates of the draggable node.
					_currentNodeX = _tileViewer.getLayoutX();
					_currentNodeY = _tileViewer.getLayoutY();
				}
			}
		};
	}

	public void addTo(Tile[][] boardArray) {
		if (this.areValidIndices(new int[] { _xIndex, _yIndex })) {
			boardArray[_xIndex][_yIndex] = this;
			// System.out.printf("Tile added to Array at %s, %s\n", _xIndex, _yIndex);
		} else {
			System.out.println("ARRAY OUT OF BOUNDS - TILE NOT ON BOARD");
		}
	}

	public void addTo(ArrayList<Tile> boardArrayList) {
		boardArrayList.add(this);
	}

	public void reset(boolean animate) {
		int targetHorizontal = _x * GRID_FACTOR + TILE_PADDING;
		int targetVertical = _y * GRID_FACTOR + TILE_PADDING;

		System.out.printf("%s, %s => by %s, by %s\n", _x, _y, targetHorizontal - _tileViewer.getLayoutX(), targetVertical - _tileViewer.getLayoutY());

		if (animate) {
			toFront();
			TranslateTransition dragSim = new TranslateTransition(Duration.seconds(PLACEMENT_DURATION), _tileViewer);
			dragSim.setByX(targetHorizontal - _tileViewer.getLayoutX());
			dragSim.setByY(targetVertical - _tileViewer.getLayoutY());
			dragSim.play();
		} else {
			_tileViewer.setLayoutX(targetHorizontal);
			_tileViewer.setLayoutY(targetVertical);
		}

		_xIndex = -1;
		_yIndex = -1;

		_isOnBoard = false;
	}

	private EventHandler<MouseEvent> dragMouse() {
		return event -> {
			if (Tile.this.isDraggable()) {
				if (event.getButton() == MouseButton.PRIMARY) {
					// find the delta coordinates by subtracting the new mouse
					// coordinates with the old.
					double deltaX = event.getSceneX() - _mouseDragX;
					double deltaY = event.getSceneY() - _mouseDragY;

					// add the delta coordinates to the node coordinates.
					_currentNodeX += deltaX;
					_currentNodeY += deltaY;

					// set the layout for the draggable node.
					_tileViewer.setLayoutX(_currentNodeX);
					_tileViewer.setLayoutY(_currentNodeY);

					// get the latest mouse coordinate.
					_mouseDragX = event.getSceneX();
					_mouseDragY = event.getSceneY();
				}
			}
		};
	}

	private void checkOutOfBoard() {
		boolean status = true;
		double centerX = this.getCenterX();
		double centerY = this.getCenterY();
		if (centerX >= X0 * GRID_FACTOR
				&& centerX < X15 * GRID_FACTOR
				&& centerY >= Y0 * GRID_FACTOR
				&& centerY < Y15 * GRID_FACTOR) {
			status = false;
		}
		if (status) {
			_tileViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
			_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_snappedX = true;
			_snappedY = true;
			_isOnBoard = false;
			_xIndex = -1;
			_yIndex = -1;
		} else {
			_isOnBoard = true;
		}
	}

	private void checkColumn(int id) {
		int xMin = 0;
		int xMax = 0;
		switch (id) {
		case 0:
			xMin = X0 * GRID_FACTOR;
			xMax = X1 * GRID_FACTOR;
			break;
		case 1:
			xMin = X1 * GRID_FACTOR;
			xMax = X2 * GRID_FACTOR;
			break;
		case 2:
			xMin = X2 * GRID_FACTOR;
			xMax = X3 * GRID_FACTOR;
			break;
		case 3:
			xMin = X3 * GRID_FACTOR;
			xMax = X4 * GRID_FACTOR;
			break;
		case 4:
			xMin = X4 * GRID_FACTOR;
			xMax = X5 * GRID_FACTOR;
			break;
		case 5:
			xMin = X5 * GRID_FACTOR;
			xMax = X6 * GRID_FACTOR;
			break;
		case 6:
			xMin = X6 * GRID_FACTOR;
			xMax = X7 * GRID_FACTOR;
			break;
		case 7:
			xMin = X7 * GRID_FACTOR;
			xMax = X8 * GRID_FACTOR;
			break;
		case 8:
			xMin = X8 * GRID_FACTOR;
			xMax = X9 * GRID_FACTOR;
			break;
		case 9:
			xMin = X9 * GRID_FACTOR;
			xMax = X10 * GRID_FACTOR;
			break;
		case 10:
			xMin = X10 * GRID_FACTOR;
			xMax = X11 * GRID_FACTOR;
			break;
		case 11:
			xMin = X11 * GRID_FACTOR;
			xMax = X12 * GRID_FACTOR;
			break;
		case 12:
			xMin = X12 * GRID_FACTOR;
			xMax = X13 * GRID_FACTOR;
			break;
		case 13:
			xMin = X13 * GRID_FACTOR;
			xMax = X14 * GRID_FACTOR;
			break;
		case 14:
			xMin = X14 * GRID_FACTOR;
			xMax = X15 * GRID_FACTOR;
			break;
		}
		boolean status = false;
		if (this.getCenterX() >= xMin && this.getCenterX() < xMax) {
			status = true;
		}
		if (status) {
			_tileViewer.setLayoutX(xMin + TILE_PADDING);
			_checkViewer.setLayoutX(xMin + TILE_PADDING);
			_xViewer.setLayoutX(xMin + TILE_PADDING);
			_minusViewer.setLayoutX(xMin + TILE_PADDING);
			_snappedX = true;
		}
	}

	private void checkRow(int id) {
		int yMin = 0;
		int yMax = 0;
		switch (id) {
		case 0:
			yMin = Y0 * GRID_FACTOR;
			yMax = Y1 * GRID_FACTOR;
			break;
		case 1:
			yMin = Y1 * GRID_FACTOR;
			yMax = Y2 * GRID_FACTOR;
			break;
		case 2:
			yMin = Y2 * GRID_FACTOR;
			yMax = Y3 * GRID_FACTOR;
			break;
		case 3:
			yMin = Y3 * GRID_FACTOR;
			yMax = Y4 * GRID_FACTOR;
			break;
		case 4:
			yMin = Y4 * GRID_FACTOR;
			yMax = Y5 * GRID_FACTOR;
			break;
		case 5:
			yMin = Y5 * GRID_FACTOR;
			yMax = Y6 * GRID_FACTOR;
			break;
		case 6:
			yMin = Y6 * GRID_FACTOR;
			yMax = Y7 * GRID_FACTOR;
			break;
		case 7:
			yMin = Y7 * GRID_FACTOR;
			yMax = Y8 * GRID_FACTOR;
			break;
		case 8:
			yMin = Y8 * GRID_FACTOR;
			yMax = Y9 * GRID_FACTOR;
			break;
		case 9:
			yMin = Y9 * GRID_FACTOR;
			yMax = Y10 * GRID_FACTOR;
			break;
		case 10:
			yMin = Y10 * GRID_FACTOR;
			yMax = Y11 * GRID_FACTOR;
			break;
		case 11:
			yMin = Y11 * GRID_FACTOR;
			yMax = Y12 * GRID_FACTOR;
			break;
		case 12:
			yMin = Y12 * GRID_FACTOR;
			yMax = Y13 * GRID_FACTOR;
			break;
		case 13:
			yMin = Y13 * GRID_FACTOR;
			yMax = Y14 * GRID_FACTOR;
			break;
		case 14:
			yMin = Y14 * GRID_FACTOR;
			yMax = Y15 * GRID_FACTOR;
			break;
		}
		boolean status = false;
		if (this.getCenterY() >= yMin && this.getCenterY() < yMax) {
			status = true;
		}
		if (status) {
			_tileViewer.setLayoutY(yMin + TILE_PADDING);
			_checkViewer.setLayoutY(yMin + TILE_PADDING);
			_xViewer.setLayoutY(yMin + TILE_PADDING);
			_minusViewer.setLayoutY(yMin + TILE_PADDING);
			_snappedY = true;
		}
	}

	private EventHandler<MouseEvent> releaseMouse() {
		return event -> {
			if (Tile.this.isDraggable()) {
				// System.out.printf("Clicked tile released at %s, %s\n", _xIndex, _yIndex);
				_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
				Tile.this.checkOutOfBoard();
				for (int c = 0; c < 15; c++) {
					if (!_snappedX) {
						Tile.this.checkColumn(c);
						if (_snappedX) {
							_xIndex = c;
							// System.out.printf("Index X = %s\n", _xIndex);
						}
					} else {
						break;
					}
				}
				for (int r = 0; r < 15; r++) {
					if (!_snappedY) {
						Tile.this.checkRow(r);
						if (_snappedY) {
							_yIndex = r;
							// System.out.printf("Index Y = %s\n", _yIndex);
						}
					} else {
						break;
					}
				}
				if (_scrabbleGame.boardSquareOccupiedAt(_xIndex, _yIndex) || event.getClickCount() == 2) {
					_tileViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
					_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
					_checkViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
					_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
					_xViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
					_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
					_minusViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
					_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
					_overlapFlash.stop();
					_overlapScale.stop();
					_tileViewer.setScaleX(1);
					_tileViewer.setScaleY(1);
					_tileViewer.setOpacity(1.0);
					_tileViewer.setEffect(_pieceShadow);
					_snappedX = true;
					_snappedY = true;
					_isOnBoard = false;
					_xIndex = -1;
					_yIndex = -1;
					_tilesOnBoard.remove(Tile.this);
				}
				if (_newestWord.containsTileAt(Tile.this, _xIndex, _yIndex)) {
					_overlapFlash.play();
					_overlapScale.play();
					_tileViewer.setEffect(null);
				}
				if (_isOnBoard && !_partOfNewestWord) {
					_newestWord.addTileToWord(Tile.this);
					if (!_tilesOnBoard.contains(Tile.this)) {
						_tilesOnBoard.add(Tile.this);
					}
					_partOfNewestWord = true;
				} else if (!_isOnBoard && _partOfNewestWord) {
					_newestWord.removeTileFromWord(Tile.this);
					if (_tilesOnBoard.contains(Tile.this)) {
						_tilesOnBoard.remove(Tile.this);
					}
					_partOfNewestWord = false;
				}
				// System.out.printf("\n%s tiles on board\n\n", tilesOnBoard.size());
				// System.out.printf("Index X = %s, Index y = %s\n", _xIndex, _yIndex);
				_newestWord.printTileListContents(_newestWord.getTiles());
				_newestWord.checkAddedTiles();
				if (_newestWord.containsOnlyAddedTiles()) {
					_newestWord.clear();
				}
				// _newestWord.updateValue();
				_newestWord.printAllTileLists();
				System.out.printf("There are %s tiles on the board\n", _tilesOnBoard.size());
			}
		};
	}

	void setToOpaque() {
		_tileViewer.setOpacity(1.0);
	}

	private void refreshPlayerInfo() {
		_currentPlayer = _scrabbleGame.getReferee().getCurrentPlayer();
		_newestWord = _currentPlayer.getNewestWord();
	}

	private void addShadow() {
		_pieceShadow = new DropShadow();
		_pieceShadow.setRadius(120);
		_pieceShadow.setOffsetX(4);
		_pieceShadow.setOffsetY(4);
		_pieceShadow.setColor(SHADOW_FILL);
		_pieceShadow.setSpread(0.0);
		_pieceShadow.setHeight(25);
		_pieceShadow.setWidth(25);
		_pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_tileViewer.setEffect(_pieceShadow);
	}

	public void add(Pane boardPane, double x, double y, ScrabbleGame thisGame, PlayerNumber playerAffiliation) {
		_x = (int) x;
		_y = (int) y;
		_scrabbleGame = thisGame;
		this.setUpFlash();
		_flashable = true;
		this.addRoot();
		_boardPane = boardPane;
		_playerAffiliation = playerAffiliation;
		_tileViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_tileViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
		_boardPane.getChildren().add(_tileViewer);
	}

	void placeAtSquare(int x, int y, boolean animate) {
		_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		if (!this.areValidIndices(new int[] { x, y })) return;

		int targetHorizontal = (ZEROETH_COLUMN_OFFSET + x) * GRID_FACTOR + TILE_PADDING;
		int targetVertical = (ZEROETH_ROW_OFFSET + y) * GRID_FACTOR + TILE_PADDING;

		List<ImageView> tileComponents = Arrays.asList(_tileViewer, _checkViewer, _minusViewer, _xViewer);

		if (animate) {
			tileComponents.forEach(view -> {
				TranslateTransition dragSim = new TranslateTransition(Duration.seconds(PLACEMENT_DURATION), view);
				dragSim.setByX(targetHorizontal - view.getLayoutX());
				dragSim.setByY(targetVertical - view.getLayoutY());
				dragSim.play();
			});
		} else {
			tileComponents.forEach(view -> {
				view.setLayoutX(targetHorizontal);
				view.setLayoutY(targetVertical);
			});
		}

		_isOnBoard = true;

		if (!_tilesOnBoard.contains(Tile.this)) _tilesOnBoard.add(Tile.this);

		_xIndex = x;
		_yIndex = y;
	}

	private void setUpFlash() {
		_checkViewer = new ImageView(new Image("Images/Interaction Feedback/greencheck.png"));
		_checkViewer.setFitWidth(GRID_FACTOR - (TILE_PADDING * 2));
		_checkViewer.setLayoutX(GRID_FACTOR * 5);
		_checkViewer.setLayoutY(GRID_FACTOR * 4);
		_checkViewer.setOpacity(0);
		_checkViewer.setCache(true);
		_checkViewer.setPreserveRatio(true);
		_checkViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
		_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);

		_addedFlash = new FadeTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), _checkViewer);
		_addedFlash.setFromValue(1.0);
		_addedFlash.setToValue(0.0);
		_addedFlash.setAutoReverse(false);
		_addedFlash.setCycleCount(1);
		_addedFlash.setOnFinished(new RemoveIconsHandler("ADDED"));

		_xViewer = new ImageView(new Image("Images/Interaction Feedback/redx.png"));
		_xViewer.setFitWidth(GRID_FACTOR - (TILE_PADDING * 2));
		_xViewer.setLayoutX(GRID_FACTOR * 5);
		_xViewer.setLayoutY(GRID_FACTOR * 4);
		_xViewer.setOpacity(0);
		_xViewer.setCache(true);
		_xViewer.setPreserveRatio(true);
		_xViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
		_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);

		_failedFlash = new FadeTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), _xViewer);
		_failedFlash.setFromValue(1.0);
		_failedFlash.setToValue(0.0);
		_failedFlash.setAutoReverse(false);
		_failedFlash.setCycleCount(1);
		_failedFlash.setOnFinished(new RemoveIconsHandler("FAILED"));

		_minusViewer = new ImageView(new Image("Images/Interaction Feedback/yellowminus.png"));
		_minusViewer.setFitWidth(GRID_FACTOR - (TILE_PADDING * 2));
		_minusViewer.setLayoutX(GRID_FACTOR * 5);
		_minusViewer.setLayoutY(GRID_FACTOR * 4);
		_minusViewer.setOpacity(0);
		_minusViewer.setCache(true);
		_minusViewer.setPreserveRatio(true);
		_minusViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
		_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);

		_partialFlash = new FadeTransition(Duration.seconds(FEEDBACK_FLASH_DURATION), _minusViewer);
		_partialFlash.setFromValue(1.0);
		_partialFlash.setToValue(0.0);
		_partialFlash.setAutoReverse(false);
		_partialFlash.setCycleCount(1);
		_partialFlash.setOnFinished(new RemoveIconsHandler("PARTIAL"));
	}

	void playFlash(String id) {
		if (id == "ADDED" && _flashable == true) {
			_root.getChildren().add(_checkViewer);
			_addedFlash.play();
			_flashable = false;
		} else if (id == "FAILED" && _flashable == true) {
			_root.getChildren().add(_xViewer);
			_failedFlash.play();
			_flashable = false;
		} else if (id == "PARTIAL" && _flashable == true) {
			_root.getChildren().add(_minusViewer);
			_partialFlash.play();
			_flashable = false;
		}
	}

	private class RemoveIconsHandler implements EventHandler<ActionEvent> {
		private String _id;

		RemoveIconsHandler(String id) {
			_id = id;
		}

		@Override
		public void handle(ActionEvent event) {
			if (_id == "ADDED") {
				_root.getChildren().remove(_checkViewer);
				_flashable = true;
			} else if (_id == "FAILED") {
				_root.getChildren().remove(_xViewer);
				_flashable = true;
			} else if (_id == "PARTIAL") {
				_root.getChildren().remove(_minusViewer);
				_flashable = true;
			}
			event.consume();
		}

	}

	void setLoc(int x, int y) {
		_x = x;
		_y = y;
		_tileViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_tileViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
		_checkViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_checkViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
		_minusViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_minusViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
		_xViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_xViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
		_xIndex = -1;
		_yIndex = -1;
	}
	
	ImageView getCheckViewer() {
		return _checkViewer;
	}
	
	void setCheckLoc(double x, double y) {
		_checkViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_checkViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
	}

	public int getX() {
		return (int) ((_tileViewer.getLayoutX() - TILE_PADDING) / GRID_FACTOR);
	}

	int getY() {
		return (int) ((_tileViewer.getLayoutY() - TILE_PADDING) / GRID_FACTOR);
	}

	int getXIndex() {
		return _xIndex;
	}

	int getYIndex() {
		return _yIndex;
	}

	ImageView getTileViewer() {
		return _tileViewer;
	}

	public int getValue() {
		return _value;
	}

	public void hide() {
		_tileViewer.setOpacity(0);
	}

	public String getLetter() {
		return _letter;
	}

	void stopOverlapFlash() {
		_overlapFlash.stop();
		_overlapScale.stop();
		_tileViewer.setScaleX(1);
		_tileViewer.setScaleY(1);
		_tileViewer.setOpacity(1.0);
	}

	void resetShadow() {
		_tileViewer.setEffect(_pieceShadow);
	}

	void moveDown() {
		if (_xIndex == -1 && _yIndex == -1) {
			_y = _y + 1;
			_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
		}
	}
	
	void moveUp() {
		if (_xIndex == -1 && _yIndex == -1) {
			_y = _y - 1;
			_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
		}
	}
	
	boolean isOnBoard() {
		return _isOnBoard;
	}

}