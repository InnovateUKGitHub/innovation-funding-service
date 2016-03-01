package com.worth.ifs.application.form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class OrganisationInviteFormList extends LinkedList<OrganisationInviteForm> {
    private static final Log LOG = LogFactory.getLog(OrganisationInviteFormList.class);
    public OrganisationInviteFormList() {
        super();
    }

    public OrganisationInviteFormList(Collection<OrganisationInviteForm> c) {
        super(c);
    }

    public void merge(OrganisationInviteFormList toMerge){
        toMerge.forEach(oC -> this.mergeOrganisation(oC));
    }

    private void mergeOrganisation(OrganisationInviteForm oC) {
        Optional<OrganisationInviteForm> existingOrgOptional = this.stream()
                .filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName()))
                .findAny();

        if(!existingOrgOptional.isPresent()){
            this.add(oC);
        }else{
            OrganisationInviteForm existingOrg = existingOrgOptional.get();
            List<InviteeForm> existingInvites = existingOrg.getInvites();

            oC.getInvites().forEach(iC -> mergeInvite(iC, existingInvites));

            existingOrgOptional = this.stream()
                    .filter(o -> StringUtils.isNotEmpty(o.getOrganisationName()) && o.getOrganisationName().equals(oC.getOrganisationName()))
                    .findAny();
            if(existingOrgOptional.isPresent()){
                LOG.debug("merge after " + existingOrgOptional.get().getOrganisationName() + " invite count: " + existingOrgOptional.get().getInvites().size());
            }
        }
    }

    private void mergeInvite(InviteeForm iC, List<InviteeForm> existingInvites) {
        Optional<InviteeForm> cookieInviteFound = existingInvites.stream()
                .filter(i -> StringUtils.isNotEmpty(iC.getEmail()) && i.getEmail().equals(iC.getEmail()) && StringUtils.isNotEmpty(iC.getPersonName()) && i.getPersonName().equals(iC.getPersonName()))
                .findAny();
        if(!cookieInviteFound.isPresent()){
            existingInvites.add(iC);
        }
    }
}
