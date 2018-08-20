package Scrabble;

import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.control.Label;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.Font;

/**
 * 
 * Models an intelligent square on the Scrabble board by serving as a wrapper class around
 * a JavaFX rectangle. Identity (score multiplier) accessed in determining
 * move AI move calculation and human player score assignment.
 * 
 */
class BoardSquare {
	private Rectangle _square;
	private Label _label;
	private ScrabbleGame _scrabbleGame;
	
	private final int _x;
	private final int _y;
	private SquareIdentity _identity;
	
	private final Pane _boardPane;
	private final Pane _labelPane;
	
	private Boolean _labelIsShown;
	private Boolean _alreadyPlayed;
	
	private Boolean _is2W;
	private Boolean _is2L;
	private Boolean _is3W;
	private Boolean _is3L;
	private Boolean _isNormal;

	/**
	 *  @param x - width of square
	 * @param y - height of square
	 * @param boardPane - the pane displaying the graphical squares that comprise the board
	 * @param labelPane - the pane displaying the identifying white text of each board square
	 * 
	 */
	BoardSquare(int x, int y, Pane boardPane, Pane labelPane) {
		_x = x;
		_y = y;
		_identity = SquareIdentity.Normal;
		
		_boardPane = boardPane;
		_labelPane = labelPane;
		
		_labelIsShown = false;
		_alreadyPlayed = false;
		
		// Default booleans
		_is2W = false;
		_is2L = false;
		_is3W = false;
		_is3L = false;
		_isNormal = true;
		
		// Constructs the actual square
		this.formatSquare();
	}
	
	/**
	 * 
	 * @return whether or not the score multiplier has already been used. If true, the word or letter
	 * receives its normal value
	 * 
	 */
	boolean isAlreadyPlayed() {
		return _alreadyPlayed;
	}
	
	/**
	 *
	 * 
	 */
	void setAlreadyPlayed() {
		_alreadyPlayed = true;
	}

	/**
	 * 
	 * @return whether or not the board square exists as a double word multiplier
	 * 
	 */
	Boolean is2W() {
		return _is2W;
	}

	/**
	 * 
	 * @return whether or not the board square exists as a double letter multiplier
	 * 
	 */
	Boolean is2L() {
		return _is2L;
	}

	/**
	 * 
	 * @return whether or not the board square exists as a triple word multiplier
	 * 
	 */
	Boolean is3W() {
		return _is3W;
	}

	/**
	 * 
	 * @return whether or not the board square exists as a triple letter multiplier
	 * 
	 */
	Boolean is3L() { return _is3L; }

	/**
	 * 
	 * @return whether or not the board square has no affiliated score multiplier
	 * 
	 */
	Boolean isNormal() {
		return _isNormal;
	}

	/**
	 * 
	 * The de-facto "constructor" of the graphical board square, which relies on the
	 * constants class for formatting before adding the square to the board pane
	 * 
	 */
	private void formatSquare() {
		_square = new Rectangle(_x, _y, Constants.GRID_FACTOR, Constants.GRID_FACTOR);
		_square.setFill(Constants.BOARD_FILL);
		_square.setStroke(Color.BLACK);
		_square.setStrokeWidth(1);
		_boardPane.getChildren().add(_square);
	}

	/**
	 *
	 * Called post initialization to establish score multiplier squares 
	 * @param identity - the specific nature of the multiplier
	 * 
	 */
	void setID(SquareIdentity identity) {
		_isNormal = false;
		_alreadyPlayed = false;
		_identity = identity;

		switch (_identity) {

			case Normal:
				break;
			case DoubleLetterScore:
				_square.setFill(Constants.DOUBLE_LETTER_SCORE);
				_square.setOpacity(Constants.BOARD_OPACITY);
				_label = new Label("Lx2");
				_label.setLayoutX(_x + 8);
				_label.setLayoutY(_y + 12);
				_is2L = true;
				break;
			case TripleLetterScore:
				_square.setFill(Constants.TRIPLE_LETTER_SCORE);
				_square.setOpacity(Constants.BOARD_OPACITY);
				_label = new Label("Lx3");
				_label.setLayoutX(_x + 8);
				_label.setLayoutY(_y + 12);
				_is3L = true;
				break;
			case DoubleWordScore:
				_square.setFill(Constants.DOUBLE_WORD_SCORE);
				_square.setOpacity(Constants.BOARD_OPACITY);
				_label = new Label("Wx2");
				_label.setLayoutX(_x + 7);
				_label.setLayoutY(_y + 12);
				_is2W = true;
				break;
			case TripleWordScore:
				_square.setFill(Constants.TRIPLE_WORD_SCORE);
				_square.setOpacity(Constants.BOARD_OPACITY);
				_label = new Label("Wx3");
				_label.setLayoutX(_x + 7);
				_label.setLayoutY(_y + 12);
				_is3W = true;
				break;
			// Exists only as a transparent square to "cover" the diamond above the middle-most square
			case Ghost:
				_square.setFill(Color.BLACK);
				_square.setOpacity(0.0);
				break;
		}

		if (_label != null) {
			_label.setTextFill(Color.WHITE);
			_label.setOpacity(0.0);
			_label.setFont(Font.font("Courier New", 15));	
		}
	}

	/**
	 * 
	 * Uses a fade transition to display the multiplier text label
	 * 
	 */
	void showText() {
		if (_label == null) return;
		if (!_labelPane.getChildren().contains(_label)) _labelPane.getChildren().add(_label);

		FadeTransition fadeInLabel = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _label);
		fadeInLabel.setFromValue(0.0);
		fadeInLabel.setToValue(1.0);
		fadeInLabel.play();
	}

	/**
	 * 
	 * Uses a fade transition to conceal the multiplier text label
	 * 
	 */
	void concealText() {
		if (_label == null) return;

		FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _label);
		fadeOutLabel.setFromValue(1.0);
		fadeOutLabel.setToValue(0.0);
		fadeOutLabel.setOnFinished(new LabelFadeHandler());
		fadeOutLabel.play();
	}

	/**
	 * 
	 * @return - the nature of the score multiplier
	 * 
	 */
	SquareIdentity getIdentity() {
		return _identity;
	}

	/**
	 * 
	 * @return the underlying rectangle, called when displaying and concealing certain board squares
	 * 
	 */
	Rectangle getSquare() {
		return _square;
	}

	/**
	 *
	 * @param scrabbleGame - stored association, establishes the necessary event handlers
	 *
	 */
	void setUpHoverResponse(ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_square.addEventHandler(MouseEvent.MOUSE_PRESSED, new DisplayHandler());
		_square.addEventHandler(MouseEvent.MOUSE_RELEASED, new ConcealHandler());
	}

	private class DisplayHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (_scrabbleGame.getIsShiftDown() && !_scrabbleGame.getReferee().isThinking()) {
				_scrabbleGame.fadeOutOtherSquares(_identity);
				_labelIsShown = true;
			}
			event.consume();
		} 

	}

	private class ConcealHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (_labelIsShown) _scrabbleGame.fadeInOtherSquares(_identity);
			_labelIsShown = false;
			event.consume();
		} 

	}

	private class LabelFadeHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_labelPane.getChildren().remove(_label);
			event.consume();
		} 

	}

}