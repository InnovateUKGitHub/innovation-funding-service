package com.worth.ifs.application;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.list.LazyList;

import java.io.Serializable;
import java.util.*;

public class ContributorsForm implements Serializable {

    private Map<String, List<InviteeForm>> organisationMap;

    public ContributorsForm(LinkedHashMap<String, List<InviteeForm>> organisationMap) {
        this.organisationMap = organisationMap;
    }

    public ContributorsForm() {
        this.organisationMap = new LinkedHashMap<>();

        this.organisationMap = MapUtils.lazyMap(new LinkedHashMap<Long, List<InviteeForm>>(), new Factory() {

            public Object create() {
                return LazyList.lazyList(new ArrayList<InviteeForm>(), FactoryUtils.instantiateFactory(InviteeForm.class));
            }

        });
    }

    public Map<String, List<InviteeForm>> getOrganisationMap() {
        return organisationMap;
    }

    public List<InviteeForm> getOrganisation(String organisationId) {
        return organisationMap.getOrDefault(organisationId, new ArrayList<InviteeForm>());
    }

    public void addOrganisation(String organisation, List<InviteeForm> users){
        organisationMap.put(organisation, users);
    }

    public void setOrganisationMap(Map<String, List<InviteeForm>> organisationMap) {
        this.organisationMap = organisationMap;
    }
}
