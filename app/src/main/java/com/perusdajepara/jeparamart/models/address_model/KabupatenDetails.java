package com.perusdajepara.jeparamart.models.address_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KabupatenDetails {


    @SerializedName("id_kab")
    @Expose
    private String kabId;

    @SerializedName("nama")
    @Expose
    private String kabNama;

    @SerializedName("id_prov")
    @Expose
    private String provId;

    // ======================================================================== //

    @SerializedName("zone_id")
    @Expose
    private int zoneId;
    @SerializedName("zone_country_id")
    @Expose
    private int zoneCountryId;
    @SerializedName("zone_code")
    @Expose
    private String zoneCode;
    @SerializedName("zone_name")
    @Expose
    private String zoneName;

    /**
     *
     * @return
     *     The zoneId
     */
    public int getZoneId() {
        return zoneId;
    }

    /**
     *
     * @param zoneId
     *     The zone_id
     */
    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    /**
     *
     * @return
     *     The zoneCountryId
     */
    public int getZoneCountryId() {
        return zoneCountryId;
    }

    /**
     *
     * @param zoneCountryId
     *     The zone_country_id
     */
    public void setZoneCountryId(int zoneCountryId) {
        this.zoneCountryId = zoneCountryId;
    }

    /**
     *
     * @return
     *     The zoneCode
     */
    public String getZoneCode() {
        return zoneCode;
    }

    /**
     *
     * @param zoneCode
     *     The zone_code
     */
    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    /**
     *
     * @return
     *     The zoneName
     */
    public String getZoneName() {
        return zoneName;
    }

    /**
     *
     * @param zoneName
     *     The zone_name
     */
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getKabId() {
        return kabId;
    }

    public void setKabId(String kabId) {
        this.kabId = kabId;
    }

    public String getKabNama() {
        return kabNama;
    }

    public void setKabNama(String kabNama) {
        this.kabNama = kabNama;
    }

    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }
}
