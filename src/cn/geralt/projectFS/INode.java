package cn.geralt.projectFS;

//import cn.geralt.util.ByteIO;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.Arrays;

public class INode {
    private SuperBlock SBHandler;
    private int iNodeNum;
    private int firstBlock;
    private byte type;
    private byte status;
    private int rawFileLen;

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


//    public static INode getInstance(int iNodeNum){
//
//    }

    private void initialize() throws IOException {
        byte[] bytes = FileSystem.getbytes(SBHandler.getiNodeSegOffset()+iNodeNum* SBHandler.getiNodeSize()
                ,SBHandler.getiNodeSize());

        this.type = bytes[0]; //first byte for type; 0-> dir;1->file
        this.status = bytes[1]; //2nd byte for status
        //next 4 bytes for number of first block
        this.firstBlock = ByteIO.byteArrayToInt(bytes,2);
        //next 4 bytes for length of raw file // x byte
        this.rawFileLen = ByteIO.byteArrayToInt(bytes,6);
        //9th byte for length of filename
        this.fileNameLen = (int)bytes[10];
        byte[] buffer = new byte[this.fileNameLen];
        System.arraycopy(bytes,11,buffer,0,this.fileNameLen);
        this.fileName = new String(buffer);
    }

    private int[][] getOffSet(int off,int len){


        return null ;
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
        int a = off/(SBHandler.getBlockSize()-4);
        int b = off%(SBHandler.getBlockSize()-4);
        if(b>0)a++;
        int physicOff = firstBlock* SBHandler.getBlockSize()+SBHandler.getDataSegOffset();
        for (int i = 0; i < a; i++) {
            physicOff = ByteIO.byteArrayToInt(FileSystem.getbytes(physicOff+ SBHandler.getBlockSize()-4,4));
        }
        return physicOff+b;
    }

    public int[] write(byte[] bytes,int off,int[] additions) throws IOException {
        //TODO: first
        int[] ans;
        int newLen = off+bytes.length;
        int rawBlockNum = needBlockAmount(rawFileLen);
        int newBlockNum = needBlockAmount(newLen);

        ByteIO byteIO = new ByteIO("src/cn/geralt/util/mydisk.vhd");

        if(rawBlockNum == newBlockNum){
            //TODO:

            int[][] periods = getOffSet(off,bytes.length);
            int pos = 0;
            for (int[] period : periods) {
                byteIO.input(Arrays.copyOfRange(bytes,pos,pos+period[1]),period[0]);
                pos += period[1];
            }

            ans = new int[1];
            ans[0] = 1;
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
                int index = 0;
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
                write(bytes,off,null);
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
            write(bytes,off,null);
            ans[0]=3;
            return ans;
        }

    }
}
