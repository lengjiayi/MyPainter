import javax.swing.*;
import java.awt.*;

public class mRect extends mShape{
    double width, height;
    public mRect(myCanvas c, Point start)
    {
        canvas=c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[2];
        optlist=new optBall[4];
        for(int i=0;i<4;i++) {
            optlist[i] = new optBall(this);
        }
//        optlist[2].RotateRelative=false;
//        optlist[3].RotateRelative=false;
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        outlook=new mRectOutlook(this,start);
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
                throw new RuntimeException("mRect: wrong point.");
            }
        }
        width=mGAlgorithm.DistanceToPoint(optlist[0].getLocation(), optlist[3].getLocation());
        height=mGAlgorithm.DistanceToPoint(optlist[0].getLocation(), optlist[2].getLocation());
        mRectOutlook ol=(mRectOutlook)outlook;
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


        mCutWindowHelper.drawLine(zw,start,size,optlist[0].getLocation(),optlist[3].getLocation(),LineWidth,color);
        mCutWindowHelper.drawLine(zw,start,size,optlist[0].getLocation(),optlist[2].getLocation(),LineWidth,color);
        mCutWindowHelper.drawLine(zw,start,size,optlist[1].getLocation(),optlist[3].getLocation(),LineWidth,color);
        mCutWindowHelper.drawLine(zw,start,size,optlist[1].getLocation(),optlist[2].getLocation(),LineWidth,color);

        if(this.IsFill)
        {
            Graphics2D g2=(Graphics2D)zw.getGraphics();
            if(g2==null)
                return;
            g2.setColor(fillcolor);
            mRectOutlook ol=(mRectOutlook)outlook;
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
                    if(ol.VirtualScreen[p.y][p.x]==2)
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
        mRectOutlook ll=(mRectOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }
    @Override
    public boolean Isin(Point loc) {
        return ((mRectOutlook)outlook).Isin(loc);
    }
}

class mRectOutlook extends JLabel {
    public Point start, end;
    char[][] VirtualScreen=null;
    private mRect father;
    public mRectOutlook(mRect r, Point s) {
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

    public void RectLineTo(Graphics2D g2, Point a, Point b)
    {
        Point loc=getLocation();
        if(a.x<b.x)
            mGAlgorithm.drawLine(g2,a.x+5-loc.x, a.y+5-loc.y, b.x+5-loc.x, b.y+5-loc.y, father.LineWidth, VirtualScreen);
        else
            mGAlgorithm.drawLine(g2,b.x+5-loc.x, b.y+5-loc.y,a.x+5-loc.x, a.y+5-loc.y, father.LineWidth, VirtualScreen);
    }
    public void FillLineTo(Graphics2D g2, dPoint a, dPoint b)
    {
        Point loc=getLocation();
        if(a.x<b.x)
            mGAlgorithm.SingleLine(g2,(int)(0.5+a.x+5-loc.x), (int)(0.5+a.y+5-loc.y), (int)(0.5+b.x+5-loc.x), (int)(0.5+b.y+5-loc.y), null);
        else
            mGAlgorithm.SingleLine(g2,(int)(0.5+b.x+5-loc.x), (int)(0.5+b.y+5-loc.y), (int)(0.5+a.x+5-loc.x), (int)(0.5+a.y+5-loc.y), null);
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
            mGAlgorithm.drawLine(g2, mShape.vwidth / 2, mShape.vwidth / 2, getWidth() - mShape.vwidth / 2, mShape.vwidth / 2, LW);
            mGAlgorithm.drawLine(g2, mShape.vwidth / 2, mShape.vwidth / 2, mShape.vwidth / 2, getHeight() - mShape.vwidth / 2, LW);
            mGAlgorithm.drawLine(g2, mShape.vwidth / 2, getHeight() - mShape.vwidth / 2, getWidth() - mShape.vwidth / 2, getHeight() - mShape.vwidth / 2, LW);
            mGAlgorithm.drawLine(g2, getWidth() - mShape.vwidth / 2, mShape.vwidth / 2, getWidth() - mShape.vwidth / 2, getHeight() - mShape.vwidth / 2, LW);
        }
        else {
            RectLineTo(g2, father.optlist[0].getLocation(), father.optlist[3].getLocation());
            RectLineTo(g2, father.optlist[0].getLocation(), father.optlist[2].getLocation());
            RectLineTo(g2, father.optlist[1].getLocation(), father.optlist[3].getLocation());
            RectLineTo(g2, father.optlist[1].getLocation(), father.optlist[2].getLocation());
        }
        if(father.IsFill)
        {
            g2.setColor(father.fillcolor);
/*
            //TODO 水平矩形填充算法
            for(int i=mShape.vwidth/2+1;i<getHeight()-mShape.vwidth/2;i++)
                mGAlgorithm.SingleLine(g2,mShape.vwidth/2+1,i,getWidth()-mShape.vwidth/2-1,i, null);
*/
            //TODO 支持旋转的扫描线填充算法
            boolean even=false;
            int lastx=-1;
            for(int j=0;j<getHeight();j++)
            {
                even=false;
                lastx=-1;
                for(int i=1;i<getWidth();i++)
                {
                    if (VirtualScreen[j][i]==1 && (VirtualScreen[j][i-1]==0 || i==1))        //从空白区域进入边界
                    {
                        even=!even;
                        if(!even && lastx>=0)                                               //当前点为奇数时从上个点绘制填充线
                        {
                            mGAlgorithm.SingleLine(g2,lastx,j,i,j,null);
                            for(int t=lastx;t<=i;t++)
                                VirtualScreen[j][t]=2;
                        }
                    }
                    if (VirtualScreen[j][i]==0 && VirtualScreen[j][i-1]==1)                  //从边界进入空白区域
                        lastx=i-1;                                                           //记录上一个点
                }
            }
        }
    }
    public boolean Isin(Point loc) {
        if(mShape.Isinline(father.optlist[0].getLocation(), father.optlist[3].getLocation(), loc))
            return true;
        if(mShape.Isinline(father.optlist[0].getLocation(), father.optlist[2].getLocation(), loc))
            return true;
        if(mShape.Isinline(father.optlist[1].getLocation(), father.optlist[3].getLocation(), loc))
            return true;
        if(mShape.Isinline(father.optlist[1].getLocation(), father.optlist[2].getLocation(), loc))
            return true;
        return false;
/*
        Point curloc=getLocation();
        loc.x-=curloc.x;
        loc.y-=curloc.y;
        if(loc.x<0 || loc.x>getWidth() || loc.y<0 || loc.y>getHeight())
            return false;
        if(loc.x<=mShape.vwidth || getWidth()-loc.x<=mShape.vwidth || loc.y<=mShape.vwidth || getHeight()-loc.y<=mShape.vwidth)
            return true;
*/
    }
}

