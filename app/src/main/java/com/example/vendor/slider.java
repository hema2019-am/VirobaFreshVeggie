package com.example.vendor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.example.vendor.Adapters.MyAdapter;
import com.example.vendor.UserScreens.HomeActivity;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;


public class slider extends AhoyOnboarderActivity {


    ViewPager viewPager;
    MyAdapter myAdapter;
    SliderView sliderView;
    RelativeLayout btn_getStarted;


    //ImageSlider imageSlider = findViewById(R.id.slider);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_slider);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("" ,"",R.drawable.onboard_first);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("" ,"",R.drawable.onboard_second);
        AhoyOnboarderCard ahoyOnboarderCard3= new AhoyOnboarderCard("" ,"",R.drawable.onboard_thriid);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

        ahoyOnboarderCard1.setIconLayoutParams(500,450,100,100,0,20);
        ahoyOnboarderCard2.setIconLayoutParams(500,450,100,100,0,20);
        ahoyOnboarderCard3.setIconLayoutParams(500,450,100,100,0,20);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        for(AhoyOnboarderCard page : pages){
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.grey_600);
        }

        setFinishButtonTitle("Get Started");
        showNavigationControls(false);




        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.solid_one);
        colorList.add(R.color.solid_one);
        colorList.add(R.color.solid_one);

        setColorBackground(colorList);

        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/acumin_rpro.otf");
        setFont(face);

        setOnboardPages(pages);





    }

    @Override
    public void onFinishButtonPressed() {
       Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
       startActivity(intent);
    }
}
