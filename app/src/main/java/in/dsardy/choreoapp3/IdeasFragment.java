package in.dsardy.choreoapp3;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.dsardy.choreoapp3.models.Idea;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IdeasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IdeasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdeasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerListIdeas;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    DatabaseReference ideasreference;
    LinearLayoutManager linearLayoutManager;



    private OnFragmentInteractionListener mListener;

    public IdeasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IdeasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IdeasFragment newInstance(String param1, String param2) {
        IdeasFragment fragment = new IdeasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ideas_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_addidea){
            Intent intent = new Intent(getActivity(),AddIdea.class);

            PendingIntent pendingIntent =
                    TaskStackBuilder.create(getActivity())
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
            builder.setContentIntent(pendingIntent);
            startActivity(intent);
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ideas, container, false);

        recyclerListIdeas = (RecyclerView)view.findViewById(R.id.ideaslist);
        recyclerListIdeas.setHasFixedSize(true);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        recyclerListIdeas.setLayoutManager(linearLayoutManager);


        ideasreference = FirebaseDatabase.getInstance().getReference().child("ideas");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Idea, IdeaHolder>(Idea.class, R.layout.idea_item_layout, IdeaHolder.class,ideasreference) {
            @Override
            public void populateViewHolder(IdeaHolder ideaViewHolder, Idea idea, int position) {

                ideaViewHolder.setLikesRef(idea.getUid(),idea.getLikes());
                ideaViewHolder.setName(idea.getName());
                ideaViewHolder.setTitle(idea.getTitle());
                ideaViewHolder.setDescription(idea.getDescription());
                ideaViewHolder.setLikes(idea.getLikes());
                ideaViewHolder.setTime(idea.getTime());

            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    linearLayoutManager.scrollToPosition(positionStart);
                }
            }
        });
        recyclerListIdeas.setAdapter(firebaseRecyclerAdapter);

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

    public static class IdeaHolder extends RecyclerView.ViewHolder {
        public View mView;
        LikeButton likebutton;
        DatabaseReference likesref;
        String initialLikes;


        public IdeaHolder(View itemView) {
            super(itemView);
            mView = itemView;
            likebutton = (LikeButton) mView.findViewById(R.id.like_button);
            likebutton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {


                    likesref.setValue(""+(Integer.parseInt(initialLikes)+1));

                }

                @Override
                public void unLiked(LikeButton likeButton) {

                    likesref.setValue(""+(Integer.parseInt(initialLikes)-1));

                }
            });

        }
        //setters

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.textViewName);
            field.setText("-"+name);
        }


        public void setTitle(String title) {
            TextView field = (TextView) mView.findViewById(R.id.textViewTitle);
            field.setText(title);
        }

        public void setDescription(String description) {

            TextView field = (TextView) mView.findViewById(R.id.textViewDescription);
            field.setText(description);

        }

        public void setLikes(String likes) {


            TextView field = (TextView) mView.findViewById(R.id.textViewLikes);
            field.setText(likes);

        }

        public void setLikesRef(String uid, String likes) {
            this.likesref= FirebaseDatabase.getInstance().getReference().child("ideas").child(uid).child("likes");
            this.initialLikes = likes;

        }

        public void setTime(String time) {
            String startDateString = time;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            Date startDate ;
            try {
                startDate = df.parse(startDateString);

                if(startDate!=null){

                    Date endDate = new Date();

                    long duration  = endDate.getTime() - startDate.getTime();
                    long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);


                    String diff ;

                    if(diffInSeconds<60){
                        diff = ""+diffInSeconds+" sec ago";
                    }else if(diffInMinutes<60){
                        diff = ""+diffInMinutes+" min ago";
                    }else if(diffInHours<24){
                        diff = ""+diffInHours+" hrs ago";
                    }else {

                        int daysago = endDate.compareTo(startDate);
                        diff = ""+daysago+" days ago";
                    }


                    TextView field = (TextView) mView.findViewById(R.id.textViewAgo);
                    field.setText(diff);


                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
