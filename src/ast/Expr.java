package ast;

import java.util.List;
import tokenizer.*;

public abstract class Expr {
	public interface Visitor<R> {
		R visitAssignExpr(Assign expr);
		R visitBinaryExpr(Binary expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal expr);
		R visitLogicalExpr(Logical expr);
		R visitUnaryExpr(Unary expr);
		R visitBlockExpr(Block expr);
		R visitCommaExpr(Comma expr);
		R visitTernaryExpr(Ternary expr);
		R visitVariableExpr(Variable expr);
	}
	public static class Assign extends Expr {
		public Assign(Token name, Expr value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpr(this);
		}

		public final Token name;
		public final Expr value;
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

	public abstract <R> R accept(Visitor<R> visitor);
}
