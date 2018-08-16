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
	private Pane _root;
	private Pane _boardPane;
	private App _appClass;
	private Stage _stage;
	private Referee _referee;
	private ScrabbleGame _newGame;
	private ImageView _bagViewer;
	private ImageView _postItOneViewer;
	private ImageView _postItTwoViewer;
	private ImageView _pencilViewer;
	private ImageView _leatherViewer;
	private ImageView _specsViewer;
	private ImageView _aiViewer;
	private Circle _aiCircle;
	private Image _accept;
	private Image _reject;
	private Image _thinking;
	private ScaleTransition _aiShrink;
	private ScaleTransition _aiExpand;
	private ScaleTransition _circleExpand;
	private FadeTransition _circleFade;
	private Timeline _aiRotate;
	private Boolean _justFilled;
	private int _enterInt;
	private Boolean _enterable;
	private Boolean _displayMultipliers;
	private RotateTransition _rtBag;
	private RotateTransition _rtPencil;
	private ShiftDisplayHandler _rotatePencil;
	private ShiftConcealHandler _revertPencil;
	private Boolean _isShiftDown;
	private String _playerOneType;
	private String _playerTwoType;
	private Playable _playerOne;
	private Playable _playerTwo;
	private Pane _wordContainer1;
	private Pane _wordContainer2;
	private ScrollPane _s1;
	private ScrollPane _s2;
	private Pane _scorePane;
	private Label _playerOneScore;
	private Label _playerTwoScore;
	private int _oneScore;
	private int _twoScore;
	private ArrayList<Label> _playedWords;
	private double _olds1;
	private double _olds2;
	private ImageView _leftHalf;
	private ImageView _rightHalf;
	private ImageView _playerOneIcon;
	private ImageView _playerTwoIcon;
	private ImageView _background;
	private Image _human;
	private Image _ai;
	private TranslateTransition _moveLeft;
	private TranslateTransition _moveRight;

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
		// this.addSpecs();
		_root.getChildren().add(_boardPane);
		_appClass = appClass;
		_stage = appStage;
		_rotatePencil = new ShiftDisplayHandler();
		_revertPencil = new ShiftConcealHandler();
		_boardPane.addEventHandler(KeyEvent.KEY_PRESSED, _rotatePencil);
		_boardPane.addEventHandler(KeyEvent.KEY_RELEASED, _revertPencil);
		_newGame = new ScrabbleGame(this, _root, _boardPane);
		this.addPostIts();
		this.addAIGraphics();
		this.addScrollPane();
		_oneScore = 0;
		_twoScore = 0;
		_playedWords = new ArrayList<Label>();
		this.setUpIntroSequence();
	}

	public Pane getRoot() {
		return _root;
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
		_leftHalf = new ImageView(new Image("introLeft.jpg"));
		_leftHalf.setPreserveRatio(true);
		_leftHalf.setCache(true);
		_leftHalf.setFitWidth(Constants.SCENE_WIDTH/2);
		_leftHalf.setLayoutX(0);
		_leftHalf.setLayoutY(-100);
		_leftHalf.setOpacity(0);
		_rightHalf = new ImageView(new Image("introRight.jpg"));
		_rightHalf.setPreserveRatio(true);
		_rightHalf.setCache(true);
		_rightHalf.setFitWidth(Constants.SCENE_WIDTH/2);
		_rightHalf.setLayoutX(Constants.SCENE_WIDTH/2);
		_rightHalf.setLayoutY(-100);
		_rightHalf.setOpacity(0);
		_background = new ImageView(new Image("intro.jpg"));
		_background.setPreserveRatio(true);
		_background.setCache(true);
		_background.setFitWidth(Constants.SCENE_WIDTH);
		_background.setLayoutX(0);
		_background.setLayoutY(-100);
		_moveLeft = new TranslateTransition(Duration.seconds(2), _leftHalf);
		_moveLeft.setByX(-1 * Constants.SCENE_WIDTH/2 - 2);
		_moveRight = new TranslateTransition(Duration.seconds(2), _rightHalf);
		_moveRight.setByX(Constants.SCENE_WIDTH/2 + 2);
		_human = new Image("human.png");
		_ai = new Image("ai.png");
		_playerOneIcon = new ImageView(_human);
		_playerOneIcon.setOpacity(0.0);
		_playerOneIcon.setPreserveRatio(true);
		_playerOneIcon.setCache(true);
		_playerOneIcon.setFitWidth(Constants.GRID_FACTOR * 3);
		_playerOneIcon.setLayoutX(200);
		_playerOneIcon.setLayoutY(Constants.SCENE_HEIGHT/2 - Constants.GRID_FACTOR * 1.5);
		_playerOneIcon.setOnMouseClicked(new TogglePlayer(1));
		_playerTwoIcon = new ImageView(_human);
		_playerTwoIcon.setOpacity(0.0);
		_playerTwoIcon.setPreserveRatio(true);
		_playerTwoIcon.setCache(true);
		_playerTwoIcon.setFitWidth(Constants.GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutX(Constants.SCENE_WIDTH - 200 - Constants.GRID_FACTOR * 3);
		_playerTwoIcon.setLayoutY(Constants.SCENE_HEIGHT/2 - Constants.GRID_FACTOR * 1.5);
		_playerTwoIcon.setOnMouseClicked(new TogglePlayer(2));
		_root.getChildren().addAll(_background, _leftHalf, _rightHalf, _playerOneIcon, _playerTwoIcon);
		PauseTransition intro = new PauseTransition(Duration.seconds(2));
		intro.setOnFinished(new FadeHalvesHandler());
		intro.play();
		Tile m = new Tile(13);
		m.add(_root, Constants.X3 - 0.3, Constants.Y12, _newGame, "NONE");
		m.getTileViewer().setOpacity(0.0);
		
		ScaleTransition scaleM = new ScaleTransition(Duration.seconds(0.6), m.getTileViewer());
		scaleM.setByX(-0.60);
		scaleM.setByY(-0.60);
		RotateTransition rotateM = new RotateTransition(Duration.seconds(0.6), m.getTileViewer());
		rotateM.setByAngle(14);
		TranslateTransition moveM = new TranslateTransition(Duration.seconds(0.6), m.getTileViewer());
		moveM.setByX(24);
		moveM.setByY(-128);
		FadeTransition fadeM = new FadeTransition(Duration.seconds(0.6), m.getTileViewer());
		fadeM.setFromValue(0.0);
		fadeM.setToValue(1.0);
		fadeM.setOnFinished(new TileAnimator(rotateM, scaleM, moveM));
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
		i1.add(_root, Constants.X4 - 0.35, Constants.Y12, _newGame, "NONE");
		i1.getTileViewer().setOpacity(0.0);
		
		ScaleTransition scaleI1 = new ScaleTransition(Duration.seconds(0.6), i1.getTileViewer());
		scaleI1.setByX(-0.60);
		scaleI1.setByY(-0.60);
		RotateTransition rotateI1 = new RotateTransition(Duration.seconds(0.6), i1.getTileViewer());
		rotateI1.setByAngle(14);
		TranslateTransition moveI1 = new TranslateTransition(Duration.seconds(0.6), i1.getTileViewer());
		moveI1.setByX(0);
		moveI1.setByY(-124);
		FadeTransition fadeI1 = new FadeTransition(Duration.seconds(0.6), i1.getTileViewer());
		fadeI1.setFromValue(0.0);
		fadeI1.setToValue(1.0);
		fadeI1.setOnFinished(new TileAnimator(rotateI1, scaleI1, moveI1));
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
		n.add(_root, Constants.X5 - 0.35, Constants.Y12, _newGame, "NONE");
		n.getTileViewer().setOpacity(0.0);
		
		ScaleTransition scaleN = new ScaleTransition(Duration.seconds(0.6), n.getTileViewer());
		scaleN.setByX(-0.60);
		scaleN.setByY(-0.60);
		RotateTransition rotateN = new RotateTransition(Duration.seconds(0.6), n.getTileViewer());
		rotateN.setByAngle(14);
		TranslateTransition moveN = new TranslateTransition(Duration.seconds(0.6), n.getTileViewer());
		moveN.setByX(-26);
		moveN.setByY(-120);
		FadeTransition fadeN = new FadeTransition(Duration.seconds(0.6), n.getTileViewer());
		fadeN.setFromValue(0.0);
		fadeN.setToValue(1.0);
		fadeN.setOnFinished(new TileAnimator(rotateN, scaleN, moveN));
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
		i2.add(_root, Constants.X6 - 0.35, Constants.Y12, _newGame, "NONE");
		i2.getTileViewer().setOpacity(0.0);
		
		ScaleTransition scaleI2 = new ScaleTransition(Duration.seconds(0.6), i2.getTileViewer());
		scaleI2.setByX(-0.60);
		scaleI2.setByY(-0.60);
		RotateTransition rotateI2 = new RotateTransition(Duration.seconds(0.6), i2.getTileViewer());
		rotateI2.setByAngle(14);
		TranslateTransition moveI2 = new TranslateTransition(Duration.seconds(0.6), i2.getTileViewer());
		moveI2.setByX(-51);
		moveI2.setByY(-115);
		FadeTransition fadeI2 = new FadeTransition(Duration.seconds(0.6), i2.getTileViewer());
		fadeI2.setFromValue(0.0);
		fadeI2.setToValue(1.0);
		fadeI2.setOnFinished(new TileAnimator(rotateI2, scaleI2, moveI2));
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
		
		PauseTransition setUpPlayers = new PauseTransition(Duration.seconds(10));
		setUpPlayers.setOnFinished(new SetUpPlayers());
		setUpPlayers.play();
	}
	
	private class TileAnimator implements EventHandler<ActionEvent> {
		private RotateTransition _rotate;
		private ScaleTransition _scale;
		private TranslateTransition _move;
		
		public TileAnimator(RotateTransition rotate, ScaleTransition scale, TranslateTransition move) {
			_rotate = rotate;
			_scale = scale;
			_move = move;
		}
		
		@Override
		public void handle(ActionEvent event) {
			_rotate.play();
			_scale.play();
			_move.play();
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
			_enterable = true;
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
		_accept = new Image("aiaccept.png");
		_reject = new Image("aireject.png");
		_thinking = new Image("aithinking.png");
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
		_aiExpand.setFromX(1/4);
		_aiExpand.setFromY(1/4);
		_aiExpand.setToX(1);
		_aiExpand.setToY(1);
		_aiExpand.setCycleCount(1);
		
		_aiShrink = new ScaleTransition(Duration.seconds(Constants.PLACEMENT_DURATION), _aiViewer);
		_aiShrink.setFromX(1);
		_aiShrink.setFromY(1);
		_aiShrink.setToX(1/4);
		_aiShrink.setToY(1/4);
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
			//system.out.println("ANIMATE HANDLED");
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
	
	private class ResetAIHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_aiViewer.setRotate(0);
			_aiViewer.setImage(_thinking);
			event.consume();
		}

	}

	public void addPostIts() {
		_postItOneViewer = new ImageView(new Image("greenpostit.png"));
		_postItOneViewer.setCache(true);
		_postItOneViewer.setPreserveRatio(true);
		_postItOneViewer.setFitWidth(Constants.GRID_FACTOR * 4);
		_postItOneViewer.setLayoutX(Constants.GRID_FACTOR * 4);
		_postItOneViewer.setLayoutY(Constants.GRID_FACTOR * 5);
		_postItOneViewer.setOnMousePressed(new ScorePaneHandler("IN"));
		_postItOneViewer.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_postItTwoViewer = new ImageView(new Image("bluepostit.png"));
		_postItTwoViewer.setCache(true);
		_postItTwoViewer.setPreserveRatio(true);
		_postItTwoViewer.setFitWidth(Constants.GRID_FACTOR * 4);
		_postItTwoViewer.setLayoutX(Constants.GRID_FACTOR * 6);
		_postItTwoViewer.setLayoutY(Constants.GRID_FACTOR * 3);
		_postItTwoViewer.setOnMousePressed(new ScorePaneHandler("IN"));
		_postItTwoViewer.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerOneScore = new Label();
		_playerOneScore.setOpacity(0);
		_playerOneScore.setTextFill(Constants.GRAPHITE);
		_playerOneScore.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_POST_IT));
		_playerOneScore.setLayoutY(Constants.GRID_FACTOR * 6.1);
		_playerOneScore.setLayoutX(Constants.GRID_FACTOR * 5.7);
		// _playerOneScore.setRotate(-15);
		_playerOneScore.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerOneScore.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_playerTwoScore = new Label();
		_playerTwoScore.setOpacity(0);
		// _playerTwoScore.setRotate(15);
		_playerTwoScore.setTextFill(Constants.GRAPHITE);
		_playerTwoScore.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_POST_IT));
		_playerTwoScore.setLayoutY(Constants.GRID_FACTOR * 4.1);
		_playerTwoScore.setLayoutX(Constants.GRID_FACTOR * 7.9);
		_playerTwoScore.setOnMousePressed(new ScorePaneHandler("IN"));
		_playerTwoScore.setOnMouseReleased(new ScorePaneHandler("OUT"));
		_pencilViewer = new ImageView(new Image("pencil.png"));
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
		_pencilViewer.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
		_pencilViewer.addEventHandler(KeyEvent.KEY_RELEASED, new KeyHandler());
		_rtPencil = new RotateTransition(Duration.millis(190), _pencilViewer);
		_rtPencil.setByAngle(15);
		_rtPencil.setCycleCount(2);
		_rtPencil.setAutoReverse(true);
		_boardPane.getChildren().addAll(_postItOneViewer, _postItTwoViewer, _pencilViewer, _playerOneScore,
				_playerTwoScore);
	}

	public void addBag() {
		_bagViewer = new ImageView(new Image("bag.png"));
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
		_leatherViewer = new ImageView(new Image("leatherframe.png"));
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
		ImageView deskViewer = new ImageView(new Image("desktop.jpg"));
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
				if (_referee != null && _referee.getCurrentPlayer().getPlayerType() == "HUMAN" && _referee.getMoveInt() != 1 && event.isMetaDown()) {
					_referee.nextMove();
				}
			} else if (keyPressed == KeyCode.ENTER && _enterable == true) {
				if (_enterInt == 0) {
					_moveLeft.play();
					_moveRight.play();
					TranslateTransition moveLeftIcon = new TranslateTransition(Duration.seconds(2), _playerOneIcon);
					moveLeftIcon.setByX(-1 * Constants.SCENE_WIDTH/2 - 2);
					moveLeftIcon.play();
					TranslateTransition moveRightIcon = new TranslateTransition(Duration.seconds(2), _playerTwoIcon);
					moveRightIcon.setByX(Constants.SCENE_WIDTH/2 + 2);
					moveRightIcon.play();
					ScaleTransition scaleOne = new ScaleTransition(Duration.seconds(0.3), _playerOneIcon);
					scaleOne.setByX(0.3);
					scaleOne.setByY(0.3);
					scaleOne.setAutoReverse(true);
					scaleOne.play();
					ScaleTransition scaleTwo = new ScaleTransition(Duration.seconds(0.3), _playerTwoIcon);
					scaleTwo.setByX(0.3);
					scaleTwo.setByY(0.3);
					scaleTwo.setAutoReverse(true);
					scaleTwo.play();
					FadeTransition fadeLeft = new FadeTransition(Duration.seconds(1), _leftHalf);
					fadeLeft.setFromValue(1.0);
					fadeLeft.setToValue(0.0);
					//fadeLeft.play();
					FadeTransition fadeRight = new FadeTransition(Duration.seconds(1), _rightHalf);
					fadeRight.setFromValue(1.0);
					fadeRight.setToValue(0.0);
					//fadeRight.play();
					_newGame.startGamePlay();
					if (_playerOneType == "HUMAN") {
						_playerOne = new HumanPlayer("PLAYER ONE", _newGame);
					} else if (_playerOneType == "COMPUTER") {
						_playerOne = new ComputerPlayer("PLAYER ONE", _newGame);
					}
					if (_playerTwoType == "HUMAN") {
						_playerTwo = new HumanPlayer("PLAYER TWO", _newGame);
					} else if (_playerTwoType == "COMPUTER") {
						_playerTwo = new ComputerPlayer("PLAYER TWO", _newGame);
					}
					_referee = new Referee(_newGame, _playerOne, _playerTwo);
					if (_playerOneType == "COMPUTER" && _playerTwoType == "COMPUTER") {
						_playerOneScore.setOpacity(1.0);
						_playerTwoScore.setOpacity(1.0);
					}
					_newGame.addReferee(_referee);
					_rtPencil.play();
					PaneOrganizer.this.updateScore();
					// _newGame.setUpTiles();
				} else if (_enterInt > 0) {
					//system.out.printf("\nHANDLE, ENTER INT = %s\n", _enterInt);
					Word newestWord = _referee.getCurrentPlayer().getNewestWord();
					if (newestWord.isPlayable()) {
						//system.out.printf("ADDING TO BOARD on enterInt %s\n", _enterInt);
						newestWord.addToBoard();
						_referee.incrementWordsPlayedBy(1);
						PaneOrganizer.this.updateLabels();
						Label word = new Label(newestWord.getLetters());
						word.setTextFill(Constants.GREEN);
						word.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
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
						value.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
						value.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() - 1));
						value.setLayoutX(315 - 15 - 25 * digitmult);

						if (_referee.currentPlayer() == "PLAYER ONE") {
							_wordContainer1.getChildren().addAll(word, value);
						} else if (_referee.currentPlayer() == "PLAYER TWO") {
							_wordContainer2.getChildren().addAll(word, value);
						}

						ArrayList<Word> crosses = newestWord.getAllCrosses();
						for (int i = 0; i < crosses.size(); i++) {
							Word thisCross = crosses.get(i);
							Label newCrossWord = new Label(thisCross.getLetters());
							newCrossWord.setTextFill(Constants.GREEN);
							newCrossWord.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
							newCrossWord.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
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
							newCrossValue.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
							newCrossValue.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
							newCrossValue.setLayoutX(315 - 15 - 25 * digitmult);
							if (_referee.currentPlayer() == "PLAYER ONE") {
								_wordContainer1.getChildren().addAll(newCrossWord, newCrossValue);
							} else if (_referee.currentPlayer() == "PLAYER TWO") {
								_wordContainer2.getChildren().addAll(newCrossWord, newCrossValue);
							}
						}
						_referee.incrementWordsPlayedBy(crosses.size());
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
									PauseTransition delayFlash = new PauseTransition(Duration.seconds(Constants.FLASH_SPACING_DURATION * (i + 1)));
									delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), validity));
									delayFlash.play();
								}
							}
						}
					}
				}
				_enterInt++;
			} else if (keyPressed == KeyCode.R) {
				PaneOrganizer.this.reset();
			} else if (keyPressed == KeyCode.A) {
				boolean autoReset = _newGame.getAutoReset();
				if (autoReset == true) {
					_newGame.setAutoReset(false);
					System.out.println("AUTO RESET OFF");
				} else if (autoReset == false) {
					_newGame.setAutoReset(true);
					System.out.println("AUTO RESET ON");
				}
			} else if (keyPressed == KeyCode.M) {
				if (_displayMultipliers == false) {
					_newGame.fadeOutOtherSquares("GHOST");
					_displayMultipliers = true;
					_newGame.removeDiamond();
					// PaneOrganizer.this.playFlash();
				} else if (_displayMultipliers == true) {
					_displayMultipliers = false;
					_newGame.fadeInOtherSquares("GHOST");
					_newGame.addDiamond();
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
				_newGame.getReferee().getCurrentPlayer().getNewestWord().clear();
				_newGame.resetRackOne();
				_newGame.resetRackTwo();
				System.out.printf("There are %s tiles on the board\n", _newGame.getTilesOnBoard().size());
			}
			event.consume();
		}

	}
	
	public void addWordAI(Word newestWord) {
		//system.out.printf("ADDING TO BOARD on enterInt %s\n", _enterInt);
		newestWord.addToBoardAI();
		_referee.incrementWordsPlayedBy(1);
		PaneOrganizer.this.updateLabels();
		Label word = new Label(newestWord.getLetters());
		word.setTextFill(Constants.GREEN);
		word.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
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
		value.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
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
				newCrossWord.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
				newCrossWord.setLayoutY(-3 + (Constants.GRID_FACTOR - 4) * (_referee.getNumWordsPlayed() + i));
				newCrossWord.setLayoutX(15);
				_playedWords.add(newCrossWord);
				int crossValue = thisCross.getValue();
				//system.out.printf("\nCROSS %s = %s with a value of %s and size %s\n\n", i + 1, thisCross.getLetters(), crossValue, thisCross.getTiles().size());
				_referee.addToScore(crossValue);
				if (crossValue > 9) {
					digitmult = 2;
				} else if (crossValue > 99) {
					digitmult = 3;
				}
				Label newCrossValue = new Label(String.valueOf(crossValue));
				_playedWords.add(newCrossValue);
				newCrossValue.setTextFill(Constants.GREEN);
				newCrossValue.setFont(Font.loadFont(Constants.DOMESTIC_MANNERS, Constants.FONT_SIZE_WORD_LIST));
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
				PauseTransition delayFlash = new PauseTransition(Duration.seconds(Constants.FLASH_SPACING_DURATION * (i + 1)));
				delayFlash.setOnFinished(new PlayFlashHandler(crosses.get(i), "ADDED"));
				delayFlash.play();
			}
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
					_newGame.pauseGamePlay();
					FadeTransition fadeIn = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION), _scorePane);
					fadeIn.setFromValue(0.0);
					fadeIn.setToValue(1.0);
					fadeIn.play();
					FadeTransition fadeIn1 = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_playerOneScore);
					fadeIn1.setFromValue(0.0);
					fadeIn1.setToValue(1.0);
					fadeIn1.play();
					FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_playerTwoScore);
					fadeIn2.setFromValue(0.0);
					fadeIn2.setToValue(1.0);
					fadeIn2.play();
				} else if (_id == "OUT") {
					_olds1 = _s1.getVvalue();
					_olds2 = _s2.getVvalue();
					//system.out.printf("olds1 = %s, olds2 = %s\n", _olds1, _olds2);
					_newGame.startGamePlay();
					FadeTransition fadeOut = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_scorePane);
					fadeOut.setFromValue(1.0);
					fadeOut.setToValue(0.0);
					fadeOut.setOnFinished(new ScorePaneRemoveHandler());
					fadeOut.play();
					FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_playerOneScore);
					fadeOut1.setFromValue(1.0);
					fadeOut1.setToValue(0.0);
					fadeOut1.play();
					FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(Constants.SCORE_FADE_DURATION),
							_playerTwoScore);
					fadeOut2.setFromValue(1.0);
					fadeOut2.setToValue(0.0);
					fadeOut2.play();
				}
			}
			event.consume();
		}

	}

	private class ScrollOneRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			//system.out.println("HANDLE");
			_s1.setVvalue(_olds1);
			//system.out.printf("%s\n", _s1.getVvalue());
			event.consume();
		}

	}
	
	private class ScrollTwoRecallHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			//system.out.println("HANDLE");
			_s2.setVvalue(_olds2);
			//system.out.printf("%s\n", _s2.getVvalue());
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
				_newGame.removeDiamond();
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
				_newGame.addDiamond();
				// _rt = new RotateTransition(Duration.millis(190), _pencilViewer);
				// _rt.setByAngle(-5);
				// _rt.setCycleCount(1);
				// _rt.play();
			}
		}

	}

	public void reset() {
		// //system.out.print(Constants.ESC + "2J");
		_appClass.start(_stage);
	}

	public void manageDraw(String id) {
		_justFilled = false;
		if (_newGame.getRackOneSize() < 7 && id == "PLAYER ONE") {
			_newGame.refillRackOne();
			_justFilled = true;
		}
		if (_newGame.getRackTwoSize() < 7 && id == "PLAYER TWO") {
			_newGame.refillRackTwo();
			_justFilled = true;
		}
		if (_justFilled == true && !_newGame.bagEmpty()) {
			_newGame.printBagSize();
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

	public void removeBag() {
		TranslateTransition removeBag = new TranslateTransition(Duration.seconds(3), _bagViewer);
		removeBag.setByX(-375);
		removeBag.setByY(100);
		removeBag.play();
	}

//	public void setAIOpacity(double d) {
//		_aiViewer.setOpacity(d);
//	}

}