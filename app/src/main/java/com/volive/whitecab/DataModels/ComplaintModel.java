package com.volive.whitecab.DataModels;

public class ComplaintModel {

    String id,complaint_title;

    public ComplaintModel(String id, String complaint_title) {
        this.id=id;
        this.complaint_title=complaint_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaint_title() {
        return complaint_title;
    }

    public void setComplaint_title(String complaint_title) {
        this.complaint_title = complaint_title;
    }
}
