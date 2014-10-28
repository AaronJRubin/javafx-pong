package game;

import stages.DebugStage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

class Paddle extends Rectangle {
	
	/** 
	debugStage is used for displaying the results of various AI algorithm computations
	when the debug static variable is set to True.
	*/
	private DebugStage debugStage;
	/** 
	 optimalPoint is the AI's target y-coordinate (i.e., the target coordinate
	 of the center of the paddle). This is re-computed whenever a ball's
	 velocity changes or a new ball is added. The paddle, of course,
	 is not aware of these events. The main Game class calls the Paddle's
	 computeOptimalPoint method with the currently existing balls whenever a re-computation
	 is needed.
	*/ 
	private double optimalPoint;
	/** 
	adjustment is the amount that the AI moves in a single
	game loop iteration. The more difficult the AI, the
	faster it moves, and the higher the adjustment.
	*/
	private double adjustment;
	// See comments for debugStage above.
	private static final boolean debug = false;

	public Paddle(int difficulty) {
		this(difficulty, "Unnamed Paddle");
	}
	
	public Paddle(int difficulty, String name) {
		super();
		if (debug) {
			debugStage = new DebugStage();
			debugStage.setTitle(name);
			debugStage.show();
		} 
		adjustment = Constants.stageHeight / (((10 - difficulty) + 1) * 800);
		setHeight(Constants.stageHeight * Constants.paddleStageRatio);
		setWidth(30);
		Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
		LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		setFill(lg1);
		optimalPoint = getY();
	}
	
	public Paddle() {
		this(5);
	}
	
	public void shiftUp() {
		setY(getY() - Constants.stageHeight / 17.5);
		if (getY() < 0) {
			setY(0);
		}
	}
	
	private void shiftUpComputer() {
		setY(getY() - adjustment);
		if (getY() < 0) {
			setY(0);
		}
	}
	
	public void shiftDown() {
		setY(getY() + Constants.stageHeight / 17.5);
		if (getY() + getHeight() > Constants.stageHeight) {
			setY(Constants.stageHeight - getHeight());
		}
	}
	
	private void shiftDownComputer() {
		setY(getY() + adjustment);
		if (getY() + getHeight() > Constants.stageHeight) {
			setY(Constants.stageHeight - getHeight());
		}
	}
	/*
	private double distance(Ball ball) {
		double paddleGap = Constants.stageWidth * 3/5;
		double referencePoint = getX();
		if (ball.getXVelocity() > 0 && referencePoint > ball.getCenterX()) {
			return referencePoint - ball.getCenterX();
		} else if (ball.getXVelocity() < 0 && referencePoint < ball.getCenterX()) {
			return ball.getCenterX() - referencePoint;
		} else if (ball.getXVelocity() > 0) {
			return referencePoint - ball.getCenterX() + paddleGap;
		} else {
			return ball.getCenterX() - referencePoint + paddleGap;
		}
	}
	
	private double relativeAverageBallHeight(List<Ball> balls) {
		double toReturn = 0;
		double toDivide = 0;
		for (Ball b : balls) {
			double distance = distance(b);
			if (distance == 0) {
				distance = .01;
			}
			double numberMoves = distance / b.getXVelocity();
			double adjustedHeight = b.getCenterY() + b.getYVelocity() * numberMoves;
			if (adjustedHeight < 0) {
				adjustedHeight *= -1;
			} else if (adjustedHeight > Constants.stageHeight) {
				adjustedHeight = Constants.stageHeight - (adjustedHeight - Constants.stageHeight);
			} 
			toReturn += adjustedHeight / (distance * distance);
			toDivide += 1 / (distance * distance);
		}
		return toReturn / toDivide;
	}

	public void moveWithWeightedHeightAlgorithm(List<Ball> balls) {
		double relativeAverageBallHeight = relativeAverageBallHeight(balls);
		int i = 0;
		while (i < 2) {
			if (getY() + getHeight() / 2 < relativeAverageBallHeight) {
				shiftDownComputer();
			} else if (getY() + getHeight() / 2 > relativeAverageBallHeight) {
				shiftUpComputer();
			}
			i++;
		}
	}
	
	public void moveWithLazyAlgorithm(List<Ball> balls) {
		List<DistanceBall> toSort = new ArrayList<DistanceBall>(balls.size());
		for (Ball b : balls) {
			toSort.add(new DistanceBall(b, getX()));
		}
		Collections.sort(toSort);
	//	System.out.println(toSort);
		Bounds bounds = getBoundsInParent();
		for (DistanceBall b : toSort) {
	//		System.out.println("Examining ball that will require " + b.getNumberMoves() + " to get to this paddle.");
			double adjustedHeight = b.getAdjustedHeight();
			if (adjustedHeight > bounds.getMinY() && adjustedHeight < bounds.getMaxY()) {
				continue;
			} else {
				if (adjustedHeight > bounds.getMinY()) {
					shiftDownComputer();
					return;
				} else {
					shiftUpComputer();
					return;
				}
			}
		}
	} */
	
