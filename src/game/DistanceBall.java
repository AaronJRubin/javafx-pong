package game;

import java.text.DecimalFormat;


class DistanceBall implements Comparable<DistanceBall> {
	
	private final static DecimalFormat df = new DecimalFormat("#.###");
	private final int numberMoves;
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

	@Override
	public int compareTo(DistanceBall o) {
		return numberMoves - o.getNumberMoves();
	}
	
	@Override
	public String toString() {
		return ("NumberMoves: " + numberMoves + ", " + "Height On Impact: " + df.format(adjustedHeight));
	}

}
