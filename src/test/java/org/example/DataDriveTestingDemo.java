package org.example;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class DataDriveTestingDemo {
    @Parameters({"name", "weight"})
    @Test
    public void testWithParameters(String name, int weight) {
        System.out.println("Name: " + name + ", weight: " + weight);
    }


    @DataProvider(name = "testData")
    public Object[][] dataProvider() {
        return new Object[][]{
                {"Meo", 2},
                {"Cho", 3},
                {"Heo", 4}
        };
    }
    @Test(dataProvider = "testData")
    public void testWithData(String name, int weight) {
        System.out.println("Name: " + name + ", weight: " + weight);
    }
}