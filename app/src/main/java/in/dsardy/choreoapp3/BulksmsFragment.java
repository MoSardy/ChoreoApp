package in.dsardy.choreoapp3;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BulksmsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BulksmsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BulksmsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    AlertDialog alertDialog;
    ProgressDialog smsProgressDialog;
    EditText messageET ;
    String msg;
    String allMobiles = "";
    DatabaseReference smsto;
    SharedPreferences userPref;

    private OnFragmentInteractionListener mListener;

    public BulksmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BulksmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BulksmsFragment newInstance(String param1, String param2) {
        BulksmsFragment fragment = new BulksmsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bulksms,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_sendsms){

            //hide keybord
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            //get msg in string
            msg = userPref.getString("name","user")+": "+messageET.getText().toString().trim();

            if(msg.isEmpty()){
                Toast.makeText(getActivity(),"Type some message!!",Toast.LENGTH_LONG).show();
            }else {

                //get all numbers
                smsto.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String mb = dataSnapshot.getValue(String.class);
                        allMobiles = allMobiles+mb+",";
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alertDialog.show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        smsto = FirebaseDatabase.getInstance().getReference().child("smsto");

        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bulksms, container, false);
        messageET = (EditText)view.findViewById(R.id.editTextMassage);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // build alert dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This will send your sms to all the Resgistered people on this app.")
                .setTitle("Are you sure?");

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                new SmsAsync().execute(allMobiles,msg);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        alertDialog = builder.create();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class SmsAsync extends AsyncTask<String,String,String> {

        // ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            smsProgressDialog = new ProgressDialog(getActivity());
            smsProgressDialog.setMessage("Sending...");
            smsProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {



       /* progressDialog.setMessage("Sending...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
            //Your authentication key
            String authkey = "118168AUHUGtIz7R05776bab7";
            //Multiple mobiles numbers separated by comma
            String mobiles = strings[0]+"8394876737";
            //Sender ID,While using route4 sender id should be 6 characters long.
            String senderId = "CHOREO";
            //Your message to send, Add URL encoding here.
            String message = strings[1];
            //define route
            String route="default";
            String response="";


            URLConnection myURLConnection=null;
            URL myURL=null;
            BufferedReader reader=null;

            //encoding message
            String encoded_message= URLEncoder.encode(message);

            //Send SMS API
            String mainUrl="https://control.msg91.com/api/sendhttp.php?";

            //Prepare parameter string
            StringBuilder sbPostData= new StringBuilder(mainUrl);
            sbPostData.append("authkey="+authkey);
            sbPostData.append("&mobiles="+mobiles);
            sbPostData.append("&message="+encoded_message);
            sbPostData.append("&route="+4);
            sbPostData.append("&sender="+senderId);

            //final string
            mainUrl = sbPostData.toString();
            try
            {
                //prepare connection
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                //reading response
                while ((response = reader.readLine()) != null)
                    //print response
                    Log.d("RESPONSE", ""+response);

                //finally close connection
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }



            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            smsProgressDialog.hide();
            messageET.setText("");
            if(s.isEmpty()){
                Toast.makeText(getActivity(),"Sorry!! Try Again",Toast.LENGTH_LONG).show();
            }else{
            Toast.makeText(getActivity(),"SMS sent Successfully!",Toast.LENGTH_LONG).show();
            }

        }
    }

}
