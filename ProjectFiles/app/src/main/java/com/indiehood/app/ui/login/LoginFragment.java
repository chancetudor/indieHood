package com.indiehood.app.ui.login;


import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.indiehood.app.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    View v;
    TextView btnRegister, btnLogin;
    EditText txMail,txPass;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        v = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnRegister=(TextView) v.findViewById(R.id.btnRegister);
        txMail=(EditText)v.findViewById(R.id.txtArtistUsername);
        txPass=(EditText)v.findViewById(R.id.txtPassword);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        return v;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnRegister)
        {
            Toast.makeText(v.getContext(), "Going to registration page", Toast.LENGTH_LONG).show();
            Navigation.findNavController(v).navigate(R.id.nav_register);
        }
        else if(v.getId() == R.id.btnLogin)
        {
            LoginUser(txMail.getText().toString(),txPass.getText().toString());
        }
    }

    private void LoginUser(String mail, final String password) {

        try {
            mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(v.getContext(), "Success!", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(v).navigate(R.id.nav_post);
                    }
                    else if (mUser != null) {
                        Toast.makeText(v.getContext(), "User Currently Logged In", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(v).navigate(R.id.nav_post);
                    }
                    else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }

                }
            });

        }
        catch (Exception ex)
        {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    ex.getLocalizedMessage().toString(), Snackbar.LENGTH_LONG).show();
        }

    }}