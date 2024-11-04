package com.rsm.wmsstorage;

public class VudachaHeadData {
    private int DocNumber,DocStatus;
    private String DocName,Client,DocTime,DocUser;

    public VudachaHeadData(int docNumber, int docStatus, String docName, String client, String docTime ) {
        DocNumber = docNumber;
        DocStatus = docStatus;
        DocName = docName;
        Client = client;
        DocTime = docTime;
    }

    public int getDocNumber() {
        return DocNumber;
    }

    public void setDocNumber(int docNumber) {
        DocNumber = docNumber;
    }

    public int getDocStatus() {
        return DocStatus;
    }

    public void setDocStatus(int docStatus) {
        DocStatus = docStatus;
    }

    public String getDocName() {
        return DocName;
    }

    public void setDocName(String docName) {
        DocName = docName;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getDocTime() {
        return DocTime;
    }

    public void setDocTime(String docTime) {
        DocTime = docTime;
    }



    public void setDocUser(String docUser) {
        DocUser = docUser;
    }
}
