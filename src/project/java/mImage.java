import javax.swing.*;
import java.awt.*;

public class mImage extends mShape {
    public mImage(myCanvas c, Point start)
    {
        OnlyResizeable = true;
        canvas=c;
        pointsrequred=2;
        premain=2;
        pointlist=new Point[2];
        optlist=new optBall[2];
        for(int i=0;i<pointsrequred;i++)
            optlist[i]=new optBall(this);
        outlook=new mImageOutlook(this,start);
        canvas.add(outlook);
        addPoint(start);
    }

    @Override
    public void update(Point newloc) {
        mImageOutlook ll=(mImageOutlook)outlook;
        ll.update(pointlist[0], newloc);
    }

    @Override
    public void set()
    {
        mImageOutlook ol = (mImageOutlook)outlook;
        ol.setImage();
        if(ol.image!=null)
        {
            ol.wrapimage();
            ol.image.unSelected();
        }
        for(int i=0;i<optlist.length-premain;i++)
            canvas.remove(optlist[i]);
        canvas.repaint();
    }

    @Override
    public void reset()
    {
        for(int i=0;i<optlist.length-premain;i++)
            canvas.add(optlist[i],0);
        mImageOutlook ol = (mImageOutlook)outlook;
        ol.image.selected();
        canvas.repaint();
    }

    @Override
    public boolean Isin(Point loc) {
        return ((mImageOutlook)outlook).Isin(loc);
    }

    @Override
    public void relocate(optBall opt)
    {
        mImageOutlook ll=(mImageOutlook) outlook;
        Point a=new Point(optlist[0].getLocation());
        Point b=new Point(optlist[1].getLocation());
        a.x+=5;
        a.y+=5;
        b.x+=5;
        b.y+=5;
        ll.update(a, b);
    }

    @Override
    public void drawInWindow(zoomWindow zw, Point start, Point size)
    {
        mImageOutlook ol = (mImageOutlook)outlook;
        Graphics2D g2=(Graphics2D)zw.getGraphics();
        if(g2==null)
            return;
        Color tmpcolor;
        for(int i=0;i<zw.getHeight();i++)
        {
            for(int j=0;j<zw.getWidth();j++)
            {
                dPoint p=new dPoint(j,i);
                p.x=p.x*size.x/zw.getWidth();
                p.y=p.y*size.y/zw.getHeight();
                p.x+=start.x;
                p.y+=start.y;
                p.x-=ol.getLocation().x;
                p.y-=ol.getLocation().y;
                tmpcolor = ol.image.getColor(p.x, p.y);
                if(tmpcolor != null) {
                    g2.setColor(tmpcolor);
                    mGAlgorithm.drawPoint(g2, j, i);
                }
            }
        }
    }
}

class mImageOutlook extends JLabel {
    public Point start, end;
    private mImage father;
    public ImagePane image=null;
    public mImageOutlook(mImage r, Point s) {
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
        start = s;
        end = e;
        setLocation(min(start.x, end.x)-mShape.vwidth/2, min(start.y, end.y)-mShape.vwidth/2);
        int w=Math.abs(end.x-start.x)+mShape.vwidth;
        int h=Math.abs(end.y-start.y)+mShape.vwidth;
        if(image!=null) {
            image.reSize(getWidth() - mShape.vwidth, getHeight() - mShape.vwidth);
            image.setLocation(mShape.vwidth/2, mShape.vwidth/2);
        }
        setSize(w, h);
    }

    public void setImage()
    {
        if(image==null)
        {
            image=new ImagePane(getWidth()-mShape.vwidth, getHeight()-mShape.vwidth);
            image.setLocation(mShape.vwidth/2,mShape.vwidth/2);
            add(image);
        }
    }
    /**
     * 重新设定outlook大小使得刚好包裹图片
     */
    public void wrapimage()
    {
        if(image!=null && image.imgwidth>0) {
            image.reSize(getWidth() - mShape.vwidth, getHeight() - mShape.vwidth);
            setSize(mShape.vwidth + image.getWidth(), mShape.vwidth + image.getHeight());
            Point loc = getLocation();
            father.optlist[0].setLocation(loc.x + mShape.vwidth/2 - 5, loc.y + mShape.vwidth/2 - 5);
            father.optlist[1].setLocation(loc.x + getWidth() - mShape.vwidth/2 - 5, loc.y + getHeight() - mShape.vwidth/2 - 5);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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