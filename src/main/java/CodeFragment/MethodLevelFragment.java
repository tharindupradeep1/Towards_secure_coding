package CodeFragment;

import Parser.RealtimeParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javassist.compiler.ast.AssignExpr;
import com.github.javaparser.JavaParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MethodLevelFragment extends AnAction {

    public Map<Integer, ArrayList<Integer>> catchClause = new HashMap<Integer, ArrayList<Integer>>();
    public Map<Integer, ArrayList<Integer>> forCounter = new HashMap<Integer, ArrayList<Integer>>();


    public MethodLevelFragment(){
        JavaParser.getStaticConfiguration().setAttributeComments(false);
        RealtimeParser.getInstance();
        CompilationUnit cu = JavaParser.parse(RealtimeParser.editorText);


        VoidVisitor<Map<Integer, ArrayList<Integer>>> CatchClauseVisitor = new CatchClauseVisitor();
        CatchClauseVisitor.visit(cu, catchClause);

        BlockVisitor BlockVisitor = new BlockVisitor();
        BlockVisitor.visit(cu, forCounter);

    }

    private static class CatchClauseVisitor extends VoidVisitorAdapter<Map<Integer, ArrayList<Integer>>> {
        int x = 1;
        int line, column, end;

        @Override
        public void visit(com.github.javaparser.ast.stmt.CatchClause n, Map<Integer, ArrayList<Integer>> collector) {
            super.visit(n, collector);
            if (String.valueOf(n.getParameter().getType()).equals("NullPointerException") || String.valueOf(n.getParameter().getType()).equals("Exception") || String.valueOf(n.getParameter().getType()).equals("Throwable")) {
                line = n.getParameter().getType().getBegin().get().line;
                column = n.getParameter().getType().getBegin().get().column;
                end = n.getParameter().getType().getEnd().get().column;
                collector.put(x, new ArrayList<Integer>() {{
                    add(line);
                    add(column);
                    add(end);
                }});
                x++;
            }
        }
    }

    private static class BlockVisitor extends VoidVisitorAdapter<Map<Integer, ArrayList<Integer>>> {
        int x = 1;
        int line,column,end;
        @Override
        public void visit(ForStmt n, Map<Integer, ArrayList<Integer>> collector) {
            super.visit(n, collector);
            if (String.valueOf(n.getInitialization().get(0).getChildNodes().get(0).getChildNodes().get(0)).equals("float")) {
                line = n.getInitialization().get(0).getChildNodes().get(0).getChildNodes().get(0).getBegin().get().line;
                column = n.getInitialization().get(0).getChildNodes().get(0).getChildNodes().get(0).getBegin().get().column;
                end = n.getInitialization().get(0).getChildNodes().get(0).getChildNodes().get(0).getEnd().get().column;
                collector.put(x,new ArrayList<Integer>() {{
                    add(line);
                    add(column);
                    add(end);}});
                x++;
            }
        }
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
    }
}
