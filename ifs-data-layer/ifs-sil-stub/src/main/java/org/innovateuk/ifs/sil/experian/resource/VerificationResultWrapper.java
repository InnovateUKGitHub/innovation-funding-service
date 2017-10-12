package org.innovateuk.ifs.sil.experian.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerificationResultWrapper {
    @JsonProperty("VerificationResult")
    private VerificationResult verificationResult;

    public VerificationResultWrapper() {}

    public VerificationResultWrapper(VerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }

    public org.innovateuk.ifs.sil.experian.resource.VerificationResult getVerificationResult() {
        return verificationResult;
    }

    public void setVerificationResult(org.innovateuk.ifs.sil.experian.resource.VerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }
}
