package com.hbbsolution.owner.more.viet_pham.View.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hbbsolution.owner.R;

/**
 * Created by buivu on 29/05/2017.
 */

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.lo_toolbar)
    LinearLayout toolbar;
    @BindView(R.id.info_user_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_header)
    Toolbar toolbarHeader;
    @BindView(R.id.txt_profile_name)
    TextView txtProfileName;
    @BindView(R.id.txt_profile_address)
    TextView txtProfileAddress;
    @BindView(R.id.txt_profile_gender)
    TextView txtProfileGender;
    @BindView(R.id.txt_profile_phone)
    TextView txtProfilePhone;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.img_blur_image)
    ImageView imgBlurImage;

    private SessionManagerUser sessionManagerUser;
    private HashMap<String, String> hashDataUser = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        appBarLayout.addOnOffsetChangedListener(this);
        //set up toolbar
        setSupportActionBar(toolbarHeader);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //init
        sessionManagerUser = new SessionManagerUser(this);
        hashDataUser = sessionManagerUser.getUserDetails();
        loadData();
    }

    private void loadData() {
        txtProfileName.setText(hashDataUser.get(SessionManagerUser.KEY_NAME));
        txtProfileAddress.setText(hashDataUser.get(SessionManagerUser.KEY_ADDRESS));
        if (Integer.parseInt(hashDataUser.get(SessionManagerUser.KEY_GENDER)) == 0) {
            txtProfileGender.setText("Nam");
        } else {
            txtProfileGender.setText("Nữ");
        }
        txtProfilePhone.setText(hashDataUser.get(SessionManagerUser.KEY_PHONE));
        ImageLoader.getInstance().loadImageAvatar(ProfileActivity.this, hashDataUser.get(SessionManagerUser.KEY_AVATAR),
                imgAvatar);

        // from Bitmap
        Glide.with(ProfileActivity.this)
                .load(hashDataUser.get(SessionManagerUser.KEY_AVATAR))
                .asBitmap()
                .error(R.drawable.avatar)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Blurry.with(ProfileActivity.this)
                                .radius(10)
                                .from(resource)
                                .into(imgBlurImage);
                    }
                });

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbarLayout.getHeight() + verticalOffset <= 1 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.animate().alpha(1).setDuration(200);
        } else {
            toolbar.setVisibility(View.GONE);
            toolbar.animate().alpha(0).setDuration(200);
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbarLayout.getHeight() + verticalOffset <= 1 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.animate().alpha(1).setDuration(200);
        } else {
            toolbar.setVisibility(GONE);
            toolbar.animate().alpha(0).setDuration(200);
        }
    }
}
