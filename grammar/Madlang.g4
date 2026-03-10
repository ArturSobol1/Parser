grammar MadLang;

program
    : decl* EOF
    ;

decl
    : globalVarDecl
    | funDecl
    ;

globalVarDecl
    : IDENT ':' type_ ('=' expr)? ';'
    ;

funDecl
    : 'fn' IDENT '(' params? ')' ':' type_ block
    ;

params
    : param (',' param)*
    ;

param
    : IDENT ':' type_
    ;

type_
    : 'int'
    | 'bool'
    ;

stmt
    : block
    | varDef
    | funDef
    | assignStmt
    | ifStmt
    | whileStmt
    | returnStmt
    | exprStmt
    ;

block
    : '{' stmt* '}'
    ;

varDef
    : IDENT ':' type_ ('=' expr)? ';'
    ;

funDef
    : funDecl
    ;

assignStmt
    : IDENT '=' expr ';'
    ;

ifStmt
    : 'if' '(' expr ')' stmt ('else' stmt)?
    ;

whileStmt
    : 'while' '(' expr ')' stmt
    ;

returnStmt
    : 'return' expr ';'
    ;

exprStmt
    : expr ';'
    ;

expr
    : orExpr
    ;

orExpr
    : andExpr ('||' andExpr)*
    ;

andExpr
    : eqExpr ('&&' eqExpr)*
    ;

eqExpr
    : relExpr (('==' | '!=') relExpr)*
    ;

relExpr
    : addExpr (('<' | '<=' | '>' | '>=') addExpr)*
    ;

addExpr
    : mulExpr (('+' | '-') mulExpr)*
    ;

mulExpr
    : unaryExpr (('*' | '/' | '%') unaryExpr)*
    ;

unaryExpr
    : ('-' | '!') unaryExpr
    | primary
    ;

primary
    : INT_LIT
    | 'true'
    | 'false'
    | IDENT ('(' args? ')')?
    | '(' expr ')'
    ;

args
    : expr (',' expr)*
    ;

IDENT
    : [A-Za-z_][A-Za-z0-9_]*
    ;

INT_LIT
    : '0' | [1-9][0-9]*
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;

WS
    : [ \t\r\n]+ -> skip
    ;