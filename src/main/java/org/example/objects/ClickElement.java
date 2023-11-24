package org.example.objects;

public class ClickElement extends Expression{

    public ClickElement(String locator) {
        super(locator);
    }

    public ClickElement(ClickElement clickElement) {
        super(clickElement.getLocator());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ClickElement ce)) {
            return false;
        }

        return (this.getLocator().equals(ce.getLocator()));
    }

    @Override
    public String toString() {
        return "Click Element   " + this.getLocator();
    }
}
