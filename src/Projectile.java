import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Projectile extends GameObj implements Comparable<Projectile> {
	
	private static final double BOUNCINESS = 0.7;
	private static final int NUM_BOUNCES = 3;
	private static final double DAMAGE_MULTIPLIER = 3;
	private static final double EXPLOSIVE_MULTIPLIER = 200;
	
	
	private int bounces;
	private boolean explode;
	private String owner;
	
	private Map<String,Player> players = new TreeMap<String,Player>();
	private Set<Explosion> explosions = new TreeSet<Explosion>();
	
	public Projectile(double vx, double vy, double x, double y, 
					  double width, double height, Map<String,Player> players, 
					  Set<Explosion> explosions, String owner) {
		super(vx, vy, x, y, width, height);
		this.bounces = 0;
		this.explode = false;
		this.players = players;
		this.owner = owner;
		this.explosions = explosions;
	}
	
	public boolean getExplode() { return explode; }
	
	public void checkCollision() {
		Player closest = closestPlayer(players);
		if (dist(closest) <= 0) {
			explode = true;
			double newsize = 2 * Math.sqrt((Math.PI*Math.pow(closest.getSize()/2,2) - 
					DAMAGE_MULTIPLIER*Math.PI*Math.pow(getSize()/2,2)) / Math.PI);
			if (Double.isNaN(newsize)) { newsize = GameCourt.MIN_SIZE; }
			closest.setSize(newsize);
			explosions.add(new Explosion(getX()+getWidth()/2,getY()-getHeight()/2,
										 getSize()*EXPLOSIVE_MULTIPLIER, owner));
			Player shooter = players.get(owner);
			if (closest != shooter) {
				shooter.setScore(shooter.getScore()+Player.HIT_SCORE);
			}
		}
	}
	
	@Override
	public void move() {
        setX(getX() + getVx());
        setY(getY() + getVy());
        setVy(getVy() + GameCourt.GRAVITY);
        if (touchGround()) {
        	surface();
        	setY(getY() + 0.1);
        	if (bounces >= NUM_BOUNCES) {
        		explode = true;
        		explosions.add(new Explosion(getX()+getWidth()/2,getY()-getHeight()/2,
						 					 getSize()*EXPLOSIVE_MULTIPLIER, owner));
        	}
        	else {
	        	double angle = bounceDirection();
	        	setVx(getVx() * BOUNCINESS * Math.cos(angle));
	        	setVy(getVy() * BOUNCINESS * Math.sin(angle));
	        	bounces++;
        	}
        }
    }

	@Override
	public void draw(Graphics g) {
    	int border = 2;
    	g.setColor(Color.BLACK);
        g.fillOval((int) Math.round(getX()), Math.round(GameCourt.COURT_HEIGHT - (int) getY()), 
        		   (int) Math.round(getWidth()), (int) Math.round(getHeight()));
        g.setColor(Color.WHITE);
        g.fillOval((int) Math.round(getX()+border/2), Math.round(GameCourt.COURT_HEIGHT - (int) getY()+border/2), 
        		   (int) Math.round(getWidth()-border), (int) Math.round(getHeight()-border));
	}

	@Override
	public String status() {
		Player closest = closestPlayer(players);
		if (closest != null) {
			double distance = dist(closest);
			return players.get(owner).getName() + "'s Projectile:: " + 
				   String.valueOf(Math.round(distance)) + " from " + 
				   closest.getName();
		}
		else { 
			return null; 
		}
	}

	@Override
	public int compareTo(Projectile o) {
		return this.toString().compareTo(o.toString());
	}

}
