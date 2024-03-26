package com.pw.resumecameldemo.bean;

public class MyExceptionTester {

    public void process() { //} throws RuntimeException {

        throw new MyCustomException("This is my exception");

    }
}
