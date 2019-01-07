import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class mLine extends mShape{
    public mLine(myCanvas c, Point start)
    {
        canvas=c;
        painttype=0;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[2];
        optlist=new optBall[2];
        for(int i=0;i<pointsrequred;i++)
            optlist[i]=new optBall(this);
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        outlook=new mLineOutlook(this,start);
        canvas.add(outlook,0);
        addPoint(start);
    }

    @Override
    public void update(Point newloc) {
        mLineOutlook ll=(mLineOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }

    @Override
    public boolean Isin(Point loc) {
        return ((mLineOutlook)outlook).Isin(loc);
    }

    @Override
    public void relocate(optBall opt)
    {
        mLineOutlook ll=(mLineOutlook)outlook;
        Point a=new Point(optlist[0].getLocation());
        Point b=new Point(optlist[1].getLocation());
        a.x+=5;
        a.y+=5;
        b.x+=5;
        b.y+=5;
        ll.update(a, b);
    }
    @Override
    public void drawInWindow(zoomWindow zw, Point start, Point size) {
        mCutWindowHelper.drawLine(zw,start,size,optlist[0].getLocation(), optlist[1].getLocation(),LineWidth,color);
    }
}

class mLineOutlook extends JLabel {
    public Point start, end;
    private mLine father;

    public mLineOutlook(mLine f, Point s) {
        father=f;
        this.setLocation(s);
        this.setBackground(new Color(0,0,0,0));
        start = s;
        end = new Point(s.x + 1, s.y + 1);
        this.setSize(1, 1);
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
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int LW=father.LineWidth;
        if(father.hightligh) {
            LW++;
            g2.setColor(Color.BLUE);
        }else {
            g2.setColor(father.color);
        }
        if((start.x -end.x) * (start.y - end.y)>=0)
            mGAlgorithm.drawLine(g2,mShape.vwidth/2,mShape.vwidth/2,getWidth()-mShape.vwidth/2,getHeight()-mShape.vwidth/2, LW);
//            g2.drawLine(mShape.vwidth/2,mShape.vwidth/2,getWidth()-mShape.vwidth/2,getHeight()-mShape.vwidth/2);
        else
            mGAlgorithm.drawLine(g2,mShape.vwidth/2,getHeight()-mShape.vwidth/2,getWidth()-mShape.vwidth/2,mShape.vwidth/2, LW);
//          g2.drawLine(mShape.vwidth/2,getHeight()-mShape.vwidth/2,getWidth()-mShape.vwidth/2,mShape.vwidth/2);
    }
    public boolean Isin(Point loc) {
        Point a=father.optlist[0].getLocation();
        Point b=father.optlist[1].getLocation();
        a.x+=5; a.y+=5; b.x+=5; b.y+=5;
        return mShape.Isinline(a,b,loc);
    }
}

/*
    private Point[] ps;
    @Override
    public void drawInWindow(Graphics2D g2, Point start, Point size) {
        if(g2!=null && ps!=null) {
            g2.setColor(Color.WHITE);
            mGAlgorithm.drawLine(g2, ps[0].x, ps[0].y, ps[1].x, ps[1].y, LineWidth);
            g2.setColor(color);
        }
        Point p1, p2;
        if(optlist[0].getLocation().x > optlist[1].getLocation().x)
        {
            p1=optlist[1].getLocation();
            p2=optlist[0].getLocation();
        }
        else
        {
            p1=optlist[0].getLocation();
            p2=optlist[1].getLocation();
        }
        ps=mGAlgorithm.drawLineInWindow(start, size, p1, p2);
        if(g2!=null && ps!=null)
            mGAlgorithm.drawLine(g2, ps[0].x, ps[0].y, ps[1].x, ps[1].y, LineWidth);
    }
*/