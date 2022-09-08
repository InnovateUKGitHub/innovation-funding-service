IFS.competitionManagement.initialDetails = (function () {
  'use strict'
  return {
    init: function () {
      IFS.competitionManagement.initialDetails.handleInnovationSector(true)
      jQuery('body.competition-management.competition-setup').on('change', '[name="innovationSectorCategoryId"]', function () {
        IFS.competitionManagement.initialDetails.handleInnovationSector(false)
      })
      IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
      IFS.competitionManagement.initialDetails.handleInnovationArea()
      IFS.competitionManagement.initialDetails.rebindInnovationAreas()
    },
    rebindInnovationAreas: function () {
      jQuery('.competition-management.competition-setup [name^="innovationAreaCategoryIds"]').unbind('change')
      jQuery('.competition-management.competition-setup').on('change', '[name^="innovationAreaCategoryIds"]', function () {
        IFS.competitionManagement.initialDetails.handleInnovationArea()
        IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
      })
    },
    handleInnovationArea: function () {
      var multipleRowsButton = jQuery('[data-add-row="innovationArea"]')
      var isShowingAll = IFS.competitionManagement.initialDetails.hasAllInnovationArea()

      multipleRowsButton.attr('aria-hidden', isShowingAll)
      if (isShowingAll) {
        jQuery('[id*="innovation-row"]').not('#innovation-row-0').remove()
        IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
        jQuery('#innovation-row-0 select').val('-1')
      }
    },
    hasAllInnovationArea: function () {
      var hasAllInnovationArea = false
      jQuery('[name^="innovationAreaCategoryIds"]').each(function (index, el) {
        if (jQuery(el).val() === '-1') {
          hasAllInnovationArea = true
          return false
        }
      })

      return hasAllInnovationArea
    },
    handleInnovationSector: function (pageLoad) {
      var sector = jQuery('[name="innovationSectorCategoryId"]').val()
      if (typeof (sector) === 'undefined' || sector === null) {
        var innovationCategory = jQuery('[name*="innovationAreaCategoryId"]')
        innovationCategory.html('<option value="" disabled="disabled" selected="selected">Please select an innovation sector first &hellip;</option>')
      } else {
        var url = window.location.protocol + '//' + window.location.host + '/management/competition/setup/get-innovation-areas/' + sector
        jQuery.ajaxProtected({
          type: 'GET',
          url: url
        }).done(function (areas) {
          if (pageLoad) {
            IFS.competitionManagement.initialDetails.filterInnovationAreasPageLoad(areas)
          } else {
            IFS.competitionManagement.initialDetails.fillInnovationAreas(areas)
            jQuery(innovationCategory).trigger('ifsValidate')
          }
        })
      }
    },
    disableAlreadySelectedOptions: function () {
      var disabledSections = {}
      jQuery('[name*="innovationAreaCategoryId"]').each(function () {
        var inst = jQuery(this)
        var value = inst.val()
        var name = inst.prop('name')
        if (value !== null) {
          disabledSections[name] = value
        }
      })
      jQuery('[name*="innovationAreaCategoryId"]').find('[disabled]:not([value=""])').removeAttr('disabled').removeAttr('aria-hidden')
      for (var section in disabledSections) {
        jQuery('[name*="innovationAreaCategoryId"]:not([name="' + section + '"]) option[value="' + disabledSections[section] + '"]').attr('disabled', 'disabled').attr('aria-hidden', 'true')
      }
    },
    fillInnovationAreas: function (currentAreas) {
      var innovationAreasFields = jQuery('[name*="innovationAreaCategoryId"]')
      jQuery.each(innovationAreasFields, function () {
        var innovationAreasField = jQuery(this)
        innovationAreasField.children().remove()
        innovationAreasField.append('<option value="" disabled="disabled" selected="selected">Please select &hellip;</option>')
        jQuery.each(currentAreas, function () {
          innovationAreasField.append('<option value="' + this.id + '">' + this.name + '</option>')
        })
      })
    },
    filterInnovationAreasPageLoad: function (currentAreas) {
      currentAreas = jQuery.map(currentAreas, function (area) {
        return '[value="' + area.id + '"]'
      })
      currentAreas.push('[value=""]')
      currentAreas = currentAreas.join(',')
      var innovationAreas = jQuery('[name*="innovationAreaCategoryId"] option')
      innovationAreas.not(currentAreas).remove()
    }
  }
})()
