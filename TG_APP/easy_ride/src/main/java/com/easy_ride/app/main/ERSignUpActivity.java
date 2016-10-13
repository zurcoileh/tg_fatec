package com.easy_ride.app.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.easy_ride.R;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.User;
import com.firebase.client.Firebase;

import java.util.Observable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ERSignUpActivity extends Activity implements ERView {
    private static final String TAG = "SignUpActivity";
    private ERDBModel model;
    private static final int REQUEST_LOGIN = 0;
    ProgressDialog progressDialog = null;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_ra) EditText _raText;
    @InjectView(R.id.input_phone) EditText _phoneText;
    @InjectView(R.id.input_end) EditText _endText;
    @InjectView(R.id.input_neigh) EditText _neighText;
    @InjectView(R.id.input_course) EditText _courseText;
    @InjectView(R.id.input_city) EditText _cityText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ersign_up);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        Firebase.setAndroidContext(this);
        model = new ERDBModel();
        model.addObserver(this);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Campos inválidos!");
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(ERSignUpActivity.this,
                R.style.AppTheme_Dark);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = _emailText.getText().toString();

        model.verifyEmail(email);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String ra = _raText.getText().toString();
        String phone = _phoneText.getText().toString();
        String end = _endText.getText().toString();
        String neigh = _neighText.getText().toString();
        String course = _courseText.getText().toString();
        String city = _cityText.getText().toString();
        User u =  new User(ra,name,email,phone,end,neigh,city,course,"");

        model.storeUser(u);
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String ra = _raText.getText().toString();
        String phone = _phoneText.getText().toString();
        String end = _endText.getText().toString();
        String neigh = _neighText.getText().toString();
        String course = _courseText.getText().toString();
        String city = _cityText.getText().toString();

        if (name.isEmpty() || name.length() < 3 ||  name.split(" ").length < 2) {
            _nameText.setError("nome e sobrenome!");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (ra.isEmpty() || ra.length() != 13 ) {
            _raText.setError("campo inválido!");
            valid = false;
        } else {
            _raText.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 10 || phone.length() > 12) {
            _phoneText.setError("campo inválido!");
            valid = false;
        } else {
            _phoneText.setError(null);
        }


        if ( end.isEmpty() || end.split(" ").length < 2) {
            _endText.setError("campo inválido!");
            valid = false;
        } else {
            _endText.setError(null);
        }

        if ( neigh.isEmpty() ) {
            _neighText.setError("campo inválido!");
            valid = false;
        } else {
            _neighText.setError(null);
        }

        if ( city.isEmpty() ) {
            _cityText.setError("campo inválido!");
            valid = false;
        } else {
            _cityText.setError(null);
        }

        if ( course.isEmpty() ) {
            _courseText.setError("campo inválido!");
            valid = false;
        } else {
            _courseText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("entre com email válido!");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("entre 4 e 10 caracteres alfanumericos!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void update(Observable observable, Object data) {

        if(((String) data).equals("VALID_EMAIL")) {
            String password = _passwordText.getText().toString();
            String email = _emailText.getText().toString();
            model.signup(email, password);
        }else if(((String) data).equals("OK")) {
            onSignupSuccess();
        }else if(((String) data).equals("STORED")) {
            Toast.makeText(getBaseContext(), "Cadastro criado com sucesso!", Toast.LENGTH_LONG).show();
            // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), ERLoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
            finish();
        }else if(((String) data).equals("USER_EXIST")){
            onSignupFailed("Email já cadastrado no sistema!");
        }else{
            onSignupFailed((String) data);
        }
        progressDialog.dismiss();

    }
}