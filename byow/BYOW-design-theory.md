# Build your own world design documentation

## Phase 1: World Generation

### Requirments:
#### Validity:

-The world must be a 2D grid, drawn using our tile engine. The tile engine is described in lab 16.

-The generated world must include distinct rooms and hallways, though it may also include outdoor spaces.

-At least some rooms should be rectangular, though you may support other shapes as well. Non-rectangular and overlapping rooms are ok, as long as the rest of the requirements are still met.

-Your world generator must be capable of generating hallways that include turns (or equivalently, straight hallways that intersect). Random worlds should generate a turning hallway with at least moderate frequency (20% of worlds or more).

-Dead-end hallways are not allowed.

-Rooms and hallways must have walls that are visually distinct from floors. Walls and floors should be visually distinct from unused spaces.

-Corner walls are optional.

-Rooms and hallways should be connected, i.e. there should not be gaps in the floor between adjacent rooms or hallways.

-All rooms should be reachable, i.e. there should be no rooms with no way to enter.

-Rooms cannot clip off the edge of the world. In other words, there should be no floor tiles on the edge of the world.

-The world must not have excess unused space. While this criterion is inherently subjective, aim to populate more than 50% of the world with rooms and hallways.
 
#### Randomness:


-The world must be pseudo-randomly generated. Pseudo-randomness is discussed in lab 16.

-The world should contain a random number of rooms and hallways.

-The locations of the rooms and hallways should be random.

-The width and height of rooms should be random.

-Hallways should have a width of 1 or 2 tiles and a random length.

-Hallways must originate from a random position in a room. They should not predicatably come from a particular point, like a corner or the center of a room.

-The world should be substantially different each time, i.e. you should NOT have the same basic layout with easily predictable features.

### Design

create 2 key objects, rectangles and hallways 

we need a collision system

#### Rectangles

The idea is that this will act as the room base that will be generated. In terms of world generation, it is the first
object that we can use to tie things together. For the sake of variation, we should allow and add logic to handle 
overlapping placements, such that it creates wider rooms that are rectangular, but create variation in the result.

We should aim to have these rooms have a random size, with a clear maximum and minimum width and height.

In terms of the code, a simple approach could be using a 2-dimensional for loop set to random values to create the
objects. The challenge in this object exists in ensuring that there always exists a path from all rooms to all other
rooms, and creating it so that it is able to recognize and properly construct overlapping rooms. This may involve 
doing something like not allowing wall tiles to be fully surrounded by tiles floor tiles (assuming an intended world
 like that found in the spec, with nothing tiles acting as a void surrounding the world). 

#### Hallways

These will exist to connect rooms. They should be able to be placed either north to south, or east to west. 
This might present a challenge, but we could create diagonal hallways. 

We would want random length. The width must either be 1 or 2 per the spec. 

We would need to a collision system to determine and properly handle hallway collisions. On a higher level, the world
could utilize a collision detection system, which could throw our own error at the event of a collision, which then 
handles the collision by either attempting to downsize the object creating the collision, or could reattempt a random
placement. 

#### World generation Algorithm

We need a means to handle world generation.