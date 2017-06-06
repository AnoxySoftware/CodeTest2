package com.example.toddsyndromelib.Models;

/**
 * Created by Lefteris on 06/02/17.
 *
 * Model Class for the Answer Table of our Database
 */
public class Answer {

    private long userId;
    private long questionId;
    private String answer;

    //constructors
    public Answer() {

    }

    public Answer(long userId, long questionId, String answer) {
        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
    }

    //setters
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    //getters
    public long getUserId() {
        return userId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }
}