/*
*
    @Override
    public void relocate(optBall opt)
    {
        if(angleacc==0)
            angleacc=10e-12;
        System.out.println(angleacc);
        //TODO 针对矩形实现旋转
        dPoint center=new dPoint(optlist[0].getLocation().x+optlist[1].getLocation().x, optlist[0].getLocation().y+optlist[1].getLocation().y);
        double ctana=Math.tan(angleacc);
        double tana=1/ctana;
        center.x=center.x/2;
        center.y=center.y/2;
        //TODO 计算关于p1的中轴线对称点p3
        double b1=center.y-tana*center.x;
        double b2=optlist[1].getLocation().y+ctana*(optlist[1].getLocation().x);
        dPoint tmpP=new dPoint();
        tmpP.x=(b2-b1)/(tana+ctana);
        tmpP.y=tana*tmpP.x+b1;
        optlist[3].setLocation((int)(tmpP.x*2-optlist[1].getLocation().x), (int)(tmpP.y*2-optlist[1].getLocation().y));

        //TODO 计算关于p0的中轴线对称点p2

        b2=optlist[0].getLocation().y+ctana*(optlist[0].getLocation().x);
        tmpP.x=(b2-b1)/(tana+ctana);
        tmpP.y=tana*tmpP.x+b1;
        optlist[2].setLocation((int)(tmpP.x*2-optlist[0].getLocation().x), (int)(tmpP.y*2-optlist[0].getLocation().y));


        mRectOutlook ll=(mRectOutlook)outlook;
        Point a=new Point(optlist[0].getLocation());
        Point b=new Point(optlist[1].getLocation());
        a.x+=5;
        a.y+=5;
        b.x+=5;
        b.y+=5;
        ll.update(a, b);
        canvas.getGraphics().drawLine((int)center.x, (int)center.y,(int)tmpP.x,(int)tmpP.y);
    }

* */


/*
            // 重写支持旋转的斜线填充算法
            dPoint p0=new dPoint(father.optlist[0].getLocation());
            dPoint p2=new dPoint(father.optlist[2].getLocation());
            dPoint p3=new dPoint(father.optlist[3].getLocation());
            double e=Math.max(Math.abs(p2.x-p0.x), Math.abs(p2.y-p0.y))*10;
            double dx=(p2.x-p0.x)/e;
            double dy=(p2.y-p0.y)/e;
            for(int i=0;i<e;i++)
            {
                p0.x+=dx;   p0.y+=dy;   p3.x+=dx;   p3.y+=dy;
                FillLineTo(g2, p0, p3);
            }
*/
