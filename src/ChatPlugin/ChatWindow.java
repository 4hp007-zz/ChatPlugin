package ChatPlugin;

import java.io.*;
import java.util.logging.*;
import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.peers.*;
import org.gudy.azureus2.plugins.sharing.*;
import org.gudy.azureus2.plugins.torrent.*;

public class ChatWindow {

    public Shell shell;
    private Text text;
    private Browser browser;
    private final FileHandle f;
    private final Peer p;
    private final PluginInterface pi;

    public ChatWindow(Peer p, FileHandle f, PluginInterface pi) {

        this.p = p;
        this.f = f;
        this.pi = pi;

    }

    public Peer peer() {
        return p;
    }

    public void run(Shell parent, String msg) {

        shell = new Shell(parent, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText(p.getIp());
        Menu menu = new Menu(shell, SWT.BAR);
        MenuItem cFileMenu = new MenuItem(menu, SWT.CASCADE);
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cFileMenu.setMenu(fileMenu);
        cFileMenu.setText("&File");
        MenuItem sendFile = new MenuItem(fileMenu, SWT.PUSH);
        sendFile.setText("Send &File...");
        MenuItem sendDir = new MenuItem(fileMenu, SWT.PUSH);
        sendDir.setText("Send &Directory...");
        MenuItem exit = new MenuItem(fileMenu, SWT.PUSH);
        exit.setText("E&xit");
        MenuItem cEditMenu = new MenuItem(menu, SWT.CASCADE);
        Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
        cEditMenu.setMenu(editMenu);
        cEditMenu.setText("&Edit");
        MenuItem clear = new MenuItem(editMenu, SWT.PUSH);
        clear.setText("Delete &History");
        MenuItem deleteShare = new MenuItem(editMenu, SWT.PUSH);
        deleteShare.setText("Delete &Share");
        shell.setMenuBar(menu);
        GridLayout outer = new GridLayout(2, false);
        shell.setLayout(outer);
        browser = new Browser(shell, SWT.BORDER | SWT.WRAP);
        GridData griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalSpan = 2;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = SWT.FILL;
        browser.setLayoutData(griddata);
        text = new Text(shell, SWT.BORDER);
        griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        text.setLayoutData(griddata);
        text.setFocus();
        Button btnSend = new Button(shell, SWT.PUSH);
        btnSend.setText("SEND");
        griddata = new GridData();
        griddata.horizontalAlignment = SWT.RIGHT;
        btnSend.setLayoutData(griddata);        
        FileDialog file = new FileDialog(shell, SWT.OPEN);
        DirectoryDialog dir = new DirectoryDialog(shell);
        exit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });

        deleteShare.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                try {
                    ShareResource[] sr = pi.getShareManager().getShares();
                    for (ShareResource i : sr) {
                        i.delete(true);
                    }
                } catch (ShareException ex) {
                    Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        sendFile.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent se) {

                try {
                    file.setText("Open Torrent");
                    String temp = file.open();

                    if (temp != null) {
                        Torrent out = pi.getShareManager().addFile(new File(temp)).getItem().getTorrent();

                        send("<a href=\"" + out.getMagnetURI().toString() + "\">" + out.getName() + "</a>");
                    }
                } catch (ShareException | TorrentException ex) {
                    Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        sendDir.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                try {
                    dir.setText("Select Directory");

                    dir.setMessage("Select a dirtectory to send");
                    String temp = dir.open();
                    if (temp != null) {
                        Torrent out = pi.getShareManager().addDir(new File(temp)).getItem().getTorrent();
                        send("<a href=\"" + out.getMagnetURI().toString() + "\">" + out.getName() + "</a>");
                    }
                } catch (TorrentException | ShareException ex) {
                    Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
        clear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                f.insert(p.getIp(), "");
                browser.refresh();
            }
        }
        );

        browser.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent de) {
                f.insert(p.getIp(), browser.getText());
            }
        }
        );
        btnSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String input = text.getText();
                if (input != null && input.length() > 0)
                    send(input);
            }
        }
        );
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.keyCode == 13) {
                    String input = text.getText();
                    if (input != null && input.length() > 0)
                        send(input);
                }
            }
        }
        );
        browser.setText(f.read(p.getIp()));
        shell.setSize(450, 300);
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

        browser.setText(browser.getText() + "<font color=\'red\'>me</font>: " + str + "<br>");
        text.setText("");
        p.getConnection().getOutgoingMessageQueue().sendMessage(new NewMessage(str));
    }

    public void update(final String str) {

        if (shell == null || shell.isDisposed() || str == null || str.equals(""))
            return;
        shell.getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (str.equals("code:21215311"))
                    browser.setText("<font color=\'red\'>*Sorry, you've been blocked by the user*</font><br>");
                else
                    browser.setText(browser.getText() + "<font color=\'blue\'>" + p.getIp() + "</font>: " + str + "<br>");
            }
        });
    }
}
