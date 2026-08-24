package com.globetrotter.dto;

public class TripSharingResponse {

    private Long tripId;
    private Boolean isPublic;
    private String shareToken;
    private String publicUrl;

    public TripSharingResponse() {
    }

    public TripSharingResponse(Long tripId, Boolean isPublic, String shareToken, String publicUrl) {
        this.tripId = tripId;
        this.isPublic = isPublic;
        this.shareToken = shareToken;
        this.publicUrl = publicUrl;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }
}
