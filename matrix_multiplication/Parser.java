import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

public class Parser {
    public static class Error extends Exception {
        public Error(String message) {
            super(message);
        }
    }

    public static Izraz parse(String s) throws IOException, Error {
        StreamTokenizer lexer = new StreamTokenizer(new BufferedReader(new StringReader(s)));
        lexer.resetSyntax();
        lexer.parseNumbers();
        lexer.ordinaryChar('-');
        lexer.wordChars('a', 'z');
        lexer.wordChars('A', 'Z');
        lexer.whitespaceChars(' ', ' ');
        return izraz(lexer);
    }

    // <izraz> ::= <aditivni> EOF
    public static Izraz izraz(StreamTokenizer lexer) throws IOException, Error {
        Izraz e = aditivni(lexer);
        if (lexer.nextToken() != StreamTokenizer.TT_EOF) {
            throw new Error("pričakoval EOF, dobil: " + lexer.toString());
        } else {
            return e;
        }
    }

    // <aditivni> ::= <multiplikativni> | <aditivni> + <multiplikativni>
    public static Izraz aditivni(StreamTokenizer lexer) throws IOException, Error {
        // Pozor: pravilo je levo rekurzivno. Naivna implementacija bi
        // povzročila neskončno rekurzijo, saj bi funkcija klicala samo sebe, ne
        // da bi vzela vsaj en token z vhoda. Zato z zanko preberemo celo
        // zaporedje členov a + b + ... + c naenkrat.
        Izraz e = multiplikativni(lexer);
        while (true) {
            switch (lexer.nextToken()) {
	            case '+':
	                e = new Plus(e, multiplikativni(lexer));
	                break;
	            case '-':
	            	e = new Minus(e, multiplikativni(lexer));
	            	break;
	            default:
	                lexer.pushBack();
	                return e;
            }
        }
    }

    // <multiplikativni> ::= <nasprotni> | <multiplikativni> * <nasprotni>
    public static Izraz multiplikativni(StreamTokenizer lexer) throws IOException, Error {
        // Pozor: pravilo je levo rekurzivno. Naivna implementacija bi
        // povzročila neskončno rekurzijo, saj bi funkcija klicala samo sebe, ne
        // da bi vzela vsaj en token z vhoda. Zato z zanko preberemo celo
        // zaporedje členov a * b * ... * c naenkrat.
    	
    	Izraz e = potenca(lexer);
    	
        while (true) {
            switch (lexer.nextToken()) {
	            case '*':
	                e = new Krat(e, nasprotni(lexer));
	                break;
	            case '/':
	            	e = new Deljeno(e, nasprotni(lexer));
	            	break;
	            default:
	                lexer.pushBack();
	                return e;
            }
        }
    }
    
    public static Izraz potenca(StreamTokenizer lexer) throws IOException, Error {
    	Izraz e = nasprotni(lexer);
    	switch(lexer.nextToken()) {
	    	case '^':
	    		return new Potenca(e, potenca(lexer));
	    	default:
	    		lexer.pushBack();
	    		return e;
    	}
    }

    // <nasprotni> ::= - <nasprotni> | <osnovni>
    public static Izraz nasprotni(StreamTokenizer lexer) throws IOException, Error {
        if (lexer.nextToken() == '-') {
            Izraz e = nasprotni(lexer);
            return new UnarniMinus(e);
        } else {
            lexer.pushBack();
            Izraz e = osnovni(lexer);
            return e;
        }
    }

    // <osnovni> ::= ( <aditivni> ) | <konstanta> | <spremenljivka>
    public static Izraz osnovni(StreamTokenizer lexer) throws IOException, Error {
        switch (lexer.nextToken()) {
            case '(':
                Izraz e = aditivni(lexer);
                if (lexer.nextToken() != ')') {
                    throw new Error("pričakoval ')', dobil " + lexer.toString());
                } else {
                    return e;
                }
            case StreamTokenizer.TT_NUMBER:
                lexer.pushBack();
                return konstanta(lexer);
            case StreamTokenizer.TT_WORD:
                lexer.pushBack();
                return spremenljivka(lexer);
            default:
                lexer.pushBack();
                throw new Error("pričakoval osnovni izraz, dobil " + lexer.toString());
        }
    }

    // <konstanta> ::= <float>
    public static Izraz konstanta(StreamTokenizer lexer) throws IOException, Error {
        if (lexer.nextToken() == StreamTokenizer.TT_NUMBER) {
            return new Konstanta(lexer.nval);
        } else {
            lexer.pushBack();
            throw new Error("pričakoval konstanto, dobil " + lexer.toString());
        }
    }

    // <spremenljivka> ::= [a-zA-Z]+
    public static Izraz spremenljivka(StreamTokenizer lexer) throws IOException, Error {
        if (lexer.nextToken() == StreamTokenizer.TT_WORD) {
            return new Spremenljivka(lexer.sval);
        } else {
            lexer.pushBack();
            throw new Error("pričakoval spremenljivko, dobil " + lexer.toString());
        }
    }
}
