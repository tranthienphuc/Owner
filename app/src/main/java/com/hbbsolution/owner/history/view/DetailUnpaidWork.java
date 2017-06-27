package com.hbbsolution.owner.history.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hbbsolution.owner.R;
import com.hbbsolution.owner.history.model.liabilities.LiabilitiesHistory;
import com.hbbsolution.owner.maid_profile.view.MaidProfileActivity;
import com.hbbsolution.owner.utils.WorkTimeValidate;
import com.hbbsolution.owner.work_management.model.chekout.DataBill;
import com.hbbsolution.owner.work_management.model.workmanagerpending.DatumPending;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailUnpaidWork extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title_toothbar)
    TextView txt_title_toothbar;
    @BindView(R.id.txtNameInfoMaid)
    TextView txtNameInfoMaid;
    @BindView(R.id.txtAddressInfoMaid)
    TextView txtAddressInfoMaid;
    @BindView(R.id.img_avatarInfoMiad)
    ImageView img_avatarInfoMiad;
    @BindView(R.id.img_job_type)
    ImageView img_job_type;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtType)
    TextView txtType;
    @BindView(R.id.txtContent)
    TextView txtContent;
    @BindView(R.id.txtPrice)
    TextView txtPrice;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtTimeDoWrok)
    TextView txtTimeDoWrok;
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    @BindView(R.id.lo_infoMaid)
    RelativeLayout lo_infoMaid;

    private LiabilitiesHistory mLiabilitiesHistory;
    private DatumPending mDatum;
    private DataBill mDataBill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_unpaid_work);

        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lo_infoMaid.setOnClickListener(this);


        Bundle extras = getIntent().getExtras();
        Bundle bdDataBill = getIntent().getBundleExtra("databill");

        mLiabilitiesHistory = (LiabilitiesHistory) extras.getSerializable("liability");
        mDatum = (DatumPending) bdDataBill.getSerializable("mDatum");
        mDataBill = (DataBill) bdDataBill.getSerializable("datacheckout");

        if (mLiabilitiesHistory!=null) {
            txtNameInfoMaid.setText(mLiabilitiesHistory.getTask().getStakeholders().getReceived().getInfo().getName());
            txtAddressInfoMaid.setText(mLiabilitiesHistory.getTask().getStakeholders().getReceived().getInfo().getAddress().getName());
            Glide.with(this).load(mLiabilitiesHistory.getTask().getStakeholders().getReceived().getInfo().getImage())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .centerCrop()
                    .dontAnimate()
                    .into(img_avatarInfoMiad);

            txtTitle.setText(mLiabilitiesHistory.getTask().getInfo().getTitle());
            txtType.setText(mLiabilitiesHistory.getTask().getInfo().getWork().getName());
            txtContent.setText(mLiabilitiesHistory.getTask().getInfo().getDescription());
            txtPrice.setText(String.valueOf(mLiabilitiesHistory.getTask().getInfo().getPrice()));
            txtAddress.setText(mLiabilitiesHistory.getTask().getInfo().getAddress().getName());
            txtDate.setText(WorkTimeValidate.getDatePostHistory(mLiabilitiesHistory.getTask().getHistory().getUpdateAt()));
            String mStartTime = WorkTimeValidate.getTimeWork(mLiabilitiesHistory.getTask().getInfo().getTime().getStartAt());
            String mEndTime = WorkTimeValidate.getTimeWork(mLiabilitiesHistory.getTask().getInfo().getTime().getEndAt());
            txtTimeDoWrok.setText(mStartTime + " - " + mEndTime);
            Glide.with(this).load(mLiabilitiesHistory.getTask().getInfo().getWork().getImage())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .centerCrop()
                    .dontAnimate()
                    .into(img_job_type);
        }
        else
        {
            txtNameInfoMaid.setText(mDatum.getStakeholders().getMadi().getInfo().getName());
            txtAddressInfoMaid.setText(mDatum.getStakeholders().getMadi().getInfo().getAddress().getName());
            Glide.with(this).load(mDatum.getStakeholders().getMadi().getInfo().getImage())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .centerCrop()
                    .dontAnimate()
                    .into(img_avatarInfoMiad);

            txtTitle.setText(mDatum.getInfo().getTitle());
            txtType.setText(mDatum.getInfo().getWork().getName());
            txtContent.setText(mDatum.getInfo().getDescription());
            txtPrice.setText(String.valueOf(mDatum.getInfo().getPrice()));
            txtAddress.setText(mDatum.getInfo().getAddress().getName());
            txtDate.setText(WorkTimeValidate.getDatePostHistory(mDatum.getHistory().getUpdateAt()));
            String mStartTime = WorkTimeValidate.getTimeWork(mDatum.getInfo().getTime().getStartAt());
            String mEndTime = WorkTimeValidate.getTimeWork(mDatum.getInfo().getTime().getEndAt());
            txtTimeDoWrok.setText(mStartTime + " - " + mEndTime);
            Glide.with(this).load(mDatum.getInfo().getWork().getImage())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .centerCrop()
                    .dontAnimate()
                    .into(img_job_type);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lo_infoMaid:
                intent = new Intent(DetailUnpaidWork.this, MaidProfileActivity.class);
                intent.putExtra("work", mLiabilitiesHistory.getTask());
                startActivity(intent);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}