package com.rsm.wmsstorage;

public class PeremichDownData {
    private String prodName;
    private double qty;
    private int SC;

    private String ProdEd,Part,Calibr,Ton;

    public PeremichDownData(String prodName, double qty, int SC, String prodEd, String part, String calibr, String ton) {
        this.prodName = prodName;
        this.qty = qty;
        this.SC = SC;
        ProdEd = prodEd;
        Part = part;
        Calibr = calibr;
        Ton = ton;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public int getSC() {
        return SC;
    }

    public void setSC(int SC) {
        this.SC = SC;
    }

    public String getProdEd() {
        return ProdEd;
    }

    public void setProdEd(String prodEd) {
        ProdEd = prodEd;
    }

    public String getPart() {
        return Part;
    }

    public void setPart(String part) {
        Part = part;
    }

    public String getCalibr() {
        return Calibr;
    }

    public void setCalibr(String calibr) {
        Calibr = calibr;
    }

    public String getTon() {
        return Ton;
    }

    public void setTon(String ton) {
        Ton = ton;
    }
}
