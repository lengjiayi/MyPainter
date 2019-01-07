import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

class reformBall extends JLabel {
    protected Shape shape;
    protected Shape border;
    protected Point pressPoint=null;
    protected Color color=new Color(106,171,218);
    protected boolean movable=true;
    ImagePane father;
    public reformBall(ImagePane f)
    {
        father=f;
        setBackground(new Color(0,0,0,0));
        setSize(10,10);
        shape=new Ellipse2D.Float(1.5f,1.5f,7,7);
        border=new Ellipse2D.Float(0,0,10,10);
        this.setVisible(true);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(movable)
                    bedragged(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(movable)
                    pressPoint=e.getPoint();      //记录鼠标按下的位置，用于实现拖动效果
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(movable)
                    father.deform();
            }
        });
    }
    public void NotMovable()
    {
        this.movable=false;
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.fill(border);
        g2.setColor(color);
        g2.fill(shape);
    }

    protected void bedragged(MouseEvent e)      //实现拖动效果
    {
        if(pressPoint==null)
            return;
        Point toloc=e.getPoint();
        Point nowloc=getLocation();
        Point nextloc=new Point(nowloc.x+toloc.x-pressPoint.x,nowloc.y+toloc.y-pressPoint.y);
        setLocation(nextloc.x,nextloc.y);
    }
}
