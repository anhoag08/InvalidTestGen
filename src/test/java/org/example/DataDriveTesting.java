package org.example;

import org.example.objects.InputText;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class DataDriveTesting {
    @Parameters({"param1", "param2"})
    @Test
    public void testWithParameters(String param1, int param2) {
        System.out.println("Param1: " + param1 + " - Param2: " + param2);
    }


    @DataProvider(name = "testData")
    public Object[][] dataProvider() {
        return new Object[][]{
                {"test1", 2, new InputText("a", "A")},
                {"test2", 3, new InputText("b", "B")},
                {"test3", 4, new InputText("c", "C")}
        };
    }
    @Test(dataProvider = "testData")
    public void testWithData(String param1, int param2, InputText param3) {
        System.out.println("Param1 " + param1 + ", Param2: " + param2 + ", Param3: " + param3.toString());
    }
}