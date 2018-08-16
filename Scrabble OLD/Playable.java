package Scrabble;

public interface Playable {

	public void makeMove();
	public String getPlayerType();
	public String getPlayerNumber();
	public Word getNewestWord();

}