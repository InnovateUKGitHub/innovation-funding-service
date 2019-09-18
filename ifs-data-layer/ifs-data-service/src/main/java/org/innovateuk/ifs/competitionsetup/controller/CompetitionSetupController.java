package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling competitions that are in setup
 */
@RestController
@RequestMapping("/competition/setup")
public class CompetitionSetupController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Value("${ifs.data.service.file.storage.competition.terms.max.filesize.bytes}")
    private Long maxFilesizeBytesForCompetitionTerms;

    @Value("${ifs.data.service.file.storage.competition.terms.valid.media.types}")
    private List<String> validMediaTypesForCompetitionTerms;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PutMapping("/{id}")
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource,
                                                           @PathVariable("id") final Long id) {
        return competitionSetupService.save(id, competitionResource).toGetResponse();
    }

    @PutMapping("/{id}/update-competition-initial-details")
    public RestResult<Void> updateCompetitionInitialDetails(@Valid @RequestBody CompetitionResource competitionResource,
                                                            @PathVariable("id") final long id) {
        CompetitionResource existingCompetitionResource = competitionService.getCompetitionById(id).getSuccess();
        return competitionSetupService.updateCompetitionInitialDetails(id, competitionResource,
                existingCompetitionResource.getLeadTechnologist()).toPutResponse();
    }

    @PostMapping("/{id}/initialise-form/{competitionTypeId}")
    public RestResult<Void> initialiseForm(@PathVariable("id") Long competitionId,
                                           @PathVariable("competitionTypeId") Long competitionType) {
        return competitionSetupService.copyFromCompetitionTypeTemplate(competitionId, competitionType).toPostResponse();
    }

    @PostMapping("/generate-competition-code/{id}")
    public RestResult<String> generateCompetitionCode(@RequestBody ZonedDateTime dateTime,
                                                      @PathVariable("id") final Long id) {
        return competitionSetupService.generateCompetitionCode(id, dateTime).toGetResponse();
    }

    @PutMapping("/section-status/complete/{competitionId}/{section}")
    public RestResult<Void> markSectionComplete(@PathVariable("competitionId") final Long competitionId,
                                                @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionComplete(competitionId, section).toPutResponse();
    }

    @PutMapping("/section-status/incomplete/{competitionId}/{section}")
    public RestResult<Void> markSectionIncomplete(@PathVariable("competitionId") final Long competitionId,
                                                  @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionIncomplete(competitionId, section).toPutResponse();
    }

    @PutMapping("/subsection-status/complete/{competitionId}/{parentSection}/{subsection}")
    public RestResult<Void> markSubsectionComplete(@PathVariable("competitionId") final Long competitionId,
                                                   @PathVariable("parentSection") final CompetitionSetupSection parentSection,
                                                   @PathVariable("subsection") final CompetitionSetupSubsection subsection) {
        return competitionSetupService.markSubsectionComplete(competitionId, parentSection, subsection).toPutResponse();
    }

    @PutMapping("/subsection-status/incomplete/{competitionId}/{parentSection}/{subsection}")
    public RestResult<Void> markSubsectionIncomplete(@PathVariable("competitionId") final Long competitionId,
                                                     @PathVariable("parentSection") final CompetitionSetupSection parentSection,
                                                     @PathVariable("subsection") final CompetitionSetupSubsection subsection) {
        return competitionSetupService.markSubsectionIncomplete(competitionId, parentSection, subsection).toPutResponse();
    }

    @GetMapping("/section-status/{competitionId}")
    public RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(@PathVariable("competitionId") final Long competitionId) {
        return competitionSetupService.getSectionStatuses(competitionId).toGetResponse();
    }

    @GetMapping("/subsection-status/{competitionId}")
    public RestResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(@PathVariable("competitionId") final Long competitionId) {
        return competitionSetupService.getSubsectionStatuses(competitionId).toGetResponse();
    }

    @PostMapping("/{id}/mark-as-setup")
    public RestResult<Void> markAsSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.markAsSetup(competitionId).toPostResponse();
    }

    @PostMapping("/{id}/return-to-setup")
    public RestResult<Void> returnToSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.returnToSetup(competitionId).toPostResponse();
    }

    @PostMapping
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }

    @PostMapping("/non-ifs")
    public RestResult<CompetitionResource> createNonIfs() {
        return competitionSetupService.createNonIfs().toPostCreateResponse();
    }

    @DeleteMapping("{id}")
    public RestResult<Void> deleteIfExists(@PathVariable("id") long competitionId) {
        return competitionSetupService.deleteCompetition(competitionId).toDeleteResponse();
    }

    @PostMapping(value = "/competition-terms", produces = "application/json")
    public RestResult<FileEntryResource> uploadCompetitionTerms(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam long competitionId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator,
                validMediaTypesForCompetitionTerms, maxFilesizeBytesForCompetitionTerms, request,
                     (fileAttributes, inputStreamSupplier) ->
                            competitionSetupService.uploadCompetitionTerms(contentType, contentLength, originalFilename, competitionId, request));
    }

    @DeleteMapping(value = "/competition-terms", produces = "application/json")
    public RestResult<Void> deleteCompetitionTerms(@RequestParam(value = "competitionId") long competitionId) {
        return competitionSetupService.deleteCompetitionTerms(competitionId).toDeleteResponse();
    }
}