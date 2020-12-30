import javax.swing.*;
import java.awt.event.*;

public class DropletEditor extends JDialog {
    private SwingFXWebView contentPane;

    public DropletEditor() {
        contentPane = new SwingFXWebView();
        setContentPane(contentPane);
//        Browser browser = new Browser();
//        BrowserView view = new BrowserView(browser);
//        contentPane.add(view, BorderLayout.CENTER);
//        browser.loadURL("http://www.google.com");
        //        setModal(true);
//        contentPane.setLayout(new BorderLayout());
//        contentPane.add(new SwtBrowserCanvas(), BorderLayout.CENTER);

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
// add your code here if necessary
        contentPane.setVisible(false);
        dispose();
    }

    public static void main(String[] args) {
        DropletEditor dialog = new DropletEditor();
        dialog.pack();
        dialog.setVisible(true);
//        System.exit(0);
    }
}
