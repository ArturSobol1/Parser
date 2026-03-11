package madlang;

import java.util.ArrayList;
import java.util.List;

public class AstBuilder extends MadLangBaseVisitor<Object> {

    public List<Stmt> build(MadLangParser.ProgramContext ctx) {
        @SuppressWarnings("unchecked")
        List<Stmt> stmts = (List<Stmt>) visit(ctx);
        return stmts;
    }

    @Override
    public Object visitProgram(MadLangParser.ProgramContext ctx) {
        List<Stmt> decls = new ArrayList<>();
        for (MadLangParser.DeclContext d : ctx.decl()) {
            decls.add((Stmt) visit(d));
        }
        return decls;
    }

    @Override
    public Object visitDecl(MadLangParser.DeclContext ctx) {
        if (ctx.globalVarDecl() != null) {
            return visit(ctx.globalVarDecl());
        }
        return visit(ctx.funDecl());
    }

    @Override
    public Object visitGlobalVarDecl(MadLangParser.GlobalVarDeclContext ctx) {
        String name = ctx.IDENT().getText();
        VarType type = parseType(ctx.type_());
        Expr init = null;
        if (ctx.expr() != null) {
            init = (Expr) visit(ctx.expr());
        }
        return new Stmt.Var(name, type, init);
    }

    @Override
    public Object visitFunDecl(MadLangParser.FunDeclContext ctx) {
        String name = ctx.IDENT().getText();
        VarType returnType = parseType(ctx.type_());

        List<Stmt.Parameter> params = new ArrayList<>();
        if (ctx.params() != null) {
            for (MadLangParser.ParamContext p : ctx.params().param()) {
                params.add((Stmt.Parameter) visit(p));
            }
        }

        Stmt.Block body = (Stmt.Block) visit(ctx.block());
        return new Stmt.Function(name, returnType, params, body.statements);
    }

    @Override
    public Object visitParam(MadLangParser.ParamContext ctx) {
        String name = ctx.IDENT().getText();
        VarType type = parseType(ctx.type_());
        return new Stmt.Parameter(name, type);
    }

    @Override
    public Object visitStmt(MadLangParser.StmtContext ctx) {
        if (ctx.block() != null) return visit(ctx.block());
        if (ctx.varDef() != null) return visit(ctx.varDef());
        if (ctx.funDef() != null) return visit(ctx.funDef());
        if (ctx.assignStmt() != null) return visit(ctx.assignStmt());
        if (ctx.ifStmt() != null) return visit(ctx.ifStmt());
        if (ctx.whileStmt() != null) return visit(ctx.whileStmt());
        if (ctx.returnStmt() != null) return visit(ctx.returnStmt());
        return visit(ctx.exprStmt());
    }

    @Override
    public Object visitBlock(MadLangParser.BlockContext ctx) {
        List<Stmt> statements = new ArrayList<>();
        for (MadLangParser.StmtContext s : ctx.stmt()) {
            statements.add((Stmt) visit(s));
        }
        return new Stmt.Block(statements);
    }

    @Override
    public Object visitVarDef(MadLangParser.VarDefContext ctx) {
        String name = ctx.IDENT().getText();
        VarType type = parseType(ctx.type_());
        Expr init = null;
        if (ctx.expr() != null) {
            init = (Expr) visit(ctx.expr());
        }
        return new Stmt.Var(name, type, init);
    }

    @Override
    public Object visitFunDef(MadLangParser.FunDefContext ctx) {
        return visit(ctx.funDecl());
    }

    @Override
    public Object visitAssignStmt(MadLangParser.AssignStmtContext ctx) {
        String name = ctx.IDENT().getText();
        Expr value = (Expr) visit(ctx.expr());
        return new Stmt.Assign(name, value);
    }

