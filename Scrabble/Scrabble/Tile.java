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

import static Scrabble.Constants.*;

class Tile {
	private final String _letter;
	private final ImageView _tileViewer;
	private Pane _boardPane;
	private Boolean _tempPlacedOnBoard;

	private final int _value;
	private int _x;
	private int _y;

	private double _currentNodeX;
	private double _currentNodeY;

	private double _mouseDragX;
	private double _mouseDragY;

	private int _xIndex;
	private int _yIndex;

	private ScrabbleGame _scrabbleGame;
	private Word _newestWord;
	private Boolean _partOfNewestWord;
	private PlayerNum _tileAffiliation;
	private Playable _currentPlayer;
	private Boolean _hasBeenPlayed;

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

	private DropShadow _pieceShadow;
	private ArrayList<Tile> _tilesOnBoard;
	private boolean _snappedX;
	private boolean _snappedY;

	Tile(int letter) {
		// Create stock new tile image view
		_tileViewer = new ImageView();
		_tileViewer.setFitWidth(GRID_FACTOR - (TILE_PADDING * 2));
		_tileViewer.setPreserveRatio(true);
		_tileViewer.setCache(true);

		_snappedX = false;
		_snappedY = false;

		// Set its default properties

		_tempPlacedOnBoard = false;
		_hasBeenPlayed = false;
		_partOfNewestWord = false;
		_flashable = false;

		_xIndex = -1;
		_yIndex = -1;

		_tileAffiliation = PlayerNum.Neither;
		_currentPlayer = null;

		Alpha t = TILE_INFO.get(letter);
		_letter = t.getLetter();
		_value = t.getValue();
		_tileViewer.setImage(t.getImage());

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

	Boolean hasBeenPlayed() {
		return _hasBeenPlayed;
	}

	void declareHasBeenPlayed() {
		_hasBeenPlayed = true;
	}

	void declareNotInNewestWord() {
		_partOfNewestWord = false;
	}

	void declareNotPlaced() {
		_tempPlacedOnBoard = false;
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

	private Boolean isBetween(int check, int check2) {
		boolean result = false;
		if (check >= 0 && check <= 15 && check2 >= 0 && check2 <= 15) result = true;
		return result;
	}

	private void toFront() {
		_boardPane.getChildren().remove(_tileViewer);
		_boardPane.getChildren().add(_tileViewer);
	}

	private Boolean isDraggable() {
		if (!_scrabbleGame.gameIsPlaying() || _scrabbleGame.getReferee().isThinking()) return false;

		this.refreshPlayerInfo();

		if (_tileAffiliation == _currentPlayer.getPlayerNumber()) return !hasBeenPlayed();
		return false;
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
			if (!Tile.this.isDraggable()) return;

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
				// get the current mouse coordinates according to the scene.
				_mouseDragX = event.getSceneX();
				_mouseDragY = event.getSceneY();

				// get the current coordinates of the draggable node.
				_currentNodeX = _tileViewer.getLayoutX();
				_currentNodeY = _tileViewer.getLayoutY();
			}
		};
	}

	private EventHandler<MouseEvent> dragMouse() {
		return event -> {
			if (!Tile.this.isDraggable()) return;

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
		};
	}

	private EventHandler<MouseEvent> releaseMouse() {
		return event -> {
 			if (!Tile.this.isDraggable()) return;

			_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
			Tile.this.checkOutOfBoard();

			for (int c = 0; c < 15; c++) {
				if (_snappedX) break;

				Tile.this.checkColumn(c);
				if (_snappedX) _xIndex = c;
			}

			for (int r = 0; r < 15; r++) {
				if (_snappedY) break;

				Tile.this.checkRow(r);
				if (_snappedY) _yIndex = r;
			}

			// If the user releases on a played piece or manually sends the piece back to the rack via double click...
			if (_scrabbleGame.boardSquareOccupiedAt(_xIndex, _yIndex) || event.getClickCount() == 2) {
				// ...update locations of both the tile and all of its associated flash overlays
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

				// Has been returned to rack from board
				_tempPlacedOnBoard = false;
				_xIndex = -1;
				_yIndex = -1;

				_tilesOnBoard.remove(Tile.this);
			}

			if (_newestWord.containsATileAt(Tile.this, _xIndex, _yIndex)) {
				_overlapFlash.play();
				_overlapScale.play();
				_tileViewer.setEffect(null);
			}

			if (_tempPlacedOnBoard && !_partOfNewestWord) {

				// After processing, if the tile has been removed from the board but isn't yet associated with current word, add to current word
				_newestWord.addTileToWord(Tile.this);
				if (!_tilesOnBoard.contains(Tile.this)) _tilesOnBoard.add(Tile.this);
				_partOfNewestWord = true;

			} else if (!_tempPlacedOnBoard && _partOfNewestWord) {

				// After processing, if the tile has been removed from the board and isn't associated with current word, revert to rack default properties
				_newestWord.removeTileFromWord(Tile.this);
				_tilesOnBoard.remove(Tile.this);
				_partOfNewestWord = false;

			}

			_newestWord.checkAddedTiles();
			// If all temp user-dragged tiles have been manually removed, ensure that no played tiles that had been added as adjacents linger in current word
			if (_newestWord.containsOnlyAddedTiles()) _newestWord.clear();
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

			_tempPlacedOnBoard = false;

			_xIndex = -1;
			_yIndex = -1;
		} else _tempPlacedOnBoard = true;
	}

