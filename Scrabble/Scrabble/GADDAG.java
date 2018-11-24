package Scrabble;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GADDAG {
    private Node RootNode;

    GADDAG() {
        RootNode = new Node(Node.Root);
    }

    void Add(String word) {
        word = word.toLowerCase();
        List<Node> prevNode = new ArrayList<>();
        int len = word.length();

        for (int i = 1; i <= len; ++i) {
            String prefix = word.substring(0, i);
            String suffix = "";

            if (i != len)
                suffix = word.substring(i, len);
            String addWord = new StringBuilder(prefix).reverse().toString() + Node.Break + suffix + Node.Eow;

            Node currentNode = RootNode;
            boolean breakFound = false;
            int j = 0;

            for (char c : addWord.toCharArray()) {
                if (breakFound && prevNode.size() > j) {
                    currentNode.addChild(c, prevNode.get(j));
                    break;
                }

                currentNode = currentNode.addChild(c);

                if (prevNode.size() == j)
                    prevNode.add(currentNode);

                if (c == Node.Break)
                    breakFound = true;

                j++;
            }
        }
    }

    private static String getWord(String str) {
        StringBuilder word = new StringBuilder();

        for (int i = str.indexOf(Node.Break) - 1; i >= 0; i--)
            word.append(str.charAt(i));

        for (int i = str.indexOf(Node.Break) + 1; i < str.length(); i++)
            word.append(str.charAt(i));

        return word.toString();
    }

    ArrayList<String> containsHookWithRack(String hook, String rack) {
        hook = new StringBuilder(hook.toLowerCase()).reverse().toString();
        rack = rack.toLowerCase();

        HashSet<String> set = new HashSet<>();

        containsHookWithRackRecursive(RootNode, set, "", rack, hook);
        System.out.println(set);
        return new ArrayList<>(set);
    }

    private void containsHookWithRackRecursive(Node node, Set<String> set, String letters, String rack, String hook) {
        if (node == null) {
            String word = getWord(letters);
            set.add(word);
            return;
        }

        if (hook != null && !hook.isEmpty()) {
            char let = node.getLetter();
            letters += let != Node.Root ? String.valueOf(let) : "";

            char first = hook.charAt(0);
            if (node.containsKey(first)) {
                String h = new StringBuilder(hook).delete(0, 2).toString();
                containsHookWithRackRecursive(node.childAt(hook.charAt(0)), set, letters, rack, h);
            }
        } else {
            char let = node.getLetter();
            letters += let != Node.Root ? String.valueOf(let) : "";

            Stream<Character> keys = node.Keys().stream().filter(c -> rack.contains(String.valueOf(c)) || c == Node.Eow || c == Node.Break);
            for (char key : keys.collect(Collectors.toList())) {
                StringBuilder rackBuilder = new StringBuilder(rack);
                int index = rack.indexOf(key);
                String newRack = (key != Node.Eow && key != Node.Break) ? rackBuilder.delete(index, index + 1).toString() : rack;
                containsHookWithRackRecursive(node.childAt(key), set, letters, newRack, hook);
            }
        }
    }
}
