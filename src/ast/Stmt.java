package ast;

import java.util.List;
import tokenizer.*;

public abstract class Stmt {
	public interface Visitor<R> {
		R visitBlockStmt(Block stmt);
		R visitExpressionStmt(Expression stmt);
		R visitFunctionStmt(Function stmt);
		R visitIfStmt(If stmt);
		R visitPrintStmt(Print stmt);
		R visitReturnStmt(Return stmt);
		R visitVarStmt(Var stmt);
		R visitConstStmt(Const stmt);
		R visitWhileStmt(While stmt);
		R visitBreakStmt(Break stmt);
		R visitContinueStmt(Continue stmt);
		R visitTryCatchStmt(TryCatch stmt);
		R visitCatchStmt(Catch stmt);
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
	public static class Function extends Stmt {
		public Function(Token name, List<Token> params, List<Stmt> body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}

		public final Token name;
		public final List<Token> params;
		public final List<Stmt> body;
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
	public static class Return extends Stmt {
		public Return(Token keyword, Expr value) {
			this.keyword = keyword;
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}

		public final Token keyword;
		public final Expr value;
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
	public static class Const extends Stmt {
		public Const(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitConstStmt(this);
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
	public static class TryCatch extends Stmt {
		public TryCatch(Stmt tryBlock, List<Catch> catchBlocks) {
			this.tryBlock = tryBlock;
			this.catchBlocks = catchBlocks;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitTryCatchStmt(this);
		}

		public final Stmt tryBlock;
		public final List<Catch> catchBlocks;
	}
	public static class Catch extends Stmt {
		public Catch(Token exceptionType, Token variable, Stmt block) {
			this.exceptionType = exceptionType;
			this.variable = variable;
			this.block = block;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitCatchStmt(this);
		}

		public final Token exceptionType;
		public final Token variable;
		public final Stmt block;
	}

	public abstract <R> R accept(Visitor<R> visitor);
}
