package ChatPlugin;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SWTDemo {

    public static void main(String[] args) {

        Display display = new Display();
        Shell shell = new Shell(display,SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("p.getIp()");
        GridLayout outer = new GridLayout(2, false);
        shell.setLayout(outer);        
        StyledText styledText = new StyledText(shell, SWT.BORDER|SWT.WRAP|SWT.V_SCROLL);
        styledText.setEditable(false);        
        GridData griddata = new GridData();
        griddata.horizontalAlignment = SWT.FILL;
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalSpan = 2;
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
        griddata.horizontalAlignment = SWT.RIGHT;
        snd.setLayoutData(griddata);        
        shell.setSize(450, 300);        
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}
