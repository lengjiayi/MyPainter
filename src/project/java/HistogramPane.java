import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.concurrent.atomic.AtomicInteger;

public class HistogramPane extends JLabel {
    private ImagePane father;
    public int width,height;
    private Color color;
    public Color acolor;
    public AtomicInteger div = new AtomicInteger(0);
    private int[] data=null;
    private SpliteTri spliter;
    private double max;
    /**
     * 绘制一个宽度为270, 高度为100的灰度直方图
     * @parm father ImagePane容器
     * @param color 矩形边界的颜色
     */
    public HistogramPane(ImagePane father, Color color)
    {
        this.father = father;
        this.color=color;
        this.acolor = new Color(color.getRed(), color.getGreen(), color.getBlue(),60);

        this.setBorder(BorderFactory.createLineBorder(color,1));
        width=270; height=100;
        this.setSize(width, height);
        spliter = new SpliteTri(this);
        spliter.setLocation(6,0);
        add(spliter);
    }
    /**
     * 设置灰度直方图的数据，并进行重绘
     * @param d 长度为256的int数组，每一位代表对应灰度像素的频数
     */
    public void setdata(int[] d)
    {
        data=d;
        max=data[1];
        for(int i=1;i<256;i++)
            if(data[i]>max)
                max=data[i];
        repaint();
    }
    public void setDiv(int div)
    {
        this.div.set(div);
        spliter.setLocation(this.div.get()+5, 0);
        repaint();
    }
    public boolean notinit(){ return data==null; }
    public void updateSpliter(){ father.filter(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        if(data==null)
            return;
        g2.setColor(Color.BLACK);
        for(int i=0;i<256;i++)
            g2.drawLine(i+10, height, i+10, (int)(height-data[i]*(height-10)/max));
        g2.setColor(acolor);
        g2.drawLine(div.get()+10, height, div.get()+10, 0);
    }
}

class SpliteTri extends JLabel{
    protected Path2D.Double path;
    private HistogramPane father;
    private BasicStroke stroke;
    private Point lstpoint=null;
    private int div = 1;
    public SpliteTri(HistogramPane father)
    {
        this.father = father;
        setBackground(new Color(0,0,0,0));
        setSize(10,10);
        stroke = new BasicStroke(2);
        path = new Path2D.Double();
        path.moveTo(0,0);
        path.lineTo(10,0);
        path.lineTo(5,10);
        path.closePath();
        this.setVisible(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                lstpoint = null;
                father.updateSpliter();
            }
        });
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
                lstpoint=e.getPoint();      //记录鼠标按下的位置，用于实现拖动效果
            }
        });
    }
    protected void bedragged(MouseEvent e)      //实现拖动效果
    {
        if(lstpoint==null || father.notinit())
            return;
        Point toloc=e.getPoint();
        int dx = toloc.x-lstpoint.x;
        div += dx;
        if(div<1)
            div = 1;
        if(div>256)
            div = 256;
        father.setDiv(div);
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(father.acolor);
        g2.fill(path);
        ((Graphics2D) g).setStroke(stroke);
        g2.setColor(Color.BLACK);
        g2.draw(path);
    }
}