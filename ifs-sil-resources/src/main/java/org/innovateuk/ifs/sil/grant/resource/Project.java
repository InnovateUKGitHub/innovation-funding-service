package org.innovateuk.ifs.sil.grant.resource;

import java.util.Date;
import java.util.Set;

public class Project {
    private String id;
    private String competitionCode;
    private String title;
    private String summary;
    private String publicDescription;
    private Date grantOfferLetterDate;
    private Date startDate;
    private int duration;
    private Set<Participant> participants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public Date getGrantOfferLetterDate() {
        return grantOfferLetterDate;
    }

    public void setGrantOfferLetterDate(Date grantOfferLetterDate) {
        this.grantOfferLetterDate = grantOfferLetterDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Get duration in months.
     *
     * @return months
     */
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Participant> getParticipant() {
        return participants;
    }

    public void setParticipant(Set<Participant> participants) {
        this.participants = participants;
    }
}
