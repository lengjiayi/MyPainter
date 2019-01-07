import java.awt.*;

class mGAlgorithm {
    /**
     * 计算两点间距离
     */
    public static double DistanceToPoint(Point p1, Point p2)
    {
        double dx=p1.getLocation().x-p2.getLocation().x;
        double dy=p1.getLocation().y-p2.getLocation().y;
        return Math.sqrt(dx*dx+dy*dy);
    }
    /**
     * 计算点到直线距离
     * @param start 直线起点
     * @param start 直线终点
     * @param loc 直线外点坐标
     * @return 点到直线的距离
     */
    public static double DistanceToLine(Point start, Point end, Point loc)
    {
        Point lt,rt;
        if(start.x<end.x) {
            lt=start; rt=end;
        }
        else {
            lt=end; rt=start;
        }
        float A,B,C;
        A=lt.y-rt.y;    B=rt.x-lt.x;    C=-rt.y*B-rt.x*A;
        double dist=(A*loc.x+B*loc.y+C)/Math.sqrt(A*A+B*B);
        return Math.abs(dist);
    }
    public static double filter(double x)
    {
        if(x>=1)
            return 1;
        return x;
    }
    /**
     * 绘制一个像素点
     * @param g Graphic2D对象，用于绘图
     * @param x 像素点在g上的横坐标
     * @param y 像素点在g上的纵坐标
     */
    public static void drawPoint(Graphics2D g, int x, int y)
    {
        if(x>=0 && y>=0)
            g.drawLine(x,y,x,y);
    }
    /**
     * 获取先转后点坐标
     * @param x 像素点在g上的旋转前横坐标
     * @param y 像素点在g上的旋转前纵坐标
     * @param center 旋转中心
     * @param angle 旋转角度（弧度制）
     * @return Point 旋转后坐标
     */
    public static Point rotatePoint(int x, int y, Point center, double angle)
    {
        dPoint relativeloc = new dPoint(0, 0);
        dPoint rloc = new dPoint(0, 0);
        relativeloc.x = x - center.x;
        relativeloc.y = y - center.y;
        //根据公式计算旋转后位置
        rloc.x = relativeloc.x * Math.cos(angle) - relativeloc.y * Math.sin(angle) + center.x;
        rloc.y = relativeloc.x * Math.sin(angle) + relativeloc.y * Math.cos(angle) + center.y;
        x=(int)(rloc.x+0.5);
        y=(int)(rloc.y+0.5);
        return new Point(x,y);
    }
    /**
     * 绘制一个有宽度的像素点
     * @param g Graphic2D对象，用于绘图
     * @param x 像素点在g上的旋转前横坐标
     * @param y 像素点在g上的旋转前纵坐标
     * @param LW 像素点大小（半径）
     */
    public static void drawPoint(Graphics2D g, int x, int y, int LW)
    {
        g.fillOval(x, y, LW, LW);
    }
    /**
     * 绘制一个旋转后的像素点
     * @param g Graphic2D对象，用于绘图
     * @param x 像素点在g上的旋转前横坐标
     * @param y 像素点在g上的旋转前纵坐标
     * @param center 旋转中心
     * @param angle 旋转角度（弧度制）
     * @param LW 像素点大小（半径）
     * @param vs char矩阵，用于写入绘图信息
     */
    public static void drawPoint(Graphics2D g, int x, int y, Point center, double angle, int LW, char[][] vs)
    {
        dPoint relativeloc = new dPoint(0, 0);
        dPoint rloc = new dPoint(0, 0);
        relativeloc.x = x - center.x;
        relativeloc.y = y - center.y;
        //根据公式计算旋转后位置
        rloc.x = relativeloc.x * Math.cos(angle) - relativeloc.y * Math.sin(angle) + center.x;
        rloc.y = relativeloc.x * Math.sin(angle) + relativeloc.y * Math.cos(angle) + center.y;
        x=(int)(rloc.x+0.5);
        y=(int)(rloc.y+0.5);
        if(vs!=null)
            if(y>0 && y<vs.length && x>0 && x<vs[0].length)
                vs[y][x]=1;
        g.fillOval(x, y, LW, LW);
    }
    /**
     * 绘制直线
     * @param g Graphic2D对象，用于绘图
     * @param x1 直线起点横坐标
     * @param y1 直线起点纵坐标
     * @param x2 直线终点横坐标
     * @param y2 直线终点纵坐标
     * @param LineWidth 绘制直线宽度
     */
    public static void drawLine(Graphics2D g, int x1, int y1, int x2, int y2, int LineWidth)
    {
        drawLine(g,x1,y1,x2,y2,LineWidth,null);
    }
    /**
     * 绘制直线
     * @param g Graphic2D对象，用于绘图
     * @param x1 直线起点横坐标
     * @param y1 直线起点纵坐标
     * @param x2 直线终点横坐标
     * @param y2 直线终点纵坐标
     * @param LineWidth 绘制直线宽度
     * @param vs char矩阵，用于写入绘图信息
     */
    public static void drawLine(Graphics2D g, int x1, int y1, int x2, int y2, int LineWidth, char vs[][])
    {
        Color rawColor=g.getColor();
        if(LineWidth==1) {
            SingleAntiLine(g, x1, y1, x2, y2, vs);
            g.setColor(rawColor);
            return;
        }
        if(Math.abs(x1-x2)<Math.abs(y1-y2)) {
            SingleAntiLine(g, x1 + LineWidth, y1, x2 + LineWidth, y2, vs);
            SingleAntiLine(g, x1 - LineWidth, y1, x2 - LineWidth, y2, vs);
            g.setColor(rawColor);
            SingleLine(g, x1, y1, x2, y2, vs);
            for (int i = 1; i < LineWidth; i++) {
                SingleLine(g, x1 + i, y1, x2 + i, y2, vs);
                SingleLine(g, x1 - i, y1, x2 - i, y2, vs);
            }
        }
        else
        {
            SingleAntiLine(g, x1 , y1+ LineWidth, x2 , y2+ LineWidth, vs);
            SingleAntiLine(g, x1 , y1- LineWidth, x2 , y2- LineWidth, vs);
            g.setColor(rawColor);
            SingleLine(g, x1, y1, x2, y2, vs);
            for (int i = 1; i < LineWidth; i++) {
                SingleLine(g, x1 , y1+ i, x2 , y2+ i, vs);
                SingleLine(g, x1 , y1- i, x2 , y2- i, vs);
            }
        }
    }
    /**
     * 绘制宽度为1的反走样直线
     * @param g Graphic2D对象，用于绘图
     * @param x1 直线起点横坐标
     * @param y1 直线起点纵坐标
     * @param x2 直线终点横坐标
     * @param y2 直线终点纵坐标
     * @param vs char矩阵，用于写入绘图信息
     */
    public static void SingleAntiLine(Graphics2D g, int x1, int y1, int x2, int y2, char vs[][])
    {
        //反走样算法
        double dx=x2-x1;
        double dy=y2-y1;
        double e=Math.abs(dx)>Math.abs(dy)?Math.abs(dx):Math.abs(dy);	//选择长宽中较大者作为像素点数，防止出现遗漏像素点
        dx/=e;		//计算两个方向的差分
        dy/=e;
        double x=x1, y=y1;
        Color color=g.getColor();
        Point[] around=new Point[4];                //对于每个采样点周围都绘制四个点
        Color[] acolors=new Color[4];               //acolor储存这四个点的颜色
        Point start=new Point(x1,y1);
        Point end=new Point(x2,y2);
        for(int i=0;i<=e;i++)
        {
            around[0]=new Point((int)x, (int)y);
            around[1]=new Point(around[0].x+1, around[0].y);
            around[2]=new Point(around[0].x+1, around[0].y+1);
            around[3]=new Point(around[0].x+1, around[0].y+1);
            for(int j=0;j<4;j++)
            {
                double rate=1-filter(DistanceToLine(start,end,around[j]));  //利用该点和直线的距离计算该点色度
                double rt=255-color.getRed();
                double gt=255-color.getGreen();
                double bt=255-color.getBlue();
                rt=255-rt*rate;
                gt=255-gt*rate;
                bt=255-bt*rate;
                acolors[j]=new Color((int)rt, (int)gt, (int)bt);
                g.setColor(acolors[j]);
                drawPoint(g, around[j].x, around[j].y);		//绘制一个像素点，这里使用drawLine实现
                if(vs!=null && around[j].x>=0 && around[j].y>=0 && around[j].y<vs.length && around[j].x< vs[0].length)
                    vs[around[j].y][around[j].x]=1;
            }
            x+=dx;	//计算下一个位置
            y+=dy;
        }
    }
    /**
     * 绘制宽度为1的DDA直线
     * @param g Graphic2D对象，用于绘图
     * @param x1 直线起点横坐标
     * @param y1 直线起点纵坐标
     * @param x2 直线终点横坐标
     * @param y2 直线终点纵坐标
     * @param vs char矩阵，用于写入绘图信息
     */
    public static void SingleLine(Graphics2D g, int x1, int y1, int x2, int y2, char vs[][])
    {
        //DDA算法
        double dx=x2-x1;
        double dy=y2-y1;
        double e=Math.abs(dx)>Math.abs(dy)?Math.abs(dx):Math.abs(dy);	//选择长宽中较大者作为像素点数，防止出现遗漏像素点
        dx/=e;		//计算两个方向的差分
        dy/=e;
        double x=x1, y=y1;
        for(int i=0;i<=e;i++)
        {
            if(vs!=null && (int)(y+0.5)>=0 && (int)(x+0.5)>=0)
                vs[(int)(y+0.5)][(int)(x+0.5)]=1;
            drawPoint(g, (int)(x+0.5), (int)(y+0.5));		//使用fillOval绘制像素点
            x+=dx;	//计算下一个位置
            y+=dy;
        }
    }
    /**
     * 绘制圆形
     * @param g Graphic2D对象，用于绘图
     * @param x0 圆外接矩形的起点横坐标
     * @param y0  圆外接矩形的起点纵坐标
     * @param r 圆的半径
     * @param LineWidth 圆边界宽度
     */
    public static void drawCircle(Graphics2D g, int x0, int y0, int r, int LineWidth)
    {
        LineWidth++;
        int x=0, y=r;
        while(x<=(int)r/Math.sqrt(2)+1)
        {
            g.fillOval(x+x0-LineWidth/2, y+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(-x+x0-LineWidth/2, y+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(x+x0-LineWidth/2, -y+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(-x+x0-LineWidth/2, -y+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(y+x0-LineWidth/2, x+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(-y+x0-LineWidth/2, x+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(y+x0-LineWidth/2, -x+y0-LineWidth/2, LineWidth, LineWidth);
            g.fillOval(-y+x0-LineWidth/2, -x+y0-LineWidth/2, LineWidth, LineWidth);
            if((float)x*x+((float)y-0.5)*((float)y-0.5)-(float)r*r>0)
                y--;
            x++;
        }
    }
    /**
     * 绘制椭圆
     * @param g Graphic2D对象，用于绘图
     * @param x0 椭圆中心横坐标
     * @param y0 椭圆中心纵坐标
     * @param width 椭圆外接矩形宽度
     * @param height 椭圆外接矩形高度
     * @param LineWidth 椭圆边界宽度
     * @param angle 旋转角度
     * @param vs char矩阵，用于写入绘图信息
     */
    public static void drawOval(Graphics2D g, int x0, int y0, int width, int height, int LineWidth, double angle, char[][] vs)
    {
        LineWidth++;
        double a=width/2, b=height/2;
        double c=Math.sqrt(a*a-b*b);
        float x=0,y=(int)(height/2);
        double part1=a*a/Math.sqrt(a*a+b*b);        //计算分界点
        Point center = new Point(x0, y0);
        while(x<=part1)         //绘制上半段
        {
            drawPoint(g,(int)x+x0-LineWidth/2, (int)y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)-x+x0-LineWidth/2, (int)y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)x+x0-LineWidth/2, (int)-y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)-x+x0-LineWidth/2, (int)-y+y0-LineWidth/2, center, angle, LineWidth, vs);
            x++;
            if(a*a*(y-0.5)*(y-0.5)+b*b*x*x>a*a*b*b)
                y--;
        }
        while(y>=0)             //绘制下半段
        {
            drawPoint(g,(int)x+x0-LineWidth/2, (int)y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)-x+x0-LineWidth/2, (int)y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)x+x0-LineWidth/2, (int)-y+y0-LineWidth/2, center, angle, LineWidth, vs);
            drawPoint(g,(int)-x+x0-LineWidth/2, (int)-y+y0-LineWidth/2, center, angle, LineWidth, vs);
            y--;
            if(a*a*y*y+b*b*(x+0.5)*(x+0.5)<=a*a*b*b)
                x++;
        }
    }
    /**
     * 绘制填充椭圆
     * @param g Graphic2D对象，用于绘图
     * @param x0 椭圆外接矩形的起点横坐标
     * @param y0 椭圆外接矩形的起点纵坐标
     * @param width 椭圆外接矩形宽度
     * @param height 椭圆外接矩形高度
     */
    public static void fillOval(Graphics2D g, int x0, int y0, int width, int height, double angle)
    {
        double a=width/2, b=height/2;
        double c=Math.sqrt(a*a-b*b);
        float x=0,y=(int)(height/2);
        double part1=a*a/Math.sqrt(a*a+b*b);        //计算分界点
        Point center = new Point(x0, y0);
        Point[] tmppoints=new Point[4];
        while(x<=part1)         //绘制上半段
        {
            tmppoints[0] = rotatePoint((int)x+x0, (int)y+y0, center, angle);
            tmppoints[1] = rotatePoint((int)-x+x0, (int)y+y0, center, angle);
            tmppoints[2] = rotatePoint((int)x+x0, (int)-y+y0, center, angle);
            tmppoints[3] = rotatePoint((int)-x+x0, (int)-y+y0, center, angle);
//            SingleLine(g,tmppoints[0].x, tmppoints[0].y, tmppoints[1].x, tmppoints[1].y,null);
//            SingleLine(g,tmppoints[2].x, tmppoints[2].y, tmppoints[3].x, tmppoints[3].y,null);
            g.drawLine(tmppoints[0].x, tmppoints[0].y, tmppoints[1].x, tmppoints[1].y);
            g.drawLine(tmppoints[2].x, tmppoints[2].y, tmppoints[3].x, tmppoints[3].y);
            x++;
            if(a*a*(y-0.5)*(y-0.5)+b*b*x*x>a*a*b*b)
                y--;
        }
        while(y>=0)             //绘制下半段
        {
            tmppoints[0] = rotatePoint((int)x+x0, (int)y+y0, center, angle);
            tmppoints[1] = rotatePoint((int)-x+x0, (int)y+y0, center, angle);
            tmppoints[2] = rotatePoint((int)x+x0, (int)-y+y0, center, angle);
            tmppoints[3] = rotatePoint((int)-x+x0, (int)-y+y0, center, angle);
//            SingleLine(g,tmppoints[0].x, tmppoints[0].y, tmppoints[1].x, tmppoints[1].y,null);
//            SingleLine(g,tmppoints[2].x, tmppoints[2].y, tmppoints[3].x, tmppoints[3].y,null);
            g.drawLine(tmppoints[0].x, tmppoints[0].y, tmppoints[1].x, tmppoints[1].y);
            g.drawLine(tmppoints[2].x, tmppoints[2].y, tmppoints[3].x, tmppoints[3].y);
            y--;
            if(a*a*y*y+b*b*(x+0.5)*(x+0.5)<=a*a*b*b)
                x++;
        }
    }
    /**
     * 绘制3维Bezier曲线
     * @param g Graphic2D对象，用于绘图
     * @param x0 曲线起点
     * @param y0 曲线终点
     * @param ctrlx 控制点横坐标
     * @param ctrly 控制点纵坐标
     * @param LineWidth 曲线宽度
     */
    public static void drawCurveR3(Graphics2D g, int x0, int y0, int ctrlx, int ctrly, int x1, int y1, int LineWidth)    //Bezier曲线几何意义实现
    {
        LineWidth++;
        double e=Math.max(Math.abs(ctrlx-x0),Math.abs(x1-ctrlx));
        e=Math.max(e,Math.abs(ctrly-y0));
        e=Math.max(e,Math.abs(y1-ctrly));
        e=Math.max(e,Math.abs(y1-y0));
        e=Math.max(e,Math.abs(x1-x0));

        dPoint p1=new dPoint();
        dPoint p2=new dPoint();
        dPoint p=new dPoint();
        double u;
        for(int i=0;i<e;i++)
        {
            u=i/e;
            p1.x=(ctrlx-x0)*u+x0;
            p1.y=(ctrly-y0)*u+y0;
            p2.x=(x1-ctrlx)*u+ctrlx;
            p2.y=(y1-ctrly)*u+ctrly;
            p.x=p1.x+(p2.x-p1.x)*u;
            p.y=p1.y+(p2.y-p1.y)*u;
            g.fillOval((int)(p.x+0.5), (int)(p.y+0.5), LineWidth, LineWidth);
        }
    }
    /**
     * 求解阶乘n!
     */

    public static int Factorial(int n)
    {
        int ret=1;
        for(int i=1;i<=n;i++)
            ret*=i;
        return ret;
    }
    /**
     * 求解组合数C(i,n)
     */
    public static int CNum(int i,int n)
    {
        if(i==0)
            return 1;
        double ret=(double)Factorial(n)/Factorial(i)/Factorial(n-i);
        return (int)ret;
    }

    public static dPoint BezierPoint(Point[] points, double u)
    {
        dPoint loc=new dPoint(0,0);
        int rank=points.length-1;
        if(rank>10)
            return null;
        for(int i=0;i<rank+1;i++)
        {
            loc.x+=points[i].x*CNum(i,rank)*Math.pow(u,i)*Math.pow(1-u,rank-i);
            loc.y+=points[i].y*CNum(i,rank)*Math.pow(u,i)*Math.pow(1-u,rank-i);
        }
        return loc;
    }
    /**
     * 绘制任意维Bezier曲线
     * @param g 用来绘制的画布
     * @param points 控制点和起始点数组，数组第一位为起始点，最后一位为终止点
     * @param e 采样点数
     * @param LineWidth 曲线宽度
     */
    public static void drawCurve(Graphics2D g, Point[] points,int e,  int LineWidth)
    {
        LineWidth++;
        dPoint p;
        double u;
        for(int i=0;i<e;i++)
        {
            u=(float)i/e;
            p=BezierPoint(points,u);
            g.fillOval((int)(p.x+0.5), (int)(p.y+0.5), LineWidth, LineWidth);
        }
    }

    public static boolean IsInRect(Point start, Point size, Point p)
    {
        if(p.x < start.x || p.x > start.x+size.x)
            return false;
        if(p.y < start.y || p.y > start.y+size.y)
            return false;
        return true;
    }
}

class dPoint{
    public double x,y;
    public dPoint() {
        x=0;    y=0;
    }
    public dPoint(Point p) {
        x=p.x;    y=p.y;
    }
    public dPoint(Dimension p) {
        x=p.width;    y=p.height;
    }
    public dPoint(double x0, double y0) {
        x=x0;   y=y0;
    }
}
