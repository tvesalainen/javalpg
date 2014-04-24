javalpg
=======

Java Lookahead Parser Generator. Generator produces LALR(k) parsers. Grammar 
rules are entered using annotations. Rule annotation can be attached to reducer 
method, which makes rule and it's action together.

Lexical analyzer is based on regular expressions. These are also entered using 
annotations.

The whole parser generator code is in lpg library. Javalpg contains parsers for 
extended regex and bnf syntaxies. These are in separate libray because they are 
generated using lpg.