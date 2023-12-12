package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class TestClass1 {
    private static final Logger logger = LogManager.getLogger(TestClass1.class);

    @Test(timeOut = 500, groups = {"group1"})
    public void test1() {
        DataPreprocessing.lineDict = new Hashtable<>();
        DataPreprocessing.lineDict.put("LINE1", new Vector<>());
        DataPreprocessing.lineDict.put("LINE2", new Vector<>());
        DataPreprocessing.lineDict.put("LINE3", new Vector<>());
        DataPreprocessing.initInvalidDict();
        assert (DataPreprocessing.lineDict != null);
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }

    @Test(timeOut = 500, groups = {"group2"})
    public void test2a() {
        Map<String, List<String>> testMap = DataPreprocessing.createDataMap("src/main/resources/data_thinktester.csv");
        assert (testMap != null);
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }

    @Test(timeOut = 500, groups = {"group3"})
    public void test3() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream("src/main/resources/outline_demoqa.xml")) {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            // read from a project's resources folder
            Document doc = db.parse(is);

            if (doc.hasChildNodes()) {
                DataPreprocessing.parseTestSuite(doc.getChildNodes());
                assert (DataPreprocessing.temp != null);
                logger.info("Thread ID Is : " + Thread.currentThread().getId());
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }


    @Test(timeOut = 500, groups = {"group4"})
    public void test4() {
        Vector<Vector<String>> test = new Vector<>();
        Vector<String> test1 = new Vector<>();
        test1.add("it1");
        test1.add("it2");
        test1.add("it3");
        test1.add("ce4");
        test1.add("ce5");
        test.add(test1);
        DataPreprocessing.templateGen(test, 3);
        assert (DataPreprocessing.temp != null);
        logger.info("Thread ID Is : " + Thread.currentThread().getId());
    }

    @Test(timeOut = 500, groups = {"group3"})
    public void test3b() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream("src/main/resources/outline_demoqa1.xml")) {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            // read from a project's resources folder
            Document doc = db.parse(is);

            if (doc.hasChildNodes()) {
                DataPreprocessing.parseTestSuite(doc.getChildNodes());
                assert (DataPreprocessing.temp != null);
                logger.info("Thread ID Is : " + Thread.currentThread().getId());
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}