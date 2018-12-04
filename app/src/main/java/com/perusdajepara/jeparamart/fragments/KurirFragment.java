package com.perusdajepara.jeparamart.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.perusdajepara.jeparamart.R;
import com.perusdajepara.jeparamart.activities.Login;
import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.adapters.OrdersListAdapter;
import com.perusdajepara.jeparamart.constant.ConstantValues;
import com.perusdajepara.jeparamart.customs.EndlessRecyclerViewScroll;
import com.perusdajepara.jeparamart.models.order_model.OrderData;
import com.perusdajepara.jeparamart.models.order_model.OrderDetails;
import com.perusdajepara.jeparamart.network.APIClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KurirFragment extends Fragment {


    ProgressBar progressBar;
    TextView orderNotFound;
    RecyclerView kurirList;
    String pageNo, type, customerID;
    ArrayList<OrderDetails> orderList;
    OrdersListAdapter allOrderAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String kurir;

    View rootView;

    public KurirFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_kurir, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.courier));
        MainActivity.jmartLogo.setVisibility(View.GONE);

        progressBar = rootView.findViewById(R.id.kurir_loading_bar);
        orderNotFound = rootView.findViewById(R.id.kurir_empty_record);
        kurirList = rootView.findViewById(R.id.kurir_recycler);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the Customer's ID from SharedPreferences
        customerID = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        kurir = getArguments().getString("kurir");

        getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("isKurir", "");

        if(getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE)
                .getString("isKurir", "").equalsIgnoreCase("0")) {

            // Navigate to Login Activity
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }

        progressBar.setVisibility(View.GONE);
        orderNotFound.setVisibility(View.GONE);

        swipeRefreshLayout = rootView.findViewById(R.id.kurir_swipe);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this::setListOrder);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        kurirList.setLayoutManager(layoutManager);

        kurirList.addOnScrollListener(new EndlessRecyclerViewScroll() {
            @Override
            public void onLoadMore(int current_page) {
                progressBar.setVisibility(View.VISIBLE);
                new LoadMore(current_page).execute();
            }
        });

        setListOrder();
    }

    private void setListOrder() {
        orderList = new ArrayList<>();
        allOrderAdapter = new OrdersListAdapter(getContext(), customerID, orderList, kurir);
        kurirList.setAdapter(allOrderAdapter);

        RequestAllOrderList(pageNo, customerID, ConstantValues.LANGUAGE_ID, "");
    }


    private class LoadMore extends AsyncTask<String, Void, String> {

        int page_number;


        private LoadMore(int page_number) {
            this.page_number = page_number;
        }


        //*********** Runs on the UI thread before #doInBackground() ********//

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        //*********** Performs some Processes on Background Thread and Returns a specified Result  ********//

        @Override
        protected String doInBackground(String... params) {

            // Request for Products based on PageNo.
            RequestAllOrderList(String.valueOf(page_number), customerID, ConstantValues.LANGUAGE_ID, "");

            return "All Done!";
        }


        //*********** Runs on the UI thread after #doInBackground() ********//

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void RequestAllOrderList(String pageNo, String customerID, int languageId, String type) {

        Call<OrderData> call = APIClient.getInstance().getAllOrders(pageNo, customerID, languageId, type);

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, Response<OrderData> response) {
                if(response.isSuccessful()) {
                    if(response.body().getSuccess().equalsIgnoreCase("1")) {
                        addListOrder(response.body());
                    } else if(response.body().getSuccess().equalsIgnoreCase("0")) {
                        addListOrder(response.body());
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }

                    // Hide the ProgressBar
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addListOrder(OrderData orderData) {
        // Add Products to ProductsList from the List of ProductData
        for (int i = 0; i < orderData.getData().size(); i++) {

            OrderDetails orderDetails = orderData.getData().get(i);

            if(!orderDetails.getOrdersStatusId().equalsIgnoreCase("2")) {
                orderList.add(orderDetails);
            }
        }

        allOrderAdapter.notifyDataSetChanged();
    }
}
