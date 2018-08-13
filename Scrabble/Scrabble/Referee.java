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

	public Referee(ScrabbleGame scrabbleGame, Playable playerOne, Playable playerTwo) {
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

	public void incrementWordsPlayedBy(int num) {
		if (_currentPlayer == _playerOne) {
			_wordsPlayed1 = _wordsPlayed1 + num;
		} else if (_currentPlayer == _playerTwo) {
			_wordsPlayed2 = _wordsPlayed2 + num;
		}
	}

	public int getNumWordsPlayed() {
		if (_currentPlayer == _playerOne) {
			return _wordsPlayed1;
		} else {
			return _wordsPlayed2;
		}
	}

	public void addToScore(int wordValue) {
		if (_currentPlayer == _playerOne) {
			_playerOneScore = _playerOneScore + wordValue;
		} else {
			_playerTwoScore = _playerTwoScore + wordValue;
		}
	}

	public int getPlayerScore() {
		if (_currentPlayer == _playerOne) {
			return _playerOneScore;
		} else {
			return _playerTwoScore;
		}
	}
	
	public boolean currentRackIsEmpty() {
		if (_currentPlayer == _playerOne && _scrabbleGame.getRackOneSize() == 0) {
			return true;
		} else if (_currentPlayer == _playerTwo && _scrabbleGame.getRackTwoSize() == 0) {
			return true;
		}
		return false;
	}
	
	public ArrayList<Tile> getCurrentPlayerRack() {
		if (_currentPlayer == _playerOne) {
			return _scrabbleGame.getPlayerOneRack();
		} else if (_currentPlayer == _playerTwo) {
			return _scrabbleGame.getPlayerTwoRack();
		} else {
			return null;
		}
	}
	
	public String checkWinner() {
		if (_playerOneScore > _playerTwoScore) {
			return "PLAYER ONE";
		} else if (_playerOneScore < _playerTwoScore) {
			return "PLAYER TWO";
		} else {
			return "TIE";
		}
	}

	public int getPlayerOneScore() {
		return _playerOneScore;
	}

	public int getPlayerTwoScore() {
		return _playerTwoScore;
	}
	
	public boolean isThinking() {
		return _thinking;
	}
	
	private class EndGameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			ArrayList<Tile> rackOne = _scrabbleGame.getPlayerOneRack();
			ArrayList<Tile> rackTwo = _scrabbleGame.getPlayerTwoRack();
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
	
	public void processAdditives() {
		ArrayList<Tile> rack = null;
		rack = _scrabbleGame.getPlayerTwoRack();
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("ADDED");
			}
			_playerOneScore = _playerOneScore + sum;
		}
		rack = _scrabbleGame.getPlayerOneRack();
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
	
	public void processSubtractives() {
		ArrayList<Tile> rack = null;
		rack = _scrabbleGame.getPlayerOneRack();
		if (rack.size() > 0) {
			int sum = 0;
			for (int i = 0; i < rack.size(); i++) {
				Tile thisTile = rack.get(i);
				sum = sum + thisTile.getValue();
				thisTile.playFlash("FAILED");
			}
			_playerOneScore = _playerOneScore - sum;
		}
		rack = _scrabbleGame.getPlayerTwoRack();
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

	public void nextMove() {
		boolean gameOver = false;
		_scrabbleGame.updateAlreadyPlayed();
		_scrabbleGame.resetEnterInt();
		System.out.printf("There are %s tiles on the board\n", _scrabbleGame.getTilesOnBoard().size());
		if (_scrabbleGame.tileBagIsEmpty() && this.currentRackIsEmpty()) {
			_scrabbleGame.pauseGamePlay();
			_thinking = false;
			_scrabbleGame.getOrganizer().displayScoreLabels();
			_scrabbleGame.shiftTiles("PLAYER ONE");
			_scrabbleGame.shiftTiles("PLAYER TWO");
			if (_currentPlayer == _playerOne) {
				_scrabbleGame.fadeRacks(2, "IN");
			} else if (_currentPlayer == _playerTwo) {
				_scrabbleGame.fadeRacks(1, "IN");
			}
			this.processAdditives();
			System.out.println("GAME OVER!!!!");
			gameOver = true;
		}
		if (gameOver == false) {
			_moveInt = _moveInt + 1;
			if (_moveInt == 1 || _moveInt == 2) {
				_scrabbleGame.rotateBag();
			}
			_scrabbleGame.setEnterable(true);
			if (_currentPlayer == _playerOne) {
				_scrabbleGame.fadeRacks(2, "IN");
				if (_moveInt > 1) {
					_scrabbleGame.fadeRacks(1, "OUT");		
				}
				_currentPlayer = _playerTwo;
				_scrabbleGame.shiftTiles("PLAYER TWO");
				_scrabbleGame.manageDraw("PLAYER TWO");
			} else if (_currentPlayer == _playerTwo) {
				_scrabbleGame.fadeRacks(1, "IN");
				if (_moveInt > 1) {
					_scrabbleGame.fadeRacks(2, "OUT");		
				}
				_currentPlayer = _playerOne;
				_scrabbleGame.shiftTiles("PLAYER ONE");
				_scrabbleGame.manageDraw("PLAYER ONE");
			}
			if (_currentPlayer.getPlayerType() == "HUMAN") {
				_thinking = false;
				_currentPlayer.makeMove();
			} else if (_currentPlayer.getPlayerType() == "COMPUTER") {
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

	public Playable getCurrentPlayer() {
		return _currentPlayer;
	}

	public String currentPlayer() {
		if (_currentPlayer == _playerOne) {
			return "PLAYER ONE";
		} else {
			return "PLAYER TWO";
		}
	}

	public boolean firstMoveMade() {
		return _firstMoveMade;
	}

	public void setFirstMoveIsMade() {
		_firstMoveMade = true;
	}

}