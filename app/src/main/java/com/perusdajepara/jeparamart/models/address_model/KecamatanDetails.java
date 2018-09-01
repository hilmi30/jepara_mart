package com.perusdajepara.jeparamart.models.address_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KecamatanDetails {

    @SerializedName("id_kab")
    @Expose
    private String kabId;

    @SerializedName("nama")
    @Expose
    private String kecNama;

    @SerializedName("id_kec")
    @Expose
    private String kecId;

    public String getKabId() {
        return kabId;
    }

    public void setKabId(String kabId) {
        this.kabId = kabId;
    }

    public String getKecNama() {
        return kecNama;
    }

    public void setKecNama(String kecNama) {
        this.kecNama = kecNama;
    }

    public String getKecId() {
        return kecId;
    }

    public void setKecId(String kecId) {
        this.kecId = kecId;
    }
}
