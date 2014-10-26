package main.java.game;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

final class Constants {
	static private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	static final double stageWidth = screenBounds.getWidth();
	static final double stageHeight = screenBounds.getHeight();
	static final double paddleStageRatio = .5;
	
	private Constants() {
	}
	
}
