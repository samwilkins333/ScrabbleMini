package Scrabble;

import java.util.Collection;
import java.util.HashMap;

public class Node {
    public static final char Break = '>';
    public static final char Eow = '$';
    public static final char Root = ' ';
    private char Letter;
    private HashMap<Character, Node> Children = new HashMap<>();

    public Node(char letter) {
        Letter = letter;
    }

    public Node childAt(char letter) {
        return Children.get(letter);
    }

    public Collection<Character> Keys() {
        return Children.keySet();
    }

    public boolean containsKey(char key) {
        return Children.containsKey(key);
    }

    public Node addChild(char letter, Node node) {
        if (Children == null) Children = new HashMap<>();

        if (!Children.containsKey(letter)) {
            Children.put(letter, node);
            return node;
        }

        return Children.get(letter);
    }

    public Node addChild(char letter) {
        if (Children == null) Children = new HashMap<>();

        if (!Children.containsKey(letter)) {
            Node newChild = letter != Eow ? new Node(letter) : null;
            Children.put(letter, newChild);
            return newChild;
        }

        return Children.get(letter);
    }

    public String ToString() {
        return Character.toString(Letter);
    }

    public char getLetter() {
        return Letter;
    }

    public void setLetter(char letter) {
        Letter = letter;
    }
}
