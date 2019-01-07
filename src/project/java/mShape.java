//import com.sun.xml.internal.bind.v2.TODO;
import org.omg.CORBA.DATA_CONVERSION;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

class mShape extends JLabel {
    protected Point pointlist[];
    public RotateCenter rcenter;              //旋转中心
    public RotateBall rball;                  //旋转控制点
    public double angle=0;                   //一次编辑中的旋转角度
    public double angleacc=0;                //相对于初始状态时的旋转角度
    public optBall optlist[];                 //控制点
    public int pointsrequred;               //本形状所需控制点数
    public int premain;                      //剩余未设置点数
    public myCanvas canvas;                   //形状所在画布
    public JLabel outlook;                    //形状外观
    public static int vwidth=6;
    public static int rrdist=80;              //旋转中心和旋转控制点距离
    public boolean poly=false;               //是否为多边形
    public boolean hightligh=false;         //当前是否为高亮状态
    public boolean IsFill=false;             //是否为填充状态
    public boolean OnlyResizeable=false;    //针对只能进行放缩操作的形状
    public Color fillcolor=Color.BLACK;       //填充颜色

    public int painttype=1;
    public Color color=Color.BLACK;         //形状边框颜色
    public int LineWidth=1;                 //形状线条粗细
    public void addPoint(Point p)            //设置新的控制点
    {
        pointlist[pointsrequred-premain]=p;
        optlist[pointsrequred-premain].setLocation(p.x-5,p.y-5);
        canvas.add(optlist[pointsrequred-premain]);
        canvas.repaint();
        premain--;
    }
    /**
     * 初始化形状时用于鼠标拖动时更新视图
     */
    public void update(Point newloc){};
    /**
     * 判断当前鼠标是否在此形状内
     */
    public boolean Isin(Point loc){ return false; };
    /**
     * 当有某个控制点被拖动时调用进行重绘
     */
    public void relocate(optBall opt) { }

    public void set()                                                                    //隐藏所有控制点
    {
        for(int i=0;i<optlist.length-premain;i++)
            canvas.remove(optlist[i]);
        if(!OnlyResizeable) {
            canvas.remove(rball);
            canvas.remove(rcenter);
        }
        canvas.repaint();
    }
    public void reset()                                                                  //显示所有控制点
    {
        for(int i=0;i<optlist.length-premain;i++)
            canvas.add(optlist[i],0);
        if(!OnlyResizeable)
        {
            rcenter.setLocation(outlook.getLocation().x + outlook.getWidth() / 2 - 5, outlook.getLocation().y + outlook.getHeight() / 2 - 5);
            UpdateLocBeforeRotate();
            canvas.add(rball, 0);
            canvas.add(rcenter, 0);
        }
        canvas.repaint();
    }
    public void bedragged(int dx, int dy)                                               //用于被拖拽时改变位置
    {
        Point tmp;
        for(int i=0;i<optlist.length;i++){
            tmp=optlist[i].getLocation();
            tmp.x+=dx;
            tmp.y+=dy;
            optlist[i].setLocation(tmp);
        }
        if(!OnlyResizeable)
        {
            tmp = rcenter.getLocation();
            tmp.x += dx;
            tmp.y += dy;
            rcenter.setLocation(tmp);
            tmp = rball.getLocation();
            tmp.x += dx;
            tmp.y += dy;
            rball.setLocation(tmp);
        }
        tmp=outlook.getLocation();
        tmp.x+=dx;
        tmp.y+=dy;
        outlook.setLocation(tmp);
        outlook.repaint();
        UpdateLocBeforeRotate();
    }
    public static boolean Isinline(Point start, Point end, Point loc)                  //判断鼠标是否在直线上的算法
    {
        Point lt,rt;
        if(start.x<end.x) {
            lt=start; rt=end;
        }
        else {
            lt=end; rt=start;
        }
        if(loc.x<lt.x-vwidth || loc.x>rt.x+vwidth)
            return false;
        float A,B,C;
        A=lt.y-rt.y;    B=rt.x-lt.x;    C=-rt.y*B-rt.x*A;
        double dist=(A*loc.x+B*loc.y+C)/Math.sqrt(A*A+B*B);
        if(Math.abs((float)dist)>vwidth)
            return false;
        return true;
    }
    public void inHighlight()                                                            //取消高亮当前形状
    {
        hightligh=false;
        outlook.repaint();
    }
    public void Highlight()                                                              //高亮当前形状
    {
        hightligh=true;
        outlook.repaint();
    }
    public void setColor(Color newcolor){ this.color=newcolor; }                       //设置形状颜色
    public void setLineWidth(int width){ this.LineWidth=width; }                      //设置形状宽度
    public void fill(Color color){                                                      //为形状设置填充颜色
        if(OnlyResizeable)
            return;
        if(color.getAlpha()==0)
        {
            IsFill=false;
            return;
        }
        IsFill=true;
        fillcolor=color;
        canvas.removeShape(this);
        canvas.addShape(this);
//        canvas.remove(outlook);
        canvas.add(outlook,0);
        canvas.repaint();
    };
    /**
     * 更新旋转控制点位置和各控制点的realloc（旋转前位置）
     */
    public void UpdateLocBeforeRotate()
    {
        if(OnlyResizeable)
            return;
        angle=0;
        dPoint deltaloc=new dPoint(rrdist*Math.cos(angle),rrdist*Math.sin(angle));
        rball.setLocation((int)(rcenter.getLocation().x+deltaloc.x), (int)(rcenter.getLocation().y+deltaloc.y));
        for(optBall x:optlist)
        {
            x.realLoc.x=x.getLocation().x;
            x.realLoc.y=x.getLocation().y;
        }
    }
    public void Rotate()                                                                //旋转后进行重绘
    {
        if(OnlyResizeable)
            return;
        for(optBall x:optlist)
        {
            if(x.RotateRelative) {          //旋转图元中所有的控制点
                dPoint relativeloc = new dPoint(0, 0);
                dPoint nextloc = new dPoint(0, 0);
                relativeloc.x = x.realLoc.x - rcenter.getLocation().x;      //计算相对于旋转中心的位置
                relativeloc.y = x.realLoc.y - rcenter.getLocation().y;
                //根据公式计算旋转后位置
                nextloc.x = relativeloc.x * Math.cos(angle) - relativeloc.y * Math.sin(angle) + rcenter.getLocation().x;
                nextloc.y = relativeloc.x * Math.sin(angle) + relativeloc.y * Math.cos(angle) + rcenter.getLocation().y;
                x.setLocation((int) nextloc.x, (int) nextloc.y);
            }
        }
        AutoResize();       //重绘
    }
    public void drawInWindow(zoomWindow zw, Point start, Point size){}                  //在zoomWindows中映射绘图
    public void AutoResize()
    {
        relocate(null);
    }                                 //自动调整outlook大小
    public void dispose() {
        canvas.remove(outlook);
        for(int i=0;i<optlist.length;i++)
            canvas.remove(optlist[i]);
        canvas.repaint();
    }                                                         //从画布上删除此形状
}

