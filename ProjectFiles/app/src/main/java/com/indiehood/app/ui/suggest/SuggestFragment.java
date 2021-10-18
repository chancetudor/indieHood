package com.indiehood.app.ui.suggest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.indiehood.app.R;

import java.util.HashMap;

public class SuggestFragment extends Fragment {

    private com.indiehood.app.ui.suggest.SuggestViewModel suggestViewModel;

    private Button mVenueButton;
    private EditText mVenueName;
    private EditText mVenueAdd;
    private EditText mVenueNum;
    private EditText mVenueText;

    private FirebaseFirestore mFireStore;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        suggestViewModel =
                ViewModelProviders.of(this).get(com.indiehood.app.ui.suggest.SuggestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_suggest, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        suggestViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        mVenueName = (EditText) root.findViewById(R.id.venueName);
        mVenueAdd = (EditText) root.findViewById(R.id.venueAdd);
        mVenueNum = (EditText) root.findViewById(R.id.venueNum);
        mVenueText = (EditText) root.findViewById(R.id.venueText);
        mVenueButton = (Button) root.findViewById(R.id.venueButton);

        mVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String venName = mVenueName.getText().toString();
                String venAdd = mVenueAdd.getText().toString();
                String venNum = mVenueNum.getText().toString();
                String venText = mVenueText.getText().toString();

                HashMap<String, String> venMap = new HashMap<>();

                venMap.put("venueName", venName);
                venMap.put("venueAddress", venAdd);
                venMap.put("venuePhone", venNum);
                venMap.put("venueComments", venText);

                mFireStore.collection("UserInputCol").add(venMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast toast2 = Toast.makeText(getActivity(), "Thank you for your suggestion!", Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                });
            }
        });


        return root;
    }
}