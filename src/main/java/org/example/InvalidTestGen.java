package org.example;

import org.example.objects.ClickElement;
import org.example.objects.InputText;
import org.example.strategies.Context;

import java.util.Collections;
import java.util.Vector;

import static org.example.DataPreprocessing.*;

public class InvalidTestGen {
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

    //TODO refactor
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
                                InputText multiInvalidIt = new InputText(dataMap.get(inputTextMap.get(invalidExpr).getLocator()).get(0), "NOT" + searchValidValue(invalidExpr));
                                multiInvalidTemp.set(i, "   " + multiInvalidIt);
                            } else {
                                ClickElement multiInvalidCe = new ClickElement("NOT" + searchValidValue(invalidExpr));
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
                            InputText singleInvalidIt = new InputText(dataMap.get(inputTextMap.get(expr.get(0)).getLocator()).get(0), "NOT" + searchValidValue(expr.get(0)));
                            singleInvalidTemp.set(i, "   " + singleInvalidIt);
                        } else {
                            ClickElement singleInvalidCe = new ClickElement("NOT" + searchValidValue(expr.get(0)));
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
                                InputText invalidIt = new InputText(dataMap.get(inputTextMap.get(invalidExpr).getLocator()).get(0), "NOT" + searchValidValue(invalidExpr));
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
                        if (clickElementMap.get(ceKey).getLocator().equals(keyVal)) {
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
                        for (int i = 0; i < multiValidTempClone.size(); i++) {
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

    //TODO refactor: DONE
    public static String searchValidValue(String expr) {
        String exprVal;
        String validVal = null;
        Context searchValidValueContext = new Context();
        exprVal = searchValidValueContext.searchValidValue(expr);
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


    //TODO refactor: DONE
    private static String getMultiValidKey(Vector<String> header) {
        String keyVal = null;
        Context getMultiValidKeyContext = new Context();
        Vector<String> headerKey = getMultiValidKeyContext.getMultiValid(header);
        for (String key : dataMap.keySet()) {
            Vector<String> keyVec = arrToVec(key.split(" & "));
            if (sameElement(keyVec, headerKey)) {
                keyVal = key;
                break;
            }
        }
        return keyVal;
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
}
