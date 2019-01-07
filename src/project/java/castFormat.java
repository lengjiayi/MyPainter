import java.io.*;

public class castFormat {

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
     * 将off文件转换为obj文件
     * @param filename off文件名称
     * @throws IOException 文件不存在或格式错误
     * @return 生成obj文件名称
     */
    public static String off2obj(String filename) throws IOException {
        String[] dataS = readFile(filename).split("\n");
        String[] info = dataS[1].split(" ");
        //vertex number
        int vnum = Integer.parseInt(info[0]);
        //surface number
        int snum = Integer.parseInt(info[1]);
        //other num
        int onum = Integer.parseInt(info[2].trim());
        System.out.printf("vnum %d, snum %d, onum %d\n",vnum, snum, onum);

        String outfilename = filename.substring(0, filename.length()-4)+".obj";
        File outputfile = new File(outfilename);
        OutputStream os = new FileOutputStream(outputfile);

        for(int i=0;i<vnum;i++)
        {
            os.write(("v "+dataS[i+2]+"\n").getBytes());
        }
        for(int i=vnum;i<vnum+snum;i++)
        {
            String[] surface = dataS[i+2].split(" ");
            int p1 = Integer.parseInt(surface[1])+1;
            int p2 = Integer.parseInt(surface[2])+1;
            int p3 = Integer.parseInt(surface[3].trim())+1;
            String out = "f "+p1+" "+p2+" "+p3+"\n";
            os.write(out.getBytes());
        }
        os.close();
        return outfilename;
    }

}
