package com.example.lefteris.codetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.toddsyndromelib.toddsSyndrome;

import java.util.regex.Pattern;

/**
 * The main application Screen that first creates the user, or get's the existing user's answers and score!
 */
public class MainActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private View mLoginFormView;

    //Library Reference
    private toddsSyndrome libSyndrome;

    //the userId of the user if user Exists for the given email
    protected long existingUserId = -1;

    public static final String EXTRA_USER_ID = "com.example.lefteris.codetest.userId";
    public static final String EXTRA_USER_EMAIL = "com.example.lefteris.codetest.userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize our library
        libSyndrome = new toddsSyndrome(getApplicationContext());

        // Set up the form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        //add the listener for the button
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //add a textWatcher to watch if the typed email address exists and if it exists, change the button caption
        TextWatcher userWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (isEmailValid(email)) {
                    existingUserId = libSyndrome.getUserIdForEmail(email);
                    //check if we have an account for this user and if yes, change the button text...
                    if (existingUserId!=-1) {
                        mEmailSignInButton.setText(R.string.action_sign_in_exist);
                        return;
                    }
                }
                else {
                    existingUserId = -1;
                }

                mEmailSignInButton.setText(R.string.action_sign_in);
            }
        };

        mEmailView.addTextChangedListener(userWatcher);
        mLoginFormView = findViewById(R.id.login_form);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close the database in our library
        libSyndrome.closeDatabase();
    }

    /**
     * Attempts to create a user or load a user's answers
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and the process stops here
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);

        //get the email address entered
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't continue and focus the form field with the error.
            focusView.requestFocus();
        }
        else {
            //everything ok, create user if a new user and proceed
            if (existingUserId==-1) {
                //create the user in the db
                existingUserId = libSyndrome.addUserWithEmail(email);
                //show the questions
                Intent intent = new Intent(this, QuestionsActivity.class);
                intent.putExtra(EXTRA_USER_ID, existingUserId);
                intent.putExtra(EXTRA_USER_EMAIL, email);
                startActivity(intent);
            }
            else {
                //this is an existing user, show the answers he gave already and his score
                //show the questions
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra(EXTRA_USER_ID, existingUserId);
                intent.putExtra(EXTRA_USER_EMAIL, email);
                startActivity(intent);
            }
        }
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}

