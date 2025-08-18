import java.util.*;

public class Parser {

    enum TokenType {NUM, DOLLAR, PLUS, MINUS, PP, MM, LPAREN, RPAREN, EOF, HASH}

    // Token Class
    // Container for a single token [++, --, +, -, 0-9, $, (, )]
    static class Token {
        TokenType type;
        String text;

        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    static class LexScanner {
        String input;
        int currIdx;
        int len;

        LexScanner(String input) {
            this.input = input.trim(); // remove trailing white spaces
            this.currIdx = 0;
            this.len = (this.input).length();
        }

        public Token nextToken() {
            Token retToken;

            // no iteration left
            if (len <= currIdx) {
                retToken = new Token(TokenType.EOF, "");
                return retToken;
            }

            // skip all white spaces
            char currChar = input.charAt(currIdx);
            // TODO: remove new lines while parsing
            while (currChar == ' ' || currChar == '\n') {
                if (len <= currIdx) {
                    retToken = new Token(TokenType.EOF, "");
                    return retToken;
                }
                currIdx = currIdx + 1;
                currChar = input.charAt(currIdx);
            }

            // initializing the nextChar, if available
            char nextChar;
            if (currIdx + 1 < len) {
                nextChar = input.charAt(currIdx + 1);
            }
            else {
                nextChar = '\0';
            }

            // all possible cases
            if (currChar == '$') {
                retToken = new Token(TokenType.DOLLAR, "$");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (currChar == '+' && nextChar == '+') {
                retToken = new Token(TokenType.PP, "++");
                currIdx = currIdx + 2;
                return retToken;
            }
            else if (currChar == '-' && nextChar == '-') {
                retToken = new Token(TokenType.MM, "--");
                currIdx = currIdx + 2;
                return retToken;
            }
            else if (currChar == '(') {
                retToken = new Token(TokenType.LPAREN, "(");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (currChar == ')') {
                retToken = new Token(TokenType.RPAREN, ")");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (currChar == '+') {
                retToken = new Token(TokenType.PLUS, "+");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (currChar == '-') {
                retToken = new Token(TokenType.MINUS, "-");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (currChar == '#') {
                // process full comment
                while (input.charAt(currIdx) != '\n') {
                    currIdx = currIdx + 1;
                    if (len <= currIdx) {
                        retToken = new Token(TokenType.EOF, "");
                        return retToken;
                    }
                }
                retToken = new Token(TokenType.HASH, "");
                currIdx = currIdx + 1;
                return retToken;
            }
            else if (Character.isDigit(currChar)) {
                retToken = new Token(TokenType.NUM, String.valueOf(currChar));
                currIdx = currIdx + 1;
                return retToken;
            }
            else {
                throw new RuntimeException("Unexpected character: " + currChar);
            }

        }
    }



    static class Parse {

        private final LexScanner lexsc;
        private Token curr;
        private StringBuilder parsedString;

        Parse(LexScanner lexsc) {
            this.lexsc = lexsc;
            this.curr = lexsc.nextToken();
            parsedString = new StringBuilder("");
        }


        StringBuilder parse() {
            E();
            if (curr.type != TokenType.EOF) {
                System.out.println("Parse fail! " + curr.text);
                throw new RuntimeException("Unexpected token!");
            }
            System.out.println("Successfully parsed!");
            return parsedString;
        }

        // E -> AE'
        void E() {
            // TODO
            System.out.println("In E() " + curr.text);

            if (curr.type == TokenType.HASH) {
                curr = lexsc.nextToken();
            }
            if (curr.type != TokenType.EOF) {
                A();
                Eprime();
            }
        }

        // A -> N | L | IE | (E)
        void A() {
            // TODO
            System.out.println("In A() " + curr.text);

            if (curr.type == TokenType.NUM) {
                N();
            }
            else if (curr.type == TokenType.DOLLAR) {
                L();
            }
            else if (curr.type == TokenType.PP || curr.type == TokenType.MM) {
                I();
                E();
            }
            else if (curr.type == TokenType.LPAREN) {
                curr = lexsc.nextToken();
                E();
                if (curr.type == TokenType.RPAREN) {
                    curr = lexsc.nextToken();
                }
                else {
                    throw new RuntimeException("Unexpected token in (E) in A()!");
                }
            }
            else {
                throw new RuntimeException("Unexpected token in A()!");
            }

        }

        // E' -> IEE' | BEE' | empty
        void Eprime() {
            // TODO
            System.out.println("In Eprime() " + curr.text);

            if (curr.type == TokenType.PP || curr.type == TokenType.MM) {
                I();
                Eprime();
            }
            else if (curr.type == TokenType.PLUS || curr.type == TokenType.MINUS) {
                B();
                E();
                Eprime();
            }
            else if (curr.type == TokenType.DOLLAR ||
                    curr.type == TokenType.NUM ||
                    curr.type == TokenType.PP ||
                    curr.type == TokenType.MM ||
                    curr.type == TokenType.LPAREN) {
                // String concatenation
                E();
                Eprime();
            }
        }


        // I -> ++ | --
        void I() {
            // TODO
            System.out.println("In I() " + curr.text);

            if (curr.type == TokenType.PP || curr.type == TokenType.MM) {
                curr = lexsc.nextToken();
            }
            else {
                throw new RuntimeException("Unexpected token in I()!");
            }
        }


        // L -> $E
        void L() {
            // TODO
            System.out.println("In L() " + curr.text);

            if (curr.type == TokenType.DOLLAR) {
                curr = lexsc.nextToken();
                E();
            }
            else {
                throw new RuntimeException("Unexpected token in L()!");
            }
        }


        // B -> + | - |
        void B() {
            // TODO
            System.out.println("In B() " + curr.text);

            if (curr.type == TokenType.PLUS || curr.type == TokenType.MINUS) {
                curr = lexsc.nextToken();
            }
            // else {
            //     // TODO: String concat
            //     curr = lexsc.nextToken();
            // }
        }


        // N -> [0-9]
        void N() {
            // TODO
            System.out.println("In N() " + curr.text);

            if (curr.type == TokenType.NUM) {
                curr = lexsc.nextToken();
            }
            else {
                throw new RuntimeException("Unexpected token in N()!");
            }
        }


 
    }

    public static void main(String[] args) {
        LexScanner lexsc = new LexScanner("    $  4 ++ - --- ( ) # fsdnenj");
        Token out = lexsc.nextToken();
        while (out.type != TokenType.EOF) {
            System.out.println(out.text);
            out = lexsc.nextToken();
        }

        // TEST CASE 1

        // String input = "(1 2)";
        // lexsc = new LexScanner(input);
        // Parse parser = new Parse(lexsc);
        // parser.parse();

        // TEST CASE 2

        // String input = "$  1 + \n (1 -++$2) $ #sfnonorneo \n 3";
        // lexsc = new LexScanner(input);
        // Parse parser = new Parse(lexsc);
        // parser.parse();

        // TEST CASE 3

        // String input = "$ $ 1 ++ ++ - $2";
        // lexsc = new LexScanner(input);
        // Parse parser = new Parse(lexsc);
        // parser.parse();

        // TEST CASE 4

        // String input = "1 ++ ++ - $2";
        // lexsc = new LexScanner(input);
        // Parse parser = new Parse(lexsc);
        // parser.parse();

        // TEST CASE 4

        String input = "$ 1 2";
        lexsc = new LexScanner(input);
        Parse parser = new Parse(lexsc);
        parser.parse();
    }

}