# Reference: https://www.cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html

# Clear default targets for building .class from .java if any.
.SUFFIXES: .java .class

./%.class: ./src/%.java
	javac -encoding utf8 -cp ./ -d ./ $<

# Our .java files
JAVAS = Tile.java Hand.java Shuffler.java Action.java Player.java AI.java mainGUI.java comGUI.java Board.java

# .class files by replacing .java in $(JAVAS)
classes: $(JAVAS:.java=.class)

default: classes

clean:
	rm -f *.class

run: classes
	java Board
