package com.masjidsolutions.mymasjid;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;
import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class Register extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    EditText mFirstName,mLastName,mEmail,mPassword;
    String mMasjidChoice;
    Button mRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    FirebaseFirestore db = getInstance();


    public Register() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final ArrayList<String> masjidNamesList = new ArrayList<>();
        masjidNamesList.add("Select a Masjid");

        db.collection("masjidNames")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                masjidNamesList.add(document.getString("name"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,masjidNamesList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String masjidName = parent.getItemAtPosition(position).toString();
                mMasjidChoice = masjidName;

                Toast.makeText(parent.getContext(),"Selected: "+ masjidName, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        mFirstName   =   findViewById(R.id.firstName);
        mLastName   =   findViewById(R.id.lastName);
        mEmail      =   findViewById(R.id.email);
        mPassword   =   findViewById(R.id.pwd);
        mRegister   =   findViewById(R.id.register);
        fStore      =   getInstance();
        fAuth       =  FirebaseAuth.getInstance();





        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = mEmail.getText().toString().trim();
                String pwdString = mPassword.getText().toString().trim();
                final String firstNameString = mFirstName.getText().toString();
                final String lastNameString = mLastName.getText().toString();


                if (TextUtils.isEmpty(firstNameString) || TextUtils.isEmpty(lastNameString) ) {
                    mFirstName.setError("Complete All Fields.");
                    return;
                }

                if (TextUtils.isEmpty(emailString)) {
                    mEmail.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(pwdString)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (pwdString.length() < 8) {
                    mPassword.setError("Password Must be >= 8 Characters.");
                    return;
                }

                //Register user in Firebase

                fAuth.createUserWithEmailAndPassword(emailString,pwdString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
//                            String collectionPath = null;
                            Map<String,Object> data =new HashMap<>();
                            data.put("firstname",firstNameString);
                            data.put("lastname",lastNameString);
                            data.put("uid",userID);
                            data.put("masjid choice",mMasjidChoice);
                            db.collection("users").document(userID)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot Successfully created");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error Writing on Document",e);
                                        }
                                    });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));


                        }else {
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });







    }
}
