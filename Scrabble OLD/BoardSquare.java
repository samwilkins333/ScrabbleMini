package Scrabble;

import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.event.*;
import javafx.scene.input.*;
// import javafx.scene.effect.*;
import javafx.scene.control.Label;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.Font;

public class BoardSquare {
	private Rectangle _square;
	private Pane _boardPane;
	private Pane _labelPane;
	private int _x;
	private int _y;
	private String _id;
	private ScrabbleGame _thisGame;
	private Label _label;
	private Boolean _labelIsShown;
	private Boolean _is2W;
	private Boolean _is2L;
	private Boolean _is3W;
	private Boolean _is3L;
	private Boolean _isNormal;
	private boolean _alreadyPlayed;

	public BoardSquare(int x, int y, String id, Pane boardPane, Pane labelPane) {
		_x = x;
		_y = y;
		_id = id;
		_boardPane = boardPane;
		_labelPane = labelPane;
		_labelIsShown = false;
		_is2W = false;
		_is2L = false;
		_is3W = false;
		_is3L = false;
		_isNormal = true;
		_alreadyPlayed = false;
		this.formatSquare();
	}
	
	public boolean isAlreadyPlayed() {
		return _alreadyPlayed;
	}
	
	public void setAlreadyPlayed(boolean status) {
		_alreadyPlayed = status;
	}

	public Boolean is2W() {
		return _is2W;
	}

	public Boolean is2L() {
		return _is2L;
	}

	public Boolean is3W() {
		return _is3W;
	}

	public Boolean is3L() {
		return _is3L;
	}

	public Boolean isNormal() {
		return _isNormal;
	}

	public void formatSquare() {
		_square = new Rectangle(_x, _y, Constants.GRID_FACTOR, Constants.GRID_FACTOR);
		_square.setFill(Constants.BOARD_FILL);
		_square.setStroke(Color.BLACK);
		_square.setStrokeWidth(1);
		_boardPane.getChildren().add(_square);
	}

	public void setID(String id) {
		_isNormal = false;
		_alreadyPlayed = false;
		_id = id;
		if (_id == "DOUBLE LETTER SCORE") {
			_square.setFill(Constants.DOUBLE_LETTER_SCORE);
			_square.setOpacity(Constants.BOARD_OPACITY);
			_label = new Label("Lx2");
			_label.setLayoutX(_x + 8);
			_label.setLayoutY(_y + 12);
			_is2L = true;
		} else if (_id == "DOUBLE WORD SCORE") {
			_square.setFill(Constants.DOUBLE_WORD_SCORE);
			_square.setOpacity(Constants.BOARD_OPACITY);
			_label = new Label("Wx2");
			_label.setLayoutX(_x + 7);
			_label.setLayoutY(_y + 12);
			_is2W = true;
		} else if (_id == "TRIPLE LETTER SCORE") {
			_square.setFill(Constants.TRIPLE_LETTER_SCORE);
			_square.setOpacity(Constants.BOARD_OPACITY);
			_label = new Label("Lx3");
			_label.setLayoutX(_x + 8);
			_label.setLayoutY(_y + 12);
			_is3L = true;
		} else if (_id == "TRIPLE WORD SCORE") {
			_square.setFill(Constants.TRIPLE_WORD_SCORE);
			_square.setOpacity(Constants.BOARD_OPACITY);
			_label = new Label("Wx3");
			_label.setLayoutX(_x + 7);
			_label.setLayoutY(_y + 12);
			_is3W = true;
		} 
		else if (_id == "GHOST") {
			_square.setFill(Color.BLACK);
			_square.setOpacity(0.0);
		}
		if (_label != null) {
			_label.setTextFill(Color.WHITE);
			_label.setOpacity(0.0);
			_label.setFont(Font.font("Courier New", 15));	
		}
	}

	public void showText() {
		if (_label != null) {
			if (!_labelPane.getChildren().contains(_label)) {
				_labelPane.getChildren().add(_label);	
			}
			FadeTransition fadeInLabel = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _label);
			fadeInLabel.setFromValue(0.0);
			fadeInLabel.setToValue(1.0);
			fadeInLabel.play();	
		}
	}

	public void concealText() {
		if (_label != null) {
			FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(Constants.LABEL_ANIMATION), _label);
			fadeOutLabel.setFromValue(1.0);
			fadeOutLabel.setToValue(0.0);
			fadeOutLabel.setOnFinished(new LabelFadeHandler());
			fadeOutLabel.play();
		}
	}

	public void removeLabel() {
		_labelPane.getChildren().remove(_label);
		_label = null;
	}

	public String getID() {
		return _id;
	}

	public void setFill(Color color) {
		_square.setFill(color);
	}

	public Rectangle getSquare() {
		return _square;
	}

	public void setUpHoverResponse(ScrabbleGame thisGame) {
		_thisGame = thisGame;
		_square.addEventHandler(MouseEvent.MOUSE_PRESSED, new DisplayHandler());
		_square.addEventHandler(MouseEvent.MOUSE_RELEASED, new ConcealHandler());
	}

	private class DisplayHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (_thisGame.getIsShiftDown() && !_thisGame.getReferee().isThinking()) {
				_thisGame.fadeOutOtherSquares(_id);	
				_labelIsShown = true;
			}
			event.consume();
		} 

	}

	private class ConcealHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (_labelIsShown == true) {
				_thisGame.fadeInOtherSquares(_id);
			}
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