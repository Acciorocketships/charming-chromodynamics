import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import Jama.Matrix;

public class BotPlayer extends Player {
	
	Map<String,Player> players = new TreeMap<String,Player>();
	Set<Projectile> projectiles = new TreeSet<Projectile>();
	
	private int shootcounter;
	private int tickcounter;
	private boolean following;
	private double roamX;
	
	private int EVAL_FREQ = 5; // number of ticks to elapse before re-evaluating
							   // adjusts how fast the bot "changes its mind"
	private double SHOOT_FREQ = 0.25;
	
	private double ARC_HEIGHT = 0.3;
	
	public BotPlayer(Color color, int size, Set<Projectile> projectiles, Map<String,Player> players, Set<Explosion> explosions, String name) {
		super(color,size,projectiles,players,explosions,name);
		this.players = players;
		this.projectiles = projectiles;
		shootcounter = 0;
		tickcounter = 0;
		following = true;
		roamX = Math.random() * GameCourt.COURT_WIDTH;
	}
	
	
    public void computeAction() {
    	// In the future I might add a different bot, so I can just replace this with
    	// the other method. 
    	simpleBot();
    }
    
    
    private void simpleBot() {
    	double targetdist = 300; // how far away to stay from player that it's following
    	double closedist = 10 + getWidth(); // close enough distance
    	shootcounter++;
    	tickcounter++;
    	Player closest = closestPlayer(players);
    	
    	// General Movement
    	if (following) {
	    	if (closest != null) {
	    		if (closest.getX() > getX() + targetdist) { right(); }
	    		else if (closest.getX() < getX() - targetdist) { left(); }
	    		else { stop(); }
	    	}
	    	if (tickcounter % EVAL_FREQ == 0 && Math.random() < 0.1) {
	    		// % chance to switch to roaming randomly every EVAL_FREQ ticks
	    		following = false;
	    	}
    	}
    	else {
    		if (tickcounter % EVAL_FREQ == 0 && Math.random() < 0.2) {
    			// % chance to choose a new location to go to every EVAL_FREQ ticks
    			roamX = Math.random() * GameCourt.COURT_WIDTH;
    		}
    		if (tickcounter % EVAL_FREQ == 0 && Math.random() < 0.1) {
    			// % chance to switch back to following every EVAL_FREQ ticks
    			following = true;
    		}
    		if (roamX > getX() + closedist) { right(); }
    		else if (roamX < getX() - closedist) { left(); }
    		else { stop(); }
    	}
    	
    	// Shooting
    	if (tickProbability((int) (dist(closest) / SHOOT_FREQ) + (int) (250 / SHOOT_FREQ), shootcounter)) {
    		// the bot will shoot more when it is very close to someone
    		shootcounter = 0;
    		Player target = null;
    		if (Math.random() < 0.8) {
    			// % chance that it will shoot at the nearest person
    			target = closest;
    		}
    		else {
    			// % chance it will shoot at a random person
    			target = randomPlayer(players.values());
    		}
    		if (target != null) {
    			try {
    				shoot(target);
    			} catch (Exception e) {}
    		}
    	}
    	
    	// Jumping
    	if (dist(closest) < 4*closedist) {
    		if (tickcounter % EVAL_FREQ == 0 && Math.random() < 0.3) {
    			// % chance to jump every EVAL_FREQ ticks when close to another player
    			setUp(true);
    		}
    	}
    	if (tickcounter % EVAL_FREQ == 0 && Math.random() < 0.08) {
			// % chance to jump every EVAL_FREQ ticks regardless
			setUp(true);
		}
    	// Avoiding Edges, Explosions, and Canyons
    	
    	// I removed this because the bots were too good
    	
    	// Avoiding Projectiles
    	Projectile closestProj = closestProjectile(projectiles);
    	if (closestProj != null) {
	    	double[] explodePos = predictCollision(closestProj);
	    	if (dist(closestProj) < 2*closedist && !getGrounded()) {
	    		// if in the air, move away from the projectile itself
	    		if (closestProj.getX() + closestProj.getWidth()/2 > getX() + getWidth()/2) {
	    			left();
	    		}
	    		else {
	    			right();
	    		}
	    	}
	    	else if ((Math.abs(getX() + getWidth()/2 - explodePos[0]) < closedist) &&
	    			  getGrounded()) {
	    		// if on the ground, move away from where the projectile will land
	    		if (explodePos[0] > getX() + getWidth()/2) {
	    			left();
	    		}
	    		else {
	    			right();
	    		}
	    	}
    	}
    }
    
    
    private Player randomPlayer(Collection<Player> choices) {
    	int size = choices.size();
    	int index = (int) Math.round(Math.random() * (size-1));
    	return choices.toArray(new Player[size])[index];
    }
    
