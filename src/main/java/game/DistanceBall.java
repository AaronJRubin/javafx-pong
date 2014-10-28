package game;

import java.text.DecimalFormat;

/** 
This class is constructed with a ball and a "reference point," corresponding to a horizontal coordinate.
In practice, this horizontal coordinate will be the position of a paddle controlled by an AI.
Taking into account the horizontal and vertical velocities of the ball, the constructor
computes the expected vertical height of the ball when the ball reaches that horizontal coordinate,
as well as the number of move actions needed to reach that coordinate, where a move action consists of offsetting
the ball's position by the x and y velocities. This class is used in the AI's optimal move algorithm,
and nowhere else.
*/
class DistanceBall implements Comparable<DistanceBall> {
	
	// Used for toString, which I found useful when debugging this class.
	private final static DecimalFormat df = new DecimalFormat("#.###");
	// The number of movies needed for the ball to reach the reference point passed in.
	private final int numberMoves;
	// The expected y-coordinate of the ball when it reaches the reference point passed in.
	private final double adjustedHeight;
	
	public DistanceBall(Ball b, double referencePoint) {
		double xVelocity = b.getXVelocity();
		double yVelocity = b.getYVelocity();
		double centerX = b.getCenterX();
		double centerY = b.getCenterY();
		double radius = b.getRadius();
		this.numberMoves = Math.abs((int) Math.floor(distance(referencePoint, xVelocity, centerX, radius) / xVelocity));
		double adjustedHeight = centerY + yVelocity * numberMoves;
		if (adjustedHeight < 0) {
			adjustedHeight *= -1;
		} else if (adjustedHeight > Constants.stageHeight) {
			adjustedHeight = Constants.stageHeight - (adjustedHeight - Constants.stageHeight);
		} 
		this.adjustedHeight = adjustedHeight;
	}

	private static double distance(double referencePoint, double xVelocity, double centerX, double radius) {
	//	System.out.println("Calling distance with reference point " + referencePoint);
		double paddleGap = Constants.stageWidth * .6;
	//	System.out.println("Paddle Gap is " + paddleGap);
		if (xVelocity > 0 && referencePoint > centerX) {
			return referencePoint - centerX - radius;
		} else if (xVelocity < 0 && referencePoint < centerX) {
			return centerX - referencePoint - radius;
		} else if (xVelocity < 0) {
		//	System.out.println("Found a ball that meets certain conditions");
			return paddleGap - (referencePoint - centerX - radius) + paddleGap;
		} else {
		//	System.out.println("Elsewhere condition");
			return paddleGap - (centerX - referencePoint - radius) + paddleGap;
		}
	}
	
	public int getNumberMoves() {
		return numberMoves;
	}
	
	public double getAdjustedHeight() {
		return adjustedHeight;
	}

	/** 
	It is useful to sort DistanceBalls by number of moves when computing the AI's optimal action.
	The paddle AI looks at the closest balls (the ones with the least number of moves needed to reach it)
	and adjusts its position accordingly so as to be guaranteed to hit the closest ball, as close as possible
	to the next-closest given the need to hit the first-closest, etc.
	*/
	@Override
	public int compareTo(DistanceBall o) {
		return numberMoves - o.getNumberMoves();
	}
	
	@Override
	public String toString() {
		return ("NumberMoves: " + numberMoves + ", " + "Height On Impact: " + df.format(adjustedHeight));
	}

}
