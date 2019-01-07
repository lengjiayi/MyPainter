import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class mCurveR3 extends mShape{
    public mCurveR3(myCanvas c, Point start)
    {
        canvas=c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[3];
        optlist=new optBall[3];
        for(int i=0;i<optlist.length;i++)
            optlist[i]=new optBall(this);
        optlist[2].setLocation(-1,-1);
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        outlook=new mCurveR3Outlook(this,start);
        canvas.add(outlook,0);
        addPoint(start);
    }
    @Override
    public void update(Point newloc) {
        mCurveR3Outlook ll=(mCurveR3Outlook)outlook;
        ll.update(pointlist[0], newloc);
    }

    @Override
    public boolean Isin(Point loc) {
        return ((mCurveR3Outlook)outlook).Isin(loc);
    }

    @Override
    public void set()
    {
        for(int i=0;i<optlist.length;i++)
            canvas.remove(optlist[i]);
        canvas.repaint();
        if(optlist[2].getLocation().x<0)
        {
            Point middle=new Point();
            middle.x=(optlist[0].getLocation().x+optlist[1].getLocation().x)/2;
            middle.y=(optlist[0].getLocation().y+optlist[1].getLocation().y)/2;
            optlist[2].setLocation(middle);
            optlist[2].setColor(Color.ORANGE);
        }
    }

    @Override
    public void relocate(optBall opt)
    {
        mCurveR3Outlook ll=(mCurveR3Outlook)outlook;
        Point a=new Point(optlist[0].getLocation());
        Point b=new Point(optlist[1].getLocation());
        a.x+=5;
        a.y+=5;
        b.x+=5;
        b.y+=5;
        ll.update(a, b);
    }
}

class mCurveR3Outlook extends JLabel
{
    public Point start, end;
    private mCurveR3 father;

    public mCurveR3Outlook(mCurveR3 f, Point s) {
        father=f;
        this.setLocation(s);
        this.setBackground(new Color(0,0,0,0));
        start = s;
        end = new Point(s.x + 1, s.y + 1);
        this.setSize(mShape.vwidth+1, mShape.vwidth+1);
    }

    public static int max(int a, int b){ return a>b?a:b; }
    public static int min(int a, int b){ return a<b?a:b; }
    public void update(Point s, Point e) {
        start = s;
        end = e;
        if(father.premain>0) {
            setLocation(min(start.x, end.x) - mShape.vwidth / 2, min(start.y, end.y) - mShape.vwidth / 2);
            int w = Math.abs(end.x - start.x) + mShape.vwidth;
            int h = Math.abs(end.y - start.y) + mShape.vwidth;
            setSize(w, h);
            return;
        }
        Point lu, rb;
        lu=father.optlist[0].getLocation();
        lu.x+=5;
        lu.y+=5;
        rb=new Point(lu.x, lu.y);
        for(optBall x:father.optlist)
        {
            Point tmploc=x.getLocation();
            tmploc.x+=5;  tmploc.y+=5;
            if(tmploc.x<lu.x)
                lu.x=tmploc.x;
            if(tmploc.x>rb.x)
                rb.x=tmploc.x;
            if(tmploc.y<lu.y)
                lu.y=tmploc.y;
            if(tmploc.y>rb.y)
                rb.y=tmploc.y;
        }
        setLocation(lu.x-mShape.vwidth/2, lu.y-mShape.vwidth/2);
        setSize(rb.x-lu.x+mShape.vwidth,rb.y-lu.y+mShape.vwidth);
        repaint();
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
        Point p[]=new Point[3];
        for(int i=0;i<3;i++)
        {
            p[i]=father.optlist[i].getLocation();
            p[i].x+=5-getLocation().x;
            p[i].y+=5-getLocation().y;
        }
        if(p[2].x<0)
        {
            if((start.x -end.x) * (start.y - end.y)>=0)
                mGAlgorithm.drawLine(g2,mShape.vwidth/2,mShape.vwidth/2,getWidth()-mShape.vwidth/2,getHeight()-mShape.vwidth/2, LW);
            else
                mGAlgorithm.drawLine(g2,mShape.vwidth/2,getHeight()-mShape.vwidth/2,getWidth()-mShape.vwidth/2,mShape.vwidth/2, LW);
        }
        else {
            Point lu, rb;
            lu=new Point(min(father.optlist[0].getLocation().x,father.optlist[1].getLocation().x),min(father.optlist[0].getLocation().y,father.optlist[1].getLocation().y));
            rb=new Point(max(father.optlist[0].getLocation().x,father.optlist[1].getLocation().x),max(father.optlist[0].getLocation().y,father.optlist[1].getLocation().y));
            lu.x+=5-getLocation().x;
            lu.y+=5-getLocation().y;
            rb.x+=5-getLocation().x;
            rb.y+=5-getLocation().y;
            if((start.x -end.x) * (start.y - end.y)>=0)
                mGAlgorithm.drawCurveR3(g2,lu.x,lu.y,p[2].x, p[2].y, rb.x,rb.y, LW);
            else
                mGAlgorithm.drawCurveR3(g2,lu.x,rb.y,p[2].x, p[2].y, rb.x,lu.y, LW);
        }
    }
    public boolean Isin(Point loc) {
        Point a=father.optlist[0].getLocation();
        Point b=father.optlist[1].getLocation();
        a.x+=5; a.y+=5; b.x+=5; b.y+=5;
        return mShape.Isinline(a,b,loc);
    }
}