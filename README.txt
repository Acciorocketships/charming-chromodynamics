Classes:

Game: Uses swing to set up the GUI

GameCourt: Main game loop. Initializes the game and runs the logic for each game tick
 by calling functions in other classes.

SettingsReader: Reads in the settings for the game and initializes the players

TokenScanner: Creates an iterator that returns words, given a reader

GameObj: A superclass of any type of game object. Stores information about size and
position, and provides methods that are useful for any type of game object. This
includes: finding the angle to another object, finding the distance to another object,
detecting if it is on the ground, and updating its position.

Player: A superclass of both human players and AI players. The main method it contains
handles the logic of turning the player's input (however it is obtained) into an action
by updating the properties of GameObj.

HumanPlayer: Sets the Player input with keypresses.

BotPlayer: Sets the Player input with a decision-making algorithm.

Projectile: The class associated with the projectiles that the players shoot. Contains
logic for bouncing on the ground and detecting collisions. When it explodes, it creates an
explosion object. When it collides with a player, it updates the score.

Explosion: Creates a vector field based on the the projectile's blast radius and power
that will affect the forces on nearby players.

Ground: Uses a truncated Fourier series to represent the ground. Occasionally updates
the coefficients to make the ground move.



AI Overview:

  I really wanted to use reinforcement learning for the bot AI, but this project already took a really long time, so I decided to simply use a random number generator to simulate sporadic behaviour. The bot chooses between following someone and going to a random location, jumps more often when near another player, shoots more often when near another player, is more likely to shoot at the closest player, and attempts to dodge projectiles. To dodge projectiles, I needed to predict where it would land, then move away from that position. To shoot, I needed to predict where the target would be a given number of ticks in the future (it looks further in the future for targets that are further away, because it takes the projectile longer to get there). Once the bot figures out where to aim, it calculates a trajectory by using quadratic regression (this takes the gravity into account, so it will still work if the gravity constant changes). Usually, the bot shoots in a bit of an arc so as to clear the hills (the maximum height of the shot is adjustable in the code–the bot will calculate the trajectory accordingly). However, if the bot is in the air above the target, it just shoots directly at them.
