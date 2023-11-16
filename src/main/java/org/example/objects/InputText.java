package org.example.objects;

public class InputText extends Expression{
    private String value;
    private int size;


    public InputText(String locator, String value) {
        super(locator);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof InputText it)) {
            return false;
        }

        return (this.getLocator().equals(it.getLocator()) && this.getValue().equals(it.getValue()));
    }

    @Override
    public String toString() {
        return "Input Text   " + this.getLocator() + "   " + this.value;
    }
}
