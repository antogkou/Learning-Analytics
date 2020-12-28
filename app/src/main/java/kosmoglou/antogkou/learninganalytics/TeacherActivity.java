package kosmoglou.antogkou.learninganalytics;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class TeacherActivity extends AppCompatActivity {
    private Button gobackbutton;
    private ImageButton button_allstudents,button_forum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);


        button_allstudents = (ImageButton) findViewById(R.id.button_allstudents);
        button_forum = (ImageButton) findViewById(R.id.button_forum);

        button_allstudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendTeacherToAllStudents();
            }
        });

        button_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendTeacherToForum();
            }
        });

    }



    private void GoBack() {
        SendUserToMainActivity();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent (TeacherActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendTeacherToAllStudents() {
        Intent mainIntent = new Intent(TeacherActivity.this, TeacherViewStudents.class);
        startActivity(mainIntent);
    }

    private void SendTeacherToForum() {
        Intent mainIntent = new Intent(TeacherActivity.this, Forum.class);
        startActivity(mainIntent);
    }

}
