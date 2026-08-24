package com.globetrotter.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateSharingRequest {

    @NotNull(message = "isPublic is required")
    private Boolean isPublic;

    public UpdateSharingRequest() {
    }

    public UpdateSharingRequest(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
