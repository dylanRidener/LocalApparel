package root.localApparel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;


import java.util.ArrayList;


public class ProfileFragment extends Fragment {

//    ProgressDialog pd;
    private FirebaseAuth firebaseAuth;

    TextView name, email;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView userPicPaged;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        // firebase pull for user data
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //un-needed
//        pd = new ProgressDialog(getActivity());
//        pd.setCanceledOnTouchOutside(false);

        // create associated data to view
        email = view.findViewById(R.id.email_prof);
        name = view.findViewById(R.id.name_prof);
        userPicPaged = view.findViewById(R.id.userPic);

        // creating editor button for username
        Button editor = view.findViewById(R.id.button9);

//        name.setText(databaseReference.); //this works to pull data from firebase but it's the only thing that's worked
//        email.setText(firebaseUser.getDisplayName());

//        email.setText(firebaseAuth.getInstance().getCurrentUser().getDisplayName());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override //pulls data from firebase
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(FirebaseAuth.getInstance().getUid())) {
                        String name1 = "" + snap.child("name").getValue().toString();
                        String email1 = "" + snap.child("email").getValue().toString();
                        String pic = "" + snap.child("image").getValue().toString();

                        //populate pic
                        try {
                            if (pic == "") {

                            } else {
                                Glide.with(getActivity()).load(pic).into(userPicPaged);
                            }
                        } catch (Exception error) {

                        }

                        //populate name and email
                        name.setText(name1);
                        email.setText(email1);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileEditor.class));
            }
        });
        return view;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_out, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Logout Functionality
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(), SplashScreen.class));
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);


    }
}
