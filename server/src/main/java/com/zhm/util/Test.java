package com.zhm.util;

public class Test {
    public static void main(String[] args){
       String str="001";
       String str1="001005";
       String my=str1.substring(str1.length()-3,str1.length());
       String my1=str1.substring(0,str1.length()-1);
       String end=my1+String.valueOf(Integer.parseInt(my)+1);
       System.out.println(str.length()+"-----"+str1.length()+"----"+my+"-----"+my1+"------"+end);
    }
}
