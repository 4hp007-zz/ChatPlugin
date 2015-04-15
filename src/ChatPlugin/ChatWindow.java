package ChatPlugin;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.gudy.azureus2.plugins.peers.*;

public class ChatWindow {

    public Shell shell;
    private Text text;
    private StyledText styledText;
    private final FileHandle f;
    private final Peer p;

    public ChatWindow(Peer p, FileHandle f) {

        this.p = p;
        this.f = f;

    }

    public Peer peer() {
        return p;
    }

    public void run(Shell parent, String msg) {

        shell = new Shell(parent, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText(p.getIp());
        GridLayout outer = new GridLayout(2, false);
        shell.setLayout(outer);
        styledText = new StyledText(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        styledText.setEditable(false);
        GridData griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalSpan = 2;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = SWT.FILL;
        styledText.setLayoutData(griddata);
        text = new Text(shell, SWT.BORDER);
        griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        text.setLayoutData(griddata);
        Button btnSend = new Button(shell, SWT.PUSH);
        btnSend.setText("SEND");
        griddata = new GridData();
        griddata.horizontalAlignment = SWT.RIGHT;
        btnSend.setLayoutData(griddata);
        shell.setSize(450, 300);
        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent de) {
                f.insert(p.getIp(), styledText.getText());
            }
        });
        btnSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String input = text.getText();
                if (input != null && input.length() > 0)
                    send(input);
            }
        });
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.keyCode == 13) {
                    String input = text.getText();
                    if (input != null && input.length() > 0)
                        send(input);
                }
            }
        });
        styledText.setText(f.read(p.getIp()));
        shell.open();
        shell.layout();
        if (msg != null)
            update(msg);
        while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch()) {
                shell.getDisplay().sleep();
            }
        }

        shell.dispose();

    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    public void send(String str) {

        styledText.append("me: " + str + "\n");
        text.setText("");
        p.getConnection().getOutgoingMessageQueue().sendMessage(new NewMessage(str));
    }

    public void update(final String str) {

        if (shell == null || shell.isDisposed() || str == null || str.equals(""))
            return;
        shell.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (str.equals("code:"))
                    styledText.append("*Sorry, you've been blocked by the user*\n");
                else
                    styledText.append(p.getIp() + ": " + str + "\n");
            }
        });
    }
}
