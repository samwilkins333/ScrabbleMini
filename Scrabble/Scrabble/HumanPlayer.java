package Scrabble;

public class HumanPlayer implements Playable {
	private Word _newestWord;
	private PlayerNumber _playerNumber;
	private ScrabbleGame _scrabbleGame;

	HumanPlayer(PlayerNumber playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
	}

	public String getPlayerType() {
		return "HUMAN";
	}

	public PlayerNumber getPlayerNumber() {
		return _playerNumber;
	}

	public void makeMove() {
		_newestWord = new Word(_scrabbleGame, _playerNumber);
	}

	public Word getNewestWord() {
		return _newestWord;
	}

}