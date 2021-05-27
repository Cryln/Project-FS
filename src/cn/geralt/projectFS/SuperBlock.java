package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SuperBlock {
    private SuperBlock nextSuperBlock = null; //下一个超级块，暂时没用
    private String device = null;   //设备标识
    private int partitionSize = 16*1024*1024; //分区大小
//    private int iNodeSize_bits = 8;
    private int iNodeSize = 256; //i节点大小
//    private int blockSize_bits = 12;
    private int blockSize = 4096; //块大小
    private int maxBytes; // 最大文件字节数
    private int magic; //魔数，用来标识文件系统
    private int iNodeSegOffset = 4096; //i节点区的起始地址，也是超级块的大小
    private int lenOfINodeSeg = (partitionSize /blockSize)*iNodeSize; //i节点区的长度
    private int DataSegOffset = iNodeSegOffset+lenOfINodeSeg; //数据区的起始地址
    private int lenOfDataSeg = partitionSize -DataSegOffset; // 数据区长度

    public void load() throws FileNotFoundException {
        ByteIO byteIO = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\mydisk.vhd");

    }
    public void save() throws IOException {
        ByteIO byteIO = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\mydisk.vhd");
        File myfile = new File("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\hello.txt");
        FileInputStream fileInputStream = new FileInputStream(myfile);
        byte[] bytes = fileInputStream.readAllBytes();
        byteIO.input(bytes,this.DataSegOffset);
    }

    public static void main(String[] args) throws IOException {
        SuperBlock superBlock = new SuperBlock();
        superBlock.save();

    }
}
