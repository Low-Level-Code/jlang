package ast;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class GenerateAST {
    private static void defineType( PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("\tpublic static class " + className + " extends " + baseName + " {");
        // Constructor.
        writer.println("\t\tpublic " + className + "(" + fieldList + ") {");
        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }
        writer.println("\t\t}");
        // Visitor pattern.
        writer.println();
        writer.println("\t\t@Override");
        writer.println("\t\tpublic <R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" +className + baseName + "(this);");
        writer.println("\t\t}");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("\t\tpublic final " + field + ";");
        }
        writer.println("\t}");
    }
    private static void defineVisitor(
        PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tpublic interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit" + typeName + baseName + "(" +typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("\t}");
    }

    private static void defineAst( String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package ast;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println("import tokenizer.*;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        // the base accept method
        writer.println();
        writer.println("\tpublic abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Generate AST");
        String outputDir = "F:\\Pro Documents\\Low Level Code\\repos\\jlang\\src\\ast";
        
        defineAst(outputDir, "Expr", Arrays.asList(
            "Assign : Token name, Expr value, Token operator",
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Get : Expr object, Token name",
            "Call : Expr callee, Token paren, List<Expr> arguments",
            "Literal : Object value",
            "Array : List<Expr> elements",
            "JString : String value",
            "ArrayAccess : Expr name, Expr index",
            "Logical : Expr left, Token operator, Expr right",
            "Set : Expr object, Token name, Expr value",
            "Super : Token keyword, Token method",
            "This : Token keyword",
            "Unary : Token operator, Expr right",
            "Postfix : Expr left, Token operator",
            "Block : List<Expr> statements",
            "Comma : Expr left, Expr right",
            "Ternary : Expr condition, Expr thenExpr, Expr elseExpr",
            "Variable : Token name",
            "LambdaFunction : Token name, List<Token> params," +
                                    " List<Stmt> body",
            "AnonymousClass : Token name, List<Expr.Variable> parents, List<Stmt.Function> methods",
            "ObjectLiteral : List<Token> keys, List<Expr> values"

        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
            "Block : List<Stmt> statements", 
            "Class : Token name, List<Expr.Variable> parents," +
                    " List<Stmt.Function> methods",
            "Expression : Expr expression",
            "Function : Token name, List<Token> params," +
                                " List<Stmt> body",
            "If : Expr condition, Stmt thenBranch," +
                                " Stmt elseBranch",
            "Print : Expr expression",
            "Return : Token keyword, Expr value",
            "Var : Token name, Expr initializer",
            "Const : Token name, Expr initializer",
            "While : Expr condition, Stmt body",
            "Break : Token keyword",
            "Continue : Token keyword",
            "TryCatch : Stmt tryBlock, List<Catch> catchBlocks, Stmt finallyBlock",
            "Catch : Token exceptionType, Token variable, Stmt block"


        ));

    }
}
