package com.example.toddsyndromelib.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.toddsyndromelib.Models.Answer;
import com.example.toddsyndromelib.Models.AnswerGroup;
import com.example.toddsyndromelib.Models.AnswerGroupValues;
import com.example.toddsyndromelib.Models.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lefteris on 06/02/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    /** Log Tag */
    private String LOG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "toddsSyndrome";

    // Tables Names
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ANSWERGROUPS = "answerGroups";
    private static final String TABLE_ANSWERVALUES = "answerValues";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ANSWERS = "answers";

    //List of all our Tables
    private List<String> tables = new ArrayList<String>() {{
        add(TABLE_QUESTIONS);
        add(TABLE_ANSWERGROUPS);
        add(TABLE_ANSWERVALUES);
        add(TABLE_USERS);
        add(TABLE_ANSWERS);
    }};

    // COMMON Table Columns names
    private static final String KEY_ID = "id";

    // QUESTIONS Table Columns names
    private static final String KEY_QUESTION = "question";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ANSWERGROUPNAME = "answerGroupName";

    // ANSWERGROUPS Table Columns names
    private static final String KEY_VALUEID = "groupValueId";
    private static final String KEY_GROUPNAME = "groupName";
    private static final String KEY_POSITIVEVALUE = "positiveValue";
    private static final String KEY_COMPARISONTYPE = "comparisonType";

    // ANSWERVALUES Table Columns names
    private static final String KEY_GROUPID = "groupId";
    private static final String KEY_VALUE = "value";

    // USERS Table Columns names
    private static final String KEY_EMAIL = "email";

    // ANSWERS Table Columns names
    private static final String KEY_USERID = "userId";
    private static final String KEY_QUESTIONID = "questionId";
    private static final String KEY_ANSWER = "answer";

    //Table creation SQL Statements
    private static final String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_QUESTION + " TEXT," + KEY_TYPE + " TEXT," + KEY_ANSWERGROUPNAME + " TEXT" + ")";

    private static final String CREATE_ANSWERGROUPS_TABLE = "CREATE TABLE " + TABLE_ANSWERGROUPS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_VALUEID + " INTEGER,"
            + KEY_GROUPNAME + " TEXT UNIQUE," + KEY_POSITIVEVALUE + " TEXT," + KEY_COMPARISONTYPE + " TEXT" + ")";

    private static final String CREATE_ANSWERVALUES_TABLE = "CREATE TABLE " + TABLE_ANSWERVALUES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_GROUPID + " INTEGER,"
            + KEY_VALUE + " TEXT" + ")";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_EMAIL + " TEXT" + ")";

    private static final String CREATE_ANSWERS_TABLE = "CREATE TABLE " + TABLE_ANSWERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERID + " INTEGER,"
            + KEY_QUESTIONID + " INTEGER," + KEY_ANSWER + " TEXT" + ")";

    /**
     * The SQL Database has 5 tables. Questions, AnswerGroups, AnswerValues, Users and Answers
     *
     * The idea is that all questions are stored in the Database and thus, can be managed/updated by a REST API
     *
     * At the first run of the App ideally the app would check against an REST endpoint with the Database Version and would get the questions and answers
     * in a JSON format.
     * Also periodically it should check for updates and see if we want to add new questions or modify some of the questions in case data changes in the way the Syndrome is tested against
     *
     * The DB is organized in a way, so that the Users table contains all Users that we have already run the test for. The users are identified by their email address which should be unique
     * The Questions are stored in an normalized way in the DB:
     *
     * Questions contains the question Text, the field type (drop down,numeric, etc) and the group name for the possible answers from the table answerGroups
     *
     * AnswerGroups contains the answers, where groupValueId has the possible answers from the AnswerGroupValues Table, groupName is a unique name
     * for the group so we can identify the group and not add duplicate groups when we get data from our REST endpoint
     * The positiveValue contains the value which should be used as positive indication for the Syndrome
     * The comparisonType contains what we should test against the positive value (equal,lessThan,lessOrEqual, etc)
     *
     * Finally Answers, contains the answers we have for a user, and we specify this with the userId, the questionId and the answer we have
     *
     */


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //execute the SQL Queries to create our Tables
        db.execSQL(CREATE_QUESTIONS_TABLE);
        db.execSQL(CREATE_ANSWERGROUPS_TABLE);
        db.execSQL(CREATE_ANSWERVALUES_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ANSWERS_TABLE);

        //Add the default data to the DB....
        //TODO: This should replaced by an REST endpoint where data is fetched from there and added to the DB
        addDefaultData(db);
    }

    /**
     * Updates the database (called when database version is newer than stored)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
        // Create tables again
        onCreate(db);
    }

    /**
     * Makes sure the database is closed
     */
    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private void addDefaultData(SQLiteDatabase db) {
        //our inserts in a list so we can iterate through it
        List<String> insertSQLCommands = new ArrayList<String>() {{
            add("INSERT OR REPLACE INTO " + TABLE_ANSWERVALUES + " VALUES (1, 1, 'YES')");
            add("INSERT INTO " + TABLE_ANSWERVALUES + " VALUES (2, 1, 'NO')");
            add("INSERT INTO " + TABLE_ANSWERVALUES + " VALUES (3, 2, 'MALE')");
            add("INSERT INTO " + TABLE_ANSWERVALUES + " VALUES (4, 2, 'FEMALE')");
            add("INSERT INTO " + TABLE_ANSWERGROUPS + " VALUES (1, 1, 'YESNO', 'YES', 'equal')");
            add("INSERT INTO " + TABLE_ANSWERGROUPS + " VALUES (2, 2, 'SEX', 'MALE', 'equal')");
            add("INSERT INTO " + TABLE_ANSWERGROUPS + " VALUES (3, 0, 'AGE', '15', 'lessOrEqual')");
            add("INSERT INTO " + TABLE_QUESTIONS + " VALUES (1, 'Do you have Migranes?', 'dropDown', 'YESNO')");
            add("INSERT INTO " + TABLE_QUESTIONS + " VALUES (2, 'What is your Age?', 'numeric', 'AGE')");
            add("INSERT INTO " + TABLE_QUESTIONS + " VALUES (3, 'Your Sex is?', 'dropDown', 'SEX')");
            add("INSERT INTO " + TABLE_QUESTIONS + " VALUES (4, 'Do you use hallucinogenic drugs?', 'dropDown', 'YESNO')");
        }};

        try {
            for (String sqlCommand : insertSQLCommands) {
                db.execSQL(sqlCommand);
            }
        }
        catch (Exception e) {
            Log.e(LOG, "Error: "+e.toString());
        }
    }

    // ------------------------ "QUESTIONS" table methods ----------------//

    /**
     * Creating a Question
     */
    public long createQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, question.getQuestion());
        values.put(KEY_TYPE, question.getType());
        values.put(KEY_ANSWERGROUPNAME, question.getAnswerGroup().getGroupName());

        // insert row
        long question_id = db.insert(TABLE_QUESTIONS, null, values);

        return question_id;
    }

    /**
     * get single Question
     */
    public Question getQuestion(long question_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + question_id;

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
            //construct the object from cursor
            Question question = createQuestionFromCursor(c);
            return question;
        }
        else {
            return null;
        }
    }

    /**
     * getting all Questions
     * */
    public List<Question> getAllQuestions() {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Question> questions = new ArrayList<Question>();

        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //construct the object from cursor
                Question question = createQuestionFromCursor(c);
                //add it to the question list
                questions.add(question);
            } while (c.moveToNext());
        }

        return questions;
    }

    /**
     * Gets the questions count
     * @return The available questions count
     */
    public long getQuestionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  COUNT(" + KEY_ID + ") FROM " + TABLE_QUESTIONS;
        SQLiteStatement s = db.compileStatement(selectQuery);
        long count = s.simpleQueryForLong();
        return count;
    }

    //Helper Method to create a Question object from a Cursor
    private Question createQuestionFromCursor(Cursor c) {

        Question question = new Question();
        question.setQuestion(c.getString(c.getColumnIndex(KEY_QUESTION)));
        question.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
        question.setId(c.getLong(c.getColumnIndex(KEY_ID)));

        //get the groupname and populate the object
        question.setAnswerGroup(getAnswerGroupForGroupName(c.getString(c.getColumnIndex(KEY_ANSWERGROUPNAME))));

        return question;
    }

    // ------------------------ "ANSWERGROUPS" table methods ----------------//

    /**
     * Creating an AnswerGroup
     */
    public long createAnswerGroup(AnswerGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ANSWERGROUPNAME, group.getGroupName());
        values.put(KEY_POSITIVEVALUE, group.getPositiveValue());
        values.put(KEY_VALUEID, group.getGroupValueId());
        values.put(KEY_COMPARISONTYPE, group.getComparisonType());

        // insert row
        long group_id = db.insert(TABLE_ANSWERGROUPS, null, values);

        return group_id;
    }

    /**
     * gets a single AnswerGroup for a given group Name
     * @param groupName The Name of the group to get the answerGroup for
     */
    public AnswerGroup getAnswerGroupForGroupName(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ANSWERGROUPS + " WHERE " + KEY_GROUPNAME + " = " + DatabaseUtils.sqlEscapeString(groupName);

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        //construct the object from cursor
        AnswerGroup answerGroup = new AnswerGroup();

        answerGroup.setGroupName(groupName);
        answerGroup.setComparisonType(c.getString(c.getColumnIndex(KEY_COMPARISONTYPE)));
        answerGroup.setGroupValueId(c.getLong(c.getColumnIndex(KEY_VALUEID)));
        answerGroup.setPositiveValue(c.getString(c.getColumnIndex(KEY_POSITIVEVALUE)));
        answerGroup.setAnswers(getAnswerGroupValuesForGroupId(answerGroup.getGroupValueId()));

        return answerGroup;
    }


    // ------------------------ "AnswerGroupValues" table methods ----------------//

    /**
     * Creating an AnswerGroupValue
     */
    public long createAnswerGroupValue(AnswerGroupValues answerGroupValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GROUPID, answerGroupValue.getGroupId());
        values.put(KEY_VALUE, answerGroupValue.getValue());

        // insert row
        long value_id = db.insert(TABLE_ANSWERVALUES, null, values);

        return value_id;
    }

    /**
     * get all AnswerGroupValues for a given group Id
     * @param groupId The Id of the group to get the answers for
     */
    public List<AnswerGroupValues> getAnswerGroupValuesForGroupId(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<AnswerGroupValues> answerGroupValues = new ArrayList<AnswerGroupValues>();

        String selectQuery = "SELECT  * FROM " + TABLE_ANSWERVALUES + " WHERE " + KEY_GROUPID + " = " + groupId;

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //construct the object from cursor
                AnswerGroupValues groupValue = new AnswerGroupValues();
                groupValue.setGroupId(c.getLong(c.getColumnIndex(KEY_GROUPID)));
                groupValue.setValue(c.getString(c.getColumnIndex(KEY_VALUE)));

                //add it to the answerGroupValues list
                answerGroupValues.add(groupValue);

            } while (c.moveToNext());
        }

        return answerGroupValues;
    }

    // ------------------------ "Answer" table methods ----------------//

    /**
     * Creating an Answer
     */
    public long createAnswer(Answer answer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ANSWER, answer.getAnswer());
        values.put(KEY_QUESTIONID, answer.getQuestionId());
        values.put(KEY_USERID, answer.getUserId());

        // insert row
        long anwer_id = db.insert(TABLE_ANSWERS, null, values);

        return anwer_id;
    }

    /**
     * gets the Answers for a given userId
     * @param userId The id of the user to fetch the answers for
     */
    public List<Answer> getAnswersForUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Answer> answers = new ArrayList<Answer>();

        String selectQuery = "SELECT  * FROM " + TABLE_ANSWERS + " WHERE " + KEY_USERID + " = " + userId;

        Log.d(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                //construct the object from cursor
                Answer answer = new Answer();

                answer.setAnswer(c.getString(c.getColumnIndex(KEY_ANSWER)));
                answer.setQuestionId(c.getLong(c.getColumnIndex(KEY_QUESTIONID)));
                answer.setUserId(userId);

                //add it to the answers list
                answers.add(answer);

            } while (c.moveToNext());
        }

        return answers;
    }

    /**
     * Deletes all answers for a given User Id
     * @param userId The user's id to delete the answers for
     */
    public void deleteAnswersForUserId(long userId) {
        String deleteQuery = " DELETE FROM " + TABLE_ANSWERS +" WHERE " + KEY_USERID +" IN (SELECT " + KEY_USERID + " FROM " + TABLE_ANSWERS +" WHERE " + KEY_USERID + " = " + userId + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    // ------------------------ "User" table methods ----------------//

    /**
     * Adds a user to the database with a given email
     * @param email The users email to add
     * @return The userId assigned to the user
     */
    public long addUserWithEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        //check if the user already exists and if it exists don't add again
        long user_id = getUserIdForEmail(email);

        if (user_id!=-1)
            return  user_id;

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);

        // insert row
        user_id = db.insert(TABLE_USERS, null, values);

        return user_id;
    }

    /**
     * Deletes a user from the database with a given email
     * @param email The users email that we are going to remove
     */
    public void deleteUserWithEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_EMAIL + "=?",new String[] { String.valueOf(email) });
    }

    /**
     * Gets the user Id of a user with a given email
     * @param email The email of the user to fetch the id for
     * @return The userId or -1 if the email is not found
     */
    public long getUserIdForEmail(String email) {
        long userId = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = " + DatabaseUtils.sqlEscapeString(email);

        Log.d(LOG, selectQuery);

        Cursor c = null;

        try {
            c = db.rawQuery(selectQuery, null);
        }
        catch (Exception e) {
            Log.e(LOG, "Error: "+e.toString());
        }


        if (c != null && c.moveToFirst()) {
            userId = c.getLong(c.getColumnIndex(KEY_ID));
        }

        return userId;
    }

}