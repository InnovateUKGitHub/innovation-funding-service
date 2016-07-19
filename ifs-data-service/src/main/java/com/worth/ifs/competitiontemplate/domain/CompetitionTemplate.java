package com.worth.ifs.competitiontemplate.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.worth.ifs.competition.domain.CompetitionType;

/**
 * Template for creating new competitions.
 */
@Entity
public class CompetitionTemplate {

    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="competitionTypeId", referencedColumnName="id")
    private CompetitionType competitionType;
    
    @OneToMany(mappedBy="competitionTemplate")
    @OrderBy("priority ASC")
    private List<SectionTemplate> sectionTemplates = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CompetitionType getCompetitionType() {
		return competitionType;
	}

	public void setCompetitionType(CompetitionType competitionType) {
		this.competitionType = competitionType;
	}

	public List<SectionTemplate> getSectionTemplates() {
		return sectionTemplates;
	}

	public void setSectionTemplates(List<SectionTemplate> sectionTemplates) {
		this.sectionTemplates = sectionTemplates;
	}
    
}
