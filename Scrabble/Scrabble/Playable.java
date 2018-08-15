package Scrabble;

public interface Playable {

	void makeMove();
	boolean isHuman();
	PlayerNum getPlayerNumber();
	Word getNewestWord();

}