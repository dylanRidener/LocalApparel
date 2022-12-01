package root.localApparel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class HelpPage extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_page, container, false);

        // Inflate the layout for this fragment
        Button store = view.findViewById(R.id.SubmitButton);
        EditText editText = view.findViewById(R.id.helper_comment);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        return view;
    }
}


