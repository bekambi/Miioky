package com.bekambimouen.miiokychallenge.model;

public class FrequentedEstablishment {
    private String user_establishment;
    private String user_establishment_header;
    private String user_id;
    private String establishmentKey;
    private long date_created;

    public FrequentedEstablishment() {
    }

    public FrequentedEstablishment(String user_establishment_header, String user_establishment, String user_id,
                                   String establishmentKey, long date_created) {
        this.user_establishment_header = user_establishment_header;
        this.user_establishment = user_establishment;
        this.user_id = user_id;
        this.establishmentKey = establishmentKey;
        this.date_created = date_created;
    }

    public String getUser_establishment_header() {
        return user_establishment_header;
    }

    public void setUser_establishment_header(String user_establishment_header) {
        this.user_establishment_header = user_establishment_header;
    }

    public String getUser_establishment() {
        return user_establishment;
    }

    public void setUser_establishment(String user_establishment) {
        this.user_establishment = user_establishment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEstablishmentKey() {
        return establishmentKey;
    }

    public void setEstablishmentKey(String establishmentKey) {
        this.establishmentKey = establishmentKey;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}
