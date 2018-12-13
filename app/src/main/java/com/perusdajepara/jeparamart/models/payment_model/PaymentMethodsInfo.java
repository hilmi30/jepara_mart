
package com.perusdajepara.jeparamart.models.payment_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentMethodsInfo {
    
    @SerializedName("environment")
    @Expose
    private String environment;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("method")
    @Expose
    private String method;

    @SerializedName("public_key")
    @Expose
    private String publicKey;

    @SerializedName("public_key2")
    @Expose
    private String publicKey2;

    @SerializedName("public_key3")
    @Expose
    private String publicKey3;

    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("payment_currency")
    @Expose
    private String paymentCurrency;

    @SerializedName("bank_name")
    @Expose
    private String bankName;

    @SerializedName("bank_name2")
    @Expose
    private String bankName2;

    @SerializedName("bank_name3")
    @Expose
    private String bankName3;

    public String getPublicKey2() {
        return publicKey2;
    }

    public void setPublicKey2(String publicKey2) {
        this.publicKey2 = publicKey2;
    }

    public String getPublicKey3() {
        return publicKey3;
    }

    public void setPublicKey3(String publicKey3) {
        this.publicKey3 = publicKey3;
    }

    public String getBankName2() {
        return bankName2;
    }

    public void setBankName2(String bankName2) {
        this.bankName2 = bankName2;
    }

    public String getBankName3() {
        return bankName3;
    }

    public void setBankName3(String bankName3) {
        this.bankName3 = bankName3;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
    
    public String getPaymentCurrency() {
        return paymentCurrency;
    }
    
    public void setPaymentCurrency(String paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }
}

