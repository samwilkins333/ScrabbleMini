package Scrabble;

public interface Playable {

	void makeMove();
	PlayerType getPlayerType();
	PlayerNumber getPlayerNumber();
	Word getNewestWord();

}