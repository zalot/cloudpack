package com.taobao.hive.udf;

//Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 8/29/2005 15:46:47
//Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
//Decompiler options: packimports(3) 
//Source File Name:   DES.java

import java.util.Arrays;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
* 对称算法
* DES是Data Encryption Standard（数据加密标准）的缩写。
* CREATE TEMPORARY FUNCTION des AS 'com.taobao.hive.udf.UDFDES';
*/
public class UDFDES extends UDF
{
	
	private Text result = new Text();

 public UDFDES()
 {
     SubKey = new byte[2][16][48];
     Tmp = new byte[256];
     deskey = new byte[16];
     MR = new byte[48];
 }
 
 public Text evaluate(String data,String key,int mode) {
	 
	 if (data ==null || data.equals("") || key == null || "".equals(key) || mode <0 || mode >1) return null;
	 try {
		 result.set(desHex(data,key,mode));
	 } catch (Exception e) {
		 return null;
	 }
	 return result;
 }

 private static void XOR(byte abyte0[], byte abyte1[], int i)
 {
     for(int j = 0; j < i; j++)
         abyte0[j] = (byte)(abyte0[j] ^ abyte1[j]);

 }

 private static void bitToByte(byte abyte0[], byte abyte1[], int i)
 {
     int j = i >> 3;
     Arrays.fill(abyte0, 0, j, (byte)0);
     for(int k = 0; k < i; k++)
         abyte0[k >> 3] |= abyte1[k] << (k & 7);

 }

 private static void byteToBit(byte abyte0[], byte abyte1[], int i)
 {
     for(int j = 0; j < i; j++)
         abyte0[j] = (byte)(abyte1[j >> 3] >> (j & 7) & 1);

 }

 private void rotateL(byte abyte0[], int i, int j)
 {
     System.arraycopy(abyte0, 0, Tmp, 0, j);
     System.arraycopy(abyte0, j, abyte0, 0, i - j);
     System.arraycopy(Tmp, 0, abyte0, i - j, j);
 }

 private void transform(byte abyte0[], byte abyte1[], byte abyte2[], int i)
 {
     for(int j = 0; j < i; j++)
         Tmp[j] = abyte1[abyte2[j] - 1];

     System.arraycopy(Tmp, 0, abyte0, 0, i);
 }

 private void sFunc(byte abyte0[], byte abyte1[])
 {
     for(byte byte0 = 0; byte0 < 8; byte0++)
     {
         byte byte1 = (byte)((abyte1[byte0 * 6 + 0] << 1) + abyte1[byte0 * 6 + 5]);
         byte byte2 = (byte)((abyte1[byte0 * 6 + 1] << 3) + (abyte1[byte0 * 6 + 2] << 2) + (abyte1[byte0 * 6 + 3] << 1) + abyte1[byte0 * 6 + 4]);
         byte abyte2[] = new byte[4];
         for(int i = 0; i < abyte2.length; i++)
             abyte2[i] = abyte0[byte0 * 4 + i];

         byte abyte3[] = new byte[1];
         abyte3[0] = S_Box[byte0][byte1][byte2];
         byteToBit(abyte2, abyte3, 4);
         for(int j = 0; j < abyte2.length; j++)
             abyte0[byte0 * 4 + j] = abyte2[j];

     }

 }

 private void fFunc(byte abyte0[], byte abyte1[])
 {
     transform(MR, abyte0, E_Table, 48);
     XOR(MR, abyte1, 48);
     sFunc(abyte0, MR);
     transform(abyte0, abyte0, P_Table, 32);
 }

 private void setSubKey(byte abyte0[][], byte abyte1[])
 {
     byte abyte2[] = new byte[64];
     byteToBit(abyte2, abyte1, 64);
     transform(abyte2, abyte2, PC1_Table, 56);
     byte abyte3[] = new byte[64];
     System.arraycopy(abyte2, 0, abyte3, 0, 64);
     byte abyte4[] = new byte[36];
     System.arraycopy(abyte2, 28, abyte4, 0, 36);
     for(int i = 0; i < 16; i++)
     {
         rotateL(abyte3, 28, LOOP_Table[i]);
         rotateL(abyte4, 28, LOOP_Table[i]);
         System.arraycopy(abyte3, 0, abyte2, 0, abyte3.length);
         System.arraycopy(abyte4, 0, abyte2, 28, abyte4.length);
         transform(abyte0[i], abyte2, PC2_Table, 48);
     }

 }

