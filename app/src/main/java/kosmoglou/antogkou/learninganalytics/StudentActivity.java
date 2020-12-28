package kosmoglou.antogkou.learninganalytics;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StudentActivity extends AppCompatActivity {
    private Button gobackstudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);


        gobackstudent = (Button) findViewById(R.id.gobackstudent);
        gobackstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoBack();
            }
        });

    }


    private void GoBack() {
        SendUserToMainActivity();

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(StudentActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



}
