package com.perusdajepara.jeparamart.fragments;


import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.R;

import java.util.ArrayList;
import java.util.List;

import com.perusdajepara.jeparamart.models.address_model.AddressData;
import com.perusdajepara.jeparamart.models.address_model.Kabupaten;
import com.perusdajepara.jeparamart.models.address_model.Kecamatan;
import com.perusdajepara.jeparamart.models.address_model.KecamatanDetails;
import com.perusdajepara.jeparamart.models.address_model.Provinsi;
import com.perusdajepara.jeparamart.models.address_model.ProvinsiDetails;
import com.perusdajepara.jeparamart.models.address_model.KabupatenDetails;
import com.perusdajepara.jeparamart.network.APIClient;
import com.perusdajepara.jeparamart.utils.ValidateInputs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Add_Address extends Fragment {

    View rootView;
    Boolean isUpdate;

    String customerID, addressID;
    String selectedKabID, selectedProvID, selectedKecID;

    Button saveAddressBtn;
    LinearLayout default_shipping_layout;
    EditText input_firstname, input_lastname, input_address, input_prov, input_kab, input_city, input_kec, input_postcode;
    
    ArrayAdapter<String> kabAdapter;
    ArrayAdapter<String> provAdapter;
    ArrayAdapter<String> kecAdapter;

    List<String> kabNames;
    List<String> provNames;
    List<String> kecNames;

    List<KabupatenDetails> kabList = new ArrayList<>();
    List<ProvinsiDetails> provList = new ArrayList<>();
    List<KecamatanDetails> kecList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.address, container, false);

        // Get the Bundle Arguments
        Bundle addressInfo = getArguments();
        isUpdate = addressInfo.getBoolean("isUpdate");

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        
        // Set the Title of Toolbar
        if (isUpdate) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.update_address));
        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.new_address));
        }
        

        // Get the CustomersID from SharedPreferences
        customerID = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views
        input_firstname = (EditText) rootView.findViewById(R.id.firstname);
        input_lastname = (EditText) rootView.findViewById(R.id.lastname);
        input_address = (EditText) rootView.findViewById(R.id.address);
        input_prov = (EditText) rootView.findViewById(R.id.province);
        input_kab = (EditText) rootView.findViewById(R.id.regency);
        input_kec = (EditText) rootView.findViewById(R.id.subdistrict);
        input_city = (EditText) rootView.findViewById(R.id.city);
        input_postcode = (EditText) rootView.findViewById(R.id.postcode);
        saveAddressBtn = (Button) rootView.findViewById(R.id.save_address_btn);
        default_shipping_layout = (LinearLayout) rootView.findViewById(R.id.default_shipping_layout);


        // Set KeyListener of some View to null
        input_prov.setKeyListener(null);
        input_kab.setKeyListener(null);
        input_kec.setKeyListener(null);
    
        kabNames = new ArrayList<>();
        provNames = new ArrayList<>();
        kecNames = new ArrayList<>();

        // Hide the Default Checkbox Layout
        default_shipping_layout.setVisibility(View.GONE);


        // Request for Provinsi List
        RequestProv();


        // Check if an existing Address is being Edited
        if (isUpdate) {
            // Set the Address details from Bundle Arguments
            addressID = addressInfo.getString("addressID");
            selectedProvID = addressInfo.getString("addressProvID");
            selectedKabID = addressInfo.getString("addressKabID");
            selectedKecID = addressInfo.getString("addressKecID");

            input_firstname.setText(addressInfo.getString("addressFirstname"));
            input_lastname.setText(addressInfo.getString("addressLastname"));
            input_address.setText(addressInfo.getString("addressStreet"));
            input_prov.setText(addressInfo.getString("addressProvName"));
            input_kab.setText(addressInfo.getString("addressKabName"));
            input_kec.setText(addressInfo.getString("addressKecName"));
            input_city.setText(addressInfo.getString("addressCity"));
            input_postcode.setText(addressInfo.getString("addressPostCode"));
    
            RequestKab(String.valueOf(selectedProvID));
            RequestKec(String.valueOf(selectedKabID));
        }
        else {
            kabNames.add("Other");
            kecNames.add("Other");
            selectedKabID = "0";
            selectedKecID = "0";
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
                            // Filter CountryAdapter
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
                            // Request for all Kabupaten in the selected Country
                            RequestKab(selectedProvID);
            
                            kabNames.clear();
                            kecNames.clear();
                            input_kab.setText("");
                            input_kec.setText("");
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
                            // Filter ZoneAdapter
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
            
                            String kabID = "0";
                            input_kab.setText(selectedItem);
            
                            if (!kabAdapter.getItem(position).equalsIgnoreCase("Other")) {
                
                                for (int i = 0; i< kabList.size(); i++) {
                                    if (kabList.get(i).getKabNama().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        kabID = kabList.get(i).getKabId();
                                    }
                                }
                            }
            
                            selectedKabID = kabID;
                            // Request for all Kecamatan in the selected Country
                            RequestKec(selectedKabID);

                            kecNames.clear();
                            input_kec.setText("");
                        }
                    });
                }

                return false;
            }
        });


        // Handle Touch event of input_kab EditText
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

                    dialog_title.setText(getString(R.string.zone));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(kecAdapter);

                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Filter ZoneAdapter
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

                            if (!kecAdapter.getItem(position).equalsIgnoreCase("Other")) {

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

        // Handle the Click event of Save Button
        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Address Form Inputs
                boolean isValidData = validateAddressForm();

                if (isValidData) {
                    if (isUpdate) {
                        // Update the Address
                        updateUserAddress(addressID);
                    } else {
                        // Add a new Address
                        addUserAddress();
                    }
                }
            }
        });


        return rootView;
    }



    //*********** Get Provinsi List from the Server ********//

    private void RequestProv() {

        Call<Provinsi> call = APIClient.getInstance()
                .getProvinsi();

        call.enqueue(new Callback<Provinsi>() {
            @Override
            public void onResponse(Call<Provinsi> call, Response<Provinsi> response) {
                
                if (response.isSuccessful()) {

                	// Check the Success status
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

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
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Get Kabupaten List of the Country from the Server ********//

    private void RequestKec(String kecId) {

        Call<Kecamatan> call = APIClient.getInstance()
                .getKecamatan
                        (
                                kecId
                        );

        call.enqueue(new Callback<Kecamatan>() {
            @Override
            public void onResponse(Call<Kecamatan> call, Response<Kecamatan> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
    
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
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void RequestKab(String provId) {

        Call<Kabupaten> call = APIClient.getInstance()
                .getKabupaten
                        (
                                provId
                        );

        call.enqueue(new Callback<Kabupaten>() {
            @Override
            public void onResponse(Call<Kabupaten> call, Response<Kabupaten> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        kabList = response.body().getData();

                        // Add the Zone Names to the kabNames List
                        for (int i = 0; i< kabList.size(); i++){
                            kabNames.add(kabList.get(i).getKabNama());
                        }

                        kabNames.add("Other");

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
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Proceed the Request of New Address ********//

    public void addUserAddress() {

        final String customers_default_address_id = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");


        Call<AddressData> call = APIClient.getInstance()
                .addUserAddress
                        (
                                customerID,
                                input_firstname.getText().toString().trim(),
                                input_lastname.getText().toString().trim(),
                                input_address.getText().toString().trim(),
                                input_postcode.getText().toString().trim(),
                                input_city.getText().toString().trim(),
                                selectedProvID,
                                selectedKabID,
                                selectedKecID,
                                customers_default_address_id
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {
                
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Address has been added to User's Addresses
                        // Navigate to Addresses fragment
                        ((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
                        
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
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Proceed the Request of Update Address ********//

    public void updateUserAddress(String addressID) {

        final String customers_default_address_id = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");


        Call<AddressData> call = APIClient.getInstance()
                .updateUserAddress
                        (
                                customerID,
                                addressID,
                                input_firstname.getText().toString().trim(),
                                input_lastname.getText().toString().trim(),
                                input_address.getText().toString().trim(),
                                input_postcode.getText().toString().trim(),
                                input_city.getText().toString().trim(),
                                selectedProvID,
                                selectedKabID,
                                selectedKecID,
                                customers_default_address_id
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {
                
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Address has been Edited
                        // Navigate to Addresses fragment
                        ((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
                        
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Address has not been Edited
                        // Show the Message to the User
                        Toast.makeText(getContext(), ""+response.body().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
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
        } else if (!ValidateInputs.isValidInput(input_kec.getText().toString().trim())) {
            input_kab.setError(getString(R.string.select_your_subdistrict));
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

