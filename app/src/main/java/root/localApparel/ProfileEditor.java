package root.localApparel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    StorageReference storageRef;
    DatabaseReference databaseRef;

    ProgressDialog window;

    String picStorage = "Users_profile_pic_path/";
    TextView password, email, picture, name;

    String uid, newProfilePic;
    ImageView pics;
    ImageView userpic, temp;
    TextView changePW, changeName, changeEmail;
    Uri imageuri;

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
        uid = FirebaseAuth.getInstance().getUid();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("Users");
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(uid)) {
                        String updatePic = "" +  snap.child("image").getValue();
                        String name1 = "" + snap.child("name").getValue().toString();
                        String email1 = "" + snap.child("email").getValue().toString();

                        try {
                            if (updatePic == "") {

                            } else {
                                Glide.with(ProfileEditor.this).load(updatePic).into(userpic);
                            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGEPICK_GALLERY_REQUEST) {
            imageuri = data.getData();
            window.show();


            userpic.setImageURI(imageuri);

            String filepathname = picStorage + "" + newProfilePic + "_" + firebaseUser.getUid();
            StorageReference storageReference1 = storageRef.child(filepathname);
            storageReference1.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;

                    final Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(newProfilePic, downloadUri.toString());
                        databaseRef.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                window.dismiss();
                                Toast.makeText(ProfileEditor.this, "Picture succesfully loaded", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                window.dismiss();
                                Toast.makeText(ProfileEditor.this, "Picture failed to load ", Toast.LENGTH_LONG).show();
                            }
                        });
                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Posts");
                        df.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot i: snapshot.getChildren()){
                                    for (DataSnapshot j: i.getChildren()){
                                        if (j.child("uid").getValue().equals(uid)){
                                            DatabaseReference k = j.child("udp").getRef();
                                            k.setValue(hashMap.get(newProfilePic));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        window.dismiss();
                        Toast.makeText(ProfileEditor.this, "Failure uploading Picture .1", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    window.dismiss();
                    Toast.makeText(ProfileEditor.this, "Failure uploading Picture .2", Toast.LENGTH_LONG).show();
                }
            });
            window.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.fragment_profile_editor);

        window = new ProgressDialog(this);
        window.setCanceledOnTouchOutside(false);

        changeName = findViewById(R.id.name_prof);
        userpic = findViewById(R.id.userPic);
        changeEmail = findViewById(R.id.email_prof);
        uid = FirebaseAuth.getInstance().getUid();

        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        picture = findViewById(R.id.picture);
        name = findViewById(R.id.name);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("Users");
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(uid)) {
                        String updatePic = "" +  snap.child("image").getValue();
                        String name1 = "" + snap.child("name").getValue().toString();
                        String email1 = "" + snap.child("email").getValue().toString();

                        try {
                            if (updatePic == "") {

                            } else {
                                Glide.with(ProfileEditor.this).load(updatePic).into(userpic);
                            }
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

        name.setOnClickListener(new View.OnClickListener() { //name change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Name");
                nameUpdator("name");
            }
        });

        email.setOnClickListener(new View.OnClickListener() { //email change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Email");
                emailUpdator("email");
            }
        });

        picture.setOnClickListener(new View.OnClickListener() { //pic change
            @Override
            public void onClick(View view) {
                window.setMessage("Change Profile Picture");
                newProfilePic = "image";
                if (!storagePermissionVerification()) {
                    reqStoragePerm();
                } else {
                    Intent gallReqIntent = new Intent(Intent.ACTION_PICK);
                    gallReqIntent.setType("image/*");
                    startActivityForResult(gallReqIntent, IMAGEPICK_GALLERY_REQUEST);
                }
            }
        });

        password.setOnClickListener(new View.OnClickListener() { //password change
            @Override
            public void onClick(View v) {
                window.setMessage("Change Password");
                pwUpdator("Password");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        setContentView(R.layout.fragment_profile_editor);

        window = new ProgressDialog(this);
        window.setCanceledOnTouchOutside(false);

        changeName = findViewById(R.id.name_prof);
        userpic = findViewById(R.id.userPic);
        changeEmail = findViewById(R.id.email_prof);
        uid = FirebaseAuth.getInstance().getUid();

        picture = findViewById(R.id.picture);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseRef = firebaseDatabase.getReference("Users");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(uid)) {
                        String updatePic = "" +  snap.child("image").getValue();
                        String name1 = "" + snap.child("name").getValue().toString();
                        String email1 = "" + snap.child("email").getValue().toString();

                        try {
                            if (updatePic == "") {

                            } else {
                                Glide.with(ProfileEditor.this).load(updatePic).into(userpic);
                            }
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

    private void nameUpdator(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change " + key);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(this);
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
                } else {
                    Toast.makeText(ProfileEditor.this, " Error on Name Update .2 ", Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                window.dismiss();
            }
        });
        builder.create().show();
    }

    private void emailUpdator(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change " + key);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(this);
        editText.setHint("Enter new " + key);
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
                            Toast.makeText(ProfileEditor.this, " Email Updated ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            window.dismiss();
                            Toast.makeText(ProfileEditor.this, " Error on Email Update .1 ", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileEditor.this, " Error on Email Update .2 ", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                window.dismiss();
            }
        });
        builder.create().show();
    }

    private void pwUpdator(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change " + key);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        final EditText oldPW = new EditText(this);
        final EditText newPW = new EditText(this);
        oldPW.setHint("Enter old " + key);
        layout.addView(oldPW);

        newPW.setHint("Enter new " + key);
        layout.addView(newPW);

        builder.setView(layout);


        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String oldVal = oldPW.getText().toString().trim();
                final String newVal = newPW.getText().toString().trim();
                if (TextUtils.isEmpty(oldVal) || TextUtils.isEmpty(newVal)) {
                    Toast.makeText(ProfileEditor.this, "Missing inputs for old and new password inputs", Toast.LENGTH_LONG).show();
                    return;
                }

                // verify old vs new password and place into firebase
                final FirebaseUser curr = firebaseAuth.getCurrentUser();
                AuthCredential authCredential = EmailAuthProvider.getCredential(curr.getEmail(), oldVal);
                curr.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        curr.updatePassword(newVal).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                window.dismiss();
                                Toast.makeText(ProfileEditor.this, "Changed Password", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                window.dismiss();
                                Toast.makeText(ProfileEditor.this, "Failed to change Password .1", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        window.dismiss();
                        Toast.makeText(ProfileEditor.this, "Failed to change Password .2", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                window.dismiss();
            }
        });
        builder.create().show();

    }

    private Boolean storagePermissionVerification() {
        boolean permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return permCheck;
    }

    private void reqStoragePerm() {
        requestPermissions(storagePerm, STORAGE_REQUEST);
    }

}


