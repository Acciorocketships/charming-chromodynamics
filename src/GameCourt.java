import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.*;

@SuppressWarnings("serial")
public class GameCourt extends JPanel {

    private JLabel statuslabel;
    private JLabel transientstatuslabel;

    public static final int COURT_WIDTH = 1200;
    public static final int COURT_HEIGHT = 675;

    public static final int INTERVAL = 35;
    
    public static final double GRAVITY = -0.4;
    
    public static final int MIN_SIZE = 10;
    public static final int MAX_SIZE = 50;
    
    public static final int WIN_SCORE = 20;
    
    private String winner;
    
    private Map<String,Player> players = new TreeMap<String,Player>();
    private Set<Projectile> projectiles = new TreeSet<Projectile>();
    private Set<Explosion> explosions = new TreeSet<Explosion>();
    public static final Ground ground = new Ground();

    public GameCourt(JLabel status, JLabel transientstatus) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start();
        setFocusable(true);
        winner = null;
        this.transientstatuslabel = transientstatus;
        this.statuslabel = status;
        
        @SuppressWarnings("unused")
		SettingsReader settings = new SettingsReader(players,projectiles,explosions);
        
		addKeyListener(new KeyAdapter() {
			  public void keyPressed(KeyEvent e) {
				  for (Player player : players.values()) {
					  if (player instanceof HumanPlayer) {
						  ((HumanPlayer) player).input(e,true);
					  }
		          }
			  }
			  public void keyReleased(KeyEvent e) {
				  for (Player player : players.values()) {
					  if (player instanceof HumanPlayer) {
						  ((HumanPlayer) player).input(e,false);
					  }
		          }
			  }
			});
		
		addKeyListener(new KeyAdapter() {
			  public void keyPressed(KeyEvent e) {
				  if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					  reset();
				  }
			  }
			});
    }

    
    public void reset() {
    	winner = null;
    	for (Player player : players.values()) {
    		player.setScore(0);
    		player.spawn();
    	}
    	projectiles.clear();
    	explosions.clear();
    }
    
    
    private String generateStatus() {
    	String status = "";
    	for (Map.Entry<String, Player> entry : players.entrySet()) {
    		String playerstatus = entry.getValue().status();
    		if (playerstatus != null && playerstatus != "") {
    			status += "| " + entry.getKey() + ":: ";
    			status += playerstatus + " |";
    		}
    	}
    	return status;
    }
    
    
    private String generateTransientStatus() {
    	String status = "";
    	if (winner != null) {
    		status += "| Winner: " + winner + " |";
    	}
    	for (Projectile projectile : projectiles) {
    		String projectilestatus = projectile.status();
    		if (projectilestatus != null && projectilestatus != "") {
    			status += "| " + projectilestatus + " |";
    		}
    	}
    	return status;
    }

    
    void tick() {
    	for (Player player : players.values()) {
    		if (winner == null && player.getScore() >= WIN_SCORE) {
    			winner = player.getName();
    		}
    		player.handleDeath();
    		player.setGrounded(player.touchGroundApprox());
    		if (!player.getCanMove() && player.touchGround()) {
    		// return control to the player when they touch the ground after
    		// an explosion
    			player.setCanMove(true);
    			player.setHitBy(null);
    			player.setVx(0);
    			player.setXForce(0);
    			player.setYForce(0);
    		}
    		if (player instanceof BotPlayer) {
				((BotPlayer) player).computeAction();
			}
    		player.takeAction();
    		player.setSize(player.getSize()+0.03);
    		player.move();
        }
    	Set<Projectile> removeproj = new TreeSet<Projectile>();
    	for (Projectile projectile : projectiles) {
    		projectile.move();
    		projectile.checkCollision();
    		if (projectile.getExplode()) {
    			removeproj.add(projectile);
    		}
    	}
    	projectiles.removeAll(removeproj);
    	Set<Explosion> removeexp = new TreeSet<Explosion>();
    	for (Explosion explosion : explosions) {
    		if (explosion.getDestroy()) {
    			removeexp.add(explosion);
    		}
    		else {
        		for (Player player : players.values()) {
        			if (player.dist(explosion) < explosion.getBlastRadius()) {
        				player.setHitBy(explosion.getOwner());
        				player.setCanMove(false);
        				player.addForce(explosion.getForce(player),explosion.getAngle(player));
        				if (player.getGrounded()) {
        					// Launches them in the air a bit if they are on the ground
        					player.setY(player.getY()-20/(player.dist(explosion)+1));
        					player.setYForce(player.getYForce()+8);
        				}
        			}
        		}
    		}
    		explosion.addTick();
    	}
    	explosions.removeAll(removeexp);
    	ground.moveGround();
    	statuslabel.setText(generateStatus());
    	transientstatuslabel.setText(generateTransientStatus());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(30,180,180));
        g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
        for (Explosion explosion : explosions) {
        	explosion.draw(g);
        }
        ground.draw(g);
        for (Projectile projectile : projectiles) {
        	projectile.draw(g);
        }
        for (Player player : players.values()) {
        	player.draw(g);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}