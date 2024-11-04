package com.rsm.wmsstorage;
public class PrihodHead {
    private int DocNumber;
    private String DocName;
    private String DocTime;
    private int DocStatus;
    private String DocUser;

    public PrihodHead(int docNumber, String docName, String docTime, int docStatus, String docUser) {
        DocNumber = docNumber;
        DocName = docName;
        DocTime = docTime;
        DocStatus = docStatus;
        DocUser = docUser;
    }

    public int getDocNumber() {
        return DocNumber;
    }

    public void setDocNumber(int docNumber) {
        DocNumber = docNumber;
    }

    public String getDocName() {
        return DocName;
    }

    public void setDocName(String docName) {
        DocName = docName;
    }

    public String getDocTime() {
        return DocTime;
    }

    public void setDocTime(String docTime) {
        DocTime = docTime;
    }

    public int getDocStatus() {
        return DocStatus;
    }

    public void setDocStatus(int docStatus) {
        DocStatus = docStatus;
    }

    public String getDocUser() {
        return DocUser;
    }

    public void setDocUser(String docUser) {
        DocUser = docUser;
    }
}
