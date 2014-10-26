package main.java.game;

import java.util.LinkedList;
import java.util.List;


import dialogs.MessageBox;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.LinearGradient;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Game extends Stage {
	
	private Stage primaryStage;
	private ParallelTransition parallel = new ParallelTransition();
	private Paddle leftPaddle;
	private Paddle rightPaddle;
	private Group root = new Group();
	private List<Ball> balls = new LinkedList<Ball>();
	private double acceleration;
	
	public Game(Stage primaryStage, final int ballCount, final int difficulty, final double acceleration, final boolean leftComputer,
			final boolean rightComputer, final boolean leftMouse, final boolean rightMouse) {
		this.primaryStage = primaryStage;
		this.acceleration = acceleration;
		this.leftPaddle = new Paddle(difficulty, "Left");
		this.rightPaddle = new Paddle(difficulty, "Right");
		
		addBall();
		
		leftPaddle.setX(Constants.stageWidth / 5);
		leftPaddle.setY(Constants.stageHeight * (1 - Constants.paddleStageRatio) / 2);
		leftPaddle.computeOptimalPoint(balls);
		
		rightPaddle.setX(Constants.stageWidth * 4 / 5);
		rightPaddle.setY(Constants.stageHeight * (1 - Constants.paddleStageRatio) / 2);
		rightPaddle.computeOptimalPoint(balls);
		
		root.getChildren().addAll(leftPaddle, rightPaddle);
		
		double moveBallsFrameAmtDouble = 1000/60;
		Duration moveBallsFrameAmt = Duration.millis(moveBallsFrameAmtDouble);
		final KeyFrame moveBallsFrame = new KeyFrame(moveBallsFrameAmt, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				for (Ball b : balls) {
					b.move();
				}
				checkCollisions();
			}
		});
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.getKeyFrames().add(moveBallsFrame);
		
		final KeyFrame computerFrame = new KeyFrame(Duration.millis(1000/960), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (rightComputer) {
					rightPaddle.moveWithOptimalAlgorithm();
				}
				if (leftComputer) {
					leftPaddle.moveWithOptimalAlgorithm();
				}
			}
		});
		Timeline computerTimeline = new Timeline();
		computerTimeline.setCycleCount(Animation.INDEFINITE);
		computerTimeline.getKeyFrames().add(computerFrame);
		
		double oneCycle = Constants.stageWidth * 6/5 - (Ball.radius * 2);
		//System.out.println("One cycle is " + oneCycle);
		double movesPerCycle = oneCycle / Ball.startingVelocity;
		//System.out.println("There are " + movesPerCycle + " moves in one cycle");
		double millisPerCycle = movesPerCycle * moveBallsFrameAmtDouble;
		//System.out.println("There are " + millisPerCycle + " milliseconds in one cycle");
		double timeBetweenBalls = Math.floor(millisPerCycle / ballCount);
		//System.out.println("The time between balls should be " + timeBetweenBalls);
		final Duration addBallsFrameAmt = Duration.millis(timeBetweenBalls);
		//System.out.println("addBallsFrameAmt is " + addBallsFrameAmt);
		//final Duration addBallsFrameAmt = Duration.millis(100000/60);
		//System.out.println("For reference, the original default was " + 100000/60);
		final KeyFrame oneAddFrame = new KeyFrame(addBallsFrameAmt, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (balls.size() < ballCount) {
					addBall();
					rightPaddle.computeOptimalPoint(balls);
					leftPaddle.computeOptimalPoint(balls);
				}
			}
		});
		Timeline addBallTimeline = new Timeline();
		addBallTimeline.setCycleCount(Animation.INDEFINITE);
		addBallTimeline.getKeyFrames().add(oneAddFrame);

		final Duration accelerateBallsFrameAmt = Duration.millis(100000/60);
		final KeyFrame accelerateBallsFrame = new KeyFrame(accelerateBallsFrameAmt, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (balls.size() >= ballCount) {
					for (Ball b : balls) {
						b.accelerate();
						rightPaddle.computeOptimalPoint(balls);
						leftPaddle.computeOptimalPoint(balls);
					}
				}
			}
		});
		Timeline accelerateBallTimeline = new Timeline();
		accelerateBallTimeline.setCycleCount(Animation.INDEFINITE);
		accelerateBallTimeline.getKeyFrames().add(accelerateBallsFrame);

		parallel.getChildren().addAll(timeline, addBallTimeline, accelerateBallTimeline, computerTimeline);
		
		Scene scene = new Scene(root, Constants.stageWidth, Constants.stageHeight);
		scene.setFill(LinearGradient.valueOf("linear-gradient(from 0% 0% to 100% 100%, red  0% , blue 30%,  black 100%)"));
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (!rightMouse) {
					if (!rightComputer) {
						if (e.getCode() == KeyCode.UP) {
							rightPaddle.shiftUp();
						} else if (e.getCode() == KeyCode.DOWN) {
							rightPaddle.shiftDown();
						}
					}
				}
				if (!leftMouse) {
					if (!leftComputer) {
						if (e.getCode() == KeyCode.F) {
							leftPaddle.shiftDown();
						} else if (e.getCode() == KeyCode.R) {
							leftPaddle.shiftUp();
						}
					}
				}
			}
		});
		if (rightMouse) {
			EventHandler<MouseEvent> followMouse = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent m) {
					if (!rightComputer) {
						double y = m.getY();
						if (y + rightPaddle.getHeight() > Constants.stageHeight) {
							y = Constants.stageHeight - rightPaddle.getHeight();
						}
						if (y < 0) {
							y = 0;
						}
						rightPaddle.setY(y);
					}
				}
			};
			scene.setOnMouseMoved(followMouse);
			scene.setOnMouseDragged(followMouse);
		} else if (leftMouse) {
			EventHandler<MouseEvent> followMouse = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent m) {
					if (!leftComputer) {
						double y = m.getY();
						if (y + leftPaddle.getHeight() > Constants.stageHeight) {
							y = Constants.stageHeight - leftPaddle.getHeight();
						}
						if (y < 0) {
							y = 0;
						}
						leftPaddle.setY(y);
					}
				}
			};
			scene.setOnMouseMoved(followMouse);
			scene.setOnMouseDragged(followMouse);
		}
		setScene(scene);
		setResizable(false);
		show();
		parallel.play();
		setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				parallel.stop();
				close();
			}
		});
	}
	
	public void addBall() {
		Ball toAdd = new Ball(acceleration); 
		toAdd.setCenterX(Constants.stageWidth / 2);
		toAdd.setCenterY(Constants.stageHeight / 2);
		root.getChildren().add(toAdd);
		balls.add(toAdd);
	}
	/*
	public double averageBallHeight() {
		double toReturn = 0;
		for (Ball b : balls) {
			toReturn += b.getCenterY();
		}
		return toReturn / balls.size();
	} */
	/*
	private double distance(Ball ball, double referencePoint) {
		double paddleGap = Constants.stageHeight * 3/5;
		if (ball.getXVelocity() > 0 && referencePoint > ball.getCenterX()) {
			return referencePoint - ball.getCenterX() - ball.getRadius();
		} else if (ball.getXVelocity() < 0 && referencePoint < ball.getCenterX()) {
			return ball.getCenterX() - referencePoint - ball.getRadius();
		} else if (ball.getXVelocity() > 0) {
			return referencePoint - ball.getCenterX() - ball.getRadius() + paddleGap;
		} else {
			return ball.getCenterX() - referencePoint - ball.getRadius() + paddleGap;
		}
	}
	
	public double relativeAverageBallHeight(double referencePoint) {
		double toReturn = 0;
		double toDivide = 0;
		for (Ball b : balls) {
			double distance = distance(b, referencePoint);
			if (distance == 0) {
				distance = .01;
			}
			toReturn += b.getCenterY() / (distance * distance);
			toDivide += 1 / (distance * distance);
		}
		return toReturn / toDivide;
	}
	 */
	private void checkCollisions() {
		for (Ball ball : balls) {
			if (ball.getBoundsInParent().intersects(leftPaddle.getBoundsInParent())) {
				ball.collision();
				ball.move();
				rightPaddle.computeOptimalPoint(balls);
				leftPaddle.computeOptimalPoint(balls);
				continue;
			}
			if (ball.getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {
				ball.collision();
				ball.move();
				rightPaddle.computeOptimalPoint(balls);
				leftPaddle.computeOptimalPoint(balls);
				continue;
			}
			if (ball.getBoundsInParent().getMaxX() > rightPaddle.getBoundsInParent().getMinX()) {
				gameEnd("Right");
			//	System.out.println(rightPaddle.expectedMove());
				parallel.stop();
			} else if (ball.getBoundsInParent().getMinX() < leftPaddle.getBoundsInParent().getMaxX()) {
				gameEnd("Left");
			//	System.out.println(leftPaddle.expectedMove());
				parallel.stop();
			}
		}
		return;
	}
	
	private void gameEnd(String loser) {
		MessageBox message = new MessageBox(primaryStage, loser + " Player Loses!");
		message.show();
	}

}