	/**
	The getSafeMin and getSafeMax functions compute the lowest and highest
	possible y-coordinates for the paddle such that the paddle hits
	the DistanceBall passed to it.
	*/
	private double getSafeMin(DistanceBall b) {
		return b.getAdjustedHeight() - getHeight() / 2;
	}
	
	private double getSafeMax(DistanceBall b) {
		return b.getAdjustedHeight() + getHeight() / 2;
	}

	// I hate mixing debugging statements with the actual algorithm
	// like I do in the below code, but I'm leaving them in for now.
	public void computeOptimalPoint(List<Ball> balls) {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder log = new StringBuilder();
		List<DistanceBall> toSort = new ArrayList<DistanceBall>(balls.size());
		for (Ball b : balls) {
			toSort.add(new DistanceBall(b, getX()));
		}
		Collections.sort(toSort);
		if (debug) {
			log.append("Balls:\n");
			for (DistanceBall b : toSort) {
				log.append(b + "\n");
			}
			log.append("Calculated Optimal Point:\n");
		}

		//Compute safe range for the closest ball 
		DistanceBall first = toSort.get(0);
		double min = getSafeMin(first);
		double max = getSafeMax(first);

		//Examine other balls to get as close as possible without missing first ball
		int i = 1;
		while (i < toSort.size()) {
			DistanceBall cur = toSort.get(i);
			double curMin = getSafeMin(cur);
			double curMax = getSafeMax(cur);
			if (curMin > min && curMin < max) {
				min = curMin;
			} else if (curMax < max && curMax > min) {
				max = curMax;
			} else if (curMax > max) {
				optimalPoint = max;
				if (debug) {
					log.append(df.format(optimalPoint));
					debugStage.setMessage(log.toString());
				}
				return;
			} else if (curMin < min) {
				optimalPoint = min;
				if (debug) {
					log.append(df.format(optimalPoint));
					debugStage.setMessage(log.toString());
				}
				return;
			}
			else {
				optimalPoint = (min + max) / 2;
				if (debug) {
					log.append(df.format(optimalPoint));
					debugStage.setMessage(log.toString());
				}
				return;
			}
			i++;
		}
		// Here, we shoot for the midpoint for purely aesthetic reasons.
		// optimalPoint = min or optimalPoint = max would both be fine.
		optimalPoint = (min + max) / 2;
		if (debug) {
			log.append(df.format(optimalPoint));
			debugStage.setMessage(log.toString());
		}
	}

	public void moveWithOptimalAlgorithm() {
		if (getY() + getHeight() / 2 < optimalPoint) {
			shiftDownComputer();
			if (getY() + getHeight() / 2 > optimalPoint) {
				shiftUpComputer();
			}
		} else if (getY() + getHeight() / 2 > optimalPoint) {
				shiftUpComputer();
				if (getY() + getHeight() / 2 < optimalPoint) {
					shiftDownComputer();
				}
			}
	}

	public String expectedMove() {
		if (getY() + getHeight() / 2 < optimalPoint) {
			if (getY() + getHeight() / 2 > optimalPoint) {
				return "Expected to not move";
			} else {
				return "Expected to move up";
			}
		} else if (getY() + getHeight() / 2 > optimalPoint) {
			if (getY() + getHeight() / 2 < optimalPoint) {
				return "Expected to not move";
			} else {
				return "Expected to move down";
			}
		}
		return "Expected to not move.";
	}

}


