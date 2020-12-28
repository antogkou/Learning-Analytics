package kosmoglou.antogkou.learninganalytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import kosmoglou.antogkou.learninganalytics.Models.StudentsViewAllModel;

public class TeacherViewStudents extends AppCompatActivity {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.student_list)
    RecyclerView studentlist;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherviewstudents);
        ButterKnife.bind(this);
        init();
        getStudentList();
    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        studentlist.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getStudentList(){
        Query query = db.collection("Users").whereEqualTo("usertype", "Student");

        FirestoreRecyclerOptions<StudentsViewAllModel> response = new FirestoreRecyclerOptions.Builder<StudentsViewAllModel>()
                .setQuery(query, StudentsViewAllModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<StudentsViewAllModel, StudentsHolder>(response) {
            @Override
            public void onBindViewHolder(StudentsHolder holder, int position, StudentsViewAllModel model) {
                progressBar.setVisibility(View.GONE);
                holder.textFullname.setText(model.getFullname());
                holder.textUser_id.setText(model.getUser_id());
                holder.textUsername.setText(model.getUsername());


                holder.itemView.setOnClickListener(v -> {
                    //Snackbar.make(studentlist, model.getFullname()+", "+model.getUser_id()+" at "+model.getUsername(), Snackbar.LENGTH_LONG)
                           //.setAction("Action", null).show();

                    //added code to move strings to new activity
                    Intent intent = new Intent(getApplicationContext(), Teacher_SingleStudentView.class);
                    intent.putExtra("textFullname", model.getFullname());
                    intent.putExtra("textUser_id", model.getUser_id());
                    intent.putExtra("textUser_name", model.getUsername());
                   startActivity(intent);

                });

            }

            @Override
            public StudentsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new StudentsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        studentlist.setAdapter(adapter);
    }

    public class StudentsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textFullname)
        TextView textFullname;
        @BindView(R.id.textUser_id)
        TextView textUser_id;
        @BindView(R.id.textUsername)
        TextView textUsername;

        public StudentsHolder(View itemView) {
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

}
