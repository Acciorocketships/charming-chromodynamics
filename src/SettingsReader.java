import java.awt.Color;
import java.awt.event.*;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SettingsReader {

	private static final String filename = "settings.txt";
	Reader file;
	TokenScanner scanner;
	
	private int numhumans;
	private int numbots;
	private int[] up;
	private int[] down;
	private int[] left;
	private int[] right;
	private int[] aimleft;
	private int[] aimright;
	private int[] shoot;
	private String[] name;
	private Color[] color;
	private String[] botname;
	private Color[] botcolor;
	
	private Map<String,Player> players = new TreeMap<String,Player>();
	private Set<Projectile> projectiles = new TreeSet<Projectile>();
	private Set<Explosion> explosions = new TreeSet<Explosion>();

	public SettingsReader(Map<String,Player> players, Set<Projectile> projectiles, Set<Explosion> explosions) {
		this.players = players;
		this.projectiles = projectiles;
		this.explosions = explosions;
		try {
			file = new FileReader(filename);
			scanner = new TokenScanner(file);
			scan();
			addHumans();
			addBots();
		} catch (Exception e) {
			System.out.println(e);
			// Defaults to one human player and one bot
			Player player = new HumanPlayer(Color.CYAN,(GameCourt.MAX_SIZE+GameCourt.MIN_SIZE)/2,
					 KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_UP,KeyEvent.VK_DOWN,
					 KeyEvent.VK_COMMA,KeyEvent.VK_PERIOD,KeyEvent.VK_SLASH,
					 projectiles,players,explosions,"Player");
			players.put("Player",player);
			Player bot = new BotPlayer(Color.RED,(GameCourt.MAX_SIZE+GameCourt.MIN_SIZE)/2,
					projectiles,players,explosions,"Bot");
			players.put("Bot", bot);
		}
	}
	
	private void scan() {
		readNumPlayers();
		up = new int[numhumans];
		down = new int[numhumans];
		left = new int[numhumans];
		right = new int[numhumans];
		aimleft = new int[numhumans];
		aimright = new int[numhumans];
		shoot = new int[numhumans];
		name = new String[numhumans];
		color = new Color[numhumans];
		botname = new String[numbots];
		botcolor = new Color[numbots];
		readSettings();
	}
	
	private void readNumPlayers() {
		String token = "";
		while (!(token.toLowerCase().contains("num") && 
				(token.toLowerCase().contains("human") || 
				(token.toLowerCase().contains("player"))))) {
			token = getNext();
		}
		numhumans = Integer.parseInt(getNext());
		token = "";
		while (!(token.toLowerCase().contains("num") && 
				(token.toLowerCase().contains("bot") || 
				(token.toLowerCase().contains("ai"))))) {
			token = getNext();
		}
		numbots = Integer.parseInt(getNext());
	}
	
	private void readSettings() {
		while (scanner.hasNext()) {
			String field = getNext();
			int playernum = getNum(field);
			String value = getNext();
			if (playernum >= 0 && field != null && value != null) {
				setValue(field.toLowerCase(),value,playernum);
			}
		}
	}
	
	private int getNum(String token) {
		int result;
		try {
			token = token.replaceAll("[*a-zA-Z]", "");
			token = token.replaceAll("\\s", "");
			result = Integer.parseInt(token);
		} catch (Exception e) {
			result = -1;
		}
		return result;
	}
	
	private void setValue(String field, String value, int playernum) {
		if (field.contains("up")) {
			if (playernum > numhumans) { return; }
			up[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("down")) {
			if (playernum > numhumans) { return; }
			down[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("left") && !field.contains("aim")) {
			if (playernum > numhumans) { return; }
			left[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("right") && !field.contains("aim")) {
			if (playernum > numhumans) { return; }
			right[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("aim") && field.contains("left")) {
			if (playernum > numhumans) { return; }
			aimleft[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("aim") && field.contains("right")) {
			if (playernum > numhumans) { return; }
			aimright[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("shoot")) {
			if (playernum > numhumans) { return; }
			shoot[playernum-1] = getKey(value.toLowerCase());
		}
		else if (field.contains("name")) {
			if (field.contains("bot") || field.contains("ai")) {
				if (playernum > numbots) { return; }
				botname[playernum-1] = value;
			}
			else {
				if (playernum > numhumans) { return; }
				name[playernum-1] = value;
			}
		}
		else if (field.contains("color")) {
			if (field.contains("bot") || field.contains("ai")) {
				if (playernum > numbots) { return; }
				botcolor[playernum-1] = getColor(value.toLowerCase());
			}
			else {
				if (playernum > numhumans) { return; }
				color[playernum-1] = getColor(value.toLowerCase());
			}
		}
		
	}
	
	private int getKey(String value) {
		if (value.contains("up")) { return KeyEvent.VK_UP; }
		else if (value.contains("down")) { return KeyEvent.VK_DOWN; }
		else if (value.contains("left")) { return KeyEvent.VK_LEFT; }
		else if (value.contains("right")) { return KeyEvent.VK_RIGHT; }
		else if (value.contains(".") || value.contains("period")) { return KeyEvent.VK_PERIOD; }
		else if (value.contains(",") || value.contains("comma")) { return KeyEvent.VK_COMMA; }
		else if (value.contains("/") || value.contains("slash")) { return KeyEvent.VK_SLASH; }
		else if (value.contains(";") || value.contains("semicolon")) { return KeyEvent.VK_SEMICOLON; }
		else if (value.contains("'") || value.contains("apostrophe")) { return KeyEvent.VK_QUOTE; }
		else if (value.contains("return") || value.contains("enter")) { return KeyEvent.VK_ENTER; }
		else if (value.contains("tab")) { return KeyEvent.VK_TAB; }
		else if (value.contains("shift")) { return KeyEvent.VK_SHIFT; }
		else if (value.contains("space")) { return KeyEvent.VK_SPACE; }
		else if (value.contains("ctrl") || value.contains("control")) { return KeyEvent.VK_CONTROL; }
		else if (value.contains("alt") || value.contains("opt")) { return KeyEvent.VK_ALT; }
		else if (value.contains("cmd") || value.contains("meta")) { return KeyEvent.VK_META; }
		else if (value.contains("f")) { return KeyEvent.VK_F; }
		else if (value.contains("a")) { return KeyEvent.VK_A; }
		else if (value.contains("b")) { return KeyEvent.VK_B; }
		else if (value.contains("c")) { return KeyEvent.VK_C; }
		else if (value.contains("d")) { return KeyEvent.VK_D; }
		else if (value.contains("e")) { return KeyEvent.VK_E; }
		else if (value.contains("f")) { return KeyEvent.VK_F; }
		else if (value.contains("g")) { return KeyEvent.VK_G; }
		else if (value.contains("h")) { return KeyEvent.VK_H; }
		else if (value.contains("i")) { return KeyEvent.VK_I; }
		else if (value.contains("j")) { return KeyEvent.VK_J; }
		else if (value.contains("k")) { return KeyEvent.VK_K; }
		else if (value.contains("l")) { return KeyEvent.VK_L; }
		else if (value.contains("m")) { return KeyEvent.VK_M; }
		else if (value.contains("n")) { return KeyEvent.VK_N; }
		else if (value.contains("o")) { return KeyEvent.VK_O; }
		else if (value.contains("p")) { return KeyEvent.VK_P; }
		else if (value.contains("q")) { return KeyEvent.VK_Q; }
		else if (value.contains("r")) { return KeyEvent.VK_R; }
		else if (value.contains("s")) { return KeyEvent.VK_S; }
		else if (value.contains("t")) { return KeyEvent.VK_T; }
		else if (value.contains("u")) { return KeyEvent.VK_U; }
		else if (value.contains("v")) { return KeyEvent.VK_V; }
		else if (value.contains("w")) { return KeyEvent.VK_W; }
		else if (value.contains("x")) { return KeyEvent.VK_X; }
		else if (value.contains("y")) { return KeyEvent.VK_Y; }
		else if (value.contains("z")) { return KeyEvent.VK_Z; }
		else { throw new RuntimeException("Not a valid key"); }
	}
	
	private Color getColor(String value) {
		if (value.contains("red")) { return Color.RED; }
		else if (value.contains("blue")) { return Color.BLUE; }
		else if (value.contains("green")) { return Color.GREEN; }
		else if (value.contains("grey")) { return Color.LIGHT_GRAY; }
		else if (value.contains("yellow")) { return Color.YELLOW; }
		else if (value.contains("orange")) { return Color.ORANGE; }
		else if (value.contains("pink")) { return Color.PINK; }
		else if (value.contains("cyan")) { return Color.CYAN; }
		else { throw new RuntimeException("Not a valid color"); }
	}
	
	private String getNext() {
		String output = "";
		String temp = "";
		while (!TokenScanner.isWord(temp)) {
			if (scanner.hasNext()) {
				temp = scanner.next();
			}
			else {
				return null;
			}
		}
		output += temp;
		temp = "";
		while (scanner.hasNext()) {
			temp = scanner.next();
			if (temp.contains("\n") || temp.contains(":")) {
				break;
			}
			else {
				output += temp;
			}
		}
		return output;
	}
	
	private void addHumans() {
		for (int i = 0; i < numhumans; i++) {
			Player player;
			player = new HumanPlayer(color[i],(GameCourt.MAX_SIZE+GameCourt.MIN_SIZE)/2,
									 left[i],right[i],up[i],down[i],aimleft[i],aimright[i],
									 shoot[i],projectiles,players,explosions,name[i]);
			players.put(name[i],player);
		}
	}
	
	private void addBots() {
		for (int i = 0; i < numbots; i++) {
			Player bot;
			bot = new BotPlayer(botcolor[i],(GameCourt.MAX_SIZE+GameCourt.MIN_SIZE)/2,
								projectiles,players,explosions,botname[i]);
			players.put(botname[i], bot);
		}
	}
	
}
