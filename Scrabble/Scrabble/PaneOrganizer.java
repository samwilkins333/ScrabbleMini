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

import static Scrabble.Constants.*;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
class PaneOrganizer {

	// ORIGINAL CONTENT
	private App _appClass;
	private Stage _stage;
	private Referee _referee;
	private ScrabbleGame _scrabbleGame;

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
	private PlayerType _playerOneType;
	private PlayerType _playerTwoType;

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

	PaneOrganizer(App appClass, Stage appStage) {
		_root = new Pane();

		_enterInt = 0;
		_enterable = false;
		_displayMultipliers = false;
		_isShiftDown = false;
		_playerOneType = PLAYER_ONE_START_STATE;
		_playerTwoType = PLAYER_TWO_START_STATE;

		this.setUpDesk();

		_boardPane = new Pane();
		_boardPane.requestFocus();
		_boardPane.setFocusTraversable(true);
		_boardPane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());

		this.addBag();

		_root.getChildren().add(_boardPane);
		_appClass = appClass;
		_stage = appStage;

		ShiftDisplayHandler _rotatePencil = new ShiftDisplayHandler();
		ShiftConcealHandler _revertPencil = new ShiftConcealHandler();

		_boardPane.addEventHandler(KeyEvent.KEY_PRESSED, _rotatePencil);
		_boardPane.addEventHandler(KeyEvent.KEY_RELEASED, _revertPencil);

		_scrabbleGame = new ScrabbleGame(this, _root, _boardPane);

		this.addPostIts();
		this.addAIGraphics();
		this.addScrollPane();

		_oneScore = 0;
		_twoScore = 0;
		_playedWords = new ArrayList<>();

