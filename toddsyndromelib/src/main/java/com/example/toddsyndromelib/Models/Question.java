package com.example.toddsyndromelib.Models;

/**
 * Created by Lefteris on 06/02/17.
 *
 * Model Class for the Questions Table of our Database
 */
public class Question {

    private String question;
    private String type;
    private AnswerGroup answerGroup;
    private long id;

    // constructors
    public Question() {
    }

    public Question(String question, String type, AnswerGroup answerGroup) {
        this.question = question;
        this.type = type;
        this.answerGroup = answerGroup;
    }

    //setters
    public void setType(String type) {
        this.type = type;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswerGroup(AnswerGroup answerGroup) {
        this.answerGroup = answerGroup;
    }

    public void setId(long id) {
        this.id = id;
    }

    //getters
    public String getType() {
        return this.type;
    }

    public String getQuestion() {
        return this.question;
    }

    public AnswerGroup getAnswerGroup() {
        return this.answerGroup;
    }

    public long getId() {
        return id;
    }
}
