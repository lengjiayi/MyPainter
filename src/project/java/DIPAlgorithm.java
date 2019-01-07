import Jama.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DIPAlgorithm {

    /**
     * 提取(x, y)处的颜色
     * @param img 输入rgb图像
     * @param x 取样点横坐标
     * @param y 取样点纵坐标
     * @return rgba数组
     */
    public static int[] getColor(BufferedImage img, int x, int y)
    {
        int[] color=new int[4];
        Object data = img.getRaster().getDataElements(x, y, null);//获取该点像素，并以object类型表示
        color[0]=img.getColorModel().getRed(data);
        color[1]=img.getColorModel().getGreen(data);
        color[2]=img.getColorModel().getBlue(data);
        color[3]=img.getColorModel().getAlpha(data);
        return color;
    }

    /**
     * 将rgb图像转化成灰度图像
     * @param img 输入rgb图像
     * @param pbar 算法进度条，可为空
     */
    public static BufferedImage getGray(BufferedImage img, processBar pbar)
    {
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imageWidth,0);
        for(int i = img.getMinX();i < imageWidth ;i++) {
            for (int j = img.getMinY(); j < imageHeight; j++) {
                Object data = img.getRaster().getDataElements(i, j, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                r=g=b=(r*3+g*6+b)/10;

                argb=((a*256+r)*256+g)*256+b;
                img.setRGB(i,j,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imageWidth,i);
        }
        if(pbar!=null)
            pbar.setvalue(imageWidth,imageWidth);
        return img;
    }
    /**
     * 获取rgb图像的灰度直方图
     * @param img 输入rgb图像
     * @return 二维数组，每一维代表一个channel的灰度直方图数据
     */
    public static int[][] getHist(BufferedImage img)
    {
        int[][] hist=new int[3][256];
        for(int i=0;i<3;i++)
            for(int j=0;j<256;j++)
                hist[i][j]=0;
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        int r,g,b;
        for(int i = img.getMinX();i < imageWidth ;i++) {
            for (int j = img.getMinY(); j < imageHeight; j++) {
                Object data = img.getRaster().getDataElements(i, j, null);//获取该点像素，并以object类型表示
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                hist[0][r]++;
                hist[1][g]++;
                hist[2][b]++;
            }
        }
        return hist;
    }
    /**
     * 直方图均衡化
     * @param img 输入rgb图像
     * @param hist 输入三个channel的灰度直方图数组
     * @param pbar 算法进度条，可为空
     * @return 均衡化后的图像
     */
    public static BufferedImage HistEqualization(BufferedImage img, int[][] hist, processBar pbar)
    {
        double[][] CDF=new double[3][256];
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imageWidth,0);
        for(int i=0;i<3;i++)
            for(int j=0;j<256;j++)
                CDF[i][j]=0;
        CDF[0][0]=hist[0][0];
        CDF[1][0]=hist[1][0];
        CDF[2][0]=hist[2][0];
        for(int i=1;i<256;i++)
        {
            CDF[0][i]=CDF[0][i-1]+hist[0][i];
            CDF[1][i]=CDF[1][i-1]+hist[1][i];
            CDF[2][i]=CDF[2][i-1]+hist[2][i];
        }
        double rate=256.0/(imageHeight*imageWidth);
        for(int i=1;i<256;i++)
        {
            CDF[0][i]=CDF[0][i]*rate;
            CDF[1][i]=CDF[1][i]*rate;
            CDF[2][i]=CDF[2][i]*rate;
        }
        for(int i = img.getMinX();i < imageWidth ;i++) {
            for (int j = img.getMinY(); j < imageHeight; j++) {
                Object data = img.getRaster().getDataElements(i, j, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                r=(int)CDF[0][r];
                g=(int)CDF[1][g];
                b=(int)CDF[2][b];
                argb=((a*256+r)*256+g)*256+b;
                img.setRGB(i,j,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imageWidth,i);
        }
        if(pbar!=null)
            pbar.setvalue(imageWidth,imageWidth);
        return img;
    }

    public static BufferedImage filterDiv(BufferedImage img, int fr, int fg, int fb, processBar pbar)
    {
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        BufferedImage ret = new BufferedImage(imageWidth, imageHeight, img.getType());
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imageWidth,0);
        for(int i = img.getMinX();i < imageWidth ;i++) {
            for (int j = img.getMinY(); j < imageHeight; j++) {
                Object data = img.getRaster().getDataElements(i, j, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                if(r<fr) r=0;
                if(g<fg) g=0;
                if(b<fb) b=0;
                argb=((a*256+r)*256+g)*256+b;
                ret.setRGB(i,j,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imageWidth,i);
        }
        if(pbar!=null)
            pbar.setvalue(imageWidth,imageWidth);
        return ret;
    }


    //控制栅格插值变形
    private static float dotProduct(Point p1, Point p2) {
        return p1.x * p2.x + p1.y * p2.y;
    }
    //判断是否在三角形内
    private static boolean isInTriangle(Point p, Point a, Point b, Point c) {
        Point AB, AC, AP;
        AB = new Point(b.x - a.x, b.y - a.y);
        AC = new Point(c.x - a.x, c.y - a.y);
        AP = new Point(p.x - a.x, p.y - a.y);
        float dot00 = dotProduct(AC, AC);
        float dot01 = dotProduct(AC, AB);
        float dot02 = dotProduct(AC, AP);
        float dot11 = dotProduct(AB, AB);
        float dot12 = dotProduct(AB, AP);
        float inverDeno = 1 / (dot00 * dot11 - dot01 * dot01);
        // 计算重心坐标
        float u = (dot11 * dot02 - dot01 * dot12) * inverDeno;
        float v = (dot00 * dot12 - dot01 * dot02) * inverDeno;
        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
    /**
     * 将p1,p2,p3围成的三角形映射到q1,q2,q3围成的三角形中
     * @param img 原图片
     * @param deformimg 映射后图片
     */
    private static void deformTriangle(BufferedImage img, BufferedImage deformimg, Point q1, Point q2, Point q3, Point p1, Point p2, Point p3)
    {
        int width=img.getWidth();
        int height=img.getHeight();
        double[][] A=new double[3][3];
        double[][] B=new double[2][3];
        Point[] trips=new Point[3];
        int a,r,g,b,argb;
        A[0][0] = q1.x;  A[0][1] = q1.y;  A[0][2] = 1;
        A[1][0] = q2.x;  A[1][1] = q2.y;  A[1][2] = 1;
        A[2][0] = q3.x;  A[2][1] = q3.y;  A[2][2] = 1;
        B[0][0] = p1.x;  B[1][0] = p1.y;
        B[0][1] = p2.x;  B[1][1] = p2.y;
        B[0][2] = p3.x;  B[1][2] = p3.y;
        trips[0]=q1; trips[1]=q2;   trips[2]=q3;
        Matrix matrixA=new Matrix(A);
        matrixA=matrixA.transpose();
        Matrix matrixB=new Matrix(B);
        Matrix M = matrixB.times(matrixA.inverse());
        Point lu=new Point(trips[0]);
        Point rb=new Point(trips[0]);
        lu.x=Math.min(lu.x, trips[1].x);
        lu.x=Math.min(lu.x, trips[2].x);
        lu.y=Math.min(lu.y, trips[1].y);
        lu.y=Math.min(lu.y, trips[2].y);
        rb.x=Math.max(rb.x, trips[1].x);
        rb.x=Math.max(rb.x, trips[2].x);
        rb.y=Math.max(rb.y, trips[1].y);
        rb.y=Math.max(rb.y, trips[2].y);
        for (int m = lu.y; m < rb.y; m++) {
            for (int n = lu.x; n < rb.x; n++) {
                if(isInTriangle(new Point(n,m),trips[0],trips[1],trips[2]))
                {
                    int rx = (int) (M.get(0, 0) * n + M.get(0, 1) * m + M.get(0, 2));
                    int ry = (int) (M.get(1, 0) * n + M.get(1, 1) * m + M.get(1, 2));
                    rx=Math.max(rx,0);
                    ry=Math.max(ry,0);
                    rx=Math.min(rx,width-1);
                    ry=Math.min(ry,height-1);
                    Object data = img.getRaster().getDataElements(rx, ry, null);//获取该点像素，并以object类型表示
                    a = img.getColorModel().getAlpha(data);
                    r = img.getColorModel().getRed(data);
                    g = img.getColorModel().getGreen(data);
                    b = img.getColorModel().getBlue(data);
                    argb = ((a*256+r) * 256 + g) * 256 + b;
                    deformimg.setRGB(n, m, argb);
                }
            }
        }
    }
    /**
     * 三角网格形变算法
     * @param img 输入rgb图像
     * @param ops 三角网格控制点在原图片上的坐标
     * @param pbar 算法进度条，可为空
     * @return 形变后的图像
     */
    public static BufferedImage deform(BufferedImage img, Point[][] ops, processBar pbar)
    {
        BufferedImage deformimg=new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        int width=img.getWidth();
        int height=img.getHeight();
        double dy=height/6.0;
        double dx=width/6.0;
        if(pbar!=null)
            pbar.setvalue(6,0);
        Point[][] realloc=new Point[7][];
        for (int i = 0; i < 7; i++) {
            int row=6+i%2;
            realloc[i]=new Point[row];
            for (int j = 0; j < row; j++) {
                if(i%2==0)
                    realloc[i][j]=new Point((int) (dx*j+dx/2), (int) (dy*i));
                else
                    realloc[i][j]=new Point((int) (dx*j), (int) (dy*i));
            }
        }
        for(int i=0;i<6;i++) {
            for (int j = i%2; j < 11+i%2; j++) {
                //TODO 映射三角形网格
                int j1=j/2;
                if(j%2==0)
                {
                    deformTriangle(img,deformimg,ops[i][j1],ops[i+1][j1-i%2],ops[i+1][j1-i%2+1],realloc[i][j1],realloc[i+1][j1-i%2],realloc[i+1][j1-i%2+1]);
                }
                else
                {
                    deformTriangle(img,deformimg,ops[i][j1],ops[i][j1+1],ops[i+1][j1+(i+1)%2],realloc[i][j1],realloc[i][j1+1],realloc[i+1][j1+(i+1)%2]);
                }
            }
            if(pbar!=null)
                pbar.setvalue(6,i);
        }
        //TODO 填充边界
        deformTriangle(img,deformimg,new Point(0,0),ops[0][1],ops[1][0],new Point(0,0),realloc[0][1],realloc[1][0]);
        deformTriangle(img,deformimg,ops[1][0],ops[2][0],ops[3][0],realloc[1][0],realloc[2][0],realloc[3][0]);
        deformTriangle(img,deformimg,ops[3][0],ops[4][0],ops[5][0],realloc[3][0],realloc[4][0],realloc[5][0]);
        deformTriangle(img,deformimg,new Point(0,height),ops[5][0],ops[6][0],new Point(0,height),realloc[5][0],realloc[6][0]);

        deformTriangle(img,deformimg,new Point(width,0),ops[0][5],ops[1][6],new Point(width,0),realloc[0][5],realloc[1][6]);
        deformTriangle(img,deformimg,ops[1][6],ops[2][5],ops[3][6],realloc[1][6],realloc[2][5],realloc[3][6]);
        deformTriangle(img,deformimg,ops[3][6],ops[4][5],ops[5][6],realloc[3][6],realloc[4][5],realloc[5][6]);
        deformTriangle(img,deformimg,new Point(width,height),ops[5][6],ops[6][5],new Point(width,height),realloc[5][6],realloc[6][5]);
        if(pbar!=null)
            pbar.setvalue(6,6);
        return deformimg;
    }
    /** 高斯滤波器 1:79 */
    static int[][] GaussianFilter=new int[][]{{1,2,3,2,1},{2,4,6,4,2},{3,6,7,6,3},{2,4,6,4,2},{1,2,3,2,1}};
    /**
     * 高斯滤波
     * @param img 输入rgb图像
     * @param pbar 算法进度条，可为空
     * @return 经过高斯函数滤波后的图像
     */
    public static BufferedImage GaussianFilter(BufferedImage img, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        BufferedImage filtimg=new BufferedImage(imgWidth,imgHeight,img.getType());
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imgHeight,0);
        for(int i=0;i<imgHeight;i++)
        {
            for(int j=0;j<imgWidth;j++)
            {
                int fa=0,fr=0,fg=0,fb=0;
                for(int p=-2;p<3;p++)
                {
                    for(int q=-2;q<3;q++)
                    {
                        int rx=((j+q)<0 || (j+q)>=imgWidth)?(j-q):(j+q);
                        int ry=((i+p)<0 || (i+p)>=imgHeight)?(i-p):(i+p);
                        Object data = img.getRaster().getDataElements(rx, ry, null);//获取该点像素，并以object类型表示
                        a=img.getColorModel().getAlpha(data);
                        r=img.getColorModel().getRed(data);
                        g=img.getColorModel().getGreen(data);
                        b=img.getColorModel().getBlue(data);
                        fa+=a*GaussianFilter[p+2][q+2];
                        fr+=r*GaussianFilter[p+2][q+2];
                        fg+=g*GaussianFilter[p+2][q+2];
                        fb+=b*GaussianFilter[p+2][q+2];
                    }
                }
                fa=(int)(fa/79.0);
                fr=(int)(fr/79.0);
                fg=(int)(fg/79.0);
                fb=(int)(fb/79.0);
                argb=((fa*256+fr)*256+fg)*256+fb;
                filtimg.setRGB(j,i,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imgHeight,i);
        }
        if(pbar!=null)
            pbar.setvalue(imgHeight,imgHeight);
        return filtimg;
    }
    /**
     * 中值滤波
     * @param img 输入rgb图像
     * @param pbar 算法进度条，可为空
     * @return 经过中值滤波后的图像
     */
    public static BufferedImage MidFilter(BufferedImage img, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        BufferedImage filtimg=new BufferedImage(imgWidth,imgHeight,img.getType());
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imgHeight,0);
        for(int i=0;i<imgHeight;i++)
        {
            for(int j=0;j<imgWidth;j++)
            {
                Object data = img.getRaster().getDataElements(j, i, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                ArrayList<Integer> aroundr=new ArrayList<>();
                ArrayList<Integer> aroundg=new ArrayList<>();
                ArrayList<Integer> aroundb=new ArrayList<>();
                for(int p=-2;p<3;p++)
                {
                    for(int q=-2;q<3;q++)
                    {
                        int rx=((j+q)<0 || (j+q)>=imgWidth)?(j-q):(j+q);
                        int ry=((i+p)<0 || (i+p)>=imgHeight)?(i-p):(i+p);
                        data = img.getRaster().getDataElements(rx, ry, null);//获取该点像素，并以object类型表示
                        r=img.getColorModel().getRed(data);
                        g=img.getColorModel().getGreen(data);
                        b=img.getColorModel().getBlue(data);
                        aroundr.add(r);
                        aroundg.add(g);
                        aroundb.add(b);
                    }
                }
                aroundr.sort(null);
                aroundg.sort(null);
                aroundb.sort(null);
                argb=((a*256+aroundr.get(2))*256+aroundg.get(2))*256+aroundb.get(2);
                filtimg.setRGB(j,i,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imgHeight,i);
        }
        if(pbar!=null)
            pbar.setvalue(imgHeight,imgHeight);
        return filtimg;
    }
    /**
     * OSTU大律法二值化
     * @param img 输入rgb图像
     * @param hist 输入图像直方图
     * @param pbar 算法进度条，可为空
     * @return 二值化后的图像
     */
    public static BufferedImage Binarilize(BufferedImage img, int[][] hist, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        int[] threshold=getThreshold(hist);
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imgHeight,0);
        for(int i=0;i<imgHeight;i++)
        {
            for(int j=0;j<imgWidth;j++)
            {
                Object data = img.getRaster().getDataElements(j, i, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                r=(r>threshold[0])?255:0;
                g=(g>threshold[1])?255:0;
                b=(b>threshold[2])?255:0;
                argb=((a*256+r)*256+g)*256+b;
                img.setRGB(j,i,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imgHeight,i);
        }
        if(pbar!=null)
            pbar.setvalue(imgHeight,imgHeight);
        return img;
    }
    private static int[] getThreshold(int[][] hist)
    {
        int[] threshold=new int[3];
        double[] maxg=new double[]{0,0,0};
        double[][] CDF=new double[3][256];
        double[][] integration=new double[3][256];
        double w0=0,w1=0,u0=0,u1=0,g=0;

        for(int i=0;i<3;i++)
            for(int j=0;j<256;j++)
                CDF[i][j]=integration[i][j]=0;
        CDF[0][0]=hist[0][0];   CDF[1][0]=hist[1][0];   CDF[2][0]=hist[2][0];
        for(int i=1;i<256;i++)
        {
            CDF[0][i]=CDF[0][i-1]+hist[0][i];
            CDF[1][i]=CDF[1][i-1]+hist[1][i];
            CDF[2][i]=CDF[2][i-1]+hist[2][i];
            integration[0][i]=integration[0][i-1]+hist[0][i]*i;
            integration[1][i]=integration[1][i-1]+hist[1][i]*i;
            integration[2][i]=integration[2][i-1]+hist[2][i]*i;
        }
        for(int i=0;i<256;i++)
        {
            for(int j=0;j<3;j++)
            {
                w0=CDF[j][i]/CDF[0][255];
                w1=1-w0;
                u0=integration[j][i]/CDF[j][i];
                u1=(integration[j][255]-integration[j][i])/(CDF[j][255]-CDF[j][i]);
                g=w0*w1*(u0-u1)*(u0-u1);
                if(g>maxg[j])
                {
                    maxg[j]=g;
                    threshold[j]=i;
                }
            }
        }
        return threshold;
    }
    /**
     * 形态学扩张操作
     * @param img 输入二值图像
     * @param pbar 算法进度条，可为空
     * @return 扩张后图像
     */
    public static BufferedImage dilation(BufferedImage img, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        BufferedImage dilationimg=new BufferedImage(imgWidth, imgHeight, img.getType());
        int a=255,r=255,g=255,b=255;
        int argb=((a*256+r)*256+g)*256+b;
        if(pbar!=null)
            pbar.setvalue(imgHeight,0);
        for(int i=0;i<imgHeight;i++)
            for (int j = 0; j < imgWidth; j++)
                dilationimg.setRGB(j,i,argb);
        for(int i=0;i<imgHeight;i++) {
            for (int j = 0; j < imgWidth; j++) {
                Object data = img.getRaster().getDataElements(j, i, null);//获取该点像素，并以object类型表示
                a=img.getColorModel().getAlpha(data);
                r=img.getColorModel().getRed(data);
                g=img.getColorModel().getGreen(data);
                b=img.getColorModel().getBlue(data);
                for (int p = -2; p < 3; p++) {
                    for (int q = -2; q < 3; q++) {
                        int nr,ng,nb,na;
                        int rx=((j+q)<0 || (j+q)>=imgWidth)?(j-q):(j+q);
                        int ry=((i+p)<0 || (i+p)>=imgHeight)?(i-p):(i+p);
                        data = dilationimg.getRaster().getDataElements(rx, ry, null);//获取该点像素，并以object类型表示
                        na=dilationimg.getColorModel().getAlpha(data);
                        nr=dilationimg.getColorModel().getRed(data);
                        ng=dilationimg.getColorModel().getGreen(data);
                        nb=dilationimg.getColorModel().getBlue(data);
                        na=Math.min(a,na);
                        nr=Math.min(r,nr);
                        ng=Math.min(g,ng);
                        nb=Math.min(b,nb);
                        argb=((na*256+nr)*256+ng)*256+nb;
                        dilationimg.setRGB(rx,ry,argb);
                    }
                }
            }
            if(pbar!=null)
                pbar.setvalue(imgHeight,i);
        }
        if(pbar!=null)
            pbar.setvalue(imgHeight,imgHeight);
        return dilationimg;
    }
    /**
     * 形态学腐蚀操作
     * @param img 输入二值图像
     * @param pbar 算法进度条，可为空
     * @return 腐蚀后图像
     */
    public static BufferedImage erosion(BufferedImage img, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        BufferedImage erosionimg=new BufferedImage(imgWidth, imgHeight, img.getType());
        int a,r,g,b,argb;
        if(pbar!=null)
            pbar.setvalue(imgHeight,0);
        for(int i=0;i<imgHeight;i++) {
            for (int j = 0; j < imgWidth; j++) {
                a=0; r=0;  g=0;  b=0;
                for (int p = -2; p < 3; p++) {
                    for (int q = -2; q < 3; q++) {
                        int na,nr,ng,nb;
                        int rx=((j+q)<0 || (j+q)>=imgWidth)?(j-q):(j+q);
                        int ry=((i+p)<0 || (i+p)>=imgHeight)?(i-p):(i+p);
                        Object data = img.getRaster().getDataElements(rx, ry, null);//获取该点像素，并以object类型表示
                        na=img.getColorModel().getAlpha(data);
                        nr=img.getColorModel().getRed(data);
                        ng=img.getColorModel().getGreen(data);
                        nb=img.getColorModel().getBlue(data);
                        a=Math.max(na,a);
                        r=Math.max(nr,r);
                        g=Math.max(ng,g);
                        b=Math.max(nb,b);
                    }
                }
                argb=((a*256+r)*256+g)*256+b;
                erosionimg.setRGB(j,i,argb);
            }
            if(pbar!=null)
                pbar.setvalue(imgHeight,i);
        }
        if(pbar!=null)
            pbar.setvalue(imgHeight,imgHeight);
        return erosionimg;
    }

    public static double getdist(double a, double b)
    {
        return Math.sqrt(a*a+b*b);
    }

    /**
     * 二维傅里叶变换
     * @param pict 待变换方阵，大小为Size*Size
     * @param Size 矩阵大小
     * @return Fourier结构体，用于傅里叶逆变换还原
     */
    private static FourierInfo DFT(double[][] pict, int Size, processBar pbar)
    {
        double[][] Re = new double[Size][Size];
        double[][] Lm = new double[Size][Size];
        if(pbar != null)
            pbar.setvalue(Size,0);
        for(int i=0;i<Size;i++)
        {
            for(int j=0;j<Size;j++)
            {
                if(i<=j)
                {
                    Re[i][j] = 1/Math.sqrt(Size) * Math.cos(2*Math.PI*i*j/(double)Size);
                    Lm[i][j] = 1/Math.sqrt(Size) * Math.sin(2*Math.PI*i*j/(double)Size);
                }
                else
                {
                    Re[i][j]=Re[j][i];
                    Lm[i][j]=Lm[j][i];
                }
            }
            if(pbar != null)
                pbar.setvalue(Size,i*3/2);
        }
        Matrix F = new Matrix(pict);
        Matrix MatrixRe = new Matrix(Re);
        Matrix MatrixLm = new Matrix(Lm);
        Matrix Reresult = MatrixRe.times(F.times(MatrixRe));
        Reresult = Reresult.minus(MatrixLm.times(F.times(MatrixLm)));
        Matrix Lmresult = MatrixRe.times(F.times(MatrixLm));
        Lmresult = Lmresult.plus(MatrixLm.times(F.times(MatrixRe)));
        if(pbar != null)
            pbar.setvalue(12,10);
/*
        double[][] reunitRe = new double[Size][Size];
        double[][] reunitLm = new double[Size][Size];
        for(int i=0;i<Size;i++)
            for(int j=0;j<Size;j++)
            {
                int ix = j-Size/2;
                int iy = i-Size/2;
                if(ix<0) ix+=Size;
                if(iy<0) iy+=Size;
                reunitRe[i][j] = Reresult.get(iy,ix);
                reunitLm[i][j] = Lmresult.get(iy,ix);
            }
        Reresult = new Matrix(reunitRe);
        Lmresult = new Matrix(reunitLm);
*/
        double[][] result = new double[Size][Size];
        for(int i=0;i<Size;i++)
            for(int j=0;j<Size;j++)
                result[i][j] = getdist(Reresult.get(i,j), Lmresult.get(i,j));
        FourierInfo ret = new FourierInfo();
        if(pbar != null)
            pbar.setvalue(12,12);
        ret.fmatrix = result;
        ret.WRe = MatrixRe;
        ret.WLm = MatrixLm;
        ret.Gre = Reresult;
        ret.Glm = Lmresult;
        return ret;
    }

    /**
     * 通过二维离散傅里叶变换获得能量谱图
     * @param img 灰度图片
     * @return 能量谱图
     */
    public static FourierInfo getSpectrum(BufferedImage img, processBar pbar)
    {
        int imgWidth=img.getWidth();
        int imgHeight=img.getHeight();
        int size = Math.max(imgHeight, imgWidth);
        double[][] grayMtx = new double[size][size];
        int avg;
        for(int i=0;i<size;i++) {
            for (int j = 0; j < size; j++) {
                if(i>=imgHeight || j>=imgWidth)
                    grayMtx[i][j]=0;
                else
                {
                    Object data = img.getRaster().getDataElements(j, i, null);//获取该点像素，并以object类型表示
                    grayMtx[i][j]=(char)img.getColorModel().getRed(data);
                }
            }
        }
        FourierInfo ret =  DFT(grayMtx, size, pbar);
        BufferedImage Spectrum=new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
        int a=255,r,g,b,argb;
        for(int i=0;i<size;i++) {
            for (int j = 0; j < size; j++) {
                int ti = i-size/2;
                int tj = j-size/2;
                if(ti<0) ti+=size;
                if(tj<0) tj+=size;
                r=g=b=(int)(Math.log(ret.fmatrix[ti][tj]+1)*256/Math.log(ret.fmatrix[0][0]+1));
                argb=((a*256+r)*256+g)*256+b;
                Spectrum.setRGB(j,i,argb);
            }
        }
        ret.Spectrum = Spectrum;
        ret.imgheight = imgHeight;
        ret.imgwidth = imgWidth;
        return ret;
    }

    /**
     * 傅里叶逆变换
     * @param inverseF FourierInfo结构体
     * @return 还原的矩阵
     */
    private static Matrix inverseDFT(FourierInfo inverseF, processBar pbar)
    {
        int Size = inverseF.fmatrix.length;
/*
        double[][] reunitRe = new double[Size][Size];
        double[][] reunitLm = new double[Size][Size];
        for(int i=0;i<Size;i++)
            for(int j=0;j<Size;j++)
            {
                int ix = j-Size/2;
                int iy = i-Size/2;
                if(ix<0) ix+=Size;
                if(iy<0) iy+=Size;
                reunitRe[i][j] = inverseF.Gre.get(iy,ix);
                reunitLm[i][j] = inverseF.Glm.get(iy,ix);
            }
        inverseF.Gre = new Matrix(reunitRe);
        inverseF.Glm = new Matrix(reunitLm);
*/
        if(pbar!=null)
            pbar.setvalue(10,0);
        Matrix ret = inverseF.WRe.times(inverseF.Gre.times(inverseF.WRe));
        if(pbar!=null)
            pbar.setvalue(10,1);
        ret = ret.minus(inverseF.WLm.times(inverseF.Gre.times(inverseF.WLm)));
        if(pbar!=null)
            pbar.setvalue(10,2);
        ret = ret.plus(inverseF.WLm.times(inverseF.Glm.times(inverseF.WRe)));
        if(pbar!=null)
            pbar.setvalue(10,3);
        ret = ret.plus(inverseF.WRe.times(inverseF.Glm.times(inverseF.WLm)));
        if(pbar!=null)
            pbar.setvalue(10,4);
        ret = ret.minus(inverseF.WRe.times(inverseF.Glm.times(inverseF.WRe)));
        if(pbar!=null)
            pbar.setvalue(10,5);
        ret = ret.plus(inverseF.WLm.times(inverseF.Gre.times(inverseF.WRe)));
        if(pbar!=null)
            pbar.setvalue(10,6);
        ret = ret.plus(inverseF.WRe.times(inverseF.Gre.times(inverseF.WLm)));
        if(pbar!=null)
            pbar.setvalue(10,7);
        ret = ret.plus(inverseF.WLm.times(inverseF.Glm.times(inverseF.WLm)));
        if(pbar!=null)
            pbar.setvalue(10,8);

        return ret;
    }

    /**
     * 通过二维傅里叶逆变换还原图片
     * @param inverseF Fourier结构体
     * @return 经过逆变换还原的图片
     */
    public static BufferedImage Spectrum2Img(FourierInfo inverseF, processBar pbar)
    {
        int size = inverseF.fmatrix.length;
        Matrix grayimg =  inverseDFT(inverseF, pbar);
        BufferedImage img=new BufferedImage(inverseF.imgwidth, inverseF.imgheight, BufferedImage.TYPE_4BYTE_ABGR);
        int a=255,r,g,b,argb;
        for(int i=0;i<inverseF.imgheight;i++) {
            for (int j = 0; j < inverseF.imgwidth; j++) {
                r=g=b=Math.abs((int)grayimg.get(i,j));
                argb=((a*256+r)*256+g)*256+b;
                img.setRGB(j,i,argb);
            }
            if(pbar!=null)
                pbar.setvalue(inverseF.imgheight * 10,inverseF.imgheight * 8 + i*2);
        }
        if(pbar!=null)
            pbar.setvalue(10,10);
        return img;
    }

}

class FourierInfo{
    /** 图片真实宽度*/
    int imgwidth;
    /** 图片真实高度*/
    int imgheight;
    /** 能量谱矩阵，即傅里叶变换后每项的模*/
    double[][] fmatrix;
    /** 傅里叶变换基函数的实部*/
    Matrix WRe;
    /** 傅里叶变换基函数的虚部*/
    Matrix WLm;
    /** 傅里叶变换结果的实部*/
    Matrix Gre;
    /** 傅里叶变换结果的虚部*/
    Matrix Glm;
    /** 能量谱图*/
    BufferedImage Spectrum;
}

//      TODO: 暴力DFT计算
/*
        double[][] sine = new double[height][width];
        double[][] cosine = new double[height][width];
        if(pbar!=null)
            pbar.setvalue(height,0);
        for(int y=0;y<height;y++)
        {
            for(int x=0;x<width;x++) {
                sine[y][x] = 0;
                cosine[y][x] = 0;
                for (int i = 0; i < height; i++)
                {
                    for (int j = 0; j < width; j++) {
                        sine[y][x] += Math.sin(2 * Math.PI * (x * i/(double)width + y * j/(double)height))*pict[i][j];
                        cosine[y][x] += Math.cos(2 * Math.PI * (x * i/(double)width + y * j/(double)height))*pict[i][j];
                    }
                }
                sine[y][x] /= width*height;
                cosine[y][x] /= width*height;
                sine[y][x] = Math.sqrt(sine[y][x]*sine[y][x] + cosine[y][x]*cosine[y][x]);
            }
            if(pbar!=null)
                pbar.setvalue(height,y);
        }
        if(pbar!=null)
            pbar.setvalue(height,height);
        return sine;

*/