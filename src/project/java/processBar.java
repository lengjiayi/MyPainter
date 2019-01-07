import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class processBar extends JLabel {       //用于绘制一个进度条
    private int width, height, max;
    private int value = 0;
    private Color forecolor;
    private Shape foreground;
    private Shape border;
    private BasicStroke stroke;

    /**
     * 一个圆角矩形的进度条
     * @param width 进度条宽度
     * @param height 进度条高度
     * @param color 进度条的颜色
     */
    public processBar(int width, int height, Color color) {
        setSize(width+2, height+2);
        setLayout(null);
        setVisible(true);
        this.width = width;
        this.height = height;
        this.max = max;
        this.value = max;
        this.forecolor = color;
        border = new RoundRectangle2D.Float(1,1,width,height,height/2f,height/2f);
        stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    /**
     * 设置进度条的当前数值，并进行重绘
     * @param max 进度条满时的数值
     * @param value 当前数值
     */
    public void setvalue(int max, int value) {
        this.max = max;
        this.value = value;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Shape forground = new RoundRectangle2D.Float(1, 1, (float) width  / max * value, height,height/2f, height/2f);
        g2.setColor(forecolor);
        g2.fill(forground);
        g2.setStroke(stroke);
        g2.setColor(new Color(100, 100, 100));
        g2.draw(border);
    }

}