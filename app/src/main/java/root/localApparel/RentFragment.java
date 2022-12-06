package root.localApparel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<PostObjects> posts;
    PostHolder postHolder;

    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rent, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.postrecyclerview);
        recyclerView.setHasFixedSize(true);
        myuid = firebaseAuth.getInstance().getUid();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        loadPosts();
        return view;
    }

    private void loadPosts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Boolean notMyPosts = !dataSnapshot1.child("uid").getValue().equals(myuid);
                    Boolean toRent = dataSnapshot1.child("buyOrRent").getValue().equals("Rent");
                    Boolean isNotPurchased = dataSnapshot1.child("purchased").getValue().equals("No");
                    if (notMyPosts && toRent && isNotPurchased) {
                        PostObjects modelPost = dataSnapshot1.getValue(PostObjects.class);
                        modelPost.setbuyOrRent("Rent");
                        modelPost.setDescription((String) dataSnapshot1.child("description").getValue());
                        modelPost.setPrice((String) dataSnapshot1.child("price").getValue());
                        modelPost.setPtime((String) dataSnapshot1.child("ptime").getValue());
                        modelPost.setTitle((String) dataSnapshot1.child("title").getValue());
                        modelPost.setUdp((String) dataSnapshot1.child("udp").getValue());
                        modelPost.setUemail((String) dataSnapshot1.child("uemail").getValue());
                        modelPost.setUid((String) dataSnapshot1.child("uid").getValue());
                        modelPost.setUimage((String) dataSnapshot1.child("uimage").getValue());
                        modelPost.setUname((String) dataSnapshot1.child("uname").getValue());
                        modelPost.setAlive((String) dataSnapshot1.child("purchased").getValue());
                        modelPost.setAddress((String) dataSnapshot1.child("addressToSend").getValue());
                        modelPost.setBuyer((String) dataSnapshot1.child("buyer").getValue());
                        posts.add(modelPost);
                    }
                }
                postHolder = new PostHolder(getActivity(), posts, "RentFragment");
                recyclerView.setAdapter(postHolder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}