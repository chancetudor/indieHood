package com.indiehood.app.ui.report;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

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


public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;

    private Button mReportButton;
    private EditText mReportText;

    private FirebaseFirestore mFireStore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                ViewModelProviders.of(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        reportViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        mReportText = (EditText) root.findViewById(R.id.reportText);
        mReportButton = (Button) root.findViewById(R.id.reportButton);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userReport = mReportText.getText().toString();

                HashMap<String, String> userMap = new HashMap<>();

                userMap.put("reportText", userReport);

                mFireStore.collection("UserInputCol").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast toast = Toast.makeText(getActivity(), "Report has been sent!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });



                }
        });


        return root;
    }


}