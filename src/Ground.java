import java.awt.Color;
import java.awt.Graphics;

public class Ground {

	private double y0 = 150;
	private double A0 = 20;
	private double T0 = 200;
	private double P0 = 0;
	private double A1 = 50;
	private double T1 = 600;
	private double P1 = 0;
	private double A2 = 70;
	private double T2 = 1500;
	private double P2 = 0;
	
	private double VARIATION = 0.05;
	private double MAX_VARIATION = 0.2;
	
	private double[] coeffs = new double[10];
	private double[] variation = new double[10];
	private static final int NUM_STEPS = 1000;
	private int step;
	
	private int[] x = new int[GameCourt.COURT_WIDTH+2];
	private int[] y = new int[GameCourt.COURT_WIDTH+2];
	
	public Ground() {
		reinitialize();
		coeffs[0] = y0;
		coeffs[1] = A0;
		coeffs[2] = T0;
		coeffs[3] = P0;
		coeffs[4] = A1;
		coeffs[5] = T1;
		coeffs[6] = P1;
		coeffs[7] = A2;
		coeffs[8] = T2;
		coeffs[9] = P2;
	}
	
	public void reinitialize() {
		updatePoints();
		step = 0;
		for (int i = 0; i < 10; i++) {
			variation[i] = (Math.random()-0.5) * VARIATION;
		}
	}
	
	public void updatePoints() {
		for (int i = 0; i < GameCourt.COURT_WIDTH; i++){
			x[i] = i;
			y[i] = GameCourt.COURT_HEIGHT - (int) Math.round(func(i));
		}
		x[GameCourt.COURT_WIDTH] = GameCourt.COURT_WIDTH-1;
		y[GameCourt.COURT_WIDTH] = GameCourt.COURT_HEIGHT-1;
		x[GameCourt.COURT_WIDTH+1] = 0;
		y[GameCourt.COURT_WIDTH+1] = GameCourt.COURT_HEIGHT-1;
	}
	
	public void draw(Graphics g) {
		g.setColor(new Color(50,120,50));
		g.fillPolygon(x, y, GameCourt.COURT_WIDTH+2);
    }
	
	public double func(double x) {
		return coeffs[0] + coeffs[1]*Math.cos(2*Math.PI/coeffs[2] * x + coeffs[3]) + 
						   coeffs[4]*Math.cos(2*Math.PI/coeffs[5] * x + coeffs[6]) + 
						   coeffs[7]*Math.cos(2*Math.PI/coeffs[8] * x + coeffs[9]);
	}
	
	public double dfunc(double x) {
		return -1 * coeffs[1]*(2*Math.PI/coeffs[2]) * Math.sin(2*Math.PI/coeffs[2] * x + coeffs[3]) +
			   -1 * coeffs[4]*(2*Math.PI/coeffs[5]) * Math.sin(2*Math.PI/coeffs[5] * x + coeffs[6]) +
			   -1 * coeffs[7]*(2*Math.PI/coeffs[8]) * Math.sin(2*Math.PI/coeffs[8] * x + coeffs[9]);
	}
	
	private void clip() {
		coeffs[0] = Math.max(Math.min(coeffs[0], y0*(1+MAX_VARIATION)), y0*(1-MAX_VARIATION));
		coeffs[1] = Math.max(Math.min(coeffs[1], A0*(1+MAX_VARIATION)), A0*(1-MAX_VARIATION));
		coeffs[2] = Math.max(Math.min(coeffs[2], T0*(1+MAX_VARIATION)), T0*(1-MAX_VARIATION));
		coeffs[3] = Math.max(Math.min(coeffs[3], P0*(1+MAX_VARIATION)), P0*(1-MAX_VARIATION));
		coeffs[4] = Math.max(Math.min(coeffs[4], A1*(1+MAX_VARIATION)), A1*(1-MAX_VARIATION));
		coeffs[5] = Math.max(Math.min(coeffs[5], T1*(1+MAX_VARIATION)), T1*(1-MAX_VARIATION));
		coeffs[6] = Math.max(Math.min(coeffs[6], P1*(1+MAX_VARIATION)), P1*(1-MAX_VARIATION));
		coeffs[7] = Math.max(Math.min(coeffs[7], A2*(1+MAX_VARIATION)), A2*(1-MAX_VARIATION));
		coeffs[8] = Math.max(Math.min(coeffs[8], T2*(1+MAX_VARIATION)), T2*(1-MAX_VARIATION));
		coeffs[9] = Math.max(Math.min(coeffs[9], P2*(1+MAX_VARIATION)), P2*(1-MAX_VARIATION));
	}
	
	public void moveGround() {
		if (step == NUM_STEPS) {
			reinitialize();
		}
		coeffs[0] += y0 * (variation[0] * step/NUM_STEPS);
		coeffs[1] += A0 * (variation[1] * step/NUM_STEPS);
		coeffs[2] += T0 * (variation[2] * step/NUM_STEPS);
		coeffs[3] += P0 * (variation[3] * step/NUM_STEPS);
		coeffs[4] += A1 * (variation[4] * step/NUM_STEPS);
		coeffs[5] += T1 * (variation[5] * step/NUM_STEPS);
		coeffs[6] += P1 * (variation[6] * step/NUM_STEPS);
		coeffs[7] += A2 * (variation[7] * step/NUM_STEPS);
		coeffs[8] += T2 * (variation[8] * step/NUM_STEPS);
		coeffs[9] += P2 * (variation[9] * step/NUM_STEPS);
		clip();
		updatePoints();
		step++;
	}
}
