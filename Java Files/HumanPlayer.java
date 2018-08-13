package Scrabble;

public class HumanPlayer implements Playable {
	private Word _newestWord;
	private String _playerNumber;
	private ScrabbleGame _scrabbleGame;

	public HumanPlayer(String playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
	}

	public String getPlayerType() {
		return "HUMAN";
	}

	public String getPlayerNumber() {
		return _playerNumber;
	}

	public void makeMove() {
		_newestWord = new Word(_scrabbleGame, _playerNumber);
	}

	public Word getNewestWord() {
		return _newestWord;
	}

}