# BYOW

## Objects
1. Rooms 
2. Hallways
### Sample class (object)
1. Rectangle
2. Hallways

#### variables
Rectangle
- Xbound- marks the right bottom corner of where the rectangle/room/hallway starts
- Ybound - marks the starting point of the height of the Rectangle/room/hallway
- Width- randomly generated room with a minimum of 6 tiles in width
- Height- randomly generated room with a minimum of 6 tiles in height 

#### Sample methods / algorithms
1. Rectangle.CreateRoom - creates a randomly placed room inside our world where 
- XBound(left side of our room), XBound+width-1 (right side), YBound (bottom), and YBound+height-1 (top side) mark the walls
- and everywhere else in that area is a floor

2. HallWays
   to place the hallway, we should specifically generate a vertical hallway and a horizontal hallway!!  -->  put the center points as  starting points and then we want to make it mandatory to connect each point, we can set it randomly for CONNECTING, but keep the condition such that when centerpoint(x,y) --> goalcenterpoint (z,t); if x and z are the same then insert a vertical hallway at that point!!!

#### Nested classes (nested objects)

### new class (object)
...


## Persistence