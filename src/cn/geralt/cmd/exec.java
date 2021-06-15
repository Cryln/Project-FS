package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class exec extends Executable{
    public exec(FileSystem fileSystem) {
        super(fileSystem);
        this.permission0 = 7;
        type0 = 1;
    }

    @Override
    public int process(String[] args) throws IOException { //exec notepad test.java
        if((getFSHandler().getCurrentUser().getUid()&0xffff0000)==0){
            this.permission0 = 0;
        }
        int fd = getFSHandler().open(args[1]);
        MyFile myFile = getFSHandler().getFiles().get(fd);
        String fileName = myFile.getName();
        int fileLen = myFile.getFileLen();
        byte[] data = new byte[fileLen];
        FSHandler.read(fd,data,0,fileLen);

        String filepath ="D:\\home\\Desktop\\temp1\\" + fileName;
        File file  = new File(filepath);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data,0,data.length);
        fos.flush();
        fos.close();
        Runtime runtime = Runtime.getRuntime();
//        Process p0 = runtime.exec(" D:\\home\\Desktop\\temp");
        Process p = runtime.exec(args[0]+" D:\\home\\Desktop\\temp1\\"+args[1]);
        Scanner sc = new Scanner(p.getInputStream());
        if(sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }

        FileInputStream fis = new FileInputStream(file);
        data = fis.readAllBytes();
        fis.close();

        FSHandler.write(fd,data,0);
        FSHandler.close(fd);

        file.delete();

        return 0;
    }

    @Override
    public int preProcess(String[] args) {
        switch (args[0]) {
            case "notepad": permission0 =6;break;
        }

        DEntry des = getFSHandler().dir2DEntry(args[1]);
        int fd = 0;
        try {
            fd = getFSHandler().open(des.getAbsPath());
        }catch (NullPointerException e){
            return -4;
        }
        if(des.getiNode().getType()!=type0){
            return -3;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file, permission0);
        getFSHandler().close(fd);
        return ans?1:0;
    }
}
