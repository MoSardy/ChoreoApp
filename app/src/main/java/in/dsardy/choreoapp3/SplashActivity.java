package in.dsardy.choreoapp3;

import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash{



    @Override
    public void initSplash(ConfigSplash configSplash) {

        /* you don't have to override every property */
        getSupportActionBar().hide();


        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorPrimary); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(2000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.dsardyappssmall); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)






        //Customize Title
        configSplash.setTitleSplash("D'sardy Apps");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(25f); //float value
        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimTitleTechnique(Techniques.FadeInUp);

    }

    @Override
    public void animationsFinished() {

        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();

    }
}
