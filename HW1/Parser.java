import java.util.*;

/**
 * Parser.java
 *
 * Description: Recursive descent parser for LL(1) grammar.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-08-18
 */


public class Parser {

    enum TokenType {NUM, DOLLAR, PLUS, MINUS, PP, MM, LPAREN, RPAREN, EOF, HASH}

    /*
     * Token Class
     * Container for a single token [++, --, +, -, 0-9, $, (, )]
     */
    static class Token {
        TokenType type;
        String text;

        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }
    }

    /*
     * Scanner for input syntax
     */
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


    /*
     * Class to implement recursive descent parser.
     */
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
            while (curr.type == TokenType.HASH) {
                curr = lexsc.nextToken();
            }
            if (curr.type != TokenType.EOF) {
                A();
                Eprime();
            }
        }

        // A -> N | L | IE | (E)
        String A() {
            while (curr.type == TokenType.HASH) {
                curr = lexsc.nextToken();
            }

            if (curr.type == TokenType.NUM) {
                parsedString.append(curr.text + " ");
                N();
            }
            else if (curr.type == TokenType.DOLLAR) {
                L();
            }
            else if (curr.type == TokenType.PP) {
                I();
                A();
                parsedString.append("++_ ");
            }
            else if (curr.type == TokenType.MM) {
                I();
                A();
                parsedString.append("--_ ");
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
            else if (curr.type == TokenType.EOF) {
                return "";
            }
            else {
                throw new RuntimeException("Unexpected token in A()!");
            }

            return "";

        }

        // E' -> IEE' | BEE' | empty
        void Eprime() {
            if (curr.type == TokenType.PP || curr.type == TokenType.MM) {
                String operator = curr.text;
                I();
                parsedString.append("_" + operator + " ");
                Eprime();
            }
            else if (curr.type == TokenType.PLUS || curr.type == TokenType.MINUS) {
                String operator = curr.text;
                B();
                A();
                parsedString.append(operator + " ");
                Eprime();
            }
            else if (curr.type == TokenType.DOLLAR ||
                    curr.type == TokenType.NUM ||
                    curr.type == TokenType.PP ||
                    curr.type == TokenType.MM ||
                    curr.type == TokenType.LPAREN) {
                // String concatenation
                A();
                parsedString.append("_ ");
                Eprime();
            }
        }


        // I -> ++ | --
        void I() {
            if (curr.type == TokenType.PP || curr.type == TokenType.MM) {
                curr = lexsc.nextToken();
            }
            else {
                throw new RuntimeException("Unexpected token in I()!");
            }
        }


        // L -> $E
        void L() {
            if (curr.type == TokenType.DOLLAR) {
                curr = lexsc.nextToken();
                A();
                parsedString.append("$ ");
            }
            else {
                throw new RuntimeException("Unexpected token in L()!");
            }
        }


        // B -> + | - |
        void B() {
            if (curr.type == TokenType.PLUS || curr.type == TokenType.MINUS) {
                curr = lexsc.nextToken();
            }
        }


        // N -> [0-9]
        void N() {
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

        String input1 = "(1 2)";
        System.out.println("Input: " + input1);
        LexScanner lexsc1 = new LexScanner(input1);
        Parse parser1 = new Parse(lexsc1);
        StringBuilder parsedStr1 = parser1.parse();
        System.out.println("Parsed Str: " + parsedStr1);


        // TEST CASE 2

        String input2 = "$  1 + \n (1 -++$2) $ #some strange comment \n 3";
        System.out.println("Input: " + input2);
        LexScanner lexsc2 = new LexScanner(input2);
        Parse parser2 = new Parse(lexsc2);
        StringBuilder parsedStr2 = parser2.parse();
        System.out.println("Parsed Str: " + parsedStr2);

        // TEST CASE 3

        String input3 = "$ $ 1 ++ ++ - $2";
        System.out.println("Input: " + input3);
        LexScanner lexsc3 = new LexScanner(input3);
        Parse parser3 = new Parse(lexsc3);
        StringBuilder parsedStr3 = parser3.parse();
        System.out.println("Parsed Str: " + parsedStr3);

        // TEST CASE 4

        String input4 = "1 ++ ++ - $2";
        System.out.println("Input: " + input4);
        LexScanner lexsc4 = new LexScanner(input4);
        Parse parser4 = new Parse(lexsc4);
        StringBuilder parsedStr4 = parser4.parse();
        System.out.println("Parsed Str: " + parsedStr4);

        // TEST CASE 5

        String input5 = "$ ++ 1 - 2";
        System.out.println("Input: " + input5);
        LexScanner lexsc5 = new LexScanner(input5);
        Parse parser5 = new Parse(lexsc5);
        StringBuilder parsedStr5 = parser5.parse();
        System.out.println("Parsed Str: " + parsedStr5);


        // TEST CASE 6

        String input6 = "++1++";
        System.out.println("Input: " + input6);
        LexScanner lexsc6 = new LexScanner(input6);
        Parse parser6 = new Parse(lexsc6);
        StringBuilder parsedStr6 = parser6.parse();
        System.out.println("Parsed Str: " + parsedStr6);
    }

}