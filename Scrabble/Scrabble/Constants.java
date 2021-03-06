package Scrabble;

import javafx.scene.paint.Color;
import java.util.HashMap;

class Constants {
	
	static final PlayerType PLAYER_ONE_START_STATE = PlayerType.AI;
	static final PlayerType PLAYER_TWO_START_STATE = PlayerType.Human;

	static final HashMap<Integer, Alpha> TILE_INFO;
	static {
		TILE_INFO = new HashMap<>();

		TILE_INFO.put(0, new Alpha("BLANK", 2, 0));
		TILE_INFO.put(1, new Alpha("A", 9, 1));
		TILE_INFO.put(2, new Alpha("B", 2, 3));
		TILE_INFO.put(3, new Alpha("C", 2, 3));
		TILE_INFO.put(4, new Alpha("D", 4, 2));
		TILE_INFO.put(5, new Alpha("E", 12, 1));
		TILE_INFO.put(6, new Alpha("F", 2, 4));
		TILE_INFO.put(7, new Alpha("G", 3, 2));
		TILE_INFO.put(8, new Alpha("H", 2, 4));
		TILE_INFO.put(9, new Alpha("I", 9, 1));
		TILE_INFO.put(10, new Alpha("J", 1, 8));
		TILE_INFO.put(11, new Alpha("K", 1, 5));
		TILE_INFO.put(12, new Alpha("L", 4, 1));
		TILE_INFO.put(13, new Alpha("M", 2, 3));
		TILE_INFO.put(14, new Alpha("N", 6, 1));
		TILE_INFO.put(15, new Alpha("O", 8, 1));
		TILE_INFO.put(16, new Alpha("P", 2, 3));
		TILE_INFO.put(17, new Alpha("Q", 1, 10));
		TILE_INFO.put(18, new Alpha("R", 6, 1));
		TILE_INFO.put(19, new Alpha("S", 4, 1));
		TILE_INFO.put(20, new Alpha("T", 6, 1));
		TILE_INFO.put(21, new Alpha("U", 4, 1));
		TILE_INFO.put(22, new Alpha("V", 2, 4));
		TILE_INFO.put(23, new Alpha("W", 2, 4));
		TILE_INFO.put(24, new Alpha("X", 1, 8));
		TILE_INFO.put(25, new Alpha("Y", 2, 4));
		TILE_INFO.put(26, new Alpha("Z", 1, 10));
	}

	static final String A = "_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	static final int GRID_FACTOR = 42;
	static final int SCENE_WIDTH = (GRID_FACTOR + 5) * 31;
	static final int SCENE_HEIGHT = (GRID_FACTOR + 5) * 17 + 10;
	static final double BOARD_OPACITY = 1.0;
	static final int TILE_PADDING = 4;

	static final Color BOARD_FILL = Color.WHITE;
	static final Color DOUBLE_WORD_SCORE = Color.web("#E71308"); //RED
	static final Color DOUBLE_LETTER_SCORE = Color.web("#3333FF"); //BLUE
	static final Color TRIPLE_WORD_SCORE = Color.web("#FF9900"); //ORANGE
	static final Color TRIPLE_LETTER_SCORE = Color.web("#00CC00"); //GREEN
	static final Color SHADOW_FILL = Color.web("#000000"); //GREY

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

	static final String DOMESTIC_MANNERS = ClassLoader.getSystemResource(System.getProperty("user.dir") + "Fonts/domesticmanners.ttf").toExternalForm();

	static final double PLACEMENT_DURATION = 0.6;
	static final double FLASH_SPACING_DURATION = 0.6;
	static final double AI_ROTATE_DURATION = 0.1;
}