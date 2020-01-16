package com.kenyadevelopers.unganishwa;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment
{
    private DatabaseReference mDatabaseReference;
    private List<Recent> mRecentList;
    private RecyclerView mRecentsRecyclerView;
    private RecentAdapter recentAdapter;
    private ValueEventListener mValueEventListener;
    private int pos;


    private class RecentAdapter extends RecyclerView.Adapter<RecentViewHolder>
    {

        public RecentAdapter(List<Recent> bigButtList)
        {
            mRecentList=bigButtList;
        }

        @NonNull
        @Override
        public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sexy_item,viewGroup,false);
            return new RecentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecentViewHolder recentViewHolder, int i)
        {
            recentViewHolder.bind(i);

        }

        @Override
        public int getItemCount() {
            return mRecentList.size();
        }
    }

    private class RecentViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView mRecentImageView;
        private TextView textViewRecent;

        public RecentViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mRecentImageView=(ImageView)itemView.findViewById(R.id.imageview_sexy);
            textViewRecent=(TextView)itemView.findViewById(R.id.textview_sexy);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    if(getAdapterPosition()!=RecyclerView.NO_POSITION)
                    {
                        openWhatsApp(v,  mRecentList.get(getAdapterPosition()).getName());
                    }

                }
            });
        }

        public void bind(int i)
        {
            textViewRecent.setBackgroundColor(getResources().getColor(android.R.color.white));
            textViewRecent.setText(mRecentList.get(i).getName());
            pos=i;
            Picasso.with(getContext()).load(mRecentList.get(i).getDownloadUrl()).fit().centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(mRecentImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(getContext()).load(mRecentList.get(pos).getDownloadUrl()).fit().centerCrop()
                                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                                    .into(mRecentImageView);
                        }
                    });
        }
    }



    public void openWhatsApp(View view,String name)
    {
        WhatsAppCommunicationManager wm=new WhatsAppCommunicationManager();
        wm.openWhatsApp(getContext(),name);
    }



    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_display, container, false);
        mDatabaseReference=FirebaseDatabase.getInstance().getReference("recents");
        mDatabaseReference.keepSynced(true);
        mRecentsRecyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        mRecentList=new ArrayList<>();
        getValuesFromFirebaseDb();
        mRecentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentAdapter=new RecentAdapter(mRecentList);
        mRecentsRecyclerView.setAdapter(recentAdapter);
        return view;
    }

    private void getValuesFromFirebaseDb()
    {

        mValueEventListener=mDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mRecentList.clear();
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Recent recent=postSnapShot.getValue(Recent.class);
                    mRecentList.add(recent);
                }
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mValueEventListener);
    }
}
