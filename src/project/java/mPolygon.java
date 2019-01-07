import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class mPolygon extends mShape {
    public boolean IsSet=false;
    public mPolygon(myCanvas c, Point start)
    {
        poly=true;
        painttype=2;
        canvas=c;
        pointsrequred=100;
        premain=100;
        pointlist=new Point[100];
        optlist=new optBall[100];
        for(int i=0;i<pointsrequred;i++)
            optlist[i]=new optBall(this);
        rball=new RotateBall(this);
        rcenter=new RotateCenter(this);
        addPoint(start);
        outlook=new mPolygonOutlook(this);
        canvas.add(outlook,0);
        canvas.repaint();
    }

    @Override
    public void update(Point newloc) {
//        System.out.println("father: "+newloc);
        mPolygonOutlook pl=(mPolygonOutlook)outlook;
        pl.update(newloc);
    }
    @Override
    public void relocate(optBall opt)
    {
/*
        if(opt!=null) {
            Point lu = outlook.getLocation();
            Point rb=new Point(lu.x+outlook.getWidth()-mShape.vwidth/2,lu.y+outlook.getHeight()-mShape.vwidth/2);
            Point loc=opt.getLocation();
            loc.x+=5;   loc.y+=5;
            lu.x += mShape.vwidth / 2;
            lu.y += mShape.vwidth / 2;
            boolean change=false;
            if(loc.x<lu.x) {
                lu.x=loc.x;
                change=true;
            }
            if(loc.y<lu.y) {
                lu.y=loc.y;
                change=true;
            }
            if(change) {
                lu.x=lu.x-5-mShape.vwidth/2;
                lu.y=lu.y-5-mShape.vwidth/2;
                outlook.setLocation(lu);
                outlook.setSize(rb.x-lu.x+mShape.vwidth/2, rb.y-lu.y+mShape.vwidth/2);
            }
        }
*/
        Point lu, rb;
        lu=optlist[0].getLocation();
        lu.x+=5;
        lu.y+=5;
        rb=new Point(lu.x, lu.y);
        int n_point=0;
        for(optBall x:optlist)
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
            n_point++;
            if(n_point>=pointsrequred-premain)
                break;
        }
        outlook.setLocation(lu.x-mShape.vwidth/2, lu.y-mShape.vwidth/2);
        outlook.setSize(rb.x-lu.x+mShape.vwidth,rb.y-lu.y+mShape.vwidth);

        mPolygonOutlook pl=(mPolygonOutlook)outlook;
        pl.loop();
    }

    @Override
    public void set() {
        IsSet=true;
        mPolygonOutlook pl=(mPolygonOutlook)outlook;
        pl.loop();
        super.set();
    }

    @Override
    public void drawInWindow(zoomWindow zw, Point start, Point size)
    {
        int i=0;
        for(i=0;i<pointsrequred-premain-1;i++)
        {
            mCutWindowHelper.drawLine(zw,start,size,optlist[i].getLocation(),optlist[i+1].getLocation(),LineWidth,color);
        }
        mCutWindowHelper.drawLine(zw,start,size,optlist[i].getLocation(),optlist[0].getLocation(),LineWidth,color);

        if(this.IsFill)
        {
            Graphics2D g2=(Graphics2D)zw.getGraphics();
            if(g2==null)
                return;
            g2.setColor(fillcolor);
            mPolygonOutlook pl=(mPolygonOutlook) outlook;
            for(i=0;i<zw.getHeight();i++)
                for(int j=0;j<zw.getWidth();j++)
                {
                    Point p=new Point(j,i);
                    p.x=(int)(p.x*(double)size.x/zw.getWidth()+0.5);
                    p.y=(int)(p.y*(double)size.y/zw.getHeight()+0.5);
                    p.x+=start.x;
                    p.y+=start.y;
                    p.x-=pl.getLocation().x;
                    p.y-=pl.getLocation().y;
                    if(p.y>0 && p.y<pl.VirtualScreen.length && p.x>0 && p.x<pl.VirtualScreen[0].length)
                        if(pl.VirtualScreen[p.y][p.x]==2)
                            mGAlgorithm.drawPoint(g2, j, i);
                }
        }
    }

    @Override
    public boolean Isin(Point loc) {
        return ((mPolygonOutlook)outlook).Isin(loc);
    }

    @Override
    public void bedragged(int dx, int dy)
    {
        super.bedragged(dx,dy);
        relocate(null);
    }
}

