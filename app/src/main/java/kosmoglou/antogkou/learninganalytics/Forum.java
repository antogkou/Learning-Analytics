package kosmoglou.antogkou.learninganalytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kosmoglou.antogkou.learninganalytics.Models.ForumModel;

public class Forum extends AppCompatActivity {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.forum_list)
    RecyclerView forumlist;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    FloatingActionButton floating_add_post;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        ButterKnife.bind(this);
        mAuth= FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        init();
        getForumList();

        FloatingActionButton floating_add_post = (FloatingActionButton) findViewById(R.id.floating_add_post);

        floating_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_post_activity();
            }
        });
    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        forumlist.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getForumList(){
        Query query = db.collection("Posts")
                .orderBy("postdate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ForumModel> response = new FirestoreRecyclerOptions.Builder<ForumModel>()
                .setQuery(query, ForumModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ForumModel, ForumHolder>(response) {
            @Override
            public void onBindViewHolder(ForumHolder holder, int position, ForumModel model) {
                progressBar.setVisibility(View.GONE);
                holder.textTitle.setText(model.getTitle());
                holder.textDescription.setText(model.getDescription());
                //holder.textPostDate.setText(model.getDate());

                Date date = model.getPostdate();
                if (date != null) {
                    DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
                    String creationDate = dateFormat.format(date);
                    holder.textPostDate.setText(creationDate);
                    Log.d("TAG", creationDate);
                }






                holder.itemView.setOnClickListener(v -> {

                    //added code to move strings to new activity
                    Intent intent = new Intent(getApplicationContext(), ForumComments.class);
                    intent.putExtra("textTitle", model.getTitle());
                    intent.putExtra("textDescription", model.getDescription());

                    if (date != null) {
                        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
                        String creationDate = dateFormat.format(date);
                        intent.putExtra("textPostDate", creationDate);
                    }

                    intent.putExtra("textDocumentId", model.getDocumentId());
                    intent.putExtra("textUserID", model.getCurrentUserID());
                    intent.putExtra("textCreator_UserID", model.getCreator_userid());
                    startActivity(intent);

                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //allow only the user that created the post to edit it
                        if( currentUserID.equals(model.getCreator_userid())  ) {
                            //carry strings to editPostActivity
                            Intent intent = new Intent(getApplicationContext(), PostEditActivity.class);
                            intent.putExtra("textTitle", model.getTitle());
                            intent.putExtra("textDescription", model.getDescription());
                            intent.putExtra("textDocumentId", model.getDocumentId());
                            intent.putExtra("textCreator_UserID", model.getCreator_userid());
                            startActivity(intent);
                        }

                        return true;
                    }
                });


            }

            @Override
            public ForumHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_forumitems, group, false);

                return new ForumHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        forumlist.setAdapter(adapter);
    }

    public class ForumHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView(R.id.textPostDate)
        TextView textPostDate;

        public ForumHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    private void new_post_activity() {
        Intent mainIntent = new Intent (Forum.this, PostsActivity.class);
        startActivity(mainIntent);
    }

}
