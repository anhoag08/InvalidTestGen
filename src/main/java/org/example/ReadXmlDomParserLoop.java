package org.example;

import com.opencsv.CSVReader;
import org.example.objects.ClickElement;
import org.example.objects.InputText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.JavaHttpClient.logicParse;

public class ReadXmlDomParserLoop {
    static Dictionary<String, Vector<Vector<String>>> lineDict = new Hashtable<>();
    static Vector<Vector<String>> invalidDict = new Vector<>();
    public static Vector<String> temp = new Vector<>();

    static HashMap<String, InputText> inputTextMap = new HashMap<>();
    static HashMap<String, ClickElement> clickElementMap = new HashMap<>();
    static Map<String, List<String>> valueMap = new HashMap<>();

    public static void main(String[] args) {
        valueMap = createDataMap("src/main/resources/data_thinktester.csv");
        System.out.println(valueMap);

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream is = new FileInputStream("src/main/resources/outline_demoqa.xml")) {

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
                invalidTestCaseGen();
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
                if (tempNode.getNodeName().equals("Scenario")) {
                    temp.add(0, "Test-" + tempNode.getTextContent());
                } else if (tempNode.getNodeName().equals("LogicExpressionOfActions")) {
                    exprToMap(logicExpr(tempNode.getChildNodes(), false));
                    Vector<Vector<String>> tb = truthTableParse(logicParse(exprEncode(logicExpr(tempNode.getChildNodes(), false))));
                    lineDict.put("LINE" + count, tb);
                    templateGen(tb, count);
                } else if (tempNode.getNodeName().equals("Validation")) {
                    parseValidation(tempNode.getChildNodes());
                }
            }
        }
