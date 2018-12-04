package com.perusdajepara.jeparamart.fragments;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.perusdajepara.jeparamart.R;

import java.util.List;

import com.perusdajepara.jeparamart.adapters.CouponsAdapter;
import com.perusdajepara.jeparamart.adapters.OrderedProductsListAdapter;
import com.perusdajepara.jeparamart.constant.ConstantValues;
import com.perusdajepara.jeparamart.models.coupons_model.CouponsInfo;
import com.perusdajepara.jeparamart.models.order_model.OrderData;
import com.perusdajepara.jeparamart.models.order_model.OrderDetails;
import com.perusdajepara.jeparamart.models.order_model.OrderProducts;
import com.perusdajepara.jeparamart.customs.DividerItemDecoration;
import com.perusdajepara.jeparamart.network.APIClient;
import com.perusdajepara.jeparamart.utils.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Order_Details extends Fragment {

    View rootView;
    
    OrderDetails orderDetails;

    CardView buyer_comments_card, seller_comments_card;
    RecyclerView checkout_items_recycler, checkout_coupons_recycler;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount, checkout_total,
            finalPrice, bankName, rekeningText, paymentCode, konfirmasiDiterima, konfirmasiTransfer, contactUsText;
    TextView billing_name, billing_street, billing_address, shipping_name, shipping_street,
            shipping_address, copyRekening, copyFinalPrice, currencyTotalPrice;
    TextView order_price, order_products_count, order_status, order_date, shipping_method,
            payment_method, buyer_comments, seller_comments, order_id, copyOrderID, orderCodeText;
    LinearLayout transferLayout, mapLayout;
    Button contactUs, itemConfirm, goToLocation, itemReceived;
    private MapView mapView;
    NestedScrollView orderScroll;

    List<CouponsInfo> couponsList;
    List<OrderProducts> orderProductsList;

    CouponsAdapter couponsAdapter;
    OrderedProductsListAdapter orderedProductsAdapter;
    String isKurir;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_details, container, false);
    
    
        // Get orderDetails from bundle arguments
        orderDetails = getArguments().getParcelable("orderDetails");
        isKurir = getArguments().getString("isKurir");
        
        
        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_details));
        

        // Binding Layout Views
        order_price = (TextView) rootView.findViewById(R.id.order_price);
        order_products_count = (TextView) rootView.findViewById(R.id.order_products_count);
        shipping_method = (TextView) rootView.findViewById(R.id.shipping_method);
        payment_method = (TextView) rootView.findViewById(R.id.payment_method);
        order_status = (TextView) rootView.findViewById(R.id.order_status);
        order_date = (TextView) rootView.findViewById(R.id.order_date);
        checkout_subtotal = (TextView) rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = (TextView) rootView.findViewById(R.id.checkout_tax);
        checkout_shipping = (TextView) rootView.findViewById(R.id.checkout_shipping);
        checkout_discount = (TextView) rootView.findViewById(R.id.checkout_discount);
        checkout_total = (TextView) rootView.findViewById(R.id.checkout_total);
        billing_name = (TextView) rootView.findViewById(R.id.billing_name);
        billing_address = (TextView) rootView.findViewById(R.id.billing_address);
        billing_street = (TextView) rootView.findViewById(R.id.billing_street);
        shipping_name = (TextView) rootView.findViewById(R.id.shipping_name);
        shipping_address = (TextView) rootView.findViewById(R.id.shipping_address);
        shipping_street = (TextView) rootView.findViewById(R.id.shipping_street);
        buyer_comments = (TextView) rootView.findViewById(R.id.buyer_comments);
        seller_comments = (TextView) rootView.findViewById(R.id.seller_comments);
        buyer_comments_card = (CardView) rootView.findViewById(R.id.buyer_comments_card);
        seller_comments_card = (CardView) rootView.findViewById(R.id.seller_comments_card);
        checkout_items_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_items_recycler);
        checkout_coupons_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_coupons_recycler);
        order_id = (TextView) rootView.findViewById(R.id.order_products_id);
        transferLayout = (LinearLayout) rootView.findViewById(R.id.transfer_layout);
        finalPrice = (TextView) rootView.findViewById(R.id.final_price);
        bankName = (TextView) rootView.findViewById(R.id.bank_name);
        rekeningText = (TextView) rootView.findViewById(R.id.rekening_text);
        copyFinalPrice = (TextView) rootView.findViewById(R.id.copy_totalprice);
        copyRekening = (TextView) rootView.findViewById(R.id.copy_rekening);
        currencyTotalPrice = (TextView) rootView.findViewById(R.id.currency_totalprice);
        paymentCode = (TextView) rootView.findViewById(R.id.checkout_payment_code);
        contactUs = (Button) rootView.findViewById(R.id.contact_us_btn);
        contactUsText = rootView.findViewById(R.id.contact_us_text);
        itemConfirm = (Button) rootView.findViewById(R.id.item_confirm);
        mapLayout = rootView.findViewById(R.id.map_layout);
        mapView = (MapView) rootView.findViewById(R.id.order_map);
        orderScroll = rootView.findViewById(R.id.order_scroll);
        goToLocation = rootView.findViewById(R.id.go_to_location);
        itemReceived = rootView.findViewById(R.id.item_received);
        konfirmasiDiterima = rootView.findViewById(R.id.konfirmasi_diterima_text);
        konfirmasiTransfer = rootView.findViewById(R.id.konfirmasi_transfer_text);

        copyOrderID = (TextView) rootView.findViewById(R.id.copy_order_id);
        orderCodeText = (TextView) rootView.findViewById(R.id.order_product_code);

        checkout_items_recycler.setNestedScrollingEnabled(false);
        checkout_coupons_recycler.setNestedScrollingEnabled(false);

        // Set Order Details
        couponsList = orderDetails.getCoupons();
        orderProductsList = orderDetails.getProducts();

        double subTotal = 0;
        int noOfProducts = 0;
        for (int i=0;  i<orderProductsList.size();  i++) {
            subTotal += Double.parseDouble(orderProductsList.get(i).getFinalPrice());
            noOfProducts += orderProductsList.get(i).getProductsQuantity();
        }
        
        
