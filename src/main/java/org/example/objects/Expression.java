package org.example.objects;

public class Expression {
    private String locator;
    private String locatorValue;

    public Expression(String locator) {
        this.locator = locator;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }
}
