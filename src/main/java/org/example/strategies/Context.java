package org.example.strategies;

import java.util.Vector;

public class Context {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public String exprEncode(String expr) {
        this.setStrategy(new InputTextStrategy());
        expr = strategy.exprEncode(expr);
        this.setStrategy(new ClickElementStrategy());
        expr = strategy.exprEncode(expr);
        return expr;
    }

    public void exprToMap(String expr) {
        if (expr.contains("Input Text")) {
            this.setStrategy(new InputTextStrategy());
        } else if (expr.contains("Click Element")) {
            this.setStrategy(new ClickElementStrategy());
        }
        strategy.exprToMap(expr);
    }

    public String searchValidValue(String expr) {
        if (expr.contains("it")) {
            this.setStrategy(new InputTextStrategy());
        } else if (expr.contains("ce")) {
            this.setStrategy(new ClickElementStrategy());
        }
        return strategy.searchValidValue(expr);
    }

    public Vector<String> getMultiValid (Vector<String> header) {
        Vector<String> headerKey = new Vector<>();
        for (String expr : header) {
            if (expr.contains("it")) {
                this.strategy = new InputTextStrategy();
            } else if (expr.contains("ce")) {
                this.strategy = new ClickElementStrategy();
            }
            if (!headerKey.contains(strategy.searchValidValue(expr))) {
                headerKey.add(strategy.searchValidValue(expr));
            }
        }
        return headerKey;
    }
}
