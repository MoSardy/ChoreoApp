package in.dsardy.choreoapp3.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dell on 8/19/2016.
 */
public class Member {

    String enlr ;
    String mobile ;
    String name ;
    double lat,lang;
    String time;

    public Member(){

    }



    public Member(String enlr , String mobile , String name , double lat , double lang , String time ){
        this.enlr = enlr;
        this.lang = lang;
        this.lat = lat;
        this.mobile = mobile;
        this.name = name;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getEnlr() {
        return enlr;
    }

    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

}
