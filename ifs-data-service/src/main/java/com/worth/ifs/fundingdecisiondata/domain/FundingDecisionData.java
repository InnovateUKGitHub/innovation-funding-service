package com.worth.ifs.fundingdecisiondata.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;

@Entity
public class FundingDecisionData {
	@Id
    private Long id;

	@ElementCollection
	@JoinTable(name="FUNDING_DECISION_DATA_APPLICATION_DECISION", joinColumns=@JoinColumn(name="ID"))
	@MapKeyColumn (name="FUNDING_DECISION_DATA_ID")
	@Column(name="VALUE")
    @Enumerated(EnumType.STRING)
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
