package in.dsardy.choreoapp3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import in.dsardy.choreoapp3.models.Member;

public class RegActivity extends AppCompatActivity implements VerificationListener {

    EditText Enlr , OTP ;
    TextView MobileMsg, regMsg;
    ImageButton EnlrGo;
    Button otpGO;
    LinearLayout imageStart , OtpEnter;
    DatabaseReference people;
    Verification verification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        initializeUI();
        people = FirebaseDatabase.getInstance().getReference().child("people");


        EnlrGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enlr = Enlr.getText().toString().trim();
                DatabaseReference you = people.child(enlr);
                you.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Member me = dataSnapshot.getValue(Member.class);
                        if(me==null){

                            regMsg.setText(R.string.notmembermsg);


                        }else {

                            regMsg.setText("Sending OTP ...");
                            startsendingOTP(me.getMobile());


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        otpGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String votp = OTP.getText().toString().trim();
                verification.verify(votp); //verifying otp for given number
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

    }

    @Override
    public void onVerified(String response) {

        // update prefs


        //go to launcher Activity
        Log.e("mmmmmmm","success");


    }

    @Override
    public void onVerificationFailed(Exception paramException) {

        MobileMsg.setText("Ops! verification failed ..please try again!");

    }
}
