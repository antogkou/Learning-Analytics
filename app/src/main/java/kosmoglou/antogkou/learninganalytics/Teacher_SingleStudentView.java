package kosmoglou.antogkou.learninganalytics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Teacher_SingleStudentView extends AppCompatActivity {

    private static final String TAG = "";


    private Button SaveMarkButton, CancelButton;
    private EditText EnterJavaScore;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    String userid;
    String currentUserID;
    private View mParentLayout;
    DocumentReference dbjavamark;

    private CollectionReference CurrentStudentRef;
    private DocumentReference grade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachersinglestudentview);
        loadingBar = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        CurrentStudentRef = FirebaseFirestore.getInstance().collection("Users");

        Log.d(TAG, "onCreate: started.");
        EnterJavaScore  = (EditText) findViewById(R.id.enterjavascore);
        SaveMarkButton = (Button) findViewById(R.id.save_mark_button);
        CancelButton = (Button) findViewById(R.id.mark_cancel_button);

        SaveMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveStudentMarks();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelAccountSetupInformation();
            }
        });

        EditText enterjavascore  = (EditText) findViewById(R.id.enterjavascore);
        GetAccountInformation();

        String fullname = getIntent().getStringExtra("textFullname");
        String username = getIntent().getStringExtra("textUser_name");
        String userid = getIntent().getStringExtra("textUser_id");

        // Capture the layout's TextView and set the string as its text
        TextView ffullname = (TextView) findViewById(R.id.post_title);
        ffullname.setText(fullname);

        TextView fusername = (TextView) findViewById(R.id.post_date);
        fusername.setText(username);

        TextView fuser_id = (TextView) findViewById(R.id.post_description);
        fuser_id.setText(userid);
    }

    private void GetAccountInformation(){

        String userid = getIntent().getStringExtra("textUser_id");
        grade = FirebaseFirestore.getInstance().collection("Users").document(userid);
        EditText enterjavascore  = (EditText) findViewById(R.id.enterjavascore);




        grade.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String grade = documentSnapshot.getString("Mark_Java");

                            enterjavascore.setHint(grade);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Exception: ", e.getMessage());
                    }
                });
    }
    /*private void getStudentinfo(){
        String userid = getIntent().getStringExtra("textUser_name");
        Query query1 = db.collection("Users").whereEqualTo("userid", userid);
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Created new note");
                }
                else{
                    makeSnackBarMessage("Query failed, Check logs.");
                }
            }

        });
    }*/




    private void CancelAccountSetupInformation() {
        SendUserToViewAllStudents();

    }

    private void SendUserToViewAllStudents() {
        Intent mainIntent = new Intent(Teacher_SingleStudentView.this, TeacherViewStudents.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void makeSnackBarMessage(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void SaveStudentMarks() {

        String javascore = EnterJavaScore.getText().toString();

        String userid = getIntent().getStringExtra("textUser_id");

        if (TextUtils.isEmpty(javascore)) {
            Toast.makeText(this, "javascore field is empty", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, your mark is being saved on server..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            Map<String, Object> mark = new HashMap<>();
            mark.put("Mark_Java", javascore);

            CurrentStudentRef.document(userid)
                    .set(mark, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Mark successfully written for " + userid + "! ");
                            Toast.makeText(Teacher_SingleStudentView.this, "Saved!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Log.w(TAG, "Error writing mark", e);
                        }
                    });
        }
    }



}