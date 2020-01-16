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
public class ChicksFragment extends Fragment
{
    private DatabaseReference mDatabaseReference;
    private List<Chick> chicks;
    private RecyclerView chicksRecyclerView;
    private ChicksAdapter chicksAdapter;
    private WhatsAppCommunicationManager wm;
    private int pos;
    private ValueEventListener mValueEventListener;

    private class ChicksAdapter extends RecyclerView.Adapter<ChicksViewHolder>
    {

        public ChicksAdapter(List<Chick> mChickList)
        {
            chicks=mChickList;
        }

        @NonNull
        @Override
        public ChicksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view=LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.sexy_item,viewGroup,false);
            return new ChicksViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChicksViewHolder chicksViewHolder, int i)
        {
            chicksViewHolder.bind(i);

        }

        @Override
        public int getItemCount() {
            return chicks.size();
        }
    }

    private class ChicksViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView chickImageview;
        private TextView textViewChick;

        public ChicksViewHolder(@NonNull View itemView)
        {
            super(itemView);

            chickImageview=(ImageView)itemView.findViewById(R.id.imageview_sexy);
           textViewChick=(TextView)itemView.findViewById(R.id.textview_sexy);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v)
               {

                   if(getAdapterPosition()!=RecyclerView.NO_POSITION)
                   {
                       openWhatsApp(v,chicks.get(getAdapterPosition()).getName());
                   }

               }
           });
        }

        public void bind(int i)
        {
            textViewChick.setBackgroundColor(getResources().getColor(android.R.color.white));
            textViewChick.setText(chicks.get(i).getName());
            pos=i;
            Picasso.with(getContext()).load(chicks.get(i).downloadUrl).fit().centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(chickImageview, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(getContext()).load(chicks.get(pos).downloadUrl).fit().centerCrop()
                                    .placeholder(getResources().getDrawable(R.drawable.placeholder))
                                    .into(chickImageview);
                        }
                    });
        }
    }
    public void openWhatsApp(View view,String name)
    {
        wm.openWhatsApp(getContext(),name);
    }



    public ChicksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_display, container, false);
        wm=new WhatsAppCommunicationManager();
        mDatabaseReference=FirebaseDatabase.getInstance().getReference("chicks");
        mDatabaseReference.keepSynced(true);

        chicksRecyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        chicks=new ArrayList<>();
        getValuesFromFirebaseDb();
        chicksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chicksAdapter=new ChicksAdapter(chicks);
        chicksRecyclerView.setAdapter(chicksAdapter);
        return view;
    }

    private void getValuesFromFirebaseDb()
    {
        mValueEventListener=mDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                chicks.clear();
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Chick chick=postSnapShot.getValue(Chick.class);
                    chicks.add(chick);
                }
               chicksAdapter.notifyDataSetChanged();
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
