package org.example.strategies;

import org.example.objects.InputText;

import static org.example.DataPreprocessing.inputTextMap;

public class InputTextStrategy implements Strategy{
    @Override
    public String exprEncode(String expr) {
        for (String key : inputTextMap.keySet()) {
            expr = expr.replaceAll(inputTextMap.get(key).toString(), key);
        }
        return expr;
    }

    @Override
    public void exprToMap(String expr) {
        String[] component = expr.split(" {3}");
        InputText it = new InputText(component[1], component[2]);
        if (!inputTextMap.containsValue(it)) {
            inputTextMap.put("it" + (inputTextMap.keySet().size() + 1), it);
        }
    }

    @Override
    public String searchValidValue(String expr) {
        return inputTextMap.get(expr).getValue();
    }
}
