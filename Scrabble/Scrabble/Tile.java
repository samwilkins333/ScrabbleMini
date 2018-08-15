package Scrabble;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.event.*;
import javafx.scene.input.*;
import java.util.ArrayList;
import javafx.animation.*;
import javafx.util.Duration;

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
	private PlayerNum _tileAffiliation;
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
		_tileViewer.setFitWidth(Constants.GRID_FACTOR - (Constants.TILE_PADDING * 2));
		_tileViewer.setPreserveRatio(true);
		_tileViewer.setCache(true);
		_snappedX = false;
		_snappedY = false;
		_isOnBoard = false;
		_xIndex = -1;
		_yIndex = -1;
		_partOfNewestWord = false;
		_tileAffiliation = null;
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

	public Tile(int x, int y, String letter) {
		_xIndex = x;
		_yIndex = y;
		_letter = letter;
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

	public Boolean getIsFirstLetter() {
		return _isFirstLetter;
	}

	public void setIsFirstLetter(Boolean status) {
		_isFirstLetter = status;
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
		// _tileViewer.addEventHandler(MouseEvent.MOUSE_DRAGGED, new DragHandler());
		// _draggableTile.getChildren().add(_tileViewer);
		_tileViewer.setOnMousePressed(this.pressMouse());
		_tileViewer.setOnMouseDragged(this.dragMouse());
		_tileViewer.setOnMouseReleased(this.releaseMouse());
	}

	private Boolean isBetween(int lowerBounds, int check, int check2, int upperBounds) {
		Boolean result = false;
		if (check >= lowerBounds && check <= upperBounds && check2 >= lowerBounds && check2 <= upperBounds) {
			result = true;
		}
		return result;
	}

	private void toFront() {
		_boardPane.getChildren().remove(_tileViewer);
		_boardPane.getChildren().add(_tileViewer);
	}

	private Boolean isDraggable() {
		if (!_scrabbleGame.gameIsPlaying()) {
			return false;
		} else if (_scrabbleGame.getReferee().isThinking()) {
			return false;
		} else {
			this.refreshPlayerInfo();
			if (_currentPlayer.getPlayerNumber() == _tileAffiliation) {
				if (_isAddedToBoard == true) {
					return false;
				} else if (_isAddedToBoard == false) {
					return true;
				}
			} else {
				return false;
			}
			return false;
		}
	}

	private void setUpOverlapFlash() {
		_overlapFlash = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION), _tileViewer);
		_overlapFlash.setFromValue(1.0);
		_overlapFlash.setToValue(0.0);
		_overlapFlash.setAutoReverse(true);
		_overlapFlash.setCycleCount(Animation.INDEFINITE);
		_overlapScale = new ScaleTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION), _tileViewer);
		_overlapScale.setByX(0.2);
		_overlapScale.setByY(0.2);
		_overlapScale.setAutoReverse(true);
	}

	private EventHandler<MouseEvent> pressMouse() {
		EventHandler<MouseEvent> mousePressHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
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
			}
		};
		return mousePressHandler;
	}

	public void addTo(Tile[][] boardArray) {
		if (this.isBetween(0, _xIndex, _yIndex, 15)) {
			boardArray[_xIndex][_yIndex] = this;
			// System.out.printf("Tile added to Array at %s, %s\n", _xIndex, _yIndex);
		} else {
			System.out.println("ARRAY OUT OF BOUNDS - TILE NOT ON BOARD");
		}
	}

	public void addTo(ArrayList<Tile> boardArrayList) {
		boardArrayList.add(this);
	}

	void reset() {
		_tileViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_tileViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_xIndex = -1;
		_yIndex = -1;
		_isOnBoard = false;
	}

	private EventHandler<MouseEvent> dragMouse() {
		EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
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
			}
		};
		return dragHandler;
	}

	private void checkOutOfBoard() {
		Boolean status = true;
		double centerX = this.getCenterX();
		double centerY = this.getCenterY();
		if (centerX >= Constants.X0 * Constants.GRID_FACTOR
				&& centerX < Constants.X15 * Constants.GRID_FACTOR
				&& centerY >= Constants.Y0 * Constants.GRID_FACTOR
				&& centerY < Constants.Y15 * Constants.GRID_FACTOR) {
			status = false;
		}
		if (status == true) {
			_tileViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_tileViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
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
			xMin = Constants.X0 * Constants.GRID_FACTOR;
			xMax = Constants.X1 * Constants.GRID_FACTOR;
			break;
		case 1:
			xMin = Constants.X1 * Constants.GRID_FACTOR;
			xMax = Constants.X2 * Constants.GRID_FACTOR;
			break;
		case 2:
			xMin = Constants.X2 * Constants.GRID_FACTOR;
			xMax = Constants.X3 * Constants.GRID_FACTOR;
			break;
		case 3:
			xMin = Constants.X3 * Constants.GRID_FACTOR;
			xMax = Constants.X4 * Constants.GRID_FACTOR;
			break;
		case 4:
			xMin = Constants.X4 * Constants.GRID_FACTOR;
			xMax = Constants.X5 * Constants.GRID_FACTOR;
			break;
		case 5:
			xMin = Constants.X5 * Constants.GRID_FACTOR;
			xMax = Constants.X6 * Constants.GRID_FACTOR;
			break;
		case 6:
			xMin = Constants.X6 * Constants.GRID_FACTOR;
			xMax = Constants.X7 * Constants.GRID_FACTOR;
			break;
		case 7:
			xMin = Constants.X7 * Constants.GRID_FACTOR;
			xMax = Constants.X8 * Constants.GRID_FACTOR;
			break;
		case 8:
			xMin = Constants.X8 * Constants.GRID_FACTOR;
			xMax = Constants.X9 * Constants.GRID_FACTOR;
			break;
		case 9:
			xMin = Constants.X9 * Constants.GRID_FACTOR;
			xMax = Constants.X10 * Constants.GRID_FACTOR;
			break;
		case 10:
			xMin = Constants.X10 * Constants.GRID_FACTOR;
			xMax = Constants.X11 * Constants.GRID_FACTOR;
			break;
		case 11:
			xMin = Constants.X11 * Constants.GRID_FACTOR;
			xMax = Constants.X12 * Constants.GRID_FACTOR;
			break;
		case 12:
			xMin = Constants.X12 * Constants.GRID_FACTOR;
			xMax = Constants.X13 * Constants.GRID_FACTOR;
			break;
		case 13:
			xMin = Constants.X13 * Constants.GRID_FACTOR;
			xMax = Constants.X14 * Constants.GRID_FACTOR;
			break;
		case 14:
			xMin = Constants.X14 * Constants.GRID_FACTOR;
			xMax = Constants.X15 * Constants.GRID_FACTOR;
			break;
		}
		Boolean status = false;
		if (this.getCenterX() >= xMin && this.getCenterX() < xMax) {
			status = true;
		}
		if (status == true) {
			_tileViewer.setLayoutX(xMin + Constants.TILE_PADDING);
			_checkViewer.setLayoutX(xMin + Constants.TILE_PADDING);
			_xViewer.setLayoutX(xMin + Constants.TILE_PADDING);
			_minusViewer.setLayoutX(xMin + Constants.TILE_PADDING);
			_snappedX = true;
		}
	}

	private void checkRow(int id) {
		int yMin = 0;
		int yMax = 0;
		switch (id) {
		case 0:
			yMin = Constants.Y0 * Constants.GRID_FACTOR;
			yMax = Constants.Y1 * Constants.GRID_FACTOR;
			break;
		case 1:
			yMin = Constants.Y1 * Constants.GRID_FACTOR;
			yMax = Constants.Y2 * Constants.GRID_FACTOR;
			break;
		case 2:
			yMin = Constants.Y2 * Constants.GRID_FACTOR;
			yMax = Constants.Y3 * Constants.GRID_FACTOR;
			break;
		case 3:
			yMin = Constants.Y3 * Constants.GRID_FACTOR;
			yMax = Constants.Y4 * Constants.GRID_FACTOR;
			break;
		case 4:
			yMin = Constants.Y4 * Constants.GRID_FACTOR;
			yMax = Constants.Y5 * Constants.GRID_FACTOR;
			break;
		case 5:
			yMin = Constants.Y5 * Constants.GRID_FACTOR;
			yMax = Constants.Y6 * Constants.GRID_FACTOR;
			break;
		case 6:
			yMin = Constants.Y6 * Constants.GRID_FACTOR;
			yMax = Constants.Y7 * Constants.GRID_FACTOR;
			break;
		case 7:
			yMin = Constants.Y7 * Constants.GRID_FACTOR;
			yMax = Constants.Y8 * Constants.GRID_FACTOR;
			break;
		case 8:
			yMin = Constants.Y8 * Constants.GRID_FACTOR;
			yMax = Constants.Y9 * Constants.GRID_FACTOR;
			break;
		case 9:
			yMin = Constants.Y9 * Constants.GRID_FACTOR;
			yMax = Constants.Y10 * Constants.GRID_FACTOR;
			break;
		case 10:
			yMin = Constants.Y10 * Constants.GRID_FACTOR;
			yMax = Constants.Y11 * Constants.GRID_FACTOR;
			break;
		case 11:
			yMin = Constants.Y11 * Constants.GRID_FACTOR;
			yMax = Constants.Y12 * Constants.GRID_FACTOR;
			break;
		case 12:
			yMin = Constants.Y12 * Constants.GRID_FACTOR;
			yMax = Constants.Y13 * Constants.GRID_FACTOR;
			break;
		case 13:
			yMin = Constants.Y13 * Constants.GRID_FACTOR;
			yMax = Constants.Y14 * Constants.GRID_FACTOR;
			break;
		case 14:
			yMin = Constants.Y14 * Constants.GRID_FACTOR;
			yMax = Constants.Y15 * Constants.GRID_FACTOR;
			break;
		}
		Boolean status = false;
		if (this.getCenterY() >= yMin && this.getCenterY() < yMax) {
			status = true;
		}
		if (status == true) {
			_tileViewer.setLayoutY(yMin + Constants.TILE_PADDING);
			_checkViewer.setLayoutY(yMin + Constants.TILE_PADDING);
			_xViewer.setLayoutY(yMin + Constants.TILE_PADDING);
			_minusViewer.setLayoutY(yMin + Constants.TILE_PADDING);
			_snappedY = true;
		}
	}

	private EventHandler<MouseEvent> releaseMouse() {
		EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				if (Tile.this.isDraggable()) {
					// System.out.printf("Clicked tile released at %s, %s\n", _xIndex, _yIndex);
					_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
					Tile.this.checkOutOfBoard();
					for (int c = 0; c < 15; c++) {
						if (_snappedX == false) {
							Tile.this.checkColumn(c);
							if (_snappedX == true) {
								_xIndex = c;
								// System.out.printf("Index X = %s\n", _xIndex);
							}
						} else {
							break;
						}
					}
					for (int r = 0; r < 15; r++) {
						if (_snappedY == false) {
							Tile.this.checkRow(r);
							if (_snappedY == true) {
								_yIndex = r;
								// System.out.printf("Index Y = %s\n", _yIndex);
							}
						} else {
							break;
						}
					}
					if (_scrabbleGame.boardSquareOccupiedAt(_xIndex, _yIndex) || event.getClickCount() == 2) {
						_tileViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_tileViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_checkViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_checkViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_xViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_xViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_minusViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
						_minusViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
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
						if (_tilesOnBoard.contains(Tile.this)) {
							_tilesOnBoard.remove(Tile.this);
						}
					}
					if (_newestWord.containsATileAt(Tile.this, _xIndex, _yIndex)) {
						_overlapFlash.play();
						_overlapScale.play();
						_tileViewer.setEffect(null);
					}
					if (_isOnBoard == true && _partOfNewestWord == false) {
						_newestWord.addTileToWord(Tile.this);
						if (!_tilesOnBoard.contains(Tile.this)) {
							_tilesOnBoard.add(Tile.this);
						}
						_partOfNewestWord = true;
					} else if (_isOnBoard == false && _partOfNewestWord == true) {
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
			}
		};
		return releaseHandler;
	}

	void setToOpaque() {
		_tileViewer.setOpacity(1.0);
	}

	private void refreshPlayerInfo() {
		_currentPlayer = _scrabbleGame.getReferee().getCurrentPlayerInstance();
		_newestWord = _currentPlayer.getNewestWord();
	}

	private void addShadow() {
		_pieceShadow = new DropShadow();
		_pieceShadow.setRadius(120);
		_pieceShadow.setOffsetX(4);
		_pieceShadow.setOffsetY(4);
		_pieceShadow.setColor(Constants.SHADOW_FILL);
		_pieceShadow.setSpread(0.0);
		_pieceShadow.setHeight(25);
		_pieceShadow.setWidth(25);
		_pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_tileViewer.setEffect(_pieceShadow);
	}

	void add(Pane boardPane, double x, double y, ScrabbleGame thisGame, PlayerNum tileAffiliation) {
		_x = (int) x;
		_y = (int) y;
		_scrabbleGame = thisGame;
		this.setUpFlash();
		_flashable = true;
		this.addRoot();
		_boardPane = boardPane;
		_tileAffiliation = tileAffiliation;
		_tileViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_tileViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_boardPane.getChildren().add(_tileViewer);
	}

	public String getTileAffiliation() {
		return _tileAffiliation;
	}

	void placeAtSquare(int x, int y) {
		_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
		if (this.isBetween(0, x, y, 15)) {
			_tileViewer.setLayoutX((Constants.ZEROETH_COLUMN_OFFSET + x) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_tileViewer.setLayoutY((Constants.ZEROETH_ROW_OFFSET + y) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutX((Constants.ZEROETH_COLUMN_OFFSET + x) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutY((Constants.ZEROETH_ROW_OFFSET + y) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutX((Constants.ZEROETH_COLUMN_OFFSET + x) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutY((Constants.ZEROETH_ROW_OFFSET + y) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutX((Constants.ZEROETH_COLUMN_OFFSET + x) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutY((Constants.ZEROETH_ROW_OFFSET + y) * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_isOnBoard = true;
			if (!_tilesOnBoard.contains(Tile.this)) {
				_tilesOnBoard.add(Tile.this);
			}
			_xIndex = x;
			_yIndex = y;
		}
	}

	private void setUpFlash() {
		_checkViewer = new ImageView(new Image("Images/Interaction Feedback/greencheck.png"));
		_checkViewer.setFitWidth(Constants.GRID_FACTOR - (Constants.TILE_PADDING * 2));
		_checkViewer.setLayoutX(Constants.GRID_FACTOR * 5);
		_checkViewer.setLayoutY(Constants.GRID_FACTOR * 4);
		_checkViewer.setOpacity(0);
		_checkViewer.setCache(true);
		_checkViewer.setPreserveRatio(true);
		_checkViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_checkViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);

		_addedFlash = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION), _checkViewer);
		_addedFlash.setFromValue(1.0);
		_addedFlash.setToValue(0.0);
		_addedFlash.setAutoReverse(false);
		_addedFlash.setCycleCount(1);
		_addedFlash.setOnFinished(new RemoveIconsHandler("ADDED"));

		_xViewer = new ImageView(new Image("Images/Interaction Feedback/redx.png"));
		_xViewer.setFitWidth(Constants.GRID_FACTOR - (Constants.TILE_PADDING * 2));
		_xViewer.setLayoutX(Constants.GRID_FACTOR * 5);
		_xViewer.setLayoutY(Constants.GRID_FACTOR * 4);
		_xViewer.setOpacity(0);
		_xViewer.setCache(true);
		_xViewer.setPreserveRatio(true);
		_xViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_xViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);

		_failedFlash = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION), _xViewer);
		_failedFlash.setFromValue(1.0);
		_failedFlash.setToValue(0.0);
		_failedFlash.setAutoReverse(false);
		_failedFlash.setCycleCount(1);
		_failedFlash.setOnFinished(new RemoveIconsHandler("FAILED"));

		_minusViewer = new ImageView(new Image("Images/Interaction Feedback/yellowminus.png"));
		_minusViewer.setFitWidth(Constants.GRID_FACTOR - (Constants.TILE_PADDING * 2));
		_minusViewer.setLayoutX(Constants.GRID_FACTOR * 5);
		_minusViewer.setLayoutY(Constants.GRID_FACTOR * 4);
		_minusViewer.setOpacity(0);
		_minusViewer.setCache(true);
		_minusViewer.setPreserveRatio(true);
		_minusViewer.setLayoutX(_x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_minusViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);

		_partialFlash = new FadeTransition(Duration.seconds(Constants.FEEDBACK_FLASH_DURATION), _minusViewer);
		_partialFlash.setFromValue(1.0);
		_partialFlash.setToValue(0.0);
		_partialFlash.setAutoReverse(false);
		_partialFlash.setCycleCount(1);
		_partialFlash.setOnFinished(new RemoveIconsHandler("PARTIAL"));
	}

	void playFlash(WordAddition outcome) {
	    if (!_flashable) return;

	    switch (outcome) {
            case Success:
                _root.getChildren().add(_checkViewer);
                _addedFlash.play();
                break;
            case Partial:
                _root.getChildren().add(_minusViewer);
                _partialFlash.play();
                break;
            case Failure:
                _root.getChildren().add(_xViewer);
                _failedFlash.play();
                break;
        }

        _flashable = false;
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
		_tileViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_tileViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_checkViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_checkViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_minusViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_minusViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_xViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_xViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_xIndex = -1;
		_yIndex = -1;
	}
	
	ImageView getCheckViewer() {
		return _checkViewer;
	}
	
	void setCheckLoc(double x, double y) {
		_checkViewer.setLayoutX(x * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		_checkViewer.setLayoutY(y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
	}

	public int getX() {
		return (int) ((_tileViewer.getLayoutX() - Constants.TILE_PADDING) / Constants.GRID_FACTOR);
	}

	int getY() {
		return (int) ((_tileViewer.getLayoutY() - Constants.TILE_PADDING) / Constants.GRID_FACTOR);
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

	int getValue() {
		return _value;
	}

	void hide() {
		_tileViewer.setOpacity(0);
	}

	String getLetter() {
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
			_tileViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		}
	}
	
	void moveUp() {
		if (_xIndex == -1 && _yIndex == -1) {
			_y = _y - 1;
			_tileViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_checkViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_minusViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
			_xViewer.setLayoutY(_y * Constants.GRID_FACTOR + Constants.TILE_PADDING);
		}
	}
	
	boolean isOnBoard() {
		return _isOnBoard;
	}

}