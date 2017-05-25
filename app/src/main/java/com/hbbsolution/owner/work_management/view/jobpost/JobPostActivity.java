package com.hbbsolution.owner.work_management.view.jobpost;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hbbsolution.owner.R;
import com.hbbsolution.owner.adapter.BottomSheetAdapter;
import com.hbbsolution.owner.maid_near_by.view.filter.presenter.FilterPresenter;
import com.hbbsolution.owner.maid_near_by.view.filter.view.FilterActivity;
import com.hbbsolution.owner.model.TypeJob;
import com.hbbsolution.owner.model.TypeJobResponse;
import com.hbbsolution.owner.utils.ShowAlertDialog;
import com.hbbsolution.owner.work_management.model.geocodemap.GeoCodeMapResponse;
import com.hbbsolution.owner.work_management.presenter.JobPostPresenter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tantr on 5/14/2017.
 */

public class JobPostActivity extends AppCompatActivity implements JobPostView, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtTitlePost)
    EditText edtTitlePost;
    @BindView(R.id.edtDescriptionPost)
    EditText edtDescriptionPost;
    @BindView(R.id.edtAddressPost)
    EditText edtAddressPost;
    @BindView(R.id.job_post_txtType_job)
    EditText edtType_job;
    @BindView(R.id.rad_type_money_work)
    RadioButton rad_type_money_work;
    @BindView(R.id.rad_type_money_khoan)
    RadioButton rad_type_money_khoan;
    @BindView(R.id.txtPost)
    TextView txt_post_complete;
    @BindView(R.id.edt_monney_work)
    EditText edt_monney_work;
    @BindView(R.id.txtTime_start)
    TextView txtTime_start;
    @BindView(R.id.txtTime_end)
    TextView txtTime_end;
    @BindView(R.id.txtDate_start_work)
    TextView txtDate_start_work;
    @BindView(R.id.chb_tools_work)
    CheckBox chb_tools_work;

    private String mTitlePost, mTypeJob, mDescriptionPost, mAddressPost, mPackageId, mPrice,
            mDateStartWork, mTimeStartWork, mTimeEndWork;

    private boolean isTimeStart, mChosenTools = false;

    private double lat, lng;

    private HashMap<String, String> hashMapTypeJob = new HashMap<>();
    private List<String> listTypeJobName = new ArrayList<>();
    private JobPostPresenter mJobPostPresenter;
    private String token = "0eb910010d0252eb04296d7dc32e657b402290755a85367e8b7a806c7e8bd14b0902e541763a67ef41f2dfb3b9b4919869b609e34dbf6bace4525fa6731d1046";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);
        ButterKnife.bind(this);

        //setup view
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mJobPostPresenter = new JobPostPresenter(this);
        mJobPostPresenter.getAllTypeJob();

        getDateCurrent();

        edtType_job.setOnClickListener(this);
        txt_post_complete.setOnClickListener(this);
        txtTime_start.setOnClickListener(this);
        txtTime_end.setOnClickListener(this);
        txtDate_start_work.setOnClickListener(this);
        rad_type_money_work.setOnClickListener(this);
        rad_type_money_khoan.setOnClickListener(this);

        if (rad_type_money_work.isChecked()) {
            edt_monney_work.setEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rad_type_money_work:
                mPackageId = "000000000000000000000001";
                edt_monney_work.setEnabled(true);
                break;

            case R.id.rad_type_money_khoan:
                mPackageId = "000000000000000000000002";
                edt_monney_work.setEnabled(false);
                edt_monney_work.setText("");
                edt_monney_work.setHint("Nhập số tiền công");
                break;

            case R.id.job_post_txtType_job:
                eventClickTypeWork(listTypeJobName, edtType_job);
                break;

            case R.id.txtTime_start:
                getTimePicker(txtTime_start);
                break;

            case R.id.txtTime_end:
                getTimePicker(txtTime_end);
                break;

            case R.id.txtDate_start_work:
                getDatePicker();
                break;

            case R.id.txtPost:
                checkLocaltionOfOwner();
                break;

        }
    }

    @Override
    public void getAllTypeJob(TypeJobResponse typeJobResponse) {

        for (TypeJob typeJob : typeJobResponse.getData()) {
            hashMapTypeJob.put(typeJob.getName(), typeJob.getId());
            listTypeJobName.add(typeJob.getName());
        }
    }

    @Override
    public void getLocaltionAddress(GeoCodeMapResponse geoCodeMapResponse) {

        if (checkDataComplete()) {
            posData(geoCodeMapResponse);
        }
    }

    @Override
    public void displayNotifyJobPost(boolean isJobPost) {
        txt_post_complete.setEnabled(true);
        if (isJobPost){
            ShowAlertDialog.showAlert("Thành công", JobPostActivity.this);
        }else {
            ShowAlertDialog.showAlert("Thất bại", JobPostActivity.this);
        }
    }

    @Override
    public void displayNotFoundLocaltion() {

        ShowAlertDialog.showAlert("Vui lòng kiểm tra lại địa chỉ", JobPostActivity.this);
    }

    @Override
    public void displayError(String error) {

    }

    private void eventClickTypeWork(final List<String> listData, final EditText txtShow) {

        View view = getLayoutInflater().inflate(R.layout.job_post_bottom_sheet, null);
        //map components
        TextView txtCancel = (TextView) view.findViewById(R.id.txt_cancel);
        RecyclerView mRecycler = (RecyclerView) view.findViewById(R.id.recy_type_job);

        mRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //create bottom sheet
        BottomSheetAdapter mTypeJobtAdapter = new BottomSheetAdapter(JobPostActivity.this, listData);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setAdapter(mTypeJobtAdapter);
        mTypeJobtAdapter.notifyDataSetChanged();

        final Dialog mBottomSheetDialog = new Dialog(JobPostActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        mTypeJobtAdapter.setCallback(new BottomSheetAdapter.Callback() {
            @Override
            public void onItemClick(int position) {
                txtShow.setText(listData.get(position));
                String item = listData.get(position);
                String idTypeJob = hashMapTypeJob.get(item);
                mTypeJob = idTypeJob;
                mBottomSheetDialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void posData(GeoCodeMapResponse _geoCodeMapResponse) {

        double lat = _geoCodeMapResponse.getResults().get(0).getGeometry().getLocation().getLat();
        double lng = _geoCodeMapResponse.getResults().get(0).getGeometry().getLocation().getLng();
        mTitlePost = edtTitlePost.getText().toString();
        mDescriptionPost = edtDescriptionPost.getText().toString();
        mTimeStartWork = getTimeWork(txtTime_start.getText().toString());
        mTimeEndWork = getTimeWork(txtTime_end.getText().toString());
        if (!edt_monney_work.getText().toString().isEmpty()) {
            mPrice = edt_monney_work.getText().toString();
        } else {
            mPrice = "";
        }
        mPrice = edt_monney_work.getText().toString();

        if (chb_tools_work.isChecked()) {
            mChosenTools = true;
        }
       txt_post_complete.setEnabled(false);
        mJobPostPresenter.postJob(token, mTitlePost, mTypeJob, mDescriptionPost, mAddressPost, lat, lng,
                mChosenTools, mPackageId, mPrice, mTimeStartWork, mTimeEndWork );
    }

    private boolean checkDataComplete() {

        if (edtTitlePost.getText().toString().isEmpty() || edtDescriptionPost.getText().toString().isEmpty() ||
                edtAddressPost.getText().toString().isEmpty() || edtType_job.getText().toString().isEmpty()) {
            ShowAlertDialog.showAlert("Chua nhap day du tiêu đề ", JobPostActivity.this);
            return false;
        }

        if (rad_type_money_work.isChecked() && edt_monney_work.getText().toString().isEmpty()) {
            ShowAlertDialog.showAlert("Chua nhap so tien", JobPostActivity.this);
            return false;
        }

        if (!validateTimeWork()) {
            ShowAlertDialog.showAlert("Chon giờ chưa phù hợp ", JobPostActivity.this);
            return false;
        }

        return true;
    }

    private void getTimePicker(final TextView txtTime) {

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(0, 0, 0, hourOfDay, minute, 0);
                txtTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();

    }

    private void getDatePicker() {

        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                DateTime dateTime = new DateTime(calendar);
                mDateStartWork = dateTime.toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                txtDate_start_work.setText(simpleDateFormat.format(calendar.getTime()));
                if (CompareDays(txtDate_start_work.getText().toString())) {
                    ShowAlertDialog.showAlert("Sai ngày", JobPostActivity.this);
                } else {
                    ShowAlertDialog.showAlert("Đúng ngày", JobPostActivity.this);
                }
            }
        }, year, month, date);
        mDatePickerDialog.show();
    }

    private void getDateCurrent() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        txtDate_start_work.setText(simpleDateFormat.format(calendar.getTime()));

    }

    private boolean CompareDays(String dateStartWork) {

        Date date1 = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            date1 = sdf.parse(dateStartWork);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.after(date)) {
            return false;
        }
        return true;
    }

    private boolean CompareTime(String start, String end) {

        String startTime = start;
        String endTime = end;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date d1 = null, d2 = null;

        try {
            d1 = sdf.parse(startTime);
            d2 = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long elapsed = d2.getTime() - d1.getTime();
        if (elapsed > 0) {
            return true;
        }

        return false;
    }

    private boolean validateTimeWork() {

        if (CompareTime(txtTime_start.getText().toString(), txtTime_end.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    private String getTimeWork(String mTimeWork) {

        DateFormat mCreateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        String _TimeWork = txtDate_start_work.getText().toString() + " " + mTimeWork;
        Date mTimeAt = null;
        try {
            mTimeAt = mCreateTime.parse(_TimeWork);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new DateTime(mTimeAt).toString();
    }

    private void checkLocaltionOfOwner() {
        mAddressPost = edtAddressPost.getText().toString();
        if (!mAddressPost.isEmpty()) {
            mJobPostPresenter.getLocaltionAddress(mAddressPost);
        } else {
            ShowAlertDialog.showAlert("Vui lòng nhập địa chỉ đầy đủ!", JobPostActivity.this);
        }
    }
}