package kosmoglou.antogkou.learninganalytics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PostEditActivity extends AppCompatActivity {

    Button submit;
    EditText edit_post_title, edit_post_description;
    public static String TAG = "MainActivity";
    private FirebaseFirestore db;
    private CollectionReference postscollection;
    private DocumentReference currentpost,currentUserPath
            ;
    String currentUserID, documentId;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;



    @Override
    protected void onStart() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        String title = getIntent().getStringExtra("textTitle");
        String description = getIntent().getStringExtra("textDescription");
        String post_id = getIntent().getStringExtra("textDocumentId");
        edit_post_title.setText(title);
        edit_post_description.setText(description);

        postscollection = db.collection("Posts");
        currentpost = postscollection.document(post_id);


        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post);

        //toolbar and navigation drawer staff
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth= FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        currentUserPath = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);
        initviews();

        //button listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_comment();
            }
        });

    }





    private void edit_comment() {

        String screatoruserid = mAuth.getCurrentUser().getUid();
        String sedit_post_title = edit_post_title.getText().toString();
        String sedit_post_description = edit_post_description.getText().toString();
        String post_id = getIntent().getStringExtra("textDocumentId");

        //currentpost = postscollection.document(post_id);

        if(sedit_post_title.isEmpty() || sedit_post_description.isEmpty()){
            Toast.makeText(PostEditActivity.this, "Text is required", Toast.LENGTH_SHORT).show();
        }else {

            db.collection("Posts").document(post_id)
                    .update("title", sedit_post_title)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PostEditActivity.this, "Edit was successful!", Toast.LENGTH_SHORT).show();
                            db.collection("Posts").document(post_id)
                                    .update("description", sedit_post_description)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(PostEditActivity.this, "Edit was successful!", Toast.LENGTH_SHORT).show();
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error editing comment", e);
                                        }
                                    });
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error editing comment", e);
                        }
                    });


        }

    }

    private void DeletePost() {



        String id = getIntent().getStringExtra("textDocumentId");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("Posts");

        Query qid = itemsRef.whereEqualTo("documentId", id);
        qid.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                        finish();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comments_menu, menu);
        String creator_userID = getIntent().getStringExtra("textCreator_UserID");
        //check if user is an admin/teacher and allow him to access the hidden menu
        MenuItem deletePost = menu.findItem(R.id.delete_post);
        currentUserPath.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("usertype").equals("Admin") || documentSnapshot.getString("usertype").equals("Teacher") || creator_userID==creator_userID){
                            deletePost.setVisible(true);
                        }else{

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_post:
                new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                DeletePost();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initviews() {
        edit_post_title = (EditText) findViewById(R.id.edit_post_title);
        edit_post_description = (EditText) findViewById(R.id.edit_post_description);
        submit = (Button)findViewById(R.id.submit);
    }



}
