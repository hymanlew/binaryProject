package main.java.code;


import java.io.UnsupportedEncodingException;

public class Demo01 {

    public static void main(String[] args) throws UnsupportedEncodingException {

        // Integer.parseInt("10")
        int n = 12;
        System.out.println(n);
        printInteger(n);

        for(int i=0; i<=50; i++){
            printInteger(i);
        }


        n = 0x6cfed5af;
        printInteger(n);


        n = -1;
        printInteger(n);
        long nl = -1L;
        printLong(nl);
        printLong(n);


        for(int i=-50; i<=50; i++){
            printInteger(n);
        }


        int max = Integer.MAX_VALUE;
        System.out.println(max);
        printInteger(max);
        int min = Integer.MIN_VALUE;
        System.out.println(min);
        printInteger(min);
        max = 0x7fffffff;
        min = 0x80000000;


        n = 8;
        //n =  00000000 00000000 00000000 00001000
        //~n=  11111111 11111111 11111111 11110111 -9
        //~n+1 11111111 11111111 11111111 11111000 -8
        int m = ~n+1;
        //-8
        System.out.println(m);
        printInteger(n);
        printInteger(~n);
        printInteger(~n+1);
        printInteger(m);


        int c = '卢';
        int b3 = c & 0x3f | 0x80;
        int b2 = (c>>>6) & 0x3f | 0x80;
        int b1 = (c>>>12) & 0xf | 0xe0;
        // 验证: new String(bytes, 编码方案)，将bytes数据进行解码
        byte[] bytes = {
                (byte)b1,(byte)b2,(byte)b3,
                (byte)b1,(byte)b2,(byte)b3};
        String str=new String(bytes,"UTF-8");
        System.out.println(str);

        int ch = (b1&0xf)<<12 |
                (b2&0x3f)<<6 |
                (b3&0x3f)<<0;

        System.out.println((char)ch);
    }

    private static void printInteger(int i){
        System.out.println(Integer.toBinaryString(i));
    }

    private static void printLong(long i){
        System.out.println(Long.toBinaryString(i));
    }
}
