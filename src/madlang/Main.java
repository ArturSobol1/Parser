package madlang;

import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: madlang.Main <file.madl>");
            System.exit(1);
        }

        String file = args[0];
        CharStream input = CharStreams.fromString(Files.readString(Path.of(file)));

        MadLangLexer lexer = new MadLangLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ParseErrorListener.INSTANCE);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        MadLangParser parser = new MadLangParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(ParseErrorListener.INSTANCE);

        parser.program();

        System.out.println("parse successful");
    }
}