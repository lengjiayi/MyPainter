import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpectHandler extends JLabel {
    private JDialog father;
    private FourierInfo Finfo;
    private int size = 500;
    private int Radius = 5;
    public SpectHandler(JDialog father)
    {
        this.father = father;
        setSize(500,500);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.isMetaDown())
                    half(e.getPoint());
                else
                    filter(e.getPoint());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(e.isMetaDown())
                    half(e.getPoint());
                else
                    filter(e.getPoint());
            }
        });
    }
    public void setSpect(FourierInfo Finfo)
    {
        this.Finfo = Finfo;
        ImageIcon icon=new ImageIcon(Finfo.Spectrum);
        icon.setImage(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        setIcon(icon);
    }
    private void half(Point p)
    {
        if(Finfo == null)
            return;
        double realsize = Math.max(Finfo.imgheight, Finfo.imgwidth);
        int intsize = Math.max(Finfo.imgheight, Finfo.imgwidth);
        double realx = p.x * realsize / size;
        double realy = p.y * realsize / size;
        int realRadius = (int)(Radius * realsize / size);
        for(int i=-realRadius;i<realRadius;i++)
            for(int j=-realRadius;j<realRadius;j++)
                if(Math.sqrt(i*i + j*j)<realRadius)
                {
                    int curx = (int) realx + i;
                    int cury = (int) realy + j;
                    if(curx>0 && cury>0 && curx<realsize && cury<realsize)
                    {
                        Object data = Finfo.Spectrum.getRaster().getDataElements(curx, cury, null);//获取该点像素，并以object类型表示
                        int val=Finfo.Spectrum.getColorModel().getRed(data);
                        val /= 2;
                        Finfo.Spectrum.setRGB(curx,cury,((255*256+val)*256+val)*256+val);
                        curx -= intsize/2;
                        cury -= intsize/2;
                        if(curx<0) curx += intsize;
                        if(cury<0) cury += intsize;
                        double re = Finfo.Gre.get(curx, cury);
                        double lm = Finfo.Glm.get(curx, cury);
                        Finfo.Gre.set(curx, cury, re/2);
                        Finfo.Glm.set(curx, cury, lm/2);
                    }
                }
        setSpect(Finfo);
    }
    private void filter(Point p)
    {
        if(Finfo == null)
            return;
        double realsize = Math.max(Finfo.imgheight, Finfo.imgwidth);
        int intsize = Math.max(Finfo.imgheight, Finfo.imgwidth);
        double realx = p.x * realsize / size;
        double realy = p.y * realsize / size;
        int realRadius = (int)(Radius * realsize / size);
        int count=0, avgrgb=0;
        double avgre=0, avglm=0;
        for(int i=-realRadius;i<realRadius;i++)
            for(int j=-realRadius;j<realRadius;j++)
                if(Math.sqrt(i*i + j*j)<realRadius)
                {
                    int curx = (int) realx + i;
                    int cury = (int) realy + j;
                    if(curx>0 && cury>0 && curx<realsize && cury<realsize)
                    {
                        count++;
                        Object data = Finfo.Spectrum.getRaster().getDataElements(curx, cury, null);//获取该点像素，并以object类型表示
                        int val=Finfo.Spectrum.getColorModel().getRed(data);
                        avgrgb += val;
                        curx -= intsize/2;
                        cury -= intsize/2;
                        if(curx<0) curx += intsize;
                        if(cury<0) cury += intsize;
                        double re = Finfo.Gre.get(curx, cury);
                        double lm = Finfo.Glm.get(curx, cury);
                        avgre += re;
                        avglm += lm;
                    }
                }
        avglm /= count;
        avgre /= count;
        avgrgb /= count;
        for(int i=-realRadius;i<realRadius;i++)
            for(int j=-realRadius;j<realRadius;j++)
                if(Math.sqrt(i*i + j*j)<realRadius)
                {
                    int curx = (int) realx + i;
                    int cury = (int) realy + j;
                    if(curx>0 && cury>0 && curx<realsize && cury<realsize)
                    {
                        Finfo.Spectrum.setRGB(curx,cury,((255*256+avgrgb)*256+avgrgb)*256+avgrgb);
                        curx -= intsize/2;
                        cury -= intsize/2;
                        if(curx<0) curx += intsize;
                        if(cury<0) cury += intsize;
                        Finfo.Gre.set(curx, cury, avgre);
                        Finfo.Glm.set(curx, cury, avglm);
                    }
                }
        setSpect(Finfo);
    }
}
