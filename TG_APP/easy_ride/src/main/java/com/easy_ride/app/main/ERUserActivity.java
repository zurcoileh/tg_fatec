package com.easy_ride.app.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.User;
import com.easy_ride.app.model.UserSessionManager;
import com.easy_ride.app.support.Constants;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Observable;

public class ERUserActivity extends Fragment implements ERView {
    private static final String TAG = "UpdateUserActivity";
    private View view;
    private ERDBModel model;
    private UserSessionManager session;
    private static final int REQUEST_LOGIN = 0;

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _raText;
    EditText _phoneText;
    EditText _endText;
    EditText _neighText;
    Button _updateButton;
    TextView _loginLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_eruser, container, false);
       // ButterKnife.inject(getActivity());

        _nameText = (EditText) this.view.findViewById(R.id.input_name);
        _emailText = (EditText) this.view.findViewById(R.id.input_email);
        _raText = (EditText) this.view.findViewById(R.id.input_ra);
        _phoneText = (EditText) this.view.findViewById(R.id.input_phone);
        _endText = (EditText) this.view.findViewById(R.id.input_end);
        _passwordText = (EditText) this.view.findViewById(R.id.input_password);
        _neighText = (EditText) this.view.findViewById(R.id.input_neigh);
        _updateButton = (Button) this.view.findViewById(R.id.btn_update);

        _updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        _emailText.setEnabled(false);
        _emailText.setClickable(false);
        _raText.setEnabled(false);
        _raText.setClickable(false);
        _passwordText.setVisibility(View.GONE);

        Firebase.setAndroidContext(getActivity());
        model = new ERDBModel();
        model.addObserver(this);

        this.session = new UserSessionManager(getActivity().getApplicationContext());
        model.getUserData(session.getUserEmail());

        return view;
    }

    public void updateUser() {

        if (!validate()) {
            onUpdateFailed("Campos inválidos!");
            return;
        }

        _updateButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String ra = _raText.getText().toString();
        String phone = _phoneText.getText().toString();
        String end = _endText.getText().toString();
        String neigh = _neighText.getText().toString();
        User u =  new User(ra,name,email,phone,end,neigh);

        model.updateUser(u);
    }

    public void fillUserData(User user) {
        _nameText.setText(user.getName());
        _raText.setText(user.getRa());
        _phoneText.setText(user.getPhone());
        _endText.setText(user.getEnd());
        _neighText.setText(user.getNeigh());
        _emailText.setText(user.getEmail());

        if (!_updateButton.isEnabled()){
            Toast.makeText(getActivity().getApplicationContext(),"Cadastro atualizado com sucesso!", Toast.LENGTH_LONG).show();
            _updateButton.setEnabled(true);
        }
    }

    public void onUpdateFailed(String msg) {
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        _updateButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String end = _endText.getText().toString();
        String neigh = _neighText.getText().toString();

        if (name.isEmpty() || name.length() < 3 ||  name.split(" ").length < 2) {
            _nameText.setError("nome e sobrenome!");
            valid = false;
        } else {
            _nameText.setError(null);
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

        return valid;
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data != null){
            ArrayList<User> uList = (ArrayList<User>) data;
            for (User u: uList) fillUserData(u);
        }else{
            onUpdateFailed((String)data);
        }

    }
}