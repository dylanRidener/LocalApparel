package root.localApparel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

public class ProfileEditor extends AppCompatActivity {
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    StorageReference storageRef;
    DatabaseReference databaseRef;

    ProgressDialog window;
    
//    String picStorage
    String uid, newProfilePic;
//    ImageView pics;
    ImageView userpic, temp;
    TextView changePW, changeName, changeEmail;



    String storagePerm[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_editor);

        window = new ProgressDialog(this);
        window.setCanceledOnTouchOutside(false);

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

        changeEmail.setText(firebaseUser.getEmail());

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

        changeName.setOnClickListener(new View.OnClickListener() { //name change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Name");
                nameUpdator("name");
            }
        });
        
        changeEmail.setOnClickListener(new View.OnClickListener() { //email change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Email");
                emailUpdator("email");
            }
        });

        userpic.setOnClickListener(new View.OnClickListener() { //pic change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Profile Picture");
                newProfilePic = "image";
                picUpdator();
            }
        });

        changePW.setOnClickListener(new View.OnClickListener() { //password change
            @Override
            public void onClick(View v) {
                window.setMessage("Change Password");
                pwUpdator();
            }
        });
    }

    private void nameUpdator(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change " + key);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText editText = new EditText(this);
        layout.setPadding(10, 10, 10, 10);
        editText.setHint("Enter " + key);
        layout.addView(editText);
        builder.setView(layout);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    window.show();

                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseRef.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            window.dismiss();

                            // after updated we will show updated
                            Toast.makeText(ProfileEditor.this, " Name Updated ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            window.dismiss();
                            Toast.makeText(ProfileEditor.this, " Error on Name Update .1 ", Toast.LENGTH_LONG).show();
                        }
                    });

                    if (key.equals("name")) {
                        final DatabaseReference dbPointer = FirebaseDatabase.getInstance().getReference("Posts");

                        dbPointer.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap1: snapshot.getChildren()) {
                                    for (DataSnapshot snap2: snap1.getChildren()) {
                                        if (snap2.child("uid").getValue().equals(uid)) {
                                            snap2.child("uname").getRef().setValue(value);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileEditor.this, " Error on Name Update .2 ", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void emailUpdator(final String key) {

    }
    private void picUpdator() {

    }
    private void pwUpdator() {
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);

    }

    private Boolean storagePermissionVerification() {
        boolean permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return permCheck;
    }

    private void reqStoragePerm() {
        requestPermissions(storagePerm, STORAGE_REQUEST);
    }

}

