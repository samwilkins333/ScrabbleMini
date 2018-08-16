package Scrabble;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class AIPlayer implements Playable {
	private ScrabbleGame _scrabbleGame;

	private Word _newestWord;
	private PlayerNum _playerNumber;
	private String _rack;

	private ArrayList<Word> _validWords;
	private ArrayList<String> _validStrings;
	private int _concatInt;

	private HashMap<Integer, HashMap<String, Integer>> _prefixCrosses;
	private HashMap<Integer, HashMap<String, Integer>> _suffixCrosses;

	private HashMap<Integer, ArrayList<String>> _invalidLettersAndPrefixIndices;
	private HashMap<Integer, ArrayList<String>> _invalidLettersAndSuffixIndices;

	private PauseTransition _delayAIRemoval;

	AIPlayer(PlayerNum playerNumber, ScrabbleGame scrabbleGame) {
		_scrabbleGame = scrabbleGame;
		_playerNumber = playerNumber;
		_concatInt = 0;
		this.setUpDelay();
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

	public boolean isHuman() {
		return false;
	}

	public void makeMove() {
		boolean firstMoveMade = _scrabbleGame.getReferee().isFirstMoveMade();

		_newestWord = firstMoveMade ? this.getBestWord() : this.getBestFirstWord();

		if (_newestWord != null) {
			if (!firstMoveMade) {
				_scrabbleGame.getReferee().DeclareFirstMoveMade();
			}
			_newestWord.addScrabbleGame(_scrabbleGame);
			_scrabbleGame.aiSequence(2, "ACCEPT");
			_delayAIRemoval.play();
			ArrayList<Tile> myRack = _scrabbleGame.getReferee().getCurrentPlayerRack();
			String appropriateLetters = "";
			if (!firstMoveMade) {
				appropriateLetters = _newestWord.getLetters();
			} else if (firstMoveMade) {
				appropriateLetters = _newestWord.getNewBoardLetters();
			}
			ArrayList<Tile> myTransfers = _scrabbleGame.transferTilesFromRack(appropriateLetters, myRack);
			_newestWord.addTiles(myTransfers);
			_newestWord.arrangeTilesOnBoard();
		} else {
			_scrabbleGame.aiSequence(2, "REJECT");
			_delayAIRemoval.play();
			_scrabbleGame.getReferee().nextMove();
		}
	}
	
	private Word getBestFirstWord() {
		_rack = _scrabbleGame.getRackAsString();
		_validWords = new ArrayList<>();
		_validStrings = new ArrayList<>();

		int orientationRand = (int) (Math.random() * 3);
		WordOrientation orientation;

		int value;
		_scrabbleGame.collectPermutations(_rack, _validStrings);
		for (String thisString : _validStrings) {
			Word bestWord = null;
			for (int j = 0; j <= thisString.length() - 1; j++) {
				Word thisWord;
				int x;
				int y;
				switch (orientationRand) {
					case 0:
						orientation = WordOrientation.Horizontal;
						x = 7 - j;
						y = 7;
						break;
					// 2 : 1 weighted preference toward horizontal
					case 1:
					case 2:
						orientation = WordOrientation.Horizontal;
						x = 7;
						y = 7 - j;
						break;
					default:
						orientation = WordOrientation.Horizontal;
						x = 7;
						y = 7 - j;
						break;
				}
				value = _scrabbleGame.getValueFromString(thisString, x, y, orientation);

				if (thisString.length() == 7) value += 50;

				thisWord = new Word(thisString, value, x, y, orientation);

				if (j == 0) bestWord = thisWord;
				else if (thisWord.getValue() >= bestWord.getValue()) bestWord = thisWord;
			}
			_validWords.add(bestWord);
		}
		this.sortValidWords();

		return _validWords.isEmpty() ? null : this.applyHeuristics();
	}

	private Word getBestWord() {
		_rack = _scrabbleGame.getRackAsString();
		_validWords = new ArrayList<>();
		_validStrings = new ArrayList<>();
		Tile[][] realBoard = _scrabbleGame.getTileArray();

		String _kernel;
		ArrayList<String> _prefixes;
		ArrayList<String> _suffixes;
		int _firstXKernel;
		int _firstYKernel;
		HashMap<String, ArrayList<String>> specificSuffixes;
		HashMap<String, String> _precedingKernels;
		HashMap<String, String> _succeedingKernels;
		for (int y = 0; y < 15; y++) {
			_precedingKernels = new HashMap<>();
			_succeedingKernels = new HashMap<>();
			_scrabbleGame.mapKernelsForRow(y, _precedingKernels, _succeedingKernels);
			for (int x = 0; x < realBoard[1].length; x++) {
				if (x + 1 <= 14 && realBoard[x][y] == null && realBoard[x + 1][y] != null && realBoard[x + 1][y].hasBeenPlayed()) {

					_prefixCrosses = new HashMap<>();
					_suffixCrosses = new HashMap<>();
					specificSuffixes = new HashMap<>();

					int numPrefixSlots = _scrabbleGame.getNumPrefixSlots(x, y, CollectionOrientation.Horizontal);
					_kernel = _scrabbleGame.getKernelFor(x, y, CollectionOrientation.Horizontal);

					ArrayList<Tile> kernelTiles = _scrabbleGame.getKernelTiles(x, y, CollectionOrientation.Horizontal);

					_firstXKernel = x + 1;
					_firstYKernel = y;

					_prefixes = new ArrayList<>();
					_suffixes = new ArrayList<>();

					int numSuffixSlots = _scrabbleGame.getNumSuffixSlots(x + _kernel.length() + 1, y, CollectionOrientation.Horizontal);

					this.analyzeHorizontalPrefixCrosses(x, y, numPrefixSlots);
					this.analyzeHorizontalSuffixCrosses(x + _kernel.length() + 1, y, numSuffixSlots);

					_scrabbleGame.collectPrefixes(_rack, _prefixes, numPrefixSlots, _invalidLettersAndPrefixIndices);
					_scrabbleGame.collectSuffixes(_rack, _suffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);

					for (String thisPrefix : _prefixes) {
						ArrayList<String> newSuffixes = new ArrayList<>();
						String shortenedRack = _rack;
						for (int j = 0; j < thisPrefix.length(); j++) {
							String letter = String.valueOf(thisPrefix.charAt(j));
							shortenedRack = shortenedRack.replace(letter, "");
						}
						_scrabbleGame.collectSuffixes(shortenedRack, newSuffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
						specificSuffixes.put(thisPrefix, newSuffixes);
					}
					if (numPrefixSlots > 0 && _prefixes.size() > 0) {
						for (String prefix : _prefixes) {
							_concatInt = _concatInt + 1;
							String concat = prefix + _kernel;

							boolean prefixExtended = false;
							String precedingKernel = "";

							if (prefix.length() == numPrefixSlots) {
								precedingKernel = _precedingKernels.get(_kernel);
								concat = precedingKernel + concat;
								prefixExtended = true;
							}
							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								int actualFirstX = _firstXKernel - prefix.length();
								if (prefixExtended) {
									actualFirstX = actualFirstX - precedingKernel.length();
								}
								int value = _scrabbleGame.getValueFromString(concat, actualFirstX, _firstYKernel, WordOrientation.Horizontal);
								if (prefix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = prefix.length(); j > 0; j--) {
									String letter = String.valueOf(prefix.charAt(j - 1));
									value = value + _prefixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}

								Word validWord = new Word(concat, prefix, value, originalValue, WordOrientation.Horizontal, actualFirstX, _firstYKernel);
								validWord.addKernelTiles(kernelTiles);

								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && _suffixes.size() > 0) {
						for (String suffix : _suffixes) {

							_concatInt++;
							String concat = _kernel + suffix;

							if (suffix.length() == numSuffixSlots) concat += _succeedingKernels.get(_kernel);

							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								int value = _scrabbleGame.getValueFromString(concat, _firstXKernel, _firstYKernel, WordOrientation.Horizontal);
								if (suffix.length() == 7) {
									value = value + 50;
								}
								int originalValue = value;
								int numLet = 1;
								for (int j = 0; j < suffix.length(); j++) {
									String letter = String.valueOf(suffix.charAt(j));
									value = value + _suffixCrosses.get(numLet).get(letter);
									numLet = numLet + 1;
								}

								Word validWord = new Word(concat, suffix, value, originalValue, WordOrientation.Horizontal, _firstXKernel, _firstYKernel);
								validWord.addKernelTiles(kernelTiles);

								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && numPrefixSlots > 0 && _prefixes.size() > 0 && specificSuffixes.size() > 0) {
						for (String thisPrefix : _prefixes) {
							ArrayList<String> newSuffixes = specificSuffixes.get(thisPrefix);
							_concatInt++;

							for (String newSuffix : newSuffixes) {
								String combo = thisPrefix + newSuffix;
								String concat = thisPrefix + _kernel + newSuffix;

								boolean prefixExtended = false;
								String precedingKernel = "";

								if (thisPrefix.length() == numPrefixSlots) {
									precedingKernel = _precedingKernels.get(_kernel);
									concat = precedingKernel + concat;
									prefixExtended = true;
								}

								if (newSuffix.length() == numSuffixSlots) {
									concat = concat + _succeedingKernels.get(_kernel);
								}

								if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
									int actualFirstX = _firstXKernel - thisPrefix.length();
									if (prefixExtended) actualFirstX = actualFirstX - precedingKernel.length();

									int value = _scrabbleGame.getValueFromString(concat, actualFirstX, _firstYKernel, WordOrientation.Horizontal);
									if (combo.length() == 7) value = value + 50;

									int originalValue = value;

									int numLet = 1;
									for (int k = thisPrefix.length(); k > 0; k--) {
										String letter = String.valueOf(thisPrefix.charAt(k - 1));
										value = value + _prefixCrosses.get(numLet).get(letter);
										numLet++;
									}

									numLet = 1;
									for (int k = 0; k < newSuffix.length(); k++) {
										String letter = String.valueOf(newSuffix.charAt(k));
										value = value + _suffixCrosses.get(numLet).get(letter);
										numLet++;
									}

									Word validWord = new Word(concat, combo, value, originalValue, WordOrientation.Horizontal, actualFirstX, _firstYKernel);
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
		for (int x = 0; x < 15; x++) {

			_precedingKernels = new HashMap<>();
			_succeedingKernels = new HashMap<>();
			_scrabbleGame.mapKernelsForColumn(x, _precedingKernels, _succeedingKernels);

			for (int y = 0; y < realBoard.length; y++) {
				if (y + 1 <= 14 && realBoard[x][y] == null && realBoard[x][y + 1] != null && realBoard[x][y + 1].hasBeenPlayed()) {

					_prefixCrosses = new HashMap<>();
					_suffixCrosses = new HashMap<>();
					specificSuffixes = new HashMap<>();

					int numPrefixSlots = _scrabbleGame.getNumPrefixSlots(x, y, CollectionOrientation.Vertical);

					_kernel = _scrabbleGame.getKernelFor(x, y, CollectionOrientation.Vertical);
					ArrayList<Tile> kernelTiles = _scrabbleGame.getKernelTiles(x, y, CollectionOrientation.Vertical);

					_firstXKernel = x;
					_firstYKernel = y + 1;

					_prefixes = new ArrayList<>();
					_suffixes = new ArrayList<>();

					int numSuffixSlots = _scrabbleGame.getNumSuffixSlots(x, y + _kernel.length() + 1, CollectionOrientation.Vertical);

					this.analyzeVerticalPrefixCrosses(x, y, numPrefixSlots);
					this.analyzeVerticalSuffixCrosses(x, y + _kernel.length() + 1, numSuffixSlots);

					_scrabbleGame.collectPrefixes(_rack, _prefixes, numPrefixSlots, _invalidLettersAndPrefixIndices);
					_scrabbleGame.collectSuffixes(_rack, _suffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);

					for (String thisPrefix : _prefixes) {
						ArrayList<String> newSuffixes = new ArrayList<>();
						String shortenedRack = _rack;
						for (int j = 0; j < thisPrefix.length(); j++) {
							String letter = String.valueOf(thisPrefix.charAt(j));
							shortenedRack = shortenedRack.replace(letter, "");
						}
						_scrabbleGame.collectSuffixes(shortenedRack, newSuffixes, numSuffixSlots, _invalidLettersAndSuffixIndices);
						specificSuffixes.put(thisPrefix, newSuffixes);
					}

					if (numPrefixSlots > 0 && _prefixes.size() > 0) {
						for (String prefix : _prefixes) {
							_concatInt++;
							String concat = prefix + _kernel;

							boolean prefixExtended = false;

							String precedingKernel = "";
							if (prefix.length() == numPrefixSlots) {
								precedingKernel = _precedingKernels.get(_kernel);
								concat = precedingKernel + concat;
								prefixExtended = true;
							}

							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								int actualFirstY = _firstYKernel - prefix.length();
								if (prefixExtended) actualFirstY = actualFirstY - precedingKernel.length();

								int value = _scrabbleGame.getValueFromString(concat, _firstXKernel, actualFirstY, WordOrientation.Vertical);
								if (prefix.length() == 7) value += 50;

								int originalValue = value;

								int numLet = 1;
								for (int j = prefix.length(); j > 0; j--) {
									String letter = String.valueOf(prefix.charAt(j - 1));
									value += _prefixCrosses.get(numLet).get(letter);
									numLet++;
								}

								Word validWord = new Word(concat, prefix, value, originalValue, WordOrientation.Vertical, _firstXKernel, actualFirstY);
								validWord.addKernelTiles(kernelTiles);

								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && _suffixes.size() > 0) {
						for (String suffix : _suffixes) {
							_concatInt = _concatInt + 1;
							String concat = _kernel + suffix;

							if (suffix.length() == numSuffixSlots) {
								concat = concat + _succeedingKernels.get(_kernel);
							}

							if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
								int value = _scrabbleGame.getValueFromString(concat, _firstXKernel, _firstYKernel, WordOrientation.Vertical);
								if (suffix.length() == 7) value = value + 50;

								int originalValue = value;
								int numLet = 1;
								for (int j = 0; j < suffix.length(); j++) {
									String letter = String.valueOf(suffix.charAt(j));
									value += _suffixCrosses.get(numLet).get(letter);
									numLet++;
								}

								Word validWord = new Word(concat, suffix, value, originalValue, WordOrientation.Vertical, _firstXKernel, _firstYKernel);
								validWord.addKernelTiles(kernelTiles);

								_validWords.add(validWord);
								_validStrings.add(concat);
							}
						}
					}
					if (numSuffixSlots > 0 && numPrefixSlots > 0 && _prefixes.size() > 0 && specificSuffixes.size() > 0) {
						for (String thisPrefix : _prefixes) {
							ArrayList<String> newSuffixes = specificSuffixes.get(thisPrefix);
							_concatInt++;
							for (String newSuffix : newSuffixes) {
								String combo = thisPrefix + newSuffix;
								String concat = thisPrefix + _kernel + newSuffix;

								boolean prefixExtended = false;

								String precedingKernel = "";

								if (thisPrefix.length() == numPrefixSlots) {
									precedingKernel = _precedingKernels.get(_kernel);
									concat = precedingKernel + concat;
									prefixExtended = true;
								}

								if (newSuffix.length() == numSuffixSlots) {
									concat = concat + _succeedingKernels.get(_kernel);
								}

								if (_scrabbleGame.dictionaryContains(concat) && !_validStrings.contains(concat)) {
									int actualFirstY = _firstYKernel - thisPrefix.length();
									if (prefixExtended) actualFirstY = actualFirstY - precedingKernel.length();

									int value = _scrabbleGame.getValueFromString(concat, _firstXKernel, actualFirstY, WordOrientation.Vertical);
									if (combo.length() == 7) value = value + 50;

									int originalValue = value;

									int numLet = 1;
									for (int k = thisPrefix.length(); k > 0; k--) {
										String letter = String.valueOf(thisPrefix.charAt(k - 1));
										value += _prefixCrosses.get(numLet).get(letter);
										numLet += 1;
									}

									numLet = 1;
									for (int k = 0; k < newSuffix.length(); k++) {
										String letter = String.valueOf(newSuffix.charAt(k));
										value += _suffixCrosses.get(numLet).get(letter);
										numLet++;
									}

									Word validWord = new Word(concat, combo, value, originalValue, WordOrientation.Vertical, _firstXKernel, actualFirstY);
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


		return _validWords.isEmpty() ? null : this.applyHeuristics();
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
		_invalidLettersAndPrefixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		StringBuilder upperCross;
		StringBuilder lowerCross;
		int vertical;
		int horizontal = 0;
		while (x - horizontal >= 0 && horizontal < numPrefixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			upperCross = new StringBuilder();
			lowerCross = new StringBuilder();
			ArrayList<String> invalidLetters = new ArrayList<>();
			vertical = 1;
			while (y - vertical >= 0 && tileArray[x - horizontal][y - vertical] != null) {
				upperCross.insert(0, tileArray[x - horizontal][y - vertical].getLetter());
				vertical = vertical + 1;
			}
			vertical = 1;
			while (y + vertical <= 14 && tileArray[x - horizontal][y + vertical] != null) {
				lowerCross.append(tileArray[x - horizontal][y + vertical].getLetter());
				vertical = vertical + 1;
			}
			if (upperCross.toString().equals("") && lowerCross.toString().equals("")) {
				_invalidLettersAndPrefixIndices.put(horizontal + 1, new ArrayList<>());
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					String toCheck = String.valueOf(_rack.charAt(i));
					String verticalConcat = upperCross + toCheck + lowerCross;
					if (!_scrabbleGame.dictionaryContains(verticalConcat)) {
						invalidLetters.add(toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(verticalConcat, x - horizontal, y - upperCross.length(), WordOrientation.Vertical);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndPrefixIndices.put(horizontal + 1, invalidLetters);
			}
			horizontal++;
			_prefixCrosses.put(horizontal, letterToValue);
		}
	}
	
	private void analyzeVerticalPrefixCrosses(int x, int y, int numPrefixSlots) {
		_invalidLettersAndPrefixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		StringBuilder leftCross;
		StringBuilder rightCross;
		int horizontal;
		int vertical = 0;
		while (y - vertical >= 0 && vertical < numPrefixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			leftCross = new StringBuilder();
			rightCross = new StringBuilder();
			ArrayList<String> invalidLetters = new ArrayList<>();
			horizontal = 1;
			while (x - horizontal >= 0 && tileArray[x - horizontal][y - vertical] != null) {
				leftCross.insert(0, tileArray[x - horizontal][y - vertical].getLetter());
				horizontal = horizontal + 1;
			}
			horizontal = 1;
			while (x + horizontal <= 14 && tileArray[x + horizontal][y - vertical] != null) {
				rightCross.append(tileArray[x + horizontal][y - vertical].getLetter());
				horizontal = horizontal + 1;
			}
			if (leftCross.toString().equals("") && rightCross.toString().equals("")) {
				_invalidLettersAndPrefixIndices.put(vertical + 1, new ArrayList<>());
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					String toCheck = String.valueOf(_rack.charAt(i));
					String horizontalConcat = leftCross + toCheck + rightCross;
					if (!_scrabbleGame.dictionaryContains(horizontalConcat)) {
						invalidLetters.add(toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(horizontalConcat, x - leftCross.length(), y, WordOrientation.Horizontal);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndPrefixIndices.put(vertical + 1, invalidLetters);
			}
			vertical = vertical + 1;
			_prefixCrosses.put(vertical, letterToValue);
		}
	}
	
	private void analyzeHorizontalSuffixCrosses(int x, int y, int numSuffixSlots) {
		_invalidLettersAndSuffixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		StringBuilder upperCross;
		StringBuilder lowerCross;
		int vertical;
		int horizontal = 0;
		while (x + horizontal <= 14 && horizontal < numSuffixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			upperCross = new StringBuilder();
			lowerCross = new StringBuilder();
			ArrayList<String> invalidLetters = new ArrayList<>();
			vertical = 1;
			while (y - vertical >= 0 && tileArray[x + horizontal][y - vertical] != null) {
				upperCross.insert(0, tileArray[x + horizontal][y - vertical].getLetter());
				vertical = vertical + 1;
			}
			vertical = 1;
			while (y + vertical <= 14 && tileArray[x + horizontal][y + vertical] != null) {
				lowerCross.append(tileArray[x + horizontal][y + vertical].getLetter());
				vertical = vertical + 1;
			}
			if (upperCross.toString().equals("") && lowerCross.toString().equals("")) {
				_invalidLettersAndSuffixIndices.put(horizontal + 1, new ArrayList<>());
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					String toCheck = String.valueOf(_rack.charAt(i));
					String verticalConcat = upperCross + toCheck + lowerCross;
					if (!_scrabbleGame.dictionaryContains(verticalConcat)) {
						invalidLetters.add(toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(verticalConcat, x + horizontal, y - upperCross.length(), WordOrientation.Vertical);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndSuffixIndices.put(horizontal + 1, invalidLetters);
			}
			horizontal = horizontal + 1;
			_suffixCrosses.put(horizontal, letterToValue);
		}
	}
	
	private void analyzeVerticalSuffixCrosses(int x, int y, int numSuffixSlots) {
		_invalidLettersAndSuffixIndices = new HashMap<>();
		Tile[][] tileArray = _scrabbleGame.getTileArray();
		StringBuilder leftCross;
		StringBuilder rightCross;
		int horizontal;
		int vertical = 0;
		while (y + vertical <= 14 && vertical < numSuffixSlots) {
			HashMap<String, Integer> letterToValue = new HashMap<>();
			leftCross = new StringBuilder();
			rightCross = new StringBuilder();
			ArrayList<String> invalidLetters = new ArrayList<>();
			horizontal = 1;
			while (x - horizontal >= 0 && tileArray[x - horizontal][y + vertical] != null) {
				leftCross.insert(0, tileArray[x - horizontal][y + vertical].getLetter());
				horizontal++;
			}
			horizontal = 1;
			while (x + horizontal <= 14 && tileArray[x + horizontal][y + vertical] != null) {
				rightCross.append(tileArray[x + horizontal][y + vertical].getLetter());
				horizontal++;
			}
			if (leftCross.toString().equals("") && rightCross.toString().equals("")) {
				_invalidLettersAndSuffixIndices.put(vertical + 1, new ArrayList<>());
				for (int i = 0; i < _rack.length(); i++) {
					String letter = String.valueOf(_rack.charAt(i));
					letterToValue.put(letter, 0);
				}
			} else {
				for (int i = 0; i < _rack.length(); i++) {
					String toCheck = String.valueOf(_rack.charAt(i));
					String horizontalConcat = leftCross + toCheck + rightCross;
					if (!_scrabbleGame.dictionaryContains(horizontalConcat)) {
						invalidLetters.add(toCheck);
						letterToValue.put(toCheck, 0);
					} else {
						int crossValue = _scrabbleGame.getValueFromString(horizontalConcat, x - leftCross.length(), y + vertical, WordOrientation.Horizontal);
						letterToValue.put(toCheck, crossValue);
					}
				}
				_invalidLettersAndSuffixIndices.put(vertical + 1, invalidLetters);
			}
			vertical = vertical + 1;
			_suffixCrosses.put(vertical, letterToValue);
		}
	}

	private void sortValidWords() {
		ArrayList<Word> temp = new ArrayList<>();
		int size = _validWords.size();
		for (int i = 0; i < size; i++) {
			Word highestWord = null;
			for (int j = 0; j < _validWords.size(); j++) {
				Word thisWord = _validWords.get(j);
				if (j == 0) {
					highestWord = thisWord;
				} else {
					if (thisWord.getValue() >= highestWord.getValue()) {
						highestWord = thisWord;
					}
				}
			}
			_validWords.remove(highestWord);
			temp.add(highestWord);
		}
		_validWords.addAll(temp);
	}

	public PlayerNum getPlayerNumber() {
		return _playerNumber;
	}

	public Word getNewestWord() {
		return _newestWord;
	}

}