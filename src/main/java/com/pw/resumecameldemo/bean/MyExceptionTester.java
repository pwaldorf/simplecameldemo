package com.pw.resumecameldemo.bean;

public class MyExceptionTester {
    
    public void process() { //} throws RuntimeException {
               
        throw new MyException("This is my exception");
               
    }
}
