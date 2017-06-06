package com.example.toddsyndromelib.Models;


import java.util.List;

/**
 * Created by Lefteris on 06/02/17.
 *
 * Model Class for the AnswerGroup Table of our Database
 */
public class AnswerGroup {

    long groupValueId;
    String groupName;
    String positiveValue;
    String comparisonType;
    List<AnswerGroupValues>answers;

    //Constructors
    public AnswerGroup() {
    }

    public AnswerGroup(long groupValueId, String groupName, String positiveValue, String comparisonType) {
        this.groupValueId = groupValueId;
        this.groupName = groupName;
        this.positiveValue = positiveValue;
        this.comparisonType = comparisonType;
    }

    public AnswerGroup(int groupValueId, String groupName, String positiveValue, String comparisonType, List<AnswerGroupValues> answers) {
        this.groupValueId = groupValueId;
        this.groupName = groupName;
        this.positiveValue = positiveValue;
        this.comparisonType = comparisonType;
        this.answers = answers;
    }

    //getters
    public long getGroupValueId() {
        return groupValueId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getComparisonType() {
        return comparisonType;
    }

    public String getPositiveValue() {
        return positiveValue;
    }

    //setters
    public void setGroupValueId(long groupValueId) {
        this.groupValueId = groupValueId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setPositiveValue(String positiveValue) {
        this.positiveValue = positiveValue;
    }

    public void setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }

    public List<AnswerGroupValues> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerGroupValues> answers) {
        this.answers = answers;
    }
}