class mPolygonOutlook extends JLabel {
    private mPolygon father;
    private Point end;
    public char[][] VirtualScreen=null;
    public mPolygonOutlook(mPolygon p) {
        this.father=p;
        Point start=father.optlist[0].getLocation();
        start.x-=mShape.vwidth/2;
        start.y-=mShape.vwidth/2;
        end=new Point(start.x+1, start.y+1);
        this.setLocation(start);
        this.setBackground(new Color(0,0,0,0));
        this.setSize(1+mShape.vwidth, 1+mShape.vwidth);
    }

    public static int max(int a, int b){ return a>b?a:b; }
    public static int min(int a, int b){ return a<b?a:b; }

    public void update(Point e) {
        end=e;
        Point lu, rb;
        lu=father.optlist[0].getLocation();
        lu.x+=5;
        lu.y+=5;
        rb=new Point(lu.x, lu.y);
        int n_point=0;
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
            n_point++;
            if(n_point>=father.pointsrequred-father.premain)
                break;
        }
        if(end.x+5<lu.x)
            lu.x=end.x+5;
        if(end.x+5>rb.x)
            rb.x=end.x+5;
        if(end.y+5<lu.y)
            lu.y=end.y+5;
        if(end.y+5>rb.y)
            rb.y=end.y+5;
        setLocation(lu.x-mShape.vwidth/2, lu.y-mShape.vwidth/2);
        setSize(rb.x-lu.x+mShape.vwidth,rb.y-lu.y+mShape.vwidth);
/*
        end=e;
        Point lu=getLocation();
        lu.x+=mShape.vwidth/2;  lu.y+=mShape.vwidth/2;
        Point rb=new Point(lu.x+getWidth()-mShape.vwidth/2,lu.y+getHeight()-mShape.vwidth/2);
        lu.x=min(end.x,lu.x)-mShape.vwidth/2;   lu.y=min(end.y,lu.y)-mShape.vwidth/2;
        rb.x=max(end.x,rb.x)+mShape.vwidth/2;   rb.y=max(end.y,rb.y)+mShape.vwidth/2;
        setLocation(lu);
        setSize(rb.x - lu.x, rb.y - lu.y);
*/    }

    public void loop()
    {
        end=father.optlist[0].getLocation();
        end.x+=5;   end.y+=5;
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
        Point loc=getLocation();
        Point a=null,b=father.optlist[0].getLocation();
        for(int i=0;i<father.pointsrequred-father.premain-1;i++)
        {
            a=father.optlist[i].getLocation();
            b=father.optlist[i+1].getLocation();
            if(a.x<b.x)
                mGAlgorithm.drawLine(g2,a.x+5-loc.x, a.y+5-loc.y, b.x+5-loc.x, b.y+5-loc.y, LW, VirtualScreen);
            else
                mGAlgorithm.drawLine(g2,b.x+5-loc.x, b.y+5-loc.y,a.x+5-loc.x, a.y+5-loc.y, LW, VirtualScreen);
         }
        mGAlgorithm.drawLine(g2,b.x+5-loc.x, b.y+5-loc.y, end.x-loc.x, end.y-loc.y, LW, VirtualScreen);
        if(father.IsFill)           //填充多边形
        {
            g2.setColor(father.fillcolor);
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
        Point a=null,b=null;
        for(int i=0;i<father.pointsrequred-father.premain-1;i++){
            a=father.optlist[i].getLocation();
            b=father.optlist[i+1].getLocation();
            a.x+=5; a.y+=5; b.x+=5; b.y+=5;
            if(mShape.Isinline(a,b,loc))
                return true;
        }
        a=father.optlist[0].getLocation();
        a.x+=5; a.y+=5;
        if(b!=null)
            if(mShape.Isinline(a,b,loc))
                return true;
        return false;
    }
}















/*
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Point loc=getLocation();
                loc.x+=e.getPoint().x;
                loc.y+=e.getPoint().y;
                if(!father.IsSet)
                {
                    update(loc);
                    repaint();
                }
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                super.mouseClicked(e);
                Point loc=getLocation();
                loc.x+=e.getPoint().x;
                loc.y+=e.getPoint().y;
                if(e.isMetaDown())
                {
                    father.set();
                    father.canvas.addShape(father);
                    father.canvas.father.curShape=null;
                    repaint();
                    System.out.println(isFocusOwner());
                    father.canvas.requestFocus();
                    return;
                }
                if(!father.IsSet)
                {
                    father.addPoint(loc);
                    repaint();
                    return;
                }

                if(Isin(loc)) {
                    father.canvas.father.selectedShape = father;
                    father.reset();
                    father.canvas.father.pbar.setVisible(true);
                }

            }
        });*/
