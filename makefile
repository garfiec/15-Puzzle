default: build run
build:
	javac -d bin/ src/game/*.java

run:
	java -cp bin/ game/Game

clean:
	rm -r bin/*
