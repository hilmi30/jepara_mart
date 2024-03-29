package com.perusdajepara.jeparamart.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.R;

import java.util.List;

import com.perusdajepara.jeparamart.fragments.My_Addresses;
import com.perusdajepara.jeparamart.fragments.Add_Address;
import com.perusdajepara.jeparamart.models.address_model.AddressDetails;


/**
 * AddressListAdapter is the adapter class of RecyclerView holding List of Addresses in My_Addresses
 **/

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> {

    Context context;
    String customerID;
    List<AddressDetails> addressList;

    private int selectedPosition;

    // To keep track of Checked Radio Button
    private RadioButton lastChecked_RB = null;


    public AddressListAdapter(Context context, String customerID, int defaultAddressPosition, List<AddressDetails> addressList) {
        this.context = context;
        this.customerID = customerID;
        this.addressList = addressList;
        this.selectedPosition = defaultAddressPosition;
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_addresses, parent, false);

        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Get the data model based on Position
        final AddressDetails addressDetails = addressList.get(position);

        final String addressID = String.valueOf(addressDetails.getAddressId());

        holder.address_title.setText(addressDetails.getFirstname() +" "+ addressDetails.getLastname());
        holder.address_details.setText(addressDetails.getKecNama() +", "+ addressDetails.getKabNama() +", "+ addressDetails.getProvName());
        

        // Toggle the Default Address RadioButton with Position
        if (position == selectedPosition) {
            holder.makeDefault_rb.setChecked(true);
            lastChecked_RB = holder.makeDefault_rb;
        } else {
            holder.makeDefault_rb.setChecked(false);
        }

        // Check the Clicked RadioButton
        holder.makeDefault_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lastChecked_RB != null) {
                    lastChecked_RB.setChecked(false);
                }

                // Request the Server to Change Default Address
                My_Addresses.MakeAddressDefault(customerID, addressID, context, view);

                lastChecked_RB = holder.makeDefault_rb;
                selectedPosition = position;

                // Notify that items from specified position have changed.
                notifyItemRangeChanged(holder.getAdapterPosition(), addressList.size());
            }
        });


        // Edit relevant Address
        holder.edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the current Address Info to Bundle
                Bundle addressInfo = new Bundle();
                addressInfo.putBoolean("isUpdate", true);
                addressInfo.putString("addressID", addressID);
                addressInfo.putString("addressFirstname", addressDetails.getFirstname());
                addressInfo.putString("addressLastname", addressDetails.getLastname());
                addressInfo.putString("addressProvName", addressDetails.getProvName());
                addressInfo.putString("addressProvID", ""+addressDetails.getProvId());
                addressInfo.putString("addressKabName", addressDetails.getKabNama());
                addressInfo.putString("addressKabID", ""+addressDetails.getKabId());
                addressInfo.putString("addressKecName", addressDetails.getKecNama());
                addressInfo.putString("addressKecID", ""+addressDetails.getKecId());
                addressInfo.putString("addressState", addressDetails.getState());
                addressInfo.putString("addressCity", addressDetails.getCity());
                addressInfo.putString("addressStreet", addressDetails.getStreet());
                addressInfo.putString("addressPostCode", addressDetails.getPostcode());
                addressInfo.putString("addressSuburb", addressDetails.getSuburb());
                addressInfo.putDouble("addressLat", addressDetails.getLat());
                addressInfo.putDouble("addressLng", addressDetails.getLng());

                // Navigate to Add_Address Fragment with arguments to Edit Address
                Fragment fragment = new Add_Address();
                fragment.setArguments(addressInfo);
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });

        holder.delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(R.string.delete_address);
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        My_Addresses.DeleteAddress(customerID, addressID, context, holder.itemView);

                        // Remove CartItem from Cart List
                        addressList.remove(holder.getAdapterPosition());

                        // Notify that item at position has been removed
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert1 = alert.create();
                alert1.show();
            }
        });

    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return addressList.size();
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton edit_address, delete_address;
        RadioButton makeDefault_rb;
        TextView address_title, address_details;


        public MyViewHolder(final View itemView) {
            super(itemView);

            address_title = (TextView) itemView.findViewById(R.id.address_title);
            address_details = (TextView) itemView.findViewById(R.id.address_details);
            edit_address = (ImageButton) itemView.findViewById(R.id.edit_address);
            delete_address = (ImageButton) itemView.findViewById(R.id.delete_address);
            makeDefault_rb = (RadioButton) itemView.findViewById(R.id.default_address_rb);
        }
    }
}

