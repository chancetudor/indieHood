package com.indiehood.app.ui.post;

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
import com.indiehood.app.ui.listings.ShowListing;

import java.util.HashMap;

public class PostFragment extends Fragment {

    private com.indiehood.app.ui.post.PostViewModel postViewModel;

    private Button mPostButton;
    private EditText mPostVenueName;
    private EditText mPostAdd;
    private EditText mPostDate;
    private EditText mPostTime;
    private EditText mPostPrice;
    private EditText mPostDesc;

    private FirebaseFirestore mFireStore;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postViewModel =
                ViewModelProviders.of(this).get(com.indiehood.app.ui.post.PostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        postViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        mPostVenueName = (EditText) root.findViewById(R.id.postVenueName);
        mPostAdd = (EditText) root.findViewById(R.id.postAddress);
        mPostDate = (EditText) root.findViewById(R.id.postDate);
        mPostTime = (EditText) root.findViewById(R.id.postTime);
        mPostPrice = (EditText) root.findViewById(R.id.postPrice);
        mPostDesc = (EditText) root.findViewById(R.id.postDesc);
        mPostButton = (Button) root.findViewById(R.id.postButton);

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postVenName = mPostVenueName.getText().toString();
                String postAdd = mPostAdd.getText().toString();
                String postDate = mPostDate.getText().toString();
                String postTime = mPostTime.getText().toString();
                String postPrice1 = mPostPrice.getText().toString();
                int postPrice = Integer.parseInt(postPrice1);
                String postDesc = mPostDesc.getText().toString();



                HashMap<String, Object> postMap = new HashMap<>();

                //ShowListing newList = new ShowListing("LoggedIn Artist", postVenName, postDate, postTime, postPrice, postAdd, postDesc, false, 0);

                postMap.put("bandName", "Banana Rays");
                postMap.put("numberInterested", 0);
                postMap.put("venueName", postVenName);
                postMap.put("address1", postAdd);
                postMap.put("address2", "Tuscaloosa, AL 35401");
                postMap.put("addressLat", 33.2104992);
                postMap.put("addressLong", -87.551923);
                postMap.put("startDay", postDate);
                postMap.put("startTime", postTime);
                postMap.put("endDay", postDate);
                postMap.put("endTime", postTime);
                postMap.put("price", postPrice);
                postMap.put("description", postDesc);

                mFireStore.collection("ShowListingCol").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast toast2 = Toast.makeText(getActivity(), "Your show is now listed!", Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                });
            }
        });


        return root;
    }
}