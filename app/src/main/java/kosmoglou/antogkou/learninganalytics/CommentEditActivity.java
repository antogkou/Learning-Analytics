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

public class CommentEditActivity extends AppCompatActivity {

    Button submit;
    EditText edit_comment;
    public static String TAG = "MainActivity";
    private FirebaseFirestore db;
    private CollectionReference postscollection, commentcollection;
    private DocumentReference commentscollection;
    String currentUserID, documentId;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;



    @Override
    protected void onStart() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        String comment = getIntent().getStringExtra("comment_text");
        edit_comment.setText(comment);
        String document_id = getIntent().getStringExtra("post_doc_id");
        String comment_id = getIntent().getStringExtra("commentID");

        postscollection = db.collection("Posts");
        commentscollection = postscollection.document(document_id);


        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_comment);

        //toolbar and navigation drawer staff
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
        String sedit_comment = edit_comment.getText().toString();
        String document_id = getIntent().getStringExtra("post_doc_id");
        String comment_id = getIntent().getStringExtra("commentID");
        commentscollection = postscollection.document(document_id);

        if(sedit_comment.isEmpty()){
            Toast.makeText(CommentEditActivity.this, "Text is required", Toast.LENGTH_SHORT).show();
        }else {

            commentscollection.collection("Comments").document(comment_id)
                    .update("comment", sedit_comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CommentEditActivity.this, "Edit was successful!", Toast.LENGTH_SHORT).show();
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

    private void DeleteComment() {
        String comment_id = getIntent().getStringExtra("commentID");
        String document_id = getIntent().getStringExtra("post_doc_id");

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("Posts");
        CollectionReference commentsRef = itemsRef.document(document_id).collection("Comments");



        Query qid = commentsRef.whereEqualTo("commentID", comment_id);
        qid.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        commentscollection.collection("Comments").document(comment_id).delete();
                        finish();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initviews() {
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        submit = (Button)findViewById(R.id.submit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.commentedit_menu, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_comment:
                new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                DeleteComment();
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


}
