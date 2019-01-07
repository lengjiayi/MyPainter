import javax.media.j3d.Canvas3D;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class m3DShape extends mShape {
    public m3DShape(myCanvas c, Point start)
    {
        OnlyResizeable = true;
        canvas = c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[2];
        optlist=new optBall[2];
        for(int i=0;i<pointsrequred;i++)
            optlist[i]=new optBall(this);
        outlook=new m3DShapeOutlook(this,start);
        canvas.add(outlook);
        addPoint(start);
    }
    @Override
    public void update(Point newloc) {
        m3DShapeOutlook ll=(m3DShapeOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }

    @Override
    public boolean Isin(Point loc) {
        return ((m3DShapeOutlook)outlook).Isin(loc);
    }

    @Override
    public void relocate(optBall opt)
    {
        m3DShapeOutlook ll=(m3DShapeOutlook) outlook;
        Point a=new Point(optlist[0].getLocation());
        Point b=new Point(optlist[1].getLocation());
        a.x+=5;
        a.y+=5;
        b.x+=5;
        b.y+=5;
        ll.update(a, b);
    }
}

class m3DShapeOutlook extends JLabel
{
    public Point start, end;
    private m3DShape father;
    private mPane3D pane3D;
    public m3DShapeOutlook(m3DShape r, Point s) {
        father=r;
        this.setLocation(s);
        this.setBackground(new Color(0,0,0,0));
        start = s;
        end = new Point(s.x + 1, s.y + 1);
        this.setSize(1, 1);
        setLayout(new BorderLayout());
        pane3D = new mPane3D();
        add(pane3D);
    }

    public static int max(int a, int b){ return a>b?a:b; }
    public static int min(int a, int b){ return a<b?a:b; }
    public void update(Point s, Point e) {
        start = s;
        end = e;
        setLocation(min(start.x, end.x)-mShape.vwidth/2, min(start.y, end.y)-mShape.vwidth/2);
        int w=Math.abs(end.x-start.x)+mShape.vwidth;
        int h=Math.abs(end.y-start.y)+mShape.vwidth;
        setSize(w, h);
        pane3D.setBounds(mShape.vwidth/2, mShape.vwidth/2, w-mShape.vwidth, h-mShape.vwidth);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int LW=father.LineWidth;
        if(father.hightligh) {
            LW+=2;
            g2.setColor(Color.BLUE);
        }else {
            g2.setColor(father.color);
        }
        mGAlgorithm.drawLine(g2,mShape.vwidth/2,mShape.vwidth/2,getWidth()-mShape.vwidth,mShape.vwidth/2, LW);
        mGAlgorithm.drawLine(g2,mShape.vwidth/2,mShape.vwidth/2,mShape.vwidth/2,getHeight()-mShape.vwidth, LW);
        mGAlgorithm.drawLine(g2,mShape.vwidth/2,getHeight()-mShape.vwidth,getWidth()-mShape.vwidth,getHeight()-mShape.vwidth, LW);
        mGAlgorithm.drawLine(g2,getWidth()-mShape.vwidth,mShape.vwidth/2,getWidth()-mShape.vwidth,getHeight()-mShape.vwidth, LW);
        super.paintComponent(g);
    }
    public boolean Isin(Point loc) {
        Point curloc=getLocation();
        loc.x-=curloc.x;
        loc.y-=curloc.y;
        if(loc.x<0 || loc.x>getWidth() || loc.y<0 || loc.y>getHeight())
            return false;
        if(loc.x<=mShape.vwidth || getWidth()-loc.x<=mShape.vwidth || loc.y<=mShape.vwidth || getHeight()-loc.y<=mShape.vwidth)
            return true;
        return false;
    }
}