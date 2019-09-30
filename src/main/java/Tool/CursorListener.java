package Tool;

import com.intellij.codeInsight.hint.TooltipController;
import com.intellij.codeInsight.hint.TooltipGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;



public class CursorListener implements EditorMouseMotionListener {

    private TooltipController myTooltipController;
    private TooltipGroup SS_INFO_TOOLTIP_GROUP = new TooltipGroup("SS_INFO_TOOLTIP_GROUP", 1);

    public CursorListener(@NotNull TooltipController tooltipController) {
        myTooltipController = tooltipController;
    }

    @Override
    public void mouseMoved(@NotNull EditorMouseEvent e) {
        if (Registry.is("ide.disable.editor.tooltips")) {
            return;
        }
        Editor editor = e.getEditor();
        try {
            VisualPosition visual = editor.xyToVisualPosition(e.getMouseEvent().getPoint());
            if (editor.getSoftWrapModel().isInsideOrBeforeSoftWrap(visual)) {
                return;
            }
            LogicalPosition logical = editor.visualToLogicalPosition(visual);
            if (e.getArea() == EditorMouseEventArea.EDITING_AREA && !UIUtil.isControlKeyDown(e.getMouseEvent())) {
                int offset = editor.logicalPositionToOffset(logical);
                if (editor.offsetToLogicalPosition(offset).column != logical.column) return; // we are in virtual space
                if (editor.getInlayModel().getElementAt(e.getMouseEvent().getPoint()) != null) return;
                showtooltip(offset, editor, e);
            }
        }
        finally {
        }
    }

    @Override
    public void mouseDragged(@NotNull EditorMouseEvent e) {
        myTooltipController.cancelTooltips();
    }

    public void showtooltip(int offset, Editor editor,EditorMouseEvent e){
        SyntaxHighlight syntaxHighlighter= new SyntaxHighlight();
        if (!syntaxHighlighter.annotateoffsets.isEmpty()) {
            for (int i = 1; i < syntaxHighlighter.annotateoffsets.size() + 1; i++) {
                int start = syntaxHighlighter.annotateoffsets.get(i).get(0);
                int end = syntaxHighlighter.annotateoffsets.get(i).get(1);
                String tooltip = syntaxHighlighter.tooltips.get(i-1);
                if(offset>=start && offset<=end){
                    Point p = SwingUtilities
                            .convertPoint((Component) e.getMouseEvent().getSource(),
                                    e.getMouseEvent().getPoint(),
                                    editor.getComponent().getRootPane().getLayeredPane());
                    myTooltipController.showTooltip(editor, p, tooltip, false, SS_INFO_TOOLTIP_GROUP);
                }

            }
        }
    }
}

