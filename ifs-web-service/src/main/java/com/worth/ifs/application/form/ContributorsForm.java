package com.worth.ifs.application.form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ContributorsForm implements Serializable {
    private final Log log = LogFactory.getLog(getClass());

    private boolean triedToSave = false;
    private Long applicationId;

    @Valid
    private List<OrganisationInviteForm> organisations;

    public ContributorsForm() {
        organisations = new LinkedList<>();
    }

    public List<OrganisationInviteForm> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationInviteForm> organisations) {
        this.organisations = organisations;
    }

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void merge(ContributorsForm contributorsFormCookie) {
        this.setTriedToSave(contributorsFormCookie.isTriedToSave());
//        this.setOrganisations(contributorsFormCookie.getOrganisations());
        log.info(String.format("merge; %s", this.getOrganisations().size()));

        if(!contributorsFormCookie.getOrganisations().isEmpty()){
            contributorsFormCookie.getOrganisations().forEach(oC ->{
                Optional<OrganisationInviteForm> existingOrgOptional = this.getOrganisations().stream().filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName())).findAny();

                if(!existingOrgOptional.isPresent()){
                    log.info("ADD: " + oC.getOrganisationName());
                    log.info("ADD: " + oC.getOrganisationId());
                    this.getOrganisations().add(oC);
                }else{
                    log.info("merge org "+ existingOrgOptional.get().getOrganisationName() + " invite count: " + existingOrgOptional.get().getInvites().size() + " vs "+ oC.getInvites().size());

                    OrganisationInviteForm existingOrg = existingOrgOptional.get();
                    List<InviteeForm> existingInvites = existingOrg.getInvites();
                    oC.getInvites().forEach(iC -> {
                        Optional<InviteeForm> cookieInviteFound = existingInvites.stream()
                                .filter(i -> StringUtils.isNotEmpty(iC.getEmail()) && i.getEmail().equals(iC.getEmail()) && StringUtils.isNotEmpty(iC.getPersonName()) && i.getPersonName().equals(iC.getPersonName())).findAny();
                        if(!cookieInviteFound.isPresent()){
                            log.info("addInvite "+ oC.getOrganisationName() + " email " + iC.getEmail() + " name: "+iC.getPersonName());
                            existingInvites.add(iC);
                        }
                    });


                    existingOrgOptional = this.getOrganisations().stream().filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName())).findAny();
                    if(existingOrgOptional.isPresent()){
                        log.info("merge after " + existingOrgOptional.get().getOrganisationName() + " invite count: " + existingOrgOptional.get().getInvites().size());
                    }
                }

            });
        }
        log.info(String.format("after merge; %s", this.getOrganisations().size()));

    }
}

