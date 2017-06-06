/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.toddsyndromelib.Models.Answer;
import com.example.toddsyndromelib.Models.Question;
import com.example.toddsyndromelib.toddsSyndrome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {

    //The user's user ID
    private long mUserId;

    //The user's email
    private String mUserEmail;

    //The ListView
    private ListView mListView;

    //The User Text View
    private TextView mUserTxtView;

    //The progress bar
    private ProgressBar mProgress;

    //The Score results text view
    private TextView mScoreTxtView;

    //The user's answers List
    private List<Answer>answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //get our UI elements
        mListView = (ListView) findViewById(R.id.listView);
        mUserTxtView = (TextView) findViewById(R.id.txtUser);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mScoreTxtView = (TextView) findViewById(R.id.txtScore);

        //get the user Id
        mUserId = getIntent().getLongExtra(MainActivity.EXTRA_USER_ID,-1);
        //and the email
        mUserEmail = getIntent().getStringExtra(MainActivity.EXTRA_USER_EMAIL);

        //setup the user's results
        getUserResult();
    }


    private void getUserResult() {
        //get the user's answers

        //Library Reference
        toddsSyndrome libSyndrome = new toddsSyndrome(getApplicationContext());
        answers = libSyndrome.getAnswersForUserId(mUserId);
        int score = libSyndrome.getScoreForUserWithId(mUserId);

        //setup the view...

        //set the title
        mUserTxtView.setText(getString(R.string.user_result_title, mUserEmail));

        //set the progress bar value
        mProgress.setProgress(score);

        //set the probability text
        mScoreTxtView.setText(getString(R.string.user_result_score,score));

        //prepare our listview adapter...
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        for (Answer answer : answers) {
            //get the question for this answer...
            Question question = libSyndrome.getQuestionForId(answer.getQuestionId());
            //if the question is not found (i.e. a new question set has been added and the question has been removed)
            //ignore this answer
            if (question==null)
                continue;

            Map<String, String> itemData = new HashMap<String, String>(2);

            itemData.put("question",question.getQuestion());
            itemData.put("answer",answer.getAnswer());
            data.add(itemData);
        }

        //create the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[] {"question", "answer"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        //set it
        mListView.setAdapter(adapter);
    }

}
