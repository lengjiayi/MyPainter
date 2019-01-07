import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class mCurveG extends mShape{
    public int Rank;
    public mCurveG(myCanvas c, Point start, int rank)
    {
        //rank为贝塞尔曲线阶数+1
        this.Rank=rank;
        canvas=c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[rank];
        optlist=new optBall[rank];
        for(int i=0;i<optlist.length;i++)
            optlist[i]=new optBall(this);
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        optlist[2].setLocation(-1,-1);
        outlook=new mCurveGOutlook(this,start);
        canvas.add(outlook,0);
        addPoint(start);
    }
    @Override
    public void update(Point newloc) {
        mCurveGOutlook ll=(mCurveGOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }

    @Override
    public boolean Isin(Point loc) {
        return ((mCurveGOutlook)outlook).Isin(loc);
    }

    @Override
    public void set()
    {
        for(int i=0;i<optlist.length;i++)
            canvas.remove(optlist[i]);
        canvas.remove(rcenter);
        canvas.remove(rball);
        canvas.repaint();
        if(optlist[2].getLocation().x<0)
        {
            for(int i=2;i<Rank;i++)
            {
                Point middle=new Point();
                middle.x=optlist[0].getLocation().x+(optlist[1].getLocation().x-optlist[0].getLocation().x)*(i-1)/(Rank-1);
                middle.y=optlist[0].getLocation().y+(optlist[1].getLocation().y-optlist[0].getLocation().y)*(i-1)/(Rank-1);
                optlist[i].setLocation(middle);
                optlist[i].setColor(Color.ORANGE);
            }
        }
    }

    @Override
    public void relocate(optBall opt)
    {
        mCurveGOutlook ll=(mCurveGOutlook)outlook;
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
        mCurveGOutlook ol=(mCurveGOutlook)outlook;
        ol.drawInzoomWindow(zw, start, size);
    /*
        Graphics2D g2=(Graphics2D)zw.getGraphics();
        if(g2==null)
            return;
        g2.setColor(fillcolor);
        for(int i=0;i<zw.getHeight();i++)
            for(int j=0;j<zw.getWidth();j++)
            {
                Point p=new Point(j,i);
                p.x=(int)(p.x*(double)size.x/zw.getWidth()+0.5);
                p.y=(int)(p.y*(double)size.y/zw.getHeight()+0.5);
                p.x+=start.x;
                p.y+=start.y;
                p.x-=ol.getLocation().x;
                p.y-=ol.getLocation().y;
                if(ol.IsinAcc(p))
                    mGAlgorithm.drawPoint(g2, j, i);
            }*/
    }
}

class mCurveGOutlook extends JLabel
{
    public Point start, end;
    private mCurveG father;

    public mCurveGOutlook(mCurveG f, Point s) {
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
        Point p[]=new Point[father.Rank];
        p[0]=father.optlist[0].getLocation();
        p[0].x+=5-getLocation().x;
        p[0].y+=5-getLocation().y;
        p[father.Rank-1]=father.optlist[1].getLocation();
        p[father.Rank-1].x+=5-getLocation().x;
        p[father.Rank-1].y+=5-getLocation().y;
        for(int i=1;i<father.Rank-1;i++)
        {
            p[i]=father.optlist[i+1].getLocation();
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
            mGAlgorithm.drawCurve(g2,p,Math.max(getWidth(),getHeight()),LW);
        }
    }
    public boolean Isin(Point loc) {
        loc.x-=getLocation().x;
        loc.y-=getLocation().y;
        if(loc.x<0 || loc.y<0 || loc.x>getWidth() || loc.y>getHeight())
            return false;
        Point p[]=new Point[father.Rank];
        p[0]=father.optlist[0].getLocation();
        p[0].x+=5-getLocation().x;
        p[0].y+=5-getLocation().y;
        p[father.Rank-1]=father.optlist[1].getLocation();
        p[father.Rank-1].x+=5-getLocation().x;
        p[father.Rank-1].y+=5-getLocation().y;
        for(int i=1;i<father.Rank-1;i++)
        {
            p[i]=father.optlist[i+1].getLocation();
            p[i].x+=5-getLocation().x;
            p[i].y+=5-getLocation().y;
        }
        double u;
        dPoint tmp=new dPoint(0,0);
        int e=Math.max(getWidth(),getHeight());
        for(int i=0;i<=e;i++)
        {
            u=(float)i/e;
            tmp=mGAlgorithm.BezierPoint(p,u);
            if((loc.x-tmp.x)*(loc.x-tmp.x)+(loc.y-tmp.y)*(loc.y-tmp.y)<=25)
                return true;
        }
        return false;
    }
    //Method for paint in zoomWindow
    public boolean IsinAcc(Point loc) {
        loc.x-=getLocation().x;
        loc.y-=getLocation().y;
        if(loc.x<0 || loc.y<0 || loc.x>getWidth() || loc.y>getHeight())
            return false;
        Point p[]=new Point[father.Rank];
        p[0]=father.optlist[0].getLocation();
        p[0].x+=5-getLocation().x;
        p[0].y+=5-getLocation().y;
        p[father.Rank-1]=father.optlist[1].getLocation();
        p[father.Rank-1].x+=5-getLocation().x;
        p[father.Rank-1].y+=5-getLocation().y;
        for(int i=1;i<father.Rank-1;i++)
        {
            p[i]=father.optlist[i+1].getLocation();
            p[i].x+=5-getLocation().x;
            p[i].y+=5-getLocation().y;
        }
        double u;
        dPoint tmp;
        int e=Math.max(getWidth(),getHeight());
        for(int i=0;i<=e;i++)
        {
            u=(float)i/e;
            tmp=mGAlgorithm.BezierPoint(p,u);
            if((loc.x-tmp.x)*(loc.x-tmp.x)+(loc.y-tmp.y)*(loc.y-tmp.y)<=father.LineWidth)
                return true;
        }
        return false;
    }
    public void drawInzoomWindow(zoomWindow zw, Point start, Point size)
    {
        Graphics2D g2 = (Graphics2D) zw.getGraphics();
        if(g2==null)
            return;
        g2.setColor(father.color);
        //TODO: 将控制点映射到zoomWindow中
        //求出控制点在实际窗口中坐标
        dPoint dp[]=new dPoint[father.Rank];
        dp[0]=new dPoint(father.optlist[0].getLocation());
        dp[0].x+=5;
        dp[0].y+=5;
        dp[father.Rank-1]=new dPoint(father.optlist[1].getLocation());
        dp[father.Rank-1].x+=5;
        dp[father.Rank-1].y+=5;
        for(int i=1;i<father.Rank-1;i++)
        {
            dp[i]=new dPoint(father.optlist[i+1].getLocation());
            dp[i].x+=5;
            dp[i].y+=5;
        }
        //转化为在zoomWindow中的坐标
        for(dPoint point : dp)
        {
            point.x -= start.x;
            point.y -= start.y;
            point.x *= zw.getWidth()/(double)size.x;
            point.y *= zw.getHeight()/(double)size.y;
        }
        //绘制曲线
        Point[] p = new Point[father.Rank];
        for(int i=0;i<father.Rank;i++)
        {
            p[i] = new Point((int)(dp[i].x+0.5), (int)(dp[i].y+0.5));
        }
        double u;
        dPoint tmp;
        int rx, ry;
        int e=Math.max(Math.abs(p[0].x-p[father.Rank-1].x),Math.abs(p[0].y-p[father.Rank-1].y));
        for(int i=0;i<=e;i++)
        {
            u=(float)i/e;
            tmp=mGAlgorithm.BezierPoint(p, u);
            rx=(int)(0.5+tmp.x);
            ry=(int)(0.5+tmp.y);
            if(rx>0 && rx<zw.getWidth() && ry>0 && ry<zw.getHeight())
                mGAlgorithm.drawPoint(g2, rx, ry, father.LineWidth);
        }
    }
}