//        String orderPrice = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getOrderPrice()));
//        String Tax = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getTotalTax()));
//        String Shipping = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getShippingCost()));
//        String Discount = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getCouponAmount()));
//        String Subtotal = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(subTotal);
//        String Total = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getOrderPrice()));

        String orderPrice = Utilities.convertToRupiah(String.valueOf(Double.parseDouble(orderDetails.getOrderPrice())));
        String priceFinal = Utilities.convertToRupiahWithoutSymbol(String.valueOf(Double.parseDouble(orderDetails.getOrderPrice())));
        String Tax = Utilities.convertToRupiah(String.valueOf(Double.parseDouble(orderDetails.getTotalTax())));
        String Shipping = Utilities.convertToRupiah(String.valueOf(Double.parseDouble(orderDetails.getShippingCost())));
        String Discount = Utilities.convertToRupiah(String.valueOf(Double.parseDouble(orderDetails.getCouponAmount())));
        String Subtotal = Utilities.convertToRupiah(String.valueOf(subTotal));
        String Total = Utilities.convertToRupiah(String.valueOf(Double.parseDouble(orderDetails.getOrderPrice())));
        String orderID = String.valueOf(orderDetails.getOrdersId());




        // jika status order = verified
        if(orderDetails.getOrdersStatusId().equalsIgnoreCase("6")) {
            itemReceived.setEnabled(true);
            itemReceived.setClickable(true);
            itemReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            itemReceived.setEnabled(false);
            itemReceived.setClickable(false);
            itemReceived.setBackgroundColor(getResources().getColor(R.color.colorAccentGrey));
        }

        // jika status order != completed
        if(!orderDetails.getOrdersStatusId().equalsIgnoreCase("2")) {

            if(isKurir.equalsIgnoreCase("0")) {
                // jika transfer
                if(orderDetails.getPaymentMethod().equalsIgnoreCase("transfer")) {
                    transferLayout.setVisibility(View.VISIBLE);
                }

                // jika cod
                else if(orderDetails.getPaymentMethod().equalsIgnoreCase("cod")) {
                    transferLayout.setVisibility(View.GONE);
                }
            } else {
                transferLayout.setVisibility(View.GONE);
            }
        }

        // cek jika order status = pending
        if(orderDetails.getOrdersStatusId().equalsIgnoreCase("1")) {
            itemConfirm.setEnabled(true);
            itemConfirm.setClickable(true);
            itemConfirm.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            itemConfirm.setEnabled(false);
            itemConfirm.setClickable(false);
            itemConfirm.setBackgroundColor(getResources().getColor(R.color.colorAccentGrey));
        }

        // jika user bukan kurir
        if(isKurir.equalsIgnoreCase("0")) {

            goToLocation.setVisibility(View.GONE);
            itemConfirm.setVisibility(View.VISIBLE);
            itemReceived.setVisibility(View.VISIBLE);
            konfirmasiTransfer.setVisibility(View.VISIBLE);
            konfirmasiDiterima.setVisibility(View.VISIBLE);
            contactUs.setVisibility(View.VISIBLE);
            contactUsText.setVisibility(View.VISIBLE);
        } else {
            goToLocation.setVisibility(View.VISIBLE);
            itemConfirm.setVisibility(View.GONE);
            itemReceived.setVisibility(View.GONE);
            konfirmasiTransfer.setVisibility(View.GONE);
            konfirmasiDiterima.setVisibility(View.GONE);
            konfirmasiDiterima.setVisibility(View.GONE);
            contactUs.setVisibility(View.GONE);
            contactUsText.setVisibility(View.GONE);
        }

        currencyTotalPrice.setText(ConstantValues.CURRENCY_SYMBOL);
        paymentCode.setText(ConstantValues.CURRENCY_SYMBOL + " " + orderDetails.getPaymentCode());
        order_price.setText(orderPrice);
        order_products_count.setText(String.valueOf(noOfProducts));
        shipping_method.setText(orderDetails.getShippingMethod());
        payment_method.setText(orderDetails.getPaymentMethodName());
        order_status.setText(orderDetails.getOrdersStatus());
        order_date.setText(orderDetails.getDatePurchased());
        order_id.setText(orderID);
        orderCodeText.setText(orderDetails.getOrdersCode());

        finalPrice.setText(priceFinal);
        bankName.setText(orderDetails.getBankName());
        rekeningText.setText(orderDetails.getRekening());

        checkout_tax.setText(Tax);
        checkout_shipping.setText(Shipping);
        checkout_discount.setText(Discount);
        checkout_subtotal.setText(Subtotal);
        checkout_total.setText(Total);

        billing_name.setText(orderDetails.getBillingName());
        billing_address.setText(orderDetails.getBillingCity());
        billing_street.setText(orderDetails.getBillingStreetAddress());
        shipping_name.setText(orderDetails.getDeliveryName());
        shipping_address.setText(orderDetails.getDeliveryCity());
        shipping_street.setText(orderDetails.getDeliveryStreetAddress());

        goToLocation.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + orderDetails.getCustomersLat() + "," +
                    orderDetails.getCustomersLong() + "(" + orderDetails.getDeliveryName() + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        copyOrderID.setOnClickListener(v -> {
            android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied text", orderCodeText.getText());
            clipboardMgr.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.berhasil_disalin), Toast.LENGTH_SHORT).show();
        });

        copyFinalPrice.setOnClickListener(v -> {
            android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied text", finalPrice.getText());
            clipboardMgr.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.berhasil_disalin), Toast.LENGTH_SHORT).show();
        });

        copyRekening.setOnClickListener(v -> {
            android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied text", rekeningText.getText());
            clipboardMgr.setPrimaryClip(clip);
            Toast.makeText(getContext(), getString(R.string.berhasil_disalin), Toast.LENGTH_SHORT).show();
        });

        contactUs.setOnClickListener(v -> {
            // Navigate to ContactUs Fragment
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            Fragment fragment = new ContactUs();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();
        });

        itemConfirm.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.item_received);
            alert.setMessage(R.string.have_you_received);
            alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateOrderStatus();
                }
            });
            alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.create().show();
        });

        itemReceived.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.item_received);
            alert.setMessage(R.string.konfimasi);
            alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateOrderStatusComplete();
                }
            });
            alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.create().show();
        });

        if (!TextUtils.isEmpty(orderDetails.getCustomerComments())) {
            buyer_comments_card.setVisibility(View.VISIBLE);
            buyer_comments.setText(orderDetails.getCustomerComments());
        } else {
            buyer_comments_card.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(orderDetails.getAdminComments())) {
            seller_comments_card.setVisibility(View.VISIBLE);
            seller_comments.setText(orderDetails.getAdminComments());
        } else {
            seller_comments_card.setVisibility(View.GONE);
        }


        couponsAdapter = new CouponsAdapter(getContext(), couponsList, false, null);

        checkout_coupons_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        checkout_coupons_recycler.setAdapter(couponsAdapter);

        
        orderedProductsAdapter = new OrderedProductsListAdapter(getContext(), orderProductsList);

        checkout_items_recycler.setAdapter(orderedProductsAdapter);
        checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        checkout_items_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl("mapbox://styles/hilmi30/cjno37nyd0w9q2splwp0kwuue");
        mapView.getMapAsync(mapboxMap -> {

            mapboxMap.getUiSettings().setAllGesturesEnabled(false);

            LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng(Double.parseDouble(orderDetails.getCustomersLat()),
                    Double.parseDouble(orderDetails.getCustomersLong()));

            MarkerOptions markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.delivery_point));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng) // Sets the new camera position
                    .zoom(10) // Sets the zoom to level 10
                    .tilt(20) // Set the camera tilt to 20 degrees
                    .build(); // Builds the CameraPosition object from the builder

            mapboxMap.addMarker(markerOptions);
            mapboxMap.setCameraPosition(cameraPosition);

        });

