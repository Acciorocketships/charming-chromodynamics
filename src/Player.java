import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.Set;

public class Player extends GameObj {

	// constants
    public static final double MOVESPEED = 6; // left/right move velocity
    public static final double JUMPSPEED = 9; // jump velocity
    
    public static final double PROJ_PERC = 0.1; // ratio of total mass to fire
    
    private static final double MOVEFORCE_CONVERSION = 0.1; // Moveforce = MOVEFORCE_CONVERSION * MOVESPEED
    
    private static final double MAX_ANGLE = 3*Math.PI/2; // currently no constraint
    private static final double MIN_ANGLE = -Math.PI/2; // currently no constraint
    public static final double ANGLE_SPEED = Math.PI/30; // radians that the aim angle changes per tick
    public static final double POWER_SPEED = 0.6; // power increase per tick
    
    public static final int HIT_SCORE = 1; // Points gained by hitting someone
    public static final int FALL_OFF_SCORE = 3; // Points gained/lost by flying off the stage
    											// (sending someone off with an explosion but not
    											//  a direct hit counts as well)
    private String hitby;
    
    // controls
    private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	private boolean incpower;
	private boolean aimleft;
	private boolean aimright;
    
	// attributes
    private String name;
    private Color color;
    private int score;
    private double angle;
    private double power;
    private boolean canmove; // false if user is exploded
	private boolean jump; // true if the user jumps
	private boolean useddoublejump; // keeps track of if the double jump has been used
	private boolean shoot; // trigger. only true for one tick
    
	// 
    Set<Projectile> projectiles;
    Map<String,Player> players;
    Set<Explosion> explosions;

    public Player(Color color, int size, Set<Projectile> projectiles, Map<String,Player> players, Set<Explosion> explosions, String name) {
        super(0, 0, 0, 0, size, size);
        spawn();
    	this.name = name;
    	this.color = color;
    	this.angle = 0;
    	this.power = 0;
    	this.projectiles = projectiles;
    	this.players = players;
    	this.explosions = explosions;
    	this.canmove = true;
    }
    
    public double getAim() {
		if (aimright && !aimleft) {
			return (angle - ANGLE_SPEED);
		}
		else if (aimleft && !aimright) {
			return (angle + ANGLE_SPEED);
		}
		else {
			return angle;
		}
	}
    
    public void takeAction() {
    	jump = false;
    	if (canmove) {
    		// Usually the player can move normally
    			if (left && !right) {
    				setVx(-MOVESPEED);
    			}
    			else if (right && !left) {
    				setVx(MOVESPEED);
    			}
    			else {
    				setVx(0);
    			}
    		}
    		else {
    		// This is the case where the player gets blown up and flies away.
    		// Instead of being able to immediately change direction, they can
    		// oppose their direction of motion with a force.
    			if (left && !right) {
    				setXForce(-MOVESPEED*MOVEFORCE_CONVERSION);
    				if (getVx() < MOVESPEED) {
    					setClipV(MOVESPEED);
    				}
    			}
    			else if (right && !left) {
    				setXForce(MOVESPEED*MOVEFORCE_CONVERSION);
    				if (getVx() > -MOVESPEED) {
    					setClipV(MOVESPEED);
    				}
    			}
    		}
    	if (down) {
    		addForce(MOVESPEED*MOVEFORCE_CONVERSION,-Math.PI/2);
    	}
    	if (up) {
    		if (getGrounded()) {
				useddoublejump = false;
				jump = true;
				setVy(JUMPSPEED);
			}
			else {
				if (!useddoublejump) {
					useddoublejump = true;
					setVy(JUMPSPEED);
				}
			}
    		up = false;
    	}
    	if (shoot) {
    		if (getSize()*(1-PROJ_PERC) > GameCourt.MIN_SIZE) {
				double projSize = 2 * Math.sqrt((Math.PI*Math.pow(getSize()/2,2) - Math.PI*Math.pow(getSize()*(1-PROJ_PERC)/2,2)) / Math.PI);
				double projVx = power * Math.cos(getAngle()) + getVx();
				double projVy = power * Math.sin(getAngle()) + getVy();
				double projX = getX() + 2 * getWidth()/2 * Math.cos(getAngle());
				double projY = getY() + 2 * getHeight()/2 * Math.sin(getAngle());
				projectiles.add(new Projectile(projVx, projVy, projX, projY, projSize, projSize, players, explosions, getName()));
				setSize(getSize()*(1-PROJ_PERC));
    		}
			power = 0;
			shoot = false;
    	}
    	setAngle(getAim());
		if (incpower) { 
			power += POWER_SPEED;
		}
		if (jump) { 
			setGrounded(false); 
		}
    }
    
