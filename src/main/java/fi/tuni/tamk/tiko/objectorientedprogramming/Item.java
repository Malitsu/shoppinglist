package fi.tuni.tamk.tiko.objectorientedprogramming;

public class Item {
    String label;
    int amount;

    public Item(String label, int amount) {
        this.label = label;
        this.amount = amount;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public int getAmount() {
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
