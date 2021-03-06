package com.hbbsolution.owner.paymentonline.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hbbsolution.owner.R;
import com.hbbsolution.owner.utils.Constants;


/**
 * Created by DucChinh on 6/13/2016.
 */
public class CheckOutActivity extends Activity {

    public static final String TOKEN_CODE = "token_code";
    public static final String CHECKOUT_URL = "checkout_url";

    private WebView webData;

    private String mTokenCode = "";
    private String mCheckoutUrl = "";
    private String idBillOrder = "";
    private String key = "";
    public static Activity mCheckOuActivity;
    private Bundle infoMaid;
    private String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mCheckOuActivity = this;
        Bundle extras = getIntent().getExtras();
        infoMaid = getIntent().getBundleExtra("infoMaid");

        if (extras != null) {
            mTokenCode = extras.getString(TOKEN_CODE, "");
            mCheckoutUrl = extras.getString(CHECKOUT_URL, "");
            idBillOrder = extras.getString("idOderBill", "");
            key = extras.getString("key", "");
            amount = extras.getString("amount","");
        }

        initView();
    }

    private void initView() {
        webData = (WebView) findViewById(R.id.activity_checkout_webView);
        webData.getSettings().setJavaScriptEnabled(true);
        webData.setWebChromeClient(new WebChromeClient());
        webData.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("chanvl", "url: " + url);
                if (url.equalsIgnoreCase(Constants.RETURN_URL)) {
                    Intent intentCheckOut = new Intent(getApplicationContext(), CheckOrderActivity.class);
                    intentCheckOut.putExtra(CheckOrderActivity.TOKEN_CODE, mTokenCode);
                    intentCheckOut.putExtra("idBillOrder", idBillOrder);
                    intentCheckOut.putExtra("key", key);
                    intentCheckOut.putExtra("infoMaid", infoMaid );
                    intentCheckOut.putExtra("amount",amount);
                    startActivity(intentCheckOut);
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        if (!mCheckoutUrl.equalsIgnoreCase("")) {
            webData.loadUrl(mCheckoutUrl);
        }
    }
}
