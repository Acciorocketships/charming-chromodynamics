=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: 12337670
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. I/O: Used for reading in settings.
   Specifically, it allows the user to change how many humans and bots they want playing,
   and adjust the names, colors, and controls for every player. This is a good use for I/O
   because the end user will be playing the game from a runnable jar file, not compiling the
   java code. That means that if you hardcode in how many players should be in the game,
   the end user will have no way of changing that. Reading in settings from a txt file solves
   that problem. In my proposal, it said to make sure that the file can change to allow for
   different settings. Not only is this true, but the settings file should also work if the order
   is changed. For example, instead of organizing the settings by player, you can organize them 
   by field. Furthermore, the field names are robust against changes. For example, if you change 
   "Player 1 Up Key" to "player1up" or even "up key 1", it will still work. If you say you want
   1 human player but you provide settings for 3, then it will only use the settings for player 1.

  2. Inheritance/Dynamic Dispatch: Used for GameObj and Player
  Because the human and bot player differ only in the way they get input, it makes sense that
  they would share most of their code. Human players get input from the keyboard, and bots get
  input from a decision-making algorithm. Once the player has this input, it will behave in the
  same way no matter if it is a bot or a human. That's why it makes sense to make humanplayer
  and botplayer subclasses of player, with the sole purpose to provide input. I considered making
  player an abstract class with the "input" method left blank, but it turned out not to be feasible
  because the corresponding method in humanplayer and botplayer takes different inputs.

  3. Collections: Used to store Players, Projectiles, and Explosions
  The number of players can change based on the settings file. The number of projectiles and
  explosions are always changing. I needed a way to store sets of these objects, and pass them
  into classes so they could be easily read and edited. An array wouldn't make sense because the
  size needed to be dynamic, and order didn't matter. I used a map for the players, just in case
  I wanted to look up a specific player by name. For the others, I didn't see any reason why I would
  have to look up specific elements, so I went with sets.

  4. Advanced Topic: AI
  
  	 Advanced Topic: Collisions
  In the GameObj class, when finding the distance from another gameobject, I use circular collisions.
  if "this" and "that" are circles and this.dist(that) = 0, then they are barely touching. A negative number
  means they are overlapping.
  I also use collisions when calculating the bounce of a projectile. Since the ground changes over time,
  I need to take the derivative of the ground function at a given moment in time, then use that angle and the
  incident object angle to calculate the bounce direction.
  
  	Advanced Topic: AI
  I really wanted to use reinforcement learning for the bot AI, but this project already took a really
  long time, so I decided to simply use a random number generator to simulate sporadic behaviour. The bot
  chooses between following someone and going to a random location, jumps more often when near another player,
  shoots more often when near another player, is more likely to shoot at the closest player, and attempts to
  dodge projectiles.
  To dodge projectiles, I needed to predict where it would land, then move away from that position. To shoot,
  I needed to predict where the target would be a given number of ticks in the future (it looks further in the
  future for targets that are further away, because it takes the projectile longer to get there). Once the bot
  figures out where to aim, it calculates a trajectory by using quadratic regression (this takes the gravity
  into account, so it will still work if the gravity constant changes). Usually, the bot shoots in a bit of an
  arc so as to clear the hills (the maximum height of the shot is adjustable in the code–the bot will calculate
  the trajectory accordingly). However, if the bot is in the air above the target, it just shoots directly at
  them.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

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

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
 I had a very hard time getting the explosions to work correctly. I had some issues with the
 players not budging because they were on the ground (when I wanted them to be flung into the air).
 I also figured out early on that the player controls need to change whenever they get hit by an explosion.
 The controls usually just set the velocity, but if you want to make it possible for a player to fly off the
 stage, you need to make the input control force instead (otherwise the player could just immediately arrest
 their velocity and avoid flying off). This meant I had to figure out when to change the controls, and when
 to change it back. Everywhere in my code where it says "canmove" is a place where I had to consider what state
 the player was currently in.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

I think there is very good separation of functionality. The gameobject class contains code in common
for all kinds of game elements (the players, the projectiles, and the explosions). All of those objects
need to have some basic functionality such as finding the distance to another gameobject and storing
position and size values.

Private states are all encapsulated with getter/setter functions, so that other classes can't screw
anything up.

The game is organized so that almost all of the computation for each object is contained within that
object. For example, instead of being contained in the tick() function in gamecourt, all object movement
is handled in the gameobj class. The extra logic behind player movement is contained within the player class.
The tick() function only contains the high-level flow of the game. It calls functions in other objects to
do all of the computation.

When I first started programming, I realized that ovals are plotted from their top left corner,
not from the center. At the time, I fixed this by doing x+width/2 and y-height/2 whenever I needed
to use the position of the center of the gameobject in code. However, now I wish I had just defined
x,y as x+width/2 and y-height/2, and plotted from x-width/2 and y+height/2. It would have made a lot of
the calculations more straightforward.


========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.

I used the Jama Library for Matrix math, used in trajectory calculation.
