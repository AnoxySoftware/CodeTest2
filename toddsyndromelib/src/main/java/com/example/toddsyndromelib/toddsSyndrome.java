package com.example.toddsyndromelib;

import android.content.Context;

import com.example.toddsyndromelib.Models.Answer;
import com.example.toddsyndromelib.Models.Question;
import com.example.toddsyndromelib.Util.DatabaseHandler;

import java.util.List;

/**
 * Created by Lefteris on 07/02/17.
 */
public class toddsSyndrome {

    private DatabaseHandler db;
    private Context c;

    public toddsSyndrome(Context c) {
        db = new DatabaseHandler(c);
        this.c = c;
    }

    /**
     * Gets the questions available
     * @return A list of all available questions in the Question Model
     */
    public List<Question> getQuestions() {
        List<Question> questionsList = db.getAllQuestions();
        return questionsList;
    }

    /**
     * Gets a question with a given Id
     * @return A Question object for the given id or null if the quesiton is not found
     */
    public Question getQuestionForId(long questionId) {
        return db.getQuestion(questionId);
    }

    /**
     * Gets the answers for a given User Id
     * @param userId The users ID to get the answers for
     * @return A list of all users answers in the Answer Model
     */
    public List<Answer> getAnswersForUserId(long userId) {
        List<Answer> answersList = db.getAnswersForUserId(userId);
        return answersList;
    }

    /**
     * Adds an answer for a user
     * @param userId The User Id of the user
     * @param questionId The id of the question for which we are adding the answer
     * @param answer The answer value
     */
    public void addAnswerForUserId(long userId, long questionId, String answer) {
        Answer ans = new Answer(userId,questionId,answer);
        db.createAnswer(ans);
    }

    /**
     * Gets the user Id for a given email
     * @param email the email
     * @return The user's id or -1 if the user is not found
     */
    public long getUserIdForEmail(String email) {

        long userId = db.getUserIdForEmail(email);
        return userId;
    }

    /**
     * Adds a new user with an email to the DB
     * @param email the users email
     * @return The userId for this user
     */
    public long addUserWithEmail(String email) {

        if (db==null)
            db = new DatabaseHandler(c);

        long userId = db.addUserWithEmail(email);
        return userId;
    }

    /**
     * Deletes a user with a given email and all the user's answers
     * @param email The email to delete
     * @return true if the user was found, false if not found
     */
    public boolean deleteUserWithEmail(String email) {
        long userId = db.getUserIdForEmail(email);
        //check that we found the user before deleting
        if (userId>=0) {
            db.deleteAnswersForUserId(userId);
            db.deleteUserWithEmail(email);
            return true;
        }

        return false;
    }

    /**
     * Calculates the user's score based on his answers
     * @param userId The user id to calculate the users answers for
     * @return The percentage (%) of the probability that this user has the syndrome
     */
    public int getScoreForUserWithId(long userId) {
        //get the users answer's first
        List<Answer> answersList = db.getAnswersForUserId(userId);

        float score = 0;

        //now calculate the score for each of the answers
        for (Answer answer : answersList) {
            //get the question model for this answer
            Question question = db.getQuestion(answer.getQuestionId());
            //if the question is not found (we might have removed it in a next questions version, ignore it)
            if (question==null)
                continue;
            //get if the answer is positive and if yes, increase the score
            if (isPositive(question,answer))
                score++;
        }

        //we need to convert the score to %
        score = (score/answersList.size()) * 100;

        return Math.round(score);
    }

    /**
     * Returns if an answer to a given question is positive for the syndrome
     * @param question The question model
     * @param answer The answer model
     * @return True if it's positive, false if negative
     */
    private boolean isPositive(Question question, Answer answer) {
        //get the positive Value
        String positiveValue = question.getAnswerGroup().getPositiveValue();
        boolean isPositive = false;

        //get the answer string
        String answerString = answer.getAnswer();

        //if we don't have an answer, ignore this
        if (answerString==null || answerString.isEmpty())
            return false;

        //TODO: This should be extended to cover all cases, but for the given test this covers all cases
        //compare it with the given value depending on the compare method
        switch (question.getAnswerGroup().getComparisonType()) {
            case "equal":
                isPositive = positiveValue.equalsIgnoreCase(answerString);
                break;
            case "lessOrEqual":
                long answerValue = Long.valueOf(answerString);
                long questionValue = Long.valueOf(positiveValue);
                isPositive = answerValue <= questionValue;
                break;
        }

        return isPositive;
    }

    /**
     * Closes the Database
     */
    public void closeDatabase() {
        db.closeDatabase();
    }
}
