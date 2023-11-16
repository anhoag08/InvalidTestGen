package org.example;

import org.testng.annotations.Test;

public class TimeoutTest {
    @Test(timeOut = 800)
    public void test1() throws InterruptedException {
        Thread.sleep(100);
    }
    @Test(timeOut = 800)
    public void test2() throws InterruptedException {
        Thread.sleep(1000);
    }
}
