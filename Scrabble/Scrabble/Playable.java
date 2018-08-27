package Scrabble;

public interface Playable {

	void makeMove();
	String getPlayerType();
	PlayerNumber getPlayerNumber();
	Word getNewestWord();

}