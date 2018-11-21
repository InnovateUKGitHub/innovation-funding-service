package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.innovateuk.ifs.sil.grant.resource.json.LocalDateDeserializer;
import org.innovateuk.ifs.sil.grant.resource.json.LocalDateSerializer;
import org.innovateuk.ifs.sil.grant.resource.json.ZonedDateTimeDeserializer;
import org.innovateuk.ifs.sil.grant.resource.json.ZonedDateTimeSerializer;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

public class Grant {
    private long id;
    private long competitionCode;
    private String title;
    private String summary;
    private String publicDescription;

    @JsonProperty("golDate")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime grantOfferLetterDate;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;
    private long duration;
    private Set<Participant> participants;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(long competitionCode) {
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

    public ZonedDateTime getGrantOfferLetterDate() {
        return grantOfferLetterDate;
    }

    public void setGrantOfferLetterDate(ZonedDateTime grantOfferLetterDate) {
        this.grantOfferLetterDate = grantOfferLetterDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Get duration in months.
     *
     * @return months
     */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }
}
