# LL(1) Parsing

In the Parser.java file I have implemented a tokenizer, lexical scanner, and a recursive descent parser for LL(1) grammars. The parser recognizes a subset of expressions from the Awk Programming Language. If the parser successfully recognizes the expression it will print the expression in postfix-notation.

## Grammar:

expr	::=	A E' <br>
A ::= num | lvalue | incrop expr | (expr) <br>
E' ::= binop A E' | incrop E' | _empty_ <br>
lvalue	::=	$expr <br>
incrop	::=	++ | -- <br>
binop	::=	+ | - | _empty_ <br>
num	::=	0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 <br>

