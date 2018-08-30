package Scrabble;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

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
			ArrayList<Tile> rackOne = _scrabbleGame.getRackFor(PlayerNumber.One);
			ArrayList<Tile> rackTwo = _scrabbleGame.getRackFor(PlayerNumber.Two);
			if (rackOne.size() > 0) {
				for (int i = 0; i < rackOne.size(); i++) {
					 rackOne.get(i).fadeOut();
				}
			}
			if (rackTwo.size() > 0) {
				for (int i = 0; i < rackTwo.size(); i++) {
					 rackTwo.get(i).fadeOut();
				}
			}
			_scrabbleGame.displayOutcome(Referee.this.checkWinner());
			event.consume();
		}

	}
	
	private void processAdditives() {
		ArrayList<Tile> rack = null;
		rack = _scrabbleGame.getRackFor(PlayerNumber.Two);
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("ADDED");
			}
			_playerOneScore = _playerOneScore + sum;
		}
		rack = _scrabbleGame.getRackFor(PlayerNumber.One);
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("ADDED");
			}
			_playerTwoScore = _playerTwoScore + sum;
		}
		_scrabbleGame.getOrganizer().updateScore();
		PauseTransition subs = new PauseTransition(Duration.seconds(0.8));
		subs.setOnFinished(new SubsHandler());
		subs.play();
	}
	
	private void processSubtractives() {
		ArrayList<Tile> rack = null;
		rack = _scrabbleGame.getRackFor(PlayerNumber.One);
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("FAILED");
			}
			_playerOneScore = _playerOneScore - sum;
		}
		rack = _scrabbleGame.getRackFor(PlayerNumber.Two);
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("FAILED");
			}
			_playerTwoScore = _playerTwoScore - sum;
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
		boolean gameOver = false;
		_scrabbleGame.updateAlreadyPlayed();
		_scrabbleGame.resetEnterInt();
		System.out.printf("There are %s tiles on the board\n", _scrabbleGame.getTilesOnBoard().size());
		if (_scrabbleGame.tileBagIsEmpty() && this.currentRackIsEmpty()) {
			_scrabbleGame.pauseGamePlay();
			_thinking = false;
			_scrabbleGame.getOrganizer().displayScoreLabels();
			_scrabbleGame.shiftTiles(PlayerNumber.One);
			_scrabbleGame.shiftTiles(PlayerNumber.Two);
			if (_currentPlayer == _playerOne) {
				_scrabbleGame.fadeRacks(PlayerNumber.Two, Direction.In);
			} else if (_currentPlayer == _playerTwo) {
				_scrabbleGame.fadeRacks(PlayerNumber.One, Direction.In);
			}
			this.processAdditives();
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