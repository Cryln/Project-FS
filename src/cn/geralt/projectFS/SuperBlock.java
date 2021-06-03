package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private int userInfoOffset;

    private int iNodeMapOffset;
    private int blockMapOffset;

    public int getiNodeMapOffset() {
        return iNodeMapOffset;
    }

    public int getBlockMapOffset() {
        return blockMapOffset;
    }

    public SuperBlock(FileSystem FSHandler) throws IOException {
        this.FSHandler = FSHandler;
        initialize();
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

//        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(0);
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
        iNodeMapOffset = byteIO.nextInt();
        blockMapOffset = byteIO.nextInt();
        userInfoOffset = byteIO.nextInt();

    }

    public Map<String,Integer> getUsers() throws IOException {
        Map<String,Integer> map = new HashMap<>();
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(userInfoOffset);
        int userAmount = byteIO.nextByte();
        for (int i = 0; i < userAmount; i++) {
            int uid = byteIO.nextInt();
            int nameLen = byteIO.nextByte();
            String name = new String(byteIO.nextBytes(nameLen));
            map.put(name,uid);
        }
        return map;
    }

    public void saveUsers() throws IOException {
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(userInfoOffset);
        int userAmount = FSHandler.getUser2uid().size();
        byteIO.writeBytes(new byte[]{(byte)userAmount});
        for (String s : FSHandler.getUser2uid().keySet()) {
            int uid = FSHandler.getUser2uid().get(s);
            byteIO.writeInt(uid);
            byte[] name = s.getBytes();
            byteIO.writeBytes(new byte[]{(byte)name.length});
            byteIO.writeBytes(name);
        }
    }

    public void format(int[] ints) throws IOException {
//        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(0);
        byteIO.writeInt(ints[0]); //magic 31415926
        byteIO.writeInt(ints[1]); //superBlockLen  4096
        byteIO.writeInt(ints[2]); //partitionSize  16*1024*124
        byteIO.writeInt(ints[3]); //iNodeSize 256
        byteIO.writeInt(ints[4]); //blockSize 4096
        byteIO.writeInt(ints[5]); //iNodeSegOffset 4096
        byteIO.writeInt(ints[6]); //iNodeAmount 4096
        byteIO.writeInt(ints[7]); //dataSegOffset 1052672
        byteIO.writeInt(ints[8]); //blockAmount 3837
        byteIO.writeInt(ints[9]); //rootINode 0
        byteIO.writeInt(ints[10]); //iNode map offset 52
        byteIO.writeInt(ints[11]); //block map offset 52+512=564
        byteIO.writeInt(ints[12]); //userinfo offset 52+512+512=1076

        byteIO.setPos(ints[12]);
        byteIO.writeBytes(new byte[]{1}); //user amount
        byteIO.writeInt(0); // uid
        byteIO.writeBytes(new byte[]{4}); //username len
        byteIO.writeBytes("root".getBytes()); //root


        //init the Map
        byteIO.setPos(ints[10]);
        for (int i = 0; i < 512; i++) {
            byteIO.writeBytes(new byte[]{(byte)(0x00)});
        }
        byteIO.setPos(ints[11]);
        for (int i = 0; i < 512; i++) {
            byteIO.writeBytes(new byte[]{(byte)(0x00)});
        }
//        System.out.println();
        byteIO.setPos(ints[10]);
        byteIO.writeBytes(new byte[]{-128});
        byteIO.setPos(ints[11]);
        byteIO.writeBytes(new byte[]{-128});

        //init the root inode
//        byte[] data = new byte[11];
        byte[] data = new byte[27];

        Date date = new Date();
        data[0] = 0x0; //type
        data[1] = 0x0; //status
        data[3] = 0x0; //nameLen

        System.arraycopy(ByteIO.intToByteArray(0),0,data,3,4); //first block num
        System.arraycopy(ByteIO.intToByteArray(4),0,data,7,4); //rawFileLen
        System.arraycopy(ByteIO.intToByteArray(0),0,data,11,4); //uid
        System.arraycopy(ByteIO.intToByteArray(511),0,data,15,4); //mode 777
        System.arraycopy(ByteIO.longToByteArray(date.getTime()),0,data,19,8); //time
//        System.arraycopy(name,0,data,27,name.length);

        byteIO.setPos(ints[1]+ints[3]*ints[9]); //root iNode offset
        byteIO.writeBytes(data);

        byteIO.setPos(ints[7]); //dataSegOffset
        byteIO.writeBytes(ByteIO.intToByteArray(0)); //4bytes num of children

        initialize();
    }


    public byte[] getINodeMap() throws IOException {
//        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = ByteIO.getInstance();
        return byteIO.output(iNodeMapOffset,4096/8);
    }
    public byte[] getBlockMap() throws IOException {
//        String dir = FSHandler.getVHDDir();
        ByteIO byteIO = ByteIO.getInstance();
        return byteIO.output(blockMapOffset,4096/8);
    }

    public static void main(String[] args) throws IOException {
//        ByteIO byteIO = new ByteIO("temp/text.txt");
//        File myfile0 = new File("temp/1/2");
//        if(!myfile0.exists()){
//            myfile0.mkdirs();
//        }
        File myfile = new File("temp/1/2");
        System.out.println(myfile.exists());
        FileInputStream fileInputStream = new FileInputStream(myfile);
        byte[] bytes = fileInputStream.readAllBytes();
//        byteIO.input(bytes,0);

    }
}