 private void doDES(byte abyte0[], byte abyte1[], byte abyte2[][], byte byte0)
 {
     byte abyte3[] = new byte[64];
     byte abyte4[] = new byte[32];
     byteToBit(abyte3, abyte1, 64);
     transform(abyte3, abyte3, IP_Table, 64);
     byte abyte5[] = new byte[32];
     byte abyte6[] = new byte[32];
     System.arraycopy(abyte3, 0, abyte5, 0, 32);
     System.arraycopy(abyte3, 32, abyte6, 0, 32);
     if(byte0 == 1)
     {
         for(int i = 0; i < 16; i++)
         {
             System.arraycopy(abyte6, 0, abyte4, 0, 32);
             fFunc(abyte6, abyte2[i]);
             XOR(abyte6, abyte5, 32);
             System.arraycopy(abyte4, 0, abyte5, 0, 32);
         }

     } else
     {
         for(int j = 15; j >= 0; j--)
         {
             System.arraycopy(abyte5, 0, abyte4, 0, 32);
             fFunc(abyte5, abyte2[j]);
             XOR(abyte5, abyte6, 32);
             System.arraycopy(abyte4, 0, abyte6, 0, 32);
         }

     }
     System.arraycopy(abyte5, 0, abyte3, 0, 32);
     System.arraycopy(abyte6, 0, abyte3, 32, 32);
     transform(abyte3, abyte3, IPR_Table, 64);
     bitToByte(abyte0, abyte3, 64);
 }

 private byte[] usingDES(byte abyte0[], byte abyte1[], byte byte0)
 {
     int i = abyte0.length;
     int j = abyte1.length;
     i = i + 7 & -8;
     byte abyte2[] = new byte[i];
     Arrays.fill(abyte2, (byte)0);
     byte abyte3[] = new byte[i];
     Arrays.fill(abyte3, (byte)0);
     System.arraycopy(abyte0, 0, abyte3, 0, abyte0.length);
     System.arraycopy(abyte3, 0, abyte2, 0, i);
     setKey(abyte1, j);
     if(!is3DES)
     {
         int k = 0;
         for(int i1 = i >> 3; k < i1; k++)
         {
             byte abyte4[] = new byte[8];
             System.arraycopy(abyte2, k * 8, abyte4, 0, 8);
             byte abyte5[] = new byte[8];
             System.arraycopy(abyte3, k * 8, abyte5, 0, 8);
             doDES(abyte4, abyte5, SubKey[0], byte0);
             System.arraycopy(abyte4, 0, abyte2, k * 8, 8);
         }

     } else
     {
         int l = 0;
         for(int j1 = i >> 3; l < j1; l++)
         {
             byte byte1 = 1;
             if(byte0 == 1)
                 byte1 = 2;
             byte abyte6[] = new byte[8];
             System.arraycopy(abyte2, l * 8, abyte6, 0, 8);
             byte abyte7[] = new byte[8];
             System.arraycopy(abyte3, l * 8, abyte7, 0, 8);
             doDES(abyte6, abyte7, SubKey[0], byte0);
             doDES(abyte6, abyte6, SubKey[1], byte1);
             doDES(abyte6, abyte6, SubKey[0], byte0);
             System.arraycopy(abyte6, 0, abyte2, l * 8, 8);
         }

     }
     return abyte2;
 }

 private void setKey(byte abyte0[], int i)
 {
     Arrays.fill(deskey, 0, 16, (byte)0);
     System.arraycopy(abyte0, 0, deskey, 0, i <= 16 ? i : 16);
     setSubKey(SubKey[0], deskey);
     if(i > 8)
     {
         byte abyte1[] = new byte[8];
         System.arraycopy(deskey, 8, abyte1, 0, 8);
         setSubKey(SubKey[1], abyte1);
         is3DES = true;
     } else
     {
         is3DES = false;
     }
 }

 private byte[] doEncrypt(byte abyte0[])
     throws Exception
 {
     byte abyte1[] = usingDES(abyte0, key, (byte)1);
     return abyte1;
 }

 private byte[] doDecrypt(byte abyte0[])
     throws Exception
 {
     byte abyte1[] = usingDES(abyte0, key, (byte)2);
     return abyte1;
 }

 private String toHexString(byte abyte0[])
 {
     String s = "";
     for(int i = 0; i < abyte0.length; i++)
     {
         byte byte0 = abyte0[i];
         String s1 = Integer.toHexString(byte0);
         if(s1.length() > 2)
             s1 = s1.substring(s1.length() - 2);
         if(s1.length() < 2)
             s1 = "0" + s1;
         s = s + s1;
     }

     return s.toUpperCase();
 }

