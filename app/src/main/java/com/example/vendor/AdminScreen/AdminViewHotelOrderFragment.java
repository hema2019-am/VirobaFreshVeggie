package com.example.vendor.AdminScreen;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendor.Adapters.PdfDocumentAdapter;
import com.example.vendor.Adapters.adminOrderPagerHolder;
import com.example.vendor.Common;
import com.example.vendor.Constants;
import com.example.vendor.R;
import com.example.vendor.Vendor;
import com.example.vendor.orderItemData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminViewHotelOrderFragment extends Fragment {


    public AdminViewHotelOrderFragment() {
        // Required empty public constructor
    }

    String hotelName, hotelID, hotelDateTime, member, status, orderDate, orderTime, phoneNumber;

    TextView txt_hotelName, txt_orderNumber, txt_orderTime, txt_member, txt_total_Price;
    RecyclerView rl_item;

    FirebaseRecyclerAdapter<orderItemData, adminOrderPagerHolder> adapter;
    FirebaseRecyclerOptions<orderItemData> options;
    DatabaseReference mOrderContent, mOrder, mUserID, mShopUid;
    Button btn_completed_change;
    String key, shopUid, total;
    String address;

    Button btn_invoice_print;

    ArrayList<orderItemData> order1;

    ImageButton btn_back_viewHotelOrder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_hotel_order, container, false);


        try {
            Bundle bundle = getArguments();
            hotelName = bundle.getString("hotelName", "");
            hotelID = bundle.getString("orderID", "");
            hotelDateTime = bundle.getString("orderON", "");
            member = bundle.getString("member", "");
            status = bundle.getString("status", "");
            total = bundle.getString("total", "");
            orderDate = bundle.getString("orderDate", "");
            orderTime = bundle.getString("orderTime", "");

            txt_hotelName = view.findViewById(R.id.txt_hotelName);
            txt_orderNumber = view.findViewById(R.id.txt_orderIDNumber);
            txt_orderTime = view.findViewById(R.id.txt_orderONNumber);
            txt_total_Price = view.findViewById(R.id.txt_total_Price);
            txt_member = view.findViewById(R.id.txt_memberType);
            btn_completed_change = view.findViewById(R.id.btn_pending_complete);
            rl_item = view.findViewById(R.id.recycler_pending_items);
            btn_invoice_print = view.findViewById(R.id.btn_invoice_print);
            rl_item.setLayoutManager(new LinearLayoutManager(getContext()));
            btn_back_viewHotelOrder = view.findViewById(R.id.btn_back_viewHotelOrder);

            if (status.equalsIgnoreCase("Pending")) {
                btn_completed_change.setText("Approve");
                btn_invoice_print.setVisibility(View.GONE);
            } else if (status.equalsIgnoreCase("Approve")) {
                btn_completed_change.setText("Complete");
                btn_invoice_print.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase("Complete")) {
                btn_completed_change.setVisibility(View.GONE);
                btn_invoice_print.setVisibility(View.GONE);
            }


            subscribeToTopic();

            SharedPreferences admin = this.getActivity().getSharedPreferences("MyAdminLogin", MODE_PRIVATE);
            shopUid = admin.getString("adminId", "");

            txt_hotelName.setText("" + hotelName);
            txt_orderNumber.setText("" + hotelID);
            txt_orderTime.setText("" + hotelDateTime);
            txt_member.setText("" + member);
            txt_total_Price.setText("Total: " + total);
            mOrderContent = FirebaseDatabase.getInstance().getReference().child("OrderContent").child(hotelName).child(hotelID);
            mOrder = FirebaseDatabase.getInstance().getReference().child("Order").child(hotelName).child(hotelID);
            mUserID = FirebaseDatabase.getInstance().getReference().child("User");
            mUserID.orderByChild("UserHotelName").equalTo(hotelName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            key = ds.getKey();
                            mUserID.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()){
                                        try {
                                            address = snapshot.child("UserAddress").getValue().toString();
                                        }catch (Exception e){
                                            e.printStackTrace();
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





            btn_back_viewHotelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                    }
                }
            });

            mOrder.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        try {
                            phoneNumber = snapshot.child("phone").getValue().toString();
                        } catch (Exception e) {
                            e.getMessage();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




            btn_invoice_print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Vendor.getAppContext(), "e", Toast.LENGTH_SHORT).show();
                    createPDFFile(Common.getAppPath(getContext()) + hotelName + " " + hotelID + ".pdf");
                }
            });


            getAllOrders();




        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "On Create: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            btn_completed_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (status.equals("Pending")) {
                        mOrder.child("orderStatus").setValue("Approve").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                String message = "Order is now Approved";
                                Toast.makeText(Vendor.getAppContext(), "" + message, Toast.LENGTH_SHORT).show();

                                AdminOrdersFragment hotelOrderFragment = new AdminOrdersFragment();

                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();


                                fragmentTransaction.replace(R.id.fragment_Admin_container, hotelOrderFragment).commit();
                                prepareNotificationMessage(hotelID, message);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    } else if (status.equals("Approve")) {
                        mOrder.child("orderStatus").setValue("Complete").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                String message = "Order is now Completed";
                                Toast.makeText(Vendor.getAppContext(), "" + message, Toast.LENGTH_SHORT).show();

                                try {
                                    AdminOrdersFragment hotelOrderFragment = new AdminOrdersFragment();

                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();


                                    fragmentTransaction.replace(R.id.fragment_Admin_container, hotelOrderFragment).commit();
                                    prepareNotificationMessage(hotelID, message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "ViewHotel" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    private void createPDFFile(final String path) {


        if (new File(path).exists())
            new File(path).delete();

        readContentData(new FirebaseCallbackGetData() {
            @Override
            public void onCallsbackData(List<orderItemData> list) {
                try {


                    Log.v("ContentList", "" + list.toString());
                    Document document = new Document();

                    Toast.makeText(Vendor.getAppContext(), "Here", Toast.LENGTH_SHORT).show();
                    //Save
                    PdfWriter.getInstance(document, new FileOutputStream(path));

                    //open to write
                    document.open();

                    //Setting
                    document.setPageSize(PageSize.A4);
                    document.addCreationDate();
                    document.addAuthor("Biroba Fresh Veggie");

                    //Font Selection
                    BaseColor coloAccent = new BaseColor(0, 153, 204, 255);
                    float fontSize = 20.0f;
                    float valueFont = 26.0f;


                    //Custom Font
                    BaseFont fontName = BaseFont.createFont("assets/fonts/acumin_rpro.otf", "UTF-8", BaseFont.EMBEDDED);


                    //create tittle of documnet
                    Font HotelFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
                    addNewItem(document, "Biroba Fresh Veggie", Element.ALIGN_CENTER, HotelFont);

                    Font titleFont = new Font(fontName, 30.0f, Font.NORMAL, BaseColor.BLACK);
                    addNewItem(document, "Order Details", Element.ALIGN_CENTER, titleFont);

                    addLineSepartaor(document);

                    //Add orderNumber
                    Font orderNumberFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);
                    addNewItemLeftRight(document, "Order Number: ", " " + hotelID, orderNumberFont);

                    Font orderNumberValueFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);


                    addNewItemLeftRight(document, "Order Date and Time: ", " " + orderDate + ", " + orderTime, orderNumberFont);


                    addNewItemLeftRight(document, "Hotel Name: ", " " + hotelName, orderNumberFont);
                    addNewItemLeftRight(document, "Phone Number: ", " " + phoneNumber, orderNumberFont);
                    addNewItemLeftRight(document, "Address: ", " " + address, orderNumberFont);



                    //Add product order detail
                    addLineSpace(document);
                    addNewItem(document, "Product Detail", Element.ALIGN_CENTER, titleFont);
                    addLineSepartaor(document);


                    int OrderCount = 0;

                    for (orderItemData o1 : list) {

                        OrderCount++;
                        String itemName = o1.getItemName();
                        String itemQuants = o1.getItemQuantity();
                        String itemGms = o1.getItemGms();
                        String itemFinalPrice = o1.getItemFinalPrice();
                        String itemUnit = o1.getItemUnit();
                        if (Integer.parseInt(itemGms) == 0) {
                            addNewItemWithLeftAndRight(document, Integer.toString(OrderCount) + ". " + itemName + " " + itemQuants + itemUnit,
                                    "Rs. " + itemFinalPrice, orderNumberFont, orderNumberValueFont);
                        } else if (Integer.parseInt(itemQuants) == 0) {
                            addNewItemWithLeftAndRight(document, Integer.toString(OrderCount) + ". " + itemName + " " + itemGms + "gms",
                                    "Rs. " + itemFinalPrice, orderNumberFont, orderNumberValueFont);
                        } else if (Integer.parseInt(itemGms) > 0 && Integer.parseInt(itemQuants) > 0) {
                            addNewItemWithLeftAndRight(document, Integer.toString(OrderCount) + ". " + itemName + " " + itemQuants + itemUnit + ", " + itemGms + "gms",
                                    "Rs. " + itemFinalPrice, orderNumberFont, orderNumberValueFont);
                        }

                        addLineSepartaor(document);

                    }


                    //Total

                    addLineSpace(document);
                    addNewItemWithLeftAndRight(document, "Total", "Rs. " + total, titleFont, orderNumberValueFont);
                    document.close();


                    printPdf();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("File", "" + e.getMessage());
                }
            }
        });


    }

    private void printPdf() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);

            try {
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(getContext(), Common.getAppPath(getContext()) + hotelName + " " + hotelID + ".pdf");
                printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
            } catch (Exception e) {
                Log.v("log", "" + e.getMessage());
            }
        }
    }


    private void addNewItemLeftRight(Document document, String textLeft, String textRight, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft, textRightFont);
        Chunk chunkTextRight = new Chunk(textRight, textRightFont);
        Paragraph p = new Paragraph(chunkTextLeft);

        p.add(chunkTextRight);
        document.add(p);
    }

    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft, textLeftFont);
        Chunk chunkTextRight = new Chunk(textRight, textRightFont);
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);

    }

    private void addLineSepartaor(Document document) throws DocumentException {

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);


    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph());
    }

    private void addNewItem(Document document, String text, int alignCenter, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(alignCenter);
        document.add(paragraph);

    }


    private void readContentData(final FirebaseCallbackGetData firebaseCallbackGetData) {

        order1 = new ArrayList<>();
        order1.clear();
        mOrderContent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        orderItemData d1 = ds.getValue(orderItemData.class);
                        order1.add(d1);
                    }

                    firebaseCallbackGetData.onCallsbackData(order1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    interface FirebaseCallbackGetData {
        void onCallsbackData(List<orderItemData> list);
    }


    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Vendor.getAppContext(), "Done", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Vendor.getAppContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void prepareNotificationMessage(String orderId, String message) {
        //when user place order, send notification to vendor

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order" + orderId;
        String NOTIFICATION_MEASSAGE = "" + message;
        String NOTIFICATION_TYPE = "OrderStatusChange";


        //prepare json
        JSONObject notificationJO = new JSONObject();
        JSONObject notificationBodyJO = new JSONObject();

        try {
            notificationBodyJO.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJO.put("buyerUid", key);
            notificationBodyJO.put("sellerUid", shopUid);
            notificationBodyJO.put("orderId", orderId);
            notificationBodyJO.put("NotificationTitle", NOTIFICATION_TITLE);
            notificationBodyJO.put("notificationMessage", NOTIFICATION_MEASSAGE);


            notificationJO.put("to", NOTIFICATION_TOPIC);
            notificationJO.put("data", notificationBodyJO);

        } catch (Exception e) {

            Log.v("exe", "" + e.getMessage());
        }

        sendFCMNotification(notificationJO);


    }

    private void sendFCMNotification(JSONObject notificationJO) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJO, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);


                return headers;
            }
        };

        //enqui volly
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void getAllOrders() {


        try {
            Query query = mOrderContent;
            options = new FirebaseRecyclerOptions.Builder<orderItemData>().setQuery(query, orderItemData.class).setLifecycleOwner(this).build();
            adapter = new FirebaseRecyclerAdapter<orderItemData, adminOrderPagerHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull adminOrderPagerHolder holder, int position, @NonNull final orderItemData model) {
                    try {
                        if (model.getItemGms().equalsIgnoreCase("0")) {
                            holder.txt_itemName.setText(model.getItemName() + " x " + model.getItemQuantity() + " " + model.getItemUnit());
                        } else {
                            holder.txt_itemName.setText(model.getItemName() + " x " + model.getItemQuantity() + " " + model.getItemUnit() + " , " + model.getItemGms() + " gms");
                        }
                        if (model.getItemQuantity().equalsIgnoreCase("0")) {
                            holder.txt_itemName.setText(model.getItemName() + " x " + model.getItemGms() + " " + " gms");
                        }

                        holder.txt_itemPrice.setText("Rs. " + model.getItemFinalPrice());
                    } catch (Exception e) {
                        Log.v("excep", "" + e.getMessage());
                    }


                }

                @NonNull
                @Override
                public adminOrderPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.admin_order_pager_layout, parent, false);

                    return new adminOrderPagerHolder(view);
                }
            };

            rl_item.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(Vendor.getAppContext(), "All Orders" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
