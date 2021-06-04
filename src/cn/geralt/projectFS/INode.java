package cn.geralt.projectFS;

//import cn.geralt.util.ByteIO;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class INode {
    private SuperBlock SBHandler;
    private int iNodeNum;
    private int firstBlock;
    private byte type;
    private byte status;
    private int rawFileLen;
    private long lastModifyTime;
    private int uid;
    private int mode;

    private int fileNameLen;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    //    static int iNodeAreaOffset = 4096;
//    static int iNodeLen = 256;
    public INode(SuperBlock superBlock ,int iNodeNum) throws IOException {
        this.iNodeNum = iNodeNum;
        this.SBHandler = superBlock;
        initialize();
    }

    public byte getType() {
        return type;
    }

    public byte getStatus() {
        return status;
    }

    public byte getRealStatus() throws IOException {
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(SBHandler.getiNodeSegOffset()+SBHandler.getiNodeSize()*iNodeNum+1);
        return byteIO.nextByte();
    }

    public void setRealStatus(byte b) throws IOException {
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(SBHandler.getiNodeSegOffset()+SBHandler.getiNodeSize()*iNodeNum+1);
        byteIO.writeBytes(new byte[]{b});
        setStatus(b);
    }

    public int getRawFileLen() {
        return rawFileLen;
    }
