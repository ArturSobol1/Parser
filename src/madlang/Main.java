package madlang;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("Usage: madlang.Main <file.madl>");
      System.exit(1);
    }

    String source = Files.readString(Path.of(args[0]));

    CharStream input = CharStreams.fromString(source);

    MadLangLexer lexer = new MadLangLexer(input);
    lexer.removeErrorListeners();
    lexer.addErrorListener(ParseErrorListener.INSTANCE);

    CommonTokenStream tokens = new CommonTokenStream(lexer);

    MadLangParser parser = new MadLangParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(ParseErrorListener.INSTANCE);

    MadLangParser.ProgramContext tree = parser.program();

    AstBuilder builder = new AstBuilder();
    List<Stmt> program = builder.build(tree);

    Interpreter interpreter = new Interpreter();
    interpreter.interpret(program);
  }
}