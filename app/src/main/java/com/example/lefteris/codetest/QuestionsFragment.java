/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.toddsyndromelib.Models.AnswerGroupValues;
import com.example.toddsyndromelib.Models.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lefteris on 07/02/17.
 */

public class QuestionsFragment extends Fragment {
    /**
     * The argument key for the question
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page (question) number
     */
    private int mPageNumber;

    /**
     * The fragment's Question Object
     */
    private Question mQuestion;

    /**
     * The answer for the question
     */
    private String mAnswer;

    /**
     * Factory method for constructing a new fragment for the given page number.
     */
    public static QuestionsFragment create(int pageNumber) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE,pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //get the Question from the Activity
        mQuestion = ((QuestionsActivity)this.getActivity()).getQuestionForPage(mPageNumber);

        ViewGroup rootView = null;

        //based on the question type, inflate the appropriate layout (right now we only have 2, but we can extend)
        if (mQuestion.getType().equalsIgnoreCase("dropDown")) {
            rootView = (ViewGroup) inflater.inflate(R.layout.question_dropdown, container, false);
            configureForDropDown(rootView);
        }
        else if (mQuestion.getType().equalsIgnoreCase("numeric")) {
            rootView = (ViewGroup) inflater.inflate(R.layout.question_numeric, container, false);
            configureForNumeric(rootView);
        }

        LinearLayout mainLayout = (LinearLayout)rootView.findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(mPageNumber%2==0 ? getResources().getColor(R.color.bgColor1) : getResources().getColor(R.color.bgColor2));

        return rootView;
    }

    /**
     * @return The Answer given in the current question
     */
    public String getAnswer() {
        return mAnswer;
    }

    /**
     * Configures the fragment for drop down layout
     * @param rootView The Inflated Layout ViewGroup
     */
    private void configureForDropDown(ViewGroup rootView) {
        //get the elements.
        TextView textView = (TextView)rootView.findViewById(R.id.textView);
        Spinner spinner = (Spinner)rootView.findViewById(R.id.spinner);

        //configure them
        textView.setText(mQuestion.getQuestion());

        //create the simple adapter for our spinner
        final List<String> list = new ArrayList<String>();

        //get our possible answers from the AnswerGroupValues model and add them to the list as strings
        for (AnswerGroupValues groupValue : mQuestion.getAnswerGroup().getAnswers()) {
            list.add(groupValue.getValue());
        }

        //set the default answer to the first selection
        mAnswer = list.get(0);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAnswer = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Configures the fragment for a numeric input layout
     * @param rootView The Inflated Layout ViewGroup
     */
    private void configureForNumeric(ViewGroup rootView) {
        //get the elements.
        TextView textView = (TextView)rootView.findViewById(R.id.textView);
        EditText editText = (EditText)rootView.findViewById(R.id.editText);

        //configure them
        textView.setText(mQuestion.getQuestion());

        //add a TextWatcher to store the answer
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAnswer = s.toString();
            }
        });
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
