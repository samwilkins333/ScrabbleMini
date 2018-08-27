package Scrabble;

import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.util.ArrayList;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

public class PaneOrganizer {
	// ORIGINAL CONTENT
	private App _appClass;
	private Stage _stage;
	private Referee _referee;
	private ScrabbleGame _scrabbleGame;
	private Playable _playerOne;
	private Playable _playerTwo;
	private ShiftDisplayHandler _rotatePencil;
	private ShiftConcealHandler _revertPencil;
	// BASE TYPES
	private int _enterInt;
	private int _oneScore;
	private int _twoScore;
	private double _olds1;
	private double _olds2;
	private boolean _justFilled;
	private boolean _enterable;
	private boolean _displayMultipliers;
	private boolean _isShiftDown;
	private String _playerOneType;
	private String _playerTwoType;
	// DATA STRUCTURES
	private ArrayList<Label> _playedWords;

	private Pane _root;
	private Pane _boardPane;
	private Pane _wordContainer1;
	private Pane _wordContainer2;
	private Pane _scorePane;
	private ScrollPane _s1;
	private ScrollPane _s2;
	private Circle _aiCircle;
	private Label _playerOneScore;
	private Label _playerTwoScore;

	private Image _human;
	private Image _ai;
	private Image _accept;
	private Image _reject;
	private Image _thinking;
	private ImageView _bagViewer;
	private ImageView _postItOneViewer;
	private ImageView _postItTwoViewer;
	private ImageView _pencilViewer;
	private ImageView _hiViewer;
	private ImageView _leatherViewer;
	private ImageView _specsViewer;
	private ImageView _aiViewer;
	private ImageView _playerOneHi;
	private ImageView _playerTwoHi;
	private ImageView _leftHalf;
	private ImageView _rightHalf;
	private ImageView _playerOneIcon;
	private ImageView _playerTwoIcon;
	private ImageView _background;

	private Timeline _aiRotate;

	private FadeTransition _circleFade;

	private RotateTransition _rtBag;
	private RotateTransition _rtPencil;
	private RotateTransition _rotateM;
	private RotateTransition _rotateMCheck;
	private RotateTransition _rotateI1;
	private RotateTransition _rotateI1Check;
	private RotateTransition _rotateN;
	private RotateTransition _rotateNCheck;
	private RotateTransition _rotateI2;
	private RotateTransition _rotateI2Check;

	private ScaleTransition _aiShrink;
	private ScaleTransition _aiExpand;
	private ScaleTransition _circleExpand;
	private ScaleTransition _scaleM;
	private ScaleTransition _scaleMCheck;
	private ScaleTransition _scaleI1;
	private ScaleTransition _scaleI1Check;
	private ScaleTransition _scaleN;
	private ScaleTransition _scaleNCheck;
	private ScaleTransition _scaleI2;
	private ScaleTransition _scaleI2Check;

	private TranslateTransition _moveHi;
	private TranslateTransition _moveLeft;
	private TranslateTransition _moveRight;
	private TranslateTransition _moveM;
	private TranslateTransition _moveMCheck;
	private TranslateTransition _moveI1;
	private TranslateTransition _moveI1Check;
	private TranslateTransition _moveN;
	private TranslateTransition _moveNCheck;
	private TranslateTransition _moveI2;
	private TranslateTransition _moveI2Check;

	public PaneOrganizer(App appClass, Stage appStage) {
		_root = new Pane();
		@SuppressWarnings("unused")
		Constants constants = new Constants();

		_enterInt = 0;
		_enterable = false;
		_displayMultipliers = false;
		_isShiftDown = false;
		_playerOneType = Constants.PLAYER_ONE_START_STATE;
		_playerTwoType = Constants.PLAYER_TWO_START_STATE;

		this.setUpDesk();

		_boardPane = new Pane();
		_boardPane.requestFocus();
		_boardPane.setFocusTraversable(true);
		_boardPane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());

		this.addBag();

		_root.getChildren().add(_boardPane);
		_appClass = appClass;
		_stage = appStage;
		_rotatePencil = new ShiftDisplayHandler();
		_revertPencil = new ShiftConcealHandler();
		_boardPane.addEventHandler(KeyEvent.KEY_PRESSED, _rotatePencil);
		_boardPane.addEventHandler(KeyEvent.KEY_RELEASED, _revertPencil);
		_scrabbleGame = new ScrabbleGame(this, _root, _boardPane);

		this.addPostIts();
		this.addAIGraphics();
		this.addScrollPane();

		_oneScore = 0;
		_twoScore = 0;
		_playedWords = new ArrayList<Label>();

