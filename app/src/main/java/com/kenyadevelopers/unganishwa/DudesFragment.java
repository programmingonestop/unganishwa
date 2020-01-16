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
public class DudesFragment extends Fragment
{

    private List<Dude> mDudes;
    private DatabaseReference mDatabaseReference;
    private RecyclerView dudesRecyclerView;
    private DudesAdapter dudesAdapter;
    private int pos;
    private ValueEventListener mValueEventListener;


    private class DudesAdapter extends RecyclerView.Adapter<DudesViewHolder>
    {

        public DudesAdapter(List<Dude> dudeList)
        {
            mDudes=dudeList;
        }

        @NonNull
        @Override
        public DudesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sexy_item,viewGroup,false);
            return new DudesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DudesViewHolder dudesViewHolder, int i)
        {
            dudesViewHolder.bind(i);
           
        }

        @Override
        public int getItemCount() {
            return mDudes.size();
        }
    }

    private class DudesViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView dudeImageview;
        private TextView textViewdude;

        public DudesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dudeImageview=(ImageView)itemView.findViewById(R.id.imageview_sexy);
            textViewdude=(TextView)itemView.findViewById(R.id.textview_sexy);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                   if(getAdapterPosition()!=RecyclerView.NO_POSITION)
                   {
                       openWhatsApp(v,mDudes.get(getAdapterPosition()).getName());
                   }

                }
            });
        }

        public void bind(int i) 
        {
            
            textViewdude.setBackgroundColor(getResources().getColor(android.R.color.white));
            textViewdude.setText(mDudes.get(i).getName());

            pos = i;
            Picasso.with(getContext()).load(mDudes.get(i).downloadUrl).fit().centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(dudeImageview, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(getContext()).load(mDudes.get(pos).downloadUrl).fit().centerCrop()
                                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                                    .into(dudeImageview);
                        }
                    });
        }
        
    }

    public void openWhatsApp(View view,String name)
    {
        WhatsAppCommunicationManager wm=new WhatsAppCommunicationManager();
        wm.openWhatsApp(getContext(),name);
    }



    public DudesFragment() {
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

        mDatabaseReference=FirebaseDatabase.getInstance().getReference("dudes");
        mDatabaseReference.keepSynced(true);
        dudesRecyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        mDudes=new ArrayList<>();
        getValuesFromFirebaseDb();
        dudesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dudesAdapter=new DudesAdapter(mDudes);
        dudesRecyclerView.setAdapter(dudesAdapter);
        return view;
    }

    private void getValuesFromFirebaseDb()
    {

        mValueEventListener=mDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mDudes.clear();
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Dude dude=postSnapShot.getValue(Dude.class);
                    mDudes.add(dude);
                }
                dudesAdapter.notifyDataSetChanged();
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
