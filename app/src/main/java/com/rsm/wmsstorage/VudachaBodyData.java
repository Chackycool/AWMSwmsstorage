package com.rsm.wmsstorage;

public class VudachaBodyData {
    private int DocNumber;
    private String ProdName;
    private int SC;
    private String ProdEd;
    private String Part;
    private String Calibr;
    private String Ton;
    private Double Plan;
    private Double Fact;
    private String CellName;
    private String Time;
    private String User;
    private int Status;

    public VudachaBodyData(int docNumber, String prodName, int SC, String prodEd, String part, String calibr, String ton, double plan, double fact, String cellName, String time, String user, int status) {
        DocNumber = docNumber;
        ProdName = prodName;
        this.SC = SC;
        ProdEd = prodEd;
        Part = part;
        Calibr = calibr;
        Ton = ton;
        Plan = plan;
        Fact = fact;
        CellName = cellName;
        Time = time;
        User = user;
        Status = status;
    }

    public int getDocNumber() {
        return DocNumber;
    }

    public void setDocNumber(int docNumber) {
        DocNumber = docNumber;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
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

    public Double getPlan() {
        return Plan;
    }

    public void setPlan(Double plan) {
        Plan = plan;
    }

    public Double getFact() {
        return Fact;
    }

    public void setFact(Double fact) {
        Fact = fact;
    }

    public String getCellName() {
        return CellName;
    }

    public void setCellName(String cellName) {
        CellName = cellName;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

}

