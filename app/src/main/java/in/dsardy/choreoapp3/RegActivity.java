package in.dsardy.choreoapp3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.util.HashMap;
import java.util.Map;

import in.dsardy.choreoapp3.models.Member;
import rb.popview.PopField;

public class RegActivity extends AppCompatActivity implements VerificationListener {

    EditText Enlr , OTP ;
    TextView MobileMsg, regMsg;
    ImageButton EnlrGo;
    Button otpGO;
    LinearLayout imageStart , OtpEnter , enlrlay;
    DatabaseReference people;
    Verification verification;
    int process = 0 ;
    SharedPreferences userPref;
    SharedPreferences.Editor editor;
    PopField popField;
    ValueEventListener valueEventListener;
    DatabaseReference you;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        userPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //check if already regestered
        if(userPref.getInt("reg",0)==1){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        popField = PopField.attach2Window(this);
        initializeUI();
        editor = userPref.edit();
        people = FirebaseDatabase.getInstance().getReference().child("people");

    }

    @Override
    protected void onStart() {
        super.onStart();

        EnlrGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //hide keybord
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if(process==0) {

                    EnlrGo.setImageResource(R.drawable.ic_cancel_black_24dp);
                    process = 1;

                    String enlr = Enlr.getText().toString().trim();
                    if(enlr.isEmpty()){

                        Toast.makeText(getApplicationContext(),"Enter Something There !",Toast.LENGTH_LONG).show();
                        process=0;
                        EnlrGo.setImageResource(R.drawable.ic_check_circle_black_24dp);;

                    }else {

                        you = people.child(enlr);

                        valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Member me = dataSnapshot.getValue(Member.class);


                                if (me == null) {

                                    regMsg.setText(R.string.notmembermsg);
                                    EnlrGo.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                    Enlr.setText("");
                                    process=0;


                                } else {
                                    //save to editor
                                    editor.putString("name",me.getName());
                                    editor.putString("enlr",me.getEnlr());
                                    editor.putInt("sex",me.getSex());
                                    editor.putString("mobile",me.getMobile());

                                    regMsg.setText("Sending OTP ...");
                                    startsendingOTP(me.getMobile());


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                regMsg.setText("Check connection!");

                            }
                        };
                        you.addValueEventListener(valueEventListener);


                    }


                }else {
                    process = 0;
                    Enlr.setText("");
                    EnlrGo.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    OtpEnter.setVisibility(View.GONE);
                    imageStart.setVisibility(View.VISIBLE);
                    regMsg.setText("Register Now!");

                }

            }
        });

        otpGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //hide keybord
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);


                String votp = OTP.getText().toString().trim();
                verification.verify(votp); //verifying otp for given number
                MobileMsg.setText("varifying...");
            }
        });

    }

    private void initializeUI() {

        Enlr = (EditText)findViewById(R.id.editTextEnlr);
        OTP = (EditText)findViewById(R.id.editTextOTP);
        MobileMsg = (TextView) findViewById(R.id.textViewmobile);
        EnlrGo = (ImageButton)findViewById(R.id.imageButtonEnlr);
        otpGO = (Button)findViewById(R.id.buttonVarify);
        imageStart = (LinearLayout)findViewById(R.id.linerImage);
        OtpEnter = (LinearLayout)findViewById(R.id.linearOtp);
        regMsg = (TextView)findViewById(R.id.textViewreg);
        enlrlay = (LinearLayout)findViewById(R.id.enlrlayout);
    }

    private void startsendingOTP(String mobile) {

        verification = SendOtpVerification.createSmsVerification(this,mobile,this, "91");
        verification.initiate(); //sending otp on given number
    }


    @Override
    public void onInitiated(String response) {

        imageStart.setVisibility(View.GONE);
        OtpEnter.setVisibility(View.VISIBLE);
        MobileMsg.setText("OTP is sent to your RMN !");

    }

    @Override
    public void onInitiationFailed(Exception paramException) {

        regMsg.setText("Ops! Please try again");
        EnlrGo.setImageResource(R.drawable.ic_check_circle_black_24dp);
        Enlr.setText("");
        editor.clear();
        process = 0;

    }

    @Override
    public void onVerified(String response) {

        // update prefs
        editor.putInt("reg",1);
        editor.commit();
        MobileMsg.setText("Welcome "+userPref.getString("name","user"));


        //add no to smsto in firebase
        DatabaseReference smsto = FirebaseDatabase.getInstance().getReference().child("smsto");
        smsto.child(userPref.getString("name","user")).setValue(userPref.getString("mobile","8394876737"));

        popField.popView(OtpEnter);
        you.removeEventListener(valueEventListener);
        popField.popView(enlrlay);




        //blast layout;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //go to launcher Activity
                Intent intent = new Intent(RegActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1 * 1000);






    }

    @Override
    public void onVerificationFailed(Exception paramException) {

        MobileMsg.setText("Ops! verification failed ..please try again!");


    }
}
