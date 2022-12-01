package root.localApparel;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellingClosetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellingClosetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SellingClosetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellingClosetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellingClosetFragment newInstance(String param1, String param2) {
        SellingClosetFragment fragment = new SellingClosetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selling_closet, container, false);

//        Button addBtn = (Button) rootView.findViewById(R.id.AddButton);
//
//        addBtn.setOnClickListener((View.OnClickListener) this);
//
//
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v)
//            {
//                // Launching new Activity on selecting single List Item
//                Intent i = new Intent(getActivity(), AddClothes.class);
//                startActivity(i);
//            }
//        });

        // Inflate the layout for this fragment
        return rootView;
    }

//    public void onClick(View view) {
//        Fragment fragment = null;
//        switch (view.getId()) {
//            case R.id.AddButton:
//                AddClothes fragment1 = new AddClothes();
//                FragmentTransaction fragmentTransaction3 = getFragmentManager().beginTransaction();
//                fragmentTransaction3.replace(R.id.AddButton, fragment1, "");
//                fragmentTransaction3.commit();
//                break;
//        }
//    }

}