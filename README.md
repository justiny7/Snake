# Snake
A fun variation of the classic game of Snake!

![Sample snake gameplay](https://github.com/justiny7/Snake/blob/master/Snake/pictures/gameplay.gif)


## Game Mechanics
This version of Snake preserves all aspects of the original, such as eating apples to gain length, suicide crashes, and wall collisions. However, a
new twist is the addition of randomly generated barriers, a limited field of vision, as well as a new type of apple. The game runs  just like its original
counterpart except that you die when hit a barrier, which swaps positions every time you eat an apple. The barriers are obscured by a limited view
radius, but consuming a golden apple, which have a one in ten chance of appearing, dispels the darkness until the next non-golden apple is eaten.

## Implementation Specifics

### Block Class
![Block class](https://github.com/justiny7/Snake/blob/master/Snake/pictures/block%20class.PNG)\
The Block class is the building block of the game. Containing x and y instance variables representing the column and row index each block belongs in,
it also holds the path to each image sprite used to fill up the space. The four types of blocks, which can be set in the constructor, are an apple block,
snake block, floor block, and barrier block. It also contains a changeImage function that is useful in changing specific image files when needed, such
as using different types of block to build the snake or lightening and darkening the background.

### Moving the Apple
![Move apple](https://github.com/justiny7/Snake/blob/master/Snake/pictures/move%20apple.PNG)\
To move the apple, first, an ArrayList of possible locations for the apple is created (i.e., blocks where the snake isn't). Then, one one of the blocks
are randomly selected to be the new position. Finally, a random number is generated to determine whether the apple is golden or not, with a 10% probability.

### Resetting the Barriers
Resetting the barriers is a three-step process.\
\
![Generating barriers](https://github.com/justiny7/Snake/blob/master/Snake/pictures/generate%20barriers.PNG)\
First, a process similar to generating an apple is used to come up with possible locations for the barrier. There are three criteria for this: it can't
have a snake or apple block in it, and it has to be more than four blocks away from the snake's head, so it won't spawn right in front of the head and cause
the player to crash immediately. Then, it randomly selects ten indices for the barrier to spawn in. However, there is one problem: it's possible that the
barriers are generated in such a way that it's impossible to get to the apple (the apple is surrounded in all four adjacent squares by either a barrier block
or a wall.

![Can get apple](https://github.com/justiny7/Snake/blob/master/Snake/pictures/can%20get%20apple.PNG)\
To check for this, a canGetApple function is used to check the four adjacent squares to an apple and see if the fruit is reachable.

![Reset barries](https://github.com/justiny7/Snake/blob/master/Snake/pictures/reset%20barriers.PNG)\
Finally, the resetBarriers function uses a do-while loop to combine the two previous ones to generate a set of valid barriers.

### Snake Mechanics
There are four functions that control the properties of the snake.\
\
![Change snake head](https://github.com/justiny7/Snake/blob/master/Snake/pictures/set%20head.PNG)\
The first two are helper functions for determining what type of head the snake has. If you look at the gameplay closely, you'll notice that if the snake
is within a certain vicinity of the apple, then it'll stick out its tongue, a small easter egg that is also feature in Google's version of the Snake game.

![Move snake](https://github.com/justiny7/Snake/blob/master/Snake/pictures/move%20snake.PNG)\
The next function is for moving the snake. It first checks if the new block the snake will end up in a valid square by seeing if it's occupied by a wall
or a barrier. If it is, then it resets the game; otherwise, it adds a block at the head, sets the previous head to a body block using the setHead function,
and checks if it has eaten an apple. If it hasn't, then it removes the tail block, effectively moving the snake forward; otherwise, it updates the respective
booleans representing whether it has just eaten a buffed apple and whether the current apple is buffed or not.

![Reset snake](https://github.com/justiny7/Snake/blob/master/Snake/pictures/reset%20snake.PNG)\
Finally, the reset function, as its name suggests, resets the snake and all of the helper variables to their original positions and values.

### Distance Function
![Distance function](https://github.com/justiny7/Snake/blob/master/Snake/pictures/distance%20function.PNG)\
A helper function that proved to be useful on multiple occasions is the distance function, which uses the Pythagorean Theorem to calculate the approximate
distance between two blocks, rounded down to the nearest integer. It is used in determining the view radius, when the snake tongue will come out, and where
the barrier blocks can be generated in relation to the snake head.

### Changing Directions
![Change direction](https://github.com/justiny7/Snake/blob/master/Snake/pictures/change%20direction.PNG)\
Finally, the last step is processing direction changes. Since the game only updates every 100 milliseconds, it's possible that a player presses two buttons
in succession before the snake moves a block. If a primitive "set direction as soon as a key is pressed" is used, then it wouldn't work because the snake would
try to move two directions in one block's worth of time, running into itself. To remedy this, a deque data structure is used to store a queue of pending moves.
Each frame, the deque is checked to see if there are any moves the player entered, but haven't been implemented yet. If there is, then it changes the snake head's
direction and pops the front of the deque. Otherwise, it continues in the same direction that it was headed before. A deque data structure is used instead of a
regular queue in order to have access to the peekLast function, which determines if the next move being added is valid or not (has to be perpendicular to the
current last move). If the move being entered is valid, then it pushes it to the back of the deque to be processed once its turn comes.

## Conclusion
This game was really fun to code and it's probably one of my favorite projects that (my partner and) I have made in AP CSA this year. We hope you have fun with it! gg\
\
Disclaimer: The "lag" isn't actually lag, it's just the low framerate to make the game not impossible - if you want it to be smoother, you can increase it in the
code.
