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
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = "F:\\Pro Documents\\Low Level Code\\repos\\jlang\\src\\ast";
        defineAst(outputDir, "Expr", Arrays.asList(
            "Assign : Token name, Expr value",
        "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Object value",
            "Unary : Token operator, Expr right",
            "Block : List<Expr> statements",
            "Comma : Expr left, Expr right",
            "Ternary : Expr condition, Expr thenExpr, Expr elseExpr",
            "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
            "Expression : Expr expression",
            "Print : Expr expression",
            "Var : Token name, Expr initializer"

        ));

    }
}
