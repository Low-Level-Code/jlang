package ast;

import java.util.List;
import tokenizer.*;

public abstract class Stmt {
	public interface Visitor<R> {
		R visitBlockStmt(Block stmt);
		R visitExpressionStmt(Expression stmt);
		R visitIfStmt(If stmt);
		R visitPrintStmt(Print stmt);
		R visitVarStmt(Var stmt);
		R visitWhileStmt(While stmt);
		R visitBreakStmt(Break stmt);
		R visitContinueStmt(Continue stmt);
	}
	public static class Block extends Stmt {
		public Block(List<Stmt> statements) {
			this.statements = statements;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

		public final List<Stmt> statements;
	}
	public static class Expression extends Stmt {
		public Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

		public final Expr expression;
	}
	public static class If extends Stmt {
		public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

		public final Expr condition;
		public final Stmt thenBranch;
		public final Stmt elseBranch;
	}
	public static class Print extends Stmt {
		public Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitPrintStmt(this);
		}

		public final Expr expression;
	}
	public static class Var extends Stmt {
		public Var(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}

		public final Token name;
		public final Expr initializer;
	}
	public static class While extends Stmt {
		public While(Expr condition, Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

		public final Expr condition;
		public final Stmt body;
	}
	public static class Break extends Stmt {
		public Break(Token keyword) {
			this.keyword = keyword;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStmt(this);
		}

		public final Token keyword;
	}
	public static class Continue extends Stmt {
		public Continue(Token keyword) {
			this.keyword = keyword;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitContinueStmt(this);
		}

		public final Token keyword;
	}

	public abstract <R> R accept(Visitor<R> visitor);
}
