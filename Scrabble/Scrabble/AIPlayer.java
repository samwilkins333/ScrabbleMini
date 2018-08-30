package Scrabble;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
//import java.util.HashSet;
//import java.util.Set;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class AIPlayer implements Playable {
	private Word _newestWord;
	private PlayerNumber _playerNumber;
	private ScrabbleGame _scrabbleGame;
	private String _rack;
	private String _kernel;
	private ArrayList<Word> _validWords;
	private ArrayList<String> _validStrings;
	private ArrayList<String> _prefixes;
	private ArrayList<String> _suffixes;
	private int _concatInt;
	private int _numHooksTested;
	private int _firstXkernel;
	private int _firstYkernel;
	private HashMap<Integer, HashMap<String, Integer>> _prefixCrosses;
	private HashMap<Integer, HashMap<String, Integer>> _suffixCrosses;
	private HashMap<String, ArrayList<String>> _specificSuffixes;
	private HashMap<Integer, ArrayList<String>> _invalidLettersAndPrefixIndices;
	private HashMap<Integer, ArrayList<String>> _invalidLettersAndSuffixIndices;
	private HashMap<String, String> _precedingKernels;
	private HashMap<String, String> _succeedingKernels;
	private PauseTransition _delayAIRemoval;
	private int _numBlankSlots;

	AIPlayer(PlayerNumber playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
		_concatInt = 0;
		setUpDelay();
	}	

	private void setUpDelay() {
		_delayAIRemoval = new PauseTransition(Duration.seconds(1.5));
		_delayAIRemoval.setOnFinished(new FadeAIHandler());
	}
	
	private class FadeAIHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			_scrabbleGame.aiSequence(3, null);
			event.consume();
		}

	}

	public PlayerType getPlayerType() {
		return PlayerType.AI;
	}

	public void makeMove() {
		boolean firstMoveMade = _scrabbleGame.getReferee().firstMoveMade();
		if (firstMoveMade == false) {
			_newestWord = this.getBestFirstWord();
		} else if (firstMoveMade == true) {
			_newestWord = this.getBestWord();
		}
		if (_newestWord != null) {
			if (firstMoveMade == false) {
				_scrabbleGame.getReferee().setFirstMoveIsMade();
			}
			System.out.println("BEST WORD = " + _newestWord.getLetters());
			_newestWord.addScrabbleGame(_scrabbleGame);
			_scrabbleGame.aiSequence(2, "ACCEPT");
			_delayAIRemoval.play();
			ArrayList<Tile> myRack = _scrabbleGame.getReferee().getCurrentPlayerRack();
			String appropriateLetters = "";
			if (firstMoveMade == false) {
				appropriateLetters = _newestWord.getLetters();
			} else if (firstMoveMade == true) {
				appropriateLetters = _newestWord.getNewBoardLetters();
			}
			ArrayList<Tile> myTransfers = _scrabbleGame.transferTilesFromRack(appropriateLetters, myRack);
			_newestWord.addTiles(myTransfers);
			_newestWord.arrangeTilesOnBoard();
		} else {
			_scrabbleGame.aiSequence(2, "REJECT");
			_delayAIRemoval.play();
			//system.out.println("NO VALID WORDS CAN BE MADE");
			_scrabbleGame.getReferee().nextMove();
		}
	}
	
	private Word getBestFirstWord() {
		_rack = _scrabbleGame.getRackAsString();
		_validWords = new ArrayList<>();
		_validStrings = new ArrayList<>();
		int orientationRand = (int) (Math.random() * 3);
		Orientation orientation = null;
		int value = 0;
		_validStrings = _scrabbleGame.validPermutationsOf(_rack);
		for (String thisString : _validStrings) {
			Word bestWord = null;
			for (int j = 0; j <= thisString.length() - 1; j++) {
				Word thisWord;
				int x = -1;
				int y = -1;
				switch (orientationRand) {
					case 0:
						orientation = Orientation.Horizontal;
						x = 7 - j;
						y = 7;
						break;
					case 1:
					case 2:
						orientation = Orientation.Vertical;
						x = 7;
						y = 7 - j;
						break;
				}
				value = _scrabbleGame.getValueFromString(thisString, x, y, orientation);
				if (thisString.length() == 7) {
					value = value + 50;
				}
				thisWord = new Word(thisString, value, x, y, orientation);
				if (j == 0) {
					bestWord = thisWord;
				} else {
					if (thisWord.getValue() >= bestWord.getValue()) {
						bestWord = thisWord;
					}
				}
			}
			_validWords.add(bestWord);
		}
		this.sortValidWords();
		this.printValidWords();
		if (_validWords.size() > 0) {
			return this.applyHeuristics();
		} else {
			return null;
		}
	}

	private Word getBestWord() {
		_rack = _scrabbleGame.getRackAsString();
		_validWords = new ArrayList<>();
		_validStrings = new ArrayList<>();
		Tile[][] realBoard = _scrabbleGame.getTileArray();
		
		_numHooksTested = 0;
		for (int y = 0; y < 15; y++) {
			_precedingKernels = new HashMap<>();
			_succeedingKernels = new HashMap<>();
			_scrabbleGame.mapKernelsForRow(y, _precedingKernels, _succeedingKernels);
			for (int x = 0; x < realBoard[1].length; x++) {
				if (Constants.PRINT_STATUS) {
					//system.out.printf("Checking = %s, %s\n", x, y);
				}
				if (x + 1 <= 14 && realBoard[x][y] == null && realBoard[x + 1][y] != null && realBoard[x + 1][y].isAddedToBoard()) {
					_prefixCrosses = new HashMap<>();
					_suffixCrosses = new HashMap<>();
					_numHooksTested = _numHooksTested + 1;
					//system.out.printf("\n\nTESTING HORIZONTAL HOOK #%s\n\n\n", _numHooksTested);
					int numPrefixSlots = _scrabbleGame.getNumPrefixSlots(x, y, "HORIZONTAL");
					_kernel = _scrabbleGame.getKernelFor(x, y, "HORIZONTAL");
					ArrayList<Tile> kernelTiles = _scrabbleGame.getKernelTiles(x, y, "HORIZONTAL");
					if (Constants.PRINT_STATUS) {
						//system.out.printf("%s kernel tiles\n", kernelTiles.size());
					}
					_firstXkernel = x + 1;
					_firstYkernel = y;
					_prefixes = new ArrayList<>();
					_suffixes = new ArrayList<>();
					_specificSuffixes = new HashMap<>();
					int numSuffixSlots = _scrabbleGame.getNumSuffixSlots(x + _kernel.length() + 1, y, "HORIZONTAL");
					this.analyzeHorizontalPrefixCrosses(x, y, numPrefixSlots);
					this.analyzeHorizontalSuffixCrosses(x + _kernel.length() + 1, y, numSuffixSlots);
					_scrabbleGame.collectPrefixes(_rack, _prefixes, numPrefixSlots, _invalidLettersAndPrefixIndices);
					_scrabbleGame.collectSuffixes(_rack, _suffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
					for (int i = 0; i < _prefixes.size(); i++) {
						String thisPrefix = _prefixes.get(i);
						ArrayList<String> newSuffixes = new ArrayList<>();
						String shortenedRack = _rack;
						for (int j = 0; j < thisPrefix.length(); j++) {
							String letter = String.valueOf(thisPrefix.charAt(j));
							shortenedRack = shortenedRack.replace(letter,"");
						}
						_scrabbleGame.collectSuffixes(shortenedRack, newSuffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
						_specificSuffixes.put(thisPrefix, newSuffixes);
					}
					if (numPrefixSlots > 0 && _prefixes.size() > 0) {
						//system.out.printf("\nCHECKING ISOLATED PREFIXES\n");
						//system.out.printf("%s PREFIX SLOTS\n", numPrefixSlots);
						//system.out.printf("Outer prefix HashMap size = %s\n", _prefixCrosses.size());
						for (int i = 0; i < _prefixes.size(); i++) {
							_concatInt = _concatInt + 1;
							String prefix = _prefixes.get(i);
							String concat = prefix + _kernel;
							boolean prefixExtended = false;
							boolean suffixExtended = false;
							String precedingKernel = "";
							if (prefix.length() == numPrefixSlots) {
								precedingKernel = _precedingKernels.get(_kernel); 
								concat = precedingKernel + concat;
								prefixExtended = true;
//								System.out.printf("Extended prefix %s: %s + %s\n", _concatInt, precedingKernel, prefix + _kernel);
							}
							if (Constants.PRINT_STATUS) {
								System.out.printf("Extended prefix %s: %s + %s\n", _concatInt, precedingKernel, prefix + _kernel);
							}
							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								//system.out.printf("Prefix %s: %s\n", _concatInt, prefix);
								int actualFirstX = _firstXkernel - prefix.length();
								if (prefixExtended == true) {
									actualFirstX = actualFirstX - precedingKernel.length();
								}
								int value = _scrabbleGame.getValueFromString(concat, actualFirstX, _firstYkernel, Orientation.Horizontal);
								if (prefix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = prefix.length(); j > 0; j--) {
									String letter = String.valueOf(prefix.charAt(j - 1));
									//system.out.printf("Prefix: i = %s, j = %s, length = %s, outer key = %s, inner key = %s\n", i, j, prefix.length(), numLet, letter);
									value = value + _prefixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}
								Word validWord = new Word(concat, prefix, value, originalValue, Orientation.Horizontal, actualFirstX, _firstYkernel, prefixExtended, suffixExtended);
								validWord.addKernelTiles(kernelTiles);
								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && _suffixes.size() > 0) {
						//system.out.printf("\nCHECKING ISOLATED SUFFIXES\n");
						//system.out.printf("%s SUFFIX SLOTS\n", numSuffixSlots);
						//system.out.printf("Outer suffix HashMap size = %s\n", _suffixCrosses.size());
						for (int i = 0; i < _suffixes.size(); i++) {
							_concatInt = _concatInt + 1;
							String suffix = _suffixes.get(i);
							String concat = _kernel + suffix;
							boolean prefixExtended = false;
							boolean suffixExtended = false;
							if (suffix.length() == numSuffixSlots) {
								concat = concat + _succeedingKernels.get(_kernel);
								suffixExtended = true;
							}
							if (Constants.PRINT_STATUS) {
								//system.out.printf("Suffix %s: %s\n", _concatInt, concat);
							}	
							
							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								//system.out.printf("Suffix %s: %s\n", _concatInt, suffix);
								int value = _scrabbleGame.getValueFromString(concat, _firstXkernel, _firstYkernel, Orientation.Horizontal);
								if (suffix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = 0; j < suffix.length(); j++) {
									String letter = String.valueOf(suffix.charAt(j));
									//system.out.printf("Suffix: i = %s, j = %s, length = %s, outer key = %s, inner key = %s\n", i, j, suffix.length(), numLet, letter);
									value = value + _suffixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}
								Word validWord = new Word(concat, suffix, value, originalValue, Orientation.Horizontal, _firstXkernel, _firstYkernel, prefixExtended, suffixExtended);
								validWord.addKernelTiles(kernelTiles);
								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && numPrefixSlots > 0 && _prefixes.size() > 0 && _specificSuffixes.size() > 0) {
						//system.out.printf("\nCHECKING COMBINED PREFIXES AND SUFFIXES\n");
						//system.out.printf("Outer prefix HashMap size = %s\n", _prefixCrosses.size());
						//system.out.printf("Outer suffix HashMap size = %s\n", _suffixCrosses.size());
						for (int i = 0; i < _prefixes.size(); i++) {
							String thisPrefix = _prefixes.get(i);
							ArrayList<String> newSuffixes = _specificSuffixes.get(thisPrefix);
							_concatInt = _concatInt + 1;
							for (int j = 0; j < newSuffixes.size(); j++) {
								String newSuffix = newSuffixes.get(j);
								String combo = thisPrefix + newSuffix;
								String concat = thisPrefix + _kernel + newSuffix;
								boolean prefixExtended = false;
								boolean suffixExtended = false;
								String precedingKernel = "";
								if (thisPrefix.length() == numPrefixSlots) {
									precedingKernel = _precedingKernels.get(_kernel);
									concat =  precedingKernel + concat;
									prefixExtended = true;
								}
								if (newSuffix.length() == numSuffixSlots) {
									concat = concat + _succeedingKernels.get(_kernel);
									suffixExtended = true;
								}
								if (Constants.PRINT_STATUS) {
									//system.out.printf("Combo %s: %s\n", _concatInt, concat);
								}	
								if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
									int actualFirstX = _firstXkernel - thisPrefix.length();
									if (prefixExtended == true) {
										actualFirstX = actualFirstX - precedingKernel.length();
									}
									int value = _scrabbleGame.getValueFromString(concat, actualFirstX, _firstYkernel, Orientation.Horizontal);
									if (combo.length() == 7) {
										value = value + 50;
									}
									int originalValue = value;
									int numLet = 1;
									for (int k = thisPrefix.length(); k > 0; k--) {
										String letter = String.valueOf(thisPrefix.charAt(k - 1));
										//system.out.printf("Prefix: outer key = %s, inner key = %s\n", numLet, letter);
										value = value + _prefixCrosses.get(numLet).get(letter);
										numLet = numLet + 1;
									}
									numLet = 1;
									for (int k = 0; k < newSuffix.length(); k++) {
										String letter = String.valueOf(newSuffix.charAt(k));
										//system.out.printf("Suffix: outer key = %s, inner key = %s\n", numLet, letter);
										value = value + _suffixCrosses.get(numLet).get(letter);
										numLet = numLet + 1;
									}
									Word validWord = new Word(concat, combo, value, originalValue, Orientation.Horizontal, actualFirstX, _firstYkernel, prefixExtended, suffixExtended);
									validWord.addKernelTiles(kernelTiles);
									_validWords.add(validWord);
									_validStrings.add(concat);
								}
							}
						}
					}
				}
			}
		}
		//int temp = _numHooksTested;
		_numHooksTested = 0;
		for (int x = 0; x < 15; x++) {
			_precedingKernels = new HashMap<>();
			_succeedingKernels = new HashMap<>();
			_scrabbleGame.mapKernelsForColumn(x, _precedingKernels, _succeedingKernels);
			for (int y = 0; y < realBoard.length; y++) {
				if (Constants.PRINT_STATUS) {
					//system.out.printf("Checking = %s, %s\n", x, y);
				}
				if (y + 1 <= 14 && realBoard[x][y] == null && realBoard[x][y + 1] != null && realBoard[x][y + 1].isAddedToBoard()) {
					_prefixCrosses = new HashMap<>();
					_suffixCrosses = new HashMap<>();
					_numHooksTested = _numHooksTested + 1;
					//system.out.printf("\n\nTESTING VERTICAL HOOK #%s\n\n\n", _numHooksTested);
					int numPrefixSlots = _scrabbleGame.getNumPrefixSlots(x, y, "VERTICAL");
					_kernel = _scrabbleGame.getKernelFor(x, y, "VERTICAL");
					ArrayList<Tile> kernelTiles = _scrabbleGame.getKernelTiles(x, y, "VERTICAL");
					if (Constants.PRINT_STATUS) {
						//system.out.printf("%s kernel tiles\n", kernelTiles.size());
					}
					_firstXkernel = x;
					_firstYkernel = y + 1;
					_prefixes = new ArrayList<>();
					_suffixes = new ArrayList<>();
					_specificSuffixes = new HashMap<>();
					int numSuffixSlots = _scrabbleGame.getNumSuffixSlots(x, y + _kernel.length() + 1, "VERTICAL");
					this.analyzeVerticalPrefixCrosses(x, y, numPrefixSlots);
					this.analyzeVerticalSuffixCrosses(x, y + _kernel.length() + 1, numSuffixSlots);
					_scrabbleGame.collectPrefixes(_rack, _prefixes, numPrefixSlots, _invalidLettersAndPrefixIndices);
					_scrabbleGame.collectSuffixes(_rack, _suffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
					for (int i = 0; i < _prefixes.size(); i++) {
						String thisPrefix = _prefixes.get(i);
						ArrayList<String> newSuffixes = new ArrayList<>();
						String shortenedRack = _rack;
						for (int j = 0; j < thisPrefix.length(); j++) {
							String letter = String.valueOf(thisPrefix.charAt(j));
							shortenedRack = shortenedRack.replace(letter,"");
						}
						_scrabbleGame.collectSuffixes(shortenedRack, newSuffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
						_specificSuffixes.put(thisPrefix, newSuffixes);
					}
					if (numPrefixSlots > 0 && _prefixes.size() > 0) {
						//system.out.printf("\nCHECKING ISOLATED PREFIXES\n");
						//system.out.printf("%s PREFIX SLOTS\n", numPrefixSlots);
						//system.out.printf("Outer prefix HashMap size = %s\n", _prefixCrosses.size());
						for (int i = 0; i < _prefixes.size(); i++) {
							_concatInt = _concatInt + 1;
							String prefix = _prefixes.get(i);
							String concat = prefix + _kernel;
							boolean prefixExtended = false;
							boolean suffixExtended = false;
							String precedingKernel = "";
							if (prefix.length() == numPrefixSlots) {
								precedingKernel = _precedingKernels.get(_kernel); 
								concat = precedingKernel + concat;
								prefixExtended = true;
							}
							if (Constants.PRINT_STATUS) {
								//system.out.printf("Prefix %s: %s\n", _concatInt, concat);
							}
							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								//system.out.printf("Prefix %s: %s\n", _concatInt, prefix);
								int actualFirstY = _firstYkernel - prefix.length();
								if (prefixExtended == true) {
									actualFirstY = actualFirstY - precedingKernel.length();
								}
								int value = _scrabbleGame.getValueFromString(concat, _firstXkernel, actualFirstY, Orientation.Vertical);
								if (prefix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = prefix.length(); j > 0; j--) {
									String letter = String.valueOf(prefix.charAt(j - 1));
									//system.out.printf("Prefix: i = %s, j = %s, length = %s, outer key = %s, inner key = %s\n", i, j, prefix.length(), numLet, letter);
									value = value + _prefixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}
								Word validWord = new Word(concat, prefix, value, originalValue, Orientation.Vertical, _firstXkernel, actualFirstY, prefixExtended, suffixExtended);
								validWord.addKernelTiles(kernelTiles);
								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && _suffixes.size() > 0) {
						//system.out.printf("\nCHECKING ISOLATED SUFFIXES\n");
						//system.out.printf("%s SUFFIX SLOTS\n", numSuffixSlots);
						//system.out.printf("Outer suffix HashMap size = %s\n", _suffixCrosses.size());
						for (int i = 0; i < _suffixes.size(); i++) {
							_concatInt = _concatInt + 1;
							String suffix = _suffixes.get(i);
							String concat = _kernel + suffix;
							boolean prefixExtended = false;
							boolean suffixExtended = false;
							if (suffix.length() == numSuffixSlots) {
								concat = concat + _succeedingKernels.get(_kernel);
								suffixExtended = true;
							}
							if (Constants.PRINT_STATUS) {
								//system.out.printf("Suffix %s: %s\n", _concatInt, concat);
							}	
							
							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								//system.out.printf("Suffix %s: %s\n", _concatInt, suffix);
								int value = _scrabbleGame.getValueFromString(concat, _firstXkernel, _firstYkernel, Orientation.Vertical);
								if (suffix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = 0; j < suffix.length(); j++) {
									String letter = String.valueOf(suffix.charAt(j));
									//system.out.printf("Suffix: i = %s, j = %s, length = %s, outer key = %s, inner key = %s\n", i, j, suffix.length(), numLet, letter);
									value = value + _suffixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}
								Word validWord = new Word(concat, suffix, value, originalValue, Orientation.Vertical, _firstXkernel, _firstYkernel, prefixExtended, suffixExtended);
								validWord.addKernelTiles(kernelTiles);
								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && numPrefixSlots > 0 && _prefixes.size() > 0 && _specificSuffixes.size() > 0) {
						//system.out.printf("\nCHECKING COMBINED PREFIXES AND SUFFIXES\n");
						//system.out.printf("Outer prefix HashMap size = %s\n", _prefixCrosses.size());
						//system.out.printf("Outer suffix HashMap size = %s\n", _suffixCrosses.size());
						for (int i = 0; i < _prefixes.size(); i++) {
							String thisPrefix = _prefixes.get(i);
							ArrayList<String> newSuffixes = _specificSuffixes.get(thisPrefix);
							_concatInt = _concatInt + 1;
							for (int j = 0; j < newSuffixes.size(); j++) {
								String newSuffix = newSuffixes.get(j);
								String combo = thisPrefix + newSuffix;
								String concat = thisPrefix + _kernel + newSuffix;
								boolean prefixExtended = false;
								boolean suffixExtended = false;
								String precedingKernel = "";
								if (thisPrefix.length() == numPrefixSlots) {
									precedingKernel = _precedingKernels.get(_kernel);
									concat =  precedingKernel + concat;
									prefixExtended = true;
								}
								if (newSuffix.length() == numSuffixSlots) {
									concat = concat + _succeedingKernels.get(_kernel);
									suffixExtended = true;
								}
								if (Constants.PRINT_STATUS) {
									//system.out.printf("Combo %s: %s\n", _concatInt, concat);
								}	
								if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
									int actualFirstY = _firstYkernel - thisPrefix.length();
									if (prefixExtended == true) {
										actualFirstY = actualFirstY - precedingKernel.length();
									}
									int value = _scrabbleGame.getValueFromString(concat, _firstXkernel, actualFirstY, Orientation.Vertical);
									if (combo.length() == 7) {
										value = value + 50;
									}
									int originalValue = value;
									int numLet = 1;
									for (int k = thisPrefix.length(); k > 0; k--) {
										String letter = String.valueOf(thisPrefix.charAt(k - 1));
										//system.out.printf("Prefix: outer key = %s, inner key = %s\n", numLet, letter);
										value = value + _prefixCrosses.get(numLet).get(letter);
										numLet = numLet + 1;
									}
									numLet = 1;
									for (int k = 0; k < newSuffix.length(); k++) {
										String letter = String.valueOf(newSuffix.charAt(k));
										//system.out.printf("Suffix: outer key = %s, inner key = %s\n", numLet, letter);
										value = value + _suffixCrosses.get(numLet).get(letter);
										numLet = numLet + 1;
									}
									Word validWord = new Word(concat, combo, value, originalValue, Orientation.Vertical, _firstXkernel, actualFirstY, prefixExtended, suffixExtended);
									validWord.addKernelTiles(kernelTiles);
									_validWords.add(validWord);
									_validStrings.add(concat);
								}
							}
						}
					}
				}
			}
		}
		this.sortValidWords();
		this.printValidWords();
		//system.out.printf("*** %s hooks tested\n", _numHooksTested + temp);
		if (_validWords.size() > 0) {
			return this.applyHeuristics();
		} else {
			return null;
		}
	}
	
	public int getNumBlankSlots() {
		return _numBlankSlots;
	}

	private Word applyHeuristics() {
		Word bestWord = _validWords.get(0);
		if (_validWords.size() == 1) {
			return bestWord;
		}
		int bestValue = bestWord.getValue();
		if (_validWords.get(1).getValue() < bestValue) {
			return bestWord;
		} else {
			int i = 1;
			ArrayList<Word> topTier = new ArrayList<>();
			while (i < _validWords.size() && _validWords.get(i).getValue() == bestValue) {
				topTier.add(_validWords.get(i));
				i++;
			}
			Word shortestWord = null;
			for (i = 0; i < topTier.size(); i++) {
				Word thisWord = topTier.get(i);
				if (i == 0) {
					shortestWord = thisWord;
				} else {
					if (thisWord.getLetters().length() < shortestWord.getLetters().length()) {
						shortestWord = thisWord;
					}
				}
			}
			return shortestWord;
		}
	}

	private void analyzeHorizontalPrefixCrosses(int x, int y, int numPrefixSlots) {
		//system.out.printf("ANALYZING PREFIX CROSSES for Hook %s at %s\n", _numHooksTested, y);
		_invalidLettersAndPrefixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		String upperCross = "";
		String lowerCross = "";
		int vertical = 0;
		int horizontal = 0;
		while (x - horizontal >= 0 && horizontal < numPrefixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			upperCross = "";
			lowerCross = "";
			//system.out.printf("Prefix slot #%s\n", horizontal + 1);
			ArrayList<String> invalidLetters = new ArrayList<>();
			vertical = 1;
			while (y - vertical >= 0 && tileArray[x - horizontal][y - vertical] != null) {
				upperCross = tileArray[x - horizontal][y - vertical].getLetter() + upperCross;
				vertical = vertical + 1;
			}
			vertical = 1;
			while (y + vertical <= 14 && tileArray[x - horizontal][y + vertical] != null) {
				lowerCross = lowerCross + tileArray[x - horizontal][y + vertical].getLetter();
				vertical = vertical + 1;
			}
			//system.out.printf("At %s, %s, cross = %s + __ + %s\n", x - horizontal, y, upperCross, lowerCross);
			if (upperCross == "" && lowerCross == "") {
				_invalidLettersAndPrefixIndices.put(horizontal + 1, new ArrayList<>());
				//system.out.printf("Don't worry about prefix crosses at %s, %s\n", x - horizontal, y);
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					//system.out.printf("NO CROSSES: Adding %s and 0 to inner HashMap\n", letter);
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					//system.out.printf("Rack = %s, index = %s\n", _rack, i);
					String toCheck = String.valueOf(_rack.charAt(i));
					String verticalConcat = upperCross + toCheck + lowerCross;
					if (!_scrabbleGame.dictionaryContains(verticalConcat)) {
						invalidLetters.add(toCheck);
						//system.out.printf("%s forms %s - can't be played at %s, %s\n", toCheck, verticalConcat, x - horizontal, y);
						//system.out.printf("INVALID: Adding %s and 0 to inner HashMap\n", toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(verticalConcat, x - horizontal, y - upperCross.length(), Orientation.Vertical);
						//system.out.printf("%s forms %s - CAN be played at %s, %s with a value of %s\n", toCheck, verticalConcat, x - horizontal, y, crossValue);
						//system.out.printf("VALID: Adding %s and %s to inner HashMap\n", toCheck, crossValue);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndPrefixIndices.put(horizontal + 1, invalidLetters);
			}
			horizontal = horizontal + 1;
			_prefixCrosses.put(horizontal, letterToValue);
			//system.out.printf("Adding key at %s to outer HashMap (Prefix), now w size %s\n", horizontal, _prefixCrosses.size());
		}
		//system.out.printf("Invalid prefix crosses size = %s\n", _invalidLettersAndPrefixIndices.size());
	}
	
	private void analyzeVerticalPrefixCrosses(int x, int y, int numPrefixSlots) {
		//system.out.printf("ANALYZING PREFIX CROSSES for Hook %s at %s\n", _numHooksTested, y);
		_invalidLettersAndPrefixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		String leftCross = "";
		String rightCross = "";
		int horizontal = 0;
		int vertical = 0;
		while (y - vertical >= 0 && vertical < numPrefixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			leftCross = "";
			rightCross = "";
			//system.out.printf("Prefix slot #%s\n", vertical + 1);
			ArrayList<String> invalidLetters = new ArrayList<>();
			horizontal = 1;
			while (x - horizontal >= 0 && tileArray[x - horizontal][y - vertical] != null) {
				leftCross = tileArray[x - horizontal][y - vertical].getLetter() + leftCross;
				horizontal = horizontal + 1;
			}
			horizontal = 1;
			while (x + horizontal <= 14 && tileArray[x + horizontal][y - vertical] != null) {
				rightCross = rightCross + tileArray[x + horizontal][y - vertical].getLetter();
				horizontal = horizontal + 1;
			}
			//system.out.printf("At %s, %s, cross = %s + __ + %s\n", x, y - vertical, leftCross, rightCross);
			if (leftCross == "" && rightCross == "") {
				_invalidLettersAndPrefixIndices.put(vertical + 1, new ArrayList<>());
				//system.out.printf("Don't worry about prefix crosses at %s, %s\n", x, y - vertical);
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					//system.out.printf("NO CROSSES: Adding %s and 0 to inner HashMap\n", letter);
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					//system.out.printf("Rack = %s, index = %s\n", _rack, i);
					String toCheck = String.valueOf(_rack.charAt(i));
					String horizontalConcat = leftCross + toCheck + rightCross;
					if (!_scrabbleGame.dictionaryContains(horizontalConcat)) {
						invalidLetters.add(toCheck);
						//system.out.printf("%s forms %s - can't be played at %s, %s\n", toCheck, horizontalConcat, x, y - vertical);
						//system.out.printf("INVALID: Adding %s and 0 to inner HashMap\n", toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(horizontalConcat, x - leftCross.length(), y, Orientation.Horizontal);
						//system.out.printf("%s forms %s - CAN be played at %s, %s with a value of %s\n", toCheck, horizontalConcat, x, y - vertical, crossValue);
						//system.out.printf("VALID: Adding %s and %s to inner HashMap\n", toCheck, crossValue);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndPrefixIndices.put(vertical + 1, invalidLetters);
			}
			vertical = vertical + 1;
			_prefixCrosses.put(vertical, letterToValue);
			//system.out.printf("Adding key at %s to outer HashMap (Prefix), now w size %s\n", vertical, _prefixCrosses.size());
		}
		//system.out.printf("Invalid prefix crosses size = %s\n", _invalidLettersAndPrefixIndices.size());
	}
	
	private void analyzeHorizontalSuffixCrosses(int x, int y, int numSuffixSlots) {
		//system.out.printf("ANALYZING SUFFIX CROSSES for Hook %s at %s\n", _numHooksTested, y);
		_invalidLettersAndSuffixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		String upperCross = "";
		String lowerCross = "";
		int vertical = 0;
		int horizontal = 0;
		while (x + horizontal <= 14 && horizontal < numSuffixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			upperCross = "";
			lowerCross = "";
			//system.out.printf("Suffix slot #%s\n", horizontal + 1);
			ArrayList<String> invalidLetters = new ArrayList<>();
			vertical = 1;
			while (y - vertical >= 0 && tileArray[x + horizontal][y - vertical] != null) {
				upperCross = tileArray[x + horizontal][y - vertical].getLetter() + upperCross;
				vertical = vertical + 1;
			}
			vertical = 1;
			while (y + vertical <= 14 && tileArray[x + horizontal][y + vertical] != null) {
				lowerCross = lowerCross + tileArray[x + horizontal][y + vertical].getLetter();
				vertical = vertical + 1;
			}
			//system.out.printf("At %s, %s, cross = %s + __ + %s\n", x + horizontal, y, upperCross, lowerCross);
			if (upperCross == "" && lowerCross == "") {
				_invalidLettersAndSuffixIndices.put(horizontal + 1, new ArrayList<>());
				//system.out.printf("Don't worry about suffix crosses at %s, %s\n", x + horizontal, y);
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					//system.out.printf("NO CROSSES: Adding %s and 0 to inner HashMap\n", letter);
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					//system.out.printf("Rack = %s, index = %s\n", _rack, i);
					String toCheck = String.valueOf(_rack.charAt(i));
					String verticalConcat = upperCross + toCheck + lowerCross;
					if (!_scrabbleGame.dictionaryContains(verticalConcat)) {
						invalidLetters.add(toCheck);
						//system.out.printf("%s forms %s - can't be played at %s, %s\n", toCheck, verticalConcat, x + horizontal, y);
						//system.out.printf("INVALID: Adding %s and 0 to inner HashMap\n", toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(verticalConcat, x + horizontal, y - upperCross.length(), Orientation.Vertical);
						//system.out.printf("%s forms %s - CAN be played at %s, %s with a value of %s\n", toCheck, verticalConcat, x + horizontal, y, crossValue);
						//system.out.printf("VALID: Adding %s and %s to inner HashMap\n", toCheck, crossValue);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndSuffixIndices.put(horizontal + 1, invalidLetters);
			}
			horizontal = horizontal + 1;
			_suffixCrosses.put(horizontal, letterToValue);
			//system.out.printf("Adding key at %s to outer HashMap (Suffix), now w size %s\n", horizontal, _suffixCrosses.size());
		}
		//system.out.printf("Invalid suffix crosses size = %s\n", _invalidLettersAndSuffixIndices.size());
	}
	
	private void analyzeVerticalSuffixCrosses(int x, int y, int numSuffixSlots) {
		//system.out.printf("ANALYZING SUFFIX CROSSES for Hook %s at %s\n", _numHooksTested, x);
		_invalidLettersAndSuffixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		String leftCross = "";
		String rightCross = "";
		int horizontal = 0;
		int vertical = 0;
		while (y + vertical <= 14 && vertical < numSuffixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			leftCross = "";
			rightCross = "";
			//system.out.printf("Suffix slot #%s\n", vertical + 1);
			ArrayList<String> invalidLetters = new ArrayList<>();
			horizontal = 1;
			while (x - horizontal >= 0 && tileArray[x - horizontal][y + vertical] != null) {
				leftCross = tileArray[x - horizontal][y + vertical].getLetter() + leftCross;
				horizontal++;
			}
			horizontal = 1;
			while (x + horizontal <= 14 && tileArray[x + horizontal][y + vertical] != null) {
				rightCross = rightCross + tileArray[x + horizontal][y + vertical].getLetter();
				horizontal++;
			}
			//system.out.printf("At %s, %s, cross = %s + __ + %s\n", x, y + vertical, leftCross, rightCross);
			if (leftCross == "" && rightCross == "") {
				_invalidLettersAndSuffixIndices.put(vertical + 1, new ArrayList<>());
				//system.out.printf("Don't worry about suffix crosses at %s, %s\n", x, y + vertical);
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					//system.out.printf("NO CROSSES: Adding %s and 0 to inner HashMap\n", letter);
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					//system.out.printf("Rack = %s, index = %s\n", _rack, i);
					String toCheck = String.valueOf(_rack.charAt(i));
					String horizontalConcat = leftCross + toCheck + rightCross;
					if (!_scrabbleGame.dictionaryContains(horizontalConcat)) {
						invalidLetters.add(toCheck);
						//system.out.printf("%s forms %s - can't be played at %s, %s\n", toCheck, horizontalConcat, x, y + vertical);
						//system.out.printf("INVALID: Adding %s and 0 to inner HashMap\n", toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(horizontalConcat, x - leftCross.length(), y + vertical, Orientation.Horizontal);
						//system.out.printf("%s forms %s - CAN be played at %s, %s with a value of %s\n", toCheck, horizontalConcat, x, y + vertical, crossValue);
						//system.out.printf("VALID: Adding %s and %s to inner HashMap\n", toCheck, crossValue);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndSuffixIndices.put(vertical + 1, invalidLetters);
			}
			vertical++;
			_suffixCrosses.put(vertical, letterToValue);
			//system.out.printf("Adding key at %s to outer HashMap (Suffix), now w size %s\n", vertical, _suffixCrosses.size());
		}
		//system.out.printf("Invalid suffix crosses size = %s\n", _invalidLettersAndSuffixIndices.size());
	}

	private void sortValidWords() {
		_validWords = (ArrayList<Word>) _validWords.stream().sorted(Comparator.comparingInt(Word::getValue).reversed()).collect(Collectors.toList());
	}

	private void printValidWords() {
		if (_validWords.isEmpty()) return;
		for (Word _validWord : _validWords) _validWord.printInfo();
	}
	
	public PlayerNumber getPlayerNumber() {
		return _playerNumber;
	}

	public Word getNewestWord() {
		return _newestWord;
	}

}