package cn.geralt.projectFS;

//import cn.geralt.util.ByteIO;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class INode {
    private SuperBlock SBHandler;
    private int iNodeNum;
    private int firstBlock;
    private byte type;
    private byte status;
    private int rawFileLen;
//    static int iNodeAreaOffset = 4096;
//    static int iNodeLen = 256;
    public INode(int iNodeNum) throws IOException {
        this.iNodeNum = iNodeNum;
        init(FileSystem.getbytes(SBHandler.getiNodeSegOffset()+iNodeNum* SBHandler.getiNodeSize()
                ,SBHandler.getiNodeSize()));
    }
    private void init(byte[] bytes){
        this.type = bytes[0];
        this.status = bytes[1];
        this.firstBlock = ByteIO.byteArrayToInt(Arrays.copyOfRange(bytes,2,2+4));
        this.rawFileLen = ByteIO.byteArrayToInt(Arrays.copyOfRange(bytes,2+4,2+4+4));
    }
    public byte[] getFile() throws IOException {
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
}
