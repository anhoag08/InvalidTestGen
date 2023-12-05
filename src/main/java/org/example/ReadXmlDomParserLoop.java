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
import static org.example.FileWriteExample.writeStringsToFile;

public class ReadXmlDomParserLoop {
    static Dictionary<String, Vector<Vector<String>>> lineDict = new Hashtable<>();
    static Vector<Vector<String>> invalidDict = new Vector<>();
    public static Vector<String> temp = new Vector<>();

    static HashMap<String, InputText> inputTextMap = new HashMap<>();
    static HashMap<String, ClickElement> clickElementMap = new HashMap<>();
    static Map<String, List<String>> dataMap = new HashMap<>();

    public static void main(String[] args) {
        initInvalidDataParse("src/main/resources/data_thinktester.csv", "src/main/resources/outline_demoqa.xml", "src/main/resources/final_test.robot");
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
                        Vector<Vector<String>> tb = truthTableParse(logicParse(exprEncode(logicExpr(tempNode.getChildNodes(), false))));
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
        for (String s : value) {
            if (s.contains("Input Text")) {
                boolean isDup = false;
                String[] component = s.split(" {3}");
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
            } else if (s.contains("Click Element")) {
                boolean isDup = false;
                String[] component = s.split(" {3}");
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

    public static Vector<Vector<String>> truthTableParse(String expr) {
        String headerString = null;
        expr = expr.replaceAll("\\s+", " ");
        if (!expr.contains(" : ")) {
            expr += " 1 : 1 0 : 0";
        }
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
        StringBuilder expr = new StringBuilder();
        Enumeration<String> enumeration = lineDict.keys();
        while (enumeration.hasMoreElements()) {
            expr.append(enumeration.nextElement()).append("%26");
        }
        expr = new StringBuilder(expr.substring(0, expr.length() - 3));
        invalidDict = truthTableParse(logicParse(expr.toString()));
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

    public static Vector<String> invalidTestCaseGen() {
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
        return finalTest;
    }

    public static Vector<String> invalidLineParse(String line, String value, Vector<String> lineTemp) {
        Vector<String> tempTemplate = new Vector<>();
        if (value.equals("0")) {
            for (String valueLine : lineDict.get(line).get(2)) {
                Vector<String> exprTemp = new Vector<>(lineTemp);
                Vector<String> header = getHeader(lineDict.get(line).get(0), valueLine);
                Vector<String> valueVector = arrToVec(valueLine.split(" "));
                if (header.isEmpty()) {
                    for (int i = 0; i < lineDict.get(line).get(0).size(); i++) {
                        Vector<String> expr = new Vector<>();
                        expr.add(lineDict.get(line).get(0).get(i));
                        exprTemp = invalidExprParse(line, expr, valueVector.get(i), header, exprTemp);
                    }
                } else {
                    Vector<String> invalidExpr = new Vector<>(lineDict.get(line).get(0));
                    invalidExpr.removeAll(header);
                    exprTemp = invalidExprParse(line, invalidExpr, "0", header, exprTemp);
                }
                tempTemplate.addAll(exprTemp);
            }
        } else {
            for (String valueLine : lineDict.get(line).get(1)) {
                Vector<String> exprTemp = new Vector<>(lineTemp);
                Vector<String> header = getHeader(lineDict.get(line).get(0), valueLine);
                Vector<String> valueVector = arrToVec(valueLine.split(" "));
                if (header.isEmpty()) {
                    for (int i = 0; i < lineDict.get(line).get(0).size(); i++) {
                        Vector<String> expr = new Vector<>();
                        expr.add(lineDict.get(line).get(0).get(i));
                        exprTemp = invalidExprParse(line, expr, valueVector.get(i), header, exprTemp);
                    }
                } else {
                    Vector<String> invalidExpr = new Vector<>(lineDict.get(line).get(0));
                    invalidExpr.removeAll(header);
                    exprTemp = invalidExprParse(line, invalidExpr, "1", header, exprTemp);
                }
                tempTemplate.addAll(exprTemp);
            }
        }
        return tempTemplate;
    }

    public static Vector<String> invalidExprParse(String line, Vector<String> expr, String value, Vector<String> header, Vector<String> exprTemp) {
        Vector<String> templateLine = new Vector<>();
        if (value.equals("0")) {
            if (!header.isEmpty()) {
                System.out.println("multiInvalid   " + line + "   " + expr + "   " + header + "   " + value);
                Vector<String> multiInvalidTemp = new Vector<>(exprTemp);
                for (int i = 0; i < multiInvalidTemp.size(); i++) {
                    for (String invalidExpr : expr) {
                        if (multiInvalidTemp.get(i).contains(line) && multiInvalidTemp.get(i).contains(invalidExpr)) {
                            if (invalidExpr.contains("it")) {
                                InputText multiInvalidIt = new InputText(dataMap.get(inputTextMap.get(invalidExpr).getLocator()).get(0), "NOT"+searchValidValue(invalidExpr));
                                multiInvalidTemp.set(i, "   " + multiInvalidIt);
                            } else {
                                ClickElement multiInvalidCe = new ClickElement("NOT"+searchValidValue(invalidExpr));
                                multiInvalidTemp.set(i, "   " + multiInvalidCe);
                            }
                        }
                    }
                }
                for (int i = 0; i < multiInvalidTemp.size(); i++) {
                    for (String validExpr : header) {
                        if (multiInvalidTemp.get(i).contains(line) && multiInvalidTemp.get(i).contains(validExpr)) {
                            if (validExpr.contains("it")) {
                                InputText multiInvalidIt = new InputText(dataMap.get(inputTextMap.get(validExpr).getLocator()).get(0), searchValidValue(validExpr));
                                multiInvalidTemp.set(i, "   " + multiInvalidIt);
                            } else {
                                ClickElement multiInvalidCe = new ClickElement(searchValidValue(validExpr));
                                multiInvalidTemp.set(i, "   " + multiInvalidCe);
                            }
                        }
                    }
                }
                templateLine.addAll(multiInvalidTemp);
            } else {
                System.out.println("singleInvalid   " + line + "   " + expr + "   " + header + "   " + value);
                Vector<String> singleInvalidTemp = new Vector<>(exprTemp);
                for (int i = 0; i < singleInvalidTemp.size(); i++) {
                    if (singleInvalidTemp.get(i).contains(line) && singleInvalidTemp.get(i).contains(expr.get(0))) {
                        if (expr.get(0).contains("it")) {
                            InputText singleInvalidIt = new InputText(dataMap.get(inputTextMap.get(expr.get(0)).getLocator()).get(0), "NOT"+searchValidValue(expr.get(0)));
                            singleInvalidTemp.set(i, "   " + singleInvalidIt);
                        } else {
                            ClickElement singleInvalidCe = new ClickElement("NOT"+searchValidValue(expr.get(0)));
                            singleInvalidTemp.set(i, "   " + singleInvalidCe);
                        }
                    }
                }
                templateLine.addAll(singleInvalidTemp);
            }
        } else if (value.equals("1")) {
            if (!header.isEmpty()) {
                System.out.println("multiValid   " + line + "   " + expr + "   " + header + "   " + value);
                Vector<String> multiValidTemp = new Vector<>(exprTemp);
                for (int i = 0; i < multiValidTemp.size(); i++) {
                    for (String invalidExpr : expr) {
                        if (multiValidTemp.get(i).contains(line) && multiValidTemp.get(i).contains(invalidExpr)) {
                            if (invalidExpr.contains("it")) {
                                InputText invalidIt = new InputText(inputTextMap.get(invalidExpr).getLocator(), "NOT"+searchValidValue(invalidExpr));
                                multiValidTemp.set(i, "   " + invalidIt);
                            } else if (invalidExpr.contains("ce")) {
                                ClickElement invalidCe = new ClickElement("NOT" + searchValidValue(invalidExpr));
                                multiValidTemp.set(i, "   " + invalidCe);
                            }
                        }
                    }
                }
                String validKey = getMultiValidKey(header);
                Vector<String> keyVec = arrToVec(validKey.split(" & "));
                Vector<String> keyValVec = new Vector<>();
                Vector<String> headerLocVec = new Vector<>();
                Vector<String> headerValVec = new Vector<>();
                for (String validExpr : header) {
                    if (validExpr.contains("it")) {
                        headerValVec.add(validExpr);
                    } else if (validExpr.contains("ce")) {
                        headerLocVec.add(validExpr);
                    }
                }
                for (String keyVal : keyVec) {
                    boolean isLoc = false;
                    for (String ceKey : clickElementMap.keySet()) {
                        if(clickElementMap.get(ceKey).getLocator().equals(keyVal)) {
                            isLoc = true;
                            break;
                        }
                    }
                    if (!isLoc) {
                        keyValVec.add(keyVal);
                    }
                }
                System.out.println(keyValVec + "  " + headerLocVec + "  " + headerValVec);
                for (int i = 0; i < multiValidTemp.size(); i++) {
                    for (String locExpr : headerLocVec) {
                        if (multiValidTemp.get(i).contains(line) && multiValidTemp.get(i).contains(locExpr)) {
                            ClickElement validCe = new ClickElement(dataMap.get(clickElementMap.get(locExpr).getLocator()).get(0));
                            multiValidTemp.set(i, "   " + validCe);
                        }
                    }
                }
                for (String validVal : dataMap.get(validKey)) {
                    if (!validVal.isEmpty()) {
                        Vector<String> validValVec = arrToVec(validVal.split(" & "));
                        System.out.println(validValVec + "  " + keyValVec + "   " + headerValVec);
                        Vector<String> multiValidTempClone = new Vector<>(multiValidTemp);
                        for (int i=0; i<multiValidTempClone.size(); i++) {
                            for (String valExpr : headerValVec) {
                                if (multiValidTempClone.get(i).contains(line) && multiValidTempClone.get(i).contains(valExpr)) {
                                    InputText validIt = new InputText(dataMap.get(inputTextMap.get(valExpr).getLocator()).get(0), validValVec.get(keyValVec.indexOf(inputTextMap.get(valExpr).getValue())));
                                    multiValidTempClone.set(i, "   " + validIt);
                                }
                            }
                        }
                        templateLine.addAll(multiValidTempClone);
                    }
                }
            } else {
                System.out.println("singleValid   " + line + "   " + expr + "   " + header + "   " + value);
                if (expr.get(0).contains("it")) {
                    for (String data : dataMap.get(inputTextMap.get(expr.get(0)).getValue())) {
                        if (!data.isEmpty()) {
                            InputText singleValidIt = new InputText(dataMap.get(inputTextMap.get(expr.get(0)).getLocator()).get(0), data);
                            Vector<String> singleValidTemp = new Vector<>(exprTemp);
                            for (int i = 0; i < singleValidTemp.size(); i++) {
                                if (singleValidTemp.get(i).contains(line) && singleValidTemp.get(i).contains(expr.get(0))) {
                                    singleValidTemp.set(i, "   " + singleValidIt);
                                }
                            }
                            templateLine.addAll(singleValidTemp);
                        }
                    }
                } else {
                    ClickElement singleValidCe = new ClickElement(dataMap.get(clickElementMap.get(expr.get(0)).getLocator()).get(0));
                    Vector<String> singleValidTemp = new Vector<>(exprTemp);
                    for (int i = 0; i < singleValidTemp.size(); i++) {
                        if (singleValidTemp.get(i).contains(line) && singleValidTemp.get(i).contains(expr.get(0))) {
                            singleValidTemp.set(i, "   " + singleValidCe);
                        }
                    }
                    templateLine.addAll(singleValidTemp);
                }
            }
        }
        System.out.println(templateLine);
        return templateLine;
    }

    public static Vector<String> getHeader(Vector<String> header, String value) {
        Vector<String> valueVec = arrToVec(value.split(" "));
        Vector<String> headerVec = new Vector<>();
        for (int i = 0; i < header.size(); i++) {
            if (valueVec.get(i).equals("1")) {
                headerVec.add(header.get(i));
            }
        }
        if (headerVec.size() < 2) {
            headerVec.clear();
        }
        return headerVec;
    }

    public static boolean sameElement(Vector<String> v1, Vector<String> v2) {
        Vector<String> a1 = new Vector<>(v1);
        Vector<String> a2 = new Vector<>(v2);
        Collections.sort(a1);
        Collections.sort(a2);
        return a1.equals(a2);
    }


    public static String searchValidValue(String expr) {
        String exprVal;
        String validVal = null;
        if (expr.contains("it")) {
            exprVal = inputTextMap.get(expr).getValue();
        } else {
            exprVal = clickElementMap.get(expr).getLocator();
        }
        for (String key : dataMap.keySet()) {
            Vector<String> keyVec = arrToVec(key.split(" & "));
            if (keyVec.contains(exprVal)) {
                Vector<String> valueVec = arrToVec(dataMap.get(key).get(0).split(" & "));
                validVal = valueVec.get(keyVec.indexOf(exprVal));
                break;
            }
        }
        return validVal;
    }

    private static String getMultiValidKey(Vector<String> header) {
        String keyVal = null;
        Vector<String> headerKey = new Vector<>();
        for (String expr : header) {
            if (expr.contains("it")) {
                if (!headerKey.contains(inputTextMap.get(expr).getValue())) {
                    headerKey.add(inputTextMap.get(expr).getValue());
                }
            } else if (expr.contains("ce")) {
                if (!headerKey.contains(clickElementMap.get(expr).getLocator())) {
                    headerKey.add(clickElementMap.get(expr).getLocator());
                }
            }
        }
        for (String key : dataMap.keySet()) {
            Vector<String> keyVec = arrToVec(key.split(" & "));
            if (sameElement(keyVec, headerKey)) {
                keyVal = key;
                break;
            }
        }
        return keyVal;
    }
}