package org.example;

import com.opencsv.CSVReader;
import org.example.objects.ClickElement;
import org.example.objects.InputText;
import org.example.strategies.Context;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.example.InvalidTestGen.invalidTestCaseGen;
import static org.example.PythonTruthTableServer.logicParse;
import static org.example.FileWriteModule.writeStringsToFile;

public class DataPreprocessing {
    static Dictionary<String, Vector<Vector<String>>> lineDict = new Hashtable<>();
    static Vector<Vector<String>> invalidDict = new Vector<>();
    public static Vector<String> temp = new Vector<>();

    public static HashMap<String, InputText> inputTextMap = new HashMap<>();
    public static HashMap<String, ClickElement> clickElementMap = new HashMap<>();
    static Map<String, List<String>> dataMap = new HashMap<>();

    public static void main(String[] args) {
        initInvalidDataParse("src/main/resources/data_thinktester.csv", "src/main/resources/outline_demoqa1.xml", "src/main/resources/final_test.robot");
    }

    public static void initInvalidDataParse(String csvPath, String xmlPath, String robotPath) {
        dataMap = createDataMap(csvPath);
        System.out.println(dataMap);

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream(xmlPath)) {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            // read from a project's resources folder
            Document doc = db.parse(is);

            if (doc.hasChildNodes()) {
                parseTestSuite(doc.getChildNodes());
                System.out.println(temp);
                System.out.println(invalidDict);
                System.out.println(lineDict);
                System.out.println(inputTextMap);
                System.out.println(clickElementMap);
                Vector<String> finalTest = invalidTestCaseGen();
                writeStringsToFile(finalTest, robotPath);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    public static void parseTestSuite(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals("TestSuite")) {
                    parseUrl(tempNode.getChildNodes());
                }
            }
        }
    }

    public static void parseUrl(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals("url")) {
                    temp.add("   Open Browser   " + tempNode.getTextContent() + "   Edge");
                } else if (tempNode.getNodeName().equals("TestCase")) {
                    parseTest(tempNode.getChildNodes());
                    initInvalidDict();
                }
            }
        }
    }

    public static void parseTest(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                switch (tempNode.getNodeName()) {
                    case "Scenario" -> temp.add(0, "Test-" + tempNode.getTextContent());
                    case "LogicExpressionOfActions" -> {
                        exprToMap(logicExpr(tempNode.getChildNodes(), false));
//                        String rs = exprString.replaceAll("\\d", " $0 ").replaceAll("\\s+", " ").trim();
                        Vector<Vector<String>> tb = truthTableParse(logicParse(exprEncode(logicExpr(tempNode.getChildNodes(), false))), exprEncode(logicExpr(tempNode.getChildNodes(), false)));
                        lineDict.put("LINE" + count, tb);
                        templateGen(tb, count);
                    }
                    case "Validation" -> parseValidation(tempNode.getChildNodes());
                }
            }
        }
//        System.out.println(lineDict);
    }

    public static void templateGen(Vector<Vector<String>> truthTable, int count) {
        for (String expr : truthTable.get(0)) {
            temp.add("#LINE" + count + "   " + expr);
        }
    }


    //TODO refactor: Done
    public static String exprEncode(String expr) {
        Context encodeContext = new Context();
        expr = encodeContext.exprEncode(expr);
        return expr;
    }


    //TODO refactor: Done
    public static void exprToMap(String expr) {
        Vector<String> value = arrToVec(expr.split("\\||%26|%28|%29"));
        String[] removed = {" ", ""};
        value.replaceAll(String::trim);
        value.removeAll(List.of(removed));
        Context exprToMapContext = new Context();
        for (String s : value) {
            exprToMapContext.exprToMap(s);
        }
    }

    public static void parseValidation(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals("url")) {
                    temp.add("   Should Go To   " + tempNode.getTextContent());
                }
            }
        }
    }

    //TODO refactor
    public static String logicExpr(NodeList nodeList, boolean isChild) {
        String type = null;
        StringBuilder temp = new StringBuilder();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                switch (tempNode.getNodeName()) {
                    case "type" -> type = switch (tempNode.getTextContent()) {
                        case "and" -> "and";
                        case "or" -> "or";
                        case "Input Text" -> "Input Text";
                        case "Click Element" -> "Click Element";
                        default -> type;
                    };
                    case "LogicExpressionOfActions" -> {
                        String expr = logicExpr(tempNode.getChildNodes(), true);
                        if (count == nodeList.getLength() - 2) {
                            temp.append(expr);
                        } else {
                            assert type != null;
                            if (type.equals("and")) {
                                temp.append(expr).append("%26");
                            } else if (type.equals("or")) {
                                temp.append(expr).append("|");
                            }
                        }
                    }
                    case "locator" -> {
                        assert type != null;
                        if (type.equals("Input Text")) {
                            temp.append("Input Text   ").append(tempNode.getTextContent());
                        } else if (type.equals("Click Element")) {
                            return "Click Element   " + tempNode.getTextContent();
                        }
                    }
                    case "text" -> {
                        return temp + "   " + tempNode.getTextContent();
                    }
                }
            }
        }
        if (isChild && (Objects.equals(type, "and") || Objects.equals(type, "or"))) {
            return "%28" + temp + "%29";
        } else {
            return temp.toString();
        }
    }

    public static Vector<Vector<String>> truthTableParse(String tbString, String encodeExpr) {
        if (!tbString.contains(" : ")) {
            tbString += " 1 : 1 0 : 0";
        }
        Vector<Vector<String>> tb = new Vector<>();
        tb.add(arrToVec(encodeExpr.split("\\||%26|%28|%29")));
        for (String header : tb.get(0)) {
            tbString = tbString.replaceAll(header, "");
        }
        tbString = "0 " + tbString.replaceAll("\\d", " $0 ").replaceAll("\\s+", " ").trim();
        Vector<String> vector = arrToVec(tbString.split(" : "));
        Vector<String> validVector = new Vector<>();
        Vector<String> invalidVector = new Vector<>();
        for (int i = 0; i < vector.size() - 1; i++) {
            vector.set(i, vector.get(i).substring(2) + " " + vector.get(i + 1).charAt(0));
        }
        vector.remove(vector.size() - 1);
        for (String tbLine : vector) {
            if (tbLine.charAt(tbLine.length() - 1) == '0') {
                invalidVector.add(tbLine);
            } else {
                validVector.add(tbLine);
            }
        }
        tb.add(validVector);
        tb.add(invalidVector);
        return tb;
    }

    public static Vector<String> arrToVec(String[] arr) {
        return new Vector<>(Arrays.asList(arr));
    }

    public static void initInvalidDict() {
        StringBuilder expr = new StringBuilder();
        Enumeration<String> enumeration = lineDict.keys();
        while (enumeration.hasMoreElements()) {
            expr.append(enumeration.nextElement()).append("%26");
        }
        expr = new StringBuilder(expr.substring(0, expr.length() - 3));
        invalidDict = truthTableParse(logicParse(expr.toString()), expr.toString());
    }

    public static Map<String, List<String>> createDataMap(String path) {
        Map<String, List<String>> variables = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(path))) {
            String[] words;
            while ((words = csvReader.readNext()) != null) {
                variables.put(words[0], new ArrayList<>());
                for (int i = 1; i < words.length; i++) {
                    variables.get(words[0]).add(words[i]);
                }
            }
            return variables;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variables;
    }
}