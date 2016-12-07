package app.psiteportal.com.psiteportal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 10/11/2016.
 */
public class ConfigurationActivity extends AppCompatActivity {

    EditText editText;
    Button btnChange;
    Button btnDefault;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_layout);

        editText = (EditText) findViewById(R.id.edit_root);
        btnChange = (Button) findViewById(R.id.btnConfigure);
        btnDefault = (Button) findViewById(R.id.btnDefaultUrl);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String root = editText.getText().toString();
                Config.ROOT_URL = root;
            }
        });

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String root = "http://psite7.usjr.edu.ph/";
                Config.ROOT_URL = root;
            }
        });
    }
}
