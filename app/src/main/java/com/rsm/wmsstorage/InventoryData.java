package com.rsm.wmsstorage;

public class InventoryData {
    private String prodName;
    private double qty;
    private int SC;
    private double initialQty;

    public InventoryData(String prodName, double qty, int SC) {
        this.prodName = prodName;
        this.qty = qty;
        this.SC = SC;
        this.initialQty = qty;
    }
    // Додайте getter і setter для поля initialQty
    public double getInitialQty() {
        return initialQty;
    }

    public void setInitialQty(double initialQty) {
        this.initialQty = initialQty;
    }
    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getProdName() {
        return prodName;
    }

    public double getQty() {
        return qty;
    }

    public int getSC() {
        return SC;
    }

}
