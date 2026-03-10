ANTLR_VERSION = 4.13.2
ANTLR_JAR = tools/antlr-$(ANTLR_VERSION)-complete.jar
GRAMMAR = grammar/MadLang.g4
GEN_DIR = gen
OUT = out

SRC = $(shell find src -name "*.java")
GEN_SRC = $(shell find $(GEN_DIR) -name "*.java" 2>/dev/null)
FILE ?=

ifeq ($(OS),Windows_NT)
CP = $(OUT);$(ANTLR_JAR)
else
CP = $(OUT):$(ANTLR_JAR)
endif

.PHONY: all gen run clean

all: $(ANTLR_JAR) gen
	mkdir -p $(OUT)
	javac -cp "$(ANTLR_JAR)" -d $(OUT) $(SRC) $(GEN_SRC)

$(ANTLR_JAR):
	mkdir -p tools
	curl -L -o $(ANTLR_JAR) https://www.antlr.org/download/antlr-$(ANTLR_VERSION)-complete.jar

gen: $(ANTLR_JAR) $(GRAMMAR)
	mkdir -p $(GEN_DIR)
	java -jar $(ANTLR_JAR) -Dlanguage=Java -visitor -no-listener -package madlang -o $(GEN_DIR) $(GRAMMAR)

run: all
ifndef FILE
	@echo "usage: make run FILE=filename.madl"
	@exit 1
else
	java -cp "$(CP)" madlang.Main $(FILE)
endif

clean:
	rm -rf $(OUT) $(GEN_DIR)