//    public static INode getInstance(int iNodeNum){
//
//    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public int getUid() {
        return uid;
    }

    public int getMode() {
        return mode;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void initialize() throws IOException {
//        byte[] bytes = FileSystem.getbytes(SBHandler.getiNodeSegOffset()+iNodeNum* SBHandler.getiNodeSize()
//                ,SBHandler.getiNodeSize());
//
//        this.type = bytes[0]; //first byte for type; 0-> dir;1->file
//        this.status = bytes[1]; //2nd byte for status
//        //next 4 bytes for number of first block
//        this.firstBlock = ByteIO.byteArrayToInt(bytes,2);
//        //next 4 bytes for length of raw file // x byte
//        this.rawFileLen = ByteIO.byteArrayToInt(bytes,6);
//        //9th byte for length of filename
//        this.fileNameLen = (int)bytes[10];
//        byte[] buffer = new byte[this.fileNameLen];
//        System.arraycopy(bytes,11,buffer,0,this.fileNameLen);
//        this.fileName = new String(buffer);
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(SBHandler.getiNodeSegOffset()+iNodeNum*SBHandler.getiNodeSize());
        type = byteIO.nextByte();
        status = byteIO.nextByte();
        fileNameLen = byteIO.nextByte();
        firstBlock = byteIO.nextInt();
        rawFileLen = byteIO.nextInt();
        uid = byteIO.nextInt();
        mode = byteIO.nextInt();
        lastModifyTime = ByteIO.byteArrayToLong(byteIO.nextBytes(8));
        byte[] buffer = new byte[this.fileNameLen];
        System.arraycopy(byteIO.nextBytes(fileNameLen),0,buffer,0,this.fileNameLen);
        this.fileName = new String(buffer);
    }

    private int[][] getOffSet(int off,int len) throws IOException {
        //get the start
        int start = off;
        List<List<Integer>> int2d = new ArrayList<>();
        int nextBlockNum = firstBlock;
        int curS = B2P(nextBlockNum);
        int curE = curS + SBHandler.getBlockSize() -4;
        nextBlockNum = getNextBlockNum(nextBlockNum);
        while(curS+start>=curE && nextBlockNum!=0){
            start -= (SBHandler.getBlockSize()-4);
            curS = B2P(nextBlockNum);
            curE = curS + SBHandler.getBlockSize() -4;
            nextBlockNum = getNextBlockNum(nextBlockNum);
        }
        //get the end
        int rest = len;
        while(curS+start+rest>=curE){
            List<Integer> temp = new ArrayList<>();
            int tempStart = curS+start;
            temp.add(tempStart);
            int tempLen = curE-tempStart;
            temp.add(tempLen);
            int2d.add(temp);

            start = 0 ;
            curS = B2P(nextBlockNum);
            curE = curS + SBHandler.getBlockSize() -4;
            nextBlockNum = getNextBlockNum(nextBlockNum);
            rest -= tempLen;
        }
        List<Integer> temp = new ArrayList<>();
        int tempStart = curS+start;
        temp.add(tempStart);
        temp.add(rest);
        int2d.add(temp);

        int[][] ans = new int[int2d.size()][2];
        for (int i = 0; i < int2d.size(); i++) {
            ans[i][0] = int2d.get(i).get(0);
            ans[i][1] = int2d.get(i).get(1);
        }
        return ans;
    }

    public int[] getAllBlockNum() throws IOException {
        int amount = this.rawFileLen/(SBHandler.getBlockSize()-4);
        if(this.rawFileLen%(SBHandler.getBlockSize()-4)>0) amount++;
        int[] ans = new int[amount];
        int nextBlockNum = this.firstBlock;
        for (int i = 0; i < amount; i++) {
            ans[i] = nextBlockNum;
            nextBlockNum = getNextBlockNum(nextBlockNum);
        }
        return ans;
    }

//    public int getEndOffset() throws IOException {
//        int[][] a = getOffSet(rawFileLen,0);
//        return a[0][0];
//    }

    private int getNextBlockNum(int curBlockNum) throws IOException {
        byte[] curBlock = FileSystem.getbytes(SBHandler.getDataSegOffset()+curBlockNum* SBHandler.getBlockSize()
                , SBHandler.getBlockSize());
        return ByteIO.byteArrayToInt(Arrays.copyOfRange(curBlock,curBlock.length-4,curBlock.length));
    }

    private int B2P(int blockNum){
        /*
        * get the blockNum_th block's offset
        * */
        return SBHandler.getDataSegOffset()+ SBHandler.getBlockSize()*blockNum;
    }

    public void update() throws IOException {
//        ByteIO byteIO = ByteIO.getInstance();
//        byte[] name = getFileName().getBytes();
//        byte[] data = new byte[11+name.length];
//        data[0] = getType(); //type
//        data[1] = getStatus(); //status
//
//        System.arraycopy(ByteIO.intToByteArray(this.firstBlock),0,data,2,4); //first block num
//        System.arraycopy(ByteIO.intToByteArray(this.rawFileLen),0,data,6,4); //rawFileLen
//        data[10] = (byte)name.length ; //filename len
//        System.arraycopy(name,0,data,11,name.length);
//        byteIO.setPos(iNodeNum*SBHandler.getiNodeSize()+SBHandler.getiNodeSegOffset());
//        byteIO.writeBytes(data);
        ByteIO byteIO = ByteIO.getInstance();
        byte[] name = getFileName().getBytes();
//        byte[] data = new byte[11+name.length];
        byte[] data = new byte[27+name.length];
        data[0] = getType(); //type
        data[1] = getStatus(); //status
        data[2] = (byte)name.length; //nameLen
        System.arraycopy(ByteIO.intToByteArray(this.firstBlock),0,data,3,4); //first block num
        System.arraycopy(ByteIO.intToByteArray(this.rawFileLen),0,data,7,4); //rawFileLen
        System.arraycopy(ByteIO.intToByteArray(this.uid),0,data,11,4); //uid
        System.arraycopy(ByteIO.intToByteArray(this.mode),0,data,15,4); //mode
        System.arraycopy(ByteIO.longToByteArray(this.lastModifyTime),0,data,19,8); //time
        System.arraycopy(name,0,data,27,name.length); //file name
        byteIO.setPos(iNodeNum*SBHandler.getiNodeSize()+SBHandler.getiNodeSegOffset());
        byteIO.writeBytes(data);
    }

    public byte[] read() throws IOException {
        byte[] buffer = new byte[rawFileLen];
        int restBytes = rawFileLen;
        int nextBlockNum = this.firstBlock;
        while (restBytes > SBHandler.getBlockSize()){
            byte[] temp = FileSystem.getbytes(SBHandler.getDataSegOffset()+nextBlockNum* SBHandler.getBlockSize()
            , SBHandler.getBlockSize());
            System.arraycopy(temp,0,buffer,rawFileLen-restBytes,temp.length-4);
            restBytes -= (temp.length-4);
            nextBlockNum = ByteIO.byteArrayToInt(Arrays.copyOfRange(temp,temp.length-4,temp.length));
        }
        if(restBytes<= SBHandler.getBlockSize() && restBytes!=0){
            byte[] temp = FileSystem.getbytes(SBHandler.getDataSegOffset()+nextBlockNum* SBHandler.getBlockSize()
                    , SBHandler.getBlockSize());
            System.arraycopy(temp,0,buffer,rawFileLen-restBytes,restBytes);
            restBytes -= restBytes;
        }
        return buffer;
    }

    private int needBlockAmount(int len){
        int a = len/(SBHandler.getBlockSize()-4);
        int b = len%(SBHandler.getBlockSize()-4);
        if(b>0) a++;
        return a;
    }

    private int fileOff2PhysicOff(int off) throws IOException {
//        int a = off/(SBHandler.getBlockSize()-4);
//        int b = off%(SBHandler.getBlockSize()-4);
//        if(b>0)a++;
//        int physicOff = firstBlock* SBHandler.getBlockSize()+SBHandler.getDataSegOffset();
//        for (int i = 0; i < a; i++) {
//            physicOff = ByteIO.byteArrayToInt(FileSystem.getbytes(physicOff+ SBHandler.getBlockSize()-4,4));
//        }
//        return physicOff+b;
        int[][]ans =  getOffSet(off,0);
        return ans[0][0];
    }

    public int[] write(byte[] data,int off,int[] additions) throws IOException {

        int[] ans;
        int newLen = off+data.length;
        int rawBlockNum = needBlockAmount(rawFileLen);
        int newBlockNum = needBlockAmount(newLen);

        ByteIO byteIO = ByteIO.getInstance();

        if(rawBlockNum == newBlockNum){


            int[][] periods = getOffSet(off,data.length);
            int pos = 0;
            for (int[] period : periods) {
                byteIO.input(Arrays.copyOfRange(data,pos,pos+period[1]),period[0]);
                pos += period[1];
            }

            ans = new int[1];
            ans[0] = 1;
            this.rawFileLen = newLen;
            return ans;
        }
        else if(rawBlockNum<newBlockNum){
            if(additions==null){
                ans = new int[2];
                ans[0] = 0;
                ans[1] = newBlockNum-rawBlockNum;
                return ans;
            }else{
                if(newBlockNum-rawBlockNum<additions.length){
                    ans = new int[1];
                    ans[0] = -1;
                    System.out.println("分配的block 不足");
                    return ans;
                }
                ans = new int[newBlockNum-rawBlockNum+1];

                int blockIndex = rawBlockNum;
                int nextBlockPtrAddr = fileOff2PhysicOff(blockIndex* (SBHandler.getBlockSize()-4)-1)+1;
                for (int i = 0; i < additions.length; i++) {
                    byteIO.setPos(nextBlockPtrAddr);
                    byteIO.writeInt(additions[i]);
                    blockIndex ++;
                    nextBlockPtrAddr = fileOff2PhysicOff(blockIndex* (SBHandler.getBlockSize()-4)-1)+1;
                    ans[i+1] = additions[i];
                }
//
//                for (int additon : additions) {
//                    byteIO.setPos(nextBlockPtrAddr);
//                    byteIO.writeInt(additon);
//                    blockIndex ++;
//                    nextBlockPtrAddr = fileOff2PhysicOff(blockIndex* (SBHandler.getBlockSize()-4)-1)+1;
//                    ans[index+1] = additon;
//                    index++;
//                }
                this.rawFileLen = newLen;
                write(data,off,null);
                ans[0]=2;
                return ans;
            }
        }
        else{
            ans = new int[1+rawBlockNum-newBlockNum];
            int blockIndex = rawBlockNum;
            int blockPtrAddr = fileOff2PhysicOff((blockIndex-1)* (SBHandler.getBlockSize()-4)-1)+1;
            for (int i = 0; i < rawBlockNum - newBlockNum; i++) {
                byteIO.setPos(blockPtrAddr);
                ans[i+1] = byteIO.nextInt();
                byteIO.setPos(blockPtrAddr);
                byteIO.writeInt(0);
                blockIndex--;
                blockPtrAddr = fileOff2PhysicOff((blockIndex-1)* (SBHandler.getBlockSize()-4)-1)+1;
            }
            this.rawFileLen = newLen;
            write(data,off,null);
            ans[0]=3;
            return ans;
        }

    }

    public static void initInode(String dirName,int type,int off,int firstBlock,User owner) throws IOException {
        ByteIO byteIO = ByteIO.getInstance();
        byte[] name = dirName.getBytes();
//        byte[] data = new byte[11+name.length];
        byte[] data = new byte[27+name.length];

        int fileLen = 0;
        int mode = 0777;
        switch (type) {
            case 0: fileLen = 4;
            mode = 0755;
            break;
            case 1: fileLen = 0;
            mode = 0644;
            break;
        }
        Date date = new Date();

        data[0] = (byte)type ; //type
        data[1] = 0x0; //status
//        System.arraycopy(ByteIO.intToByteArray(firstBlock),0,data,2,4); //first block num
//
//        System.arraycopy(ByteIO.intToByteArray(fileLen),0,data,6,4); //rawFileLen
//        data[10] = (byte)name.length; //filename len
//        System.arraycopy(dirName.getBytes(),0,data,11,dirName.length());
        data[2] = (byte)name.length;
        System.arraycopy(ByteIO.intToByteArray(firstBlock),0,data,3,4); //first block num
        System.arraycopy(ByteIO.intToByteArray(fileLen),0,data,7,4); //rawFileLen
        System.arraycopy(ByteIO.intToByteArray(owner.getUid()),0,data,11,4); //uid
        System.arraycopy(ByteIO.intToByteArray(mode),0,data,15,4); //mode
        System.arraycopy(ByteIO.longToByteArray(date.getTime()),0,data,19,8); //time
        System.arraycopy(name,0,data,27,name.length);

        byteIO.setPos(off);
        byteIO.writeBytes(data);


    }
}
