package in.dsardy.choreoapp3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rb.popview.PopField;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatroomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatroomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatroomFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //firebase database
    private DatabaseReference chatroomreference,onlinereference;
    String temp_key;

    //ui
    EditText msg;
    ImageButton sendMsg;
    TextView chatview;
    SharedPreferences userPref;
    LinearLayout onlineLayout;
    TextView onlineShow;
    ScrollView chatscroll;
    SpinKitView loader;
    PopField popField;




    private OnFragmentInteractionListener mListener;

    public ChatroomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatroomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatroomFragment newInstance(String param1, String param2) {
        ChatroomFragment fragment = new ChatroomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        chatroomreference = FirebaseDatabase.getInstance().getReference().child("chatroom");
        onlinereference = FirebaseDatabase.getInstance().getReference().child("online");
        popField = PopField.attach2Window(getActivity());

        //incriment online people
        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        DatabaseReference referenceOnline = FirebaseDatabase.getInstance().getReference().child("online");


        if(userPref.getInt("sex",1)==1){
            DatabaseReference boys = referenceOnline.child("boys");
            Log.e("chack1>>> on create ","mutable data");

            boys.runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    String bo = mutableData.getValue(String.class);
                    String boNew = ""+(Integer.parseInt(bo)+1);
                    mutableData.setValue(boNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }else {

            DatabaseReference boys = referenceOnline.child("girls");
            boys.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    String go = mutableData.getValue(String.class);
                    String goNew = ""+(Integer.parseInt(go)+1);
                    mutableData.setValue(goNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });


        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        // initialise elements
        sendMsg = (ImageButton)view.findViewById(R.id.imageButtonSend);
        msg = (EditText)view.findViewById(R.id.editTextMsg);

        chatview = (TextView)view.findViewById(R.id.msgView);


        onlineLayout = (LinearLayout)view.findViewById(R.id.onlinebar);
        onlineShow = (TextView)view.findViewById(R.id.textViewOnline);
        chatscroll = (ScrollView)view.findViewById(R.id.ChatscrollView);
        loader = (SpinKitView)view.findViewById(R.id.spin_kit);

        chatscroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                chatscroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },1000);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // on click send button
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String typedMsg = msg.getText().toString().trim();

                if(typedMsg.isEmpty()){

                    // hide keyboard
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    //make tost
                    Toast.makeText(getActivity(),"Type something!",Toast.LENGTH_SHORT).show();

                }else {


                    // make edit text empty
                    msg.setText("");

                    Map<String,Object> map = new HashMap<String, Object>();
                    temp_key = chatroomreference.push().getKey();
                    chatroomreference.updateChildren(map);

                    //in this ney child

                    DatabaseReference msg_ref = chatroomreference.child(temp_key);
                    Map<String,Object> map1 = new HashMap<String, Object>();
                    map1.put("msg",typedMsg);
                    map1.put("name",userPref.getString("name","user"));
                    map1.put("sex",userPref.getInt("sex",1));
                    msg_ref.updateChildren(map1);

                }

            }
        });

        onlineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineLayout.setVisibility(View.GONE);
            }
        });

        chatview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineLayout.setVisibility(View.VISIBLE);

                //hide keybord
                InputMethodManager inputManager = (InputMethodManager)
                       getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();



        //get list from reference
        chatroomreference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loader.setVisibility(View.GONE);
                append_msg(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_msg(dataSnapshot);

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

        onlinereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String oboys = dataSnapshot.child("boys").getValue(String.class);
                String ogirls = dataSnapshot.child("girls").getValue(String.class);
                setvaluestoOnlineBar(oboys,ogirls);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    private void setvaluestoOnlineBar(String oboys, String ogirls) {

        onlineShow.setText("");

        appendColoredText(onlineShow,oboys,Color.BLACK);
        appendColoredText(onlineShow,"+",Color.WHITE);
        appendColoredText(onlineShow,ogirls,Color.BLUE);
        appendColoredText(onlineShow," ONLINE",Color.WHITE);

        onlineLayout.setVisibility(View.VISIBLE);




    }

    String temp_name,prev_name , temp_msg ;
    Long temp_gen;
    void append_msg(DataSnapshot dataSnapshot){


        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){

            temp_msg = (String) ((DataSnapshot)i.next()).getValue();
            temp_name = (String) ((DataSnapshot)i.next()).getValue();
            temp_gen = (Long) ((DataSnapshot)i.next()).getValue();


            if(temp_name.equals(prev_name)){

                if(temp_gen==1)
                    appendColoredText(chatview,"         "+ temp_msg+"\n", Color.BLACK);
                else
                    appendColoredText(chatview,"         "+ temp_msg+"\n", Color.BLUE);


            }else{

                if(temp_gen==1)
                appendColoredText(chatview,"\n-->> : "+ temp_msg+"\n", Color.BLACK);
                else
                    appendColoredText(chatview,"\n-->> : "+ temp_msg+"\n", Color.BLUE);


            }

            prev_name = temp_name;

            chatscroll.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatscroll.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },1000);

        }


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
    @Override
    public void onDestroy() {
        super.onDestroy();
        decrimentOnlineUser();
    }

    void decrimentOnlineUser(){
        //decriment online people
        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        DatabaseReference referenceOnline = FirebaseDatabase.getInstance().getReference().child("online");

        if(userPref.getInt("sex",1)==1){
            DatabaseReference boys = referenceOnline.child("boys");
            boys.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    Log.e("chack>>> on destroy ","mutable data");


                    String bo = mutableData.getValue(String.class);
                    String boNew = ""+(Integer.parseInt(bo)-1);
                    mutableData.setValue(boNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }else {

            DatabaseReference boys = referenceOnline.child("girls");
            boys.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    String go = mutableData.getValue(String.class);
                    String goNew = ""+(Integer.parseInt(go)-1);
                    mutableData.setValue(goNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });


        }
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

    @Override
    public void onPause() {
        super.onPause();
        chatview.setText("");
    }

    public static void appendColoredText(TextView tv, String text, int color) {
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();

        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }
}
