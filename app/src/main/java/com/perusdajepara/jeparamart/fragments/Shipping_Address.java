package com.perusdajepara.jeparamart.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.perusdajepara.jeparamart.customs.DialogLoader;

import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.R;

import java.util.ArrayList;
import java.util.List;

import com.perusdajepara.jeparamart.app.App;
import com.perusdajepara.jeparamart.models.address_model.AddressData;
import com.perusdajepara.jeparamart.models.address_model.AddressDetails;
import com.perusdajepara.jeparamart.models.address_model.Kabupaten;
import com.perusdajepara.jeparamart.models.address_model.Kecamatan;
import com.perusdajepara.jeparamart.models.address_model.KecamatanDetails;
import com.perusdajepara.jeparamart.models.address_model.Provinsi;
import com.perusdajepara.jeparamart.models.address_model.ProvinsiDetails;
import com.perusdajepara.jeparamart.models.address_model.KabupatenDetails;
import com.perusdajepara.jeparamart.network.APIClient;
import com.perusdajepara.jeparamart.utils.CheckPermissions;
import com.perusdajepara.jeparamart.utils.ValidateInputs;

import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Shipping_Address extends Fragment {

    View rootView;
    Boolean isUpdate = false;
    String customerID, defaultAddressID;
    String selectedKabID, selectedProvID, selectedKecID;

    List<String> kabNames;
    List<String> provNames;
    List<String> kecNames;

    List<KabupatenDetails> kabList;
    List<ProvinsiDetails> provList;
    List<KecamatanDetails> kecList;

    ArrayAdapter<String> kabAdapter;
    ArrayAdapter<String> provAdapter;
    ArrayAdapter<String> kecAdapter;

    private MapView mapView;

    Button proceed_checkout_btn, resetBtn, lokasiBtn;
    LinearLayout default_shipping_layout;
    EditText input_firstname, input_lastname, input_address, input_prov, input_kab, input_kec, input_city, input_postcode;

    DialogLoader dialogLoader;

    NestedScrollView scrollView;
    Double lat, lng, defLat, defLng;
    CameraPosition cameraPosition;
    MarkerOptions markerOptions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.address, container, false);

        if (getArguments() != null) {
            if (getArguments().containsKey("isUpdate")) {
                isUpdate = getArguments().getBoolean("isUpdate", false);
            }
        }

        Log.d("update", isUpdate.toString());

        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.shipping_address));
    
        // Get the customersID and defaultAddressID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        defaultAddressID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");
        Log.d("default", defaultAddressID);

        // Binding Layout Views
        input_firstname = (EditText) rootView.findViewById(R.id.firstname);
        input_lastname = (EditText) rootView.findViewById(R.id.lastname);
        input_address = (EditText) rootView.findViewById(R.id.address);
        input_prov = (EditText) rootView.findViewById(R.id.province);
        input_kab = (EditText) rootView.findViewById(R.id.regency);
        input_kec = (EditText) rootView.findViewById(R.id.subdistrict);
        input_city = (EditText) rootView.findViewById(R.id.city);
        input_postcode = (EditText) rootView.findViewById(R.id.postcode);
        default_shipping_layout = (LinearLayout) rootView.findViewById(R.id.default_shipping_layout);
        proceed_checkout_btn = (Button) rootView.findViewById(R.id.save_address_btn);
        lokasiBtn = rootView.findViewById(R.id.get_my_location);


        // Set KeyListener of some View to null
        input_prov.setKeyListener(null);
        input_kab.setKeyListener(null);
        input_kec.setKeyListener(null);
    
        kabNames = new ArrayList<>();
        provNames = new ArrayList<>();
        kecNames = new ArrayList<>();
        

        // Hide the Default Checkbox Layout
        default_shipping_layout.setVisibility(View.GONE);

        // Set the text of Button
        proceed_checkout_btn.setText(getContext().getString(R.string.next));


        dialogLoader = new DialogLoader(getContext());

        scrollView = rootView.findViewById(R.id.scroll_view_address);
        resetBtn = rootView.findViewById(R.id.reset_coor);

        mapView = (MapView) rootView.findViewById(R.id.mapViewAddress);
        mapView.onCreate(savedInstanceState);

        // Request Provinsi
        RequestProv();

        // If an existing Address is being Edited
        if (isUpdate) {
            // Get the Shipping AddressDetails from AppContext that is being Edited
            AddressDetails shippingAddress = ((App) getContext().getApplicationContext()).getShippingAddress();

            // Set the Address details
            selectedKabID = shippingAddress.getKabId();
            selectedProvID = shippingAddress.getProvId();
            selectedKecID = shippingAddress.getKecId();
            input_firstname.setText(shippingAddress.getFirstname());
            input_lastname.setText(shippingAddress.getLastname());
            input_address.setText(shippingAddress.getStreet());
            input_prov.setText(shippingAddress.getProvId());
            input_kab.setText(shippingAddress.getKabId());
            input_kec.setText(shippingAddress.getKecId());
            input_city.setText(shippingAddress.getCity());
            input_postcode.setText(shippingAddress.getPostcode());

            lat = shippingAddress.getLat();
            lng = shippingAddress.getLng();

            defLat = shippingAddress.getLat();
            defLng = shippingAddress.getLng();

            setMap();

            RequestKab(String.valueOf(selectedProvID));
            RequestKec(String.valueOf(selectedKabID));
        }
        else {
            // Request All Addresses of the User
            RequestAllAddresses();
        }

        // Handle Touch event of input_prov EditText
        input_prov.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
    
                    provAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    provAdapter.addAll(provNames);
    
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
    
                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
    
                    dialog_title.setText(getString(R.string.country));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(provAdapter);
    
                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            provAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
    
    
                    final AlertDialog alertDialog = dialog.create();
    
                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
    
                    alertDialog.show();
                    
                    
                    
                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            
                            alertDialog.dismiss();
                            final String selectedItem = provAdapter.getItem(position);
                            
                            String provID = "0";
                            input_prov.setText(selectedItem);
    
                            if (!selectedItem.equalsIgnoreCase("Other")) {
                                
                                for (int i = 0; i< provList.size(); i++) {
                                    if (provList.get(i).getProvNama().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        provID = provList.get(i).getProvId();
                                    }
                                }
                                
                            }
    
                            selectedProvID = provID;

                            kabNames.clear();
                            kecNames.clear();

                            input_kab.setText("");
                            input_kec.setText("");
    
                            // Request for all Kabupaten in the selected Country
                            RequestKab(selectedProvID);
                        }
                    });
                    
                }

                return false;
            }
        });

        
        // Handle Touch event of input_kab EditText
        input_kab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
    
                    kabAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    kabAdapter.addAll(kabNames);
    
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
    
                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
    
                    dialog_title.setText(getString(R.string.zone));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(kabAdapter);
    
                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            kabAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
    
    
                    final AlertDialog alertDialog = dialog.create();
    
                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
    
                    alertDialog.show();
    
    
    
                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
                            alertDialog.dismiss();
                            final String selectedItem = kabAdapter.getItem(position);
    
                            String zoneID = "0";
                            input_kab.setText(selectedItem);
    
                            if (!kabAdapter.getItem(position).equalsIgnoreCase("Other")) {
                                
                                for (int i = 0; i< kabList.size(); i++) {
                                    if (kabList.get(i).getKabNama().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        zoneID = kabList.get(i).getKabId();
                                    }
                                }
                            }
    
                            selectedKabID = zoneID;

                            kecNames.clear();
                            input_kec.setText("");

                            RequestKec(selectedKabID);
                        }
                    });
                    
                }

                return false;
            }
        });


        input_kec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    kecAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    kecAdapter.addAll(kecNames);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                    dialog_title.setText(getString(R.string.subdistrict));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(kecAdapter);

                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            kecAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();



                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = kecAdapter.getItem(position);

                            String kecID = "0";
                            input_kec.setText(selectedItem);

                            if (!selectedItem.equalsIgnoreCase("Other")) {

                                for (int i = 0; i< kecList.size(); i++) {
                                    if (kecList.get(i).getKecNama().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        kecID = kecList.get(i).getKecId();
                                    }
                                }

                            }

                            selectedKecID = kecID;
                        }
                    });

                }

                return false;
            }
        });

        // Handle the Click event of Proceed Order Button
        proceed_checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Address Form Inputs
                boolean isValidData = validateAddressForm();

                if (isValidData) {
                    // New Instance of AddressDetails
                    AddressDetails shippingAddress = new AddressDetails();

                    shippingAddress.setFirstname(input_firstname.getText().toString().trim());
                    shippingAddress.setLastname(input_lastname.getText().toString().trim());
                    shippingAddress.setProvName(input_prov.getText().toString().trim());
                    shippingAddress.setKabNama(input_kab.getText().toString().trim());
                    shippingAddress.setKecNama(input_kec.getText().toString().trim());
                    shippingAddress.setCity(input_city.getText().toString().trim());
                    shippingAddress.setStreet(input_address.getText().toString().trim());
                    shippingAddress.setPostcode(input_postcode.getText().toString().trim());
                    shippingAddress.setKabId(selectedKabID);
                    shippingAddress.setProvId(selectedProvID);
                    shippingAddress.setKecId(selectedKecID);
                    shippingAddress.setLat(lat);
                    shippingAddress.setLng(lng);

                    // Save the AddressDetails
                    ((App) getContext().getApplicationContext()).setShippingAddress(shippingAddress);


                    // Check if an Address is being Edited
                    if (isUpdate) {
                        // Navigate to Checkout Fragment
                        ((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
                    }
                    else {
                        // Navigate to Billing_Address Fragment
                        Fragment fragment = new Shipping_Methods();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                                .addToBackStack(null).commit();
                    }
                }
            }
        });


        return rootView;
    }

    private void setMarker(MapboxMap mapboxMap, Double lat, Double lng) {

        LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng(lat, lng);

        markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                .position(latLng)
                .title(getString(R.string.your_delivery_point));

        cameraPosition = new CameraPosition.Builder()
                .target(latLng) // Sets the new camera position
                .zoom(10) // Sets the zoom to level 10
                .tilt(20) // Set the camera tilt to 20 degrees
                .build(); // Builds the CameraPosition object from the builder

        mapboxMap.addMarker(markerOptions);
        mapboxMap.setCameraPosition(cameraPosition);
    }


    //*********** Get Provinsi List from the Server ********//

    private void RequestProv() {

        dialogLoader.showProgressDialog();

        Call<Provinsi> call = APIClient.getInstance()
                .getProvinsi();

        call.enqueue(new Callback<Provinsi>() {
            @Override
            public void onResponse(Call<Provinsi> call, Response<Provinsi> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        provNames.clear();
                        provList = response.body().getData();
    
                        // Add the Country Names to the provNames List
                        for (int i = 0; i< provList.size(); i++) {
                            provNames.add(provList.get(i).getProvNama());
                        }
    
                        provNames.add("Other");
                        
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Provinsi> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Get Kabupaten List of the Country from the Server ********//

    private void RequestKab(String provID) {

        dialogLoader.showProgressDialog();

        Call<Kabupaten> call = APIClient.getInstance()
                .getKabupaten
                        (
                                provID
                        );

        call.enqueue(new Callback<Kabupaten>() {
            @Override
            public void onResponse(Call<Kabupaten> call, Response<Kabupaten> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        kabNames.clear();
                        kabList = response.body().getData();
    
                        // Add the Zone Names to the kabNames List
                        for (int i = 0; i< kabList.size(); i++){
                            kabNames.add(kabList.get(i).getKabNama());
                        }

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Kabupaten> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void RequestKec(String kabID) {

        dialogLoader.showProgressDialog();

        Call<Kecamatan> call = APIClient.getInstance()
                .getKecamatan
                        (
                                kabID
                        );

        call.enqueue(new Callback<Kecamatan>() {
            @Override
            public void onResponse(Call<Kecamatan> call, Response<Kecamatan> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        kecNames.clear();
                        kecList = response.body().getData();

                        // Add the Zone Names to the kabNames List
                        for (int i = 0; i< kecList.size(); i++){
                            kecNames.add(kecList.get(i).getKecNama());
                        }

                        kecNames.add("Other");

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Kecamatan> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Get the User's Default Address from all the Addresses ********//

    private void filterDefaultAddress(AddressData addressData) {

        // Get CountryList from Response
        List<AddressDetails> addressesList = addressData.getData();

        // Initialize new AddressDetails for DefaultAddress
        AddressDetails defaultAddress = new AddressDetails();

        for (int i=0;  i<addressesList.size();  i++) {
            // Check if the Current Address is User's Default Address
            if (addressesList.get(i).getAddressId() == addressesList.get(i).getDefaultAddress()) {
                // Set the Default AddressDetails
                defaultAddress = addressesList.get(i);
            }
        }


        // Set Default Address Data and Display to User
        selectedKabID = defaultAddress.getKabId();
        selectedProvID = defaultAddress.getProvId();
        selectedKecID = defaultAddress.getKecId();
        input_firstname.setText(defaultAddress.getFirstname());
        input_lastname.setText(defaultAddress.getLastname());
        input_address.setText(defaultAddress.getStreet());
        input_prov.setText(defaultAddress.getProvName());
        input_kab.setText(defaultAddress.getKabNama());
        input_kec.setText(defaultAddress.getKecNama());
        input_city.setText(defaultAddress.getCity());
        input_postcode.setText(defaultAddress.getPostcode());

        lat = defaultAddress.getLat();
        lng = defaultAddress.getLng();

        defLat = defaultAddress.getLat();
        defLng = defaultAddress.getLng();

        setMap();

        // Request Kabupaten of selected Country
        RequestKab(selectedProvID);
        RequestKec(selectedKabID);
    }

    private void setMap() {
        mapView.getMapAsync(mapboxMap -> {

            resetBtn.setEnabled(true);
            resetBtn.setClickable(true);

            setMarker(mapboxMap, lat, lng);

            lokasiBtn.setOnClickListener(view -> {
                if (CheckPermissions.is_LOCATION_PermissionGranted()) {
                    SmartLocation.with(getContext()).location().start(location -> {
                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        Log.d("lat", lat.toString());
                        Log.d("lng", lng.toString());

                        mapboxMap.clear();
                        setMarker(mapboxMap, lat, lng);
                    });
                }
            });

            resetBtn.setOnClickListener(view -> {
                mapboxMap.clear();
                setMarker(mapboxMap, defLat, defLng);
            });

            mapboxMap.addOnMapClickListener(point -> {
                lat = point.getLatitude();
                lng = point.getLongitude();

                mapboxMap.clear();

                LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng(lat, lng);

                markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.your_delivery_point));

                cameraPosition = new CameraPosition.Builder()
                        .target(latLng) // Sets the new camera position
                        .build(); // Builds the CameraPosition object from the builder

                mapboxMap.addMarker(markerOptions);
                mapboxMap.setCameraPosition(cameraPosition);
            });
        });

        mapView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return mapView.onTouchEvent(event);
        });
    }


    //*********** Request List of User Addresses ********//

    public void RequestAllAddresses() {

        dialogLoader.showProgressDialog();

        Call<AddressData> call = APIClient.getInstance()
                .getAllAddress
                        (
                                customerID
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, retrofit2.Response<AddressData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Filter all the Addresses to get the Default Address
                        filterDefaultAddress(response.body());
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {

                        lat = -6.5804981;
                        lng = 110.6789833;

                        defLat = -6.5804981;
                        defLng = 110.6789833;

                        setMap();

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Belum ada alamat");
                        alert.setMessage("Anda belum menyimpan alamat. Simpan alamat sekarang?");
                        alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Navigate to My_Addresses Fragment
                                Fragment fragment = new My_Addresses();
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.main_fragment, fragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .addToBackStack(getString(R.string.actionHome)).commit();
                            }
                        });
                        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert.create().show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
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
        SmartLocation.with(getContext()).location().stop();
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
    
    //*********** Validate Address Form Inputs ********//
    
    private boolean validateAddressForm() {
        if (!ValidateInputs.isValidName(input_firstname.getText().toString().trim())) {
            input_firstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(input_lastname.getText().toString().trim())) {
            input_lastname.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (!ValidateInputs.isValidInput(input_address.getText().toString().trim())) {
            input_address.setError(getString(R.string.invalid_address));
            return false;
        } else if (!ValidateInputs.isValidInput(input_prov.getText().toString().trim())) {
            input_prov.setError(getString(R.string.select_country));
            return false;
        } else if (!ValidateInputs.isValidInput(input_kab.getText().toString().trim())) {
            input_kab.setError(getString(R.string.select_zone));
            return false;
        } else if (!ValidateInputs.isValidInput(input_kab.getText().toString().trim())) {
            input_kec.setError(getString(R.string.select_your_subdistrict));
            return false;
        } else if (!ValidateInputs.isValidInput(input_city.getText().toString().trim())) {
            input_city.setError(getString(R.string.enter_city));
            return false;
        } else if (!ValidateInputs.isValidNumber(input_postcode.getText().toString().trim())) {
            input_postcode.setError(getString(R.string.invalid_post_code));
            return false;
        } else {
            return true;
        }
    }

}

