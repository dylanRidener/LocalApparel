package root.localApparel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SellingClosetFragment extends Fragment {

    Button addBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling_closet, container, false);

        addBtn = view.findViewById(R.id.AddButton);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment addFrag = new AddClothes();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.content, addFrag).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

//    private void loadMyPosts() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
//        postrecycle.setLayoutManager(layoutManager);
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                posts.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    for (DataSnapshot childsnapshot: dataSnapshot1.getChildren()) {
//                        if (childsnapshot.child("uid").getValue().equals(uid)) {
//                            ModelPosts modelPost = childsnapshot.getValue(ModelPosts.class);
//                            posts.add(modelPost);
//                        }
//
//                    }
//                }
//                adapterPosts = new AdapterPosts(getActivity(), posts);
//                postrecycle.setAdapter(adapterPosts);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }



}