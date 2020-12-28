package kosmoglou.antogkou.learninganalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import kosmoglou.antogkou.learninganalytics.Models.PostsModel;

public class PostsActivity extends AppCompatActivity {

    Button submit;
    EditText title, description;
    private RecyclerView.LayoutManager layoutManager;
    public static String TAG = "MainActivity";
    private FirebaseFirestore mFirestore;
    private DatabaseReference UsersRef;
    private DocumentReference profileinfo;
    String currentUserID, documentId;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    @Override
    protected void onStart() {

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        super.onStart();
        profileinfo = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        //toolbar and navigation drawer staff
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase
        mFirestore = FirebaseFirestore.getInstance();

        initviews();

        //button listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_post();
            }
        });

    }





    private void create_post() {
        
        String screatoruserid = mAuth.getCurrentUser().getUid();
        String stitle = title.getText().toString();
        String sdescription = description.getText().toString();


        if(stitle.isEmpty()|| sdescription.isEmpty()){
            Toast.makeText(PostsActivity.this, "Both fields Required", Toast.LENGTH_SHORT).show();
        }else {
            PostsModel postsModel = new PostsModel(stitle, sdescription, screatoruserid, documentId);

            mFirestore.collection("Posts")
                    .add(postsModel)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            title.setText("");
                            description.setText("");

                            String docid = documentReference.getId().toString();

                            mFirestore.collection("Posts").document(docid)
                                    .update("documentId", docid)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "evala to id!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating documentid", e);
                                        }
                                    });
                            //end activity after posting
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });


        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initviews() {
        title = (EditText) findViewById(R.id.edit_title);
        description = (EditText)findViewById(R.id.description);
        submit = (Button)findViewById(R.id.submit);

    }


}