interface mShapeOutlook
{
    public static int max(int a, int b){ return a>b?a:b; }
    public static int min(int a, int b){ return a<b?a:b; }
    public boolean Isin(Point loc);

}

class optBall extends JLabel{
    protected Shape shape;
    protected Shape border;
    protected Point pressPoint=null;
    protected mShape father;
    protected Color color=Color.lightGray;
    public Point realLoc=new Point(0,0);
    public boolean RotateRelative=true;
    public optBall(mShape s)
    {
        father=s;
        setBackground(new Color(0,0,0,0));
        setSize(10,10);
        shape=new Ellipse2D.Float(1.5f,1.5f,7,7);
        border=new Ellipse2D.Float(0,0,10,10);
        this.setVisible(true);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                bedragged(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                pressPoint=e.getPoint();      //记录鼠标按下的位置，用于实现拖动效果
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mousePressed(e);
                father.UpdateLocBeforeRotate();
            }
        });
    }
    public void setColor(Color newcolor)
    {
        color=newcolor;
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
        father.relocate(this);
    }
}

class RotateBall extends optBall
{
    public RotateBall(mShape s)
    {
        super(s);
        setColor(Color.green);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                father.UpdateLocBeforeRotate();
//                System.out.println(father.angleacc);
            }
        });
    }
    @Override
    protected void bedragged(MouseEvent e)      //实现拖动效果
    {
        if(pressPoint==null)
            return;
        Point toloc=e.getPoint();
        Point nowloc=getLocation();
        Point nextloc=new Point(nowloc.x+toloc.x-pressPoint.x,nowloc.y+toloc.y-pressPoint.y);
        setLocation(nextloc.x,nextloc.y);
        double oldangle=father.angle;
        father.angle=Math.atan((double)(nextloc.y-father.rcenter.getLocation().y)/(double)(nextloc.x-father.rcenter.getLocation().x));
        father.angleacc+=father.angle-oldangle;
        if(nextloc.x-father.rcenter.getLocation().x<0) {
            father.angle = father.angle + Math.PI;
            father.angleacc=father.angleacc+Math.PI;
        }
        dPoint deltaloc=new dPoint(mShape.rrdist*Math.cos(father.angle),mShape.rrdist*Math.sin(father.angle));
        setLocation((int)(father.rcenter.getLocation().x+deltaloc.x), (int)(father.rcenter.getLocation().y+deltaloc.y));
        father.Rotate();
    }
}

class RotateCenter extends optBall
{
    public RotateCenter(mShape s)
    {
        super(s);
        shape=new Rectangle2D.Float(0,4,10,2);
        border=new Rectangle2D.Float(4,0,2,10);
        setColor(Color.BLACK);
    }

    @Override
    protected void bedragged(MouseEvent e)      //实现拖动效果
    {
        if(pressPoint==null)
            return;
        Point toloc=e.getPoint();
        Point nowloc=getLocation();
        Point nextloc=new Point(nowloc.x+toloc.x-pressPoint.x,nowloc.y+toloc.y-pressPoint.y);
        setLocation(nextloc.x,nextloc.y);
        father.rball.setLocation(father.rball.getLocation().x+toloc.x-pressPoint.x, father.rball.getLocation().y+toloc.y-pressPoint.y);
    }
}