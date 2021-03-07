package com.example.vendor.AdminScreen;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vendor.R;
import com.example.vendor.Vendor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {


    public AddUserFragment() {
        // Required empty public constructor
    }

    EditText edt_reg_fullName, edt_reg_phone, edt_reg_password, edt_reg_confirmPassword, edt_address;
    TextView txt_hotelName;
    ImageButton img_select_hotel_name;
    RelativeLayout rl_create_accout;

    String userType;

    ArrayList<String> HotelName;

    DatabaseReference mHotelRef, mUserRef;
    String seelctedHotelName;

    String[] hotels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        edt_address = view.findViewById(R.id.edt_hotelName_add_address);
        edt_reg_confirmPassword = view.findViewById(R.id.edt_register_ConfirmPassword_add);
        edt_reg_password = view.findViewById(R.id.edt_add_Password);
        edt_reg_phone = view.findViewById(R.id.edt_add_Phone);
        edt_reg_fullName = view.findViewById(R.id.edt_add_FullName);
        txt_hotelName = view.findViewById(R.id.edt_hotelName_add);
        img_select_hotel_name = view.findViewById(R.id.img_select_hotel);
        rl_create_accout = view.findViewById(R.id.btn_add_user);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("User");

        img_select_hotel_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotelName = new ArrayList<>();
                HotelName.add("none");

                try {
                    mHotelRef = FirebaseDatabase.getInstance().getReference().child("HotelName");
                    mHotelRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    for (DataSnapshot hotel : ds.getChildren()) {
                                        String hotelName = hotel.child("hotelName").getValue().toString();

                                        HotelName.add(hotelName);
                                    }
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                hotels = HotelName.toArray(new String[0]);
                                txt_hotelName.setText(hotels[0]);

                                builder.setTitle("Choose Hotel")
                                        .setItems(hotels, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                seelctedHotelName = hotels[i];
                                                txt_hotelName.setText("" + seelctedHotelName);
                                                txt_hotelName.setTextColor(Color.BLACK);
                                            }
                                        });

                                builder.show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });


        rl_create_accout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });


//


        return view;
    }

    private void addUser() {
        if (seelctedHotelName == null) {
            seelctedHotelName = hotels[0];
        }
        if(edt_reg_phone.getText().toString().isEmpty() && edt_reg_fullName.getText().toString().isEmpty() && edt_reg_password.getText().toString().isEmpty() && edt_reg_confirmPassword.getText().toString().isEmpty() && edt_address.getText().toString().isEmpty() && txt_hotelName.getText().toString().isEmpty()  ){
            Toast.makeText(Vendor.getAppContext(), "Empty field", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!edt_reg_password.getText().toString().equalsIgnoreCase(edt_reg_confirmPassword.getText().toString())){
            Toast.makeText(Vendor.getAppContext(), "Password and Confirm Password is not equal", Toast.LENGTH_SHORT).show();
            return;
        }

        mHotelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for (DataSnapshot hotel : ds.getChildren()) {

                            String datanbaseHotel = hotel.child("hotelName").getValue().toString();

                            if (datanbaseHotel.equalsIgnoreCase(seelctedHotelName)) {
                                userType = hotel.child("memberShip").getValue().toString();
                                HashMap<String, Object> addUserS = new HashMap<>();
                                addUserS.put("UserName", edt_reg_fullName.getText().toString());
                                addUserS.put("UserPhone", edt_reg_phone.getText().toString());
                                addUserS.put("UserHotelName", txt_hotelName.getText().toString());
                                addUserS.put("UserPassword", edt_reg_password.getText().toString());
                                addUserS.put("UserType", userType);
                                addUserS.put("UserAddress", edt_address.getText().toString());

                                mUserRef.child(edt_reg_fullName.getText().toString()).updateChildren(addUserS).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Vendor.getAppContext(), "Added", Toast.LENGTH_SHORT).show();

                                        edt_address.setText("");
                                        edt_reg_confirmPassword.setText("");
                                        edt_reg_fullName.setText("");
                                        edt_reg_password.setText("");
                                        edt_reg_phone.setText("");
                                        txt_hotelName.setText(hotels[0]);
                                    }
                                });

                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
