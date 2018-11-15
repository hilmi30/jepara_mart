package com.perusdajepara.jeparamart.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.R;

import java.util.List;

import com.perusdajepara.jeparamart.fragments.Order_Details;
import com.perusdajepara.jeparamart.models.order_model.OrderDetails;
import com.perusdajepara.jeparamart.utils.Utilities;


/**
 * OrdersListAdapter is the adapter class of RecyclerView holding List of Orders in My_Orders
 **/

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.MyViewHolder> {

    Context context;
    String customerID, kurir;
    List<OrderDetails> ordersList;


    public OrdersListAdapter(Context context, String customerID, List<OrderDetails> ordersList, String kurir) {
        this.context = context;
        this.customerID = customerID;
        this.ordersList = ordersList;
        this.kurir = kurir;
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_orders, parent, false);

        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        
        // Get the data model based on Position
        final OrderDetails orderDetails = ordersList.get(position);
    
        int noOfProducts = 0;
        for (int i=0;  i<orderDetails.getProducts().size();  i++) {
            // Count no of Products
            noOfProducts += orderDetails.getProducts().get(i).getProductsQuantity();
        }

        holder.order_id.setText(String.valueOf(orderDetails.getOrdersId()));
        holder.order_status.setText(orderDetails.getOrdersStatus());
        holder.order_code.setText(orderDetails.getOrdersCode());
//        holder.order_price.setText(ConstantValues.CURRENCY_SYMBOL + orderDetails.getOrderPrice());
        holder.order_price.setText(Utilities.convertToRupiah(orderDetails.getOrderPrice()));
        holder.order_date.setText(orderDetails.getDatePurchased());
        holder.order_product_count.setText(String.valueOf(noOfProducts));
        holder.payment_method.setText(orderDetails.getPaymentMethodName());

        switch (orderDetails.getOrdersStatusId()) {
            case "1":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentBlue));
                break;
            case "2":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentGreen));
                break;
            case "3":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
                break;
            case "4":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.purple));
                break;
            case "5":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentLight));
                break;
            case "6":
                holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.yellow));
                break;
        }

        // Check Order's status
//        if (orderDetails.getOrdersStatusId().equalsIgnoreCase("1")) {
//            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentBlue));
//        } else if (orderDetails.getOrdersStatusId().equalsIgnoreCase("Completed")) {
//            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentGreen));
//        } else if (orderDetails.getOrdersStatus().equalsIgnoreCase("Verified")) {
//            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentLight));
//        } else if (orderDetails.getOrdersStatus().equalsIgnoreCase("Sending")) {
//            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentOrange));
//        } else {
//            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
//        }

        holder.order_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Order Info
                Bundle itemInfo = new Bundle();
                itemInfo.putParcelable("orderDetails", orderDetails);
                itemInfo.putString("isKurir", kurir);
    
                // Navigate to Order_Details Fragment
                Fragment fragment = new Order_Details();
                fragment.setArguments(itemInfo);
                MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });

    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return ordersList.size();
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private Button order_view_btn;
        private TextView order_id, order_product_count, order_status, order_price, order_date, payment_method, order_code;


        public MyViewHolder(final View itemView) {
            super(itemView);
    
            order_view_btn = (Button) itemView.findViewById(R.id.order_view_btn);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_product_count = (TextView) itemView.findViewById(R.id.order_products_count);
            order_status = (TextView) itemView.findViewById(R.id.order_status);
            order_price = (TextView) itemView.findViewById(R.id.order_price);
            order_date = (TextView) itemView.findViewById(R.id.order_date);
            payment_method = itemView.findViewById(R.id.order_payment_method);
            order_code = itemView.findViewById(R.id.order_code);
        }
    }
}

