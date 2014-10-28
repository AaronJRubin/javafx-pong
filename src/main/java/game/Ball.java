package game;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

class Ball extends Circle {
	
	public final static double radius = 20;
	public final static double startingVelocity = 1;
	private static Random randomizer = new Random();
	
	private final ScaleTransition grow;
	
	private double acceleration = .1;
	private double xVelocity;
	private double yVelocity;
	
	public Ball(double acceleration) {
		this(startingVelocity, 0);
		this.acceleration = acceleration;
	}
	
	public Ball() {
		this(startingVelocity, 0);
	}
	
	protected Ball(double xVelocity, double yVelocity) {
		super();
		grow = new ScaleTransition();
		grow.setDuration(Duration.millis(3000));
		grow.setCycleCount(Animation.INDEFINITE);
		grow.setNode(this);
		grow.setToX(2f);
		grow.setToY(2f);
		grow.setAutoReverse(true);
		grow.play();
		setRadius(radius);
		Stop[] stops = new Stop[] { new Stop(0, Color.GREEN), new Stop(1, Color.YELLOW)};
		LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		setFill(lg1);
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	
	public void reverseXVelocity() {
		xVelocity = -xVelocity;
		new Random();
	}
	
	public void accelerate() {
		if (xVelocity > 0) {
			xVelocity += acceleration;
		} else {
			xVelocity -= acceleration;
		}
	}
	
	public void collision() {
		reverseXVelocity();
		randomizeYVelocity();
	}
	
	public void randomizeYVelocity() {
		yVelocity = yVelocity + randomizer.nextDouble() - .5;
	}
	
	public void move() {
		setCenterX(getCenterX() + xVelocity);
		setCenterY(getCenterY() + yVelocity);
		if (getCenterY() + getRadius() * getScaleY() > Constants.stageHeight) {
			setCenterY(Constants.stageHeight - getRadius() * getScaleY());
			yVelocity = -yVelocity;
		}
		if (getCenterY() - getRadius() * getScaleY() < 0) {
			setCenterY(getRadius() * getScaleY());
			yVelocity = -yVelocity;
		}
	}
	
	public double getXVelocity() {
		return xVelocity;
	}
	
	public double getYVelocity() {
		return yVelocity;
	}
	
	public ScaleTransition getAnimation() {
		return this.grow;
	}

}
