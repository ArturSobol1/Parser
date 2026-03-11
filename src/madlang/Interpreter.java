package madlang;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {

  private static final String TYPE_MISMATCH    = "Error: type mismatch";
  private static final String UNBOUND_REFERENCE = "Error: unbound reference";
  private static final String ARITHMETIC_ERROR  = "Error: arithmetic error";

  private final Environment env = new Environment();
  private final Deque<Map<String, Callable>> functionScopes = new ArrayDeque<>();
  private final Scanner inputScanner = new Scanner(System.in);

  // -------------------------------------------------------------------------
  // Internal interfaces / helpers
  // -------------------------------------------------------------------------

  private interface Callable {
    VarType returnType();
    List<Stmt.Parameter> params();
    Object invoke(Interpreter interpreter, List<Object> args);
  }

  private static class UserFunction implements Callable {
    private final Stmt.Function declaration;

    UserFunction(Stmt.Function declaration) {
      this.declaration = declaration;
    }

    @Override public VarType returnType() { return declaration.returnType; }
    @Override public List<Stmt.Parameter> params() { return declaration.params; }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> args) {
      return interpreter.executeUserFunction(declaration, args);
    }
  }

  @FunctionalInterface
  private interface BuiltinBody {
    Object run(Interpreter interpreter, List<Object> args);
  }

  private static class BuiltinFunction implements Callable {
    private final VarType returnType;
    private final List<Stmt.Parameter> params;
    private final BuiltinBody body;

    BuiltinFunction(VarType returnType, List<Stmt.Parameter> params, BuiltinBody body) {
      this.returnType = returnType;
      this.params = params;
      this.body = body;
    }

    @Override public VarType returnType() { return returnType; }
    @Override public List<Stmt.Parameter> params() { return params; }

    @Override
    public Object invoke(Interpreter interpreter, List<Object> args) {
      return body.run(interpreter, args);
    }
  }

  /** Used as a non-local exit to carry a return value up the call stack. */
  private static class ReturnSignal extends RuntimeException {
    final Object value;
    ReturnSignal(Object value) { super(null, null, false, false); this.value = value; }
  }

  /** Used to report a runtime error and unwind to the top-level handler. */
  static class InterpreterError extends RuntimeException {
    final String errorMessage;
    InterpreterError(String msg) { super(null, null, false, false); this.errorMessage = msg; }
  }

  // -------------------------------------------------------------------------
  // Constructor
  // -------------------------------------------------------------------------

  Interpreter() {
    functionScopes.push(new HashMap<>());
    installBuiltins();
  }

  // -------------------------------------------------------------------------
  // Public entry point
  // -------------------------------------------------------------------------

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
  
      Callable mainFunction = lookupFunction("main");
  
      if (!mainFunction.params().isEmpty() || mainFunction.returnType() != VarType.INT) {
        throw new InterpreterError(TYPE_MISMATCH);
      }
  
      mainFunction.invoke(this, List.of());
  
    } catch (InterpreterError error) {
      System.out.println(error.errorMessage);
    }
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private void execute(Stmt stmt) { stmt.accept(this); }

  private Object evaluate(Expr expr) { return expr.accept(this); }

  private Map<String, Callable> currentFunctionScope() { return functionScopes.peek(); }

  private void installBuiltins() {
    currentFunctionScope().put(
            "input",
            new BuiltinFunction(
                    VarType.INT,
                    List.of(),
                    (interpreter, args) -> {
                      try {
                        return interpreter.inputScanner.nextInt();
                      } catch (Exception ignored) {
                        throw new InterpreterError(TYPE_MISMATCH);
                      }
                    }
            )
    );

    currentFunctionScope().put(
            "output",
            new BuiltinFunction(
                    VarType.INT,
                    List.of(new Stmt.Parameter("n", VarType.INT)),
                    (interpreter, args) -> {
                      Object arg = args.get(0);
                      if (!(arg instanceof Integer)) throw new InterpreterError(TYPE_MISMATCH);
                      int value = (Integer) arg;
                      System.out.print(value);
                      return value;
                    }
            )
    );
  }

  private Object executeUserFunction(Stmt.Function declaration, List<Object> args) {
    env.pushScope();
    functionScopes.push(new HashMap<>());
    try {
      for (int i = 0; i < declaration.params.size(); i++) {
        Stmt.Parameter param = declaration.params.get(i);
        env.define(param.name(), param.type(), args.get(i));
      }

      try {
        for (Stmt statement : declaration.body) {
          execute(statement);
        }
      } catch (ReturnSignal signal) {
        if (!matchesType(declaration.returnType, signal.value)) {
          throw new InterpreterError(TYPE_MISMATCH);
        }
        return signal.value;
      }

      return defaultValue(declaration.returnType);
    } finally {
      functionScopes.pop();
      env.popScope();
    }
  }

  private Callable lookupFunction(String name) {
    for (Map<String, Callable> scope : functionScopes) {
      if (scope.containsKey(name)) return scope.get(name);
    }
    throw new InterpreterError(UNBOUND_REFERENCE);
  }

  private boolean matchesType(VarType type, Object value) {
    if (type == VarType.INT)  return value instanceof Integer;
    if (type == VarType.BOOL) return value instanceof Boolean;
    return false;
  }

  private Object defaultValue(VarType type) {
    return type == VarType.INT ? 0 : false;
  }

  private int requireInt(Object value) {
    if (!(value instanceof Integer)) throw new InterpreterError(TYPE_MISMATCH);
    return (Integer) value;
  }

  private boolean requireBool(Object value) {
    if (!(value instanceof Boolean)) throw new InterpreterError(TYPE_MISMATCH);
    return (Boolean) value;
  }

  // -------------------------------------------------------------------------
  // Statement visitors
  // -------------------------------------------------------------------------

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    currentFunctionScope().put(stmt.name, new UserFunction(stmt));
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    boolean condition = requireBool(evaluate(stmt.condition));
    if (condition) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    Object value = stmt.value == null ? null : evaluate(stmt.value);
    throw new ReturnSignal(value);
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    env.pushScope();
    functionScopes.push(new HashMap<>());
    try {
      for (Stmt statement : stmt.statements) {
        execute(statement);
      }
    } finally {
      functionScopes.pop();
      env.popScope();
    }
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = stmt.initializer == null ? defaultValue(stmt.type) : evaluate(stmt.initializer);
    if (!matchesType(stmt.type, value)) throw new InterpreterError(TYPE_MISMATCH);
    env.define(stmt.name, stmt.type, value);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    while (requireBool(evaluate(stmt.condition))) {
      execute(stmt.body);
    }
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitAssignStmt(Stmt.Assign stmt) {
    Environment.Slot slot = env.getSlot(stmt.name);
    if (slot == null) throw new InterpreterError(UNBOUND_REFERENCE);
    Object value = evaluate(stmt.value);
    if (!matchesType(slot.type, value)) throw new InterpreterError(TYPE_MISMATCH);
    slot.value = value;
    return null;
  }

  // -------------------------------------------------------------------------
  // Expression visitors
  // -------------------------------------------------------------------------

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);

    // Short-circuit logical operators
    if (expr.operator == Operator.AND) {
      return requireBool(left) && requireBool(evaluate(expr.right));
    }
    if (expr.operator == Operator.OR) {
      return requireBool(left) || requireBool(evaluate(expr.right));
    }

    Object right = evaluate(expr.right);

    switch (expr.operator) {
      case PLUS:     return requireInt(left) + requireInt(right);
      case MINUS:    return requireInt(left) - requireInt(right);
      case MULTIPLY: return requireInt(left) * requireInt(right);
      case DIVIDE: {
        int divisor = requireInt(right);
        if (divisor == 0) throw new InterpreterError(ARITHMETIC_ERROR);
        return requireInt(left) / divisor;
      }
      case MODULO: {
        int divisor = requireInt(right);
        if (divisor == 0) throw new InterpreterError(ARITHMETIC_ERROR);
        return requireInt(left) % divisor;
      }
      case LESS:          return requireInt(left) < requireInt(right);
      case LESS_EQUAL:    return requireInt(left) <= requireInt(right);
      case GREATER:       return requireInt(left) > requireInt(right);
      case GREATER_EQUAL: return requireInt(left) >= requireInt(right);
      case EQUAL:
      case NOT_EQUAL: {
        boolean comparable =
                (left instanceof Integer && right instanceof Integer) ||
                        (left instanceof Boolean && right instanceof Boolean);
        if (!comparable) throw new InterpreterError(TYPE_MISMATCH);
        boolean equal = left.equals(right);
        return expr.operator == Operator.EQUAL ? equal : !equal;
      }
      default:
        throw new InterpreterError(TYPE_MISMATCH);
    }
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);
    switch (expr.operator) {
      case NOT:   return !requireBool(right);
      case MINUS: return -requireInt(right);
      default:    throw new InterpreterError(TYPE_MISMATCH);
    }
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    Environment.Slot slot = env.getSlot(expr.name);
    if (slot == null) throw new InterpreterError(UNBOUND_REFERENCE);
    return slot.value;
  }

  @Override
  public Object visitCallExpr(Expr.Call expr) {
    Callable function = lookupFunction(expr.name);
    List<Stmt.Parameter> params = function.params();

    if (params.size() != expr.arguments.size()) {
      throw new InterpreterError(TYPE_MISMATCH);
    }

    List<Object> args = new ArrayList<>();
    for (int i = 0; i < expr.arguments.size(); i++) {
      Object value = evaluate(expr.arguments.get(i));
      if (!matchesType(params.get(i).type(), value)) {
        throw new InterpreterError(TYPE_MISMATCH);
      }
      args.add(value);
    }

    Object result = function.invoke(this, args);
    if (!matchesType(function.returnType(), result)) {
      throw new InterpreterError(TYPE_MISMATCH);
    }
    return result;
  }
}