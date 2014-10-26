package pong;

import buttons.HelpButton;
import game.Game;
import io.IO;
import adjustables.AdjustableDouble;
import adjustables.AdjustableInt;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import styles.Styles;

public class Main extends Application {
	
	Stage primaryStage;
	VBox root;
	Game currentGame;

	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
			this.primaryStage = primaryStage;
			this.root = new VBox();
			
			root.setAlignment(Pos.CENTER);
			root.setSpacing(10);
			root.setPadding(new Insets(10, 10, 10, 10));
			
			HBox settings = new HBox();
			settings.setSpacing(20);
			settings.setAlignment(Pos.CENTER);
			final AdjustableInt numBalls = new AdjustableInt("Ball Count", 1, 10, 1);
			final AdjustableInt compLevel = new AdjustableInt("Computer Level", 1, 10, 1);
			final AdjustableDouble accelerationInput = new AdjustableDouble("Acceleration", 0, 1, .1);
			
			final CheckBox rightMouseControl = new CheckBox();
			rightMouseControl.setGraphic(new Label("Control Right Paddle With Mouse"));
			final CheckBox leftMouseControl = new CheckBox();
			leftMouseControl.setGraphic(new Label("Control Left Paddle With Mouse"));
			
			leftMouseControl.selectedProperty().addListener(new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			    	if (newValue) {
			    		rightMouseControl.setSelected(false);
			    	} 
			    }
			});
			
			rightMouseControl.selectedProperty().addListener(new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			    	if (newValue) {
			    		leftMouseControl.setSelected(false);
			    	} 
			    }
			});
			
			final CheckBox rightComputer = new CheckBox();
			rightComputer.setGraphic(new Label("Make Right Paddle Computer"));
			rightComputer.selectedProperty().addListener(new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			    	if (newValue) {
			    		rightMouseControl.setDisable(true);
			    		rightMouseControl.setSelected(false);
			    	} else {
			    		rightMouseControl.setDisable(false);
			    	}
			    }
			});
			
			final CheckBox leftComputer = new CheckBox();
			leftComputer.setGraphic(new Label("Make Left Paddle Computer"));
			leftComputer.selectedProperty().addListener(new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			    	if (newValue) {
			    		leftMouseControl.setDisable(true);
			    		leftMouseControl.setSelected(false);
			    	} else {
			    		leftMouseControl.setDisable(false);
			    	}
			    }
			});
			
			VBox left = new VBox();
			left.setSpacing(10);
			left.setAlignment(Pos.CENTER_LEFT);
			left.getChildren().addAll(leftComputer, leftMouseControl);
			
			VBox right = new VBox();
			right.setSpacing(10);
			right.setAlignment(Pos.CENTER_LEFT);
			right.getChildren().addAll(rightComputer, rightMouseControl);
			
			settings.getChildren().addAll(numBalls, compLevel, accelerationInput, left, right);
			root.getChildren().add(settings);
			Button playButton = new Button("Play!");
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					int ballCount = Integer.parseInt(numBalls.getCurrentValue());
					int difficulty = Integer.parseInt(compLevel.getCurrentValue());
					double acceleration = Double.parseDouble(accelerationInput.getCurrentValue());
					boolean isLeftComputer = leftComputer.isSelected();
					boolean isRightComputer = rightComputer.isSelected();
					boolean rightMouse = rightMouseControl.isSelected();
					boolean leftMouse = leftMouseControl.isSelected();
					currentGame = new Game(primaryStage, ballCount, difficulty, acceleration, isLeftComputer, isRightComputer, leftMouse, rightMouse);
				}
			});
			String helpMessage = IO.stringFromStream(getClass().getResourceAsStream("HelpMessage.txt"), 1000);
			HelpButton help = new HelpButton(primaryStage, helpMessage);
			HBox buttons = new HBox();
			buttons.setAlignment(Pos.CENTER);
			buttons.getChildren().addAll(playButton, help);
			root.getChildren().add(buttons);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(Styles.getStylesheet());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
		
			primaryStage.show();
		}  

	

	
}
