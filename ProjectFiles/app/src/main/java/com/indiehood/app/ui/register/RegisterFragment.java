package com.indiehood.app.ui.register;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.indiehood.app.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Random;

public class RegisterFragment extends Fragment {
    private com.indiehood.app.ui.register.RegisterViewModel registerViewModel;

    private Button mbtn_submit_registration;
    private EditText mregister_bio;
    private EditText mregister_name;
    private EditText mregister_social_two;
    private EditText mregister_media_one;
    private EditText mregister_media_two;
    private EditText mregister_social_one;
    private EditText mregister_password;
    private EditText mregister_email;

    private FirebaseFirestore mFireStore;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerViewModel = ViewModelProviders.of(this).get(com.indiehood.app.ui.register.RegisterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        final TextView textView = root.findViewById(R.id.text_tools);
        registerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        mbtn_submit_registration = (Button) root.findViewById(R.id.btn_submit_registration);
        mregister_bio = (EditText) root.findViewById(R.id.register_bio);
        mregister_name = (EditText) root.findViewById(R.id.register_name);
        mregister_social_two = (EditText) root.findViewById(R.id.register_social_two);
        mregister_media_one = (EditText) root.findViewById(R.id.register_media_one);
        mregister_media_two = (EditText) root.findViewById(R.id.register_media_two);
        mregister_social_one = (EditText) root.findViewById(R.id.register_social_one);
        mregister_password = (EditText) root.findViewById(R.id.register_password);
        mregister_email = (EditText) root.findViewById(R.id.register_email);

        mbtn_submit_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String registerName = mregister_name.getText().toString();
                String registerBio = mregister_bio.getText().toString();
                String registerSoc1 = mregister_social_one.getText().toString();
                String registerSoc2 = mregister_social_two.getText().toString();
                String registerMed1 = mregister_media_one.getText().toString();
                String registerMed2 = mregister_media_two.getText().toString();
                String registerPass = mregister_password.getText().toString();
                String registerEmail = mregister_email.getText().toString();


                HashMap<String, Object> regMap = new HashMap<>();
                HashMap<String, Object> regMap2 = new HashMap<>();
                Random rand = new Random();

                regMap.put("artistName", registerName);
                regMap.put("bio", registerBio);
                regMap.put("media1", registerMed1);
                regMap.put("media2", registerMed2);
                regMap.put("social1", registerSoc1);
                regMap.put("social2", registerSoc2);
                regMap.put("rating", "5");
                regMap.put("favorited", false);
                regMap.put("password", registerPass);
                regMap.put("email", registerEmail);
                regMap2.put("artist", registerName);
                regMap2.put("isArtist", true);
                regMap2.put("UID", rand.nextInt(100000));

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(registerEmail, registerPass);

                mFireStore.collection("ArtistCollection").add(regMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mregister_name.setText("");
                        mregister_bio.setText("");
                        mregister_password.setText("");
                        mregister_social_one.setText("");
                        mregister_social_two.setText("");
                        mregister_media_one.setText("");
                        mregister_media_two.setText("");
                        mregister_email.setText("");

                        //Toast.makeText(getActivity(), "Please Wait 3 Minutes Before Logging In", Toast.LENGTH_LONG).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.wait, Snackbar.LENGTH_LONG).show();
                    }
                });


                mFireStore.collection("UserCol").add(regMap2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Navigation.findNavController(v).navigate(R.id.nav_login);
                    }
                    });
                }
        });
        return root;
    }
}