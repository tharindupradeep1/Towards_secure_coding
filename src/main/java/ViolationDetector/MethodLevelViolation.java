package ViolationDetector;

import CodeFragment.MethodLevelFragment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static ViolationDetector.ViolationDetector.lce;

public class MethodLevelViolation extends AnAction {

    String rule1Detection;
    String valueNum09J = "";

    public String rule1Detection(){
        try {
            rule1Detection=detectViolationNUM09J();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rule1Detection;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

    public String detectViolationNUM09J() throws Exception {
        MethodLevelFragment cc = new MethodLevelFragment();
        if (!cc.forCounter.isEmpty()) {
            ArrayList<Integer> lines = new ArrayList<Integer>();
            ArrayList<Integer> columns = new ArrayList<Integer>();
            ArrayList<Integer> ends = new ArrayList<Integer>();
            for (int i = 1; i < cc.forCounter.size()+1; i++) {
                lines.add(cc.forCounter.get(i).get(0));
                columns.add(cc.forCounter.get(i).get(1));
                ends.add(cc.forCounter.get(i).get(2));
            }
            lce.put("method_rule1_line", lines);
            lce.put("method_rule1_column", columns);
            lce.put("method_rule1_end", ends);
            valueNum09J = "NUM09-J --> Using floating point variables as loop counters is a secure coding guideline violation. " + lines;
            return valueNum09J;
        } else {
            valueNum09J = "";
            return valueNum09J;
        }
    }
}
