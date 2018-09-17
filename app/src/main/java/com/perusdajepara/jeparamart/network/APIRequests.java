package com.perusdajepara.jeparamart.network;


import com.perusdajepara.jeparamart.models.address_model.AddressData;
import com.perusdajepara.jeparamart.models.address_model.Kecamatan;
import com.perusdajepara.jeparamart.models.address_model.Provinsi;
import com.perusdajepara.jeparamart.models.address_model.Kabupaten;
import com.perusdajepara.jeparamart.models.banner_model.BannerData;
import com.perusdajepara.jeparamart.models.category_model.CategoryData;
import com.perusdajepara.jeparamart.models.contact_model.ContactUsData;
import com.perusdajepara.jeparamart.models.device_model.AppSettingsData;
import com.perusdajepara.jeparamart.models.filter_model.get_filters.FilterData;
import com.perusdajepara.jeparamart.models.language_model.LanguageData;
import com.perusdajepara.jeparamart.models.news_model.all_news.NewsData;
import com.perusdajepara.jeparamart.models.news_model.news_categories.NewsCategoryData;
import com.perusdajepara.jeparamart.models.pages_model.PagesData;
import com.perusdajepara.jeparamart.models.product_model.GetAllProducts;
import com.perusdajepara.jeparamart.models.coupons_model.CouponsData;
import com.perusdajepara.jeparamart.models.payment_model.PaymentMethodsData;
import com.perusdajepara.jeparamart.models.shipping_model.PostTaxAndShippingData;
import com.perusdajepara.jeparamart.models.order_model.OrderData;
import com.perusdajepara.jeparamart.models.payment_model.GetBrainTreeToken;
import com.perusdajepara.jeparamart.models.order_model.PostOrder;
import com.perusdajepara.jeparamart.models.product_model.ProductData;
import com.perusdajepara.jeparamart.models.search_model.SearchData;
import com.perusdajepara.jeparamart.models.shipping_model.ShippingRateData;
import com.perusdajepara.jeparamart.models.user_model.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;


