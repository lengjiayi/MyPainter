import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.awt.*;

class mCutWindowHelper
{
    /**
     * 在start, size组成的窗口上绘制直线
     * @param zw 裁剪窗口
     * @param start 窗口起始点
     * @param size 窗口大小
     * @param pp1 直线起点
     * @param pp2 直线终点
     * @param LineWidth 直线宽度
     * @param color 直线颜色
     */    public static void drawLine(zoomWindow zw, Point start, Point size, Point pp1, Point pp2, int LineWidth, Color color) {
        Point[] ps;
        Graphics2D g2=(Graphics2D) zw.getGraphics();
        int width=zw.getWidth();
        int height=zw.getHeight();
        Point p1, p2;
        if(pp1.x > pp2.x)
        {
            p1=pp2;
            p2=pp1;
        }
        else
        {
            p1=pp1;
            p2=pp2;
        }
        ps=mCutWindowAlg.drawLineInWindow(start, size, p1, p2);
        if(g2!=null && ps!=null) {
            for(Point p:ps)
            {
                p.x-=start.x;
                p.y-=start.y;
                p.x=(int)(p.x*width/(double)size.x);
                p.y=(int)(p.y*height/(double)size.y);
            }
            mGAlgorithm.drawLine(g2, ps[0].x, ps[0].y, ps[1].x, ps[1].y, LineWidth);
        }
    }
}
