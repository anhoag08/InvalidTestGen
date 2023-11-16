package org.example;

import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Test;
import org.apache.logging.log4j.Logger;

public class ParallelExec {
    private static final Logger logger = LogManager.getLogger(ParallelExec.class);
    @Test
    public void test1() throws InterruptedException {
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }
    @Test
    public void test2() throws InterruptedException {
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }
    @Test
    public void test3() throws InterruptedException {
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }
}
