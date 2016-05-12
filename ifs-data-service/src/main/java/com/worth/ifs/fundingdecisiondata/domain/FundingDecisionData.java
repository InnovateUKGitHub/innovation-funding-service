package com.worth.ifs.fundingdecisiondata.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

@Entity
public class FundingDecisionData {
	@Id
    private Long id;

	@ElementCollection
    @MapKeyColumn(name="application_id")
    @Column(name="funding_decision")
	@Enumerated(EnumType.STRING)
    @CollectionTable(name="funding_decision_data_application_decision", joinColumns=@JoinColumn(name="funding_decision_id"))
    private Map<Long, FundingDecisionStatus> fundingDecisions = new HashMap<>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Map<Long, FundingDecisionStatus> getFundingDecisions() {
		return fundingDecisions;
	}
	public void setFundingDecisions(Map<Long, FundingDecisionStatus> fundingDecisions) {
		this.fundingDecisions = fundingDecisions;
	}
}