//        mapView.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_MOVE:
//                    orderScroll.requestDisallowInterceptTouchEvent(true);
//                    break;
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    orderScroll.requestDisallowInterceptTouchEvent(false);
//                    break;
//            }
//            return mapView.onTouchEvent(event);
//        });

        return rootView;

    }

    private void updateOrderStatusComplete() {
        Call<OrderData> call = APIClient.getInstance().updateOrderComplete(
                ""+orderDetails.getOrdersId()
        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, Response<OrderData> response) {
                if(response.isSuccessful()) {
                    if(response.body().getSuccess().equalsIgnoreCase("1")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle(getString(R.string.thank_you));
                        alert.setMessage(getString(R.string.thank_you_for_shopping));
                        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                // Navigate to My_Orders Fragment
                                Fragment fragment = new My_Orders();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.main_fragment, fragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .addToBackStack(getString(R.string.actionHome)).commit();
                            }
                        });
                        alert.setCancelable(false);
                        alert.create().show();
                    } else if(response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void updateOrderStatus() {
        Call<OrderData> call = APIClient.getInstance().updateOrderStatus(
                ""+orderDetails.getOrdersId()
        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, Response<OrderData> response) {
                if(response.isSuccessful()) {
                    if(response.body().getSuccess().equalsIgnoreCase("1")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle(getString(R.string.thank_you));
                        alert.setMessage(getString(R.string.thank_you_for_shopping));
                        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                // Navigate to My_Orders Fragment
                                Fragment fragment = new My_Orders();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.main_fragment, fragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .addToBackStack(getString(R.string.actionHome)).commit();
                            }
                        });
                        alert.setCancelable(false);
                        alert.create().show();
                    } else if(response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }
}



