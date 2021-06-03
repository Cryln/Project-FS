package cn.geralt.cmd;

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
    }

    @Override
    public int run(String[] args) throws IOException { //exec notepad test.java
        int fd = getFSHandler().open(args[1]);
        MyFile myFile = getFSHandler().getFiles().get(fd);
        String fileName = myFile.getName();
        int fileLen = myFile.getFileLen();
        byte[] data = new byte[fileLen];
        FSHandler.read(fd,data,0,fileLen);

        String filepath ="temp/" + fileName;
        File file  = new File(filepath);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data,0,data.length);
        fos.flush();
        fos.close();
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(args[0]+" temp/"+args[1]);
        Scanner sc = new Scanner(p.getErrorStream());
        if(sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }

        FileInputStream fis = new FileInputStream(file);
        data = fis.readAllBytes();
        fis.close();

        FSHandler.write(fd,data,0);

        file.delete();

        return 0;
    }
}
