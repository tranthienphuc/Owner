package com.hbbsolution.owner.more.viet_pham.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hbbsolution.owner.R;
import com.hbbsolution.owner.more.duy_nguyen.StatisticActivity;
import com.hbbsolution.owner.more.viet_pham.View.signin.SignInActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 04/05/2017.
 */

public class MoreActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.more_title_toothbar)
    TextView txtMore_title_toothbar;
    @BindView(R.id.cv_sign_in)
    CardView cvSignIn;
    @BindView(R.id.cardview_statistic)
    CardView cvStatistic;
    @BindView(R.id.text_username)
    TextView txtUsername;
    @BindView(R.id.text_useraddress)
    TextView txtUseraddress;
    @BindView(R.id.img_avatar)
    ImageView img_avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        //config toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtMore_title_toothbar.setText(getResources().getString(R.string.more));
        addEvents();
        restoringPreferences();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addEvents() {
        cvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

//        lo_terms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(MoreActivity.this, "AAA", Toast.LENGTH_SHORT).show();
//                Intent itTerms = new Intent(MoreActivity.this, TermsActivity.class);
//                startActivity(itTerms);
//            }
//        });
        cvStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iStatistic = new Intent(MoreActivity.this, StatisticActivity.class);
                startActivity(iStatistic);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }


    public void restoringPreferences() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();


        //lấy user, pwd, nếu không thấy giá trị mặc định là rỗng
        String name = sharedPreferences.getString("name", "");
        String address = sharedPreferences.getString("address", "");
        String imageUrl = sharedPreferences.getString("avatar", "");

        txtUsername.setText(name);
        txtUseraddress.setText(address);

        Glide.with(this)
                .load(imageUrl)
                .into(img_avatar);


    }
}
