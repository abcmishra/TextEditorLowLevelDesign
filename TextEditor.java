
import java.util.Deque;
import java.util.*;

public class TextEditor {
    private List<String> lines;
    private List<String> clipboard;
    private Deque<Action> undoStack;
    private Deque<Action> redoStack;

    public TextEditor(){
       lines= new ArrayList<>();
       clipboard= new ArrayList<>();
       undoStack = new ArrayDeque<>();
       redoStack= new ArrayDeque<>();
    }

    public void display(){
        for(int i=0;i<lines.size();i++){
            System.out.println((i+1)+":"+lines.get(i));
        }
    }

    public void display(int n,int m){
        for(int i=n-1;i<m && i<lines.size();i++)
            System.out.println((i+1)+":"+lines.get(i));
    }

    public void insert(int n, String text){
        if(n>lines.size()+1)
            throw new IllegalArgumentException("Invalid line number");
        undoStack.push(new Action("insert",n,null,text) );
        redoStack.clear();
        lines.add(n-1,text);

    }
    public void delete(int n){
        if(n<1 || n>lines.size()){
            throw  new IllegalArgumentException("Invalid line number");
        }
        String removed=lines.remove(n-1);
        undoStack.push(new Action("deleted",n,removed,null));
        redoStack.clear();
    }
   public void delete(int n , int m){
        if(n<1 || m>lines.size() || n>m)
            throw  new IllegalArgumentException("Invalid line number");
        List<String> removedLines= new ArrayList<>(lines.subList(n-1,m));
        for(int i=0;i<m-n+1;i++)
            lines.remove(i);
        undoStack.push(new Action("delete_range",n,removedLines,null));
        redoStack.clear();
   }
   public void copy(int n,int m){
        if(n<1 || m>lines.size())
            throw new IllegalArgumentException("Invalid line number");
        clipboard.clear();
        clipboard.addAll(lines.subList(n-1,m));
   }

   public void paste(int n){
        if(n>lines.size()+1)
            throw new IllegalArgumentException("Invalid line number");
        undoStack.push(new Action("paste",n,null,clipboard));
        redoStack.clear();
        lines.addAll(n-1,clipboard);
   }

   public void undo(){
        if(undoStack.isEmpty()){
            System.out.println("Nothing to empty");
            return;
        }
        Action lastAction=undoStack.pop();
        redoStack.push(lastAction);
        switch (lastAction.type)
        {
            case "insert":
                lines.remove(lastAction.line-1);
                break;
            case "delete":
                lines.remove(lastAction.line-1,lastAction.oldContent);
                break;
            case "delete_range":
                lines.addAll(lastAction.line-1,lastAction.oldLines);
                break;
            case "paste":
                for(int i=0;i<lastAction.newLines.size();i++)
                    lines.remove(lastAction..line-1);
                break;

        }

   }

   public void redo(){
        if(redoStack.isEmpty()){
            System.out.println("Nothing to redo");
            return ;
        }
        Action lastRedo= redoStack.pop();
        undoStack.push(lastRedo);
        switch (lastRedo.type){
            case "insert":
                lines.add(lastRedo.line-1,lastRedo.newContent);
                break;
            case "delete":
                lines.remove(lastRedoline.line-1);
                break;
            case "delete_range":
                for(int i=0;i<lastRedo.oldLines.size();i++)
                    lines.remove(lastRedo.line-1);
                break;
            case "paste":
                lines.addAll(lastRedo.line-1,lastRedo.newLines);
                break;

        }
   }


    // Action class to store undo/redo operations
    private static class Action {
        String type;
        int line;
        String oldContent; // For single-line changes
        String newContent;
        List<String> oldLines; // For multi-line changes
        List<String> newLines;

        Action(String type, int line, String oldContent, Object content) {
            this.type = type;
            this.line = line;
            if (content instanceof String) {
                this.newContent = (String) content;
            } else if (content instanceof List) {
                this.newLines = new ArrayList<>((List<String>) content);
            }
            if (oldContent != null) {
                this.oldContent = oldContent;
            }
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();

        // Initial operations
        editor.insert(1, "Hello, World!");
        editor.insert(2, "This is a simple text editor.");
        editor.display();

        // Delete a line
        editor.delete(1);
        editor.display();

        // Copy and paste
        editor.copy(1, 1);
        editor.paste(1);
        editor.display();

        // Undo and redo
        editor.undo();
        editor.display();
        editor.redo();
        editor.display();
    }
}
}
