package com.easy_ride.app.main;


            import android.app.Activity;
            import android.app.ProgressDialog;
            import android.content.Context;
            import android.location.LocationManager;
            import android.net.ConnectivityManager;
            import android.net.NetworkInfo;
            import android.os.Bundle;
            import android.support.v4.app.Fragment;
            import android.support.v4.app.FragmentActivity;
            import android.support.v4.app.FragmentManager;
            import android.support.v7.app.AppCompatActivity;
            import android.util.Log;

            import android.content.Intent;
            import android.view.View;
            import android.widget.Button;
            import android.widget.EditText;
            import android.widget.TextView;
            import android.widget.Toast;

            import com.app.easy_ride.R;
            import com.easy_ride.app.model.ERDBModel;
            import com.easy_ride.app.model.UserSessionManager;
            import com.firebase.client.Firebase;

            import java.util.HashMap;
            import java.util.Map;
            import java.util.Observable;

            import butterknife.ButterKnife;
            import butterknife.InjectView;

public class ERLoginActivity extends Activity implements ERView {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog = null;
    private UserSessionManager session;
    private ERDBModel model;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erlogin);
        ButterKnife.inject(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ERSignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        Firebase.setAndroidContext(this);
        model = new ERDBModel();
        model.addObserver(this);

        this.session = new UserSessionManager(getApplicationContext());

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (this.session.isUserLogged()) {
                    //Start Main Activity
                    Intent intent = new Intent(getApplicationContext(), ERVMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Sem acesso a Internet.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "GPS desativado. Ative para continuar", Toast.LENGTH_LONG).show();
        }


    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("");
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(ERLoginActivity.this,
                R.style.AppTheme_Dark);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        model.login(email, password, session);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //Start Main Activity
        Intent intent = new Intent(getApplicationContext(), ERVMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String error) {
        Toast.makeText(getBaseContext(),error, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("email inválido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("senha deve ser entre 4 e 10 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void update(Observable observable, Object data) {

            if(((String) data).equals("OK")){
                onLoginSuccess();
            }else{
                onLoginFailed((String) data);
            }
        progressDialog.dismiss();
    }
}