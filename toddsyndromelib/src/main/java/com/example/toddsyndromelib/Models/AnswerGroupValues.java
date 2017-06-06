package com.example.toddsyndromelib.Models;

/**
 * Created by Lefteris on 06/02/17.
 *
 * Model Class for the AnswerGroupValues Table of our Database
 */
public class AnswerGroupValues {

    long groupId;
    String value;


    //constructors
    public AnswerGroupValues() {

    }

    public AnswerGroupValues(long groupId, String value) {
        this.groupId = groupId;
        this.value = value;
    }

    //getters
    public long getGroupId() {
        return groupId;
    }

    public String getValue() {
        return value;
    }

    //setters
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
