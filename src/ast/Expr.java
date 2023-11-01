package ast;

import java.util.List;
import tokenizer.*;

public abstract class Expr {
	public interface Visitor<R> {
		R visitBinaryExpr(Binary expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal expr);
		R visitUnaryExpr(Unary expr);
		R visitBlockExpr(Block expr);
		R visitCommaExpr(Comma expr);
	}
	public static class Binary extends Expr {
		public Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		final Expr left;
		final Token operator;
		final Expr right;
	}
	public static class Grouping extends Expr {
		public Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		final Expr expression;
	}
	public static class Literal extends Expr {
		public Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		final Object value;
	}
	public static class Unary extends Expr {
		public Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		final Token operator;
		final Expr right;
	}
	public static class Block extends Expr {
		public Block(List<Expr> statements) {
			this.statements = statements;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockExpr(this);
		}

		final List<Expr> statements;
	}
	public static class Comma extends Expr {
		public Comma(Expr left, Expr right) {
			this.left = left;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCommaExpr(this);
		}

		final Expr left;
		final Expr right;
	}

	abstract <R> R accept(Visitor<R> visitor);
}
