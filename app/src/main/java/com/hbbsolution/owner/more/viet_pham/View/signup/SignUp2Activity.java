package com.hbbsolution.owner.more.viet_pham.View.signup;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbbsolution.owner.R;
import com.hbbsolution.owner.base.InternetConnection;
import com.hbbsolution.owner.more.duy_nguyen.TermsActivity;
import com.hbbsolution.owner.more.viet_pham.Model.signin_signup.BodyResponse;
import com.hbbsolution.owner.more.viet_pham.Model.signin_signup.CheckUsernameEmailResponse;
import com.hbbsolution.owner.more.viet_pham.Model.signin_signup.DataUpdateResponse;
import com.hbbsolution.owner.more.viet_pham.Presenter.CheckUsernameAndEmailPresenter;
import com.hbbsolution.owner.more.viet_pham.Presenter.ImageFilePathPresenter;
import com.hbbsolution.owner.more.viet_pham.Presenter.RegisterPresenter;
import com.hbbsolution.owner.more.viet_pham.View.CheckUsernameAndEmailView;
import com.hbbsolution.owner.more.viet_pham.View.MoreView;
import com.hbbsolution.owner.utils.Constants;
import com.hbbsolution.owner.utils.EmailValidate;
import com.hbbsolution.owner.utils.EncodeImage;
import com.hbbsolution.owner.utils.ShowAlertDialog;
import com.hbbsolution.owner.work_management.model.geocodemap.GeoCodeMapResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 5/10/2017.
 */

