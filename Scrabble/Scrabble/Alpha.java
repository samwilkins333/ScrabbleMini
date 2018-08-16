package Scrabble;

import javafx.scene.image.Image;

class Alpha {
    private final String _letter;
    private final int frequency;
    private final int _value;
    private Image _image;

    Alpha(String let, int frequency, int val) {
        _letter = let;
        this.frequency = frequency;
        _value = val;
        _image = new Image("Images/Scrabble Tiles/" + let.toLowerCase() + ".png");
    }

    String getLetter() {
        return _letter;
    }

    int getValue() {
        return _value;
    }

    int getFrequency() {
        return frequency;
    }

    public Image getImage() {
        return _image;
    }
}