 private byte[] hexStr2ByteArr(String s)
     throws Exception
 {
     byte abyte0[] = s.getBytes();
     int i = abyte0.length;
     byte abyte1[] = new byte[i / 2];
     for(int j = 0; j < i; j += 2)
     {
         String s1 = new String(abyte0, j, 2);
         abyte1[j / 2] = (byte)Integer.parseInt(s1, 16);
     }

     return abyte1;
 }

 public final String desHex(String s, String s1, int i) throws Exception
 {        
     if(i == 0)
     {
         key = s1.getBytes();
         byte abyte0[] = doEncrypt(s.getBytes());
         String s3 = toHexString(abyte0);
         return s3;
     } else
     {
         key = s1.getBytes();
         byte abyte1[] = hexStr2ByteArr(s);
         byte abyte2[] = doDecrypt(abyte1);
         String s4 = new String(abyte2);
         return s4.trim();
     }
 }

 private static final byte IP_Table[] = {
     58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 
     44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 
     30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 
     16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 
     59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 
     45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 
     31, 23, 15, 7
 };
 private static final byte IPR_Table[] = {
     40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 
     47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 
     54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 
     61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 
     35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 
     42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 
     49, 17, 57, 25
 };
 private static final byte E_Table[] = {
     32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 
     8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 
     14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 
     20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 
     28, 29, 28, 29, 30, 31, 32, 1
 };
 private static final byte P_Table[] = {
     16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 
     23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 
     32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 
     4, 25
 };
 private static final byte PC1_Table[] = {
     57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 
     42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 
     27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 
     47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 
     30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 
     13, 5, 28, 20, 12, 4
 };
 private static final byte PC2_Table[] = {
     14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 
     21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 
     27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 
     30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 
     34, 53, 46, 42, 50, 36, 29, 32
 };
 private static final byte LOOP_Table[] = {
     1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 
     2, 2, 2, 2, 2, 1
 };
 private static final byte S_Box[][][] = {
     {
         {
             14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 
             6, 12, 5, 9, 0, 7
         }, {
             0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 
             12, 11, 9, 5, 3, 8
         }, {
             4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 
             9, 7, 3, 10, 5, 0
         }, {
             15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 
             3, 14, 10, 0, 6, 13
         }
     }, {
         {
             15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 
             2, 13, 12, 0, 5, 10
         }, {
             3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 
             1, 10, 6, 9, 11, 5
         }, {
             0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 
             12, 6, 9, 3, 2, 15
         }, {
             13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 
             7, 12, 0, 5, 14, 9
         }
     }, {
         {
             10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 
             12, 7, 11, 4, 2, 8
         }, {
             13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 
             5, 14, 12, 11, 15, 1
         }, {
             13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 
             2, 12, 5, 10, 14, 7
         }, {
             1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 
             14, 3, 11, 5, 2, 12
         }
     }, {
         {
             7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 
             8, 5, 11, 12, 4, 15
         }, {
             13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 
             2, 12, 1, 10, 14, 9
         }, {
             10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 
             3, 14, 5, 2, 8, 4
         }, {
             3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 
             5, 11, 12, 7, 2, 14
         }
     }, {
         {
             2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 
             3, 15, 13, 0, 14, 9
         }, {
             14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 
             15, 10, 3, 9, 8, 6
         }, {
             4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 
             12, 5, 6, 3, 0, 14
         }, {
             11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 
             0, 9, 10, 4, 5, 3
         }
     }, {
         {
             12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 
             3, 4, 14, 7, 5, 11
         }, {
             10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 
             13, 14, 0, 11, 3, 8
         }, {
             9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 
             4, 10, 1, 13, 11, 6
         }, {
             4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 
             1, 7, 6, 0, 8, 13
         }
     }, {
         {
             4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 
             9, 7, 5, 10, 6, 1
         }, {
             13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 
             5, 12, 2, 15, 8, 6
         }, {
             1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 
             6, 8, 0, 5, 9, 2
         }, {
             6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 
             0, 15, 14, 2, 3, 12
         }
     }, {
         {
             13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 
             3, 14, 5, 0, 12, 7
         }, {
             1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 
             6, 11, 0, 14, 9, 2
         }, {
             7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 
             10, 13, 15, 3, 5, 8
         }, {
             2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 
             9, 0, 3, 5, 6, 11
         }
     }
 };
 private byte key[];
 private boolean is3DES;
 private byte SubKey[][][];
 private byte Tmp[];
 private byte deskey[];
 private byte MR[];

}
