package fi.tuni.tamk.tiko.objectorientedprogramming;

public class Item {
    private String label;
    private String amount;

    public Item(String label, String amount) {
        this.label = label;
        this.amount = amount;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Item{" +
                "label='" + label + '\'' +
                ", amount=" + amount +
                '}';
    }
}
