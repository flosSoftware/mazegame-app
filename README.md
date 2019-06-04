# mazegame-app

This is a maze game emulator with custom levels (just edit *app/src/main/res/raw/livXXX.txt*) developed for Android systems. <br />
The purpose of the game is trying to teach Java OOP and GUI programming to high school students (so ... sorry for italian comments in source code!).<br />
If you'd like to add new levels, change *MAX_LEVEL* constant in *MainActivity.java*.

## Some screenshots of the game in action!
<img src="https://albertof.com/img/mazegame/0.png" alt="pic" width="300"/>
<img src="https://albertof.com/img/mazegame/1.png" alt="pic" width="300"/>
<img src="https://albertof.com/img/mazegame/2.png" alt="pic" width="300"/>
<img src="https://albertof.com/img/mazegame/3.png" alt="pic" width="300"/>

## Level file structure
*`<size of elements matrix>` `<size of element in pixels> <number of lives>`*<br />
*`<elements matrix>`*<br />
*`<index of door unlocked by key 0>`*<br />
*`<index of door unlocked by key 1>`*<br />
*...*<br />
*`<index of door unlocked by key n>`*

Legend of symbols present in element matrix:
- *^ Monster (vertically moving)*
- *v Monster (vertically moving)*
- *> Monster (horizontally moving)*
- *< Monster (horizontally moving)*
- *- Brickwall (Horizontal)*
- *| Brickwall (Vertical)*
- *@ Door Key*
- *? Locked Door (Vertical)*
- */ Unlocked Door (Vertical)*
- *_ Locked Door (Horizontal)*
- *\ Unlocked Door (Horizontal)*
- *o Hero Start Position*
- *x Final Goal*

Note that keys are read in row-based fashion.
