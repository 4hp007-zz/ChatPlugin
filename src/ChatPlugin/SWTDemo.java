package ChatPlugin;

import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SWTDemo {

    public static void main(String[] args) {

        Display display = new Display();
        Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("p.getIp()");
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
        Menu editMenu = new Menu(shell,SWT.DROP_DOWN);        
        cEditMenu.setMenu(editMenu);
        cEditMenu.setText("&Edit");
        MenuItem clear = new MenuItem(editMenu, SWT.PUSH);
        clear.setText("Delete &History");
        shell.setMenuBar(menu);
        GridLayout outer = new GridLayout(3, false);
        shell.setLayout(outer);
        Browser styledText = new Browser(shell, SWT.BORDER | SWT.WRAP);
        //   styledText.setEditable(false);        
        GridData griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalSpan = 3;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = SWT.FILL;
        styledText.setLayoutData(griddata);
        Text text = new Text(shell, SWT.BORDER);
        griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        text.setLayoutData(griddata);
        Button snd = new Button(shell, SWT.PUSH);
        snd.setText("SEND");
        griddata = new GridData();
        //griddata.horizontalAlignment = SWT.RIGHT;
        snd.setLayoutData(griddata);
        Button opn = new Button(shell, SWT.PUSH);
        griddata = new GridData();
        opn.setLayoutData(griddata);
        opn.setText("Open");
        FileDialog open = new FileDialog(shell);
        DirectoryDialog dir = new DirectoryDialog(shell);
        exit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }            
        });
        
        sendFile.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent se) {

                String[] ext = {"*.torrent"};
                open.setFilterExtensions(ext);               
                open.setText("Open Torrent");
                String file = open.open();
                //      Torrent t = new Torrent
            }
        });
        sendDir.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                
                dir.setText("Select Directory");
                dir.setMessage("Select a dirtectory to send");
                dir.open();
                
            }

        
        });
        clear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                
                
            }

            
        });
        snd.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent se) {
                styledText.setText(styledText.getText() + text.getText());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent se) {
            }
        });
        shell.setSize(450, 300);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}
