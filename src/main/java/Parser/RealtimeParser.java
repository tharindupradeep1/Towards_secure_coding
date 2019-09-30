package Parser;

import CodeFragment.MethodLevelFragment;
import Tool.CursorListener;
import Tool.SyntaxHighlight;
import ViolationDetector.MethodLevelViolation;
import com.intellij.codeInsight.hint.TooltipController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static ViolationDetector.ViolationDetector.lce;

public class RealtimeParser extends AnAction {
    public static String editorText;
    static String rule1; //method level


    private static RealtimeParser instance;

    public static RealtimeParser getInstance(){
        if(instance == null){
            instance = new RealtimeParser();
        }
        return instance;
    }

    static {
        final EditorActionManager actionManager = EditorActionManager.getInstance();
        final TypedAction typedAction = actionManager.getTypedAction();
        TypedActionHandler handler = typedAction.getHandler();
        typedAction.setupHandler(new MyTypedHandler(handler));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
    }

    public static class MyTypedHandler implements TypedActionHandler {
        JPanel myToolWindowContent = new JPanel();
        Box box = Box.createVerticalBox();

        JLabel title = new JLabel();
        private final TypedActionHandler handler;

        public MyTypedHandler(TypedActionHandler handler) {
            this.handler = handler;
        }
        @Override
        public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
            handler.execute(editor, c, dataContext);
            Project project = editor.getProject();
            Document document = editor.getDocument();
            editorText= document.getText();
            TooltipController tooltipController = new TooltipController();
            editor.addEditorMouseMotionListener(new CursorListener(tooltipController));

            JScrollPane scrollArea = new JBScrollPane(box, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollArea.add(myToolWindowContent);

            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Secure Coding Plugin");
            SyntaxHighlight syntaxhighlighter = new SyntaxHighlight();
            syntaxhighlighter.annotateoffsets.clear();
            syntaxhighlighter.tooltips.clear();
            this.cleartoolwindow(toolWindow);
            this.removeAllHighlighters(editor);
            try {
                MethodLevelViolation methodLevelDetector= new MethodLevelViolation();
                MethodLevelFragment methodLevel= new MethodLevelFragment();
               // ClassLevelCodeFragment classLevel = new ClassLevelCodeFragment();
               // PackageLevelCodeFragment packageLevel = new PackageLevelCodeFragment();

                myToolWindowContent.setLayout(new GridLayout(1,5));
                myToolWindowContent.setAutoscrolls(true);
                box.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
                JLabel boxHeading = new JLabel("<html><h3><font color='black'>Violated Rules</font><h3></html>");
                boxHeading.setHorizontalAlignment(JLabel.CENTER);
                boxHeading.setBackground(JBColor.ORANGE);
                box.add(boxHeading);

                if (true) {
                    rule1 = methodLevelDetector.rule1Detection();
                    if(!rule1.equals("")){

                        String ruleDis = "Do not use floating-point variables as loop counters";
                        syntaxhighlighter.highlight(editor, document, lce.get("method_rule1_line"), lce.get("method_rule1_column"), lce.get("method_rule1_end"),ruleDis);
                        methodLevel.forCounter.clear();
                        JLabel jLabel = this.createJLabel(rule1, ruleDis);
                        String url = "https://wiki.sei.cmu.edu/confluence/display/java/NUM09-J.+Do+not+use+floating-point+variables+as+loop+counters";
                        JLabel link = new JLabel("more details");
                        link.setHorizontalAlignment(JLabel.CENTER);
                        box.add(jLabel);
                        box.add(link);
                        setLinkToRule(link,url);
                    }
                    methodLevel.forCounter.clear();
                    myToolWindowContent.add(box);
                    //myToolWindowContent.setBackground(Color.blue);
                    this.addtotoolwindow(toolWindow);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        public JLabel createJLabel(String rule, String tooltip){
            JLabel jp = new JLabel();
            jp.setText("<html><br><font color='red'>" + rule + "</font><br></html>");
            jp.setToolTipText(tooltip);
            return jp;
        }



        public void addtotoolwindow(ToolWindow toolWindow){
            title.setLayout(new FlowLayout(FlowLayout.CENTER));
            myToolWindowContent.add(title);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(myToolWindowContent, "", true);
            toolWindow.getContentManager().addContent(content);
        }

        public void cleartoolwindow(ToolWindow toolWindow){
            toolWindow.getContentManager().removeAllContents(true);
            box.removeAll();
            title.removeAll();
            myToolWindowContent.remove(box);
            myToolWindowContent.remove(title);
        }

        public void setLinkToRule(JLabel link,String url){

            link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            link.setToolTipText("Click below link for more details...");
            link.setForeground(Color.BLUE);
            Font font = link.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            link.setFont(font.deriveFont(attributes));
            link.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton()>0) {
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                URI uri = new URI(url);
                                desktop.browse(uri);
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            } catch (URISyntaxException ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else {
                            System.out.println("Desktop not supported.");
                        }

                        System.out.print(e.getClickCount());
                    }
                }
            });
        }


        public void removeAllHighlighters(Editor editor){
            editor.getMarkupModel().removeAllHighlighters();
        }
    }
}
