package in.dsardy.choreoapp3;

import android.util.Base64;
import android.util.Log;import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dell on 8/15/2016.
 */
public class AppClass extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

       //printkey();

    }

    void printkey(){

        MessageDigest md = null;
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        Log.i("SecretKey = ",Base64.encodeToString(md.digest(), Base64.DEFAULT));

    }


}
