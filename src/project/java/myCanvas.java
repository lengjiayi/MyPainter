import javax.swing.*;
import java.awt.*;
import java.awt.image.MultiPixelPackedSampleModel;

class myCanvas extends JPanel {
    public MyPainter father;
    public mShape[] shapelist={};
    public myCanvas(MyPainter f)
    {
        father=f;
        setLayout(null);
        setBackground(Color.white);
    }
    public void addShape(mShape n)
    {
        int oldlen=shapelist.length;
        mShape tmp[]=new mShape[oldlen+1];
        for(int i=0;i<oldlen;i++)
            tmp[i]=shapelist[i];
        tmp[oldlen]=n;
        shapelist=tmp;
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        //TODO 增加一个蒙版实现画布的平移、缩放和旋转
    }
    public void removeShape(mShape n)
    {
        remove(n.outlook);
        for(int i=0;i<shapelist.length;i++)
        {
            if(shapelist[i].equals(n))
            {
                remove(shapelist[i].rball);
                remove(shapelist[i].rcenter);
                shapelist[i].dispose();
                shapelist[i]=null;
                mShape[] tlist=new mShape[shapelist.length-1];
                for(int j=0;j<i;j++)
                    tlist[j]=shapelist[j];
                for(int j=i;j<shapelist.length-1;j++)
                    tlist[j]=shapelist[j+1];
                shapelist=tlist;
                return;
            }
        }
        System.out.println("not found");
    }
}
