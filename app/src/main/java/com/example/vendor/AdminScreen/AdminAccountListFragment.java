package com.example.vendor.AdminScreen;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vendor.Adapters.SectionPagerAdapter;
import com.example.vendor.HotelList;
import com.example.vendor.R;
import com.example.vendor.Vendor;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminAccountListFragment extends Fragment {


    public AdminAccountListFragment() {
        // Required empty public constructor
    }



    ViewPager viewPager;
    TabLayout tabLayout;

    ImageButton btn_viewOrderHotel_back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_admin_account_list, container, false);

        try {
            viewPager = view.findViewById(R.id.viewPagerListPayment);
            tabLayout = view.findViewById(R.id.tabLayoutListPayment);
            btn_viewOrderHotel_back = view.findViewById(R.id.btn_viewOrderHotel_back);


            btn_viewOrderHotel_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(Vendor.getAppContext(), "Tab: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
       return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            setUpViewPager(viewPager);

            tabLayout.setupWithViewPager(viewPager);

        }catch (Exception e){
            Toast.makeText(Vendor.getAppContext(), "On Activity: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setUpViewPager(ViewPager viewPager) {
        try{
            SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
            adapter.addFragment(new HotelViewOrderFragment(),"Hotel");
            adapter.addFragment(new HotelPaymentFragment(),"Account");



            viewPager.setAdapter(adapter);
            viewPager.getAdapter().notifyDataSetChanged();


        }catch (Exception e){
            Toast.makeText(Vendor.getAppContext(), "setYpViewPager: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }





    }

}
