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

	public Referee(ScrabbleGame scrabbleGame, Playable playerOne, Playable playerTwo) {
		_scrabbleGame = scrabbleGame;
		_playerOne = playerOne;
		_playerTwo = playerTwo;
		_currentPlayer = _playerTwo;
		_thinking = false;
		_moveInt = 0;
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
			return "PLAYERONEWINS";
		} else if (_playerOneScore < _playerTwoScore) {
			return "PLAYERTWOWINS";
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
			_scrabbleGame.displayOutcome(Referee.this.checkWinner());
			event.consume();
		}

	}
	
	public void processRemnants(String winner) {
		ArrayList<Tile>
		if (winner == "PLAYER ONE") {
			for (int i = 0; i < _) {
				
			}
		} else if (winner == "PLAYER TWO") {
			
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
			PauseTransition delayPlacement = new PauseTransition(Duration.seconds(2.2));
			delayPlacement.setOnFinished(new EndGameHandler());
			delayPlacement.play();
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
				_scrabbleGame.think("PLAYER TWO");
			} else if (_currentPlayer == _playerTwo) {
				_scrabbleGame.fadeRacks(1, "IN");
				if (_moveInt > 1) {
					_scrabbleGame.fadeRacks(2, "OUT");		
				}
				_currentPlayer = _playerOne;
				_scrabbleGame.shiftTiles("PLAYER ONE");
				_scrabbleGame.manageDraw("PLAYER ONE");
				_scrabbleGame.think("PLAYER ONE");
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

	public int getMoveInt() {
		return _moveInt;
	}

}