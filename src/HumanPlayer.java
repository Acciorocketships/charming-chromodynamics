import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Set;

public class HumanPlayer extends Player {
	
	private int KEY_LEFT;
	private int KEY_RIGHT;
	private int KEY_UP;
	private int KEY_DOWN;
	private int KEY_AIMLEFT;
	private int KEY_AIMRIGHT;
	private int KEY_FIRE;
	
	public HumanPlayer(Color color, int baseSize, int KEY_LEFT, int KEY_RIGHT,
					   int KEY_UP, int KEY_DOWN, int KEY_AIMLEFT, int KEY_AIMRIGHT, 
					   int KEY_FIRE, Set<Projectile> projectiles, 
					   Map<String,Player> players, Set<Explosion> explosions, String name) {
		super(color,baseSize,projectiles,players,explosions,name);
		this.KEY_LEFT = KEY_LEFT;
		this.KEY_RIGHT = KEY_RIGHT;
		this.KEY_UP = KEY_UP;
		this.KEY_DOWN = KEY_DOWN;
		this.KEY_AIMLEFT = KEY_AIMLEFT;
		this.KEY_AIMRIGHT = KEY_AIMRIGHT;
		this.KEY_FIRE = KEY_FIRE;
	}
	
    public void input(KeyEvent e, boolean keydown) {
		if (e.getKeyCode() == KEY_LEFT) {
			if (keydown) { setLeft(true); }
			else { setLeft(false); }
		} 
		else if (e.getKeyCode() == KEY_RIGHT) {
			if (keydown) { setRight(true); }
			else { setRight(false); }
		} 
		else if (e.getKeyCode() == KEY_UP) {
			if (keydown) { setUp(true); }
			else { setUp(false); }
		} 
		else if (e.getKeyCode() == KEY_DOWN) {
			if (keydown) { setDown(true); }
			else { setDown(false); }
		}
		else if (e.getKeyCode() == KEY_AIMRIGHT) {
			if (keydown) { setAimRight(true); }
			else { setAimRight(false); }
		} 
		else if (e.getKeyCode() == KEY_AIMLEFT) {
			if (keydown) { setAimLeft(true); }
			else { setAimLeft(false); }
		}
		else if (e.getKeyCode() == KEY_FIRE) {
			if (keydown) { setIncPower(true); }
			else { setIncPower(false); setShoot(true); }
		}		
    }
    
}
