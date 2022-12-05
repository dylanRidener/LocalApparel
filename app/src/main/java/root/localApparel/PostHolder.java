package root.localApparel;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostHolder extends RecyclerView.Adapter<PostHolder.MyHolder> {

    Context context;
    ViewGroup source;
    String myuid, tag;
    private DatabaseReference postref;

    public PostHolder(Context context, List<PostObjects> postObjects) {
        this.context = context;
        this.postObjects = postObjects;
<<<<<<< HEAD
=======
        //window = new ProgressDialog(context);
       // window.setCanceledOnTouchOutside(false);
>>>>>>> 4c5a2d7918ec6471c04e061dc65ab24e788aa250
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    List<PostObjects> postObjects;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        source = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.selling_posts, parent, false);
        tag = (String) view.getTag();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String uid = postObjects.get(position).getUid();
        String nameh = postObjects.get(position).getUname();
        final String items_price = postObjects.get(position).getPrice();
        final String titlee = postObjects.get(position).getTitle();
        final String descri = postObjects.get(position).getDescription();
        final String ptime = postObjects.get(position).getPtime();
        final String buyOrRent = postObjects.get(position).getbuyOrRent();
        String dp = postObjects.get(position).getUdp();
        final String image = postObjects.get(position).getUimage();
        holder.name.setText(nameh);
        holder.title.setText(titlee);
        holder.description.setText(descri);
        holder.price.setText("$ " + items_price);
        try {
            Glide.with(context).load(dp).into(holder.picture);
        } catch (Exception e) {

        }
        holder.image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(image).into(holder.image);
        } catch (Exception e) {

        }


        holder.clickPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (myuid.equals(uid)) {
                    sellClosetHandler(ptime, image);
                } else if (buyOrRent.equals("Rent")) {
                    rentHandler(ptime, image);
                } else { //current layout must be in buy
                    buyHandler(ptime, image);
                }
            }
        });

    }

    private void buyHandler(String ptime, String image){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Buying");
        builder.setMessage("Would you like to buy this item?");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ask for address and save
                //switch purchase state
                //set post's buyer's ID
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
                postRef.child(ptime).child("purchased").setValue("Yes");
                postRef.child(ptime).child("buyer").setValue(myuid);

                AlertDialog.Builder address = new AlertDialog.Builder(context);
                builder.setTitle("Address");

                LinearLayout layout2 = new LinearLayout(context);
                layout2.setOrientation(LinearLayout.VERTICAL);
                layout2.setPadding(10,10,10,10);

                final EditText name = new EditText(context);
                name.setHint("Enter Your Name");
                layout2.addView(name);
                final EditText Street = new EditText(context);
                Street.setHint("Enter Street Address");
                layout2.addView(Street);
                final EditText City = new EditText(context);
                City.setHint("Enter City");
                layout2.addView(City);
                final EditText State = new EditText(context);
                State.setHint("Enter State");
                layout2.addView(State);
                final EditText ZipCode = new EditText(context);
                ZipCode.setHint("Enter Zipcode");
                layout2.addView(ZipCode);

                builder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String nameStr = name.getText().toString().trim();
                        final String streetStr = Street.getText().toString().trim();
                        final String cityStr= City.getText().toString().trim();
                        final String stateStr = State.getText().toString().trim();
                        final String zipStr = ZipCode.getText().toString().trim();
                        if (TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(streetStr) || TextUtils.isEmpty(cityStr) || TextUtils.isEmpty(stateStr) || TextUtils.isEmpty(zipStr)) {
                            Toast.makeText(context, "Missing inputs to complete address", Toast.LENGTH_LONG).show();
                            return;
                        }
                        final String addressComplete = "" + nameStr + ": " + streetStr + ", " + cityStr + ", " + stateStr + ", " + zipStr;
                        postRef.child(ptime).child("addressToSend").setValue(addressComplete);
                    }
                });

                Toast.makeText(context, " Item Purchased ", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void rentHandler(String ptime, String image){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Renting");
        builder.setMessage("Would you like to rent this item?");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
                postRef.child(ptime).child("purchased").setValue("Yes");
                Toast.makeText(context, " Item Rented ", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void addressBlock() {

    }

    private void sellClosetHandler(String ptime, String image){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Post?");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        builder.setView(layout);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePost(ptime, image);
                Toast.makeText(context, " Post Deleted ", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void deletePost(final String ptime, String image) {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");

        postRef.child(ptime).removeValue();
        picRef.delete();
    }

    @Override
    public int getItemCount() {
        return postObjects.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        LinearLayout clickPost;
        TextView name, title, description, price;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picturetv);
            image = itemView.findViewById(R.id.pimagetv);
            name = itemView.findViewById(R.id.unametv);
            title = itemView.findViewById(R.id.ptitletv);
            description = itemView.findViewById(R.id.descript);
            price = itemView.findViewById(R.id.pricetv);
            profile = itemView.findViewById(R.id.profilelayout);
            clickPost = itemView.findViewById(R.id.clickPost);
        }
    }
}
