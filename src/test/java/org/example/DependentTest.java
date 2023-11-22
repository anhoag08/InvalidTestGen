package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class DependentTest {
    private static final Logger logger = LogManager.getLogger(DependentTest.class);
    @Test(dependsOnMethods = {"test3", "test2"})
    public void test1() {
        logger.info("Test method 1 depends on method 2 and 3");
    }

    @Test
    public void test2() {
        logger.info("Test method 2");
    }

    @Test
    public void test3() {
        logger.info("Test method 3");
    }

    @Test(dependsOnMethods = {"test5", "test6"})
    public void test4() {
        logger.info("Test method 1");
    }

    @Test
    public void test5() {
        logger.info("Test method 2");
    }

    @Test
    public void test6() {
        assert (1 == 2);
        logger.info("Test method 3");
    }
}
