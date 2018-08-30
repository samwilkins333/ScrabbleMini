package Scrabble;

public class HumanPlayer implements Playable {
	private Word _newestWord;
	private PlayerNumber _playerNumber;
	private ScrabbleGame _scrabbleGame;

	HumanPlayer(PlayerNumber playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
	}

	public PlayerType getPlayerType() {
		return PlayerType.Human;
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