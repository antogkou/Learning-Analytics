package kosmoglou.antogkou.learninganalytics;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, UserType, UserSemester, UserFathername, UserPhonenumber, UserAM, UserAge, UserSchoolregisterdate,editText;
    private Button SaveInformationButton,CancelButton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DocumentReference UsersRef1;
    private CollectionReference UsersRef2;
    private FirebaseAnalytics mFirebaseAnalytics;
    String currentUserID;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    private static final String TAG = "TasksSample";
    private DocumentReference profileinfo;
    private Uri mainImageUri = null;
    private CircleImageView setup_profile_image;
    private StorageReference mStorageRef;
    private DatePickerDialog mDatePickerDialog;
    private EditText edDate;
    private DatePickerDialog datePickerDialog;
    EditText etJoiningDate;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //toolbar and navigation drawer staff
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Setup Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//fb analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //firestore
        UsersRef1 = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);
        UsersRef2 = FirebaseFirestore.getInstance().collection("Users");
     //   UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_full_name);
        UserType = (EditText) findViewById(R.id.setup_usertype);

        UserSemester = (EditText) findViewById(R.id.setup_semester);
        UserFathername = (EditText) findViewById(R.id.setup_fathername);
        UserPhonenumber = (EditText) findViewById(R.id.setup_phonenumber);
        UserAM = (EditText) findViewById(R.id.setup_am);
        UserAge = (EditText) findViewById(R.id.setup_age);
        UserSchoolregisterdate = (EditText) findViewById(R.id.setup_schoolregisterdate);


        SaveInformationButton = (Button) findViewById(R.id.setup_information_button);
        CancelButton = (Button) findViewById(R.id.setup_cancel_button);

        setup_profile_image = findViewById(R.id.setup_profile_image);

        GetAccountInformation();
        loadingBar = new ProgressDialog(this);



        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelAccountSetupInformation();
            }
        });

        edDate = (EditText) findViewById(R.id.setup_schoolregisterdate);

        setDateTimeField();
        edDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });





        final String current_user_id = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("user_id", currentUserID);
        user.put("usertype", "Student");
        UsersRef2.document(currentUserID)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                edDate.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void CancelAccountSetupInformation() {
        SendUserToMainActivity();

    }

    private void GetAccountInformation(){

        String username = getIntent().getStringExtra("username");
        String fullname = getIntent().getStringExtra("fullname");
        String usertype = getIntent().getStringExtra("usertype");


        profileinfo = FirebaseFirestore.getInstance().collection("Users").document(currentUserID);
        EditText enterjavascore  = (EditText) findViewById(R.id.enterjavascore);
        EditText setup_username  = (EditText) findViewById(R.id.setup_username);
        EditText setup_full_name  = (EditText) findViewById(R.id.setup_full_name);
        EditText setup_usertype  = (EditText) findViewById(R.id.setup_usertype);

        EditText setup_semester  = (EditText) findViewById(R.id.setup_semester);
        EditText setup_fathername  = (EditText) findViewById(R.id.setup_fathername);
        EditText setup_phonenumber  = (EditText) findViewById(R.id.setup_phonenumber);
        EditText setup_am  = (EditText) findViewById(R.id.setup_am);
        EditText setup_age  = (EditText) findViewById(R.id.setup_age);
        EditText setup_schoolregisterdate  = (EditText) findViewById(R.id.setup_schoolregisterdate);




        profileinfo.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String username = documentSnapshot.getString("username");
                            String fullname = documentSnapshot.getString("fullname");
                            String usertype = documentSnapshot.getString("usertype");

                            String semester = documentSnapshot.getString("semester");
                            String fathername = documentSnapshot.getString("fathername");
                            String phonenumber = documentSnapshot.getString("phonenumber");
                            String am = documentSnapshot.getString("am");
                            String age = documentSnapshot.getString("age");
                            String schoolregisterdate = documentSnapshot.getString("schoolregisterdate");

                            setup_username.setHint("Username: "+ username);
                            setup_full_name.setHint("Fullname: "+ fullname);
                            setup_usertype.setHint("UserType: "+ usertype);
                            setup_semester.setHint("Semester: "+ semester);
                            setup_fathername.setHint("Father's Name: "+ fathername);
                            setup_phonenumber.setHint("Phone Number: "+ phonenumber);
                            setup_am.setHint("AM: "+ am);
                            setup_age.setHint("Age: "+ age);
                            setup_schoolregisterdate.setHint("Register Date: "+ schoolregisterdate);


                            //check if hint is empty, insert a default one to know what each editText does
                            if (TextUtils.isEmpty(username)) {
                                setup_username.setHint("Username");
                            }
                            if (TextUtils.isEmpty(fullname)) {
                                setup_full_name.setHint("Full Name");
                            }
                            if (TextUtils.isEmpty(usertype)) {
                                setup_usertype.setHint("UserType");
                            }
                            if (TextUtils.isEmpty(semester)) {
                                setup_semester.setHint("Semester");
                            }
                            if (TextUtils.isEmpty(fathername)) {
                                setup_fathername.setHint("Father's Name");
                            }
                            if (TextUtils.isEmpty(phonenumber)) {
                                setup_phonenumber.setHint("Phone Number");
                            }
                            if (TextUtils.isEmpty(am)) {
                                setup_am.setHint("AM");
                            }
                            if (TextUtils.isEmpty(age)) {
                                setup_age.setHint("Age");
                            }
                            if (TextUtils.isEmpty(schoolregisterdate)) {
                                setup_schoolregisterdate.setHint("Register Date");
                            }

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

    private void SaveAccountSetupInformation() {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String usertype = UserType.getText().toString();

        String semester = UserSemester.getText().toString();
        String fathername = UserFathername.getText().toString();
        String phonenumber = UserPhonenumber.getText().toString();
        String am = UserAM.getText().toString();
        String age = UserAge.getText().toString();
        String schoolregisterdate = UserSchoolregisterdate.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "username field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(am)) {
            Toast.makeText(this, "AM field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usertype)) {
            Toast.makeText(this, "usertype field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "semester field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fathername)) {
            Toast.makeText(this, "fathername field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phonenumber)) {
            Toast.makeText(this, "phonenumber field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(am)) {
            Toast.makeText(this, "am field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(age)) {
            Toast.makeText(this, "age field is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(schoolregisterdate)) {
            Toast.makeText(this, "schoolregisterdate field is empty", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, your information is being saved on server..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            final String current_user_id = mAuth.getCurrentUser().getUid();

            Map<String, Object> user = new HashMap<>();
            user.put("user_id", currentUserID);
            user.put("username", username);
            user.put("fullname", fullname);
            user.put("usertype", usertype);
            user.put("semester", semester);
            user.put("fathername", fathername);
            user.put("phonenumber", phonenumber);
            user.put("am", am);
            user.put("age", age);
            user.put("schoolregisterdate", schoolregisterdate);
            UsersRef2.document(currentUserID)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            loadingBar.dismiss();
                            SendUserToMainActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });


        }


    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent (SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setup_account_menu, menu);
        MenuItem change_password = menu.findItem(R.id.change_password);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.change_password:
                GoToChangePasswordActivity();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void GoToChangePasswordActivity() {
        Intent mainIntent = new Intent (SetupActivity.this, ForgotPassword.class);
        startActivity(mainIntent);
    }


}
