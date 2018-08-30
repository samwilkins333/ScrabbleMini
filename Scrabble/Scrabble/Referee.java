package Scrabble;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import static Scrabble.Util.*;

public class Referee {
	private ScrabbleGame _scrabbleGame;
	private Playable _playerOne;
	private Playable _playerTwo;
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
		_moveInt = 0;
		_firstMoveMade = false;
		_wordsPlayed1 = 0;
		_wordsPlayed2 = 0;
		_playerOneScore = 0;
		_playerTwoScore = 0;
		this.nextMove();
	}

	void incrementWordsPlayedBy(int num) {
		if (_currentPlayer == _playerOne) {
			_wordsPlayed1 = _wordsPlayed1 + num;
		} else if (_currentPlayer == _playerTwo) {
			_wordsPlayed2 = _wordsPlayed2 + num;
		}
	}

	int getNumWordsPlayed() {
		if (_currentPlayer == _playerOne) {
			return _wordsPlayed1;
		} else {
			return _wordsPlayed2;
		}
	}

	void addToScore(int wordValue) {
		if (_currentPlayer == _playerOne)
			_playerOneScore += wordValue;
		else
			_playerTwoScore += wordValue;
	}

	private boolean currentRackIsEmpty() {
		return _scrabbleGame.getRackFor(_currentPlayer.getPlayerNumber()).isEmpty();
	}
	
	ArrayList<Tile> getCurrentPlayerRack() {
		return _scrabbleGame.getRackFor(_currentPlayer.getPlayerNumber());
	}
	
	private String checkWinner() {
		if (_playerOneScore > _playerTwoScore) {
			return "PLAYER ONE";
		} else if (_playerOneScore < _playerTwoScore) {
			return "PLAYER TWO";
		} else {
			return "TIE";
		}
	}

	int getPlayerOneScore() {
		return _playerOneScore;
	}

	int getPlayerTwoScore() {
		return _playerTwoScore;
	}
	
	public boolean isThinking() {
		return _thinking;
	}
	
	private class EndGameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_scrabbleGame.fadeRacks(PlayerNumber.One, Direction.Out);
			_scrabbleGame.fadeRacks(PlayerNumber.Two, Direction.Out);
			_scrabbleGame.displayOutcome(Referee.this.checkWinner());
			event.consume();
		}

	}

	/*
	 * At end of game, adds the sum of the values of any remaining tile pieces
	 */
	private void processAdditives() {
		_scrabbleGame.getRackFor(PlayerNumber.Two).forEach(t -> {
			_playerOneScore += t.getValue();
			t.playFlash(Outcome.Added);
		});

		_scrabbleGame.getRackFor(PlayerNumber.One).forEach(t -> {
			_playerTwoScore += t.getValue();
			t.playFlash(Outcome.Added);
		});

		_scrabbleGame.getOrganizer().updateScore();

		PauseTransition subs = new PauseTransition(Duration.seconds(0.8));
		subs.setOnFinished(event -> processSubtractives());
		subs.play();
	}
	
	private void processSubtractives() {
		_scrabbleGame.getRackFor(PlayerNumber.One).forEach(t -> {
			_playerOneScore -= t.getValue();
			t.playFlash(Outcome.Failed);
		});

		_scrabbleGame.getRackFor(PlayerNumber.Two).forEach(t -> {
			_playerTwoScore -= t.getValue();
			t.playFlash(Outcome.Failed);
		});

		_scrabbleGame.getOrganizer().updateScore();
		PauseTransition end = new PauseTransition(Duration.seconds(0.8));
		end.setOnFinished(new EndGameHandler());
		end.play();
	}

	void nextMove() {
		boolean gameOver = false;
		_scrabbleGame.updateAlreadyPlayed();
		_scrabbleGame.resetEnterInt();
		System.out.printf("There are %s tiles on the board\n", _scrabbleGame.getTilesOnBoard().size());
		if (_scrabbleGame.tileBagIsEmpty() && currentRackIsEmpty()) {
			_scrabbleGame.pauseGamePlay();
			_thinking = false;
			_scrabbleGame.getOrganizer().displayScoreLabels();

			_scrabbleGame.shiftTiles(PlayerNumber.One);
			_scrabbleGame.shiftTiles(PlayerNumber.Two);
			_scrabbleGame.fadeRacks(invert(_currentPlayer.getPlayerNumber()), Direction.In);

			processAdditives();
			System.out.println("GAME OVER!!!!");
			gameOver = true;
		}
		if (!gameOver) {
			_moveInt = _moveInt + 1;
			if (_moveInt == 1 || _moveInt == 2) {
				_scrabbleGame.rotateBag();
			}
			_scrabbleGame.setEnterable(true);
			if (_currentPlayer == _playerOne) {
				_scrabbleGame.fadeRacks(PlayerNumber.Two, Direction.In);
				if (_moveInt > 1) {
					_scrabbleGame.fadeRacks(PlayerNumber.One, Direction.Out);
				}
				_currentPlayer = _playerTwo;
				_scrabbleGame.shiftTiles(PlayerNumber.Two);
				_scrabbleGame.manageDraw("PLAYER TWO");
			} else if (_currentPlayer == _playerTwo) {
				_scrabbleGame.fadeRacks(PlayerNumber.One, Direction.In);
				if (_moveInt > 1) {
					_scrabbleGame.fadeRacks(PlayerNumber.Two, Direction.Out);
				}
				_currentPlayer = _playerOne;
				_scrabbleGame.shiftTiles(PlayerNumber.One);
				_scrabbleGame.manageDraw("PLAYER ONE");
			}
			if (_currentPlayer.getPlayerType() == PlayerType.Human) {
				_thinking = false;
				_currentPlayer.makeMove();
			} else if (_currentPlayer.getPlayerType() == PlayerType.AI) {
				_thinking = true;
				_scrabbleGame.aiSequence(1, null);
				PauseTransition delayNextTurn = new PauseTransition(Duration.seconds(0.7 * 7));
				delayNextTurn.setOnFinished(new NextTurnHandler());
				delayNextTurn.play();	
			}
		}
	}
	
	private class NextTurnHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_currentPlayer.makeMove();
			event.consume();
		}

	}

	Playable getCurrentPlayer() {
		return _currentPlayer;
	}

	PlayerNumber currentPlayer() {
		return (_currentPlayer == _playerOne) ? PlayerNumber.One : PlayerNumber.Two;
	}

	boolean firstMoveMade() {
		return _firstMoveMade;
	}

	void setFirstMoveIsMade() {
		_firstMoveMade = true;
	}

}