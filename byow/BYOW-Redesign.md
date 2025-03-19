## World Generation

### Global Variables

int MAP_WIDTH - width of the map

int MAP_HEIGHT - height of the map

int MIN_SIZE - ?MINIMUM SIZE OF A ROOM?

Random random - random generator based on current time
in milliseconds

List<Room> rooms - arrayList containing all the
generated rooms

List<Hallway> hallways - arrayList containing all 
hallways generated between rooms

### Methods

#### Split(BSPNode node, int minSize) 

recursivley divides the canvas (which is represented
using the BSPNode) into areas to place rooms 

The goal of this function is to create a binary 
tree structure where each leaf node represents 
a smaller rectangular area. These leaf nodes
can later be used to place rooms and hallways.
The recursive splitting ensures that the areas are 
divided in a way that supports randomness and 
variety in the layout of the rooms and hallways.

- base condition: if (node.width < minSize || node.height < minSize) return;

this checks that the current node (which represents
the area) is smaller than the minimum size of 
a room, basically to return if splitting again would
result in a partition that is too small

- boolean splitHorizontally = random.nextBoolean();

this line determines whether to perform the partition
horizontally or vertically

- int max = (splitHorizontally ? node.width : node.height) - minSize;
  if (max <= minSize) return;

if (max <= minSize) return;

The variable max represents the maximum allowable
length along the chosen dimension (width if 
splitting horizontally, height if vertically)
minus the minSize. This ensures that the
split does not create a child node smaller than
minSize. If max is less than or equal to minSize, 
the function returns, as a valid split is not 
possible.

- int splitPoint = minSize + random.nextInt(max - minSize);

A splitPoint is chosen randomly between minSize 
and max. This determines where the split will 
occur within the node.

- if (splitHorizontally) {

  node.left = new BSPNode(node.x, node.y, splitPoint, node.height);
  node.right = new BSPNode(node.x + splitPoint, node.y, node.width - splitPoint, node.height);
  
 } else {

  node.left = new BSPNode(node.x, node.y, node.width, splitPoint);
  node.right = new BSPNode(node.x, node.y + splitPoint, node.width, node.height - splitPoint);
  }

Depending on whether spliting left or right, this
creates the children nodes for the split.

- split(node.left, minSize);
  split(node.right, minSize);

The split function is then recursively called on 
the newly created left and right child nodes,
continuing the partitioning process until all 
nodes are smaller than minSize.

#### generateRoomsAndHallways(BSP node)

this function generates the rooms and hallways using
the BSP tree, and so it is a recursive function. 
The generateRoomsAndHallways function is designed 
to create rooms within the leaf nodes of the BSP
tree structure. Each leaf node corresponds to a 
specific area of the map, and within each leaf
node, the function generates a randomly sized
and positioned room. This method ensures that the
rooms are distributed throughout the map
according to the partitioning defined by the 
BSP tree, leading to a more varied and
interesting layout.

- if (node == null) return;

This is just the base case. 