//        System.out.println(lineDict);
    }

    public static void templateGen(Vector<Vector<String>> truthTable, int count) {
        for (String expr : truthTable.get(0)) {
            if (expr.contains("it")) {
                temp.add("#LINE" + count + "   " + expr);
            } else if (expr.contains("ce")) {
                temp.add("#LINE" + count + "   " + expr);
            }
        }
    }

    public static String exprEncode(String expr) {
        String encodedExpr = expr;
        for (String key : inputTextMap.keySet()) {
            encodedExpr = encodedExpr.replaceAll(inputTextMap.get(key).toString(), key);
        }

        for (String key : clickElementMap.keySet()) {
            encodedExpr = encodedExpr.replaceAll(clickElementMap.get(key).toString(), key);
        }
        return encodedExpr;
    }

    public static void exprToMap(String expr) {
        Vector<String> value = arrToVec(expr.split("\\||%26|%28|%29"));
        String[] removed = {" ", ""};
        value.replaceAll(String::trim);
        value.removeAll(List.of(removed));
        System.out.println(value);

        for (int i = 0; i < value.size(); i++) {
            if (value.get(i).contains("Input Text")) {
                boolean isDup = false;
                String[] component = value.get(i).split(" {3}");
                InputText it = new InputText(component[1], component[2]);
                for (String key : inputTextMap.keySet()) {
                    if (inputTextMap.get(key).equals(it)) {
                        isDup = true;
                        break;
                    }
                }
                if (!isDup) {
                    inputTextMap.put("it" + (inputTextMap.keySet().size() + 1), it);
                }
            } else if (value.get(i).contains("Click Element")) {
                boolean isDup = false;
                String[] component = value.get(i).split(" {3}");
                ClickElement ce = new ClickElement(component[1]);
                for (String key : clickElementMap.keySet()) {
                    if (clickElementMap.get(key).equals(ce)) {
                        isDup = true;
                        break;
                    }
                }
                if (!isDup) {
                    clickElementMap.put("ce" + (clickElementMap.keySet().size() + 1), ce);
                }
            }
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

    public static String logicExpr(NodeList nodeList, boolean isChild) {
        String type = null;
        String temp = "";
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals("type")) {
                    if (tempNode.getTextContent().equals("and")) {
                        type = "and";
                    } else if (tempNode.getTextContent().equals("or")) {
                        type = "or";
                    } else if (tempNode.getTextContent().equals("Input Text")) {
                        type = "Input Text";
                    } else if (tempNode.getTextContent().equals("Click Element")) {
                        type = "Click Element";
                    }
                } else if (tempNode.getNodeName().equals("LogicExpressionOfActions")) {
                    String expr = logicExpr(tempNode.getChildNodes(), true);
                    if (count == nodeList.getLength() - 2) {
                        temp += expr;
                    } else if (type.equals("and")) {
                        temp += expr + "%26";
                    } else if (type.equals("or")) {
                        temp += expr + "|";
                    }
                } else if (tempNode.getNodeName().equals("locator")) {
                    if (type.equals("Input Text")) {
                        temp += "Input Text   " + tempNode.getTextContent();
                    } else if (type.equals("Click Element")) {
                        return "Click Element   " + tempNode.getTextContent();
                    }
                } else if (tempNode.getNodeName().equals("text")) {
                    return temp + "   " + tempNode.getTextContent();
                }
            }
        }
        if (isChild && (type.equals("and") || type.equals("or"))) {
            return "%28" + temp + "%29";
        } else {
            return temp;
        }
    }

    public static Vector<Vector<String>> truthTableParse(String expr) {
        String headerString = null;
        expr = expr.replaceAll("\\s+", " ");
        Pattern bodyPattern = Pattern.compile(" [0|1]");
        Matcher bodyMatcher = bodyPattern.matcher(expr);
        if (bodyMatcher.find()) {
            headerString = expr.substring(0, bodyMatcher.start());
            expr = "0 " + expr.substring(bodyMatcher.start() + 1);
        }
        Vector<Vector<String>> tb = new Vector<>();
        Vector<String> vector = arrToVec(expr.split(" : "));
        Vector<String> validVector = new Vector<>();
        Vector<String> invalidVector = new Vector<>();
        Vector<String> header = new Vector<>();
        for (int i = 0; i < vector.size() - 1; i++) {
            vector.set(i, vector.get(i).substring(2) + " " + vector.get(i + 1).charAt(0));
        }
        vector.remove(vector.size() - 1);

        Pattern headerPattern = Pattern.compile("[a-zA-Z]+[1-9]");
        assert headerString != null;
        Matcher headerMatcher = headerPattern.matcher(headerString);
        while (headerMatcher.find()) {
            header.add(headerMatcher.group());
        }
        for (String tbLine : vector) {
            if (tbLine.charAt(tbLine.length() - 1) == '0') {
                invalidVector.add(tbLine);
            } else {
                validVector.add(tbLine);
            }
        }
        tb.add(header);
        tb.add(validVector);
        tb.add(invalidVector);
        return tb;
    }

    public static Vector<String> arrToVec(String[] arr) {
        return new Vector<>(Arrays.asList(arr));
    }

    public static void initInvalidDict() {
        String expr = "";
        Enumeration<String> enumeration = lineDict.keys();
        while (enumeration.hasMoreElements()) {
            expr += enumeration.nextElement() + "%26";
        }
        expr = expr.substring(0, expr.length() - 3);
        invalidDict = truthTableParse(logicParse(expr));
//        System.out.println(invalidDict);
    }

    public static Map<String, List<String>> createDataMap(String path) {
        Map<String, List<String>> variables = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(path))) {
            String[] words = null;
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

    public static void invalidTestCaseGen() {
        Vector<String> finalTest = new Vector<>();
        for (String lineString : invalidDict.get(2)) {
            Vector<String> lineVec = arrToVec(lineString.split(" "));
            Vector<String> lineTemp = new Vector<>(temp);
            for (int i = 0; i < invalidDict.get(0).size(); i++) {
                lineTemp = invalidLineParse(invalidDict.get(0).get(i), lineVec.get(i), lineTemp);
            }
            finalTest.addAll(lineTemp);
        }
        int count = 1;
        for (int i = 0; i < finalTest.size(); i++) {
            if (finalTest.get(i).contains("Test-")) {
                finalTest.set(i, finalTest.get(i) + "-" + count);
                count++;
            }
        }
        System.out.println(finalTest);
    }

    public static Vector<String> invalidLineParse(String line, String value, Vector<String> lineTemp) {
        Vector<String> tempTemplate = new Vector<>();
        if (value.equals("0")) {
            for (String valueLine : lineDict.get(line).get(2)) {
                Vector<String> exprTemp = new Vector<>(lineTemp);
                String header = getHeader(lineDict.get(line).get(0), valueLine);
                Vector<String> valueVector = arrToVec(valueLine.split(" "));
                for (int i = 0; i < lineDict.get(line).get(0).size(); i++) {
                    invalidExprParse(line, lineDict.get(line).get(0).get(i), valueVector.get(i), header, exprTemp);
                }
                tempTemplate.addAll(exprTemp);
            }
        } else {
            for (String valueLine : lineDict.get(line).get(1)) {
                Vector<String> exprTemp = new Vector<>(lineTemp);
                String header = getHeader(lineDict.get(line).get(0), valueLine);
                Vector<String> valueVector = arrToVec(valueLine.split(" "));
                for (int i = 0; i < lineDict.get(line).get(0).size(); i++) {
                    invalidExprParse(line, lineDict.get(line).get(0).get(i), valueVector.get(i), header, exprTemp);
                }
                tempTemplate.addAll(exprTemp);
            }
        }
        return tempTemplate;
    }

    public static void invalidExprParse(String line, String expr, String value, String header, Vector<String> exprTemp) {
        Vector<String> tempTemplate = new Vector<>();
        for (int i = 0; i < exprTemp.size(); i++) {
            if (exprTemp.get(i).contains(line) && exprTemp.get(i).contains(expr)) {
                Vector<String> tempExprTemplate = new Vector<>(exprTemp);
                if (value.equals("1")) {
                    if (expr.contains("it")) {
                        if(header.isEmpty()) {
                            InputText singleValidIt = new InputText(inputTextMap.get(expr));
                            singleValidIt.setLocator(valueMap.get(singleValidIt.getLocator()).get(0));
                            for(String exprValue : valueMap.get(inputTextMap.get(expr).getValue())) {
                                singleValidIt.setValue(exprValue);
                                tempExprTemplate.set(i, singleValidIt.toString());
                                tempTemplate.addAll(tempExprTemplate);
                            }
                        } else {
                            System.out.println(header);
                        }
                    } else if (expr.contains("ce")) {
                        if(header.isEmpty()) {
                            ClickElement singleValidCe = new ClickElement(clickElementMap.get(expr));
                        } else {
                        }
                    }
                } else {
                    if (expr.contains("it")) {
                        InputText invalidIt = new InputText(inputTextMap.get(expr));
                        invalidIt.setValue("${EMPTY}");
                        exprTemp.set(i, invalidIt.toString());
                    } else if (expr.contains("ce")) {
                        ClickElement invalidCe = new ClickElement(clickElementMap.get(expr));
                        invalidCe.setLocator("${EMPTY}");
                        exprTemp.set(i, invalidCe.toString());
                    }
                }
            }
        }
        System.out.println(tempTemplate);
    }

    public static String getHeader(Vector<String> header, String value) {
        String logicHeader = "";
        Vector<String> valueVec = arrToVec(value.split(" "));
        for (int i = 0; i < header.size(); i++) {
            if (valueVec.get(i).equals("1")) {
                logicHeader += header.get(i) + ", ";
            }
        }
        if (logicHeader.trim().contains(", ")) {
            logicHeader = '[' + logicHeader.substring(0, logicHeader.length() - 2) + ']';
        } else {
            logicHeader = "";
        }
        return logicHeader;
    }
}