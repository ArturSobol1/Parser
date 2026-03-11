// Generated from grammar/MadLang.g4 by ANTLR 4.13.2
package madlang;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MadLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MadLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MadLangParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MadLangParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(MadLangParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#globalVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalVarDecl(MadLangParser.GlobalVarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#funDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunDecl(MadLangParser.FunDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(MadLangParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(MadLangParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#type_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_(MadLangParser.Type_Context ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(MadLangParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MadLangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(MadLangParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#funDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunDef(MadLangParser.FunDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(MadLangParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(MadLangParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(MadLangParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(MadLangParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#exprStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(MadLangParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(MadLangParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#orExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpr(MadLangParser.OrExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(MadLangParser.AndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#eqExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(MadLangParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#relExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExpr(MadLangParser.RelExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(MadLangParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#mulExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(MadLangParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MadLangParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(MadLangParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link MadLangParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(MadLangParser.ArgsContext ctx);
}