    // returns an boolean with an increasing probability over a given number of ticks
    // (used to decide when to take an action, so it is not completely predictable)
    private boolean tickProbability(int maxticks, int counter) {
    	if (maxticks <= 10) { maxticks = 10; }
    	return Math.random() < ((counter % maxticks) / (double) maxticks);
    }
    
    
    private void right() {
    	setRight(true);
    	setLeft(false);
    }
    
    private void left() {
    	setLeft(true);
    	setRight(false);
    }
    
    private void stop() {
    	setLeft(false);
    	setRight(false);
    }
    
    private void shoot(GameObj that) {
    	double archeight = dist(that) * ARC_HEIGHT;
    	if (!getGrounded() && (getY() > that.getY())) { archeight = -archeight; }
    	else if (!getGrounded()) { archeight = 0; }
    	double[] pos = predictPos(that, 25 + (int) ((that.getX()-getX()) * 0.05));
    	double[] vel = computeTrajectory(getX()+getWidth()/2,getY(),(getX()+that.getX())/2,(getY()+that.getY())/2+archeight,pos[0],pos[1]);
		double angle = Math.atan(vel[1]/vel[0]);
		vel[0] -= getVx();
		vel[1] -= getVy();
		if ((that.getX() + that.getWidth()/2) < (getX() + getWidth()/2)) {
    		angle += Math.PI;
    	}
		if (Math.abs((that.getX() + that.getWidth()/2) - (getX() + getWidth()/2)) < 80) {
			// if less than 60 away in x direction, just shoot directly at them
			angle = angle(that);
		}
		if (!that.getGrounded()) {
			// if the target is in the air, shoot directly at them
			angle = angle(that);
		}
		double power = Math.sqrt(Math.pow(vel[0],2) + Math.pow(vel[1],2)) * 0.68;
		setPower(power);
		setAngle(angle);
		setShoot(true);
    }
    
    
    // Predicts the position of a GameObj at a given number of ticks in the future,
    // assuming it continues its current course
    private double[] predictPos(GameObj that, int ticks) {
    	double[] position = new double[2];
    	double x = that.getX() + that.getWidth()/2;
    	double y = that.getY() - that.getHeight();
    	double vx = that.getVx();
    	double vy = that.getVy();
    	int counter = 0;
    	if (!that.getGrounded()) {
    		while (y > GameCourt.ground.func(x) && counter < ticks) {
    			counter++;
    			x += vx;
    			vy += GameCourt.GRAVITY;
    			y += vy;
    		}
    		if (y < GameCourt.ground.func(x)) {
    			y = GameCourt.ground.func(x);
    		}
    		if (counter >= ticks) {
    			position[0] = x;
    			position[1] = y + that.getHeight()/2;
    			return position;
    		}
    	}
    	x += vx * (ticks - counter);
    	if (x > GameCourt.COURT_WIDTH) { x = GameCourt.COURT_WIDTH - 2*that.getWidth(); }
    	if (x < 0) { x = 2*that.getWidth(); }
    	y = GameCourt.ground.func(x);
    	position[0] = x;
		position[1] = y + that.getHeight()/2;
		return position;
    }
    
    // Predicts where a GameObj will collide with the ground
    private double[] predictCollision(GameObj that) {
    	double[] position = new double[2];
    	double x = that.getX() + that.getWidth()/2;
    	double y = that.getY() - that.getHeight();
    	double vx = that.getVx();
    	double vy = that.getVy();
    	while (y > GameCourt.ground.func(x)) {
			x += vx;
			vy += GameCourt.GRAVITY;
			y += vy;
		}
		if (y < GameCourt.ground.func(x)) {
			y = GameCourt.ground.func(x);
		}
		position[0] = x;
		position[1] = y;
		return position;
    }
    
    private double[] computeTrajectory(double x0, double y0, double x1, double y1, double x2, double y2) {
    	Matrix X = new Matrix(new double[][] {{Math.pow(x0, 2), x0, 1},
    										  {Math.pow(x1, 2), x1, 1},
    										  {Math.pow(x2, 2), x2, 1}});
    	Matrix Y = new Matrix(new double[][] {{y0},{y1},{y2}});
    	Matrix C = X.solve(Y); // Does C = inv(X' * X) * (X' * Y)
    	double[] vel = new double[2];
    	vel[0] = Math.sqrt(GameCourt.GRAVITY/C.get(0,0));
    	if (x2 < x0) { vel[0] = -vel[0]; }
    	vel[1] = 2*C.get(0,0)*vel[0]*x0 + C.get(1,0)*vel[0];
    	return vel;
    }
    
    
    
}
