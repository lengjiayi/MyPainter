import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class mEllipse extends mShape{
    double width, height;
    public mEllipse(myCanvas c, Point start)
    {
        canvas=c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[2];
        optlist=new optBall[4];
        for(int i=0;i<4;i++) {
            optlist[i] = new optBall(this);
        }
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        outlook=new mEllipseOutlook(this,start);
        canvas.add(outlook,0);
        addPoint(start);
    }
    boolean SetOneTime=true;
    @Override
    public void relocate(optBall opt)
    {
        dPoint center=new dPoint();
        if(Math.tan(angleacc)==0)
        {
            optlist[2].setLocation(optlist[0].getLocation().x, optlist[1].getLocation().y);
            optlist[3].setLocation(optlist[1].getLocation().x, optlist[0].getLocation().y);
        }
        else
        {
            double k1=-1/Math.tan(angleacc);
            double k2=-1/k1;
            center.x=optlist[0].getLocation().x+optlist[1].getLocation().x;
            center.y=optlist[0].getLocation().y+optlist[1].getLocation().y;
            center.x/=2;    center.y/=2;

            if(opt.equals(optlist[0]))
            {
                dPoint p3plus=new dPoint(0,0);
                double b1=optlist[1].getLocation().y-k1*optlist[1].getLocation().x;
                double b2=optlist[0].getLocation().y-k2*optlist[0].getLocation().x;
                p3plus.x=(b2-b1)/(k1-k2);
                p3plus.y=k1*p3plus.x+b1;
                optlist[3].setLocation((int)(p3plus.x+0.5), (int)(p3plus.y+0.5));
                optlist[2].setLocation((int)(2*center.x-p3plus.x+0.5), (int)(2*center.y-p3plus.y+0.5));
            }
            else if(opt.equals(optlist[1]))
            {
                dPoint p2plus=new dPoint(0,0);
                double b1=optlist[0].getLocation().y-k1*optlist[0].getLocation().x;
                double b2=optlist[1].getLocation().y-k2*optlist[1].getLocation().x;
                p2plus.x=(b2-b1)/(k1-k2);
                p2plus.y=k1*p2plus.x+b1;
                optlist[2].setLocation((int)(p2plus.x+0.5), (int)(p2plus.y+0.5));
                optlist[3].setLocation((int)(2*center.x-p2plus.x+0.5), (int)(2*center.y-p2plus.y+0.5));
            }
            else
            {
                throw new RuntimeException("mEllipse: wrong point.");
            }
        }
        width=mGAlgorithm.DistanceToPoint(optlist[0].getLocation(), optlist[3].getLocation());
        height=mGAlgorithm.DistanceToPoint(optlist[0].getLocation(), optlist[2].getLocation());
        mEllipseOutlook ol=(mEllipseOutlook)outlook;
        ol.update(null, null);
    }

    @Override
    public void set()
    {
        if(SetOneTime)
        {
            optlist[2].setLocation(optlist[0].getLocation().x, optlist[1].getLocation().y);
            optlist[3].setLocation(optlist[1].getLocation().x, optlist[0].getLocation().y);
            width=Math.abs(optlist[3].getLocation().x-optlist[0].getLocation().x);
            height=Math.abs(optlist[1].getLocation().y-optlist[0].getLocation().y);
            SetOneTime=false;
        }
        super.set();
    }

    @Override
    public void reset()
    {
        canvas.add(optlist[0],0);
        canvas.add(optlist[1],0);
        rcenter.setLocation(outlook.getLocation().x+outlook.getWidth()/2-5,outlook.getLocation().y+outlook.getHeight()/2-5);
        UpdateLocBeforeRotate();
        canvas.add(rball,0);
        canvas.add(rcenter,0);
        canvas.repaint();
    }

    @Override
    public void drawInWindow(zoomWindow zw, Point start, Point size)
    {
        Graphics2D g2=(Graphics2D)zw.getGraphics();
        if(g2==null)
            return;
        mEllipseOutlook ol=(mEllipseOutlook)outlook;
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
                if(p.y>0 && p.y<ol.VirtualScreen.length && p.x>0 && p.x<ol.VirtualScreen[0].length)
                    if(ol.VirtualScreen[p.y][p.x]==2) {
                        g2.setColor(fillcolor);
                        mGAlgorithm.drawPoint(g2, j, i);
                    }
                    else if(ol.VirtualScreen[p.y][p.x]==1) {
                        g2.setColor(color);
                        mGAlgorithm.drawPoint(g2, j, i);
                    }
            }
    }
    @Override
    public void AutoResize()
    {
        update(optlist[0].getLocation());
    }
    @Override
    public void update(Point newloc) {
        mEllipseOutlook ll=(mEllipseOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }
    @Override
    public boolean Isin(Point loc) {
        return ((mEllipseOutlook)outlook).Isin(loc);
    }
}

