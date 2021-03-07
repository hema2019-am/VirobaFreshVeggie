package com.example.vendor.AdminScreen;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vendor.Adapters.AdapterCategoryOrders;
import com.example.vendor.Adapters.AdapterHotelList;
import com.example.vendor.Adapters.HolderHotelName;
import com.example.vendor.AdminHotelOrder;
import com.example.vendor.HotelNameContsnats;
import com.example.vendor.R;
import com.example.vendor.TotalHotelConstructor;
import com.example.vendor.Vendor;
import com.example.vendor.itemList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotelItemsFragment extends Fragment {


    public HotelItemsFragment() {
        // Required empty public constructor
    }

    EditText edt_search;
    RecyclerView rl_gold, rl_silver, rl_bronze, rl_fixed, rl_guest;

    ArrayList<AdminHotelOrder> guest;
    int i = 0;


    DatabaseReference dbRef, databaseReference, orderContent, mHotelList, mProductPurchase, Hotel;

    String currentDate, today;
    String memberType, dataHotel;
    int counter = 0;

    ValueEventListener valueEventListener;
    ArrayList<AdminHotelOrder> listGold;
    ArrayList<AdminHotelOrder> listSilver;
    ArrayList<AdminHotelOrder> listBronze;
    ArrayList<AdminHotelOrder> listFixed;
    ArrayList<AdminHotelOrder> listGuest;
    AdapterHotelList adapter;

    HashMap<String, Double> totalSummary;


    Button btn_export;
    String monthInString;

    String orderId, orderTime, orderDate, hotelNames, memberShip, itemName, Qty, unit;

    HashSet<AdminHotelOrder> hashSet;
    // int counter = 0;

    int count = 0;
    boolean flag = false;

    ArrayList<TotalHotelConstructor> list;
    Button btn_datepicker;
    DatePickerDialog.OnDateSetListener mDateSetListenr;
    TextView txt_date;

    String dataHotelName;

    ArrayList<String> hotelList;

    ArrayList<String> dataList;


    ArrayList<String> itemQuantityList, itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_items, container, false);

        try {
            edt_search = view.findViewById(R.id.edt_hotelWiseSearch);
            rl_gold = view.findViewById(R.id.rl_gold);
            rl_bronze = view.findViewById(R.id.rl_bronze);
            rl_fixed = view.findViewById(R.id.rl_fixed);
            rl_silver = view.findViewById(R.id.rl_silver);
            rl_guest = view.findViewById(R.id.rl_guest);
            btn_export = view.findViewById(R.id.btn_export_to_excel_hotelList);
            btn_datepicker = view.findViewById(R.id.btn_hotel_items_date_picker);
            txt_date = view.findViewById(R.id.txt_hotel_item_date);


            System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
            System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.dd");
            currentDate = sdf.format(new Date());// 12.06.1999
            today = currentDate.replace(".", "");

            //oredrContent
            orderContent = FirebaseDatabase.getInstance().getReference().child("OrderContent");

            totalSummary = new HashMap<>();


            //order
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Order");


            //hotelwiseList
            mHotelList = FirebaseDatabase.getInstance().getReference().child("HotelWiseList");

            mProductPurchase = FirebaseDatabase.getInstance().getReference().child("ProductPurchase");
            Hotel = FirebaseDatabase.getInstance().getReference().child("Hotels");


            rl_gold.setLayoutManager(new LinearLayoutManager(getContext()));
            rl_bronze.setLayoutManager(new LinearLayoutManager(getContext()));
            rl_fixed.setLayoutManager(new LinearLayoutManager(getContext()));
            rl_silver.setLayoutManager(new LinearLayoutManager(getContext()));
            rl_guest.setLayoutManager(new LinearLayoutManager(getContext()));


            showGoldHotels();
            showSilverHotels();
            showFixedHotels();
            showBronzeHotels();
            showGuest();
            loadData(today, currentDate);
            txt_date.setText("" + currentDate);


            btn_export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exportToExcel();

                }
            });

            //for date picker
            btn_datepicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    monthInString = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                    int month = cal.get(Calendar.MONDAY);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListenr,
                            year, month, day
                    );

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                }
            });


            mDateSetListenr = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    String date = year + "." + monthInString + "." + day;
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
                    loadData(today, currentDate);
                    showGoldHotels();
                    showBronzeHotels();
                    showFixedHotels();
                    showGuest();
                    showSilverHotels();

                    txt_date.setText("" + strDate);

                }
            };

        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "On Create: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    private void exportToExcel() {

