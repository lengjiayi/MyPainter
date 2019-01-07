import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class zoomWindow extends JPanel {
    private myCanvas canvas;
    private Point startloc;
    private Dimension size;
    private ImageIcon ScreenCapture;
    private Thumbnails tnail;
    private JSlider slider;
    public Point realstart;
    private int prop=100;
    public zoomWindow(myCanvas canvas)
    {
        this.canvas=canvas;
        setLayout(null);
        setBackground(Color.WHITE);
        realstart=new Point(0,0);
        tnail=new Thumbnails(this,null);
        add(tnail);
        slider=new JSlider(100,500,100);
        slider.setMajorTickSpacing(5);
        slider.setOpaque(false);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                prop=slider.getValue();
                tnail.setProp(null, prop);
            }
        });
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                zoomPaint();
            }
        });
        add(slider);
        //TODO create a thumbnail
    }

    private void cleanCanvas()
    {
        Graphics2D g2=(Graphics2D)getGraphics();
        if(g2==null)
            return;
        g2.setColor(Color.white);
        for(int i=0;i<getHeight();i++)
            g2.drawLine(0,i,getWidth(),i);
    }

    public void zoomPaint()
    {
        cleanCanvas();
        Point realsize=new Point(size.width*100/prop, size.height*100/prop);
        for(mShape x:canvas.shapelist)
        {
            x.drawInWindow(this,realstart,realsize);
        }
        slider.repaint();
        tnail.repaint();
    }
    public void setSC(ImageIcon sc)
    {
        startloc=canvas.getLocation();
        size=canvas.getSize();
        setSize(size);
        setLocation(startloc);
        tnail.setSize(size.width/6, size.height/6);
        tnail.setLocation(size.width*5/6, size.height*5/6);
        slider.setBounds(0,getHeight()-15,getWidth()/2,15);
        slider.setValue(0);
        this.ScreenCapture=sc; tnail.setBG(sc);
        zoomPaint();
    }
}

class Thumbnails extends JLabel
{
    private zoomWindow father;
    private ImageIcon background;
    private Rectangle2D.Float view;
    private BasicStroke bs;
    private Point start;
    private double prop=1;
    private Point pressPoint;
    public Thumbnails(zoomWindow zw, ImageIcon bg)
    {
        father=zw;
        Thumbnails tnail=this;
        background=bg;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                pressPoint=e.getPoint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point tmploc=e.getPoint();
                double dx=tmploc.x-pressPoint.x;
                double dy=tmploc.y-pressPoint.y;
                if(start.x+view.getWidth()+dx>getWidth())
                    dx=getWidth()-start.x-view.getWidth();
                if(start.y+view.getHeight()+dy>getHeight())
                    dy=getHeight()-start.y-view.getHeight();
                if(start.x+dx <0)
                    dx=-start.x;
                if(start.y+dy < 0)
                    dy=-start.y;
                start.x+=dx;
                start.y+=dy;
                view.setRect(start.x, start.y, view.getWidth(), view.getHeight());
                pressPoint=tmploc;
                father.realstart.x=start.x*6;
                father.realstart.y=start.y*6;
                tnail.repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                father.zoomPaint();
            }
        });
    }
    public void setBG(ImageIcon bg)
    {
        this.background=bg;
        start=new Point(0,0);
        setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        view=new Rectangle2D.Float(0,0,getWidth(),getHeight());
        bs=new BasicStroke(2);
        repaint();
    }
    public void setProp(Point s, int prop)
    {
        if(s!=null)
            this.start=s;
        dPoint size=new dPoint(getSize());
        size.x*=(double)100/prop;
        size.y*=(double)100/prop;
        view.setRect(start.x, start.y, size.x, size.y);
        repaint();
    }
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2=(Graphics2D) g;
        g2.drawImage(background.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH),0,0,this.getWidth(),this.getHeight(),this);
        g2.setStroke(bs);
        g2.setColor(Color.BLUE);
        g2.draw(view);
    }
}