package in.dsardy.choreoapp3;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.renderscript.Script;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by dell on 8/15/2016.
 */
public class AppClass extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();


    }


}