    public void spawn() {
    	setX((int) Math.round(GameCourt.COURT_WIDTH * Math.random()));
    	setY((int) Math.round(GameCourt.COURT_HEIGHT * Math.random()));
    	setCanMove(true);
    	hitby = null;
    	setSize((GameCourt.MIN_SIZE+GameCourt.MAX_SIZE)/2);
    	setVx(0);
    	setVy(0);
    }
    
    public void handleDeath() {
    	if (getX() > GameCourt.COURT_WIDTH || getX() < -getWidth() || 
    		getY() > GameCourt.COURT_HEIGHT+getHeight() || getY() < getHeight()/2) {
    		score -= FALL_OFF_SCORE;
    		if (hitby != null) {
    			Player killer = players.get(hitby);
    			killer.setScore(killer.getScore()+FALL_OFF_SCORE);
    		}
    		spawn();
    	}
    }
    
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
    public void setIncPower(boolean incpower) { this.incpower = incpower; }
    public void setAimLeft(boolean aimleft) { this.aimleft = aimleft; }
    public void setAimRight(boolean aimright) { this.aimright = aimright; }
    public void setJump(boolean jump) { this.jump = jump; }
    public void setShoot(boolean shoot) { this.shoot = shoot; }
    
    public void setHitBy(String player) { this.hitby = player; }
    
    public String getName() { return name; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public boolean getCanMove() { return canmove; }
    public void setCanMove(boolean canmove) { this.canmove = canmove; }
    
    public double getPower() { return power; }
    public void setPower(double power) { this.power = power; }
    
    public double getAngle() { return angle; }
    public void setAngle(double theta) {
    	while (theta > 3*Math.PI/2) { theta -= 2 * Math.PI; }
    	while (theta < -Math.PI/2) { theta += 2 * Math.PI; }
    	angle = theta;
    	if (angle > MAX_ANGLE) { angle = MAX_ANGLE; }
    	if (angle < MIN_ANGLE) { angle = MIN_ANGLE; }
    }
 
    
    public String status() {
    	String message = "";
    	message += "Score: " + String.valueOf(score);
    	message += ", Mass: " + String.valueOf(Math.round(getSize()));
    	//message += ", Angle: " + String.valueOf(Math.round(angle * 180 / Math.PI));
    	//message += ", Power: " + String.valueOf(Math.round(power));
    	//message += ", X: " + String.valueOf(Math.round(getX())) + ", Y: " + String.valueOf(Math.round(getY()));
    	//message += ", Vx: " + String.valueOf(Math.round(getVx())) + ", Vy: " + String.valueOf(Math.round(getVy()));
    	//if (getGrounded()) { message += ", On Ground"; } else { message += ", In Air"; }
    	return message;
    }

    @Override
    public void draw(Graphics g) {
    	int border = 4;
    	g.setColor(Color.BLACK);
        g.fillOval((int) Math.round(getX()), Math.round(GameCourt.COURT_HEIGHT - (int) getY()), 
        		   (int) Math.round(getWidth()), (int) Math.round(getHeight()));
        g.setColor(color);
        g.fillOval((int) Math.round(getX()+border/2), Math.round(GameCourt.COURT_HEIGHT - (int) getY()+border/2), 
        		   (int) Math.round(getWidth()-border), (int) Math.round(getHeight()-border));
        
        g.setColor(Color.BLACK);
        g.fillOval((int) Math.round(getX() + getWidth()/2 + 
        			(1.4+getPower()/6)*getWidth()/2*Math.cos(angle)),
        		   (int) Math.round(GameCourt.COURT_HEIGHT - ((int) getY()) +
        			getHeight()/2 - (1.4+getPower()/6)*getHeight()/2*Math.sin(angle)), 4, 4);
    }
}
