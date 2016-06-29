
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
    private static final Log LOG = LogFactory.getLog(ContributorsForm.class);
    private static final long serialVersionUID = -2887644741634451825L;

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

        if(!contributorsFormCookie.getOrganisations().isEmpty()){
            contributorsFormCookie.getOrganisations().forEach(oC -> mergeOrganisation(oC));
        }
    }

    private void mergeOrganisation(OrganisationInviteForm oC) {
        Optional<OrganisationInviteForm> existingOrgOptional = this.getOrganisations().stream()
                .filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName()))
                .findAny();

        if(!existingOrgOptional.isPresent()){
            this.getOrganisations().add(oC);
        }else{
            OrganisationInviteForm existingOrg = existingOrgOptional.get();
            List<InviteeForm> existingInvites = existingOrg.getInvites();

            oC.getInvites().forEach(iC -> mergeInvite(iC, existingInvites));

            existingOrgOptional = this.getOrganisations().stream()
                    .filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName()))
                    .findAny();
            if(existingOrgOptional.isPresent()){
                LOG.debug("merge after " + existingOrgOptional.get().getOrganisationName() + " invite count: " + existingOrgOptional.get().getInvites().size());
            }
        }
    }

    private void mergeInvite(InviteeForm iC, List<InviteeForm> existingInvites) {
        Optional<InviteeForm> cookieInviteFound = existingInvites.stream()
                .filter(i -> i.getEmail().equals(iC.getEmail()) && i.getPersonName().equals(iC.getPersonName()))
                .findAny();
        if(!cookieInviteFound.isPresent()){
            existingInvites.add(iC);
        }
    }
}

