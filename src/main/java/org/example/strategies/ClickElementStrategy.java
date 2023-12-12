package org.example.strategies;

import org.example.objects.ClickElement;

import static org.example.DataPreprocessing.clickElementMap;

public class ClickElementStrategy implements Strategy{
    @Override
    public String exprEncode(String expr) {
        for (String key : clickElementMap.keySet()) {
            expr = expr.replaceAll(clickElementMap.get(key).toString(), key);
        }

        return expr;
    }

    @Override
    public void exprToMap(String expr) {
        String[] component = expr.split(" {3}");
        ClickElement ce = new ClickElement(component[1]);
        if (!clickElementMap.containsValue(ce)) {
            clickElementMap.put("ce" + (clickElementMap.keySet().size() + 1), ce);
        }

    }

    @Override
    public String searchValidValue(String expr) {
        return clickElementMap.get(expr).getLocator();
    }
}