    @Override
    public Object visitIfStmt(MadLangParser.IfStmtContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        Stmt thenBranch = (Stmt) visit(ctx.stmt(0));
        Stmt elseBranch = null;
        if (ctx.stmt().size() > 1) {
            elseBranch = (Stmt) visit(ctx.stmt(1));
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    @Override
    public Object visitWhileStmt(MadLangParser.WhileStmtContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        Stmt body = (Stmt) visit(ctx.stmt());
        return new Stmt.While(condition, body);
    }

    @Override
    public Object visitReturnStmt(MadLangParser.ReturnStmtContext ctx) {
        Expr value = (Expr) visit(ctx.expr());
        return new Stmt.Return(value);
    }

    @Override
    public Object visitExprStmt(MadLangParser.ExprStmtContext ctx) {
        return new Stmt.Expression((Expr) visit(ctx.expr()));
    }

    @Override
    public Object visitExpr(MadLangParser.ExprContext ctx) {
        return visit(ctx.orExpr());
    }

    @Override
    public Object visitOrExpr(MadLangParser.OrExprContext ctx) {
        Expr left = (Expr) visit(ctx.andExpr(0));
        for (int i = 1; i < ctx.andExpr().size(); i++) {
            Expr right = (Expr) visit(ctx.andExpr(i));
            left = new Expr.Binary(left, Operator.OR, right);
        }
        return left;
    }

    @Override
    public Object visitAndExpr(MadLangParser.AndExprContext ctx) {
        Expr left = (Expr) visit(ctx.eqExpr(0));
        for (int i = 1; i < ctx.eqExpr().size(); i++) {
            Expr right = (Expr) visit(ctx.eqExpr(i));
            left = new Expr.Binary(left, Operator.AND, right);
        }
        return left;
    }

    @Override
    public Object visitEqExpr(MadLangParser.EqExprContext ctx) {
        Expr left = (Expr) visit(ctx.relExpr(0));
        for (int i = 1; i < ctx.relExpr().size(); i++) {
            String opText = ctx.getChild(2 * i - 1).getText();
            Expr right = (Expr) visit(ctx.relExpr(i));
            left = new Expr.Binary(left, parseOperator(opText), right);
        }
        return left;
    }

    @Override
    public Object visitRelExpr(MadLangParser.RelExprContext ctx) {
        Expr left = (Expr) visit(ctx.addExpr(0));
        for (int i = 1; i < ctx.addExpr().size(); i++) {
            String opText = ctx.getChild(2 * i - 1).getText();
            Expr right = (Expr) visit(ctx.addExpr(i));
            left = new Expr.Binary(left, parseOperator(opText), right);
        }
        return left;
    }

    @Override
    public Object visitAddExpr(MadLangParser.AddExprContext ctx) {
        Expr left = (Expr) visit(ctx.mulExpr(0));
        for (int i = 1; i < ctx.mulExpr().size(); i++) {
            String opText = ctx.getChild(2 * i - 1).getText();
            Expr right = (Expr) visit(ctx.mulExpr(i));
            left = new Expr.Binary(left, parseOperator(opText), right);
        }
        return left;
    }

    @Override
    public Object visitMulExpr(MadLangParser.MulExprContext ctx) {
        Expr left = (Expr) visit(ctx.unaryExpr(0));
        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String opText = ctx.getChild(2 * i - 1).getText();
            Expr right = (Expr) visit(ctx.unaryExpr(i));
            left = new Expr.Binary(left, parseOperator(opText), right);
        }
        return left;
    }

    @Override
    public Object visitUnaryExpr(MadLangParser.UnaryExprContext ctx) {
        if (ctx.primary() != null) {
            return visit(ctx.primary());
        }

        String opText = ctx.getChild(0).getText();
        Expr right = (Expr) visit(ctx.unaryExpr());
        return new Expr.Unary(parseOperator(opText), right);
    }

    @Override
    public Object visitPrimary(MadLangParser.PrimaryContext ctx) {
        if (ctx.INT_LIT() != null) {
            return new Expr.Literal(Integer.parseInt(ctx.INT_LIT().getText()));
        }

        String text = ctx.getText();

        if (text.equals("true")) {
            return new Expr.Literal(true);
        }

        if (text.equals("false")) {
            return new Expr.Literal(false);
        }

        if (ctx.IDENT() != null && ctx.args() == null) {
            return new Expr.Variable(ctx.IDENT().getText());
        }

        if (ctx.IDENT() != null) {
            String name = ctx.IDENT().getText();
            List<Expr> args = new ArrayList<>();

            if (ctx.args() != null) {
                for (MadLangParser.ExprContext e : ctx.args().expr()) {
                    args.add((Expr) visit(e));
                }
            }

            return new Expr.Call(name, args);
        }

        return visit(ctx.expr());
    }

    private VarType parseType(MadLangParser.Type_Context ctx) {
        if (ctx.getText().equals("int")) {
            return VarType.INT;
        }
        return VarType.BOOL;
    }

    private Operator parseOperator(String text) {
        return switch (text) {
            case "+" -> Operator.PLUS;
            case "-" -> Operator.MINUS;
            case "*" -> Operator.MULTIPLY;
            case "/" -> Operator.DIVIDE;
            case "%" -> Operator.MODULO;
            case "&&" -> Operator.AND;
            case "||" -> Operator.OR;
            case "!" -> Operator.NOT;
            case "==" -> Operator.EQUAL;
            case "!=" -> Operator.NOT_EQUAL;
            case "<" -> Operator.LESS;
            case "<=" -> Operator.LESS_EQUAL;
            case ">" -> Operator.GREATER;
            case ">=" -> Operator.GREATER_EQUAL;
            default -> throw new RuntimeException("unknown operator: " + text);
        };
    }
}