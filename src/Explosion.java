import java.awt.Color;
import java.awt.Graphics;

public class Explosion extends GameObj implements Comparable<Explosion> {
	
	private static final int NUM_TICKS = 5; // the number of ticks that the explosion will exist for
	private static final double MAX_FORCE = 20;
	private static final double BLAST_RADIUS_MULTIPLIER = 0.07; // conversion from power (~1000-3000) to blast radius
	private double BLAST_RADIUS;
	private double EXPLOSION_POWER;
	private int ticks;
	private String owner;

	public Explosion(double x, double y, double power, String owner) {
		super(0, 0, x-power*BLAST_RADIUS_MULTIPLIER/2, y+power*BLAST_RADIUS_MULTIPLIER/2, power*BLAST_RADIUS_MULTIPLIER, power*BLAST_RADIUS_MULTIPLIER);
		EXPLOSION_POWER = power;
		BLAST_RADIUS = power*BLAST_RADIUS_MULTIPLIER/2;
		ticks = 0;
		this.owner = owner;
	}
	
	public double getForce(GameObj that) {
		return Math.min(EXPLOSION_POWER / (dist(that) + that.getSize()/2 + getSize()/2) / that.getSize(), MAX_FORCE);
	}
	
	public double getAngle(GameObj that) { return angle(that); }
	public double getBlastRadius() { return BLAST_RADIUS; }
	public void addTick() { ticks++; }
	public boolean getDestroy() { return ticks >= NUM_TICKS; }
	public String getOwner() { return owner; }

	@Override
	public void draw(Graphics g) {
		int red = 200;
		int green = 100;
		int blue = 20;
		int alpha = (int) Math.min(EXPLOSION_POWER/400,200);
		g.setColor(new Color(red,green,blue,alpha));
		double frac1 = 1;
		g.fillOval((int) (getX() + getWidth()/2*(1-frac1)), 
				   (int) (GameCourt.COURT_HEIGHT - (getY() - getHeight()/2*(1-frac1))), 
				   (int) (getWidth()*frac1), (int) (getHeight()*frac1));
		g.setColor(new Color(red,green,blue,3*alpha));
		double frac2 = 0.7;
		g.fillOval((int) (getX() + getWidth()/2*(1-frac2)), 
				   (int) (GameCourt.COURT_HEIGHT - (getY() - getHeight()/2*(1-frac2))), 
				   (int) (getWidth()*frac2), (int) (getHeight()*frac2));
		g.setColor(new Color(red,green,blue,5*alpha));
		double frac3 = 0.4;
		g.fillOval((int) (getX() + getWidth()/2*(1-frac3)), 
				   (int) (GameCourt.COURT_HEIGHT - (getY() - getHeight()/2*(1-frac3))), 
				   (int) (getWidth()*frac3), (int) (getHeight()*frac3));
	}

	@Override
	public String status() { 
		return "Explosion at X: " + Math.round(getX()) + ", Y: " + Math.round(getY());
	}

	@Override
	public int compareTo(Explosion o) {
		return this.toString().compareTo(o.toString());
	}

}
