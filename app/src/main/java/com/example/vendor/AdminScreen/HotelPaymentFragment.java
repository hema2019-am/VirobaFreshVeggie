package com.example.vendor.AdminScreen;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vendor.Adapters.AdapterLogData;
import com.example.vendor.LogData;
import com.example.vendor.R;
import com.example.vendor.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotelPaymentFragment extends Fragment {


    public HotelPaymentFragment() {
        // Required empty public constructor
    }

    Date date_To = null, date_from = null, current_Date = null;

    TextView txt_totalOrder, txt_totalBalance;
    RelativeLayout rl_paid;
    DatabaseReference dbTotalBalance;

    Button btn_share_excel;


    ImageView btn_date_picker_to, btn_date_picker_from;

    TextView txt_date_picker_to, txt_date_picker_from;

    String hotelName, member;

    EditText edittext_amountPayed;
    AlertDialog alertdialog;
    String amount_payed;
    ImageView imgBack;
    AlertDialog.Builder builder;
    LayoutInflater layoutinflater;

    String totalBalance;
    String monthInStringTo,monthInStringFrom;
    DatePickerDialog.OnDateSetListener mDateSetListenrTo, mDateSetListenrFrom;

    String today, currentDate;
    double afterDeduct;

    Button btn_go;

    ArrayList<LogData> logDataList;
    AdapterLogData adapterLogData;
    RecyclerView rec_log;
    int counter = 0;
    int count = 0;

    ProgressDialog mProgress ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_payment, container, false);
        txt_totalOrder = view.findViewById(R.id.txt_TotalOrderNumber);
        txt_totalBalance = view.findViewById(R.id.txt_TotalPayment);
        rl_paid = view.findViewById(R.id.btn_payment_paid);
        btn_date_picker_from = view.findViewById(R.id.btn_date_picker_item_From);
        btn_date_picker_to = view.findViewById(R.id.btn_date_picker_item_To);
        txt_date_picker_to = view.findViewById(R.id.txt_date_item_To);
        txt_date_picker_from = view.findViewById(R.id.txt_date_item_From);
        btn_share_excel = view.findViewById(R.id.btn_share_excel_data);

        SharedPreferences sh = this.getActivity().getSharedPreferences("MyHotelDetails", MODE_PRIVATE);
        hotelName = sh.getString("HotelName", "");
        member = sh.getString("memberShip", "");

        btn_go = view.findViewById(R.id.btn_go);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.dd");
        currentDate = sdf.format(new Date());
        today = currentDate.replace(".", "");
        rec_log = view.findViewById(R.id.recycler_log);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Vendor.getAppContext());

        rec_log.setLayoutManager(layoutManager);

        mProgress =new ProgressDialog(getContext());
        mProgress.setTitle("Please Wait");
        mProgress.setCanceledOnTouchOutside(false);
        Toast.makeText(Vendor.getAppContext(), "" + hotelName + member, Toast.LENGTH_SHORT).show();


        dbTotalBalance = FirebaseDatabase.getInstance().getReference().child("Payment");



        dbTotalBalance.child(hotelName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    try {
                        String totalOrder = snapshot.child("OrderNumber").getValue().toString();
                        totalBalance = snapshot.child("payment").getValue().toString();
                        txt_totalOrder.setText("" + totalOrder);
                        txt_totalBalance.setText("Rs " + totalBalance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        rl_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

                    layoutinflater = getLayoutInflater();

                    View Dview = layoutinflater.inflate(R.layout.payment_edit, null);

                    builder.setCancelable(false);

                    builder.setView(Dview);

                    edittext_amountPayed = Dview.findViewById(R.id.edt_payment);
                    imgBack = Dview.findViewById(R.id.img_cancel);


                    alertdialog = builder.create();


                    edittext_amountPayed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                                Log.v("pressed", "Enter pressed");
                                try {
                                    alertdialog.dismiss();
                                    amount_payed = edittext_amountPayed.getText().toString();
                                    if (Double.parseDouble(amount_payed) > Double.parseDouble(totalBalance) && Double.parseDouble(amount_payed) <= 0) {
                                        Toast.makeText(Vendor.getAppContext(), "Can't be dudycted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        afterDeduct = Double.parseDouble(totalBalance) - Double.parseDouble(amount_payed);

                                        dbTotalBalance.child(hotelName).child("Log").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                counter = (int) snapshot.getChildrenCount();
                                                Log.v("counter", "" + Integer.toString(counter));


                                                count = counter + 1;
                                                Log.v("count", "" + Integer.toString(count));

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                        dbTotalBalance.child(hotelName).child("payment").setValue(String.valueOf(afterDeduct)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Vendor.getAppContext(), "Amount Deducted", Toast.LENGTH_SHORT).show();


                                                HashMap<String, Object> log = new HashMap<>();
                                                log.put("date", currentDate);
                                                log.put("amount", amount_payed);
                                                dbTotalBalance.child(hotelName).child("Log").child(String.valueOf(count)).setValue(log).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Vendor.getAppContext(), "logged", Toast.LENGTH_SHORT).show();
                                                        dbTotalBalance.child(hotelName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String payment = snapshot.child("payment").getValue().toString();
                                                                txt_totalBalance.setText("Rs " + payment);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                        getData();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Vendor.getAppContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                            }
                                        });
                                    }

                                } catch (Exception e) {
                                    Log.v("ex", e.getMessage());
                                }


                            } else {
                                alertdialog.dismiss();
                            }
                            return false;
                        }
                    });


                    imgBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertdialog.dismiss();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                alertdialog.show();


            }
        });

        dbTotalBalance.child(hotelName).child("Log").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){


                    int start = 1;
                    final int count =(int) snapshot.getChildrenCount();
                    Log.v("count",""+count);

                    dbTotalBalance.child(hotelName).child("Log").child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final String paid_date_start = snapshot.child("date").getValue().toString();
                            dbTotalBalance.child(hotelName).child("Log").child(Integer.toString(count)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   String paid_date_end = snapshot.child("date").getValue().toString();

                                   txt_date_picker_to.setText(""+paid_date_end);
                                   txt_date_picker_from.setText(""+paid_date_start);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getData();

        btn_share_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 final String datesTo = txt_date_picker_to.getText().toString();
                 final String dateFrom = txt_date_picker_from.getText().toString();


                getLog(new FirebaseCallbackLog() {
                    @Override
                    public void onCallsbackLog(ArrayList<LogData> log) {
                       Log.v("LogData",""+log.toString());





                        StringBuilder sb_1 = new StringBuilder();
                       for(LogData logData : log){
                           String date = logData.getDate();
                           String amount = logData.getAmount();

                            sb_1.append(date+" - Rs. "+amount);
                            sb_1.append("\n");
                           Log.v("LogDate",""+date);
                       }

                        Intent intent = new Intent(Intent.ACTION_SENDTO);

                        intent.setType("plain/text");
                        intent.setData(Uri.parse("mailto:"));

                        intent.putExtra(Intent.EXTRA_SUBJECT,"Logs from " + dateFrom + " \n to " + datesTo);

                        intent.putExtra(Intent.EXTRA_TEXT, sb_1.toString());
                        startActivity(Intent.createChooser(intent,"Choose App to send"));
                    }
                });


            }
        });

        btn_date_picker_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                monthInStringTo = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                int month = cal.get(Calendar.MONDAY);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenrTo,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenrTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "." +monthInStringTo + "." + day;
                Date sdf = null;
                try {
                    sdf = new SimpleDateFormat("yyyy.MMM.dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd");
                String strDate = dateFormat.format(sdf);
                currentDate = strDate;

                today =  strDate.replace(".", "");


                txt_date_picker_to.setText("" + strDate);

            }
        };

        btn_date_picker_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                monthInStringFrom = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                int month = cal.get(Calendar.MONDAY);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenrFrom,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenrFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "." + monthInStringFrom + "." + day;
                Date sdf = null;
                try {
                    sdf = new SimpleDateFormat("yyyy.MMM.dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd");
                String strDate = dateFormat.format(sdf);
                currentDate = strDate;

                today =  strDate.replace(".", "");


                txt_date_picker_from.setText("" + strDate);

            }
        };

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParticularData();
            }
        });





        return view;
    }


    private void getParticularData(){
        final String dateTo = txt_date_picker_to.getText().toString();
        final String dateFrom = txt_date_picker_from.getText().toString();

        if(!dateTo.equalsIgnoreCase("date") && !dateFrom.equalsIgnoreCase("date")){
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MMM.dd");
            try {
                date_To = format.parse(dateTo);
                date_from = format.parse(dateFrom);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            if(date_To.before(date_from)){
                Toast.makeText(Vendor.getAppContext(), "dates not specified properly", Toast.LENGTH_SHORT).show();
                return;
            }


            logDataList = new ArrayList<>();


            // Firebase Query


            mProgress.show();
            dbTotalBalance.child(hotelName).child("Log").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){

                        Toast.makeText(Vendor.getAppContext(), "Here", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            mProgress.dismiss();
                            String key = ds.getKey();
                            Log.v("keys", "" + key);
                            LogData logDatas = ds.getValue(LogData.class);
                            String orderDate = logDatas.getDate();

                            Log.v("database",""+orderDate);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MMM.dd");
                            try {
                                date_To = format.parse(dateTo);
                                date_from = format.parse(dateFrom);
                                current_Date = format.parse(orderDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            if( current_Date.after(date_from) && current_Date.before(date_To) || current_Date.equals(date_To) || current_Date.equals(date_from)){
                                int dates = date_To.compareTo(current_Date) * date_from.compareTo(current_Date);
                                Log.v("fyu",""+dates);
                                logDataList.add(logDatas);
                            }


                        }

                        if(logDataList.size()<=0){
                            mProgress.dismiss();
                        }
                        Log.v("Dates",""+logDataList.toString());
                        adapterLogData = new AdapterLogData(getContext(), logDataList);
                        adapterLogData.notifyDataSetChanged();
                        rec_log.setAdapter(adapterLogData);
                        mProgress.dismiss();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }) ;



        }
    }

    private void getLog(final FirebaseCallbackLog firebaseCallbackLog){
        final String dateTo = txt_date_picker_to.getText().toString();
        final String dateFrom = txt_date_picker_from.getText().toString();

        if(!dateTo.equalsIgnoreCase("date") && !dateFrom.equalsIgnoreCase("date")){
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MMM.dd");
            try {
                date_To = format.parse(dateTo);
                date_from = format.parse(dateFrom);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            if(date_To.before(date_from)){
                Toast.makeText(Vendor.getAppContext(), "dates not specified properly", Toast.LENGTH_SHORT).show();
                return;
            }


            logDataList = new ArrayList<>();


            // Firebase Query



            dbTotalBalance.child(hotelName).child("Log").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){

                        Toast.makeText(Vendor.getAppContext(), "Here", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            Log.v("keys", "" + key);
                            LogData logDatas = ds.getValue(LogData.class);
                            String orderDate = logDatas.getDate();

                            Log.v("database",""+orderDate);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MMM.dd");
                            try {
                                date_To = format.parse(dateTo);
                                date_from = format.parse(dateFrom);
                                current_Date = format.parse(orderDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            if( current_Date.after(date_from) && current_Date.before(date_To) || current_Date.equals(date_To) || current_Date.equals(date_from)){
                                int dates = date_To.compareTo(current_Date) * date_from.compareTo(current_Date);
                                Log.v("fyu",""+dates);
                                logDataList.add(logDatas);
                                firebaseCallbackLog.onCallsbackLog(logDataList);
                            }


                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }) ;



        }
    }



    private interface FirebaseCallbackLog {
        void onCallsbackLog(ArrayList<LogData> log);


    }


    public void getData() {
        logDataList = new ArrayList<>();
        logDataList.clear();

        dbTotalBalance.child(hotelName).child("Log").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        Log.v("keys", "" + key);
                        LogData logDatas = ds.getValue(LogData.class);
                        logDataList.add(logDatas);
                    }

                    adapterLogData = new AdapterLogData(getContext(), logDataList);
                    adapterLogData.notifyDataSetChanged();
                    rec_log.setAdapter(adapterLogData);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