	private void checkColumn(int num) {
		int xMin = 0;
		int xMax = 0;

		switch (num) {
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

		boolean hasBeenSet = false;

		if (this.getCenterX() >= xMin && this.getCenterX() < xMax) hasBeenSet = true;

		if (hasBeenSet) {
			_tileViewer.setLayoutX(xMin + TILE_PADDING);
			_checkViewer.setLayoutX(xMin + TILE_PADDING);
			_xViewer.setLayoutX(xMin + TILE_PADDING);
			_minusViewer.setLayoutX(xMin + TILE_PADDING);
			_snappedX = true;
		}
	}

	private void checkRow(int num) {
		int yMin = 0;
		int yMax = 0;

		switch (num) {
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

		boolean hasBeenSet = false;

		if (this.getCenterY() >= yMin && this.getCenterY() < yMax) hasBeenSet = true;

		if (hasBeenSet) {
			_tileViewer.setLayoutY(yMin + TILE_PADDING);
			_checkViewer.setLayoutY(yMin + TILE_PADDING);
			_xViewer.setLayoutY(yMin + TILE_PADDING);
			_minusViewer.setLayoutY(yMin + TILE_PADDING);
			_snappedY = true;
		}
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
		_pieceShadow.setColor(SHADOW_FILL);
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

		_tileAffiliation = tileAffiliation;

		_tileViewer.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		_tileViewer.setLayoutY(y * GRID_FACTOR + TILE_PADDING);

		_boardPane = boardPane;
		_boardPane.getChildren().add(_tileViewer);
	}

	void placeAtSquare(int x, int y) {
		if (!this.isBetween(x, y)) return;

		// If within the boundaries, position the tile view and all of its associated overlay views at the specified indices
		_tileViewer.setLayoutX((ZEROETH_COLUMN_OFFSET + x) * GRID_FACTOR + TILE_PADDING);
		_tileViewer.setLayoutY((ZEROETH_ROW_OFFSET + y) * GRID_FACTOR + TILE_PADDING);

		_checkViewer.setLayoutX((ZEROETH_COLUMN_OFFSET + x) * GRID_FACTOR + TILE_PADDING);
		_checkViewer.setLayoutY((ZEROETH_ROW_OFFSET + y) * GRID_FACTOR + TILE_PADDING);

		_minusViewer.setLayoutX((ZEROETH_COLUMN_OFFSET + x) * GRID_FACTOR + TILE_PADDING);
		_minusViewer.setLayoutY((ZEROETH_ROW_OFFSET + y) * GRID_FACTOR + TILE_PADDING);

		_xViewer.setLayoutX((ZEROETH_COLUMN_OFFSET + x) * GRID_FACTOR + TILE_PADDING);
		_xViewer.setLayoutY((ZEROETH_ROW_OFFSET + y) * GRID_FACTOR + TILE_PADDING);

		_tempPlacedOnBoard = true;

		_tilesOnBoard = _scrabbleGame.getTilesOnBoard();
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
		_addedFlash.setOnFinished(new RemoveIconsHandler(WordAddition.Success));

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
		_failedFlash.setOnFinished(new RemoveIconsHandler(WordAddition.Failure));

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
		_partialFlash.setOnFinished(new RemoveIconsHandler(WordAddition.Partial));
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
		private final WordAddition _outcome;

		RemoveIconsHandler(WordAddition outcome) {
			_outcome = outcome;
		}

		@Override
		public void handle(ActionEvent event) {
			switch(_outcome) {
				case Success:
					_root.getChildren().remove(_checkViewer);
					break;
				case Partial:
					_root.getChildren().remove(_minusViewer);
					break;
				case Failure:
					_root.getChildren().remove(_xViewer);
					break;
			}
			_flashable = true;
			event.consume();
		}

	}

	void setLoc(int x, int y) {
		_x = x;
		_y = y;

		setImageViewLoc(_tileViewer, x, y);
		setImageViewLoc(_checkViewer, x, y);
		setImageViewLoc(_minusViewer, x, y);
		setImageViewLoc(_xViewer, x, y);

		_xIndex = -1;
		_yIndex = -1;
	}
	
	ImageView getCheckViewer() {
		return _checkViewer;
	}
	
	void setImageViewLoc(ImageView view, double x, double y) {
		view.setLayoutX(x * GRID_FACTOR + TILE_PADDING);
		view.setLayoutY(y * GRID_FACTOR + TILE_PADDING);
	}

	int getY() {
		return _y;
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

	void moveDown(String letter) {
		if (_xIndex == -1 && _yIndex == -1) {
			_y++;
			_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
		} else {
			System.out.printf("\nNOT INCREMENTED! %s had indices (%s, %s)", letter, _xIndex, _yIndex);
		}
	}
	
	void moveUp(String letter) {
		if (_xIndex == -1 && _yIndex == -1) {
			_y--;
			_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_checkViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_minusViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
			_xViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
		} else {
			System.out.printf("\nNOT INCREMENTED! %s had indices (%s, %s)", letter, _xIndex, _yIndex);
		}
	}
	
	boolean isOnBoard() {
		return _tempPlacedOnBoard;
	}

	void reset() {
		_tileViewer.setLayoutX(_x * GRID_FACTOR + TILE_PADDING);
		_tileViewer.setLayoutY(_y * GRID_FACTOR + TILE_PADDING);
		_xIndex = -1;
		_yIndex = -1;
		_tempPlacedOnBoard = false;
	}
}