public class SignUp2Activity extends AppCompatActivity implements MoreView, CheckUsernameAndEmailView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.edit_gender)
    EditText edtGender;
    @BindView(R.id.edit_email)
    EditText edtEmail;
    @BindView(R.id.edit_full_name)
    EditText edtFullName;
    @BindView(R.id.edit_number)
    EditText edtNumber;
    @BindView(R.id.edit_location)
    EditText edtLocation;
    @BindView(R.id.image_avatar)
    CircleImageView ivAvatar;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;
    private Uri mUriChooseImage;
    private String mFilePath = "";
    private int iGender;
    private RegisterPresenter mRegisterPresenter;
    private ProgressDialog mProgressDialog;
    private double mLat;
    private double mLng;
    private String mUserName, mPassword, mEmail, mFullName, mPhoneName, mLocation, mGender;
    private int permissionCheck;
    private static final int REQUEST_READ_EXTERNAL_PERMISSION = 1;
    private static final int CAMERA_REQUEST = 100;
    private Intent iChooseImage;
    private Uri fileUri;
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] PERMISSIONS_GALLERY = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private CheckUsernameAndEmailPresenter mCheckUsernameAndEmailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);
        ButterKnife.bind(this);
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addEvents();

        mProgressDialog = new ProgressDialog(this);
        mRegisterPresenter = new RegisterPresenter(this);
        mCheckUsernameAndEmailPresenter = new CheckUsernameAndEmailPresenter(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addEvents() {
        // Event click next page
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start get data from Sign up 1
                Intent iSignUp1 = getIntent();
                Bundle bSignUp1 = iSignUp1.getBundleExtra("bNextPage");
                mUserName = bSignUp1.getString("username");
                mPassword = bSignUp1.getString("password");
                // Start get data from Sign in 1

                // Start get data from Edittext of Sign up 2
                mEmail = edtEmail.getText().toString();
                mFullName = edtFullName.getText().toString();
                mGender = edtGender.getText().toString();
                mPhoneName = edtNumber.getText().toString();
                mLocation = edtLocation.getText().toString();
                // End get data from Edittext of Sign up 2
                if (mGender.equals(getResources().getString(R.string.gender_men))) {
                    iGender = 0;
                } else {
                    iGender = 1;
                }

                // Start transfer data from Sign up 2 to page terms
                if (mEmail.trim().length() == 0 || mFullName.trim().length() == 0 || mGender.length() == 0 || mPhoneName.trim().length() == 0 ||
                        mLocation.trim().length() == 0) {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.vui_long_dien_day_du), SignUp2Activity.this);
                } else {
                    if (EmailValidate.IsOk(mEmail)) {
                        if (mPhoneName.length() >= 10 && mPhoneName.length() <= 11 && (mPhoneName.charAt(0) == '0')) {
                            if (InternetConnection.getInstance().isOnline(SignUp2Activity.this)) {
                                mProgressDialog.show();
                                mProgressDialog.setMessage(getResources().getString(R.string.loading));
                                mProgressDialog.setCanceledOnTouchOutside(false);
                                mCheckUsernameAndEmailPresenter.checkEmail(mEmail);
                            } else {
                                ShowAlertDialog.showAlert(getResources().getString(R.string.no_internet), SignUp2Activity.this);
                            }
                        } else {
                            ShowAlertDialog.showAlert(getResources().getString(R.string.invalid_phone), SignUp2Activity.this);
                        }
                    } else {
                        ShowAlertDialog.showAlert(getResources().getString(R.string.email_wrong), SignUp2Activity.this);
                    }

                }
                // End transfer data from Sign up 2 to page terms
            }
        });

        // Event choosen gender
        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getLayoutInflater().inflate(R.layout.gender_bottom_sheet, null);

                final Dialog mBottomSheetDialog = new Dialog(SignUp2Activity.this, R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView(v);
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                mBottomSheetDialog.show();

                final TextView mTextViewMen = (TextView) v.findViewById(R.id.text_men);
                final TextView mTextViewWomen = (TextView) v.findViewById(R.id.text_women);

                mTextViewMen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String men = mTextViewMen.getText().toString();
                        edtGender.setText(men);
                        mBottomSheetDialog.dismiss();
                    }
                });

                mTextViewWomen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String wommen = mTextViewWomen.getText().toString();
                        edtGender.setText(wommen);
                        mBottomSheetDialog.dismiss();

                    }
                });
            }
        });

        // Choose image for Cricle Image View
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePhotoIntent, Constants.CAMERA_INTENT);
        }
    }

    private boolean verifyOpenCamera() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA
            );

            return false;
        }
        return true;
    }

    private boolean verifyGallery() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_GALLERY, REQUEST_READ_EXTERNAL_PERMISSION
            );

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(SignUp2Activity.this,getResources().getString(R.string.setting_permission), Toast.LENGTH_LONG).show();
                    // User selected the Never Ask Again Option
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                } else {
                    Toast.makeText(SignUp2Activity.this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    // do your work here
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(SignUp2Activity.this, getResources().getString(R.string.setting_permission), Toast.LENGTH_LONG).show();
                    // User selected the Never Ask Again Option
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                } else {
                    Toast.makeText(SignUp2Activity.this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUriChooseImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUriChooseImage);
                ivAvatar.setImageBitmap(bitmap);
                mFilePath = ImageFilePathPresenter.getPath(getApplicationContext(), mUriChooseImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            try {
                if (getRealPathFromURI(data.getData()) != "") {
                    //load image
                    Bitmap imageBitmap = EncodeImage.encodeImage(getRealPathFromURI(data.getData()));
                    ivAvatar.setImageBitmap(imageBitmap);
                    //  mFilePath = getRealPathFromURI(getImageUri(UpdateUserInfoActivity.this, imageBitmap));
                    mFilePath = ImageFilePathPresenter.getPath(getApplicationContext(), data.getData());
                } else {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivAvatar.setImageBitmap(photo);
                    // Bitmap des = EncodeImage.rotateBitmap(photo, orientation);
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    //   mFilePath = getRealPathFromURI(tempUri);
                    mFilePath = ImageFilePathPresenter.getPath(getApplicationContext(), tempUri);
                }
            } catch (Exception e) {
                ShowAlertDialog.showAlert(getResources().getString(R.string.loi_thu_lai), this);
            }
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {

        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }


    @Override
    public void displaySignUpAndSignIn(BodyResponse bodyResponse) {

    }

    @Override
    public void displayUpdate(DataUpdateResponse dataUpdateResponse) {

    }


    @Override
    public void displayError() {

    }

    @Override
    public void displayNotFoundLocaltion() {
        mProgressDialog.dismiss();
        ShowAlertDialog.showAlert(getResources().getString(R.string.vui_long_nhap_dia_chi), SignUp2Activity.this);
    }

    @Override
    public void getLocaltionAddress(GeoCodeMapResponse geoCodeMapResponse) {
        mLat = geoCodeMapResponse.getResults().get(0).getGeometry().getLocation().getLat();
        mLng = geoCodeMapResponse.getResults().get(0).getGeometry().getLocation().getLng();
        if (mLat != 0 && mLng != 0) {
            Intent iSignUp2 = new Intent(SignUp2Activity.this, TermsActivity.class);
            Bundle bSignUp2 = new Bundle();
            bSignUp2.putString("username", mUserName);
            bSignUp2.putString("password", mPassword);
            bSignUp2.putString("email", mEmail);
            bSignUp2.putString("fullname", mFullName);
            bSignUp2.putInt("gender", iGender);
            bSignUp2.putString("phone", mPhoneName);
            bSignUp2.putString("location", mLocation);
            bSignUp2.putDouble("lat", mLat);
            bSignUp2.putDouble("lng", mLng);
            bSignUp2.putString("filepath", mFilePath);
            iSignUp2.putExtra("bSignUp2", bSignUp2);
            startActivity(iSignUp2);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void displaySignInGooAndFace(BodyResponse bodyResponse) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void openGallery() {
        iChooseImage = new Intent();
        iChooseImage.setType("image/*");
        iChooseImage.setAction(Intent.ACTION_GET_CONTENT);
        permissionCheck = ContextCompat.checkSelfPermission(SignUp2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUp2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_PERMISSION);
        } else {
            startActivityForResult(Intent.createChooser(iChooseImage, getResources().getString(R.string.select_image)), PICK_IMAGE_FROM_GALLERY_REQUEST);
        }
    }

    public void selectImage() {
        final CharSequence[] options = {getResources().getString(R.string.sign_up_camera), getResources().getString(R.string.sign_up_libary_image), getResources().getString(R.string.sign_up_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp2Activity.this);
        builder.setTitle(getResources().getString(R.string.sign_up_choice));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getResources().getString(R.string.sign_up_camera))) {
                    if (verifyOpenCamera()) {
                        openCamera();
                    } else {
                        return;
                    }
                } else if (options[item].equals(getResources().getString(R.string.sign_up_libary_image))) {
                    if (verifyGallery()) {
                        openGallery();
                    } else {
                        return;
                    }
                }
            }
        });
        builder.show();
    }

    public boolean verifyCamerapermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
            return false;
        }
        return true;
    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    @Override
    public void checkUsername(CheckUsernameEmailResponse checkUsernameEmailResponse) {

    }

    @Override
    public void checkEmail(CheckUsernameEmailResponse checkUsernameEmailResponse) {
        if (checkUsernameEmailResponse.isStatus()) {
            mRegisterPresenter.getLocaltionAddress(mLocation);
        } else {
            mProgressDialog.dismiss();
            ShowAlertDialog.showAlert(getResources().getString(R.string.check_email), SignUp2Activity.this);
        }
    }
}
