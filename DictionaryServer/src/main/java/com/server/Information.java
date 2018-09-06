package com.server;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/5 2:56
 */
public class Information {
    private final SimpleStringProperty HostAddress;
    private final SimpleStringProperty Action;
    private final SimpleStringProperty Word;
    private final SimpleStringProperty Response;

    public void setResponse(String response) {
        this.Response.set(response);
    }

    Information(String HostAddress, String Action, String Word, String Response) {
        this.HostAddress = new SimpleStringProperty(HostAddress);
        this.Action = new SimpleStringProperty(Action);
        this.Word = new SimpleStringProperty(Word);
        this.Response = new SimpleStringProperty(Response);
    }

    public String getHostAddress() {
        return HostAddress.get();
    }

    public SimpleStringProperty hostAddressProperty() {
        return HostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.HostAddress.set(hostAddress);
    }

    public String getAction() {
        return Action.get();
    }

    public SimpleStringProperty actionProperty() {
        return Action;
    }

    public void setAction(String action) {
        this.Action.set(action);
    }

    public String getWord() {
        return Word.get();
    }

    public SimpleStringProperty wordProperty() {
        return Word;
    }

    public void setWord(String word) {
        this.Word.set(word);
    }

    public String getResponse() {
        return Response.get();
    }

    public SimpleStringProperty responseProperty() {
        return Response;
    }

}