- if (node.isLeaf()) {

The function then checks if the current node is
a leaf node. A leaf node in a BSP tree represents
an area that has not been further split into 
smaller sub-areas.

 - int roomWidth = Math.max(2, random.nextInt(node.width - 1));
   int roomHeight = Math.max(2, random.nextInt(node.height - 1));

If the node is a leaf, the function generates the 
dimensions for a room within this leaf area.

roomWidth and roomHeight are determined by
generating random numbers that are at least 2
(to ensure the room isn't too small) and 
less than the width and height of the node's area,
respectively. The Math.max(2, ...) ensures 
that the room has a minimum size of 2x2.

- int roomX = node.x + random.nextInt(node.width - roomWidth);
  int roomY = node.y + random.nextInt(node.height - roomHeight);

The roomX and roomY values are calculated to
determine the room's position within the node's area.

These are determined by randomly selecting an 
x-coordinate and a y-coordinate within the bounds 
of the node's area, ensuring that the room fits 
entirely within the node's space. The calculations
node.width - roomWidth and node.height - roomHeight
ensure that the room doesn't extend beyond the
node's boundaries.

 - Room room = new Room(roomX, roomY, roomWidth, roomHeight);
   rooms.add(room);

Creates the new room object, then adds it to the list

- } else {
  generateRoomsAndHallways(node.left);
  generateRoomsAndHallways(node.right);
  }

If the current node is not a leaf node, the 
function recursively calls generateRoomsAndHallways
on the node's left and right children. This process
continues until all leaf nodes have
been processed, ensuring that rooms are generated 
for each leaf node in the BSP tree.

#### connectRooms()

The connectRooms function is responsible for
connecting the generated rooms in
the map with hallways ensuring that all
rooms are accessible and there are no 
isolated rooms.

- for (int i = 0; i < rooms.size() - 1; i++) {

iterates through all rooms up to the second-to-last room
because it connects each room to the next room 
in the list, and there's no need to 
process the last room 
since it doesn't have a next room to connect to.

- Room roomA = rooms.get(i);
  Room roomB = rooms.get(i + 1);

this selects two consecutive rooms

int startX = roomA.x + random.nextInt(roomA.width);
int startY = roomA.y + random.nextInt(roomA.height);

The startX coordinate is calculated by adding a 
random offset (up to roomA.width - 1) to roomA.x,
the left boundary of the room.
The startY coordinate is calculated similarly,
adding a random offset (up to roomA.height - 1) 
to roomA.y, the bottom boundary of the room.

 - int endX = roomB.x + random.nextInt(roomB.width);
   int endY = roomB.y + random.nextInt(roomB.height);

Similarly, an ending point (endX, endY) for 
the hallway is determined within roomB.

- createHallway(startX, startY, endX, endY);

create the hallway at the randomly generated position

#### createHallway() 

creates the hallways using a new Hallway object

- while (x != endX || y != endY) {
  if (x != endX) {
  x += (endX > x) ? 1 : -1;
  } else if (y != endY) {
  y += (endY > y) ? 1 : -1;
  }
  hallway.addTile(x, y);
  }

This builds the hallway till x, y is equal to endX, endY.
If x doesn't equal endX, it decreases or increases y based on if 
x is less than endX or not. same goes for y. 

### BSPNode 

node class for the binary space partitioning tree.
Binary space partitioning is a generic process of
recursively dividing a scene into two until the 
partitioning satisfies one or more requirements.
In our case, the requrement(s) is/ are ...

#### Variables

x (init constructor)

y (init constructor)

width (init constructor)

height (init constructor)

left (init null)

right (init null)

#### Methods

##### isLeaf() 

returns if a node is a leaf (left and right are null)


### Room class

generic room class. utilizes constructor with cartesian
coordinate position, and width, height.
?ROOMS ARE PLACED FROM THE BOTTOM, UP???

#### Variables

x - x position on canvas

y - y position on canvas

width - room width

height - room height

#### Methods

##### intersects (other)
returns boolean value based on if the other room will 
intersect with this room based on:

o.x + o.width <= x || o.x >= x + width
 ||
o.y + o.height <= y || other.y >= y + height

this works by checking if the x coordinate of the
other room will conflict with the x placement of this
room.

### Hallway Class

generic hallway class for connecting all rooms.

#### Variables

List <int[]> path -- arraylist containing the coordinates 
of all the tiles that make up the hallway.


### Avatar Class 
Avatar class is designed to be the basic design 
of our person character and "opps" in the game 

### Variables 
Health - basic life hearts each avatar will get

Attack - Damage that avatar can make

Generic movement - automated movement

### Person extends Avatar

 Person class meant to represent the player and their 
movements in the map; the player is able to move up (W),
down (S), rightwards(D), leftwards (A); Save the user's seed
to account for PERSISTANCE. Save the player file as the file that 
will be saved. 

#### Variables 
IDEAS:

Health - we give them an arbitrary amount of life hearts (10?)

Movement - based on person's specific movement pattern (WSAD)

#### Methods

Move: move using the keys










