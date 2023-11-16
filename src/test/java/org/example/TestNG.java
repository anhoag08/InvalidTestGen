package org.example;

import org.testng.Assert;
import org.testng.annotations.*;
import org.example.ReadXmlDomParserLoop.*;
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


public class TestNG {
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Before Suite");
    }

    @Test
    public void test1() {
        ReadXmlDomParserLoop.lineDict = new Hashtable<>();
        ReadXmlDomParserLoop.lineDict.put("LINE1", new Vector<>());
        ReadXmlDomParserLoop.lineDict.put("LINE2", new Vector<>());
        ReadXmlDomParserLoop.lineDict.put("LINE3", new Vector<>());
        ReadXmlDomParserLoop.initInvalidDict();
        assert (ReadXmlDomParserLoop.lineDict != null);
    }

    @Test
    public void test2a() {
        Map<String, List<String>> testMap = ReadXmlDomParserLoop.createDataMap("src/main/resources/data_thinktester.csv");
        assert (testMap != null);
    }

    @Test
    public void test2b() {
        Map<String, List<String>> testMap = ReadXmlDomParserLoop.createDataMap("");
        assert (testMap.isEmpty());
    }

    @Test
    public void test3() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream("src/main/resources/outline_demoqa.xml")) {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            // read from a project's resources folder
            Document doc = db.parse(is);

            if (doc.hasChildNodes()) {
                ReadXmlDomParserLoop.parseTestSuite(doc.getChildNodes());
                assert (ReadXmlDomParserLoop.temp != null);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test4() {
        Vector<Vector<String>> test = new Vector<>();
        Vector<String> test1 = new Vector<>();
        test1.add("it1");
        test1.add("it2");
        test1.add("it3");
        test1.add("ce4");
        test1.add("ce5");
        test.add(test1);
        ReadXmlDomParserLoop.templateGen(test, 3);
        assert (ReadXmlDomParserLoop.temp != null);
    }

    @Test
    public void test3b() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream("src/main/resources/outline_demoqa1.xml")) {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            // read from a project's resources folder
            Document doc = db.parse(is);

            if (doc.hasChildNodes()) {
                ReadXmlDomParserLoop.parseTestSuite(doc.getChildNodes());
                assert (ReadXmlDomParserLoop.temp != null);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}