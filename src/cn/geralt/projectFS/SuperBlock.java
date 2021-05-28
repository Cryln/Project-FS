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

    public SuperBlock getNextSuperBlock() {
        return nextSuperBlock;
    }

    public void setNextSuperBlock(SuperBlock nextSuperBlock) {
        this.nextSuperBlock = nextSuperBlock;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    public int getiNodeSize() {
        return iNodeSize;
    }

    public void setiNodeSize(int iNodeSize) {
        this.iNodeSize = iNodeSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getiNodeSegOffset() {
        return iNodeSegOffset;
    }

    public void setiNodeSegOffset(int iNodeSegOffset) {
        this.iNodeSegOffset = iNodeSegOffset;
    }

    public int getLenOfINodeSeg() {
        return lenOfINodeSeg;
    }

    public void setLenOfINodeSeg(int lenOfINodeSeg) {
        this.lenOfINodeSeg = lenOfINodeSeg;
    }

    public int getDataSegOffset() {
        return DataSegOffset;
    }

    public void setDataSegOffset(int dataSegOffset) {
        DataSegOffset = dataSegOffset;
    }

    public int getLenOfDataSeg() {
        return lenOfDataSeg;
    }

    public void setLenOfDataSeg(int lenOfDataSeg) {
        this.lenOfDataSeg = lenOfDataSeg;
    }

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
