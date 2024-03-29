package com.perusdajepara.jeparamart.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.perusdajepara.jeparamart.activities.Signup;
import com.perusdajepara.jeparamart.customs.CircularImageView;

import com.perusdajepara.jeparamart.activities.MainActivity;
import com.perusdajepara.jeparamart.R;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.perusdajepara.jeparamart.constant.ConstantValues;
import com.perusdajepara.jeparamart.customs.DialogLoader;
import com.perusdajepara.jeparamart.databases.User_Info_DB;
import com.perusdajepara.jeparamart.models.product_model.Image;
import com.perusdajepara.jeparamart.models.user_model.UserData;
import com.perusdajepara.jeparamart.models.user_model.UserDetails;
import com.perusdajepara.jeparamart.network.APIClient;
import com.perusdajepara.jeparamart.utils.CheckPermissions;
import com.perusdajepara.jeparamart.utils.Utilities;
import com.perusdajepara.jeparamart.utils.ImagePicker;
import com.perusdajepara.jeparamart.utils.ValidateInputs;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class Update_Account extends Fragment {

    View rootView;
    String customers_id;
    String profileImageCurrent = "";
    String profileImageChanged = "";
    String mCurrentPhotoPath = "";
    private static final int PICK_IMAGE_ID = 360;           // the number doesn't matter
    private static final int PICK_GALLERY_ID = 350;
    private static final int galleryCode = 370;
    private static final int cameraCode = 380;

    Button updateInfoBtn;
    CircularImageView user_photo;
    FloatingActionButton user_photo_edit_fab;
    EditText input_first_name, input_last_name, input_dob, input_contact_no, input_current_password, input_new_password;
    RadioGroup radioGroup;
    RadioButton radioButton;

    DialogLoader dialogLoader;
    Bitmap bitmap;

    UserDetails userInfo;
    User_Info_DB userInfoDB = new User_Info_DB();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.update_account, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionAccount));
        MainActivity.jmartLogo.setVisibility(View.GONE);

        // Get the CustomerID from SharedPreferences
        customers_id = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views
        user_photo = (CircularImageView) rootView.findViewById(R.id.user_photo_edit);
        input_first_name = (EditText) rootView.findViewById(R.id.firstname);
        input_last_name = (EditText) rootView.findViewById(R.id.lastname);
        input_dob = (EditText) rootView.findViewById(R.id.dob);
        input_contact_no = (EditText) rootView.findViewById(R.id.contact);
        input_current_password = (EditText) rootView.findViewById(R.id.current_password);
        input_new_password = (EditText) rootView.findViewById(R.id.new_password);
        updateInfoBtn = (Button) rootView.findViewById(R.id.updateInfoBtn);
        user_photo_edit_fab = (FloatingActionButton) rootView.findViewById(R.id.user_photo_edit_fab);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_update);


        // Set KeyListener of some View to null
        input_dob.setKeyListener(null);


        dialogLoader = new DialogLoader(getContext());

        // Get the User's Info from the Local Databases User_Info_DB
        userInfo = userInfoDB.getUserData(customers_id);
        

        // Set User's Info to Form Inputs
        input_first_name.setText(userInfo.getCustomersFirstname());
        input_last_name.setText(userInfo.getCustomersLastname());
        input_contact_no.setText(userInfo.getCustomersTelephone());


        if(userInfo.getCustomersGender().equalsIgnoreCase("1")) {
            radioGroup.check(R.id.radioMaleUpdate);
        } else {
            radioGroup.check(R.id.radioFemaleUpdate);
        }

        // Set User's Date of Birth
        if (userInfo.getCustomersDob().equalsIgnoreCase("0000-00-00 00:00:00")) {
            input_dob.setText("");
        }
        else {
            // Get the String of Date from userInfo
            String dateString = userInfo.getCustomersDob();
            Log.e("tanggal", dateString);

            Locale locale = new Locale("in", "ID");
            // Set Date Format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", locale);

            // Convert String of Date to Date Format
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            input_dob.setText(dateFormat.format(convertedDate));
        }


        Log.d("userPhoto", ConstantValues.ECOMMERCE_URL+userInfo.getCustomersPicture());

        // Set User's Photo
        if (!TextUtils.isEmpty(userInfo.getCustomersPicture())  &&  userInfo.getCustomersPicture() != null){
            profileImageCurrent = userInfo.getCustomersPicture();
            Log.d("current", profileImageCurrent);
            // Picasso.with(getContext()).invalidate(ConstantValues.ECOMMERCE_URL+profileImageCurrent);
            Picasso.with(getContext())
                    .load(ConstantValues.ECOMMERCE_URL+profileImageCurrent)
                    // .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(user_photo);

        }
        else {
            profileImageCurrent = "";
            Picasso.with(getContext())
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(user_photo);
        }



        // Handle Touch event of input_dob EditText
        input_dob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get Calendar instance
                    final Calendar calendar = Calendar.getInstance();

                    // Initialize DateSetListener of DatePickerDialog
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            // Set the selected Date Info to Calendar instance
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            Locale locale = new Locale("in", "ID");
                            // Set Date Format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", locale);

                            // Set Date in input_dob EditText
                            input_dob.setText(dateFormat.format(calendar.getTime()));
                        }
                    };


                    // Initialize DatePickerDialog
                    DatePickerDialog datePicker = new DatePickerDialog
                            (
                                    getContext(),
                                    date,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );

                    // Show datePicker Dialog
                    datePicker.show();
                }

                return false;
            }
        });



        // Handle Click event of user_photo_edit_fab FAB
        user_photo_edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (CheckPermissions.is_CAMERA_PermissionGranted() && CheckPermissions.is_STORAGE_PermissionGranted()) {
                    pickImage();
                    // Toast.makeText(getContext(), "cek", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermissions
                        (
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CheckPermissions.PERMISSIONS_REQUEST_CAMERA
                        );
                }
                
            }
        });


        // Handle Click event of updateInfoBtn Button
        updateInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate User's Info Form Inputs
                boolean isValidData = validateInfoForm();

                if (isValidData) {
                    if ("".equalsIgnoreCase(input_current_password.getText().toString()) &&  "".equalsIgnoreCase(input_new_password.getText().toString())) {
                        // Proceed User Registration
                        updateCustomerInfo();
                    }
                    else {
                        if (validatePasswordForm())
                            updateCustomerInfo();
                    }
                    
                }
            }
        });


        return rootView;

    }



    //*********** Picks User Profile Image from Gallery or Camera ********//

    private void pickImage() {
//        // Get Intent with Options of Image Picker Apps from the static method of ImagePicker class
//        Intent chooseImageIntent = ImagePicker.getImagePickerIntent(getContext());
//
//        // Start Activity with Image Picker Intent
//        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alert.setTitle(R.string.take_image);
        alert.setMessage(R.string.choose_action_to_take_image);
        alert.setPositiveButton(R.string.gallery, (dialogInterface, i) -> {
            fromGallery();
        });
        alert.setNegativeButton(R.string.camera, (dialogInterface, i) -> {
            fromCamera();
        });
        alert.create().show();
    }

    private void fromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, cameraCode);
    }

    private void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), galleryCode);
    }

    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_ID) {

                // Get the User Selected Image as Bitmap from the static method of ImagePicker class
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);

                    // Upload the Bitmap to ImageView
                    user_photo.setImageBitmap(bitmap);

                    // Get the converted Bitmap as Base64ImageString from the static method of Helper class
                    profileImageChanged = Utilities.getBase64ImageStringFromBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == galleryCode) {
                if (data != null) {
                    Uri contentUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                                contentUri);
                        Bitmap scaleBitmap = Utilities.scaleDown(bitmap, 500f, false);
                        user_photo.setImageBitmap(scaleBitmap);
                        profileImageChanged = Utilities.getBase64ImageStringFromBitmap(scaleBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == cameraCode) {
                if (data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap scaleBitmap = Utilities.scaleDown(bitmap, 500f, false);
                    user_photo.setImageBitmap(scaleBitmap);
                    profileImageChanged = Utilities.getBase64ImageStringFromBitmap(scaleBitmap);
                }
            }
        }
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = user_photo.getWidth();
        int targetH = user_photo.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Get the converted Bitmap as Base64ImageString from the static method of Helper class
        profileImageChanged = Utilities.getBase64ImageStringFromBitmap(bitmap);
        Log.d("base", profileImageChanged);

        user_photo.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //*********** This method is invoked for every call on requestPermissions(Activity, String[], int) ********//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CheckPermissions.PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // The Camera and Storage Permission is granted
                pickImage();
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    // Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.permission_camera_storage));
                    builder.setMessage(getString(R.string.permission_camera_storage_needed));
                    builder.setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestPermissions
                                (
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    CheckPermissions.PERMISSIONS_REQUEST_CAMERA
                                );
                        }
                    });
                    builder.setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(getContext(),getString(R.string.permission_rejected), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    
    //*********** Updates User's Personal Information ********//

    private void updateCustomerInfo() {

        int selectedRadio = radioGroup.getCheckedRadioButtonId();

        radioButton = (RadioButton) rootView.findViewById(selectedRadio);

        dialogLoader.showProgressDialog();

        Call<UserData> call = APIClient.getInstance()
                .updateCustomerInfo
                        (
                                customers_id,
                                input_first_name.getText().toString().trim(),
                                input_last_name.getText().toString().trim(),
                                input_contact_no.getText().toString().trim(),
                                input_dob.getText().toString().trim(),
                                profileImageChanged,
                                profileImageCurrent,
                                input_current_password.getText().toString().trim(),
                                input_new_password.getText().toString().trim(),
                                radioButton.getTag().toString()
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")  &&  response.body().getData() != null) {
                        // User's Info has been Updated.
                        
                        UserDetails userDetails = response.body().getData().get(0);
                        Log.d("update", userDetails.getCustomersPicture());

                        // Update in Local Databases as well
                        userInfoDB.updateUserData(userDetails);
                        userInfoDB.updateUserPassword(userDetails);
    
                        // Get the User's Info from the Local Databases User_Info_DB
                        userInfo = userInfoDB.getUserData(customers_id);
                        Log.d("dbUserInfo", userInfo.getCustomersPicture());
                        
                        // Set the userName in SharedPreferences
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).edit();
                        editor.putString("userName", userDetails.getCustomersFirstname()+" "+userDetails.getCustomersLastname());
                        editor.apply();

                        // Set the User Info in the NavigationDrawer Header with the public method of MainActivity
                        ((MainActivity)getActivity()).setupExpandableDrawerHeader();

                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Unable to Update User's Info.
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                    
                }
                else {
                    Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Validate User Info Form Inputs ********//
    
    private boolean validateInfoForm() {
        if (!ValidateInputs.isValidName(input_first_name.getText().toString().trim())) {
            input_first_name.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(input_last_name.getText().toString().trim())) {
            input_last_name.setError(getString(R.string.invalid_last_name));
            return false;
        } else {
            return true;
        }
    }



    //*********** Validate Password Info Form Inputs ********//

    private boolean validatePasswordForm() {
        if (!ValidateInputs.isValidPassword(input_current_password.getText().toString().trim())) {
            input_current_password.setError(getString(R.string.invalid_password));
            return false;
        } else if (!ValidateInputs.isValidPassword(input_new_password.getText().toString().trim())) {
            input_new_password.setError(getString(R.string.invalid_password));
            return false;
        } else {
            return true;
        }
    }

}