class mEllipseOutlook extends JLabel {
    public Point start, end;
    char[][] VirtualScreen=null;
    private mEllipse father;
    public mEllipseOutlook(mEllipse r, Point s) {
        father=r;
        this.setLocation(s);
        this.setBackground(new Color(0,0,0,0));
        start = s;
        end = new Point(s.x + 1, s.y + 1);
        this.setSize(1, 1);
    }

    public static int max(int a, int b){ return a>b?a:b; }
    public static int min(int a, int b){ return a<b?a:b; }
    public void update(Point s, Point e) {
        if(father.premain>0)
        {
            start = s;
            end = e;
            setLocation(min(start.x, end.x)-mShape.vwidth/2, min(start.y, end.y)-mShape.vwidth/2);
            int w=Math.abs(end.x-start.x)+mShape.vwidth;
            int h=Math.abs(end.y-start.y)+mShape.vwidth;
            setSize(w, h);
        }
        else
        {
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
        }
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        VirtualScreen=new char[getHeight()+1][getWidth()+1];
        Graphics2D g2 = (Graphics2D) g;
        int LW=father.LineWidth;
        if(father.hightligh) {
            LW++;
            g2.setColor(Color.BLUE);
        }else {
            g2.setColor(father.color);
        }
        if(father.premain>0) {
            mGAlgorithm.drawOval(g2, getWidth()/2, getHeight()/2, getWidth() - mShape.vwidth, getHeight() - mShape.vwidth, LW, 0, null);
        }
        else {
            double width = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[3].getLocation());
            double height = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[2].getLocation());
            mGAlgorithm.drawOval(g2, getWidth()/2, getHeight()/2, (int)(width+0.5), (int)(height+0.5), LW, father.angleacc, VirtualScreen);
        }

        if(father.IsFill)
        {
            g2.setColor(father.fillcolor);
            //TODO 椭圆填充算法
            for(int j=0;j<getHeight();j++)
                for(int i=1;i<getWidth();i++)
                    if(fillable(i,j) && IsinAcc(i,j))
                    {
                        mGAlgorithm.drawPoint(g2, i, j);
                        VirtualScreen[j][i]=2;
                    }
        }
    }
    public boolean Isin(Point loc) {
        Point curloc=getLocation();
        loc.x-=curloc.x;
        loc.y-=curloc.y;
        if(loc.x<0 || loc.x>getWidth() || loc.y<0 || loc.y>getHeight())
            return false;
        dPoint center = new dPoint(getWidth()/2.0, getHeight()/2.0);
        dPoint relativeloc = new dPoint(0, 0);
        dPoint rloc = new dPoint(0, 0);
        relativeloc.x = loc.x - center.x;
        relativeloc.y = loc.y - center.y;
        //根据公式计算鼠标当前位置逆向旋转后位置
        rloc.x = relativeloc.x * Math.cos(-father.angleacc) - relativeloc.y * Math.sin(-father.angleacc) + center.x;
        rloc.y = relativeloc.x * Math.sin(-father.angleacc) + relativeloc.y * Math.cos(-father.angleacc) + center.y;

        double width = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[3].getLocation());
        double height = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[2].getLocation());
        double a=width/2;
        double b=height/2;
        double realprod=a*a*b*b;
        double tmpprod=(getWidth()/2-rloc.x)*(getWidth()/2-rloc.x)*b*b+(getHeight()/2-rloc.y)*(getHeight()/2-rloc.y)*a*a;
        realprod=(int)Math.sqrt(realprod);
        tmpprod=(int)Math.sqrt(tmpprod);
        if(Math.abs(tmpprod-realprod)>realprod/10)
            return false;
        return true;
    }
    //Methods for fill a Oval
    private boolean IsinAcc(int x, int y)
    {
        if(x<0 || x>getWidth() || y<0 || y>getHeight())
            return false;
        dPoint center = new dPoint(getWidth()/2.0, getHeight()/2.0);
        dPoint relativeloc = new dPoint(0, 0);
        dPoint rloc = new dPoint(0, 0);
        relativeloc.x = x - center.x;
        relativeloc.y = y - center.y;
        //根据公式计算鼠标当前位置逆向旋转后位置
        rloc.x = relativeloc.x * Math.cos(-father.angleacc) - relativeloc.y * Math.sin(-father.angleacc) + center.x;
        rloc.y = relativeloc.x * Math.sin(-father.angleacc) + relativeloc.y * Math.cos(-father.angleacc) + center.y;

        double width = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[3].getLocation());
        double height = mGAlgorithm.DistanceToPoint(father.optlist[0].getLocation(), father.optlist[2].getLocation());
        double a=width/2;
        double b=height/2;
        double realprod=a*a*b*b;
        double tmpprod=(getWidth()/2-rloc.x)*(getWidth()/2-rloc.x)*b*b+(getHeight()/2-rloc.y)*(getHeight()/2-rloc.y)*a*a;
        if(tmpprod<=realprod)
            return true;
        return false;
    }
    private boolean fillable(int x, int y)
    {
        if(x< 0 || x>=VirtualScreen[0].length || y<0 || y>=VirtualScreen.length)
            return false;
        if(VirtualScreen[y][x]!=0)
            return false;
        return true;
    }
}