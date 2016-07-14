package com.worth.ifs.sil.experian.resource;

import java.util.List;

public class VerificationResult {
    String personalDetailsScore;
    String addressScore;
    String companyNameScore;
    String regNumberScore;
    List<Condition> conditions;
}
