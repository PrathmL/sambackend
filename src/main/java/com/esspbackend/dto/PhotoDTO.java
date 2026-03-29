package com.esspbackend.dto;

public class PhotoDTO {
    private String url;
    private String caption;
    private Double latitude;
    private Double longitude;
    private String geoLocation; // For progress photos which use String

    public PhotoDTO() {}

    public PhotoDTO(String url, String caption, Double latitude, Double longitude) {
        this.url = url;
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PhotoDTO(String url, String caption, String geoLocation) {
        this.url = url;
        this.caption = caption;
        this.geoLocation = geoLocation;
        
        // Try to parse lat/long from geoLocation string if possible
        if (geoLocation != null && geoLocation.contains(",")) {
            try {
                String[] parts = geoLocation.split(",");
                this.latitude = Double.parseDouble(parts[0].trim());
                this.longitude = Double.parseDouble(parts[1].trim());
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }
}
