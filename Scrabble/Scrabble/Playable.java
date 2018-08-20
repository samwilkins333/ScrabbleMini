package Scrabble;

interface Playable {

	void makeMove();
	boolean isHuman();
	PlayerNum getPlayerNumber();
	Word getNewestWord();

}