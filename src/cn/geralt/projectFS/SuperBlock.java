package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SuperBlock {
    private SuperBlock nextSuperBlock = null;
    private String device = null;
    private int diskSize = 16*1024*1024;
    private int iNodeSize_bits = 8;
    private int iNodeSize = 256;
    private int blockSize_bits = 12;
    private int blockSize = 4096;
    private int maxBytes;
    private int magic;
    private int iNodeSegOffset = 4096;
    private int lenOfINodeSeg = (diskSize/blockSize)*iNodeSize;
    private int DataSegOffset = iNodeSegOffset+lenOfINodeSeg;
    private int lenOfDataSeg = diskSize-DataSegOffset;

    public void load() throws FileNotFoundException {
        ByteIO byteIO = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\mydisk.vhd");

    }
    public void save() throws IOException {
        ByteIO byteIO = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\mydisk.vhd");
        File myfile = new File("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\hello.txt");
        FileInputStream fileInputStream = new FileInputStream( myfile);
        byte[] bytes = fileInputStream.readAllBytes();
        byteIO.input(bytes,this.DataSegOffset);
    }

    public static void main(String[] args) throws IOException {
        SuperBlock superBlock = new SuperBlock();
        superBlock.save();

    }
}
