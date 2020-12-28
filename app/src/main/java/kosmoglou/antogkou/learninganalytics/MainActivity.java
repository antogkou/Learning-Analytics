package kosmoglou.antogkou.learninganalytics;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private CollectionReference UsersRef;
    private DocumentReference setmenudetails,currentUserPath;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//firebase call
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth= FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        try {
            currentUserPath = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);
        }catch(Exception e){}

        UsersRef = FirebaseFirestore.getInstance().collection("Users");

        //Toolbar call
        mToolbar= (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Learning Analytics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//navigation menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        View headerView = navigationView.getHeaderView(0);

        TextView nav_user_full_name = (TextView) headerView.findViewById(R.id.nav_user_full_name);
        TextView nav_user_email = (TextView) headerView.findViewById(R.id.nav_user_email);




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }


        });
    }

    private void SetMenuUserDetails() {

        View headerView = navigationView.getHeaderView(0);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        TextView nav_user_full_name = (TextView) headerView.findViewById(R.id.nav_user_full_name);
        TextView nav_user_email = (TextView) headerView.findViewById(R.id.nav_user_email);

        final String current_user_id = mAuth.getCurrentUser().getUid();
        setmenudetails = FirebaseFirestore.getInstance().collection("Users").document(current_user_id);

        setmenudetails.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String fullname = documentSnapshot.getString("fullname");
                            String username = documentSnapshot.getString("username");
                            nav_user_full_name.setText(fullname);
                            nav_user_email.setText(username);
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

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
            SetMenuUserDetails();
        }
    }

    private void CheckUserExistence() {

        //user id value over here
        final String current_user_id = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = UsersRef.document(current_user_id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Toast.makeText(MainActivity.this, "Profile Exists", Toast.LENGTH_SHORT).show();
                    } else {
                        SendUserToSetupActivity();
                    }
                }
            }
        });
    }



    private void UsersRefCheck2() {

        currentUserPath.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("usertype").equals("Admin") || documentSnapshot.getString("usertype").equals("Teacher")){
                            SendUserToTeacherActivity();
                        }else{
                            SendUserToStudentActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToTeacherActivity() {
        Intent mainIntent = new Intent (MainActivity.this, TeacherActivity.class);
        startActivity(mainIntent);
    }

    private void SendUserToStudentActivity() {
        Intent mainIntent = new Intent (MainActivity.this, StudentActivity.class);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        //finish();
    }

    private void SendUserToForum() {
        Intent loginIntent = new Intent(MainActivity.this,Forum.class);
        startActivity(loginIntent);
    }

    private void SendTeacherToTeacherViewStudent() {
        Intent mainIntent = new Intent(MainActivity.this, TeacherViewStudents.class);
        startActivity(mainIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //NavigationMenuSelect and do
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                SendUserToSetupActivity();
                break;
            case R.id.nav_home:
                UsersRefCheck2();
                break;
            case R.id.nav_students:
                SendTeacherToTeacherViewStudent();
                break;
            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_forum:
                SendUserToForum();
                break;
            case R.id.nav_Logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }



    //confirm to exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
