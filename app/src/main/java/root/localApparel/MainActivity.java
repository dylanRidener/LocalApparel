package root.localApparel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
//    MapsFragment mapFragged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    @Override
//    public void oonRequestPermissionsResult(int code, String perms[], int[] grants) {
//        if (code == mapFragged.MY_PERMISSIONS_REQUEST_LOCATION) {
//            mapFragged.onRequestPermissionsResult(code, perms, grants);
//        } else {
//            super.onRequestPermissionsResult(code, perms, grants);
//        }
//    }

}