		this.setUpIntroSequence();

	}

	public Boolean getDisplayMultipliers() {
		return _displayMultipliers;
	}

	public void rotateBag() {
		_rtBag.play();
	}

	public void addScrollPane() {
		_scorePane = new Pane();
		_scorePane.setFocusTraversable(false);
		_scorePane.setVisible(false);
		_root.getChildren().addAll(_scorePane);
		_wordContainer1 = new Pane();
		Rectangle whiteRect1 = new Rectangle(0, 0, 315, 3000);
		whiteRect1.setFill(Color.WHITE);
		Rectangle whiteRect2 = new Rectangle(0, 0, 315, 3000);
		whiteRect2.setFill(Color.WHITE);
		_wordContainer1.getChildren().add(whiteRect1);
		_s1 = new ScrollPane();
		_s1.setOnMouseEntered(new ScrollOneRecallHandler());
		_s1.setPrefSize(7.5 * Constants.GRID_FACTOR, 15 * Constants.GRID_FACTOR);
		_s1.setContent(_wordContainer1);
		_s1.setPannable(true);
		_s1.setHbarPolicy(ScrollBarPolicy.NEVER);
		_s1.setVbarPolicy(ScrollBarPolicy.NEVER);
		_s1.setLayoutX(Constants.ZEROETH_COLUMN_OFFSET * Constants.GRID_FACTOR);
		_s1.setLayoutY(Constants.ZEROETH_ROW_OFFSET * Constants.GRID_FACTOR);
		_s1.setFocusTraversable(false);
		_wordContainer2 = new Pane();
		_wordContainer2.getChildren().add(whiteRect2);
		_s2 = new ScrollPane();
		_s2.setOnMouseEntered(new ScrollTwoRecallHandler());
		_s2.setPrefSize(7.5 * Constants.GRID_FACTOR, 15 * Constants.GRID_FACTOR);
		_s2.setContent(_wordContainer2);
		_s2.setPannable(true);
		_s2.setHbarPolicy(ScrollBarPolicy.NEVER);
		_s2.setVbarPolicy(ScrollBarPolicy.NEVER);
		_s2.setLayoutX(Constants.ZEROETH_COLUMN_OFFSET * Constants.GRID_FACTOR + 7.5 * Constants.GRID_FACTOR);
		_s2.setLayoutY(Constants.Y0 * Constants.GRID_FACTOR);
		_s2.setFocusTraversable(false);
		_scorePane.getChildren().addAll(_s1, _s2);
	}

	private void setUpIntroSequence() {
		_leftHalf = new ImageView(new Image("Images/Main Theme and GUI/introLeft.jpg"));
		_leftHalf.setPreserveRatio(true);
		_leftHalf.setCache(true);
		_leftHalf.setFitWidth(Constants.SCENE_WIDTH / 2);
		_leftHalf.setLayoutX(0);
		_leftHalf.setLayoutY(-100);
		_leftHalf.setOpacity(0);
		_rightHalf = new ImageView(new Image("Images/Main Theme and GUI/introRight.jpg"));
		_rightHalf.setPreserveRatio(true);
		_rightHalf.setCache(true);
		_rightHalf.setFitWidth(Constants.SCENE_WIDTH / 2);
		_rightHalf.setLayoutX(Constants.SCENE_WIDTH / 2);
		_rightHalf.setLayoutY(-100);
		_rightHalf.setOpacity(0);
		_background = new ImageView(new Image("Images/Main Theme and GUI/intro.jpg"));
		_background.setPreserveRatio(true);
		_background.setCache(true);
		_background.setFitWidth(Constants.SCENE_WIDTH);
		_background.setLayoutX(0);
		_background.setLayoutY(-100);
		_moveLeft = new TranslateTransition(Duration.seconds(2), _leftHalf);
		_moveLeft.setByX(-1 * Constants.SCENE_WIDTH / 2 - 2);
		_moveRight = new TranslateTransition(Duration.seconds(2), _rightHalf);
		_moveRight.setByX(Constants.SCENE_WIDTH / 2 + 2);
		_human = new Image("Images/AI/human.png");
		_ai = new Image("Images/AI/ai.png");
		_playerOneIcon = new ImageView(_ai);
		_playerOneIcon.setOpacity(0.0);
		_playerOneIcon.setPreserveRatio(true);
		_playerOneIcon.setCache(true);
		_playerOneIcon.setFitWidth(Constants.GRID_FACTOR * 3);
		_playerOneIcon.setLayoutX(200);
		_playerOneIcon.setLayoutY(Constants.SCENE_HEIGHT / 2 - Constants.GRID_FACTOR * 1.5);
		_playerOneIcon.setOnMouseClicked(new TogglePlayer(1));
		_playerOneIcon.setOnMouseEntered(new ScaleIcon(1, "IN"));
		_playerOneIcon.setOnMouseExited(new ScaleIcon(1, "OUT"));
		_playerTwoIcon = new ImageView(_human);
		_playerTwoIcon.setOpacity(0.0);
		_playerTwoIcon.setPreserveRatio(true);
		_playerTwoIcon.setCache(true);
		_playerTwoIcon.setFitWidth(Constants.GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutX(Constants.SCENE_WIDTH - 200 - Constants.GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutY(Constants.SCENE_HEIGHT / 2 - Constants.GRID_FACTOR * 1.5);
		_playerTwoIcon.setOnMouseClicked(new TogglePlayer(2));
		_playerTwoIcon.setOnMouseEntered(new ScaleIcon(2, "IN"));
		_playerTwoIcon.setOnMouseExited(new ScaleIcon(2, "OUT"));
		_root.getChildren().addAll(_background, _leftHalf, _rightHalf, _playerOneIcon, _playerTwoIcon);
		PauseTransition intro = new PauseTransition(Duration.seconds(2));
		intro.setOnFinished(new FadeHalvesHandler());
		intro.play();
		Tile m = new Tile(13);
		double x = Constants.X3 - 0.3;
		double y = Constants.Y12;
		m.add(_root, x, y, _scrabbleGame, PlayerNumber.None);
		m.getTileViewer().setOpacity(0.0);
		m.setCheckLoc(x, y);

		_scaleM = new ScaleTransition(Duration.seconds(0.6), m.getTileViewer());
		_scaleM.setByX(-0.60);
		_scaleM.setByY(-0.60);
		_scaleMCheck = new ScaleTransition(Duration.seconds(0.6), m.getCheckViewer());
		_scaleMCheck.setByX(-0.60);
		_scaleMCheck.setByY(-0.60);
		_rotateM = new RotateTransition(Duration.seconds(0.6), m.getTileViewer());
		_rotateM.setByAngle(14);
		_rotateMCheck = new RotateTransition(Duration.seconds(0.6), m.getCheckViewer());
		_rotateMCheck.setByAngle(14);
		_moveM = new TranslateTransition(Duration.seconds(0.6), m.getTileViewer());
		_moveM.setByX(24);
		_moveM.setByY(-128);
		_moveMCheck = new TranslateTransition(Duration.seconds(0.6), m.getCheckViewer());
		_moveMCheck.setByX(24);
		_moveMCheck.setByY(-128);
		FadeTransition fadeM = new FadeTransition(Duration.seconds(0.6), m.getTileViewer());
		fadeM.setFromValue(0.0);
		fadeM.setToValue(1.0);
		FadeTransition fadeOutM = new FadeTransition(Duration.seconds(0.6), m.getTileViewer());
		fadeOutM.setFromValue(1.0);
		fadeOutM.setToValue(0.0);
		fadeOutM.setOnFinished(new TileRemover(m));
		PauseTransition animateM = new PauseTransition(Duration.seconds(3.5));
		animateM.setOnFinished(new PlayFadeHandler(fadeM));
		animateM.play();
		PauseTransition removeM = new PauseTransition(Duration.seconds(6.5));
		removeM.setOnFinished(new PlayFadeHandler(fadeOutM));
		removeM.play();

		Tile i1 = new Tile(9);
		x = Constants.X4 - 0.35;
		i1.add(_root, x, y, _scrabbleGame, PlayerNumber.None);
		i1.getTileViewer().setOpacity(0.0);
		i1.setCheckLoc(x, y);

		_scaleI1 = new ScaleTransition(Duration.seconds(0.6), i1.getTileViewer());
		_scaleI1.setByX(-0.60);
		_scaleI1.setByY(-0.60);
		_scaleI1Check = new ScaleTransition(Duration.seconds(0.6), i1.getCheckViewer());
		_scaleI1Check.setByX(-0.60);
		_scaleI1Check.setByY(-0.60);
		_rotateI1 = new RotateTransition(Duration.seconds(0.6), i1.getTileViewer());
		_rotateI1.setByAngle(14);
		_rotateI1Check = new RotateTransition(Duration.seconds(0.6), i1.getCheckViewer());
		_rotateI1Check.setByAngle(14);
		_moveI1 = new TranslateTransition(Duration.seconds(0.6), i1.getTileViewer());
		_moveI1.setByX(0);
		_moveI1.setByY(-124);
		_moveI1Check = new TranslateTransition(Duration.seconds(0.6), i1.getCheckViewer());
		_moveI1Check.setByX(0);
		_moveI1Check.setByY(-124);
		FadeTransition fadeI1 = new FadeTransition(Duration.seconds(0.6), i1.getTileViewer());
		fadeI1.setFromValue(0.0);
		fadeI1.setToValue(1.0);
		FadeTransition fadeOutI1 = new FadeTransition(Duration.seconds(0.6), i1.getTileViewer());
		fadeOutI1.setFromValue(1.0);
		fadeOutI1.setToValue(0.0);
		fadeOutI1.setOnFinished(new TileRemover(i1));
		PauseTransition animateI1 = new PauseTransition(Duration.seconds(4));
		animateI1.setOnFinished(new PlayFadeHandler(fadeI1));
		animateI1.play();
		PauseTransition removeI1 = new PauseTransition(Duration.seconds(7));
		removeI1.setOnFinished(new PlayFadeHandler(fadeOutI1));
		removeI1.play();

		Tile n = new Tile(14);
		x = Constants.X5 - 0.35;
		n.add(_root, x, y, _scrabbleGame, PlayerNumber.None);
		n.getTileViewer().setOpacity(0.0);
		n.setCheckLoc(x, y);

		_scaleN = new ScaleTransition(Duration.seconds(0.6), n.getTileViewer());
		_scaleN.setByX(-0.60);
		_scaleN.setByY(-0.60);
		_scaleNCheck = new ScaleTransition(Duration.seconds(0.6), n.getCheckViewer());
		_scaleNCheck.setByX(-0.60);
		_scaleNCheck.setByY(-0.60);
		_rotateN = new RotateTransition(Duration.seconds(0.6), n.getTileViewer());
		_rotateN.setByAngle(14);
		_rotateNCheck = new RotateTransition(Duration.seconds(0.6), n.getCheckViewer());
		_rotateNCheck.setByAngle(14);
		_moveN = new TranslateTransition(Duration.seconds(0.6), n.getTileViewer());
		_moveN.setByX(-26);
		_moveN.setByY(-120);
		_moveNCheck = new TranslateTransition(Duration.seconds(0.6), n.getCheckViewer());
		_moveNCheck.setByX(-26);
		_moveNCheck.setByY(-120);
		FadeTransition fadeN = new FadeTransition(Duration.seconds(0.6), n.getTileViewer());
		fadeN.setFromValue(0.0);
		fadeN.setToValue(1.0);
		FadeTransition fadeOutN = new FadeTransition(Duration.seconds(0.6), n.getTileViewer());
		fadeOutN.setFromValue(1.0);
		fadeOutN.setToValue(0.0);
		fadeOutN.setOnFinished(new TileRemover(n));
		PauseTransition animateN = new PauseTransition(Duration.seconds(4.5));
		animateN.setOnFinished(new PlayFadeHandler(fadeN));
		animateN.play();
		PauseTransition removeN = new PauseTransition(Duration.seconds(7.5));
		removeN.setOnFinished(new PlayFadeHandler(fadeOutN));
		removeN.play();

		Tile i2 = new Tile(9);
		x = Constants.X6 - 0.35;
		i2.add(_root, x, y, _scrabbleGame, PlayerNumber.None);
		i2.getTileViewer().setOpacity(0.0);
		i2.setCheckLoc(x, y);

		_scaleI2 = new ScaleTransition(Duration.seconds(0.6), i2.getCheckViewer());
		_scaleI2.setByX(-0.60);
		_scaleI2.setByY(-0.60);
		_scaleI2Check = new ScaleTransition(Duration.seconds(0.6), i2.getTileViewer());
		_scaleI2Check.setByX(-0.60);
		_scaleI2Check.setByY(-0.60);
		_rotateI2 = new RotateTransition(Duration.seconds(0.6), i2.getTileViewer());
		_rotateI2.setByAngle(14);
		_rotateI2Check = new RotateTransition(Duration.seconds(0.6), i2.getCheckViewer());
		_rotateI2Check.setByAngle(14);
		_moveI2 = new TranslateTransition(Duration.seconds(0.6), i2.getTileViewer());
		_moveI2.setByX(-51);
		_moveI2.setByY(-115);
		_moveI2.setOnFinished(new IntroFlashHandler(m, i1, n, i2));
		_moveI2Check = new TranslateTransition(Duration.seconds(0.6), i2.getCheckViewer());
		_moveI2Check.setByX(-51);
		_moveI2Check.setByY(-115);
		FadeTransition fadeI2 = new FadeTransition(Duration.seconds(0.6), i2.getTileViewer());
		fadeI2.setFromValue(0.0);
		fadeI2.setToValue(1.0);
		fadeI2.setOnFinished(new TileAnimator());
		FadeTransition fadeOutI2 = new FadeTransition(Duration.seconds(0.6), i2.getTileViewer());
		fadeOutI2.setFromValue(1.0);
		fadeOutI2.setToValue(0.0);
		fadeOutI2.setOnFinished(new TileRemover(i2));
		PauseTransition animateI2 = new PauseTransition(Duration.seconds(5));
		animateI2.setOnFinished(new PlayFadeHandler(fadeI2));
		animateI2.play();
		PauseTransition removeI2 = new PauseTransition(Duration.seconds(8));
		removeI2.setOnFinished(new PlayFadeHandler(fadeOutI2));
		removeI2.play();

		PauseTransition setUpPlayers = new PauseTransition(Duration.seconds(9));
		setUpPlayers.setOnFinished(new SetUpPlayers());
		setUpPlayers.play();

		PauseTransition enterable = new PauseTransition(Duration.seconds(9.5));
		enterable.setOnFinished(new Enterable());
		enterable.play();

	}

	private class TileAnimator implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_scaleM.play();
			_rotateM.play();
			_moveM.play();
			_scaleI1.play();
			_rotateI1.play();
			_moveI1.play();
			_scaleN.play();
			_rotateN.play();
			_moveN.play();
			_scaleI2.play();
			_rotateI2.play();
			_moveI2.play();
			_scaleMCheck.play();
			_rotateMCheck.play();
			_moveMCheck.play();
			_scaleI1Check.play();
			_rotateI1Check.play();
			_moveI1Check.play();
			_scaleNCheck.play();
			_rotateNCheck.play();
			_moveNCheck.play();
			_scaleI2Check.play();
			_rotateI2Check.play();
			_moveI2Check.play();
			event.consume();
		}

	}

	private class TileRemover implements EventHandler<ActionEvent> {
		private Tile _toRemove;

		public TileRemover(Tile toRemove) {
			_toRemove = toRemove;
		}

		@Override
		public void handle(ActionEvent event) {
			_root.getChildren().remove(_toRemove.getTileViewer());
			event.consume();
		}

	}

	private class Enterable implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_enterable = true;
			event.consume();
		}

	}

	private class ScaleIcon implements EventHandler<MouseEvent> {
		private int _id;
		private String _direction;

		private ScaleIcon(int id, String direction) {
			_id = id;
			_direction = direction;
		}

		@Override
		public void handle(MouseEvent event) {
			if (_id == 1) {
				if (_direction == "IN") {
					_playerOneIcon.setScaleX(1.1);
					_playerOneIcon.setScaleY(1.1);
				} else {
					_playerOneIcon.setScaleX(1);
					_playerOneIcon.setScaleY(1);
				}
			} else {
				if (_direction == "IN") {
					_playerTwoIcon.setScaleX(1.1);
					_playerTwoIcon.setScaleY(1.1);
				} else {
					_playerTwoIcon.setScaleX(1);
					_playerTwoIcon.setScaleY(1);
				}
			}
			event.consume();
		}

	}

	private class TogglePlayer implements EventHandler<MouseEvent> {
		private int _id;

		public TogglePlayer(int id) {
			_id = id;
		}

		@Override
		public void handle(MouseEvent event) {
			if (_id == 1) {
				if (_playerOneType == "HUMAN") {
					_playerOneType = "COMPUTER";
					_playerOneIcon.setImage(_ai);
					System.out.println(_playerOneType);
				} else if (_playerOneType == "COMPUTER") {
					_playerOneType = "HUMAN";
					_playerOneIcon.setImage(_human);
					System.out.println(_playerOneType);
				}
			} else if (_id == 2) {
				if (_playerTwoType == "HUMAN") {
					_playerTwoType = "COMPUTER";
					_playerTwoIcon.setImage(_ai);
					System.out.println(_playerTwoType);
				} else if (_playerTwoType == "COMPUTER") {
					_playerTwoType = "HUMAN";
					_playerTwoIcon.setImage(_human);
					System.out.println(_playerTwoType);
				}
			}
			event.consume();
		}

	}

	private class SetUpPlayers implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			FadeTransition fadeInOne = new FadeTransition(Duration.seconds(0.6), _playerOneIcon);
			fadeInOne.setFromValue(0.0);
			fadeInOne.setToValue(1.0);
			fadeInOne.play();
			FadeTransition fadeInTwo = new FadeTransition(Duration.seconds(0.6), _playerTwoIcon);
			fadeInTwo.setFromValue(0.0);
			fadeInTwo.setToValue(1.0);
			fadeInTwo.play();
			event.consume();
		}

	}

	private class PlayFadeHandler implements EventHandler<ActionEvent> {
		private FadeTransition _fade;

		public PlayFadeHandler(FadeTransition fade) {
			_fade = fade;
		}

		@Override
		public void handle(ActionEvent event) {
			_fade.play();
			event.consume();
		}

	}

	private class FadeHalvesHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			FadeTransition fadeLeft = new FadeTransition(Duration.seconds(1), _leftHalf);
			fadeLeft.setFromValue(0.0);
			fadeLeft.setToValue(1.0);
			fadeLeft.play();
			FadeTransition fadeRight = new FadeTransition(Duration.seconds(1), _rightHalf);
			fadeRight.setFromValue(0.0);
			fadeRight.setToValue(1.0);
			fadeRight.play();
			fadeRight.setOnFinished(new RemoveIntroHandler());
			event.consume();
		}

	}

	private class RemoveIntroHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_root.getChildren().remove(_background);
			event.consume();
		}

	}

	public void displayScoreLabels() {
		_playerOneScore.setOpacity(1.0);
		_playerOneScore.setOpacity(2.0);
	}

	public void updateScore() {
		int digitmult = 0;
		int oneScore = _referee.getPlayerOneScore();
		if (oneScore > 9 && _oneScore <= 9) {
			digitmult = 1;
		} else if (oneScore > 99 && _oneScore <= 99) {
			digitmult = 1;
		}
		_playerOneScore.setLayoutX(_playerOneScore.getLayoutX() - 14 * digitmult);
		_oneScore = oneScore;

		digitmult = 0;
		int twoScore = _referee.getPlayerTwoScore();
		if (twoScore > 9 && _twoScore <= 9) {
			digitmult = 1;
		} else if (twoScore > 99 && _twoScore <= 99) {
			digitmult = 1;
		}
		_playerTwoScore.setLayoutX(_playerTwoScore.getLayoutX() - 14 * digitmult);
		_twoScore = twoScore;

		_playerOneScore.setText(String.valueOf(oneScore));
		_playerTwoScore.setText(String.valueOf(twoScore));
	}

	public void updateLabels() {
		if (_playedWords.size() > 0) {
			for (int i = 0; i < _playedWords.size(); i++) {
				_playedWords.get(i).setTextFill(Constants.GRAPHITE);
			}
		}
	}

	public void addAIGraphics() {
		_accept = new Image("Images/AI/aiaccept.png");
		_reject = new Image("Images/AI/aireject.png");
		_thinking = new Image("Images/AI/aithinking.png");
		_aiViewer = new ImageView(_thinking);
		_aiViewer.setOpacity(0.0);
		_aiViewer.setCache(true);
		_aiViewer.setPreserveRatio(true);
		_aiViewer.setFitWidth(Constants.GRID_FACTOR * 1.2);
		_aiViewer.setLayoutX(Constants.GRID_FACTOR * 32.5);
		_aiViewer.setLayoutY(Constants.GRID_FACTOR * 17.2);
		_aiCircle = new Circle();
		_aiCircle.setRadius(Constants.GRID_FACTOR * 1);
		_aiCircle.setFill(Constants.GREEN);
		_aiCircle.setCenterX(Constants.GRID_FACTOR * 32.5 + Constants.GRID_FACTOR * 0.6);
		_aiCircle.setLayoutY(Constants.GRID_FACTOR * 17.2 + Constants.GRID_FACTOR * 0.6 + 3);
		_aiCircle.setOpacity(0.0);
		_boardPane.getChildren().addAll(_aiCircle, _aiViewer);

		_aiExpand = new ScaleTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _aiViewer);
		_aiExpand.setFromX(1 / 4);
		_aiExpand.setFromY(1 / 4);
		_aiExpand.setToX(1);
		_aiExpand.setToY(1);
		_aiExpand.setCycleCount(1);

		_aiShrink = new ScaleTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _aiViewer);
		_aiShrink.setFromX(1);
		_aiShrink.setFromY(1);
		_aiShrink.setToX(1 / 4);
		_aiShrink.setToY(1 / 4);
		_aiShrink.setCycleCount(1);
		_aiShrink.setOnFinished(new ResetAIHandler());

		_aiRotate = new Timeline();
		KeyFrame rotate = new KeyFrame(Duration.seconds(Constants.AI_ROTATE_DURATION), new RotateHandler());
		_aiRotate.getKeyFrames().add(rotate);
		_aiRotate.setCycleCount(Animation.INDEFINITE);

		_circleExpand = new ScaleTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _aiCircle);
		_circleExpand.setFromX(0.5);
		_circleExpand.setFromY(0.5);
		_circleExpand.setToX(1.2);
		_circleExpand.setToY(1.2);

		_circleFade = new FadeTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _aiCircle);
		_circleFade.setFromValue(1.0);
		_circleFade.setToValue(0.0);
	}

	public void fadeInAI() {
		FadeTransition aiFade = new FadeTransition(Duration.seconds(0.05), _aiViewer);
		aiFade.setFromValue(0.0);
		aiFade.setToValue(1.0);
		aiFade.setOnFinished(new AnimateAIHandler());
		aiFade.play();
		_aiViewer.setOpacity(0);
		_aiExpand.play();
	}

	public void showAIOutcome(String id) {
		if (id == "ACCEPT") {
			_aiCircle.setFill(Constants.GREEN);
			_aiShrink.stop();
			_aiRotate.stop();
			_circleExpand.play();
			_circleFade.play();
			_aiViewer.setImage(_accept);
			_aiViewer.setRotate(0);
			_aiViewer.setScaleX(1);
			_aiViewer.setScaleY(1);
		} else if (id == "REJECT") {
			_aiCircle.setFill(Constants.RED);
			_aiShrink.stop();
			_aiRotate.stop();
			_circleExpand.play();
			_circleFade.play();
			_aiViewer.setImage(_reject);
			_aiViewer.setRotate(0);
			_aiViewer.setScaleX(1);
			_aiViewer.setScaleY(1);
		}
	}

	public void fadeOutAI() {
		_aiShrink.play();
	}

	private class AnimateAIHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			// system.out.println("ANIMATE HANDLED");
			_aiRotate.play();
			event.consume();
		}

	}

	private class RotateHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_aiViewer.setRotate(_aiViewer.getRotate() + 60);
			event.consume();
		}

	}

	private class IntroFlashHandler implements EventHandler<ActionEvent> {
		private Tile _tileM;
		private Tile _tileI1;
		private Tile _tileN;
		private Tile _tileI2;

		private IntroFlashHandler(Tile tileM, Tile tileI1, Tile tileN, Tile tileI2) {
			_tileM = tileM;
			_tileI1 = tileI1;
			_tileN = tileN;
			_tileI2 = tileI2;
		}

		@Override
		public void handle(ActionEvent event) {
			_tileM.playFlash("ADDED");
			_tileI1.playFlash("ADDED");
			_tileN.playFlash("ADDED");
			_tileI2.playFlash("ADDED");
			event.consume();
		}

	}

	private class ResetAIHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_aiViewer.setRotate(0);
			_aiViewer.setImage(_thinking);
			event.consume();
		}

	}

	public void addPostIts() {
		_postItOneViewer = new ImageView(new Image("Images/Main Theme and GUI/greenpostit.png"));
		_postItOneViewer.setCache(true);
		_postItOneViewer.setPreserveRatio(true);
		_postItOneViewer.setFitWidth(Constants.GRID_FACTOR * 4);
		_postItOneViewer.setLayoutX(Constants.GRID_FACTOR * 4);
		_postItOneViewer.setLayoutY(Constants.GRID_FACTOR * 5);
		_postItOneViewer.setOnMousePressed(new ScorePaneHandler("IN"));
		_postItOneViewer.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_postItTwoViewer = new ImageView(new Image("Images/Main Theme and GUI/bluepostit.png"));
		_postItTwoViewer.setCache(true);
		_postItTwoViewer.setPreserveRatio(true);
		_postItTwoViewer.setFitWidth(Constants.GRID_FACTOR * 4);
		_postItTwoViewer.setLayoutX(Constants.GRID_FACTOR * 6);
		_postItTwoViewer.setLayoutY(Constants.GRID_FACTOR * 3);
		_postItTwoViewer.setOnMousePressed(new ScorePaneHandler("IN"));
		_postItTwoViewer.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerOneScore = new Label();
		_playerOneScore.setOpacity(1.0);
		_playerOneScore.setTextFill(Constants.GRAPHITE);
		// System.out.println(16);
		_playerOneScore.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_POST_IT));
		_playerOneScore.setLayoutY(Constants.GRID_FACTOR * 6.1);
		_playerOneScore.setLayoutX(Constants.GRID_FACTOR * 5.7);
		// _playerOneScore.setRotate(-15);
		_playerOneScore.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerOneScore.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerOneHi = new ImageView(new Image("Images/Main Theme and GUI/highlight.png"));
		_playerOneHi.setPreserveRatio(true);
		_playerOneHi.setCache(true);
		_playerOneHi.setFitWidth(90);
		_playerOneHi.setLayoutX(Constants.GRID_FACTOR * 5.1 - 5);
		_playerOneHi.setLayoutY(Constants.GRID_FACTOR * 6.5);
		_playerOneHi.setOpacity(0.0);
		_playerOneHi.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerOneHi.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerTwoScore = new Label();
		_playerTwoScore.setOpacity(1.0);
		// _playerTwoScore.setRotate(15);
		_playerTwoScore.setTextFill(Constants.GRAPHITE);
		_playerTwoScore.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_POST_IT));
		_playerTwoScore.setLayoutY(Constants.GRID_FACTOR * 4.1);
		_playerTwoScore.setLayoutX(Constants.GRID_FACTOR * 7.9);
		_playerTwoScore.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerTwoScore.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerTwoHi = new ImageView(new Image("Images/Main Theme and GUI/highlight.png"));
		_playerTwoHi.setPreserveRatio(true);
		_playerTwoHi.setCache(true);
		_playerTwoHi.setFitWidth(90);
		_playerTwoHi.setLayoutX(Constants.GRID_FACTOR * 7.3 - 5);
		_playerTwoHi.setLayoutY(Constants.GRID_FACTOR * 4.5);
		_playerTwoHi.setOpacity(0.0);
		_playerTwoHi.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerTwoHi.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_pencilViewer = new ImageView(new Image("Images/Main Theme and GUI/pencil.png"));
		_pencilViewer.setCache(true);
		_pencilViewer.setPreserveRatio(true);
		_pencilViewer.setFitWidth(Constants.GRID_FACTOR * 4);
		_pencilViewer.setLayoutX(Constants.GRID_FACTOR * 30.4);
		_pencilViewer.setLayoutY(Constants.GRID_FACTOR * 6);
		_pencilViewer.setRotate(60);
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(Constants.SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_pencilViewer.setEffect(pieceShadow);
		// _pencilViewer.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
		// _pencilViewer.addEventHandler(KeyEvent.KEY_RELEASED, new KeyHandler());
		_rtPencil = new RotateTransition(Duration.millis(190), _pencilViewer);
		_rtPencil.setByAngle(15);
		_rtPencil.setCycleCount(2);
		_rtPencil.setAutoReverse(true);
		_hiViewer = new ImageView(new Image("Images/Main Theme and GUI/hi.png"));
		_hiViewer.setCache(true);
		_hiViewer.setPreserveRatio(true);
		_hiViewer.setFitWidth(Constants.GRID_FACTOR * 1.4);
		_hiViewer.setLayoutX(Constants.GRID_FACTOR * 5 - 400);
		_hiViewer.setLayoutY(Constants.GRID_FACTOR * 7);
		_hiViewer.setRotate(-39);
		DropShadow hiShadow = new DropShadow();
		hiShadow.setRadius(50);
		hiShadow.setOffsetX(3);
		hiShadow.setOffsetY(3);
		hiShadow.setColor(Color.web("#444444"));
		hiShadow.setSpread(0.0);
		hiShadow.setHeight(40);
		hiShadow.setWidth(40);
		hiShadow.setBlurType(BlurType.GAUSSIAN);
		_hiViewer.setEffect(hiShadow);
		_moveHi = new TranslateTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _hiViewer);
		_moveHi.setByX(400);
		_boardPane.getChildren().addAll(_postItOneViewer, _postItTwoViewer, _playerOneHi, _playerTwoHi, _pencilViewer,
				_playerOneScore, _playerTwoScore, _hiViewer);
	}

	private class HighlightHandler implements EventHandler<ActionEvent> {
		private String _winner;

		public HighlightHandler(String winner) {
			_winner = winner;
		}

		@Override
		public void handle(ActionEvent event) {
			if (_winner == "PLAYER ONE") {
				_playerOneHi.setOpacity(0.8);
			} else if (_winner == "PLAYER TWO") {
				_playerTwoHi.setOpacity(0.8);
			} else if (_winner == "TIE") {
				_playerOneHi.setOpacity(0.8);
				_playerTwoHi.setOpacity(0.8);
			}
			event.consume();
		}

	}

	public void addBag() {
		_bagViewer = new ImageView(new Image("Images/Main Theme and GUI/bag.png"));
		_bagViewer.setCache(true);
		_bagViewer.setPreserveRatio(true);
		_bagViewer.setFitWidth(Constants.GRID_FACTOR * 7);
		_bagViewer.setLayoutX(Constants.GRID_FACTOR * 0);
		_bagViewer.setLayoutY(Constants.GRID_FACTOR * 10);
		_bagViewer.setRotate(45);
		// _bagViewer.addEventHandler(MouseEvent.MOUSE_CLICKED, new DrawHandler());
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(Constants.SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_bagViewer.setEffect(pieceShadow);
		_rtBag = new RotateTransition(Duration.millis(190), _bagViewer);
		_rtBag.setByAngle(15);
		_rtBag.setCycleCount(4);
		_rtBag.setAutoReverse(true);
		_root.getChildren().add(_bagViewer);
	}

	public void addSpecs() {
		_specsViewer = new ImageView(new Image("specs.png"));
		_specsViewer.setCache(true);
		_specsViewer.setPreserveRatio(true);
		_specsViewer.setFitWidth(Constants.GRID_FACTOR * 9);
		_specsViewer.setLayoutX(Constants.GRID_FACTOR * 30.2);
		_specsViewer.setLayoutY(Constants.GRID_FACTOR * 7);
		_specsViewer.setRotate(45);
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(Constants.SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_specsViewer.setEffect(pieceShadow);
		_root.getChildren().add(_specsViewer);
	}

	public void setUpDesk() {
		// Rectangle backgroundRect = new Rectangle(13 * Constants.GRID_FACTOR, 3 *
		// Constants.GRID_FACTOR, Constants.GRID_FACTOR * 15, Constants.GRID_FACTOR *
		// 15);
		// backgroundRect.setFill(Color.web("#CC2244"));
		_leatherViewer = new ImageView(new Image("Images/Main Theme and GUI/leatherframe - Original.png"));
		_leatherViewer.setCache(true);
		_leatherViewer.setPreserveRatio(false);
		_leatherViewer.setFitWidth(Constants.GRID_FACTOR * 15.5);
		_leatherViewer.setFitHeight(Constants.GRID_FACTOR * 15.5);
		_leatherViewer.setLayoutX(Constants.GRID_FACTOR * (Constants.ZEROETH_COLUMN_OFFSET - 0.25) - 1);
		_leatherViewer.setLayoutY(Constants.GRID_FACTOR * (Constants.ZEROETH_ROW_OFFSET - 0.25) - 1);
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(Constants.SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);
		_leatherViewer.setEffect(pieceShadow);
		ImageView deskViewer = new ImageView(new Image("Images/Main Theme and GUI/desktop.jpg"));
		deskViewer.setCache(true);
		deskViewer.setPreserveRatio(true);
		deskViewer.setFitWidth(Constants.SCENE_WIDTH);
		deskViewer.setLayoutX(0);
		deskViewer.setLayoutY(-150);
		_root.getChildren().addAll(deskViewer, _leatherViewer);
	}

	public void setEnterable(Boolean status) {
		_enterable = status;
	}

	public void resetEnterInt() {
		_enterInt = 1;
	}

	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			KeyCode keyPressed = event.getCode();
			if (keyPressed == KeyCode.Q) { // Quits game with "Q"
				System.exit(0);
			} else if (keyPressed == KeyCode.ESCAPE) { // Quits game with "Q"
				if (_referee != null
						&& _referee.getCurrentPlayer()
								.getPlayerType() == "HUMAN" /* && _referee.firstMoveMade() != false */
						&& event.isMetaDown()) {
					_referee.nextMove();
				}
			} else if (keyPressed == KeyCode.SPACE) { // Quits game with "Q"
				if (_referee != null && _referee.getCurrentPlayer().getPlayerType() == "HUMAN") {
					if (_referee.currentPlayer() == "PLAYER ONE") {
						_scrabbleGame.shuffleRack(1);
					} else if (_referee.currentPlayer() == "PLAYER TWO") {
						_scrabbleGame.shuffleRack(2);
					}
				}
			} else if (keyPressed == KeyCode.ENTER && _enterable == true) {
				System.out.printf("ENTER INT FROM THE TOP 1017 = %s\n", _enterInt);
				if (_enterInt == 0) {
					_moveLeft.play();
					_moveRight.play();
					TranslateTransition moveLeftIcon = new TranslateTransition(Duration.seconds(2), _playerOneIcon);
					moveLeftIcon.setByX(-1 * Constants.SCENE_WIDTH / 2 - 2);
					moveLeftIcon.play();
					TranslateTransition moveRightIcon = new TranslateTransition(Duration.seconds(2), _playerTwoIcon);
					moveRightIcon.setByX(Constants.SCENE_WIDTH / 2 + 2);
					moveRightIcon.play();
					ScaleTransition scaleOne = new ScaleTransition(Duration.seconds(0.15), _playerOneIcon);
					scaleOne.setByX(0.3);
					scaleOne.setByY(0.3);
					scaleOne.setCycleCount(2);
					scaleOne.setAutoReverse(true);
					scaleOne.play();
					ScaleTransition scaleTwo = new ScaleTransition(Duration.seconds(0.15), _playerTwoIcon);
					scaleTwo.setByX(0.3);
					scaleTwo.setByY(0.3);
					scaleTwo.setAutoReverse(true);
					scaleTwo.setCycleCount(2);
					scaleTwo.play();
					FadeTransition fadeLeft = new FadeTransition(Duration.seconds(1), _leftHalf);
					fadeLeft.setFromValue(1.0);
					fadeLeft.setToValue(0.0);
					// fadeLeft.play();
					FadeTransition fadeRight = new FadeTransition(Duration.seconds(1), _rightHalf);
					fadeRight.setFromValue(1.0);
					fadeRight.setToValue(0.0);
					// fadeRight.play();
					_scrabbleGame.startGamePlay();
					if (_playerOneType == "HUMAN") {
						_playerOne = new HumanPlayer(PlayerNumber.One, _scrabbleGame);
					} else if (_playerOneType == "COMPUTER") {
						_playerOne = new ComputerPlayer(PlayerNumber.One, _scrabbleGame);
					}
					if (_playerTwoType == "HUMAN") {
						_playerTwo = new HumanPlayer(PlayerNumber.Two, _scrabbleGame);
					} else if (_playerTwoType == "COMPUTER") {
						_playerTwo = new ComputerPlayer(PlayerNumber.Two, _scrabbleGame);
					}
					_referee = new Referee(_scrabbleGame, _playerOne, _playerTwo);
					_scrabbleGame.addReferee(_referee);
					_rtPencil.play();
					PaneOrganizer.this.updateScore();
					// _scrabbleGame.setUpTiles();
				} else if (_enterInt > 0 && event.isMetaDown()) {
					System.out.printf("\nHANDLE, ENTER INT = %s\n", _enterInt);
					Word newestWord = _referee.getCurrentPlayer().getNewestWord();
					if (newestWord.isPlayable()) {
						// system.out.printf("ADDING TO BOARD on enterInt %s\n", _enterInt);
						newestWord.addToBoard();
						if (_referee.firstMoveMade() == false) {
							_referee.setFirstMoveIsMade();
						}
						_referee.incrementWordsPlayedBy(1);
						PaneOrganizer.this.updateLabels();
						Label word = new Label(newestWord.getLetters());
						word.setTextFill(Constants.GREEN);
						word.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
						word.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
						word.setLayoutX(15);
						_playedWords.add(word);
						_rtPencil.play();
						Label value = new Label(String.valueOf(newestWord.getValue()));
						_playedWords.add(value);
						int digitmult = 1;
						int wordValue = newestWord.getValue();
						if (wordValue > 9) {
							digitmult = 2;
						} else if (wordValue > 99) {
							digitmult = 3;
						}
						value.setTextFill(Constants.GREEN);
						value.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
						value.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
						value.setLayoutX(315 - 15 - 25 * digitmult);

						if (_referee.currentPlayer() == "PLAYER ONE") {
							_wordContainer1.getChildren().addAll(word, value);
						} else if (_referee.currentPlayer() == "PLAYER TWO") {
							_wordContainer2.getChildren().addAll(word, value);
						}
						ArrayList<Word> crosses = newestWord.getAllCrosses();
						for (int i = 0; i < crosses.size(); i++) {
							digitmult = 1;
							Word thisCross = crosses.get(i);
							Label newCrossWord = new Label(thisCross.getLetters());
							newCrossWord.setTextFill(Constants.GREEN);
							newCrossWord.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
							newCrossWord
									.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
							newCrossWord.setLayoutX(15);
							_playedWords.add(newCrossWord);
							int crossValue = thisCross.getValue();
							_referee.addToScore(crossValue);
							if (crossValue > 9) {
								digitmult = 2;
							} else if (crossValue > 99) {
								digitmult = 3;
							}
							Label newCrossValue = new Label(String.valueOf(crossValue));
							_playedWords.add(newCrossValue);
							newCrossValue.setTextFill(Constants.GREEN);
							newCrossValue.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
							newCrossValue
									.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
							newCrossValue.setLayoutX(315 - 15 - 25 * digitmult);
							if (_referee.currentPlayer() == "PLAYER ONE") {
								_wordContainer1.getChildren().addAll(newCrossWord, newCrossValue);
							} else if (_referee.currentPlayer() == "PLAYER TWO") {
								_wordContainer2.getChildren().addAll(newCrossWord, newCrossValue);
							}
						}
						_referee.incrementWordsPlayedBy(crosses.size());
						int y = 9 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed());
						Line line = new Line(0, y, 3, y);
						line.setStroke(Color.BLACK);
						line.setStrokeWidth(3);
						line.setStrokeLineCap(StrokeLineCap.ROUND);
						line.setOpacity(0.7);
						if (_referee.currentPlayer() == "PLAYER ONE") {
							_wordContainer1.getChildren().add(line);
						} else if (_referee.currentPlayer() == "PLAYER TWO") {
							_wordContainer2.getChildren().add(line);
						}
						PaneOrganizer.this.updateScore();
						newestWord.playFlashes("ADDED");
						for (int i = 0; i < crosses.size(); i++) {
							PauseTransition delayFlash = new PauseTransition(Duration.seconds(0.5 * (i + 1)));
							delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), "ADDED"));
							delayFlash.play();
						}
						_enterable = false;
						_referee.nextMove();
					} else {
						if (newestWord.isInDictionary() && newestWord.isFormatted()) {
							newestWord.playFlashes("PARTIAL");
						} else {
							if (!newestWord.isInDictionary()) {
								System.out.printf("%s NOT IN DICTIONARY\n", newestWord.getLetters());
							}
							if (!newestWord.isFormatted()) {
								System.out.printf("%s NOT FORMATTED\n", newestWord.getLetters());
							}
							newestWord.playFlashes("FAILED");
						}
						ArrayList<Word> crosses = newestWord.getAllCrosses();
						if (crosses != null) {
							if (crosses.size() > 0) {
								for (int i = 0; i < crosses.size(); i++) {
									String validity = "";
									if (crosses.get(i).isInDictionary()) {
										validity = "ADDED";
									} else {
										validity = "FAILED";
									}
									PauseTransition delayFlash = new PauseTransition(
											Duration.seconds(Constants.FLASH_SPACING_DURATION * (i + 1)));
									delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), validity));
									delayFlash.play();
								}
							}
						}
					}
				}
				_enterInt++;
				System.out.printf("ENTER INT FROM THE BOTTOM 1181 = %s\n", _enterInt);
			} else if (keyPressed == KeyCode.R) {
				PaneOrganizer.this.reset();
			} else if (keyPressed == KeyCode.A) {
				boolean autoReset = _scrabbleGame.getAutoReset();
				if (autoReset == true) {
					_scrabbleGame.setAutoReset(false);
					System.out.println("AUTO RESET OFF");
				} else if (autoReset == false) {
					_scrabbleGame.setAutoReset(true);
					System.out.println("AUTO RESET ON");
				}
			} else if (keyPressed == KeyCode.M) {
				if (_displayMultipliers == false) {
					_scrabbleGame.fadeOutOtherSquares("GHOST");
					_displayMultipliers = true;
					_scrabbleGame.removeDiamond();
					// PaneOrganizer.this.playFlash();
				} else if (_displayMultipliers == true) {
					_displayMultipliers = false;
					_scrabbleGame.fadeInOtherSquares("GHOST");
					_scrabbleGame.addDiamond();
				}
			} else if (keyPressed == KeyCode.S) {
			} else if (keyPressed == KeyCode.DIGIT1) {
				if (_playerOneType == "HUMAN") {
					_playerOneType = "COMPUTER";
					_playerOneIcon.setImage(_ai);
					System.out.println(_playerOneType);
				} else if (_playerOneType == "COMPUTER") {
					_playerOneType = "HUMAN";
					_playerOneIcon.setImage(_human);
					System.out.println(_playerOneType);
				}
			} else if (keyPressed == KeyCode.DIGIT2) {
				if (_playerTwoType == "HUMAN") {
					_playerTwoType = "COMPUTER";
					_playerTwoIcon.setImage(_ai);
					System.out.println(_playerTwoType);
				} else if (_playerTwoType == "COMPUTER") {
					_playerTwoType = "HUMAN";
					_playerTwoIcon.setImage(_human);
					System.out.println(_playerTwoType);
				}
			} else if (keyPressed == KeyCode.C) { // Quits game with "Q"
				_scrabbleGame.getReferee().getCurrentPlayer().getNewestWord().clear();
				_scrabbleGame.resetRackOne();
				_scrabbleGame.resetRackTwo();
				System.out.printf("There are %s tiles on the board\n", _scrabbleGame.getTilesOnBoard().size());
			}
			event.consume();
		}

	}

	public void addWordAI(Word newestWord) {
		// system.out.printf("ADDING TO BOARD on enterInt %s\n", _enterInt);
		newestWord.addToBoardAI();
		_referee.incrementWordsPlayedBy(1);
		PaneOrganizer.this.updateLabels();
		Label word = new Label(newestWord.getLetters());
		word.setTextFill(Constants.GREEN);
		word.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
		word.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
		word.setLayoutX(15);
		_playedWords.add(word);
		_rtPencil.play();
		Label value = new Label(String.valueOf(newestWord.getOriginalValue()));
		_playedWords.add(value);
		int digitmult = 1;
		int wordValue = newestWord.getOriginalValue();
		if (wordValue > 9) {
			digitmult = 2;
		} else if (wordValue > 99) {
			digitmult = 3;
		}
		value.setTextFill(Constants.GREEN);
		value.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
		value.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
		value.setLayoutX(315 - 15 - 25 * digitmult);

		if (_referee.currentPlayer() == "PLAYER ONE") {
			_wordContainer1.getChildren().addAll(word, value);
		} else if (_referee.currentPlayer() == "PLAYER TWO") {
			_wordContainer2.getChildren().addAll(word, value);
		}

		ArrayList<Word> crosses = newestWord.getAllCrosses();
		if (crosses != null && crosses.size() > 0) {
			for (int i = 0; i < crosses.size(); i++) {
				digitmult = 1;
				Word thisCross = crosses.get(i);
				Label newCrossWord = new Label(thisCross.getLetters());
				newCrossWord.setTextFill(Constants.GREEN);
				newCrossWord.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
				newCrossWord.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
				newCrossWord.setLayoutX(15);
				_playedWords.add(newCrossWord);
				int crossValue = thisCross.getValue();
				// system.out.printf("\nCROSS %s = %s with a value of %s and size %s\n\n", i +
				// 1, thisCross.getLetters(), crossValue, thisCross.getTiles().size());
				_referee.addToScore(crossValue);
				if (crossValue > 9) {
					digitmult = 2;
				} else if (crossValue > 99) {
					digitmult = 3;
				}
				Label newCrossValue = new Label(String.valueOf(crossValue));
				_playedWords.add(newCrossValue);
				newCrossValue.setTextFill(Constants.GREEN);
				newCrossValue.setFont(Font.loadFont(Constants.HANDWRITING, Constants.FONT_SIZE_WORD_LIST));
				newCrossValue.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
				newCrossValue.setLayoutX(315 - 15 - 25 * digitmult);
				if (_referee.currentPlayer() == "PLAYER ONE") {
					_wordContainer1.getChildren().addAll(newCrossWord, newCrossValue);
				} else if (_referee.currentPlayer() == "PLAYER TWO") {
					_wordContainer2.getChildren().addAll(newCrossWord, newCrossValue);
				}
			}
			_referee.incrementWordsPlayedBy(crosses.size());
			for (int i = 0; i < crosses.size(); i++) {
				PauseTransition delayFlash = new PauseTransition(
						Duration.seconds(Constants.FLASH_SPACING_DURATION * (i + 1)));
				delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), "ADDED"));
				delayFlash.play();
			}
		}
		int y = 9 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed());
		Line line = new Line(0, y, 3, y);
		line.setStroke(Color.BLACK);
		line.setStrokeWidth(3);
		line.setStrokeLineCap(StrokeLineCap.ROUND);
		line.setOpacity(0.7);
		if (_referee.currentPlayer() == "PLAYER ONE") {
			_wordContainer1.getChildren().add(line);
		} else if (_referee.currentPlayer() == "PLAYER TWO") {
			_wordContainer2.getChildren().add(line);
		}
		PaneOrganizer.this.updateScore();
		newestWord.playFlashes("ADDED");
		_referee.nextMove();
	}

	private class PlayFlashHandler implements EventHandler<ActionEvent> {
		private Word _word;
		private String _id;

		public PlayFlashHandler(Word word, String id) {
			_word = word;
			_id = id;
		}

		@Override
		public void handle(ActionEvent event) {
			_word.playFlashes(_id);
			event.consume();
		}

	}

	private class ScorePaneHandler implements EventHandler<MouseEvent> {
		private String _id;

		public ScorePaneHandler(String id) {
			_id = id;
		}

		@Override
		public void handle(MouseEvent event) {
			if (_referee != null && _referee.isThinking() == false) {
				if (_id == "IN") {
					_s1.setVvalue(0);
					_s2.setVvalue(0);
					_scorePane.setVisible(true);
					_scrabbleGame.pauseGamePlay();
					FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_scorePane);
					fadeIn.setFromValue(0.0);
					fadeIn.setToValue(1.0);
					fadeIn.play();
					// // FadeTransition fadeIn1 = new
					// FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
					// _playerOneScore);
					// fadeIn1.setFromValue(0.0);
					// fadeIn1.setToValue(1.0);
					// fadeIn1.play();
					// // FadeTransition fadeIn2 = new
					// FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
					// _playerTwoScore);
					// fadeIn2.setFromValue(0.0);
					// fadeIn2.setToValue(1.0);
					// fadeIn2.play();
				} else if (_id == "OUT") {
					_olds1 = _s1.getVvalue();
					_olds2 = _s2.getVvalue();
					// system.out.printf("olds1 = %s, olds2 = %s\n", _olds1, _olds2);
					_scrabbleGame.startGamePlay();
					FadeTransition fadeOut = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_scorePane);
					fadeOut.setFromValue(1.0);
					fadeOut.setToValue(0.0);
					fadeOut.setOnFinished(new ScorePaneRemoveHandler());
					fadeOut.play();
					// FadeTransition fadeOut1 = new
					// FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
					// _playerOneScore);
					// fadeOut1.setFromValue(1.0);
					// fadeOut1.setToValue(0.0);
					// fadeOut1.play();
					// FadeTransition fadeOut2 = new
					// FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
					// _playerTwoScore);
					// fadeOut2.setFromValue(1.0);
					// fadeOut2.setToValue(0.0);
					// // fadeOut2.play();
				}
			}
			event.consume();
		}

	}

	private class ScrollOneRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// system.out.println("HANDLE");
			_s1.setVvalue(_olds1);
			// system.out.printf("%s\n", _s1.getVvalue());
			event.consume();
		}

	}

	private class ScrollTwoRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// system.out.println("HANDLE");
			_s2.setVvalue(_olds2);
			// system.out.printf("%s\n", _s2.getVvalue());
			event.consume();
		}

	}

	private class ScorePaneRemoveHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_scorePane.setVisible(false);
			event.consume();
		}

	}

	public Boolean getIsShiftDown() {
		return _isShiftDown;
	}

	private class ShiftDisplayHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.getCode() == KeyCode.SHIFT) {
				_isShiftDown = true;
				_scrabbleGame.removeDiamond();
				// _rt = new RotateTransition(Duration.millis(190), _pencilViewer);
				// _rt.setByAngle(5);
				// _rt.setCycleCount(1);
				// _rt.play();
			}
			event.consume();
		}

	}

	private class ShiftConcealHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.getCode() == KeyCode.SHIFT) {
				_isShiftDown = false;
				_scrabbleGame.addDiamond();
				// _rt = new RotateTransition(Duration.millis(190), _pencilViewer);
				// _rt.setByAngle(-5);
				// _rt.setCycleCount(1);
				// _rt.play();
			}
		}

	}

	public void reset() {
		_appClass.start(_stage);
	}

	public void manageDraw(String id) {
		_justFilled = false;
		if (_scrabbleGame.getRackFor(PlayerNumber.One).size() < 7 && id == "PLAYER ONE") {
			_scrabbleGame.refillRackOne();
			_justFilled = true;
		}
		if (_scrabbleGame.getRackFor(PlayerNumber.Two).size() < 7 && id == "PLAYER TWO") {
			_scrabbleGame.refillRackTwo();
			_justFilled = true;
		}
		if (_justFilled == true && !_scrabbleGame.isBagEmpty()) {
			_scrabbleGame.printBagSize();
			_enterable = false;
			_rtBag.play();
			PauseTransition delay = new PauseTransition();
			delay.setDuration(Duration.seconds(2));
			delay.setOnFinished(new DrawResetHandler());
			delay.play();
		}
	}

	private class DrawResetHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (_justFilled == true) {
				_enterable = true;
			}
			event.consume();
		}

	}

	public void moveHi(String winner) {
		_moveHi.setOnFinished(new HighlightHandler(winner));
		_moveHi.play();
	}

	public void removeBag() {
		TranslateTransition removeBag = new TranslateTransition(Duration.seconds(3), _bagViewer);
		removeBag.setByX(-375);
		removeBag.setByY(100);
		removeBag.play();
	}

	public Pane getRoot() {
		return _root;
	}

}