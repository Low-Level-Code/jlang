package ast;

import java.util.List;
import tokenizer.*;

public abstract class Expr {
	public interface Visitor<R> {
		R visitAssignExpr(Assign expr);
		R visitBinaryExpr(Binary expr);
		R visitGroupingExpr(Grouping expr);
		R visitGetExpr(Get expr);
		R visitCallExpr(Call expr);
		R visitLiteralExpr(Literal expr);
		R visitArrayExpr(Array expr);
		R visitArrayAccessExpr(ArrayAccess expr);
		R visitLogicalExpr(Logical expr);
		R visitSetExpr(Set expr);
		R visitSuperExpr(Super expr);
		R visitThisExpr(This expr);
		R visitUnaryExpr(Unary expr);
		R visitPostfixExpr(Postfix expr);
		R visitBlockExpr(Block expr);
		R visitCommaExpr(Comma expr);
		R visitTernaryExpr(Ternary expr);
		R visitVariableExpr(Variable expr);
		R visitLambdaFunctionExpr(LambdaFunction expr);
		R visitAnonymousClassExpr(AnonymousClass expr);
	}
	public static class Assign extends Expr {
		public Assign(Token name, Expr value, Token operator) {
			this.name = name;
			this.value = value;
			this.operator = operator;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpr(this);
		}

		public final Token name;
		public final Expr value;
		public final Token operator;
	}
	public static class Binary extends Expr {
		public Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		public final Expr left;
		public final Token operator;
		public final Expr right;
	}
	public static class Grouping extends Expr {
		public Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		public final Expr expression;
	}
	public static class Get extends Expr {
		public Get(Expr object, Token name) {
			this.object = object;
			this.name = name;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitGetExpr(this);
		}

		public final Expr object;
		public final Token name;
	}
	public static class Call extends Expr {
		public Call(Expr callee, Token paren, List<Expr> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpr(this);
		}

		public final Expr callee;
		public final Token paren;
		public final List<Expr> arguments;
	}
	public static class Literal extends Expr {
		public Literal(Object value) {
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		public final Object value;
	}
	public static class Array extends Expr {
		public Array(List<Expr> elements) {
			this.elements = elements;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitArrayExpr(this);
		}

		public final List<Expr> elements;
	}
	public static class ArrayAccess extends Expr {
		public ArrayAccess(Expr name, Expr index) {
			this.name = name;
			this.index = index;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitArrayAccessExpr(this);
		}

		public final Expr name;
		public final Expr index;
	}
	public static class Logical extends Expr {
		public Logical(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpr(this);
		}

		public final Expr left;
		public final Token operator;
		public final Expr right;
	}
	public static class Set extends Expr {
		public Set(Expr object, Token name, Expr value) {
			this.object = object;
			this.name = name;
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitSetExpr(this);
		}

		public final Expr object;
		public final Token name;
		public final Expr value;
	}
	public static class Super extends Expr {
		public Super(Token keyword, Token method) {
			this.keyword = keyword;
			this.method = method;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitSuperExpr(this);
		}

		public final Token keyword;
		public final Token method;
	}
	public static class This extends Expr {
		public This(Token keyword) {
			this.keyword = keyword;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitThisExpr(this);
		}

		public final Token keyword;
	}
	public static class Unary extends Expr {
		public Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		public final Token operator;
		public final Expr right;
	}
	public static class Postfix extends Expr {
		public Postfix(Expr left, Token operator) {
			this.left = left;
			this.operator = operator;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitPostfixExpr(this);
		}

		public final Expr left;
		public final Token operator;
	}
	public static class Block extends Expr {
		public Block(List<Expr> statements) {
			this.statements = statements;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockExpr(this);
		}

		public final List<Expr> statements;
	}
	public static class Comma extends Expr {
		public Comma(Expr left, Expr right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitCommaExpr(this);
		}

		public final Expr left;
		public final Expr right;
	}
	public static class Ternary extends Expr {
		public Ternary(Expr condition, Expr thenExpr, Expr elseExpr) {
			this.condition = condition;
			this.thenExpr = thenExpr;
			this.elseExpr = elseExpr;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitTernaryExpr(this);
		}

		public final Expr condition;
		public final Expr thenExpr;
		public final Expr elseExpr;
	}
	public static class Variable extends Expr {
		public Variable(Token name) {
			this.name = name;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpr(this);
		}

		public final Token name;
	}
	public static class LambdaFunction extends Expr {
		public LambdaFunction(Token name, List<Token> params, List<Stmt> body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLambdaFunctionExpr(this);
		}

		public final Token name;
		public final List<Token> params;
		public final List<Stmt> body;
	}
	public static class AnonymousClass extends Expr {
		public AnonymousClass(Token name, List<Expr.Variable> parents, List<Stmt.Function> methods) {
			this.name = name;
			this.parents = parents;
			this.methods = methods;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitAnonymousClassExpr(this);
		}

		public final Token name;
		public final List<Expr.Variable> parents;
		public final List<Stmt.Function> methods;
	}

	public abstract <R> R accept(Visitor<R> visitor);
}
