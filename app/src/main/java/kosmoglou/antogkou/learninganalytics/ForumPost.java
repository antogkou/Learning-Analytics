package kosmoglou.antogkou.learninganalytics;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kosmoglou.antogkou.learninganalytics.Adapters.CustomAdapter;
import kosmoglou.antogkou.learninganalytics.Models.CommentsModel;
import kosmoglou.antogkou.learninganalytics.Models.PostsModel;

public class ForumPost extends AppCompatActivity {

    private static final String TAG = "";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.comments_list)
    RecyclerView commentlist;


    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    String userid;
    String currentUserID, fullname;
    private View mParentLayout;
    DocumentReference commentref,setfullname;

    private CollectionReference CurrentPost, postscollection, userscollection;
    private DocumentReference commentscollection;
    private DocumentReference currentpostsdoc;
    private CollectionReference profileinfo;


    EditText comment, comment_text;
    Button button_delete_title, button_add_comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forumpost);
        loadingBar = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        CurrentPost = FirebaseFirestore.getInstance().collection("Posts");
        ButterKnife.bind(this);

        init();
        getCommentsList();

        Log.d(TAG, "onCreate: started.");

        button_delete_title = (Button) findViewById(R.id.button_delete_post);

        button_delete_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeletePost();
            }
        });

        button_add_comment = (Button) findViewById(R.id.button_add_comment);

        button_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddComment();
            }
        });


        String title = getIntent().getStringExtra("textTitle");
        String description = getIntent().getStringExtra("textDescription");
        String postdate = getIntent().getStringExtra("textPostDate");
        String id = getIntent().getStringExtra("textDocumentId");
        String userid = getIntent().getStringExtra(("textUserID"));

        profileinfo = FirebaseFirestore.getInstance().collection("Posts").document(id).collection("Comments");
      //  String textComment = getIntent().getStringExtra("textComment");


        // Capture the layout's TextView and set the string as its text
        TextView ttitle = (TextView) findViewById(R.id.post_title);
        ttitle.setText(title);

        TextView tdescription = (TextView) findViewById(R.id.post_description);
        tdescription.setText(description);

        TextView tpostdate = (TextView) findViewById(R.id.post_date);
        tpostdate.setText(postdate);

        TextView tid = (TextView) findViewById(R.id.post_doc_id);
        tid.setText(id);

        //Get commment text
        comment_text = (EditText) findViewById(R.id.comment_text);



    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        commentlist.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getCommentsList(){
        String id = getIntent().getStringExtra("textDocumentId");


        postscollection = db.collection("Posts");
        commentscollection = postscollection.document(id);


        Query query = commentscollection.collection("Comments")
                .orderBy("comment_date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CommentsModel> response = new FirestoreRecyclerOptions.Builder<CommentsModel>()
                .setQuery(query, CommentsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CommentsModel, ForumPost.CommentsHolder>(response) {
            @Override
            public void onBindViewHolder(ForumPost.CommentsHolder holder, int position, CommentsModel model) {
                progressBar.setVisibility(View.GONE);

                holder.textComment.setText(model.getComment());
                //  holder.textTime.setText(model.getDate().toString());

                try {
                    holder.textTime.setText(model.getDate().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //holder.post_commenter_fullname.setText(model.getUser_id());


                // holder.textCommenterName.setText(model.getFullname());

                //get commenters name to display in recycler
                TextView post_commenter_fullname = (TextView) findViewById(R.id.post_commenter_fullname);


                //query comments
                postscollection = db.collection("Posts");
                userscollection = db.collection("Users");
                currentpostsdoc = postscollection.document(id);

               /* currentpostsdoc.collection("Comments")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String who_commented = document.getString("user_id");

                                        //start second query query users

                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });*/



                Query querycomments = currentpostsdoc.collection("Comments").whereEqualTo("commentID", commentID);
                querycomments.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String current_comment_user = document.getString("user_id");
                                // holder.post_commenter_fullname.setText(commenters_name);
                                //2
                                Query queryusers = userscollection.whereEqualTo("user_id", current_comment_user);
                                queryusers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {

                                                String commenters_name = document.getString("fullname");
                                                holder.post_commenter_fullname.setText(commenters_name);
                                                //post_commenter_fullname.setText(commenters_name);


                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                                //endof2
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });






            }

            @Override
            public ForumPost.CommentsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_commentsitems, group, false);

                return new ForumPost.CommentsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        commentlist.setAdapter(adapter);
    }


    public class CommentsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textTime)
        TextView textTime;
        @BindView(R.id.textComment)
        TextView textComment;
        @BindView(R.id.post_commenter_fullname)
        TextView post_commenter_fullname;

        public CommentsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    private void AddComment() {

        String scomment = comment_text.getText().toString();
       // String sfullname = fullname.getText().toString();
        String id = getIntent().getStringExtra("textDocumentId");

        //getfullnameandusername
        final String current_user_id = mAuth.getCurrentUser().getUid();

        setfullname = FirebaseFirestore.getInstance().collection("Users").document(current_user_id);
        setfullname.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String xfullname = documentSnapshot.getString("fullname");
                            String xusername = documentSnapshot.getString("username");

                           /* Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("Full Name", fullname);


                            db.collection("Posts").document(id).collection("Comments")
                                    .add(userData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            comment_text.setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });*/
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Exception: ", e.getMessage());
                    }
                });




        postscollection = db.collection("Posts");
        commentscollection = postscollection.document(id);


        Map<String, Object> docData = new HashMap<>();
        docData.put("comment", scomment);
        docData.put("user_id", currentUserID);
        docData.put("comment_date", FieldValue.serverTimestamp());



        db.collection("Posts").document(id).collection("Comments")
                .add(docData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        comment_text.setText("");

                        String commentid = documentReference.getId().toString();

                        commentscollection.collection("Comments").document(commentid)
                                .update("commentID", commentid)
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });



    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}