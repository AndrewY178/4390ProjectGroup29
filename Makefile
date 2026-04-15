# Variables
JAVAC = javac
JAVA = java
# Find all .java files in the directory automatically
SOURCES = $(wildcard *.java)
# Substitute .java extension with .class for the target list
CLASSES = $(SOURCES:.java=.class)

# Default target
all: $(CLASSES)

# Rule to compile .java to .class
# The tab at the start of the next line is REQUIRED
%.class: %.java
	$(JAVAC) $<

# Target to run the server
server: all
	$(JAVA) MathServer

# Target to run the client
client: all
	$(JAVA) Client

# Clean up compiled files
clean:
	rm -f *.class

# Prevents make from getting confused if a file named 'clean' or 'all' exists
.PHONY: all server client clean