package cn.geralt.projectFS;


import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Test {
    public static void setMapBit(byte[] bytes,int pos,boolean bit){
        int a = pos/8;
        int b = pos%8;
        byte temp = bytes[a];
        byte mask = (byte)(0b00000001 << b);
        if((bit&&((byte)(temp&mask)!=0))||(!bit&&((byte)(temp&mask)==0))){

        }
        else {
            temp = (byte) (temp ^ mask);
            bytes[a] = temp;
            System.out.println(String.format("%X",bytes[a]));
            System.out.println(String.format("%X",temp));
        }
    }
    public static void main(String[] args) throws IOException {
//        System.out.println(new String("").getBytes()[0]);
//        byte temp =(byte)0b10101010;
//        int t = 4;
//        byte mask = (byte)(0b00000001 << t);
//        boolean bit = false;
//        if((bit&&((byte)(temp&mask)!=0))||(!bit&&((byte)(temp&mask)==0))){
//            System.out.println("yes");
//        }
//        else{
//            System.out.println("no");
//        }
//        byte[] bytes = new byte[]{(byte)0xaa,(byte)0xaa,(byte)0xaa};
//        setMapBit(bytes,10,true);
//        System.out.println(String.format("%X",bytes[1]));

        int[][] ints = {{1,2},{1,2}};
        System.out.println(ints.length);

//        a =(byte)(a^b);
//        a =(byte)(a^b);
//        System.out.println(String.format("%X",temp));
    }}
