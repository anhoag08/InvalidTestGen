package org.example;

import com.opencsv.CSVReader;
import org.example.objects.ClickElement;
import org.example.objects.Expression;
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

    public static void main(String[] args) {
        System.out.println(createDataMap("src/main/resources/data_thinktester.csv"));

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
                    System.out.println(logicExpr(tempNode.getChildNodes(), false));
                    System.out.println(exprEncode(logicExpr(tempNode.getChildNodes(), false)));
                    Vector<Vector<String>> tb = truthTableParse(logicParse(exprEncode(logicExpr(tempNode.getChildNodes(), false))));
                    lineDict.put("LINE" + count, tb);
                    templateGen(tb, count);
                } else if (tempNode.getNodeName().equals("Validation")) {
                    parseValidation(tempNode.getChildNodes());
                }
            }
        }
        System.out.println(lineDict);
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
                    inputTextMap.put("it" + (i + 1), it);
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
                    clickElementMap.put("ce" + (i + 1), ce);
                }
            }
        }
        for (Map.Entry<String, InputText> entry : inputTextMap.entrySet()) {
            System.out.println("key " + entry.getKey() + " " + entry.getValue().getLocator() + ' ' + entry.getValue().getValue());
        }
        for (Map.Entry<String, ClickElement> entry : clickElementMap.entrySet()) {
            System.out.println("key " + entry.getKey() + " " + entry.getValue().getLocator());
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
        tb.add(header);
        tb.add(vector);
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
        System.out.println(invalidDict);
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
}