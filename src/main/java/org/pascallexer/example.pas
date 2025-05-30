program TestAllTokens;

var
    // Basic types
    a: integer;
    b: real;
    c: boolean;
    d: string;
    e: char;
    f: byte;
    g: smallint;
    h: longint;

    // User-defined
    rec: record x: integer; end;
    arr: array [1..3] of integer;
    s: set of 1..5;
    p: packed array [1..2] of byte;
    pf: file of integer;
    lbl: integer;

label
    L1, L2;

const
    CONST_VAL = 42;

type
    TAlias = real;
    TCase = (CASE1, CASE2);
    TProc = procedure;

begin
    // Keyword coverage: and, array, begin, case, const, div, do, downto, else, end,
    // file, for, function, goto, if, in, label, mod, nil, not, of, or, packed,
    // procedure, program, record, repeat, set, then, to, type, until, var, while, with

    // Literals
    a := 123;                      // INTEGER_LITERAL
    b := 3.14;                     // REAL_LITERAL
    d := 'hello';                  // STRING_LITERAL
    e := 'c';                      // CHAR_LITERAL

    // Arithmetic and relational operators
    a := a + 1;                    // PLUS
    a := a - 1;                    // MINUS
    a := a * 2;                    // MUL
    a := a div 2;                  // DIV keyword
    a := a / 2;               // DIV_OP (as slash)
    a := a mod 5;                  // MOD
    if a = 10 then                 // EQ
        a := a + 0;
    if a <> 0 then                 // NE
        a := a - 0;
    if a < b then                  // LT
        a := a + 1;
    if a <= b then                 // LE
        a := a + 2;
    if a > b then                  // GT
        a := a + 3;
    if a >= b then                 // GE
        a := a + 4;

    // Control flow
    for a := 1 to 3 do             // FOR, TO, DO
        begin
            repeat                // REPEAT
                a := a + 1
            until a > 5;          // UNTIL
        end;
    while a < 10 do                // WHILE
        a := a + 1;
    if a in [1..5] then            // IN, TO, DOTDOT, LBracket, RBracket
        a := 0
    else                           // ELSE
        a := -1;
    goto L1;                       // GOTO
L1:
    with rec do                    // WITH
        x := 100;
    case a of                      // CASE, OF
        1: a := 1;
        2: a := 2
    end;                           // END of case

    // Punctuation
    arr[1] := 5;                   // LBracket, RBracket, ASSIGN
    p[2] := 10;                    // PACKED array
    lbl := CONST_VAL;              // COLON, SEMICOLON, COMMA
    inc(a);                        // PROCEDURE or IDENT
    writeln('Done', a);            // IDENT

    // Comments
    { This is a Pascal-style comment }
    // This is a C++-style comment
    (*
    This is a Delphi-style comment
     *)

    // End-of-program
end.                              // DOT
