import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImagePane extends JPanel {
    private JDialog pFloatBar;
    private JDialog spectwin;
    private SpectHandler spect;
    private JLabel image;
    private MaskPane mask;
    private processBar pbar;
    private HistogramPane rhist;
    private HistogramPane ghist;
    private HistogramPane bhist;

    private String filename;
    private BufferedImage imgbf=null;
    private BufferedImage rawimg=null;
    private ImageIcon icon=null;

    private int maxWidth=500;
    private int maxHeight=500;
    private int realwidth;
    private int realheight;
    public int imgwidth;
    public int imgheight;

    private int[][] hist=null;
    private reformBall[][] balls;
    private FourierInfo Finfo=null;
    private boolean binary=false;       //是否为二值图像
    public AtomicBoolean avaliable=new AtomicBoolean(true);    //同步

    public ImagePane(int width, int height)
    {
        this.maxWidth = width;
        this.maxHeight = height;
        pFloatBar=new JDialog();
        pFloatBar.setSize(300,500);
        pFloatBar.setLayout(null);
        pFloatBar.setResizable(false);
        pFloatBar.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        spectwin=new JDialog();
        spectwin.setTitle("能量谱");
        spectwin.setSize(500,500);
        spectwin.setLayout(null);
        spectwin.setResizable(false);
        spectwin.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        spectwin.setVisible(false);
        spect =  new SpectHandler(spectwin);
        spect.setLocation(0,0);
        spectwin.add(spect);
        //TODO: 创建图形算法菜单
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("文件");
        JMenuItem readitm = new JMenuItem("打开");
        readitm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(pFloatBar, "打开", FileDialog.LOAD);
                fd.setVisible(true);
                String path = fd.getDirectory();
                filename = fd.getFile();
                setImg(path+filename);
            }
        });
        file.add(readitm);
        JMenuItem saveitm=new JMenuItem("保存");
        saveitm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd=new FileDialog(pFloatBar,"保存", FileDialog.SAVE);
                fd.setVisible(true);
                String path=fd.getDirectory();
                String filename=fd.getFile();
                filename+=".png";
                try {
                    ImageIO.write(imgbf,"png",new File(path+filename));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        file.add(saveitm);
        menu.add(file);
        JMenu opt = new JMenu("直方图操作");
        menu.add(opt);
        JMenuItem gray = new JMenuItem("灰度化");
        gray.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rgb2gray();
            }
        });
        opt.add(gray);
        JMenuItem hequal = new JMenuItem("均衡化");
        hequal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hequal();
            }
        });
        opt.add(hequal);
        JMenuItem Binary=new JMenuItem("二值化");
        Binary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Binarilize();
            }
        });
        opt.add(Binary);

        JMenu neighbor = new JMenu("邻域操作");
        menu.add(neighbor);
        JMenuItem GFilter=new JMenuItem("高斯滤波");
        GFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GaussionFilter();
            }
        });
        neighbor.add(GFilter);
        JMenuItem MFilter=new JMenuItem("中值滤波");
        MFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MidFilter();
            }
        });
        neighbor.add(MFilter);
        JMenuItem dilation=new JMenuItem("形态学扩张");
        dilation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dilation();
            }
        });
        neighbor.add(dilation);
        JMenuItem erosion=new JMenuItem("形态学腐蚀");
        erosion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                erosion();
            }
        });
        neighbor.add(erosion);

        JMenu Fourier = new JMenu("Fourier相关");
        menu.add(Fourier);
        JMenuItem Spectrum=new JMenuItem("能量谱");
        Spectrum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DFT(false);
            }
        });
        Fourier.add(Spectrum);
        JMenuItem inverseSpectrum=new JMenuItem("逆变换");
        inverseSpectrum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DFT(true);
            }
        });
        Fourier.add(inverseSpectrum);

        pFloatBar.setJMenuBar(menu);
        pFloatBar.setVisible(true);

        //TODO: 添加图像
        ImagePane frame=this;
        this.setLayout(null);
        this.setBounds(0,0,maxWidth+2,maxHeight+2);
        image=new JLabel();
        image.setBounds(0,0,maxWidth,maxHeight);
        image.setBorder(BorderFactory.createLineBorder(Color.green,2));
        add(image);
        mask=new MaskPane();
        mask.setBounds(0,0,maxWidth,maxHeight);
        add(mask,0);

        rhist=new HistogramPane(this,Color.RED);
        ghist=new HistogramPane(this,Color.GREEN);
        bhist=new HistogramPane(this,Color.BLUE);
        rhist.setLocation(10, 20);
        ghist.setLocation(10, 40+rhist.height);
        bhist.setLocation(10, 60+rhist.height+ghist.height);
        pFloatBar.add(rhist);
        pFloatBar.add(ghist);
        pFloatBar.add(bhist);
        //进度条
        pbar=new processBar(200,10,new Color(120,210,150));
        pbar.setvalue(100,100);
        pbar.setLocation(10,100+rhist.height+ghist.height+bhist.height);
        pFloatBar.add(pbar);

        balls=new reformBall[8][];
        for(int i=0;i<7;i++) {
            int row=6+i%2;
            balls[i]=new reformBall[row];
            for (int j = 0; j < row; j++)
                balls[i][j] = new reformBall(this);
        }
        for(int i=0;i<6;i++)
        {
            balls[0][i].NotMovable();
            balls[6][i].NotMovable();
        }
        balls[1][0].NotMovable();   balls[3][0].NotMovable();   balls[5][0].NotMovable();
        balls[1][6].NotMovable();   balls[3][6].NotMovable();   balls[5][6].NotMovable();
        mask.setBalls(balls);
        this.setVisible(true);
    }
    private void setImg(String fn)
    {
        filename=fn;
        try {
            imgbf = ImageIO.read(new FileInputStream(filename));
            rawimg=ImageIO.read(new FileInputStream(filename));
            realheight=imgbf.getHeight();
            realwidth=imgbf.getWidth();
            ResetImg();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ResetImg();
        resetpoints();
    }
    private void resetpoints()
    {
        for(int i=0;i<7;i++) {
            int row=6+i%2;
            double dy=imgheight/6.0;
            double dx=imgwidth/6.0;
            for (int j = 0; j < row; j++)
                if(i%2==0)
                    balls[i][j].setLocation((int) (dx*j+dx/2)-5, (int) (dy*i)-5);
                else
                    balls[i][j].setLocation((int) (dx*j)-5, (int) (dy*i)-5);
        }
        setballs();
    }
    private void ResetImg()
    {
        setSize(maxWidth, maxHeight);
        image.setSize(maxWidth, maxHeight);
        if(imgbf==null)
            return;
        icon=new ImageIcon(imgbf);
        double w=maxWidth,h=maxHeight;

        if(realheight/(double)realwidth > maxHeight/(double)maxWidth)
            w=h*realwidth/(double)realheight;
        else
            h=w*realheight/(double)realwidth;

        imgwidth=(int)w;
        imgheight=(int)h;
        icon.setImage(icon.getImage().getScaledInstance((int)w, (int)h, Image.SCALE_SMOOTH));
        image.setSize((int)w,(int)h);
        mask.setSize((int)w,(int)h);

        image.setIcon(icon);
        hist=DIPAlgorithm.getHist(imgbf);
        rhist.setdata(hist[0]);
        ghist.setdata(hist[1]);
        bhist.setdata(hist[2]);
    }
    public void reSize(int width, int height)
    {
        this.maxWidth = width;
        this.maxHeight = height;
        ResetImg();
        setSize(imgwidth, imgheight);
        resetpoints();
    }
    private void removeballs()
    {
        for (int i = 0; i < 7; i++) {
            int row = 6 + i % 2;
            for (int j = 0; j < row; j++)
                remove(balls[i][j]);
        }
    }

    private void setballs()
    {
        for (int i = 0; i < 7; i++) {
            int row = 6 + i % 2;
            for (int j = 0; j < row; j++)
                add(balls[i][j], 0);
        }
    }

    public void selected()
    {
        image.setBorder(BorderFactory.createLineBorder(Color.green,2));
        if(imgbf!=null)
            setballs();
        pFloatBar.setVisible(true);
    }
    public void unSelected()
    {
        image.setBorder(null);
        removeballs();
        pFloatBar.setVisible(false);
    }
    //MAKE: DIP算法
    public Color getColor(double x, double y)
    {
        if(x<0 || x>imgwidth || y<0 || y>imgheight)
            return null;
        if(realwidth==0)
            return null;
        int rx, ry;
        x=x*realwidth/imgwidth;
        y=y*realheight/imgheight;
        rx=(int)(x+0.5);
        ry=(int)(y+0.5);
        if(rx>=realwidth || ry>=realheight || rx<0 || ry<0)
            return null;
        int []carray=DIPAlgorithm.getColor(imgbf, rx, ry);
        Color color = new Color(carray[0], carray[1], carray[2], carray[3]);
        return color;
    }

    public void deform()
    {
        if(!avaliable.get())
            return;
        avaliable.set(false);
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                Point[][] ops = new Point[7][];
                for (int i = 0; i < 7; i++) {
                    int row = 6 + i % 2;
                    ops[i]=new Point[row];
                    for (int j = 0; j < row; j++) {
                        ops[i][j] = balls[i][j].getLocation();
                        ops[i][j].x += 5;
                        ops[i][j].y += 5;
                    }
                }
                double dx,dy;
                for(int i=0;i<7;i++) {
                    int row = 6 + i % 2;
                    for (int j = 0; j < row; j++) {
                        dx = ops[i][j].x;
                        dy = ops[i][j].y;
                        dx *= realwidth / (double) imgwidth;
                        dy *= realheight / (double) imgheight;
                        ops[i][j].x = (int) dx;
                        ops[i][j].y = (int) dy;
                    }
                }
                imgbf=DIPAlgorithm.deform(rawimg, ops, pbar);
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void rgb2gray()
    {
        if(!avaliable.get())
            return;
        avaliable.set(false);
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.getGray(imgbf, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void Hequal()
    {
        if(!avaliable.get())
            return;
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.HistEqualization(imgbf, hist, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void GaussionFilter()
    {
        if(!avaliable.get())
            return;
        avaliable.set(false);
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.GaussianFilter(imgbf, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void MidFilter()
    {
        if(!avaliable.get())
            return;
        avaliable.set(false);
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.MidFilter(imgbf, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void Binarilize()
    {
        if(!avaliable.get())
            return;
        avaliable.set(false);
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.Binarilize(imgbf, hist, pbar);
                rawimg=imgbf;
                binary=true;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void dilation()
    {
        if(!avaliable.get())
            return;
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.dilation(imgbf, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void erosion()
    {
        if(!avaliable.get())
            return;
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf=DIPAlgorithm.erosion(imgbf, pbar);
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    private void DFT(boolean inverse)
    {
        if(!avaliable.get())
            return;
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                if(!inverse) {
                    Finfo = DIPAlgorithm.getSpectrum(imgbf, pbar);
                    spect.setSpect(Finfo);
                    spectwin.setVisible(true);
                }
                else if(Finfo!=null)
                {
                    imgbf = DIPAlgorithm.Spectrum2Img(Finfo, pbar);
                }
                rawimg=imgbf;
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }
    public void filter()
    {
        int fr=rhist.div.get();
        int fg=ghist.div.get();
        int fb=bhist.div.get();
        filter(fr, fg, fb);
    }
    private void filter(int r, int g, int b)
    {
        if(!avaliable.get())
            return;
        Thread tmp=new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgbf==null)
                    return;
                imgbf = DIPAlgorithm.filterDiv(rawimg, r,g,b,pbar);
                ResetImg();
                avaliable.set(true);
            }
        });
        tmp.start();
    }

}

class MaskPane extends JLabel
{
    reformBall[][] balls;
    private void drawLines()
    {
        Graphics2D g2=(Graphics2D) getGraphics();
        g2.setColor(new Color(0,0,0,0));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.setColor(Color.BLACK);
        for(int i=0;i<6;i++) {
            for (int j = i%2; j < 11+i%2; j++) {
                int j1=j/2;
                Point[] trips=new Point[3];
                if(j%2==0)
                {
                    trips[0]=balls[i][j1].getLocation(); trips[1]=balls[i+1][j1-i%2].getLocation();   trips[2]=balls[i+1][j1-i%2+1].getLocation();
                }
                else
                {
                    trips[0]=balls[i][j1].getLocation(); trips[1]=balls[i][j1+1].getLocation();   trips[2]=balls[i+1][j1+(i+1)%2].getLocation();
                }
                g2.drawLine(trips[0].x+5,trips[0].y+5,trips[1].x+5,trips[1].y+5);
                g2.drawLine(trips[2].x+5,trips[2].y+5,trips[1].x+5,trips[1].y+5);
                g2.drawLine(trips[0].x+5,trips[0].y+5,trips[2].x+5,trips[2].y+5);
            }
        }
    }
    public void setBalls(reformBall[][] bs){ this.balls=bs; }
    @Override
    protected void paintComponent(Graphics g) {
        if(balls!=null)
            drawLines();
    }
}