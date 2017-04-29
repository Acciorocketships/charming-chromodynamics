import java.awt.Graphics;
import java.util.Map;
import java.util.Set;

public abstract class GameObj {

    private double x; 
    private double y;
    
    private double vx;
    private double vy;

    private double width;
    private double height;
    
    private boolean grounded;
    
    private double xforce;
    private double yforce;
    
    private double clipV; // clips the velocity. if -1, it doesn't do anything.

    public GameObj(double vx, double vy, double x, double y, double width, double height) {
        this.vx = vx;
        this.vy = vy;
        this.x = x;
        this.y = y;
        this.width  = width;
        this.height = height;
        this.grounded = false;
        this.xforce = 0;
        this.yforce = 0;
        this.clipV = -1;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public double getVx() {
        return vx;
    }
    
    public double getVy() {
        return vy;
    }
    
    public boolean getGrounded() {
    	return grounded;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setWidth(double w) {
    	width = w;
    }
    
    public void setHeight(double h) {
    	height = h;
    }
    
    public double getSize() { return (width + height)/2; }
    
    public void setSize(double size) {
    	if (size < GameCourt.MIN_SIZE) { size = GameCourt.MIN_SIZE; }
    	if (size > GameCourt.MAX_SIZE) { size = GameCourt.MAX_SIZE; }
    	width = size;
    	height = size;
    }

    public void setX(double x) { this.x = x; }

    public void setY(double y) { this.y = y; }

    public void setVx(double vx) { this.vx = vx; }

    public void setVy(double vy) { this.vy = vy; }
    
    public void setGrounded(boolean grounded) { this.grounded = grounded; }
    
    public void setClipV(double clipvelocity) { clipV = clipvelocity; }
    
    public void addForce(double f, double theta) {
    	xforce += f * Math.cos(theta);
    	yforce += f * Math.sin(theta);
    }
    
    public void setXForce(double xf) { xforce = xf; }
    public void setYForce(double yf) { yforce = yf; }
    public double getXForce() { return xforce; }
    public double getYForce() { return yforce; }
    
    public void surface() {
    	y = GameCourt.ground.func(x+width/2) + height;
    }

    public void move() {
        if (!grounded) {
        	x += vx;
            y += vy;
        	vy += GameCourt.GRAVITY;
        }
        else {
        	vy = 0;
        	x += vx;
        	surface();
        }
        vx += xforce;
    	vy += yforce;
    	if (clipV >= 0) {
    		vx = Math.max(Math.min(vx, clipV), -clipV);
    	}
    	xforce = 0;
    	yforce = 0;
    }
    
    public double dist(GameObj that) {
    	if (that == null) { return GameCourt.COURT_WIDTH + GameCourt.COURT_HEIGHT; }
    	double centerdist = Math.sqrt(Math.pow((x + width/2) - (that.getX() + that.getWidth()/2), 2) +
    								  Math.pow((y - height/2) - (that.getY() - that.getHeight()/2), 2));
    	return centerdist - that.getWidth()/2 - width/2;
    }
    
    public double angle(GameObj that) {
    	double angle = Math.atan(((that.getY() - that.getHeight()/2) - (y - height/2)) / ((that.getX() + that.getWidth()/2) - (x + width/2)));
    	if ((that.getX() + that.getWidth()/2) < (x + width/2)) {
    		angle += Math.PI;
    	}
    	return angle;
    }
    
    public Player closestPlayer(Map<String,Player> players) {
    	double mindist = GameCourt.COURT_WIDTH + GameCourt.COURT_HEIGHT;
    	Player closest = null;
		for (Player player : players.values()) {
			if (player != this) {
				double distance = dist(player);
				if (distance < mindist) {
					mindist = distance;
					closest = player;
				}
			}
		}
		return closest;
    }
    
    public Projectile closestProjectile(Set<Projectile> projectiles) {
    	double mindist = GameCourt.COURT_WIDTH + GameCourt.COURT_HEIGHT;
    	Projectile closest = null;
		for (Projectile projectile : projectiles) {
			double distance = dist(projectile);
			if (distance < mindist) {
				mindist = distance;
				closest = projectile;
			}
		}
		return closest;
    }
    
    public double bounceDirection() {
    	double gndangle = Math.atan(GameCourt.ground.dfunc(x));
    	double selfangle = Math.atan(vy/vx);
    	return 2*gndangle - selfangle;
    }
    
    public double bounceDirection(double selfangle) {
    	double gndangle = Math.atan(GameCourt.ground.dfunc(x));
    	return 2*gndangle - selfangle;
    }
    
    // By adding a bit of tolerance to touching the ground, we can make
    // movement on the ground a lot smoother.
    public boolean touchGroundApprox() {
    	return (y-height*0.8 <= GameCourt.ground.func(x+width*0.1) ||
    			y-height <= GameCourt.ground.func(x+width/2) ||
    			y-height*0.8 <= GameCourt.ground.func(x+width*0.9));
    }
    
    //
    public boolean touchGround() {
    	return y-height <= GameCourt.ground.func(x+width/2);
    }

    public abstract void draw(Graphics g);
    
    public abstract String status();
}