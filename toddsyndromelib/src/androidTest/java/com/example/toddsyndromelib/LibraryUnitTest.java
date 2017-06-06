package com.example.toddsyndromelib;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.toddsyndromelib.Models.Answer;
import com.example.toddsyndromelib.Models.AnswerGroupValues;
import com.example.toddsyndromelib.Models.Question;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LibraryUnitTest {

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    private toddsSyndrome libSyndrome;

    //our test email address
    private String unitTestEmail = "unitTestRandom@unitTest.com";

    @Before
    public void setUp() {
        //init our library
        libSyndrome = new toddsSyndrome(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
        libSyndrome.closeDatabase();
    }

    @Test
    public void testAddUser() {
        //testing that we can add a user
        long userId = libSyndrome.addUserWithEmail(unitTestEmail);
        //we shouldn't be getting a -1 here if the user was created
        assertNotEquals(userId,-1);
        //verify that the user correctly exists
        long verifyUserId = libSyndrome.getUserIdForEmail(unitTestEmail);
        assertEquals(userId,verifyUserId);
    }

    @Test
    public void testDeleteUser() {
        //add the user first (if already existing, it will be ignored)
        libSyndrome.addUserWithEmail(unitTestEmail);
        //delete the user
        boolean userFound = libSyndrome.deleteUserWithEmail(unitTestEmail);

        //we should have found the user
        assertEquals(userFound,true);

        //verify the user has been deleted
        long userId = libSyndrome.getUserIdForEmail(unitTestEmail);
        assertEquals(userId,-1);
    }

    @Test
    public void testDeletingNonExistingUser() {
        //add the user first (if already existing, it will be ignored)
        libSyndrome.addUserWithEmail(unitTestEmail);

        //delete the user
        libSyndrome.deleteUserWithEmail(unitTestEmail);

        //try do delete the user again
        boolean userFound = libSyndrome.deleteUserWithEmail(unitTestEmail);
        //we should have a false here as the user shoudn't exist
        assertEquals(userFound,false);
    }

    @Test
    public void getQuestions() {
        List<Question> questions = libSyndrome.getQuestions();
        //we should have at least 1 here
        assertNotEquals(questions.size(),0);
    }

    @Test
    public void getSingleQuestion() {
        List<Question> questions = libSyndrome.getQuestions();
        //now try to get each question from the db separately and verify their id's match
        for (Question question : questions) {
            Question verifyQuestion = libSyndrome.getQuestionForId(question.getId());
            assertEquals(question.getId(), verifyQuestion.getId());
        }
    }

    @Test
    public void addAllPositiveAnswers() {
        //add the user first (if users exists, we should get his userId);
        long userId = libSyndrome.addUserWithEmail(unitTestEmail);

        //get our questions
        List<Question> questions = libSyndrome.getQuestions();
        //now try to add all positive values
        for (Question question : questions) {
            libSyndrome.addAnswerForUserId(userId,question.getId(),question.getAnswerGroup().getPositiveValue());
        }

        //now verify that we have added all our answers
        List<Answer> answers = libSyndrome.getAnswersForUserId(userId);

        //our answers must match our question
        assertEquals(questions.size(),answers.size());

        //verify that we have also the correct score
        int score = libSyndrome.getScoreForUserWithId(userId);
        //we should have gotten 100!
        assertEquals(score,100);
    }

    @Test
    public void checkDropDownAnswers() {
        //add the user first (if users exists, we should get his userId);
        long userId = libSyndrome.addUserWithEmail(unitTestEmail);

        //get our questions
        List<Question> questions = libSyndrome.getQuestions();
        //now check that all dropdown questions have at least 2 options
        for (Question question : questions) {

            if (question.getType().equalsIgnoreCase("dropdown")) {
                //add the non positive value...
                List<AnswerGroupValues> possibleAnswers = question.getAnswerGroup().getAnswers();

                //our list should have at least 2 items
                assertThat(possibleAnswers.size(), greaterThan(1));

            }
        }
    }

    @Test
    public void addAllNegativeAnswersForDropDowns() {

        //try do delete the user first
        libSyndrome.deleteUserWithEmail(unitTestEmail);

        //add the user
        long userId = libSyndrome.addUserWithEmail(unitTestEmail);

        //get our questions
        List<Question> questions = libSyndrome.getQuestions();

        int negativeValues = 0;

        //now add for all dropDownQuestions negative values, for the rest positive
        for (Question question : questions) {
            //this is a dropdown field
            if (question.getType().equalsIgnoreCase("dropdown")) {
                negativeValues++;
                //add the non positive value...
                List<AnswerGroupValues> possibleAnswers = question.getAnswerGroup().getAnswers();

                //check if the first possible answer is the positive value and if yes, add the
                //second option, else add this
                if (!possibleAnswers.get(0).equals(question.getAnswerGroup().getPositiveValue())) {
                    libSyndrome.addAnswerForUserId(userId,question.getId(),possibleAnswers.get(0).toString());
                }
                else {
                    libSyndrome.addAnswerForUserId(userId,question.getId(),possibleAnswers.get(1).toString());
                }
            }
            else {
                libSyndrome.addAnswerForUserId(userId,question.getId(),question.getAnswerGroup().getPositiveValue());
            }
        }

        //verify that we have also the correct score
        int score = libSyndrome.getScoreForUserWithId(userId);

        //our score should match the calculated value for the non drop down fields..
        int calculatedScore = Math.round((float)(questions.size() - negativeValues)/(float) questions.size()*100);

        //check that our scores match
        assertEquals(score,calculatedScore);
    }


}
