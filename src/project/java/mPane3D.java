import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class mPane3D extends JPanel {
    private model3D model;
    private String filename;
    private Point presspoint = null;
    private KeyAdapter kadp;
    public mPane3D()
    {
        setBackground(Color.lightGray);

        setTransferHandler(new TransferHandler()
        {
            @Override
            public boolean importData(JComponent comp, Transferable t)
            {
                try {
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

                    String filepath = o.toString();
                    if (filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length() - 1);
                    }
                    int index=0;
                    for(int i=0;i<filepath.length();i++)
                        if(filepath.charAt(i)=='\\')
                            index=i;
                    String fPath=filepath.substring(0, index+1);
                    String fName=filepath.substring(index+1);
                    System.out.println(fPath+fName);
                    filename = fPath + fName;
                    setmodel();
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for (int i = 0; i < flavors.length; i++) {
                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });

        kadp = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_LEFT:
                        if(model != null)
                            model.yrt += 0.5;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(model != null)
                            model.yrt -=0.5;
                        break;
                    case KeyEvent.VK_UP:
                        if(model != null)
                            model.xrt +=0.5;
                        break;
                    case KeyEvent.VK_DOWN:
                        if(model != null)
                            model.xrt -=0.5;
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if(model != null)
                            model.zrt += 0.5;
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if(model != null)
                            model.zrt -= 0.5;
                        break;
                    case KeyEvent.VK_Z:
                        if(model != null)
                            model.scalerate *= 1.2;
                        break;
                    case KeyEvent.VK_X:
                        if(model != null)
                            model.scalerate /= 1.2;
                        break;
                    case KeyEvent.VK_W:
                        if(model != null)
                            model.ymov -= 0.2;
                        break;
                    case KeyEvent.VK_S:
                        if(model != null)
                            model.ymov += 0.2;
                        break;
                    case KeyEvent.VK_A:
                        if(model != null)
                            model.xmov += 0.2;
                        break;
                    case KeyEvent.VK_D:
                        if(model != null)
                            model.xmov -= 0.2;
                        break;
                }
            }
        };
    }
    public void setmodel()
    {
        this.removeAll();
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        model = new model3D(filename);
        glcanvas.addGLEventListener(model);
        glcanvas.addKeyListener(kadp);
        glcanvas.setSize(getWidth(), getHeight());
        add(glcanvas);
        FPSAnimator animator = new FPSAnimator(glcanvas, 60, true);
        animator.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        frame.setLayout(null);
        frame.setBounds(0,0,500,500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mPane3D pane = new mPane3D();
        pane.setBounds(50,50,200,200);
        pane.setFocusable(true);
        frame.add(pane);
        frame.setVisible(true);
    }
}
