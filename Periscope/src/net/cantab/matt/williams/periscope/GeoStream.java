package net.cantab.matt.williams.periscope;

public class GeoStream {
    private boolean deleted;
    private float latitude;
    private float longitude;
    private float altitude;
    private String sessionId;
    private String tokenKey;

    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    public float getAltitude() {
        return altitude;
    }
    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getTokenKey() {
        return tokenKey;
    }
    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }
}
