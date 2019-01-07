import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyPainter extends JFrame {
    public static void main(String[] args) {
        MyPainter painter=new MyPainter();
        painter.setVisible(true);
    }
    private String types[]={"line","curve","rect","ecllipse","polygon","image","3dmodel","select","fill","zoom"};

    public PropertiesBar pbar;
    private myCanvas canvas;
    private JMenuBar menubar;
    private zoomWindow zwindow;
    private boolean zooming=false;

    private int modselected=0;      //初始时为直线
    public mShape curShape;
    public mShape selectedShape;

    private Shapebtn paint;
    private Color PaintColor=Color.red;

    private Point pressPoint=null;
    public int curverank=3;
    public MyPainter()
    {
        MyPainter father=this;
        this.setTitle("绘图");
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension screenSize=kit.getScreenSize();
        setSize(screenSize.width/2,screenSize.height/3*2);
        setLocation(screenSize.width/4,screenSize.height/6);
        setResizable(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        menubar=new JMenuBar();
        JMenu file=new JMenu("文件");
        menubar.add(file);
        JMenuItem save=new JMenuItem("保存");
        file.add(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd=new FileDialog(father,"保存图片", FileDialog.SAVE);
                fd.setVisible(true);
                String path=fd.getDirectory();
                String filename=fd.getFile();
                fd.setVisible(false);
                while(fd.isVisible());
                Savepict(path+filename);
            }
        });
        this.setJMenuBar(menubar);

        HidableBar selectPanel=new HidableBar(screenSize.width/20,3);
        this.add(selectPanel,BorderLayout.WEST);
        Shapebtn shapebtn[]=new Shapebtn[8];
        for(int i=0;i<8;i++) {
            int j=i;
            shapebtn[i] = new Shapebtn(types[i]+".png");
            shapebtn[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if(j<7)
                        setCursor(Cursor.CROSSHAIR_CURSOR);
                    else
                        setCursor(Cursor.DEFAULT_CURSOR);
                    for(int i=0;i<8;i++)
                        shapebtn[i].Notselected();
                    paint.Notselected();
                    shapebtn[j].Isselected();
                    for(mShape x: canvas.shapelist)
                        x.set();
                    selectedShape=null;
                    modselected = j;
                    if(types[j].equals("curve")) {
                        pbar.setRank(true);
                        pbar.setVisible(true);
                        pbar.updateProperties();
                    }
                    else {
                        pbar.setRank(false);
                        pbar.setVisible(false);
                    }
                }
            });
            shapebtn[0].Isselected();
            selectPanel.add(shapebtn[i]);
        }

        JPanel fcolorbar=new JPanel();
        fcolorbar.setOpaque(false);
        fcolorbar.setLayout(new BorderLayout());
        //TODO: fill button
        paint=new Shapebtn("paint.png");
        paint.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        paint.setOpaque(true);
        paint.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                for(int i=0;i<8;i++)
                    shapebtn[i].Notselected();
                for(mShape x: canvas.shapelist)
                    x.set();
                selectedShape=null;
                modselected = 8;
                paint.Isselected();
                Cursor cursor=Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(getClass().getResource("paintcursor.png")).getImage(),  new Point(0, 0), "cursor");
                father.setCursor(cursor);
            }
        });
        fcolorbar.add(paint, BorderLayout.CENTER);

        JLabel fcolor=new JLabel();
        fcolor.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        fcolor.setPreferredSize(new Dimension(7,10));
        fcolor.setOpaque(true);
        fcolor.setBackground(Color.red);
        fcolor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Color crt=JColorChooser.showDialog(null,"请选择颜色",null);
                fcolor.setBackground(crt);
                PaintColor=crt;
            }
        });
        fcolorbar.add(fcolor, BorderLayout.EAST);
        selectPanel.add(fcolorbar);

        canvas=new myCanvas(this);
        this.add(canvas,BorderLayout.CENTER);

        //TODO: zoom button and zoom window
        zwindow=new zoomWindow(canvas);
        Shapebtn zoom=new Shapebtn("zoom.png");
        zoom.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                for(int i=0;i<8;i++)
                    shapebtn[i].Notselected();
                paint.Notselected();
                zooming=!zooming;
                if(zooming)
                {
                    setCursor(Cursor.DEFAULT_CURSOR);
                    zoom.Isselected();
                    zwindow.setSC(createSC());
                    father.remove(canvas);
                    father.add(zwindow, BorderLayout.CENTER);
                    zwindow.zoomPaint();
//                    father.repaint();
                }else
                {
                    modselected=0;
                    shapebtn[0].Isselected();
                    zoom.Notselected();
                    father.remove(zwindow);
                    father.add(canvas, BorderLayout.CENTER);
                    father.repaint();
                }
            }
        });
        selectPanel.add(zoom);


        pbar=new PropertiesBar(this,screenSize.height/25);
        pbar.setVisible(false);
        pbar.setRank(false);
        this.add(pbar,BorderLayout.NORTH);


        JFrame frame=this;

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.isMetaDown())
                {
                    if(curShape!=null && curShape.poly)
                    {
                        curShape.set();
                        canvas.addShape(curShape);
                        curShape = null;
                    }
                    return;
                }
                if (curShape == null) {
                    if(modselected<=4)
                        setCursor(Cursor.CROSSHAIR_CURSOR);
                    switch (modselected) {
                        case 0:
                            curShape = new mLine(canvas, e.getPoint()); break;
                        case 1:
                            curShape = new mCurveG(canvas, e.getPoint(),curverank); break;
//                            curShape = new mCurveR3(canvas, e.getPoint()); break;
                        case 2:
                            curShape=new mRect(canvas, e.getPoint()); break;
                        case 3:
                            curShape=new mEllipse(canvas, e.getPoint()); break;
                        case 4:
                            curShape=new mPolygon(canvas, e.getPoint()); break;
                        case 5:                     //image
                            curShape=new mImage(canvas, e.getPoint()); break;
                        case 6:                     //3DModel
                            curShape=new m3DShape(canvas, e.getPoint()); break;
                        case 7:                     //selected
                            pbar.setVisible(false);
                            for(mShape x: canvas.shapelist)
                                x.set();
                            for(mShape x: canvas.shapelist)
                            {
                                if(x.Isin(e.getPoint()))
                                {
                                    x.reset();
                                    selectedShape=x;
                                    if(!selectedShape.OnlyResizeable) {
                                        pbar.setVisible(true);
                                        pbar.updateProperties();
                                    }
                                    //TODO: 显示悬浮窗
                                    break;
                                }
                                selectedShape = null;
                            }
                            break;
                        case 8:                     //fill
                            pbar.setVisible(false);
                            for(mShape x: canvas.shapelist)
                                x.set();
                            for(mShape x: canvas.shapelist)
                            {
                                if(x.Isin(e.getPoint()))
                                {
                                    if(x.painttype>0)
                                        x.fill(PaintColor);
                                    break;
                                }
                            }
                            break;
                    }
                } else {
                    curShape.addPoint(e.getPoint());
                    if (curShape.premain <= 1)
                    {
                        setCursor(Cursor.DEFAULT_CURSOR);
                        curShape.set();
                        canvas.addShape(curShape);
                        curShape = null;
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                pressPoint=e.getPoint();      //记录鼠标按下的位置，用于实现拖动效果
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(getCursor().getType()==Cursor.MOVE_CURSOR)
                    setCursor(Cursor.DEFAULT_CURSOR);
            }
        });
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (curShape != null)
                    curShape.update(e.getPoint());
                else if(curShape==null && (modselected==7 || modselected==8))
                {
                    //highlight in select and paint mod
                    for(mShape x: canvas.shapelist)
                        x.inHighlight();
                    for(mShape x: canvas.shapelist)
                    {
                        if(x.Isin(e.getPoint())) {
                            x.Highlight();
                        }
                    }
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if(modselected==5)
                    setCursor(Cursor.MOVE_CURSOR);
                if (pressPoint == null || selectedShape==null)
                    return;
                Point toloc = e.getPoint();
                selectedShape.bedragged(toloc.x - pressPoint.x, toloc.y - pressPoint.y);
                pressPoint=toloc;
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                if(e.getKeyCode()==KeyEvent.VK_DELETE) {
                    DeletemShape();
                }
            }
        });
    }
    private void DeletemShape()
    {
        if(selectedShape!=null)
        {
            canvas.removeShape(selectedShape);
            selectedShape=null;
            pbar.setVisible(false);
        }
    }
    private void Savepict(String dir)
    {
        BufferedImage img=new BufferedImage(canvas.getSize().width, canvas.getSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2=img.createGraphics();
        canvas.paint(g2);
        try{
            ImageIO.write(img,"jpg",new File(dir+".jpg"));
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    private ImageIcon createSC()
    {
        Point loc=getLocation();
        loc.x+=canvas.getLocation().x;
        loc.y+=getInsets().top+menubar.getSize().height;
        Rectangle cRect=new Rectangle(loc,canvas.getSize());
        try {
            Robot robot = new Robot();
            BufferedImage bfImage = robot.createScreenCapture(cRect);
            ImageIcon icon=new ImageIcon(bfImage);
            return icon;
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return null;
    }
}


class PropertiesBar extends JToolBar{
    private MyPainter father;
    private JPanel widpanel;
    private JPanel rankpanel;
    private JLabel bcolor;
    private JSpinner wid;
    public PropertiesBar(MyPainter mp, int height){
        father=mp;
        FlowLayout layout = new FlowLayout();
        setLayout(null);
        setPreferredSize(new Dimension(100,height));
        setFloatable(false);
        setBackground(new Color(230,230,230));

        int delta=height-2;

        bcolor=new JLabel();
        bcolor.setBounds(3,1,delta,delta);
        bcolor.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        bcolor.setOpaque(true);
        bcolor.setBackground(Color.red);
        bcolor.setText("边框");
        bcolor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Color crt=JColorChooser.showDialog(null,"请选择颜色",null);
                bcolor.setBackground(crt);
                bcolor.setForeground(new Color(255-crt.getRed(),255-crt.getGreen(),255-crt.getBlue()));
                if(father.selectedShape!=null)
                    father.selectedShape.setColor(crt);
            }
        });
        add(bcolor);

        widpanel=new JPanel();
        widpanel.setBounds(9+2*delta,1,delta*3,delta);
        widpanel.setOpaque(false);
        widpanel.setLayout(new GridLayout(0,2));
        JLabel widthtext=new JLabel("粗细:");
        wid=new JSpinner(new SpinnerNumberModel(1,1,3,1));
        wid.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(father.selectedShape!=null)
                    father.selectedShape.setLineWidth((int)wid.getValue());
            }
        });
        widpanel.add(widthtext);
        widpanel.add(wid);
        add(widpanel);

        rankpanel=new JPanel();
        rankpanel.setBounds(12+5*delta,1,delta*3,delta);
        rankpanel.setOpaque(false);
        rankpanel.setLayout(new GridLayout(0,2));
        JLabel ranktext=new JLabel("点数:");
        JSpinner rank=new JSpinner(new SpinnerNumberModel(3,3,10,1));
        rank.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                father.curverank=(int)rank.getValue();
            }
        });
        rankpanel.add(ranktext);
        rankpanel.add(rank);
        add(rankpanel);
    }

    public void setRank(boolean vis) {
        rankpanel.setVisible(vis);
    }

    public void updateProperties()
    {
        if(father.selectedShape==null)
            return;
        Color fcolor = father.selectedShape.color;
        bcolor.setBackground(fcolor);
        bcolor.setForeground(new Color(255-fcolor.getRed(), 255-fcolor.getGreen(), 255-fcolor.getBlue()));
        wid.setValue(father.selectedShape.LineWidth);
    }
}

