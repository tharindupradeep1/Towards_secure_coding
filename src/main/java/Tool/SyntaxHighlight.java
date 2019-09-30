package Tool;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SyntaxHighlight {
    public static Map<Integer, ArrayList<Integer>> annotateoffsets = new HashMap<>();
    public static List<String> tooltips = new ArrayList<String>();

    public void highlight (Editor editor, Document document, ArrayList line, ArrayList column, ArrayList end, String tooltip){
        TextAttributes x = new TextAttributes();
        x.setBackgroundColor(Color.red);
        x.setEffectColor(Color.orange);
        x.setEffectType(EffectType.WAVE_UNDERSCORE);
        try{
            for (int i=0; i<line.size(); i++){
                int lineStartOffset = document.getLineStartOffset(Math.max(0, (Integer) line.get(i)-1)) + (Integer) column.get(i) - 1;
                int lineEndOffset = document.getLineStartOffset(Math.max(0, (Integer) line.get(i) -1)) + (Integer) end.get(i);
                annotateoffsets.put(annotateoffsets.size()+1, new ArrayList<Integer>() {{
                    add(lineStartOffset);
                    add(lineEndOffset);
                }});
                tooltips.add(tooltip);
                editor.getMarkupModel().addRangeHighlighter(
                        lineStartOffset, lineEndOffset,3333, x, HighlighterTargetArea.EXACT_RANGE
                );
            }
        }catch (Exception e){
        }
    }
}
