# Charming Chromodynamics

![Gameplay](https://github.com/Acciorocketships/Charming-Chromodynamics/blob/master/img.png)

## About

Charming Chromodynamics is a Java game made entirely with the shape drawing functions within Java. It is similar to the old 2d tanks game, except it is real-time, the ground moves, and it is based on mass instead of health. Each player or AI controls a circle, which can jump and move along a dynamic rolling terrain. The goal of the game is to knock enemies off of the stage. This can be achieved by ejecting a small amount of mass towards the other players, which explodes on impact, sending them flying. However, be careful--the more mass you eject, the smaller you become, and the easier it is to be knocked of the stage. Over time, each player slowly recovers mass. The scoring of the game is as follows: 1) if a player lands a direct hit on another player, they get 1 point. 2) if a player knocks another player off the stage (not necessarily from a direct hit), they get 3 points (and the other player respawns), 3) if a player is knocked off the stage, they lose 3 points.

## Game Structure

> Game: Uses swing to set up the GUI
>
> GameCourt: Main game loop. Initializes the game and runs the logic for each game tick by calling functions in other classes.
>
> SettingsReader: Reads in the settings for the game and initializes the players
> 
> TokenScanner: Creates an iterator that returns words, given a reader
> 
> GameObj: A superclass of any type of game object. Stores information about size and position, and provides methods that are useful for any type of game object. This includes: finding the angle to > another object, finding the distance to another object, detecting if it is on the ground, and updating its position.
>
> Player: A superclass of both human players and AI players. The main method it contains handles the logic of turning the player's input (however it is obtained) into an action by updating the properties of GameObj.
>
> HumanPlayer: Sets the Player input with keypresses.
>
> BotPlayer: Sets the Player input with a decision-making algorithm.
>
> Projectile: The class associated with the projectiles that the players shoot. Contains logic for bouncing on the ground and detecting collisions. When it explodes, it creates an explosion object. When it collides with a player, it updates the score.
>
>Explosion: Creates a vector field based on the the projectile's blast radius and power that will affect the forces on nearby players.
>
> Ground: Uses a truncated Fourier series to represent the ground. Occasionally updates the coefficients to make the ground move.


## AI Overview

The bot chooses between following someone and going to a random location, jumps more often when near another player, shoots more often when near another player, is more likely to shoot at the closest player, and attempts to dodge projectiles. To dodge projectiles, I needed to predict where it would land, then move away from that position. To shoot, I needed to predict where the target would be a given number of ticks in the future (it looks further in the future for targets that are further away, because it takes the projectile longer to get there). Once the bot figures out where to aim, it calculates a trajectory by using quadratic regression (this takes the gravity into account, so it will still work if the gravity constant changes). Usually, the bot shoots in a bit of an arc so as to clear the hills (the maximum height of the shot is adjustable in the code–the bot will calculate the trajectory accordingly). However, if the bot is in the air above the target, it just shoots directly at them.
