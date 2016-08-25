package in.dsardy.choreoapp3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.dsardy.choreoapp3.models.Idea;

public class AddIdea extends AppCompatActivity {

    AlertDialog alertDialog;
    EditText title , description;
    String myTitle,myDes;
    DatabaseReference referenceIdeas;
    String temp_key;
    SharedPreferences userPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);
        userPref = PreferenceManager.getDefaultSharedPreferences(this);


        referenceIdeas = FirebaseDatabase.getInstance().getReference().child("ideas");

        // build alert dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This will post your new brand idea ! make sure it is correct.")
                .setTitle("Are you sure?");

        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                temp_key = referenceIdeas.push().getKey();
                //get date
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                String DateToStr = format.format(curDate);

                Idea idea = new Idea(myTitle,myDes,temp_key,userPref.getString("name","user420"),DateToStr);
                referenceIdeas.child(temp_key).setValue(idea);
               // referenceIdeas.push().setValue(idea);

                title.setText("");
                description.setText("");
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        alertDialog = builder.create();

        //ui

        title = (EditText)findViewById(R.id.editTextTitle);
        description = (EditText)findViewById(R.id.editTextDes);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_idea,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id== R.id.action_ideaok){

            myTitle = title.getText().toString().trim();
            myDes = description.getText().toString();


            if(myDes.isEmpty()||myTitle.isEmpty()){
                Toast.makeText(this,"Type Something!!",Toast.LENGTH_LONG).show();
            }else {

                alertDialog.show();

            }


        }
        return super.onOptionsItemSelected(item);
    }
}
