package root.localApparel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SellingClosetFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<PostObjects> posts;
    PostHolder postHolder;
    Button addBtn, purchases;

    public SellingClosetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling_closet, container, false);
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
        purchases = view.findViewById(R.id.purchases);
        addBtn = view.findViewById(R.id.AddButton);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment addFrag = new AddClothes();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.content, addFrag).commit();
            }
        });
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addFrag2 = new prevOrders();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.content, addFrag2).commit();

            }
        });
        return view;
    }
    private void loadPosts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Boolean mySales = dataSnapshot1.child("uid").getValue().equals(myuid);
                    Boolean notSold = dataSnapshot1.child("purchased").getValue().equals("No");
                    if (mySales && notSold) {
                        PostObjects modelPost = dataSnapshot1.getValue(PostObjects.class);
                        posts.add(modelPost);
                    }
                }
                postHolder = new PostHolder(getActivity(), posts, "SellingclosetFragment");
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