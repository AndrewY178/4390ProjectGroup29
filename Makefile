# TENTATIVE MAKEFILE
# to run server: enter "make server"
# to run client: enter "make client"

JAVA = java # java runtime command
JAVAC = javac # java compiler

# source files
SOURCES = Client.java ClientHandler.java MathEvaluator.java MathServer.java ServerLogger.java
# converts java to class 
CLASSES = Client.class ClientHandler.class MathEvaluator.class MathServer.class ServerLogger.class

# makes all default target
all: $(CLASSES)

# match class to java files !! may have to use newer version !!
.java.class:
	$(JAVAC) $*.java

# runs server 
server: all
  $(JAVA) MathServer

# runs client 
client: all
	$(JAVA) Client

# remvoes class files
clean:
	rm -f *.class
