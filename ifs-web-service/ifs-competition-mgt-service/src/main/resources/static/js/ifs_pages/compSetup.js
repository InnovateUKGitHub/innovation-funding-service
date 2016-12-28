IFS.competitionManagement.setup = (function () {
  'use strict'
  var s
  return {
    settings: {
      milestonesForm: '[data-section="milestones"]'
    },
    init: function () {
      s = this.settings
      IFS.competitionManagement.setup.handleCompetitionCode()
      IFS.competitionManagement.setup.handleAddRow()
      IFS.competitionManagement.setup.handleRemoveRow()

      jQuery('body.competition-management.competition-setup').on('change', '#competitionTypeId', function () {
        IFS.competitionManagement.setup.handleStateAid()
      })
      jQuery('body.competition-management.competition-setup').on('change', '[name="innovationSectorCategoryId"]', function () {
        IFS.competitionManagement.setup.handleInnovationSector(false)
      })
      IFS.competitionManagement.setup.innovationSectorOnPageLoad()

      jQuery(s.milestonesForm).on('change', 'input[data-date]', function () {
        IFS.competitionManagement.setup.milestonesExtraValidation()
        IFS.competitionManagement.setup.milestonesSetFutureDate(jQuery(this))
      })
      IFS.competitionManagement.setup.mileStoneValidateOnPageLoad()
    },
    handleCompetitionCode: function () {
      jQuery(document).on('click', '#generate-code', function () {
        var button = jQuery(this)
        var competitionId = button.val()
        var field = button.closest('.form-group').find('input')
        var url = window.location.protocol + '//' + window.location.host + '/management/competition/setup/' + competitionId + '/generateCompetitionCode'
        // todo ajax failure
        jQuery.ajaxProtected({
          type: 'GET',
          url: url,
          success: function (data) {
            if (typeof (data) !== 'undefined') {
              if (data.success === 'true') {
                IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'required'))
                field.val(data.message)
                jQuery('body').trigger('updateSerializedFormState')
              } else {
                IFS.core.formValidation.setInvalid(field, data.message)
              }
            }
          }
        })
        return false
      })
    },
    handleInnovationSector: function (pageLoad) {
      var sector = jQuery('[name="innovationSectorCategoryId"]').val()
      var innovationCategorySelected = jQuery('[name="innovationAreaCategoryId"]').val()
      if (typeof (sector) !== 'undefined') {
        var url = window.location.protocol + '//' + window.location.host + '/management/competition/setup/getInnovationArea/' + sector
        jQuery.ajaxProtected({
          type: 'GET',
          url: url,
          success: function (data) {
            var innovationCategory = jQuery('[name="innovationAreaCategoryId"]')
            innovationCategory.children().remove()
            jQuery.each(data, function () {
              if (this.id === innovationCategorySelected) {
                innovationCategory.append('<option selected="selected" value="' + this.id + '">' + this.name + '</option>')
              } else {
                innovationCategory.append('<option value="' + this.id + '">' + this.name + '</option>')
              }
            })
            if (!pageLoad) {
              jQuery(innovationCategory).trigger('ifsValidate')
              IFS.core.autoSave.fieldChanged('[name="innovationSectorCategoryId"]')
              IFS.core.autoSave.fieldChanged('[name="innovationAreaCategoryId"]')
            }
          }
        })
      }
    },
    innovationSectorOnPageLoad: function () {
      var sectorInput = jQuery('[name="innovationSectorCategoryId"]')
      var sector = sectorInput.val()
      if (sectorInput.length) {
        if (!sector) {
          var innovationCategory = jQuery('[name="innovationAreaCategoryId"]')
          innovationCategory.children().remove()
          innovationCategory.append('<option value="innovation sector" disabled="disabled" selected="selected">Please select an innovation sector first &hellip;</option>')
        } else {
          IFS.competitionManagement.setup.handleInnovationSector(true)
        }
      }
    },
    handleStateAid: function () {
      var stateAid = jQuery('#competitionTypeId').find('[value="' + jQuery('#competitionTypeId').val() + '"]').attr('data-stateaid')
      if (stateAid === 'true') {
        stateAid = 'yes'
      } else {
        stateAid = 'no'
      }
      jQuery('#stateAid').attr('aria-hidden', 'false').find('p').html('<span class="' + stateAid + '">' + stateAid + '</span>')
    },
    // Add row
    handleAddRow: function () {
      jQuery(document).on('click', '[data-add-row]', function () {
        var type = jQuery(this).attr('data-add-row')
        switch (type) {
          case 'cofunder':
            IFS.competitionManagement.setup.addCoFunder()
            break
          case 'guidance':
            IFS.competitionManagement.setup.addGuidanceRow()
            break
        }
        jQuery('body').trigger('updateSerializedFormState')
        return false
      })
    },
    addCoFunder: function () {
      var count = 0
      var idCount = 0

      if (jQuery('.funder-row').length) {
        count = parseInt(jQuery('.funder-row').length, 10) // name attribute has to be 0,1,2,3
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(jQuery('.funder-row[id^=funder-row-]').last().attr('id').split('funder-row-')[1], 10) + 1
      }
      var html = '<div class="grid-row funder-row" id="funder-row-' + idCount + '">' +
                    '<div class="column-half">' +
                      '<div class="form-group">' +
                        '<input type="text" maxlength="255" data-maxlength-errormessage="Funders has a maximum length of 255 characters" class="form-control width-x-large" id="' + idCount + '-funder" name="funders[' + count + '].funder" value="">' +
                      '</div>' +
                    '</div>' +
                    '<div class="column-half">' +
                      '<div class="form-group">' +
                        '<input type="number" min="0" class="form-control width-x-large" id="' + idCount + '-funderBudget" name="funders[' + count + '].funderBudget" value=""><input required="required" type="hidden" id="' + idCount + '-coFunder" name="funders[' + count + '].coFunder" value="true">' +
                        '<button class="buttonlink" name="remove-funder" value="' + count + '" data-remove-row="cofunder">Remove</button>' +
                      '</div>' +
                    '</div>' +
                  '</div>'
      jQuery('.funder-row').last().after(html)
    },
    addGuidanceRow: function () {
      var table = jQuery('#guidance-table')
      var isAssessed = table.hasClass('assessed-guidance')
      var count = 0
      var idCount = 0

      if (table.find('tbody tr').length) {
        count = parseInt(table.find('tbody tr').length, 10) // name attribute has to be 0,1,2,3
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(jQuery('tr[id^=guidance-]').last().attr('id').split('guidance-')[1], 10) + 1
      }
      var html = '<tr id="guidance-' + idCount + '">'

      if (isAssessed) {
        html += '<td class="form-group">' +
                '<label class="form-label" for="guidancerow-' + idCount + '-scorefrom"><span class="visuallyhidden">Score from</span></label>' +
                '<input required="required" type="number" min="0" class="form-control width-small" data-required-errormessage="Please enter a from score." data-min-errormessage="Please enter a valid number." id="guidancerow-' + idCount + '-scorefrom" name="guidanceRows[' + count + '].scoreFrom" value="">' +
              '</td>' +
              '<td class="form-group">' +
                '<label class="form-label" for="guidancerow-' + idCount + '-scoreto"><span class="visuallyhidden">Score to</span></label>' +
                '<input required="required" type="number" min="0" class="form-control width-small" value="" data-required-errormessage="Please enter a to score." data-min-errormessage="Please enter a valid number." id="guidancerow-' + idCount + '-scoreto" name="guidanceRows[' + count + '].scoreTo" value="">' +
              '</td>'
      } else {
        html += '<td class="form-group">' +
                '<label class="form-label" for="guidancerow-' + idCount + '-subject"><span class="visuallyhidden">Subject</span></label>' +
                '<input required="required" class="form-control width-small" data-maxlength-errormessage="Subject has a maximum length of 255 characters." data-required-errormessage="Please enter a subject." id="guidancerow-' + idCount + '-subject" name="question.guidanceRows[' + count + '].subject" value="">' +
              '</td>'
      }
      html += '<td class="form-group">' +
              '<label class="form-label" for="guidancerow-' + idCount + '-justification"><span class="visuallyhidden">Justification</span></label>' +
              '<textarea required="required" rows="3" class="form-control width-full" data-maxlength-errormessage="Justification has a maximum length of 255 characters." data-required-errormessage="Please enter a justification." id="guidancerow-' + count + '-justification" name="' + (isAssessed ? '' : 'question.') + 'guidanceRows[' + count + '].justification"></textarea>' +
            '</td>' +
            '<td><button class="buttonlink alignright remove-guidance-row" name="remove-guidance-row" data-remove-row="guidance" value="' + count + '">Remove</button></td>'
      html += '</tr>'
      table.find('tbody').append(html)
    },
    // remove row
    handleRemoveRow: function () {
      jQuery(document).on('click', '[data-remove-row]', function () {
        var inst = jQuery(this)
        var type = inst.attr('data-remove-row')
        switch (type) {
          case 'cofunder':
            jQuery('[name="removeFunder"]').val(inst.val())
            IFS.core.autoSave.fieldChanged('[name="removeFunder"]')

            inst.closest('.funder-row').remove()
            IFS.competitionManagement.setup.reindexRows('.funder-row')
            jQuery('body').trigger('recalculateAllFinances')
            break
          case 'guidance':
            jQuery('[name="removeGuidanceRow"]').val(inst.val())
            IFS.core.autoSave.fieldChanged('[name="removeGuidanceRow"]')
            inst.closest('tr').remove()
            IFS.competitionManagement.setup.reindexRows('tr[id^="guidance-"]')
            break
        }
      })
    },
    reindexRows: function (rowSelector) {
      jQuery(rowSelector + ' [name]').each(function () {
        var inst = jQuery(this)
        var thisIndex = inst.closest(rowSelector).index(rowSelector)

        var oldAttr = inst.attr('name')
        var oldAttrName = oldAttr.split('[')[0]
        var oldAttrElement = oldAttr.split(']')[1]
        inst.attr('name', oldAttrName + '[' + thisIndex + ']' + oldAttrElement)
      })

      jQuery(rowSelector + ' [data-remove-row]').each(function () {
        var inst = jQuery(this)
        var thisIndex = inst.closest(rowSelector).index(rowSelector)
        inst.val(thisIndex)
      })
    },
    milestonesExtraValidation: function () {
      // some extra javascript to hide the server side messages when the field is valid
      var fieldErrors = jQuery(s.milestonesForm + ' .field-error')
      var emptyInputs = jQuery(s.milestonesForm + ' input').filter(function () { return !this.value })
      if (fieldErrors.length === 0 && emptyInputs.length === 0) {
        jQuery(s.milestonesForm + ' .error-summary').attr('aria-hidden', 'true')
      }
    },
    mileStoneValidateOnPageLoad: function () {
      jQuery(s.milestonesForm + ' .day input').each(function (index, value) {
        var field = jQuery(value)
        if (index === 0) {
          IFS.core.formValidation.checkDate(field, true)
        }
        IFS.competitionManagement.setup.milestonesSetFutureDate(field)
      })
    },
    milestonesSetFutureDate: function (field) {
      setTimeout(function () {
        var nextRow = field.closest('tr').next('tr')
        var date = field.attr('data-date')
        if (nextRow.length) {
          nextRow.attr({'data-future-date': date})
          if (jQuery.trim(date.length) !== 0) {
            var input = nextRow.find('.day input')
            IFS.core.formValidation.checkDate(input, true)
          }
        }
      }, 0)
    }
  }
})()
