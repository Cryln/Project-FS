package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileSystem {
    private String VHDDir;
    private File vhd;
    private boolean isInitialized;

    public FileSystem(String VHDDir){
        this.VHDDir = VHDDir;
        this.vhd = new File(VHDDir);
        boolean checked = diskCheck(this.vhd);
        if(checked){
            this.isInitialized = initialize();
        }
        else{

        }
    }
    private boolean diskCheck(File vhd){


        return true;
    }
    private boolean initialize(){
        return true;
    }

    public static byte[] getbytes(int offset, int len) throws IOException {
        ByteIO byteIO = new ByteIO("src/cn/geralt/util/mydisk.vhd");
        return byteIO.output(offset,len);
    }
}
