import java.awt.*;

public class mCutWindowAlg {
    /**
     * 在start, size组成的窗口上绘制直线
     * @param start 窗口起始点
     * @param size 窗口大小
     * @param p1 直线端点
     * @param p2 直线端点
     * @return 裁剪后直线在窗口中的端点
     */
    public static Point[] drawLineInWindow(Point start, Point size, Point p1, Point p2)
    {
        p1.x+=5;
        p1.y+=5;
        p2.x+=5;
        p2.y+=5;
        Point[] realp=new Point[2];
        int index=0;
        if(mGAlgorithm.IsInRect(start, size, p1))
        {
            realp[index]=p1;
            index++;
        }
        if(mGAlgorithm.IsInRect(start, size, p2))
        {
            realp[index]=p2;
            index++;
        }
        if(index<=1)
        {
            double u,tmp;
            if(p1.x!=p2.x)
            {
                u=(start.x - p1.x) / (double) (p2.x - p1.x);
                tmp=p1.y+u*(p2.y-p1.y);
                if(u>0 && u<1)
                {
                    if(tmp>start.y && tmp<start.y+size.y)
                    {
                        realp[index] = new Point(start.x, (int) (tmp + 0.5));
                        index++;
                    }
                }
                u=(start.x+size.x-p1.x)/(double)(p2.x-p1.x);
                tmp=p1.y+u*(p2.y-p1.y);
                if(index<=1 && u>0 && u<1)
                {
                    if(tmp>start.y && tmp<start.y+size.y)
                    {
                        realp[index] = new Point(start.x+size.x, (int) (tmp + 0.5));
                        index++;
                    }
                }
            }
            if(p1.y!=p2.y)
            {
                u=(start.y - p1.y) / (double) (p2.y - p1.y);
                tmp=p1.x+u*(p2.x-p1.x);
                if(index<=1 && u>0 && u<1)
                {
                    if(tmp>start.x && tmp<start.x+size.x)
                    {
                        realp[index] = new Point((int) (tmp + 0.5), start.y);
                        index++;
                    }
                }
                u=(start.y+size.y-p1.y)/(double)(p2.y-p1.y);
                tmp=p1.x+u*(p2.x-p1.x);
                if(index<=1 && u>0 && u<1)
                {
                    if(tmp>start.x && tmp<start.x+size.x)
                    {
                        realp[index] = new Point((int) (tmp + 0.5), start.y+size.y);
                        index++;
                    }
                }
            }
        }
        if(index<=1)
            return null;
        return realp;
    }
}
