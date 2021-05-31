package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class SuperBlock {
    private FileSystem FSHandler;
    private int superBlockLen = 4096;
    private SuperBlock nextSuperBlock = null; //下一个超级块，暂时没用
    private String device = null;   //设备标识
    private int partitionSize = 16*1024*1024; //分区大小
//    private int iNodeSize_bits = 8;
    private int iNodeSize = 256; //i节点大小
//    private int blockSize_bits = 12;
    private int blockSize = 4096; //块大小

    private int iNodeAmount = 4096;
    private int blockAmount  = 3839;

    private int rootINode = 0;

    private int maxBytes; // 最大文件字节数
    private int magic = 1415926; //魔数，用来标识文件系统
    private int iNodeSegOffset = 4096; //i节点区的起始地址，也是超级块的大小
    private int lenOfINodeSeg = (partitionSize /blockSize)*iNodeSize; //i节点区的长度
    private int dataSegOffset = iNodeSegOffset+lenOfINodeSeg; //数据区的起始地址
    private int lenOfDataSeg = partitionSize - dataSegOffset; // 数据区长度

    public SuperBlock(FileSystem FSHandler) {
        this.FSHandler = FSHandler;
    }

    public SuperBlock getNextSuperBlock() {
        return nextSuperBlock;
    }

    private void setNextSuperBlock(SuperBlock nextSuperBlock) {
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

    private void setiNodeSize(int iNodeSize) {
        this.iNodeSize = iNodeSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    private void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getMaxBytes() {
        return maxBytes;
    }

    private void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    public int getMagic() {
        return magic;
    }

    private void setMagic(int magic) {
        this.magic = magic;
    }

    public int getiNodeSegOffset() {
        return iNodeSegOffset;
    }

    private void setiNodeSegOffset(int iNodeSegOffset) {
        this.iNodeSegOffset = iNodeSegOffset;
    }

    public int getLenOfINodeSeg() {
        return lenOfINodeSeg;
    }

    private void setLenOfINodeSeg(int lenOfINodeSeg) {
        this.lenOfINodeSeg = lenOfINodeSeg;
    }

    public int getDataSegOffset() {
        return dataSegOffset;
    }

    private void setDataSegOffset(int dataSegOffset) {
        this.dataSegOffset = dataSegOffset;
    }

    public int getLenOfDataSeg() {
        return lenOfDataSeg;
    }

    private void setLenOfDataSeg(int lenOfDataSeg) {
        this.lenOfDataSeg = lenOfDataSeg;
    }

    public int getRootINode() {
        return rootINode;
    }

    public void setRootINode(int rootINode) {
        this.rootINode = rootINode;
    }

    private void initialize() throws IOException {
        //TODO: 2.
        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = new ByteIO(dir);
        magic = byteIO.nextInt();
        superBlockLen = byteIO.nextInt();
        partitionSize = byteIO.nextInt();
        iNodeSize = byteIO.nextInt();
        blockSize = byteIO.nextInt();
        iNodeSegOffset = byteIO.nextInt();
        iNodeAmount = byteIO.nextInt();
        dataSegOffset = byteIO.nextInt();
        blockAmount = byteIO.nextInt();
        rootINode = byteIO.nextInt();

    }

    public void format(int[] ints) throws IOException {
        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = new ByteIO(dir);
        byteIO.writeInt(ints[0]); //magic 31415926
        byteIO.writeInt(ints[1]); //superBlockLen  4096
        byteIO.writeInt(ints[2]); //partitionSize  16*1024*124
        byteIO.writeInt(ints[3]); //iNodeSize 256
        byteIO.writeInt(ints[4]); //blockSize 4096
        byteIO.writeInt(ints[5]); //iNodeSegOffset 4096
        byteIO.writeInt(ints[6]); //iNodeAmount 4096
        byteIO.writeInt(ints[7]); //dataSegOffset 1052672
        byteIO.writeInt(ints[8]); //blockAmount 3839
        byteIO.writeInt(ints[9]); //rootINode 0



        byteIO.setPos(ints[1]+ints[3]*ints[9]); //root iNode offset
        byte[] data = new byte[11];
        data[0] = 0x0; //type
        data[1] = 0x0; //status
        System.arraycopy(ByteIO.intToByteArray(0),0,data,2,4); //first block num
        System.arraycopy(ByteIO.intToByteArray(1),0,data,6,4); //rawFileLen
        data[10] = 0x0; //filename len
        System.arraycopy(new String("").getBytes(),0,data,11,0);

        byteIO.writeBytes(data);

        byteIO.setPos(ints[7]); //dataSegOffset
        byteIO.writeBytes(ByteIO.intToByteArray(0)); //4bytes num of children

        initialize();
    }

    public void load() throws FileNotFoundException {
        ByteIO byteIO = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\mydisk.vhd");

    }
    public void save() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        ByteIO byteIO = new ByteIO("temp/text.txt");
//        File myfile0 = new File("temp/1/2");
//        if(!myfile0.exists()){
//            myfile0.mkdirs();
//        }
        File myfile = new File("temp/1/2");
        System.out.println(myfile.exists());
        FileInputStream fileInputStream = new FileInputStream(myfile);
        byte[] bytes = fileInputStream.readAllBytes();
        byteIO.input(bytes,0);

    }
}
