package root.localApparel;

import android.Manifest;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileEditor extends AppCompatActivity {
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    StorageReference storageRef;
    DatabaseReference databaseRef;
    
//    String picStorage
    String uid;
//    ImageView pics;
    ImageView userpic, temp;
    TextView changePW, changeName, changeEmail;



    String storagePerm[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_editor);

        changeName = findViewById(R.id.name_prof);
        userpic = findViewById(R.id.userPic);
        changeEmail = findViewById(R.id.email_prof);
        changePW = findViewById(R.id.password);
        uid = FirebaseAuth.getInstance().getUid();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("Users");
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Query query = databaseRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(uid)) {
                        String updatePic = "" + snap.child("image").getValue();
                        String name1 = "" + snap.child("name").getValue().toString();
                        String email1 = "" + snap.child("email").getValue().toString();

                        try {
                            Glide.with(ProfileEditor.this).load(updatePic).into(userpic);
                        } catch (Exception error) {

                        }
                        changeName.setText(name1);
                        changeEmail.setText(email1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

