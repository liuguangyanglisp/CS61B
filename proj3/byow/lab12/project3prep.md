# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: At first, I didn't think about recursion(like in order tranversal) and had no idea.
Lessons I have learned from video:
1. Translate difficult and big problem into easy and small subtasks is an important skill and mindset. Draw hexagon in python first, and map it into java.
2. Do basic small part first, and abstraction on it to do bigger things. To draw hexagon tesselation, draw a hexagon first. To draw a hexagon, draw a row first.
3. Think in reduction give us more and easier ways to solve a problme. Although this is a java program, it can also solve in python, which means it reduce to python.
4. Find pattern can make life easier. A hexagon is recursive call to draw rows. A hexagon column is a recursive call to draw hexagon. A hexagon tesselation is recursive call to draw hexagoncolumn. 
-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: I'm not sure. Maybe rooms and hallways are hexagons. Tessellating hexagons is generating a world?

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: generate room or hallway first, and use abstraction to build bigger world.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: a  room is surrounded by hallway?
