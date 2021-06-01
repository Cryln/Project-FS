package cn.geralt.util;

import java.io.*;

public class ByteIO {
    private String filedir;
//    private File file;
//    private FileOutputStream fileOutputStream;
//    private FileInputStream fileInputStream;
    private RandomAccessFile randomAccessFile;
    public ByteIO(String filedir) throws FileNotFoundException {
        this.filedir = filedir;
//        this.file = new File(this.filedir);
//        this.fileInputStream = new FileInputStream(this.file);
//        this.fileOutputStream = new FileOutputStream(this.file);
        this.randomAccessFile = new RandomAccessFile(this.filedir,"rw");
    }
    public void input(String content, int off) throws IOException {
        byte[] bytes =  content.getBytes();
//        this.fileOutputStream.write(bytes,off,len);
        this.randomAccessFile.seek(off);
        this.randomAccessFile.write(bytes,0, bytes.length);
    }
    public void input(byte[] bytes,int off) throws IOException {
        this.randomAccessFile.seek(off);
        this.randomAccessFile.write(bytes,0,bytes.length);
    }
    public byte[] output(int off, int bytenum) throws IOException {
        byte[] bytes = new byte[bytenum];
//        for (int i = 0; i < bytenum; i++) {
//            this.fileInputStream.read(bytes,off,bytenum);
//            this.randomAccessFile.read(bytes,off,bytenum);
//        }
        this.randomAccessFile.seek(off);
        this.randomAccessFile.read(bytes,0,bytenum);
        this.randomAccessFile.readByte();
        return bytes;
    }

    public static void main(String[] args) throws IOException {
        ByteIO byteIO = new ByteIO("src/cn/geralt/util/mydisk.vhd");
//        ByteIO byteIO2 = new ByteIO("D:\\Codes\\java\\Project-FS\\src\\cn\\geralt\\util\\JOS.vhd");
        byteIO.input("hello world",4096);
        byte[] bytes = byteIO.output(4096,11);
        for (byte aByte : bytes) {
            System.out.println(String.format("%X",aByte));
        }
        System.out.println();
        System.out.println(new String(bytes));
    }
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
