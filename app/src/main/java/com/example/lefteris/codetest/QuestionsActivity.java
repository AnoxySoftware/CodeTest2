/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.lefteris.codetest.Custom.CustomPager;
import com.example.toddsyndromelib.Models.Question;
import com.example.toddsyndromelib.toddsSyndrome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.lefteris.codetest.MainActivity.EXTRA_USER_EMAIL;
import static com.example.lefteris.codetest.MainActivity.EXTRA_USER_ID;
import static com.example.lefteris.codetest.R.id.list;
import static com.example.lefteris.codetest.R.id.pager;

/**
 * Created by Lefteris on 07/02/17.
 */

public class QuestionsActivity extends AppCompatActivity {

    //The user's user ID
    protected long mUserId;

    //The user's email
    protected String mUserEmail;

    //The pager widget
    protected CustomPager mPager;

    //The pager adapter
    private SimplePagerAdapter mPagerAdapter;

    //our Library
    private toddsSyndrome libSyndrome;

    //the List of questions available
    private List<Question> mQuestionsList;

    //the List of the users answers
    private Map<Integer, String> answerList = new HashMap<Integer, String>();

    //the ListView (only on Tablets)
    private ListView mListView;

    //holds the ListView selected View
    private View currentSelectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //get the user Id
        mUserId = getIntent().getLongExtra(EXTRA_USER_ID,-1);
        //and the email
        mUserEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);

        //initialize our library...
        libSyndrome = new toddsSyndrome(getApplicationContext());

        //get the questions List
        mQuestionsList = libSyndrome.getQuestions();

        // Instantiate the ViewPager and the PagerAdapter.
        mPager = (CustomPager) findViewById(pager);

        mPagerAdapter = new SimplePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new CubeOutTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //When changing pages, reset the action bar actions
                invalidateOptionsMenu();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //instatiate the ListView if it exists (Only on tablets)
        mListView = (ListView) findViewById(list);
        if (mListView!=null) {
            setupListView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close the database in our library
        libSyndrome.closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //inflate menu
        getMenuInflater().inflate(R.menu.activity_questions, menu);

        //if this is not the first item, set the previous action to enabled
        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        //check if this is the last item and set the button on the action bar to Finish or Next
        int itemTextResource = (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) ? R.string.action_finish : R.string.action_next;

        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE, itemTextResource);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Go back to the Home Screen
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                //hide the keyboard
                hideKeyboard();
                //store the answer
                storeAnswer();
                // Go to the previous step
                changeListSelection(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                //hide the keyboard
                hideKeyboard();
                //store the answer
                storeAnswer();
                // Go go the next step
                if (mPager.getCurrentItem()+1==mPagerAdapter.getCount()) {
                    //we are at the end
                    showResult();
                }
                else {
                    changeListSelection(mPager.getCurrentItem() + 1);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Hides the soft keyboard
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Helper function to provide the Question object for the QuestionsFragment
     * @param pageNumber The current page Number we are showing in the Questions Fragment
     * @return The Question object
     */
    public Question getQuestionForPage(int pageNumber) {
        return mQuestionsList.get(pageNumber);
    }

    /**
     * Helper function to store the answer from the QuestionsFragment
     */
    private void storeAnswer() {
        //get the answer from the active fragment
        int currentPage = mPager.getCurrentItem();
        //get the answer from the fragment
        String answer =  ((QuestionsFragment) mPagerAdapter.instantiateItem(mPager, currentPage)).getAnswer();
        //add it to the list or replace if the item already exists
        answerList.put(currentPage,answer);
    }

    /**
     * Shows the results for the user and saves the data to the db
     */
    private void showResult(){
        //if we don't have all answers, don't allow to continue...
        if (answerList.size()!=mQuestionsList.size()) {
            //show the alert and stop
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_missingAnswers_title)
                    .setMessage(R.string.alert_missingAnswers_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_btn_OK, null).show();
            return;
        }

        //iterrate over the answers and save them...
        for (Map.Entry<Integer, String> answer : answerList.entrySet()) {
            Question question = mQuestionsList.get(answer.getKey());
            long questionId = question.getId();
            libSyndrome.addAnswerForUserId(mUserId,questionId,answer.getValue());
        }

        //show the results view...
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(EXTRA_USER_ID, mUserId);
        intent.putExtra(EXTRA_USER_EMAIL, mUserEmail);
        startActivity(intent);
    }

    /**
     * A simple pager adapter for showing up the questions as screen slide
     */
    public class SimplePagerAdapter extends FragmentStatePagerAdapter {

        public SimplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionsFragment.create(position);
        }

        @Override
        public int getCount() {
            return mQuestionsList.size();
        }
    }

    private void setupListView() {
        //prepare our listview adapter...
        List<String> data = new ArrayList<String>();

        for (Question question : mQuestionsList) {
            data.add(question.getQuestion());
        }

        //create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // View from recycle
                View row = convertView;

                //if we don't have a recycled view ready, create a new one
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.row_list_item_simple, null);
                }

                //get our item
                String itemText = getItem(position);
                //get our text view
                TextView tv = (TextView) row.findViewById(R.id.txt_title);
                //set the text
                tv.setText(itemText);
                return row;
            }
        };

        //set it
        mListView.setAdapter(adapter);

        //check the first item
        mListView.setItemChecked(0,true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hide the keyboard
                hideKeyboard();
                //store the anwer
                storeAnswer();
                //change to the selected question
                mPager.setCurrentItem(position,true);
            }
        });
    }

    private void changeListSelection(int newPosition) {
        if (mListView!=null) {
            mListView.setItemChecked(newPosition,true);
        }
        mPager.setCurrentItem(newPosition,true);
    }
}