//to export to the excel

        read(new FirebaseListCallBackData() {
            @Override
            public void onListCallBackData(final List<String> itemListData) {
                Log.v("itemDatas", "" + itemListData.toString());   //"sudha","3.0kg"

                readData(new FirebaseCallback() {
                    @Override
                    public void onCallsback(final List<String> list) {
                        Log.v("hotelist", "" + list.toString());   //"samraj","hdjhd"


                        File folder = new File(Environment.getExternalStorageDirectory() + "/VendorData");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }
                        if (success) {
                            // Do something on success
                            String ext = Environment.getExternalStorageDirectory().toString();
                            final File file = new File(ext, "VendorData/" + txt_date.getText().toString() + "hotelWise.csv");

                            dataList = new ArrayList<>();


                            readItemData(new FirebaseListCallBack() {
                                @Override
                                public void onListCallBack(List<String> itemDataList) { //"samraj","76kg", "gyfhfd","89kg"
                                    Log.v("data", "" + itemDataList);







                                    String[] list1 = new String[0];
                                    ArrayList<String> spaceHotel = new ArrayList<>();

                                    for (int i = 0; i < list.size(); i++) {
                                        String hote = list.get(i);
                                        hote = hote.replaceAll(" ", "\n");
                                        spaceHotel.add(hote);
                                    }

                                    list1 = spaceHotel.toArray(list1);

                                    Log.v("List1", "" + list1.length);


                                    writer.writeNext(list1);
                                    ArrayList<String> ItemList = new ArrayList<>();
                                    Log.v("itemData", "" + itemDataList.toString()); // items
                                    Log.v("itemQuantity", "" + itemListData.toString()); // listOfData
                                    //list = hotels
                                    // itemsData = items
                                    //itemQuantity = data


                                    int counter = 1;
                                    for (int item = 0; item < itemDataList.size(); ) {
                                        int sr_no = counter++;
                                        dataList.add(Integer.toString(sr_no));
                                        dataList.add(itemDataList.get(item + 1));
                                        for (int list = 0; list < itemListData.size(); list += 3) {
                                            if (itemDataList.get(item).equalsIgnoreCase(itemListData.get(list))) {
                                                String hotelName = itemListData.get(list + 1);
                                                String quantity = itemListData.get(list + 2);
                                                ItemList.add(hotelName);
                                                ItemList.add(quantity);

                                            }
                                        }

                                        System.out.println("ItelmList: " + ItemList.toString());
                                        for (int hotel = 2; hotel < list.size(); hotel++) {
                                            for (int itemList = 0; itemList < ItemList.size(); itemList += 2) {
                                                if (list.get(hotel).equalsIgnoreCase(ItemList.get(itemList))) {
                                                    String quantitys = ItemList.get(itemList + 1);
                                                    dataList.add(quantitys);
                                                    flag = false;
                                                    break;
                                                } else {
                                                    flag = true;
                                                }

                                            }

                                            if (flag == true) {
                                                dataList.add("-");
                                            }


                                        }


                                        System.out.println("Data List " + dataList.toString());

                                        String[] dataQaunt = new String[0];
                                        dataQaunt = dataList.toArray(dataQaunt);

                                        Log.v("List1", "" + list1.length);


                                        writer.writeNext(dataQaunt);
                                        dataList.clear();
                                        ItemList.clear();
                                        item += 2;


                                    }


                                    try {
                                        writer.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });


                            Toast.makeText(Vendor.getAppContext(), "OK", Toast.LENGTH_SHORT).show();


                        }
                    }
                });


            }
        });


    }


    // load the data in firebase of hotelwise
    private void loadData(final String todays, final String currentDates) {


        try {
            dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
            count = 0;
            mProductPurchase.removeValue();
            Hotel.removeValue();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            final String keys = ds.getKey();

                            Log.v("keyHotel", "" + keys);


                            dbRef.child(keys).orderByChild("orderDate").equalTo(currentDates).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {

                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            Log.v("size", Integer.toString(totalSummary.size()));
                                            // final String hotelName = child.child("HotelName").getValue().toString();
                                            //Log.v("hotelName", hotelName);
                                            final String key = child.getKey();





                                            Log.v("orderKey", "" + key);

                                            orderContent.child(keys).child(key).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    totalSummary.clear();
                                                    for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                        String itemMarathi = "";

                                                            final String itemName = dataSnapshot.child("itemName").getValue().toString();
                                                            final double[] itemQuantity = {Double.parseDouble(dataSnapshot.child("itemQuantity").getValue().toString())};
                                                            final String itemUnit = dataSnapshot.child("itemUnit").getValue().toString();

                                                        try {
                                                            itemMarathi = dataSnapshot.child("itemNameInMarathi").getValue().toString();
                                                        }catch (Exception e){
                                                            Log.v("Exception",""+e.getMessage());
                                                        }




                                                        HashMap<String,Object> productDetail = new HashMap<>();
                                                        productDetail.put("itemName",itemName);
                                                        productDetail.put("marathi_name",itemMarathi);
                                                        Hotel.child(keys).child("hotelName").setValue(keys);
                                                        mProductPurchase.child(itemName).updateChildren(productDetail);


                                                        try {
                                                            String itemGms = dataSnapshot.child("itemGms").getValue().toString();

                                                            if (Integer.parseInt(itemGms) > 0) {
                                                                double itemGm = Double.parseDouble(itemGms) / 1000;
                                                                itemQuantity[0] = itemQuantity[0] + itemGm;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.v("excep", "" + e.getMessage());
                                                        }


                                                        Log.v("itemNameHotel", Double.toString(itemQuantity[0]));
                                                        if (totalSummary.containsKey(itemName)) {
                                                            double mapQuantity = totalSummary.get(itemName);


                                                            final double totalQuantity = mapQuantity + itemQuantity[0];
                                                            totalSummary.put(itemName, totalQuantity);


                                                            Log.v("totalQuHotel", Double.toString(totalQuantity));
                                                            final String finalItemUnit = itemUnit;
                                                            final HashMap<String, Object> update = new HashMap<>();
                                                            update.put("itemName", itemName);
                                                            update.put("itemQuantity", Double.toString(totalQuantity));
                                                            update.put("itemUnit", finalItemUnit);

                                                            // mProductPurchase.child(itemName).child("itemCount").setValue(Integer.toString(count));

                                                            update.put("hotelName", keys);


                                                            Log.v("key", key);

                                                            mHotelList.child(todays).child(itemName).child(keys).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            });


                                                            Log.v("hasmapHotel", totalSummary.get(itemName).toString());
                                                            Toast.makeText(Vendor.getAppContext(), totalSummary.get(itemName).toString(), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            totalSummary.put(itemName, itemQuantity[0]);
                                                            // Log.v("hasmap",totalSummary.get(itemName).toString());
                                                            //  Toast.makeText(Vendor.getAppContext(), totalSummary.get(itemName).toString(), Toast.LENGTH_SHORT).show();


                                                            Log.v("itemQu", Double.toString(itemQuantity[0]));
                                                            final double finalItemQuantity = itemQuantity[0];
                                                            Log.v("key", key);

                                                            final String finalItemUnit1 = itemUnit;

                                                            final HashMap<String, Object> update = new HashMap<>();
                                                            update.put("itemName", itemName);
                                                            update.put("itemQuantity", Double.toString(finalItemQuantity));
                                                            update.put("itemUnit", finalItemUnit1);


                                                            update.put("hotelName", keys);


                                                            mHotelList.child(todays).child(itemName).child(keys).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            });
                                                        }


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }


                            });




                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            dbRef.addValueEventListener(valueEventListener);








        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "show Items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null && dbRef != null) {
            dbRef.removeEventListener(valueEventListener);
        }
    }


    //firebase callback for itemQuantity list which creates list like, {"Sudha","45","Samraj","78"}
    private void read(final FirebaseListCallBackData firebaseListCallBackData) {

        dataList = new ArrayList<>();
        readData(new FirebaseCallback() {
            @Override
            public void onCallsback(final List<String> list) {
                Log.v("dataL", "" + list.toString());


                final int[] counter = {0};
                readItemData(new FirebaseListCallBack() {
                    @Override
                    public void onListCallBack(final List<String> itemLis) {
                        Log.v("quants", itemLis.toString());


                        for (i = 0; i < itemLis.size(); i++) {
                            Log.v("hotelList", "" + itemLis.get(i));

                            itemQuantityList = new ArrayList<>();
                            final String items = itemList.get(i);

                            mHotelList.child(txt_date.getText().toString().replace(".", "")).child(items).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {

                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            Log.v("itemKey", "" + ds.getKey());
                                            try {
                                                String quant = ds.child("itemQuantity").getValue().toString();
                                                String unit = ds.child("itemUnit").getValue().toString();
                                                Log.v("jgig", "" + items + ds.getKey() + quant + unit);

                                                itemQuantityList.add(items);
                                                itemQuantityList.add(ds.getKey());
                                                itemQuantityList.add(quant + unit);


                                                //Toast.makeText(Vendor.getAppContext(), "HotelData " + list.get(finalJ) + quant + unit, Toast.LENGTH_SHORT).show();

                                                Log.v("itemdetails", "" + itemQuantityList.toString());
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                        }


                                        firebaseListCallBackData.onListCallBackData(itemQuantityList);


                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            // itemQuantityList.clear();


                        }


                    }
                });


            }
        });


    }


    //firebase callback for itemList, create list like {"Cabbage","Cauliflower"}
    private void readItemData(final FirebaseListCallBack firebaseListCallback) {

        itemList = new ArrayList<>();

        mProductPurchase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    try {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String dataItem = ds.getKey();
                            String marathi_name = ds.child("marathi_name").getValue().toString();
                            if (!itemList.contains(dataItem)) {
                                itemList.add(dataItem);
                                itemList.add(marathi_name);
                            }


                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    firebaseListCallback.onListCallBack(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //firebase callback for hotelList, create list like {"Sudha","Samraj"}
    private void readData(final FirebaseCallback firebaseCallback) {


        hotelList = new ArrayList<>();


        Hotel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    if (!hotelList.contains("Sr.No")) {
                        hotelList.add("Sr.No");
                    }
                    if (!hotelList.contains("Items")) {
                        hotelList.add("Items");
                    }


                    for (DataSnapshot ds : snapshot.getChildren()) {

                        String key = ds.getKey();

                        Log.v("hotels", "" + key);
                        if (!hotelList.contains(key)) {
                            hotelList.add(key);

                        }

                        Log.v("hotelListss", "" + hotelList.toString());


                    }

                    firebaseCallback.onCallsback(hotelList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private interface FirebaseListCallBackData {
        void onListCallBackData(List<String> itemList);
    }


    private interface FirebaseListCallBack {
        void onListCallBack(List<String> itemList);
    }

    private interface FirebaseCallback {
        void onCallsback(List<String> list);


    }

    private interface FirebaseCallbackMarathi {
        void onCallsbackMarathi(List<String> list);


    }

    private void readMarathiName(final FirebaseCallbackMarathi firebaseCallbackMarathi) {

        final ArrayList hotelListMarathi = new ArrayList<>();


        Hotel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {


                    for (DataSnapshot ds : snapshot.getChildren()) {

                        String key = ds.getKey();
                        String marathi = ds.child("marathi_name").getValue().toString();
                        Log.v("hotels", "" + key);
                        if (!hotelListMarathi.contains(marathi)) {
                            hotelListMarathi.add(marathi);
                        }

                        Log.v("hotelListss", "" + hotelList.toString());


                    }

                    firebaseCallbackMarathi.onCallsbackMarathi(hotelListMarathi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //shows gold hotel list
    private void showGoldHotels() {

        listGold = new ArrayList<>();
        listGold.clear();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        dbRef.child(key).orderByChild("orderDate").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {

                                        AdminHotelOrder orders = ds.getValue(AdminHotelOrder.class);
                                        memberType = orders.getMemberType();
                                        dataHotel = orders.getHotelName();

                                        Log.v("gut", "" + memberType);
                                        if (memberType.equals("Gold")) {


                                            listGold.add(orders);

                                        }

                                        Set<AdminHotelOrder> a = new HashSet<AdminHotelOrder>();
                                        a.addAll(listGold);

                                        listGold = new ArrayList<>();
                                        listGold.addAll(a);
                                    }

                                    adapter = new AdapterHotelList(getContext(), listGold, txt_date.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    rl_gold.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //shows silver hotel list
    public void showSilverHotels() {
        listSilver = new ArrayList<>();
        listSilver.clear();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        dbRef.child(key).orderByChild("orderDate").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        AdminHotelOrder orders = ds.getValue(AdminHotelOrder.class);
                                        memberType = orders.getMemberType();
                                        dataHotel = orders.getHotelName();
                                        if (memberType.equals("Silver")) {


                                            listSilver.add(orders);

                                        }

                                        Set<AdminHotelOrder> a = new HashSet<AdminHotelOrder>();
                                        a.addAll(listSilver);

                                        listSilver = new ArrayList<>();
                                        listSilver.addAll(a);
                                    }

                                    adapter = new AdapterHotelList(getContext(), listSilver, txt_date.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    rl_silver.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //shows bronze hotel list
    public void showBronzeHotels() {
        listBronze = new ArrayList<>();
        listBronze.clear();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        dbRef.child(key).orderByChild("orderDate").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        AdminHotelOrder orders = ds.getValue(AdminHotelOrder.class);
                                        memberType = orders.getMemberType();
                                        dataHotel = orders.getHotelName();
                                        if (memberType.equals("Bronze")) {


                                            listBronze.add(orders);

                                        }

                                        Set<AdminHotelOrder> a = new HashSet<AdminHotelOrder>();
                                        a.addAll(listBronze);

                                        listBronze = new ArrayList<>();
                                        listBronze.addAll(a);
                                    }

                                    adapter = new AdapterHotelList(getContext(), listBronze, txt_date.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    rl_bronze.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //shows guest hotel list
    public void showGuest() {
        listGuest = new ArrayList<>();
        listGuest.clear();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        dbRef.child(key).orderByChild("orderDate").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        AdminHotelOrder orders = ds.getValue(AdminHotelOrder.class);
                                        memberType = orders.getMemberType();
                                        dataHotel = orders.getHotelName();
                                        if (memberType.equalsIgnoreCase("Guest")) {


                                            listGuest.add(orders);

                                        }

                                        Set<AdminHotelOrder> a = new HashSet<AdminHotelOrder>();
                                        a.addAll(listGuest);

                                        listGuest = new ArrayList<>();
                                        listGuest.addAll(a);
                                    }

                                    adapter = new AdapterHotelList(getContext(), listGuest, txt_date.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    rl_guest.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //shows fix hotel list
    public void showFixedHotels() {
        listFixed = new ArrayList<>();
        listFixed.clear();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Order");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        dbRef.child(key).orderByChild("orderDate").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        AdminHotelOrder orders = ds.getValue(AdminHotelOrder.class);
                                        memberType = orders.getMemberType();
                                        dataHotel = orders.getHotelName();
                                        if (memberType.equals("Fix")) {


                                            listFixed.add(orders);

                                        }

                                        Set<AdminHotelOrder> a = new HashSet<AdminHotelOrder>();
                                        a.addAll(listFixed);

                                        listFixed = new ArrayList<>();
                                        listFixed.addAll(a);
                                    }

                                    adapter = new AdapterHotelList(getContext(), listFixed, txt_date.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    rl_fixed.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
