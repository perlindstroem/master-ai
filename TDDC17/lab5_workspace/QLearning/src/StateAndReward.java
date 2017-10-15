public class StateAndReward {

	/* State discretization function for the angle controller */
	/*MAIN SOLUTION*/
	public static String getStateAngle(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		int a = discretize(angle, 10, -Math.PI, Math.PI);
		int x = 0;
		int y = 0;
		
		String state = a+","+x+","+y+"!";
		
		return state;
	}

	/* State discretization function for the angle controller */
	/*ALTERNATIVE SOLUTION*/
	public static String getStateAngle1(double angle, double vx, double vy) {
		int a = discretize(angle, 21, -Math.PI, Math.PI);
		if(a>10){
			return "r";
		}
		if(a<10){
			return "l";
		}
		return "s";
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		if (Math.abs(angle) < 0.2 ){
			return 1;
		}
		else if (Math.abs(angle) < 1.5 ){
			return 0.5;
		}
		return 0.1;
	}

	/* State discretization function for the full hover controller */
	/*MAIN SOLUTION*/
	public static String getStateHover(double angle, double vx, double vy) {
		int a = discretize(angle, 10, -Math.PI, Math.PI);
		int x = discretize(vx,10,-25.0,25.0);
		int y = discretize(vy,10,-20,30);
		
		return a+","+x+","+y+"!";
	}
	/*ALTERNATIVE SOLUTION/*
	/* State discretization function for the full hover controller */
	public static String getStateHover1(double angle, double vx, double vy) {		
		return getStateAngle1(angle, vx, vy) + (vy > 0 ? "d" : "u") /*+ (vx > 0 ? "rr" : "ll")*/;
	}
	
	/* Reward function for the full hover controller */
	/*MAIN SOLUTION*/
	public static double getRewardHover(double angle, double vx, double vy) {
		double v = Math.sqrt(Math.pow(vx,2)+Math.pow((vy),2));
		double a = Math.abs(angle);
		
		return 10/((a)+1) + 30/((v)+1);
	}
	
	
	/* Reward function for the full hover controller */
	/*ALTERNATIVE SOLUTION*/
	public static double getRewardHover1(double angle, double vx, double vy) {
		//double v = Math.sqrt(Math.pow(vx,2)+Math.pow((vy-1),2));
		//double a = Math.abs(angle);
		
		return getRewardAngle(angle, vx, vy) + 1/((Math.abs(vy-1))+1)/* * 1/((Math.abs(vx))+1)*/;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
