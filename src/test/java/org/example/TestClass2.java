package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class TestClass2 {
    private static final Logger logger = LogManager.getLogger(TestClass2.class);

    @Test(timeOut = 500, groups = {"group1"})
    public void test1() {
        String expr = "%28a%26b|c%29";
        String res = PythonTruthTableServer.simplifyParse(expr);
        assert (res.equals("200"));
    }

    @Test(timeOut = 500, groups = {"group1"})
    public void test2() {
        String expr = "%28a%26b|c%29";
        String res = PythonTruthTableServer.simplifyParse(expr);
        assert (res.equals("200"));
    }

    @Test(timeOut = 500, groups = {"group1"})
    public void test3() {
        String expr = "%28a%26b|c%29";
        String res = PythonTruthTableServer.simplifyParse(expr);
        assert (res.equals("200"));
    }
}
