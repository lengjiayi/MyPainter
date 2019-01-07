import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class offLoader {
    private static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if(!file.exists())
        {
            System.out.println("file not exist!");
            return null;
        }
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent);
    }
    /**
     * 读取并解析off文件
     * @param filename off文件名称
     * @throws IOException 文件不存在或格式错误
     * @return 模型参数集
     */
    public static modelInfo loadfile(String filename) throws IOException {
        String[] dataS = readFile(filename).split("\n");
        String[] info = dataS[1].split(" ");
        modelInfo model = new modelInfo();
        //vertex number
        int vnum = Integer.parseInt(info[0]);
        //surface number
        int snum = Integer.parseInt(info[1]);
        //other num
        int onum = Integer.parseInt(info[2].trim());
        model.vertexnum=vnum;
        model.surfacenum=snum;
        model.vertexSet = new double[vnum][3];
        model.surfaceSet = new int[snum][3];
        System.out.printf("vnum %d, snum %d, onum %d\n",vnum, snum, onum);

        String outfilename = filename.substring(0, filename.length()-4)+".obj";

        for(int i=0;i<vnum;i++)
        {
            String[] vertex = dataS[i+2].split(" ");
            model.vertexSet[i][0] = Double.parseDouble(vertex[0]);
            model.vertexSet[i][1] = Double.parseDouble(vertex[1]);
            model.vertexSet[i][2] = Double.parseDouble(vertex[2].trim());
        }
        for(int i=vnum;i<vnum+snum;i++)
        {
            String[] surface = dataS[i+2].split(" ");
            model.surfaceSet[i-vnum][0] = Integer.parseInt(surface[1]);
            model.surfaceSet[i-vnum][1] = Integer.parseInt(surface[2]);
            model.surfaceSet[i-vnum][2] = Integer.parseInt(surface[3].trim());
        }
        return model;
    }
/*
    public static void main(String[] args) {
        try {
            loadfile("C:\\Users\\lenovo\\Desktop\\计算机图形学\\大作业资料\\三维模型数据及说明\\bookshelf-p1058.off");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}

class modelInfo
{
    int vertexnum;      //顶点数
    int surfacenum;     //面数
    double[][] vertexSet;   //顶点集合
    int [][] surfaceSet;    //面集合
}