		this.setUpIntroSequence();
	}

	Boolean getDisplayMultipliers() {
		return _displayMultipliers;
	}

	void rotateBag() {
		_rtBag.play();
	}

	private void addScrollPane() {
		_scorePane = new Pane();
		_scorePane.setFocusTraversable(false);
		_scorePane.setVisible(false);

		_root.getChildren().addAll(_scorePane);

		Rectangle whiteRect1 = new Rectangle(0, 0, 315, 3000);
		whiteRect1.setFill(Color.WHITE);
		Rectangle whiteRect2 = new Rectangle(0, 0, 315, 3000);
		whiteRect2.setFill(Color.WHITE);

		_wordContainer1 = new Pane();
		_wordContainer1.getChildren().add(whiteRect1);

		_s1 = new ScrollPane();
		_s1.setOnMouseEntered(new ScrollOneRecallHandler());
		_s1.setPrefSize(7.5 * GRID_FACTOR, 15 * GRID_FACTOR);
		_s1.setContent(_wordContainer1);
		_s1.setPannable(true);
		_s1.setHbarPolicy(ScrollBarPolicy.NEVER);
		_s1.setVbarPolicy(ScrollBarPolicy.NEVER);
		_s1.setLayoutX(ZEROETH_COLUMN_OFFSET * GRID_FACTOR);
		_s1.setLayoutY(ZEROETH_ROW_OFFSET * GRID_FACTOR);
		_s1.setFocusTraversable(false);

		_wordContainer2 = new Pane();
		_wordContainer2.getChildren().add(whiteRect2);

		_s2 = new ScrollPane();
		_s2.setOnMouseEntered(new ScrollTwoRecallHandler());
		_s2.setPrefSize(7.5 * GRID_FACTOR, 15 * GRID_FACTOR);
		_s2.setContent(_wordContainer2);
		_s2.setPannable(true);
		_s2.setHbarPolicy(ScrollBarPolicy.NEVER);
		_s2.setVbarPolicy(ScrollBarPolicy.NEVER);
		_s2.setLayoutX(ZEROETH_COLUMN_OFFSET * GRID_FACTOR + 7.5 * GRID_FACTOR);
		_s2.setLayoutY(Y0 * GRID_FACTOR);
		_s2.setFocusTraversable(false);

		_scorePane.getChildren().addAll(_s1, _s2);
	}

	private void setUpIntroSequence() {
		_leftHalf = new ImageView(new Image("Images/Main Theme and GUI/introLeft.jpg"));
		_leftHalf.setPreserveRatio(true);
		_leftHalf.setCache(true);
		_leftHalf.setFitWidth(SCENE_WIDTH / 2);
		_leftHalf.setLayoutX(0);
		_leftHalf.setLayoutY(-100);
		_leftHalf.setOpacity(0);
		_rightHalf = new ImageView(new Image("Images/Main Theme and GUI/introRight.jpg"));
		_rightHalf.setPreserveRatio(true);
		_rightHalf.setCache(true);
		_rightHalf.setFitWidth(SCENE_WIDTH / 2);
		_rightHalf.setLayoutX(SCENE_WIDTH / 2);
		_rightHalf.setLayoutY(-100);
		_rightHalf.setOpacity(0);
		_background = new ImageView(new Image("Images/Main Theme and GUI/intro.jpg"));
		_background.setPreserveRatio(true);
		_background.setCache(true);
		_background.setFitWidth(SCENE_WIDTH);
		_background.setLayoutX(0);
		_background.setLayoutY(-100);
		_moveLeft = new TranslateTransition(Duration.seconds(2), _leftHalf);
		_moveLeft.setByX(-1 * SCENE_WIDTH / 2 - 2);
		_moveRight = new TranslateTransition(Duration.seconds(2), _rightHalf);
		_moveRight.setByX(SCENE_WIDTH / 2 + 2);

		_human = new Image("Images/AI/human.png");
		_ai = new Image("Images/AI/ai.png");

		// Create and format player one's avatar
		_playerOneIcon = new ImageView(_ai);
		_playerOneIcon.setOpacity(0.0);
		_playerOneIcon.setPreserveRatio(true);
		_playerOneIcon.setCache(true);
		_playerOneIcon.setFitWidth(GRID_FACTOR * 3);
		_playerOneIcon.setLayoutX(200);
		_playerOneIcon.setLayoutY(SCENE_HEIGHT / 2 - GRID_FACTOR * 1.5);
		_playerOneIcon.setOnMouseClicked(new TogglePlayer(PlayerNum.One));
		_playerOneIcon.setOnMouseEntered(new ScaleIcon(PlayerNum.One, Direction.In));
		_playerOneIcon.setOnMouseExited(new ScaleIcon(PlayerNum.One, Direction.Out));

		// Create and format player one's avatar
		_playerTwoIcon = new ImageView(_human);
		_playerTwoIcon.setOpacity(0.0);
		_playerTwoIcon.setPreserveRatio(true);
		_playerTwoIcon.setCache(true);
		_playerTwoIcon.setFitWidth(GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutX(SCENE_WIDTH - 200 - GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutY(SCENE_HEIGHT / 2 - GRID_FACTOR * 1.5);
		_playerTwoIcon.setOnMouseClicked(new TogglePlayer(PlayerNum.Two));
		_playerTwoIcon.setOnMouseEntered(new ScaleIcon(PlayerNum.Two, Direction.In));
		_playerTwoIcon.setOnMouseExited(new ScaleIcon(PlayerNum.Two, Direction.Out));

		// Graphically add all elements
		_root.getChildren().addAll(_background, _leftHalf, _rightHalf, _playerOneIcon, _playerTwoIcon);

		PauseTransition intro = new PauseTransition(Duration.seconds(2));
		intro.setOnFinished(new FadeHalvesHandler());
		intro.play();

		// M TILE - build associated animations

		Tile m = new Tile(13);
		double x = X3 - 0.3;
		double y = Y12;
		m.add(_root, x, y, _scrabbleGame, PlayerNum.Neither);
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

		// FIRST I TILE - build associated animations

		Tile i1 = new Tile(9);
		x = X4 - 0.35;
		i1.add(_root, x, y, _scrabbleGame, PlayerNum.Neither);
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

		// N TILE - build associated animations

		Tile n = new Tile(14);
		x = X5 - 0.35;
		n.add(_root, x, y, _scrabbleGame, PlayerNum.Neither);
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

		// SECOND I TILE - build associated animations

		Tile i2 = new Tile(9);
		x = X6 - 0.35;
		i2.add(_root, x, y, _scrabbleGame, PlayerNum.Neither);
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
			// M
			_scaleM.play();
			_rotateM.play();
			_moveM.play();

			_scaleMCheck.play();
			_rotateMCheck.play();
			_moveMCheck.play();

			// First I
			_scaleI1.play();
			_rotateI1.play();
			_moveI1.play();

			_scaleI1Check.play();
			_rotateI1Check.play();
			_moveI1Check.play();

			// N
			_scaleN.play();
			_rotateN.play();
			_moveN.play();

			_scaleNCheck.play();
			_rotateNCheck.play();
			_moveNCheck.play();

			// Second I
			_scaleI2.play();
			_rotateI2.play();
			_moveI2.play();

			_scaleI2Check.play();
			_rotateI2Check.play();
			_moveI2Check.play();

			event.consume();
		}

	}

	private class TileRemover implements EventHandler<ActionEvent> {
		private Tile _toRemove;

		TileRemover(Tile toRemove) {
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
		private Direction _direction;
		private PlayerNum _p;

		private ScaleIcon(PlayerNum p, Direction direction) {
			_p = p;
			_direction = direction;
		}

		@Override
		public void handle(MouseEvent event) {
			ImageView view = _p == PlayerNum.One ? _playerOneIcon : _playerTwoIcon;

			switch (_direction) {
				case In:
					view.setScaleX(1.1);
					view.setScaleY(1.1);
					break;
				case Out:
					view.setScaleX(1);
					view.setScaleY(1);
					break;
			}

			event.consume();
		}

	}

	private class TogglePlayer implements EventHandler<MouseEvent> {
		private PlayerNum _p;

		TogglePlayer(PlayerNum p) {
			_p = p;
		}

		@Override
		public void handle(MouseEvent event) {
			boolean isPlayerOne = _p == PlayerNum.One;

			PlayerType typeToSet = isPlayerOne ? _playerOneType : _playerTwoType;
			boolean isHuman = typeToSet == PlayerType.Human;

			if (isPlayerOne) _playerOneType = isHuman ? PlayerType.AI : PlayerType.Human;
			else _playerTwoType = isHuman ? PlayerType.AI : PlayerType.Human;

			ImageView viewToSet = isPlayerOne ? _playerOneIcon : _playerTwoIcon;
			viewToSet.setImage(isHuman ? _ai : _human);

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

		PlayFadeHandler(FadeTransition fade) {
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

	void displayScoreLabels() {
		_playerOneScore.setOpacity(1.0);
		_playerOneScore.setOpacity(2.0);
	}

	void updateScore() {
		int digitmult = 0;
		int oneScore = _referee.getPlayerOneScore();

		if (oneScore > 9 && _oneScore <= 9) digitmult = 1;
		else if (oneScore > 99 && _oneScore <= 99) digitmult = 1;

		_playerOneScore.setLayoutX(_playerOneScore.getLayoutX() - 14 * digitmult);
		_oneScore = oneScore;

		digitmult = 0;
		int twoScore = _referee.getPlayerTwoScore();

		if (twoScore > 9 && _twoScore <= 9) digitmult = 1;
		else if (twoScore > 99 && _twoScore <= 99) digitmult = 1;

		_playerTwoScore.setLayoutX(_playerTwoScore.getLayoutX() - 14 * digitmult);
		_twoScore = twoScore;

		_playerOneScore.setText(String.valueOf(oneScore));
		_playerTwoScore.setText(String.valueOf(twoScore));
	}

	private void updateLabels() {
		if (_playedWords.size() > 0) {
			for (Label _playedWord : _playedWords) _playedWord.setTextFill(GRAPHITE);
		}
	}

	private void addAIGraphics() {
		_accept = new Image("Images/AI/aiaccept.png");
		_reject = new Image("Images/AI/aireject.png");
		_thinking = new Image("Images/AI/aithinking.png");
		_aiViewer = new ImageView(_thinking);
		_aiViewer.setOpacity(0.0);
		_aiViewer.setCache(true);
		_aiViewer.setPreserveRatio(true);
		_aiViewer.setFitWidth(GRID_FACTOR * 1.2);
		_aiViewer.setLayoutX(GRID_FACTOR * 32.5);
		_aiViewer.setLayoutY(GRID_FACTOR * 17.2);
		_aiCircle = new Circle();
		_aiCircle.setRadius(GRID_FACTOR);
		_aiCircle.setFill(GREEN);
		_aiCircle.setCenterX(GRID_FACTOR * 32.5 + GRID_FACTOR * 0.6);
		_aiCircle.setLayoutY(GRID_FACTOR * 17.2 + GRID_FACTOR * 0.6 + 3);
		_aiCircle.setOpacity(0.0);
		_boardPane.getChildren().addAll(_aiCircle, _aiViewer);

		_aiExpand = new ScaleTransition(Duration.seconds(PLACEMENT_DURATION), _aiViewer);
		_aiExpand.setFromX(1 / 4);
		_aiExpand.setFromY(1 / 4);
		_aiExpand.setToX(1);
		_aiExpand.setToY(1);
		_aiExpand.setCycleCount(1);

		_aiShrink = new ScaleTransition(Duration.seconds(PLACEMENT_DURATION), _aiViewer);
		_aiShrink.setFromX(1);
		_aiShrink.setFromY(1);
		_aiShrink.setToX(1 / 4);
		_aiShrink.setToY(1 / 4);
		_aiShrink.setCycleCount(1);
		_aiShrink.setOnFinished(new ResetAIHandler());

		_aiRotate = new Timeline();
		KeyFrame rotate = new KeyFrame(Duration.seconds(AI_ROTATE_DURATION), new RotateHandler());
		_aiRotate.getKeyFrames().add(rotate);
		_aiRotate.setCycleCount(Animation.INDEFINITE);

		_circleExpand = new ScaleTransition(Duration.seconds(PLACEMENT_DURATION), _aiCircle);
		_circleExpand.setFromX(0.5);
		_circleExpand.setFromY(0.5);
		_circleExpand.setToX(1.2);
		_circleExpand.setToY(1.2);

		_circleFade = new FadeTransition(Duration.seconds(PLACEMENT_DURATION), _aiCircle);
		_circleFade.setFromValue(1.0);
		_circleFade.setToValue(0.0);
	}

	void fadeInAI() {
		FadeTransition aiFade = new FadeTransition(Duration.seconds(0.05), _aiViewer);
		aiFade.setFromValue(0.0);
		aiFade.setToValue(1.0);
		aiFade.setOnFinished(new AnimateAIHandler());
		aiFade.play();
		_aiViewer.setOpacity(0);
		_aiExpand.play();
	}

	void showAIOutcome(String id) {
		boolean accepted = id.equals("ACCEPT");
		Color fill = accepted ? GREEN : RED;
		Image image = accepted ? _accept : _reject;
		
		_aiViewer.setImage(image);
		_circleExpand.play();
		_circleFade.play();
		_aiShrink.stop();
		_aiRotate.stop();
		_aiCircle.setFill(fill);
		_aiViewer.setRotate(0);
		_aiViewer.setScaleX(1);
		_aiViewer.setScaleY(1);
	}

	void fadeOutAI() {
		_aiShrink.play();
	}

	private class AnimateAIHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
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
			_tileM.playFlash(WordAddition.Success);
			_tileI1.playFlash(WordAddition.Success);
			_tileN.playFlash(WordAddition.Success);
			_tileI2.playFlash(WordAddition.Success);
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

	private void addPostIts() {
		// Build green post it to display player one's score
		ImageView postItOneViewer = new ImageView(new Image("Images/Main Theme and GUI/greenpostit.png"));
		postItOneViewer.setCache(true);
		postItOneViewer.setPreserveRatio(true);
		postItOneViewer.setFitWidth(GRID_FACTOR * 4);
		postItOneViewer.setLayoutX(GRID_FACTOR * 4);
		postItOneViewer.setLayoutY(GRID_FACTOR * 5);
		postItOneViewer.setOnMousePressed(new ScorePaneHandler(Direction.In));
		postItOneViewer.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		// Build blue post it to display player two's score
		ImageView postItTwoViewer = new ImageView(new Image("Images/Main Theme and GUI/bluepostit.png"));
		postItTwoViewer.setCache(true);
		postItTwoViewer.setPreserveRatio(true);
		postItTwoViewer.setFitWidth(GRID_FACTOR * 4);
		postItTwoViewer.setLayoutX(GRID_FACTOR * 6);
		postItTwoViewer.setLayoutY(GRID_FACTOR * 3);
		postItTwoViewer.setOnMousePressed(new ScorePaneHandler(Direction.In));
		postItTwoViewer.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		// Set up text for both players' scores
		_playerOneScore = new Label();
		_playerOneScore.setOpacity(1.0);
		_playerOneScore.setTextFill(GRAPHITE);
		_playerOneScore.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_POST_IT));
		_playerOneScore.setLayoutY(GRID_FACTOR * 6.1);
		_playerOneScore.setLayoutX(GRID_FACTOR * 5.7);

		_playerTwoScore = new Label();
		_playerTwoScore.setOpacity(1.0);
		_playerTwoScore.setTextFill(GRAPHITE);
		_playerTwoScore.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_POST_IT));
		_playerTwoScore.setLayoutY(GRID_FACTOR * 4.1);
		_playerTwoScore.setLayoutX(GRID_FACTOR * 7.9);

		// Add on-pressed and on-released interactions to show or conceal word record
		_playerOneScore.setOnMousePressed(new ScorePaneHandler(Direction.In));
		_playerOneScore.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		_playerTwoScore.setOnMousePressed(new ScorePaneHandler(Direction.In));
		_playerTwoScore.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		// Add highlight to potentially cover player one's score if winner
		_playerOneHi = new ImageView(new Image("Images/Main Theme and GUI/highlight.png"));
		_playerOneHi.setPreserveRatio(true);
		_playerOneHi.setCache(true);
		_playerOneHi.setFitWidth(90);
		_playerOneHi.setLayoutX(GRID_FACTOR * 5.1 - 5);
		_playerOneHi.setLayoutY(GRID_FACTOR * 6.5);
		_playerOneHi.setOpacity(0.0);
		_playerOneHi.setOnMousePressed(new ScorePaneHandler(Direction.In));
		_playerOneHi.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		// Add highlight to potentially cover player two's score if winner
		_playerTwoHi = new ImageView(new Image("Images/Main Theme and GUI/highlight.png"));
		_playerTwoHi.setPreserveRatio(true);
		_playerTwoHi.setCache(true);
		_playerTwoHi.setFitWidth(90);
		_playerTwoHi.setLayoutX(GRID_FACTOR * 7.3 - 5);
		_playerTwoHi.setLayoutY(GRID_FACTOR * 4.5);
		_playerTwoHi.setOpacity(0.0);
		_playerTwoHi.setOnMousePressed(new ScorePaneHandler(Direction.In));
		_playerTwoHi.setOnMouseReleased(new ScorePaneHandler(Direction.Out));

		// Add the yellow short pencil resting on the desk
		ImageView pencilView = new ImageView(new Image("Images/Main Theme and GUI/pencil.png"));
		pencilView.setCache(true);
		pencilView.setPreserveRatio(true);
		pencilView.setFitWidth(GRID_FACTOR * 4);
		pencilView.setLayoutX(GRID_FACTOR * 30.4);
		pencilView.setLayoutY(GRID_FACTOR * 6);
		pencilView.setRotate(60);

		// Add shadow effect and rotation animation to pencil
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);
		pencilView.setEffect(pieceShadow);

		_rtPencil = new RotateTransition(Duration.millis(190), pencilView);
		_rtPencil.setByAngle(15);
		_rtPencil.setCycleCount(2);
		_rtPencil.setAutoReverse(true);

		// Add highlighter pen that appears on completion of game
		ImageView highlightView = new ImageView(new Image("Images/Main Theme and GUI/hi.png"));
		highlightView.setCache(true);
		highlightView.setPreserveRatio(true);
		highlightView.setFitWidth(GRID_FACTOR * 1.4);
		highlightView.setLayoutX(GRID_FACTOR * 5 - 400);
		highlightView.setLayoutY(GRID_FACTOR * 7);
		highlightView.setRotate(-39);

		// Add shadow effect and translation animation to highlighter
		DropShadow hiShadow = new DropShadow();
		hiShadow.setRadius(50);
		hiShadow.setOffsetX(3);
		hiShadow.setOffsetY(3);
		hiShadow.setColor(Color.web("#444444"));
		hiShadow.setSpread(0.0);
		hiShadow.setHeight(40);
		hiShadow.setWidth(40);
		hiShadow.setBlurType(BlurType.GAUSSIAN);
		highlightView.setEffect(hiShadow);

		_moveHi = new TranslateTransition(Duration.seconds(PLACEMENT_DURATION), highlightView);
		_moveHi.setByX(400);

		// Graphically add all elements to GUI
		_boardPane.getChildren().addAll(
				postItOneViewer,
				postItTwoViewer,
				_playerOneHi,
				_playerTwoHi,
				pencilView,
				_playerOneScore,
				_playerTwoScore,
				highlightView
		);
	}

	private class HighlightHandler implements EventHandler<ActionEvent> {
		private Winner _winner;

		HighlightHandler(Winner winner) {
			_winner = winner;
		}

		@Override
		public void handle(ActionEvent event) {
			switch (_winner) {
				case One:
					_playerOneHi.setOpacity(0.8);
					break;
				case Two:
					_playerOneHi.setOpacity(0.8);
					break;
				case Draw:
					_playerOneHi.setOpacity(0.8);
					_playerTwoHi.setOpacity(0.8);
					break;
			}
			event.consume();
		}

	}

	private void addBag() {
		_bagViewer = new ImageView(new Image("Images/Main Theme and GUI/bag.png"));
		_bagViewer.setCache(true);
		_bagViewer.setPreserveRatio(true);
		_bagViewer.setFitWidth(GRID_FACTOR * 7);
		_bagViewer.setLayoutX(0);
		_bagViewer.setLayoutY(GRID_FACTOR * 10);
		_bagViewer.setRotate(45);
		// _bagViewer.addEventHandler(MouseEvent.MOUSE_CLICKED, new DrawHandler());
		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(SHADOW_FILL);
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

	private void setUpDesk() {
		ImageView _leatherViewer = new ImageView(new Image("Images/Main Theme and GUI/leatherframe - Original.png"));
		_leatherViewer.setCache(true);
		_leatherViewer.setPreserveRatio(false);
		_leatherViewer.setFitWidth(GRID_FACTOR * 15.5);
		_leatherViewer.setFitHeight(GRID_FACTOR * 15.5);
		_leatherViewer.setLayoutX(GRID_FACTOR * (ZEROETH_COLUMN_OFFSET - 0.25) - 1);
		_leatherViewer.setLayoutY(GRID_FACTOR * (ZEROETH_ROW_OFFSET - 0.25) - 1);

		DropShadow pieceShadow = new DropShadow();
		pieceShadow.setRadius(120);
		pieceShadow.setOffsetX(4);
		pieceShadow.setOffsetY(4);
		pieceShadow.setColor(SHADOW_FILL);
		pieceShadow.setSpread(0.0);
		pieceShadow.setHeight(25);
		pieceShadow.setWidth(25);
		pieceShadow.setBlurType(BlurType.GAUSSIAN);

		_leatherViewer.setEffect(pieceShadow);
		ImageView deskViewer = new ImageView(new Image("Images/Main Theme and GUI/desktop.jpg"));
		deskViewer.setCache(true);
		deskViewer.setPreserveRatio(true);
		deskViewer.setFitWidth(SCENE_WIDTH);
		deskViewer.setLayoutX(0);
		deskViewer.setLayoutY(-150);

		_root.getChildren().addAll(deskViewer, _leatherViewer);
	}

	void DeclareEnterable() {
		_enterable = true;
	}

	void resetEnterInt() {
		_enterInt = 1;
	}

	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			switch (event.getCode()) {
				default:
					break;
				case A:
					_scrabbleGame.setAutoReset(!_scrabbleGame.getAutoReset());
					break;
				case C:
					_scrabbleGame.getReferee().getCurrentPlayerInstance().getNewestWord().clear();
					_scrabbleGame.resetRackOne();
					_scrabbleGame.resetRackTwo();
					break;
				case DIGIT1:
					boolean isOneHuman = _playerOneType == PlayerType.Human;
					_playerOneType = isOneHuman ? PlayerType.AI : PlayerType.Human;
					_playerOneIcon.setImage(isOneHuman ? _ai : _human);
					break;
				case DIGIT2:
					boolean isTwoHuman = _playerTwoType == PlayerType.Human;
					_playerTwoType = isTwoHuman ? PlayerType.AI : PlayerType.Human;
					_playerTwoIcon.setImage(isTwoHuman ? _ai : _human);
					break;
				case ENTER:
					if (!_enterable) return;

					// THE USER ELECTS TO BEGIN THE GAME FROM START SCREEN

					if (_enterInt == 0) {

						_moveLeft.play();
						_moveRight.play();

						// Move and scale icons to keep pace with red screen half animations revealing main board

						TranslateTransition moveLeftIcon = new TranslateTransition(Duration.seconds(2), _playerOneIcon);
						moveLeftIcon.setByX(-1 * SCENE_WIDTH / 2 - 2);
						moveLeftIcon.play();

						TranslateTransition moveRightIcon = new TranslateTransition(Duration.seconds(2), _playerTwoIcon);
						moveRightIcon.setByX(SCENE_WIDTH / 2 + 2);
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

						// Initialize the game and create the players based on user input
						_scrabbleGame.startGamePlay();

						Playable _playerOne = _playerOneType == PlayerType.Human ?
								new HumanPlayer(PlayerNum.One, _scrabbleGame) :
								new AIPlayer(PlayerNum.One, _scrabbleGame);

						Playable _playerTwo = _playerTwoType == PlayerType.Human ?
								new HumanPlayer(PlayerNum.Two, _scrabbleGame) :
								new AIPlayer(PlayerNum.Two, _scrabbleGame);

						_referee = new Referee(_scrabbleGame, _playerOne, _playerTwo);
						_scrabbleGame.addReferee(_referee);

						// Initialize scoreboard
						_rtPencil.play();
						PaneOrganizer.this.updateScore();

					// ELSE IF THE USER SUBMITS A WORD

					} else if (_enterInt > 0 && event.isMetaDown()) {

						//TODO: Combine word with crosses and AI with human word addition to cut back on duplicate code

						Word newestWord = _referee.getCurrentPlayerInstance().getNewestWord();

						// IF SUCCESS

						if (newestWord.isPlayable()) {
							newestWord.addToBoard();

							// Inform referee of move implications
							if (!_referee.isFirstMoveMade()) _referee.DeclareFirstMoveMade();
							_referee.incrementWordsPlayedBy(1);

							// Set any previous labels that may have been green to graphite to indicate past turns
							PaneOrganizer.this.updateLabels();

							// Create label for newest word to tally sheet
							Label word = new Label(newestWord.getLetters());
							word.setTextFill(GREEN);
							word.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
							word.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
							word.setLayoutX(15);
							_playedWords.add(word);

							// Create label for the value of that newest word (placed parallel to text)
							Label value = new Label(String.valueOf(newestWord.getValue()));
							value.setTextFill(GREEN);
							value.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
							value.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
							_playedWords.add(value);

							int wordValue = newestWord.getValue();
							_referee.addToScore(wordValue);

							// Compute and apply the appropriate offset for value (manual right alignment)
							int offsetFactor = 1;
							if (wordValue > 9 && wordValue <= 99) offsetFactor = 2;
							else if (wordValue > 99 && wordValue <= 999) offsetFactor = 3;
							else if (wordValue > 999) offsetFactor = 4;
							value.setLayoutX(315 - 15 - 25 * offsetFactor);

							// Graphically add new word and value labels
							Pane wc = _referee.getCurrentPlayer() == PlayerNum.One ? _wordContainer1 : _wordContainer2;
							wc.getChildren().addAll(word, value);

							// Rinse and repeat for all crosses
							ArrayList<Word> crosses = newestWord.getAllCrosses();
							for (int i = 0; i < crosses.size(); i++) {
								Word thisCross = crosses.get(i);

								// Create label for give cross word to tally sheet
								Label newCrossWord = new Label(thisCross.getLetters());
								newCrossWord.setTextFill(GREEN);
								newCrossWord.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
								newCrossWord.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
								newCrossWord.setLayoutX(15);
								_playedWords.add(newCrossWord);

								// Inform the referee of the score change (implicitly for current player)
								int crossValue = thisCross.getValue();
								_referee.addToScore(crossValue);

								// Create label for the value of that given cross word (placed parallel to text)
								Label newCrossValue = new Label(String.valueOf(crossValue));
								newCrossValue.setTextFill(GREEN);
								newCrossValue.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
								newCrossValue.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
								_playedWords.add(newCrossValue);

								// Compute and apply the appropriate offset for value (manual right alignment)
								offsetFactor = 1;
								if (crossValue > 9 && crossValue <= 99) offsetFactor = 2;
								else if (crossValue > 99 && crossValue <= 999) offsetFactor = 3;
								else if (crossValue > 999) offsetFactor = 4;
								newCrossValue.setLayoutX(300 - 25 * offsetFactor);

								// Graphically add new word and value labels
								wc.getChildren().addAll(newCrossWord, newCrossValue);
							}

							// Inform referee of move implications
							_referee.incrementWordsPlayedBy(crosses.size());

							// Create and graphically add tick mark to indicate turn separations
							int y = 9 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed());
							Line line = new Line(0, y, 3, y);
							line.setStroke(Color.BLACK);
							line.setStrokeWidth(3);
							line.setStrokeLineCap(StrokeLineCap.ROUND);
							line.setOpacity(0.7);
							wc.getChildren().add(line);

							// Update the score displayed on the current player's post it note
							PaneOrganizer.this.updateScore();

							// Display GUI feedback indicating the success for word...
							newestWord.playFlashes(WordAddition.Success);
							// ...and its crosses
							for (int i = 0; i < crosses.size(); i++) {
								PauseTransition delayFlash = new PauseTransition(Duration.seconds(0.5 * (i + 1)));
								delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), WordAddition.Success));
								delayFlash.play();
							}

							// Play pencil rotation animation to indicate completion of writing
							_rtPencil.play();

							// Ensure no spamming of enter and initiate the next move
							_enterable = false;
							_referee.nextMove();

						// ELSE IF SOME DEGREE OF FAILURE

						} else {
							newestWord.playFlashes(newestWord.isInDictionary() && newestWord.isFormatted() ? WordAddition.Partial : WordAddition.Failure);

							ArrayList<Word> crosses = newestWord.getAllCrosses();
							if (crosses != null && !crosses.isEmpty()) {
								for (int i = 0; i < crosses.size(); i++) {
									PauseTransition delayFlash = new PauseTransition(Duration.seconds(FLASH_SPACING_DURATION * (i + 1)));
									WordAddition outcome = crosses.get(i).isInDictionary() ? WordAddition.Success : WordAddition.Failure;
									delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), outcome));
									delayFlash.play();
								}
							}
						}
					}

					// Regardless of the events triggered, record that enter was pressed
					_enterInt++;

					break;
				case ESCAPE:
					if (_referee != null &&
						_referee.getCurrentPlayerInstance().isHuman() &&
						event.isMetaDown()) {

						_referee.nextMove();

					}
					break;
				case M:
					if (_displayMultipliers) {
						_scrabbleGame.addDiamond();
						_scrabbleGame.fadeInOtherSquares(SquareIdentity.Ghost);
					} else {
						_scrabbleGame.removeDiamond();
						_scrabbleGame.fadeOutOtherSquares(SquareIdentity.Ghost);
					}

					_displayMultipliers = !_displayMultipliers;
					break;
				case Q:
					System.exit(0); // Quits game with "Q"
					break;
				case R:
					PaneOrganizer.this.reset();
					break;
				case SPACE:
					if (_referee != null && _referee.getCurrentPlayerInstance().isHuman()) {
						_scrabbleGame.shuffleRack(_referee.getCurrentPlayer());
					}
					break;
			}

			event.consume();
		}

	}

	void addWordAI(Word newestWord) {

		//TODO: Combine word with crosses and AI with human word addition to cut back on duplicate code

		newestWord.addToBoardLogicallyAI();

		// Inform referee of move implications
		if (!_referee.isFirstMoveMade()) _referee.DeclareFirstMoveMade();
		_referee.incrementWordsPlayedBy(1);

		// Set any previous labels that may have been green to graphite to indicate past turns
		PaneOrganizer.this.updateLabels();

		// Create label for newest word to tally sheet
		Label word = new Label(newestWord.getLetters());
		word.setTextFill(GREEN);
		word.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
		word.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
		word.setLayoutX(15);
		_playedWords.add(word);

		// Create label for the value of that newest word (placed parallel to text)
		Label value = new Label(String.valueOf(newestWord.getOriginalValue()));
		value.setTextFill(GREEN);
		value.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
		value.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
		_playedWords.add(value);

		int wordValue = newestWord.getOriginalValue();

		// Compute and apply the appropriate offset for value (manual right alignment)
		int offsetFactor = 1;
		if (wordValue > 9 && wordValue <= 99) offsetFactor = 2;
		else if (wordValue > 99 && wordValue <= 999) offsetFactor = 3;
		else if (wordValue > 999) offsetFactor = 4;
		value.setLayoutX(315 - 15 - 25 * offsetFactor);

		// Graphically add new word and value labels
		Pane wc = _referee.getCurrentPlayer() == PlayerNum.One ? _wordContainer1 : _wordContainer2;
		wc.getChildren().addAll(word, value);

		// Rinse and repeat for all crosses
		ArrayList<Word> crosses = newestWord.getAllCrosses();

		if (crosses != null && !crosses.isEmpty()) {

			for (int i = 0; i < crosses.size(); i++) {
				Word thisCross = crosses.get(i);

				// Create label for give cross word to tally sheet
				Label newCrossWord = new Label(thisCross.getLetters());
				newCrossWord.setTextFill(GREEN);
				newCrossWord.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
				newCrossWord.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
				newCrossWord.setLayoutX(15);
				_playedWords.add(newCrossWord);

				// Inform the referee of the score change (implicitly for current player)
				int crossValue = thisCross.getValue();
				_referee.addToScore(crossValue);

				// Create label for the value of that given cross word (placed parallel to text)
				Label newCrossValue = new Label(String.valueOf(crossValue));
				newCrossValue.setTextFill(GREEN);
				newCrossValue.setFont(Font.loadFont(DOMESTIC_MANNERS, FONT_SIZE_WORD_LIST));
				newCrossValue.setLayoutY(-3 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
				_playedWords.add(newCrossValue);

				// Compute and apply the appropriate offset for value (manual right alignment)
				offsetFactor = 1;
				if (crossValue > 9 && crossValue <= 99) offsetFactor = 2;
				else if (crossValue > 99 && crossValue <= 999) offsetFactor = 3;
				else if (crossValue > 999) offsetFactor = 4;
				newCrossValue.setLayoutX(300 - 25 * offsetFactor);

				// Graphically add new word and value labels
				wc.getChildren().addAll(newCrossWord, newCrossValue);
			}

			// Inform referee of added crosses, if any
			_referee.incrementWordsPlayedBy(crosses.size());
			_referee.incrementWordsPlayedBy(crosses.size());

			// Display GUI feedback indicating the success for crosses, if any
			for (int i = 0; i < crosses.size(); i++) {
				PauseTransition delayFlash = new PauseTransition(Duration.seconds(FLASH_SPACING_DURATION * (i + 1)));
				delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), WordAddition.Success));
				delayFlash.play();
			}

		}

		// Create and graphically add tick mark to indicate turn separations
		int y = 9 + (GRID_FACTOR - 4) * (_referee.getNumWordsPlayed());
		Line line = new Line(0, y, 3, y);
		line.setStroke(Color.BLACK);
		line.setStrokeWidth(3);
		line.setStrokeLineCap(StrokeLineCap.ROUND);
		line.setOpacity(0.7);
		wc.getChildren().add(line);

		// Update the score displayed on the current player's post it note
		PaneOrganizer.this.updateScore();

		// Display GUI feedback indicating the success for word
		newestWord.playFlashes(WordAddition.Success);


		// Play pencil rotation animation to indicate completion of writing
		_rtPencil.play();

		//Initiate next move
		_referee.nextMove();
	}

	private class PlayFlashHandler implements EventHandler<ActionEvent> {
		private Word _word;
		private WordAddition _outcome;

		PlayFlashHandler(Word word, WordAddition outcome) {
			_word = word;
			_outcome = outcome;
		}

		@Override
		public void handle(ActionEvent event) {
			_word.playFlashes(_outcome);
			event.consume();
		}

	}

	private class ScorePaneHandler implements EventHandler<MouseEvent> {
		private Direction _dir;

		ScorePaneHandler(Direction direction) {
			_dir = direction;
		}

		@Override
		public void handle(MouseEvent event) {
			if (_referee != null && !_referee.isThinking()) {
				if (_dir == Direction.In) {
					_s1.setVvalue(0);
					_s2.setVvalue(0);

					_scorePane.setVisible(true);
					_scrabbleGame.pauseGamePlay();

					FadeTransition fadeIn = new FadeTransition(Duration.seconds(SCORE_FADE_DURATION), _scorePane);
					fadeIn.setFromValue(0.0);
					fadeIn.setToValue(1.0);
					fadeIn.play();
				} else {
					_olds1 = _s1.getVvalue();
					_olds2 = _s2.getVvalue();

					// _scorePane concealment handled in OnFinished of animation
					_scrabbleGame.startGamePlay();

					FadeTransition fadeOut = new FadeTransition(Duration.seconds(SCORE_FADE_DURATION), _scorePane);
					fadeOut.setFromValue(1.0);
					fadeOut.setToValue(0.0);
					fadeOut.setOnFinished(new ScorePaneRemoveHandler());
					fadeOut.play();
				}
			}
			event.consume();
		}

	}

	private class ScrollOneRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			_s1.setVvalue(_olds1);
			event.consume();
		}

	}

	private class ScrollTwoRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			_s2.setVvalue(_olds2);
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

	Boolean getIsShiftDown() {
		return _isShiftDown;
	}

	private class ShiftDisplayHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.getCode() == KeyCode.SHIFT) {
				_isShiftDown = true;
				_scrabbleGame.removeDiamond();
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
			}
		}

	}

	private void reset() {
		_appClass.start(_stage);
	}

	void manageDraw(PlayerNum num) {
		_justFilled = false;
		if (_scrabbleGame.getPlayerRack(num).size() < 7 && num == PlayerNum.One) {
			_scrabbleGame.refillRack(num);
			_justFilled = true;
		}

		// If a rack was refilled and the bag was <not> emptied in the process...
		if (_justFilled && !_scrabbleGame.tileBagIsEmpty()) {
			_enterable = false;

			// Bag wiggling to simulate dispensing of tiles
			_rtBag.play();

			// Inhibits spamming of drawing
			PauseTransition delay = new PauseTransition();
			delay.setDuration(Duration.seconds(2));
			delay.setOnFinished(new DrawResetHandler());
			delay.play();
		}
	}

	private class DrawResetHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (_justFilled) {
				_enterable = true;
			}
			event.consume();
		}

	}

	void moveHi(Winner winner) {
		_moveHi.setOnFinished(new HighlightHandler(winner));
		_moveHi.play();
	}

	void removeBag() {
		TranslateTransition removeBag = new TranslateTransition(Duration.seconds(3), _bagViewer);
		removeBag.setByX(-375);
		removeBag.setByY(100);
		removeBag.play();
	}

	Pane getRoot() {
		return _root;
	}

}