/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface APIRequests {


    //******************** User Data ********************//

    @FormUrlEncoded
    @POST("processRegistration")
    Call<UserData> processRegistration(     @Field("customers_firstname") String customers_firstname,
                                            @Field("customers_lastname") String customers_lastname,
                                            @Field("customers_email_address") String customers_email_address,
                                            @Field("customers_password") String customers_password,
                                            @Field("customers_telephone") String customers_telephone,
                                            @Field("customers_gender") String customers_gender,
                                            @Field("customers_picture") String customers_picture);

    @FormUrlEncoded
    @POST("processLogin")
    Call<UserData> processLogin(            @Field("customers_email_address") String customers_email_address,
                                            @Field("customers_password") String customers_password );

    @FormUrlEncoded
    @POST("facebookRegistration")
    Call<UserData> facebookRegistration(    @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("googleRegistration")
    Call<UserData> googleRegistration(      @Field("idToken") String idToken,
                                            @Field("userId") String userId,
                                            @Field("givenName") String givenName,
                                            @Field("familyName") String familyName,
                                            @Field("email") String email,
                                            @Field("imageUrl") String imageUrl);

    @FormUrlEncoded
    @POST("processForgotPassword")
    Call<UserData> processForgotPassword(   @Field("customers_email_address") String customers_email_address );

    @FormUrlEncoded
    @POST("updateCustomerInfo")
    Call<UserData> updateCustomerInfo(      @Field("customers_id") String customers_id,
                                            @Field("customers_firstname") String customers_firstname,
                                            @Field("customers_lastname") String customers_lastname,
                                            @Field("customers_telephone") String customers_telephone,
                                            @Field("customers_dob") String customers_dob,
                                            @Field("customers_picture") String customers_picture,
                                            @Field("customers_old_picture") String customers_old_picture,
                                            @Field("customers_current_password") String customers_current_password,
                                            @Field("customers_password") String customers_password,
                                            @Field("customers_gender") String gender);
    
    
    
    //******************** Address Data ********************//
    
    @POST("getProvinsi")
    Call<Provinsi> getProvinsi();

    @FormUrlEncoded
    @POST("getKabupaten")
    Call<Kabupaten> getKabupaten(           @Field("id_prov") String id_prov);

    @FormUrlEncoded
    @POST("getKecamatan")
    Call<Kecamatan> getKecamatan(           @Field("id_kab") String id_kab);

    @FormUrlEncoded
    @POST("getAllAddress")
    Call<AddressData> getAllAddress(        @Field("customers_id") String customers_id);

    @FormUrlEncoded
    @POST("addShippingAddress")
    Call<AddressData> addUserAddress(       @Field("customers_id") String customers_id,
                                            @Field("entry_firstname") String entry_firstname,
                                            @Field("entry_lastname") String entry_lastname,
                                            @Field("entry_street_address") String entry_street_address,
                                            @Field("entry_postcode") String entry_postcode,
                                            @Field("entry_city") String entry_city,
                                            @Field("entry_country_id") String entry_prov_id,
                                            @Field("entry_zone_id") String entry_kab_id,
                                            @Field("entry_kec_id") String entry_kec_id,
                                            @Field("customers_default_address_id") String customers_default_address_id );

    @FormUrlEncoded
    @POST("updateShippingAddress")
    Call<AddressData> updateUserAddress(    @Field("customers_id") String customers_id,
                                            @Field("address_id") String address_id,
                                            @Field("entry_firstname") String entry_firstname,
                                            @Field("entry_lastname") String entry_lastname,
                                            @Field("entry_street_address") String entry_street_address,
                                            @Field("entry_postcode") String entry_postcode,
                                            @Field("entry_city") String entry_city,
                                            @Field("entry_country_id") String entry_prov_id,
                                            @Field("entry_zone_id") String entry_kab_id,
                                            @Field("entry_kec_id") String entry_kec_id,
                                            @Field("customers_default_address_id") String customers_default_address_id );

    @FormUrlEncoded
    @POST("updateDefaultAddress")
    Call<AddressData> updateDefaultAddress( @Field("customers_id") String customers_id,
                                            @Field("address_book_id") String address_book_id );

    @FormUrlEncoded
    @POST("deleteShippingAddress")
    Call<AddressData> deleteUserAddress(    @Field("customers_id") String customers_id,
                                            @Field("address_book_id") String address_book_id );

    

    //******************** Category Data ********************//

    @FormUrlEncoded
    @POST("allCategories")
    Call<CategoryData> getAllCategories(    @Field("language_id") int language_id);
    


    //******************** Product Data ********************//

    @POST("getAllProducts")
    Call<ProductData> getAllProducts(       @Body GetAllProducts getAllProducts);


    @FormUrlEncoded
    @POST("likeProduct")
    Call<ProductData> likeProduct(          @Field("liked_products_id") int liked_products_id,
                                            @Field("liked_customers_id") String liked_customers_id );

    @FormUrlEncoded
    @POST("unlikeProduct")
    Call<ProductData> unlikeProduct(        @Field("liked_products_id") int liked_products_id,
                                            @Field("liked_customers_id") String liked_customers_id );
    

    @FormUrlEncoded
    @POST("getFilters")
    Call<FilterData> getFilters(            @Field("categories_id") int categories_id,
                                            @Field("language_id") int language_id);


    @FormUrlEncoded
    @POST("getSearchData")
    Call<SearchData> getSearchData(         @Field("searchValue") String searchValue,
                                            @Field("language_id") int language_id);


    
    //******************** News Data ********************//

    @FormUrlEncoded
    @POST("getAllNews")
    Call<NewsData> getAllNews(              @Field("language_id") int language_id,
                                            @Field("page_number") int page_number,
                                            @Field("is_feature") int is_feature,
                                            @Field("categories_id") String categories_id);

    @FormUrlEncoded
    @POST("allNewsCategories")
    Call<NewsCategoryData> allNewsCategories(@Field("language_id") int language_id,
                                            @Field("page_number") int page_number);



    //******************** Order Data ********************//
    
    @POST("addToOrder")
    Call<OrderData> addToOrder(             @Body PostOrder postOrder);

    @FormUrlEncoded
    @POST("getOrders")
    Call<OrderData> getOrders(              @Field("customers_id") String customers_id,
                                            @Field("language_id") int language_id);


    @FormUrlEncoded
    @POST("getCoupon")
    Call<CouponsData> getCouponInfo(        @Field("code") String code);
    
    
    @FormUrlEncoded
    @POST("getPaymentMethods")
    Call<PaymentMethodsData> getPaymentMethods(@Field("language_id") int language_id);

    @GET("generateBraintreeToken")
    Call<GetBrainTreeToken> generateBraintreeToken();



    //******************** Banner Data ********************//

    @GET("getBanners")
    Call<BannerData> getBanners();


    
    //******************** Tax & Shipping Data ********************//

    @POST("getRate")
    Call<ShippingRateData> getShippingMethodsAndTax(
                                            @Body PostTaxAndShippingData postTaxAndShippingData);



    //******************** Contact Us Data ********************//

    @FormUrlEncoded
    @POST("contactUs")
    Call<ContactUsData> contactUs(          @Field("name") String name,
                                            @Field("email") String email,
                                            @Field("message") String message);


    
    //******************** Languages Data ********************//
    
    @GET("getLanguages")
    Call<LanguageData> getLanguages();


    
    //******************** App Settings Data ********************//

    @GET("siteSetting")
    Call<AppSettingsData> getAppSetting();
    
    
    
    //******************** Static Pages Data ********************//
    
    @FormUrlEncoded
    @POST("getAllPages")
    Call<PagesData> getStaticPages(         @Field("language_id") int language_id);
    
    
    
    //******************** Notifications Data ********************//
    
    @FormUrlEncoded
    @POST("registerDevices")
    Call<UserData> registerDeviceToFCM(     @Field("device_id") String device_id,
                                            @Field("device_type") String device_type,
                                            @Field("ram") String ram,
                                            @Field("processor") String processor,
                                            @Field("device_os") String device_os,
                                            @Field("location") String location,
                                            @Field("device_model") String device_model,
                                            @Field("manufacturer") String manufacturer,
                                            @Field("customers_id") String customers_id);


    @FormUrlEncoded
    @POST("notify_me")
    Call<ContactUsData> notify_me(          @Field("is_notify") String is_notify,
                                            @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("updateOrderStatus")
    Call<OrderData> updateOrderStatus(          @Field("orders_id") String orders_id);
    
}

