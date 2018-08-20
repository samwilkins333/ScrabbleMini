package Scrabble;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

class Referee {
	private final ScrabbleGame _scrabbleGame;
	private final Playable _playerOne;
	private final Playable _playerTwo;
	private Playable _currentPlayer;
	private int _moveInt;
	private int _wordsPlayed1;
	private int _wordsPlayed2;
	private int _playerOneScore;
	private int _playerTwoScore;
	private boolean _thinking;
	private boolean _firstMoveMade;

	Referee(ScrabbleGame scrabbleGame, Playable playerOne, Playable playerTwo) {
		_scrabbleGame = scrabbleGame;

		_playerOne = playerOne;
		_playerTwo = playerTwo;
		_currentPlayer = _playerTwo;

		_thinking = false;
		_firstMoveMade = false;

		_moveInt = 0;
		_wordsPlayed1 = 0;
		_wordsPlayed2 = 0;

		_playerOneScore = 0;
		_playerTwoScore = 0;

		this.nextMove();
	}

	void incrementWordsPlayedBy(int num) {
		if (_currentPlayer == _playerOne) {
			_wordsPlayed1 += num;
		} else {
			_wordsPlayed2 +=  num;
		}
	}

	int getNumWordsPlayed() {
		return _currentPlayer == _playerOne ? _wordsPlayed1 : _wordsPlayed2;
	}

	void addToScore(int wordValue) {
		if (_currentPlayer == _playerOne) {
			_playerOneScore += wordValue;
		} else {
			_playerTwoScore += wordValue;
		}
	}

	ArrayList<Tile> getCurrentPlayerRack() {
		return _scrabbleGame.getPlayerRack(_currentPlayer.getPlayerNumber());
	}
	
	private Winner checkWinner() {
		if (_playerOneScore == _playerTwoScore) return Winner.Draw;
		return _playerOneScore > _playerTwoScore ? Winner.One : Winner.Two;
	}

	int getPlayerOneScore() {
		return _playerOneScore;
	}

	int getPlayerTwoScore() {
		return _playerTwoScore;
	}
	
	boolean isThinking() {
		return _thinking;
	}
	
	private class EndGameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			ArrayList<Tile> rackOne = _scrabbleGame.getPlayerRack(PlayerNum.One);
			ArrayList<Tile> rackTwo = _scrabbleGame.getPlayerRack(PlayerNum.Two);

			if (rackOne.size() > 0) for (Tile aRackOne : rackOne) aRackOne.fadeOut();
			if (rackTwo.size() > 0) for (Tile aRackTwo : rackTwo) aRackTwo.fadeOut();

			_scrabbleGame.displayOutcome(Referee.this.checkWinner());
			event.consume();
		}

	}
	
	private void processAdditives() {
		ArrayList<Tile> rack = _scrabbleGame.getPlayerRack(PlayerNum.Two);
		if (rack.size() > 0) {
			int sum = 0;
			for (Tile thisTile : rack) {
				sum += thisTile.getValue();
				thisTile.playFlash(WordAddition.Success);
			}
			_playerOneScore += sum;
		}
		rack = _scrabbleGame.getPlayerRack(PlayerNum.One);
		if (rack.size() > 0) {
			int sum = 0;
			for (Tile thisTile : rack) {
				sum += thisTile.getValue();
				thisTile.playFlash(WordAddition.Success);
			}
			_playerTwoScore += sum;
		}

		_scrabbleGame.getOrganizer().updateScore();

		PauseTransition subs = new PauseTransition(Duration.seconds(0.8));
		subs.setOnFinished(new SubsHandler());
		subs.play();
	}
	
	private void processSubtractives() {
		ArrayList<Tile> rack = _scrabbleGame.getPlayerRack(PlayerNum.One);
		if (rack.size() > 0) {
			int sum = 0;
			for (Tile thisTile : rack) {
				sum += thisTile.getValue();
				thisTile.playFlash(WordAddition.Failure);
			}
			_playerOneScore -= sum;
		}
		rack = _scrabbleGame.getPlayerRack(PlayerNum.Two);
		if (rack.size() > 0) {
			int sum = 0;
			for (Tile thisTile : rack) {
				sum = sum + thisTile.getValue();
				thisTile.playFlash(WordAddition.Failure);
			}
			_playerTwoScore -= sum;
		}
		_scrabbleGame.getOrganizer().updateScore();
		PauseTransition end = new PauseTransition(Duration.seconds(0.8));
		end.setOnFinished(new EndGameHandler());
		end.play();
	}
	
	private class SubsHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			Referee.this.processSubtractives();
			event.consume();
		}

	}

	void nextMove() {
		_scrabbleGame.updateAlreadyPlayed();
		_scrabbleGame.resetEnterInt();

		// GAME OVER - Current player has played all tiles and cannot draw any more
		if (_scrabbleGame.tileBagIsEmpty() && this.getCurrentPlayerRack().isEmpty()) {
			_scrabbleGame.pauseGamePlay();
			_thinking = false;
			_scrabbleGame.getOrganizer().displayScoreLabels();
			_scrabbleGame.shiftTiles(PlayerNum.One);
			_scrabbleGame.shiftTiles(PlayerNum.Two);

			_scrabbleGame.fadeRacks(_currentPlayer == _playerOne ? PlayerNum.Two : PlayerNum.One, Direction.In);

			this.processAdditives();
			return;
		}

		_moveInt++;
		if (_moveInt == 1 || _moveInt == 2) _scrabbleGame.rotateBag();

		_scrabbleGame.declareEnterable();

		boolean isPlayerOne = _currentPlayer == _playerOne;
		_scrabbleGame.fadeRacks(isPlayerOne ? PlayerNum.Two : PlayerNum.One, Direction.In);

		if (_moveInt > 1) _scrabbleGame.fadeRacks(isPlayerOne ? PlayerNum.One : PlayerNum.Two, Direction.Out);

		_scrabbleGame.shiftTiles(isPlayerOne ? PlayerNum.Two : PlayerNum.One);
		_scrabbleGame.manageDraw(isPlayerOne ? PlayerNum.Two : PlayerNum.One);

		// Actually switch players
		_currentPlayer = isPlayerOne ? _playerTwo : _playerOne;

		if (_currentPlayer.isHuman()) {
			_thinking = false;
			_currentPlayer.makeMove();
		} else {
			_thinking = true;
			_scrabbleGame.aiSequence(1, null);
			PauseTransition delayNextTurn = new PauseTransition(Duration.seconds(0.7 * 7));
			delayNextTurn.setOnFinished(new NextTurnHandler());
			delayNextTurn.play();
		}
	}
	
	private class NextTurnHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_currentPlayer.makeMove();
			event.consume();
		}

	}

	Playable getCurrentPlayerInstance() {
		return _currentPlayer;
	}

	PlayerNum getCurrentPlayer() {
		return _currentPlayer == _playerOne ? PlayerNum.One : PlayerNum.Two;
	}

	boolean isFirstMoveMade() {
		return _firstMoveMade;
	}

	void DeclareFirstMoveMade() {
		_firstMoveMade = true;
	}

}