package Scrabble;

public class HumanPlayer implements Playable {
	private Word _newestWord;
	private PlayerNum _playerNumber;
	private ScrabbleGame _scrabbleGame;

	HumanPlayer(PlayerNum playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
	}

	public boolean isHuman() {
		return true;
	}

	public PlayerNum getPlayerNumber() {
		return _playerNumber;
	}

	public void makeMove() {
		_newestWord = new Word(_scrabbleGame, _playerNumber);
	}

	public Word getNewestWord() {
		return _newestWord;
	}

}