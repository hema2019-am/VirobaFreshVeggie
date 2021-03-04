package com.example.vendor.AdminScreen;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vendor.Adapters.AdapterTotalSummaryItemList;
import com.example.vendor.R;
import com.example.vendor.TotalSummaryConstructor;
import com.example.vendor.Vendor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalHotelItem extends Fragment {


    public TotalHotelItem() {
        // Required empty public constructor
    }

    String dateTime;
    RecyclerView recyclerView;

    String currentDate, today;
    DatabaseReference databaseReference, mRef, orderContent, mHotelList;

    ValueEventListener valueEventListener;

    String hotelName;
    ArrayList<TotalSummaryConstructor> lists;
    AdapterTotalSummaryItemList adapter;

    HashMap<String, Double> totalSummary;
    boolean finished;

    ImageButton btn_back_total_item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_hotel_item, container, false);


        try {
            lists = new ArrayList<>();
            Bundle bundle = getArguments();
            dateTime = bundle.getString("dateTime");
            hotelName = bundle.getString("hotelName");

            recyclerView = view.findViewById(R.id.recycle__total_HotelItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.dd");
            currentDate = dateTime;
            today = currentDate.replace(".", "");
            Log.v("dateTime", "" + dateTime);

            orderContent = FirebaseDatabase.getInstance().getReference().child("OrderContent");

            mHotelList = FirebaseDatabase.getInstance().getReference().child("HotelWiseList").child(hotelName);


            totalSummary = new HashMap<>();

            btn_back_total_item = view.findViewById(R.id.btn_back_total_item);


            btn_back_total_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                    }
                }
            });


            databaseReference = FirebaseDatabase.getInstance().getReference().child("Order");


            loadHotelList();


        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "On Create: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null && databaseReference != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    public void loadHotelList() {


        try {
            lists = new ArrayList<>();
            lists.clear();
            mRef = FirebaseDatabase.getInstance().getReference().child("HotelWiseList").child(today);
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot child : snapshot.getChildren()) {

                            for (DataSnapshot ds : child.getChildren()) {
                                String hotels = ds.getKey();
                                Log.v("HotelName", "" + hotels);
                                TotalSummaryConstructor totalSummaryConstructor = ds.getValue(TotalSummaryConstructor.class);

                                if (hotels.equalsIgnoreCase(hotelName)) {
                                    lists.add(totalSummaryConstructor);
                                }


                                Log.v("listConstruct", "" + lists);

                            }


                        }


                    }

                    adapter = new AdapterTotalSummaryItemList(getContext(), lists);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "Total Hotel " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
