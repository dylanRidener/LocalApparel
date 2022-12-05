package root.localApparel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddClothes extends Fragment {
    public AddClothes() {
        // Required empty public constructor
    }

    FirebaseAuth firebaseAuth;
    EditText title, des, price;
    private static final int STORAGE_REQUEST = 1000;
    private static final int IMAGEPICK_GALLERY_REQUEST = 2000;
    String storagePermission[];
    ProgressDialog pd;
    ImageView image;

    Uri imageuri = null;
    String name, email, uid, dp;
    DatabaseReference databaseReference;
    Button uploadRent, uploadBuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.clothing_item, container, false);

        uid = FirebaseAuth.getInstance().getUid();

        title = view.findViewById(R.id.itemTitle);
        des = view.findViewById(R.id.Des_box);
        image = view.findViewById(R.id.clothesView);
        price = view.findViewById(R.id.price_box);
        uploadRent = view.findViewById(R.id.PostRent);
        uploadBuy = view.findViewById(R.id.PostBuy);
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);
        Intent intent = getActivity().getIntent();

        // Retrieving the user data like name ,email and profile pic using query
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (snap.child("uid").getValue().equals(uid)) {
                        if (snap.child("image").getValue() != null) {
                            dp = "" + snap.child("image").getValue().toString();
                        }
                        name = snap.child("name").getValue().toString();
                        email = "" + snap.child("email").getValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        image.setOnClickListener(new View.OnClickListener() { //pic change
            @Override
            public void onClick(View view) {
                pd.setMessage("Change Profile Picture");
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    Intent gallReqIntent = new Intent(Intent.ACTION_PICK);
                    gallReqIntent.setType("image/*");
                    startActivityForResult(gallReqIntent, IMAGEPICK_GALLERY_REQUEST);
                }
            }
        });

        uploadRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titl = "" + title.getText().toString().trim();
                String description = "" + des.getText().toString().trim();
                String item_price = "" + price.getText().toString().trim();
                if (TextUtils.isEmpty(titl)) {
                    title.setError("Title Cant be empty");
                    Toast.makeText(getContext(), "Title can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    des.setError("Description Cant be empty");
                    Toast.makeText(getContext(), "Description can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (imageuri == null) {
                    Toast.makeText(getContext(), "Select an Image", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    uploadData(titl, description,item_price, "Rent");
                }
            }
        });

        uploadBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titl = "" + title.getText().toString().trim();
                String description = "" + des.getText().toString().trim();
                String item_price = "" + price.getText().toString().trim();
                if (TextUtils.isEmpty(titl)) {
                    title.setError("Title Cant be empty");
                    Toast.makeText(getContext(), "Title can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    des.setError("Description Cant be empty");
                    Toast.makeText(getContext(), "Description can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (imageuri == null) {
                    Toast.makeText(getContext(), "Select an Image", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    uploadData(titl, description,item_price, "Buy");
                }
            }
        });
        return view;
    }

    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private void uploadData(final String titl, final String description, String item_price, String buyOrRent) {
        pd.setMessage("Publishing Post");
        pd.show();
        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filepathname = "Posts/" + "post" + timestamp;
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(filepathname);
        storageReference1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();
                if (uriTask.isSuccessful()) {
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("uname", name);
                    hashMap.put("uemail", email);
                    hashMap.put("udp", dp);
                    hashMap.put("title", titl);
                    hashMap.put("description", description);
                    hashMap.put("uimage", downloadUri);
                    hashMap.put("ptime", timestamp);
                    hashMap.put("price", item_price);
                    hashMap.put("buyOrRent", buyOrRent);
                    hashMap.put("addressToSend", "");
                    hashMap.put("purchased", "No");
                    hashMap.put("buyer", "");

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                    databaseReference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "Published", Toast.LENGTH_LONG).show();
                                    title.setText("");
                                    des.setText("");
                                    price.setText("");
                                    image.setImageURI(null);
                                    imageuri = null;
                                    Fragment addFrag = new SellingClosetFragment();
                                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                                    fm.replace(R.id.content, addFrag).commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == getActivity().RESULT_OK && requestCode == IMAGEPICK_GALLERY_REQUEST) {
            imageuri = data.getData();
            image.setImageURI(imageuri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}