class Shapebtn extends JLabel{
    public boolean selected;
    private ImageIcon icon;
    public Shapebtn(String filename)
    {
        setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        setBackground(Color.WHITE);
        setOpaque(true);
        icon=new ImageIcon(getClass().getResource(filename));
        setIcon(icon);
    }

    public void Isselected() {
        selected=true; setBackground(new Color(230,230,250));
    }
    public void Notselected() {
        selected=false; setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon tmp=new ImageIcon();
        tmp.setImage(icon.getImage().getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH));
        setIcon(tmp);
    }
}

class HidableBar extends JPanel {
    private JPanel container;
    private JPanel splider;
    private boolean hide=false;
    private Dimension size=new Dimension();
    public HidableBar(int width, int height)
    {
        size.width=width;
        size.height=height;

        container=new JPanel();
        container.setBackground(new Color(224,255,255));
        container.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        container.setLayout(new GridLayout(10,1,5,5));
        container.setPreferredSize(new Dimension(width-6,10));

        splider=new JPanel();
        splider.setPreferredSize(new Dimension(6,100));
        splider.setLayout(new BorderLayout());

        setPreferredSize(new Dimension(width,height));
        setLayout(new BorderLayout());
        add(container,BorderLayout.WEST);

        HidableBar father=this;
        JLabel visuable=new JLabel();
        visuable.setOpaque(true);
        visuable.setBackground(new Color(175,238,238));
        visuable.setPreferredSize(new Dimension(6,6));
        visuable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!hide) {
                    container.setVisible(false);
                    father.setPreferredSize(new Dimension(6, size.height));
                }
                else
                {
                    container.setVisible(true);
                    father.setPreferredSize(new Dimension(size.width, size.height));
                }
                hide=!hide;
            }
        });
        splider.add(visuable,BorderLayout.CENTER);

        add(splider,BorderLayout.EAST);
        container.setVisible(false);
        father.setPreferredSize(new Dimension(6, size.height));
    }

    @Override
    public Component add(Component comp) {
        return container.add(comp);
    }
}