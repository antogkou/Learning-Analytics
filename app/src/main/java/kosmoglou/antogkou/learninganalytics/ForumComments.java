package kosmoglou.antogkou.learninganalytics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kosmoglou.antogkou.learninganalytics.Models.CommentsModel;
import kosmoglou.antogkou.learninganalytics.Models.ForumModel;

public class ForumComments extends AppCompatActivity {

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
    private CollectionReference CurrentPost, postscollection, userscollection,profileinfo;
    private DocumentReference commentscollection,currentpostsdoc,currentUserPath,currentcomment;
    private Toolbar mToolbar;

    EditText edit, comment_text;
    Button button_delete_title, button_add_comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        //toolbar and navigation drawer staff
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingBar = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        CurrentPost = FirebaseFirestore.getInstance().collection("Posts");
        ButterKnife.bind(this);


        mAuth= FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        currentUserPath = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);

      //  CheckIfUserIsAllowedToEditPosts();
        init();
        getCommentsList();






        Log.d(TAG, "onCreate: started.");

        /*button_delete_title = (Button) findViewById(R.id.button_delete_post);

        button_delete_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeletePost();
            }
        });*/

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
        String creator_userID = getIntent().getStringExtra(" textCreator_UserID");

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
        //smooth

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

        adapter = new FirestoreRecyclerAdapter<CommentsModel, ForumComments.CommentsHolder>(response) {
            @Override
            public void onBindViewHolder(ForumComments.CommentsHolder holder, int position, CommentsModel model) {
                progressBar.setVisibility(View.GONE);

                holder.textComment.setText(model.getComment());

                try {
                    Date date = model.getComment_date();
                    if (date != null) {
                        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
                        String creationDate = dateFormat.format(date);
                        holder.textTime.setText(creationDate);
                        Log.d("TAG", creationDate);
                    }
                    //holder.textTime.setText(model.getComment_date().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.post_commenter_fullname.setText(model.getFullname());
                holder.itemView.setTag(model.getCommentID());


                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //allow only the user that created the comment to edit it
                        if( currentUserID.equals(model.getUser_id())  ) {
                            //carry strings to editcommentactivity
                            Intent intent = new Intent(getApplicationContext(), CommentEditActivity.class);
                            intent.putExtra("comment_text", model.getComment());
                            intent.putExtra("post_doc_id", id);
                            intent.putExtra("commentID", model.getCommentID());
                            startActivity(intent);
                        }

                        return true;
                    }
                });



                //query comments
                postscollection = db.collection("Posts");
                userscollection = db.collection("Users");
                currentpostsdoc = postscollection.document(id);

            }

            @Override
            public ForumComments.CommentsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_commentsitems, group, false);

                return new ForumComments.CommentsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        //swipe to delete comments
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


                //check if user is allowed to delete comments
                currentUserPath.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.getString("usertype").equals("Admin") || documentSnapshot.getString("usertype").equals("Teacher") ){
                                    final int position = viewHolder.getAdapterPosition();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForumComments.this, R.style.AlertDialogStyle); //alert for confirm to delete
                                    builder.setMessage("Are you sure you want this comment to be deleted?");    //set message
                                    builder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            commentlist.getAdapter().notifyItemRemoved(position);
                                            //swipe to delete
                                            String commentID = (String) viewHolder.itemView.getTag();
                                            currentpostsdoc.collection("Comments")
                                                    .document(commentID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "onSuccess: Removed list item");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                                        }
                                                    });
                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            clearView(commentlist, viewHolder);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });


                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.setCanceledOnTouchOutside(false);

                                }else{
                                    //else
                                    adapter.notifyDataSetChanged();
                                    commentlist.setAdapter(adapter);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });




            }

        }).attachToRecyclerView(commentlist);
        //swipe to delete comments end


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
        String id = getIntent().getStringExtra("textDocumentId");

        final String current_user_id = mAuth.getCurrentUser().getUid();
        postscollection = db.collection("Posts");
        commentscollection = postscollection.document(id);

        if(scomment.isEmpty()) {
            Toast.makeText(ForumComments.this, "You didn't input any text!", Toast.LENGTH_SHORT).show();
        }else {
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

                                            Query queryusers = userscollection.whereEqualTo("user_id", current_user_id);
                                            queryusers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {

                                                            String commenters_name = document.getString("fullname");
                                                            commentscollection.collection("Comments").document(commentid)
                                                                    .update("fullname", commenters_name)
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
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String creator_userID = getIntent().getStringExtra("textCreator_UserID");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comments_menu, menu);
        //check if user is an admin/teacher and allow him to access the hidden menu
        MenuItem deletePost = menu.findItem(R.id.delete_post);
        MenuItem editPost = menu.findItem(R.id.edit_post);
        currentUserPath.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("usertype").equals("Admin") || documentSnapshot.getString("usertype").equals("Teacher") || creator_userID.equals(currentUserID)){
                            deletePost.setVisible(true);
                            editPost.setVisible(true);
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
                        .setTitle("Delete entry")
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

            case R.id.edit_post:
                EditPost();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void EditPost() {
        String creator_userID = getIntent().getStringExtra("textCreator_UserID");
        String title = getIntent().getStringExtra("textTitle");
        String description = getIntent().getStringExtra("textDescription");
        String id = getIntent().getStringExtra("textDocumentId");


         if( currentUserID.equals(creator_userID)  ) {
        //carry strings to editPostActivity
        Intent intent = new Intent(getApplicationContext(), PostEditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("textTitle", title);
        intent.putExtra("textDescription",description);
        intent.putExtra("textDocumentId", id);
        startActivity(intent);
        finish();
         }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}