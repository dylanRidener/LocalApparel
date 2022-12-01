package root.localApparel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddClothes extends Fragment {
    public AddClothes() {
        // Required empty public constructor
    }

    Button postRent;
    Button postBuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clothing_item, container, false);

        postRent = view.findViewById(R.id.PostRent);
        postRent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment addFrag = new SellingClosetFragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.content, addFrag).commit();
            }
        });

        postBuy = view.findViewById(R.id.PostBuy);
        postBuy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment addFrag = new SellingClosetFragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.content, addFrag).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
