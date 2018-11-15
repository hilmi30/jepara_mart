package com.perusdajepara.jeparamart.fragments;


import android.content.ClipData;
import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.perusdajepara.jeparamart.R;
import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.constant.ConstantValues;
import com.perusdajepara.jeparamart.utils.Utilities;

public class Thank_You extends Fragment {
    
    AdView mAdView;
    FrameLayout banner_adView;
    Button order_status_btn, continue_shopping_btn;
    LinearLayout thankYou, transferLayout;
    Boolean isTransfer;
    String noRekening, orderCode, bankName, finalPrice;
    TextView rekeningText, orderCodeText, bankNameText, finalPriceText, copyTotalPrice, copyRekening,
            currencyTotalPrice, copyOrderID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thank_you, container, false);

        // Get the Bundle Arguments
        Bundle checkoutArg = getArguments();
        isTransfer = checkoutArg.getBoolean("transfer");
        Log.d("transfer", isTransfer.toString());
        noRekening = checkoutArg.getString("rekening");
        orderCode = checkoutArg.getString("orderCode");
        bankName = checkoutArg.getString("bankName");
        finalPrice = checkoutArg.getString("finalPrice");

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));


        // Binding Layout Views
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        order_status_btn = (Button) rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = (Button) rootView.findViewById(R.id.continue_shopping_btn);
        thankYou = (LinearLayout) rootView.findViewById(R.id.thankyou);
        transferLayout = (LinearLayout) rootView.findViewById(R.id.transfer_layout);
        rekeningText = (TextView) rootView.findViewById(R.id.rekening_text);
        orderCodeText = (TextView) rootView.findViewById(R.id.order_code);
        bankNameText = (TextView) rootView.findViewById(R.id.bank_name);
        finalPriceText = (TextView) rootView.findViewById(R.id.final_price);
        copyTotalPrice = (TextView) rootView.findViewById(R.id.copy_totalprice);
        copyRekening = (TextView) rootView.findViewById(R.id.copy_rekening);
        currencyTotalPrice = (TextView) rootView.findViewById(R.id.currency_totalprice);
        copyOrderID = (TextView) rootView.findViewById(R.id.copy_order_id);

        copyOrderID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied text", orderCodeText.getText());
                clipboardMgr.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        copyRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied text", rekeningText.getText());
                clipboardMgr.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        copyTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied text", finalPriceText.getText());
                clipboardMgr.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        if(isTransfer) {
            transferLayout.setVisibility(View.VISIBLE);
            rekeningText.setText(noRekening);
            orderCodeText.setText(orderCode);
            bankNameText.setText(bankName);
            finalPriceText.setText(Utilities.convertToRupiahWithoutSymbol(finalPrice));
            currencyTotalPrice.setText(ConstantValues.CURRENCY_SYMBOL);
        } else {
            transferLayout.setVisibility(View.GONE);
        }

        if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(getContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
        }
        

        // Binding Layout Views
        order_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                // Navigate to My_Orders Fragment
                Fragment fragment = new My_Orders();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                
            }
        });


        // Binding Layout Views
        continue_shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to HomePage Fragment
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });


        return rootView;
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        return super.onOptionsItemSelected(item);
    }
    
}

