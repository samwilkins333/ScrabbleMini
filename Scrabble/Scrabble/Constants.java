package Scrabble;

import java.util.HashMap;
import javafx.scene.paint.Color;

public class Constants {
	
	Constants() {
		this.setUpValues();
	}
	
	static final String PLAYER_ONE_START_STATE = "COMPUTER";
	static final String PLAYER_TWO_START_STATE = "HUMAN";
	
	static final HashMap<String, Integer> VALUES = new HashMap<String, Integer>();

	private void setUpValues() {
		VALUES.put("A", 1);
		VALUES.put("B", 3);
		VALUES.put("C", 3);
		VALUES.put("D", 2);
		VALUES.put("E", 1);
		VALUES.put("F", 4);
		VALUES.put("G", 2);
		VALUES.put("H", 4);
		VALUES.put("I", 1);
		VALUES.put("J", 8);
		VALUES.put("K", 5);
		VALUES.put("L", 1);
		VALUES.put("M", 3);
		VALUES.put("N", 1);
		VALUES.put("O", 1);
		VALUES.put("P", 3);
		VALUES.put("Q", 10);
		VALUES.put("R", 1);
		VALUES.put("S", 1);
		VALUES.put("T", 1);
		VALUES.put("U", 1);
		VALUES.put("V", 4);
		VALUES.put("W", 4);
		VALUES.put("X", 8);
		VALUES.put("Y", 4);
		VALUES.put("Z", 10);
	}
	
	static final int GRID_FACTOR = 42;
	static final int SCENE_WIDTH = (GRID_FACTOR + 5) * 31;
	static final int SCENE_HEIGHT = (GRID_FACTOR + 5) * 17 + 10;
	static final Color BOARD_FILL = Color.WHITE;
	static final double BOARD_OPACITY = 1.0;
	static final int TILE_PADDING = 4;
	static final Color DOUBLE_WORD_SCORE = Color.web("#E71308"); //RED
	static final Color DOUBLE_LETTER_SCORE = Color.web("#3333FF"); //BLUE
	static final Color TRIPLE_WORD_SCORE = Color.web("#FF9900"); //ORANGE
	static final Color TRIPLE_LETTER_SCORE = Color.web("#00CC00"); //GREEN
	static final Color SHADOW_FILL = Color.web("#000000"); //GREY
	static final double FADE_OUT_DURATION = 0.4;
	static final double FADE_IN_DURATION = 0.4;
	static final int ZEROETH_ROW_OFFSET = 3;
	static final int ZEROETH_COLUMN_OFFSET = 13;
	static final int COLLECTION_VERTICAL_OFFSET = 7;
	static final int COLLECTION_ONE_HORIZONTAL_OFFSET = ZEROETH_COLUMN_OFFSET - 2;
	static final int COLLECTION_TWO_HORIZONTAL_OFFSET = ZEROETH_COLUMN_OFFSET + 16;
	static final double LABEL_ANIMATION = 0.3;
	static final double FADED_TILE_OPACITY = 0.0;
	static final double FEEDBACK_FLASH_DURATION = 0.5;
	static final double DRAW_INTERVAL = 0.7;
	static final double SCORE_FADE_DURATION = 0.08;

	static final int X0 = ZEROETH_COLUMN_OFFSET;
	static final int X1 = ZEROETH_COLUMN_OFFSET + 1;
	static final int X2 = ZEROETH_COLUMN_OFFSET + 2;
	static final int X3 = ZEROETH_COLUMN_OFFSET + 3;
	static final int X4 = ZEROETH_COLUMN_OFFSET + 4;
	static final int X5 = ZEROETH_COLUMN_OFFSET + 5;
	static final int X6 = ZEROETH_COLUMN_OFFSET + 6;
	static final int X7 = ZEROETH_COLUMN_OFFSET + 7;
	static final int X8 = ZEROETH_COLUMN_OFFSET + 8;
	static final int X9 = ZEROETH_COLUMN_OFFSET + 9;
	static final int X10 = ZEROETH_COLUMN_OFFSET + 10;
	static final int X11 = ZEROETH_COLUMN_OFFSET + 11;
	static final int X12 = ZEROETH_COLUMN_OFFSET + 12;
	static final int X13 = ZEROETH_COLUMN_OFFSET + 13;
	static final int X14 = ZEROETH_COLUMN_OFFSET + 14;
	static final int X15 = ZEROETH_COLUMN_OFFSET + 15;

	static final int Y0 = ZEROETH_ROW_OFFSET;
	static final int Y1 = ZEROETH_ROW_OFFSET + 1;
	static final int Y2 = ZEROETH_ROW_OFFSET + 2;
	static final int Y3 = ZEROETH_ROW_OFFSET + 3;
	static final int Y4 = ZEROETH_ROW_OFFSET + 4;
	static final int Y5 = ZEROETH_ROW_OFFSET + 5;
	static final int Y6 = ZEROETH_ROW_OFFSET + 6;
	static final int Y7 = ZEROETH_ROW_OFFSET + 7;
	static final int Y8 = ZEROETH_ROW_OFFSET + 8;
	static final int Y9 = ZEROETH_ROW_OFFSET + 9;
	static final int Y10 = ZEROETH_ROW_OFFSET + 10;
	static final int Y11 = ZEROETH_ROW_OFFSET + 11;
	static final int Y12 = ZEROETH_ROW_OFFSET + 12;
	static final int Y13 = ZEROETH_ROW_OFFSET + 13;
	static final int Y14 = ZEROETH_ROW_OFFSET + 14;
	static final int Y15 = ZEROETH_ROW_OFFSET + 15;
	
	static final Color GRAPHITE = Color.web("#35373c");
	static final Color GREEN = Color.web("#198c19");
	static final Color RED = Color.web("#bf0000");
	
	static final double FONT_SIZE_POST_IT = 40;
	static final double FONT_SIZE_WORD_LIST = 35;
	static final String HANDWRITING = ClassLoader.getSystemResource("Fonts/domesticmanners.ttf").toExternalForm();
		
	static final double PLACEMENT_DURATION = 0.6;
	static final boolean PRINT_STATUS = false;
	static final double FLASH_SPACING_DURATION = 0.6;
	static final double AI_ROTATE_DURATION = 0.1;
	
}