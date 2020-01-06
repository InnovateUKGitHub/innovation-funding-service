IFS.competitionManagement.repeater = (function () {
  'use strict'
  return {
    init: function () {
      jQuery(document).on('click', '[data-add-row]', function () {
        IFS.competitionManagement.repeater.handleAddRow(this)
      })
      jQuery(document).on('click', '[data-remove-row]', function () {
        IFS.competitionManagement.repeater.handleRemoveRow(this)
      })
      if (jQuery('.contentGroup').length) {
        IFS.core.upload.registerSuccessHandler(function (html, wrapper) {
          var contentGroup = wrapper.closest('.contentGroup')
          var index = jQuery('.contentGroup').index(contentGroup)
          var id = html.find('.contentGroup:eq(' + index + ') input:hidden[id*="id"]').val()
          contentGroup.find('input:hidden[id*="id"]').val(id)
        })
      }
    },
    // Add row
    handleAddRow: function (el) {
      var type = jQuery(el).attr('data-add-row')
      switch (type) {
        case 'cofunder':
          IFS.competitionManagement.repeater.addCoFunder()
          break
        case 'guidance':
          IFS.competitionManagement.repeater.addGuidanceRow()
          break
        case 'innovationArea':
          IFS.competitionManagement.repeater.addInnovationAreaRow()
          break
        case 'contentGroup':
          IFS.competitionManagement.repeater.addContentGroup(el)
          break
        case 'dateContentGroup':
          IFS.competitionManagement.repeater.addDateContentGroup(el)
          break
      }
      jQuery('body').trigger('updateSerializedFormState')
      return false
    },
    // remove row
    handleRemoveRow: function (el) {
      var inst = jQuery(el)
      var type = inst.attr('data-remove-row')
      switch (type) {
        case 'cofunder':
          jQuery('[name="removeFunder"]').val(inst.val())
          inst.closest('.funder-row').remove()
          IFS.competitionManagement.repeater.reindexRows('.funder-row')
          jQuery('body').trigger('recalculateAllFinances')
          break
        case 'guidance':
          jQuery('[name="removeGuidanceRow"]').val(inst.val())
          inst.closest('tr').remove()
          IFS.competitionManagement.repeater.reindexRows('tr[id^="guidance-"]')
          break
        case 'innovationArea':
          inst.closest('[id^="innovation-row"]').remove()
          IFS.competitionManagement.repeater.reindexRows('.govuk-form-group[id^="innovation-row"]')
          IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
          IFS.competitionManagement.initialDetails.rebindInnovationAreas()
          break
        case 'contentGroup':
          inst.closest('[id^="contentGroup-row-"]').remove()
          IFS.competitionManagement.repeater.reindexRows('[id^="contentGroup-row-"]')
          break
        case 'dateContentGroup':
          inst.closest('[id^="contentDateGroup-row-"]').remove()
          IFS.competitionManagement.repeater.reindexRows('[id^="contentDateGroup-row-"]')
          break
      }
    },
    addInnovationAreaRow: function () {
      var rows = jQuery('.govuk-form-group[id^="innovation-row"]')

      var count = rows.length
      var idCount = parseInt(rows.last().prop('id').split('innovation-row-')[1], 10) + 1

      var newRow = rows.first().clone()
      // clear error messages
      newRow.removeClass('error')
      newRow.find('.govuk-error-message').remove()
      // new row attribute
      newRow.prop('id', 'innovation-row-' + idCount)
      // fix label link
      newRow.find('[id^="innovationAreaCategoryIds"]').prop('id', 'innovationAreaCategoryIds[' + idCount + ']')
      newRow.find('[for^="innovationAreaCategoryIds"]').prop('for', 'innovationAreaCategoryIds[' + idCount + ']')

      // set the first please select as selected
      newRow.find('[selected]').removeAttr('selected')
      newRow.find('[disabled]').first().attr('selected', '')

      // hide new row label for styling
      newRow.find('.govuk-label').children().addClass('govuk-visually-hidden')
      // change name attributes and empty values
      newRow.find('[name]').prop('name', 'innovationAreaCategoryIds[' + count + ']').val('')
      // add remove button
      newRow.append('<button data-remove-row="innovationArea" value="' + count + '" class="button-clear" type="button">Remove</button>')

      rows.last().after(newRow)

      IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
      IFS.competitionManagement.initialDetails.rebindInnovationAreas()
    },
    addContentGroup: function (buttonEl) {
      var rows = jQuery('[id^="contentGroup-row-"]')
      var count = 0
      var idCount = 0

      if (rows.length) {
        count = parseInt(rows.length, 10) // name attribute has to be 0,1,2,3
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(rows.last().prop('id').split('contentGroup-row-')[1], 10) + 1
      }
      var headerRequiredErrorMessage = 'Please enter a heading.'
      var contentRequiredErrorMessage = 'Please enter content.'
      var html = '<div class="contentGroup" id="contentGroup-row-' + idCount + '">' +
        '<input type="hidden" id="contentGroups' + idCount + '.id" name="contentGroups[' + count + '].id" value="" />' +
        '<div class="govuk-form-group">' +
        '<label class="govuk-label" for="contentGroups[' + idCount + '].heading">Heading</label>' +
        '<input class="govuk-input" id="contentGroups[' + idCount + '].heading" type="text" name="contentGroups[' + count + '].heading" data-required-errormessage="' + headerRequiredErrorMessage + '" required="required" />' +
        '</div>' +
        '<div class="govuk-form-group textarea-wrapped">' +
        '<label class="govuk-label" for="contentGroups[' + idCount + '].content">Content</label>' +
        '<textarea id="contentGroups[' + idCount + '].content" cols="30" rows="10" class="govuk-textarea" data-editor="html" name="contentGroups[' + count + '].content" data-required-errormessage="' + contentRequiredErrorMessage + '" required="required"></textarea>' +
        '</div>' +
        '<div class="govuk-form-group upload-section">' +
            '<div class="ajax-upload" data-js-number-of-files="1" data-js-upload-button-name="uploadFile" data-js-upload-file-input="contentGroups[' + count + '].attachment">' +
              '<p class="govuk-body no-file-uploaded">No file currently uploaded</p>' +
              '<input type="file" id="contentGroups-' + idCount + '.attachment" class="inputfile" name="contentGroups[' + count + '].attachment" />' +
              '<label for="contentGroups-' + idCount + '.attachment" class="button-secondary govuk-!-margin-top-6">Upload</label>' +
              '<button class="button-secondary" type="submit" name="uploadFile" data-for-file-upload="contentGroups-' + idCount + '.attachment" value="' + count + '">Save</button>' +
            '</div>' +
          '</div>' +
          '<button type="button" class="button-clear govuk-!-margin-0" data-remove-row="contentGroup">Remove section</button>' +
        '</div>'
      if (rows.length) {
        rows.last().after(html)
      } else {
        jQuery(buttonEl).parent().before(html)
      }
      IFS.competitionManagement.repeater.initEditor('[id="contentGroups[' + idCount + '].content"]')
    },
    addDateContentGroup: function (buttonEl) {
      var rows = jQuery('[id^="contentDateGroup-row-"]')
      var count = 0
      var idCount = 0

      if (rows.length) {
        count = parseInt(rows.length, 10) // name attribute has to be 0,1,2,3
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(rows.last().prop('id').split('contentDateGroup-row-')[1], 10) + 1
      }

      var html = '<div class="contentGroup" id="contentDateGroup-row-' + idCount + '">' +
                 '<div class="govuk-form-group"><fieldset class="govuk-fieldset">' +
                    '<legend class="govuk-fieldset__legend govuk-fieldset__legend--s"><span class="govuk-fieldset__heading">Date</span></legend>' +
                    '<div class="date-group">' +
                        '<input type="hidden" disabled name="dates[' + count + '].combined" />' +
                        '<div class="day">' +
                            '<label class="govuk-label" for="dates[' + idCount + '].day">Day</label>' +
                            '<input class="govuk-input govuk-input--width-3" placeholder="DD" id="dates[' + idCount + '].day" name="dates[' + count + '].day" required="required">' +
                        '</div>' +
                        '<div class="month">' +
                            '<label class="govuk-label" for="dates[' + idCount + '].month">Month</label>' +
                            '<input class="govuk-input govuk-input--width-3" placeholder="MM" id="dates[' + idCount + '].month" name="dates[' + count + '].month" required="required"/>' +
                        '</div>' +
                        '<div class="year">' +
                            '<label class="govuk-label" for="dates[' + idCount + '].year">Year</label>' +
                            '<input class="govuk-input govuk-input--width-3" placeholder="YYYY" id="dates[' + idCount + '].year" name="dates[' + count + '].year" required="required"/>' +
                        '</div>' +
                    '</div>' +
                '</fieldset></div>' +
                '<div class="govuk-form-group textarea-wrapped">' +
                    '<label class="govuk-label" for="dates[' + idCount + '].content">' +
                        'Content' +
                    '</label>' +
                    '<textarea cols="30" rows="5" id="dates[' + idCount + '].content" name="dates[' + count + '].content" data-editor="html" class="govuk-textarea" required="required" th:attr="data-required-errormessage=#{validation.publiccontent.datesform.content.required}"></textarea>' +
                '</div>' +
                '<div class="govuk-form-group"><button class="button-clear" type="button" data-remove-row="dateContentGroup">Remove event</button></div>' +
            '</div>'
      if (rows.length) {
        rows.last().after(html)
      } else {
        jQuery(buttonEl).parent().before(html)
      }
      IFS.competitionManagement.repeater.initEditor('[id="dates[' + idCount + '].content"]')
    },
    addCoFunder: function () {
      var count = 0
      var idCount = 0
      if (jQuery('.funder-row').length) {
        count = parseInt(jQuery('.funder-row').length, 10) // name attribute has to be 0,1,2,3
        // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
        idCount = parseInt(jQuery('.funder-row[id^=funder-row-]').last().attr('id').split('funder-row-')[1], 10) + 1
      }
      var options = jQuery('#funders\\[0\\]\\.funder-select').clone().end().html()
      var html = '<div class="govuk-grid-row funder-row" id="funder-row-' + idCount + '">' +
                    '<div class="govuk-grid-column-one-half">' +
                      '<div class="govuk-form-group">' +
                        '<label class="govuk-label govuk-visually-hidden" for="funders[' + idCount + '].funder">Select funder name</label>' +
                        '<select class="govuk-select funders-' + idCount + '" id="funders[' + idCount + '].funder" name="funders[' + count + '].funder" data-auto-complete="" data-required-errormessage="Please select a funder name.">' +
                          options +
                        '</select>' +
                      '</div>' +
                    '</div>' +
                    '<div class="govuk-grid-column-one-half">' +
                      '<div class="govuk-form-group">' +
                        '<label class="govuk-label govuk-visually-hidden" for="' + idCount + '-funderBudget">Budget</label>' +
                        '<input type="number" min="0" class="govuk-input govuk-input--width-30" id="' + idCount + '-funderBudget" name="funders[' + count + '].funderBudget" value=""><input required="required" type="hidden" id="' + idCount + '-coFunder" name="funders[' + count + '].coFunder" value="true">' +
                        '<button class="button-clear" name="remove-funder" value="' + count + '" data-remove-row="cofunder">Remove</button>' +
                      '</div>' +
                    '</div>' +
                  '</div>'
      jQuery('.funder-row').last().after(html)
      jQuery('.funders-' + idCount).val('')
      IFS.core.autoComplete.initAutoCompletePlugin(jQuery('.funders-' + idCount))
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
      var html = '<tr id="guidance-' + idCount + '" class="govuk-table__row form-group-row-validated">'
      if (isAssessed) {
        html += '<td class="govuk-table__cell govuk-form-group">' +
                '<label class="govuk-label" for="guidanceRows[' + idCount + '].scoreFrom"><span class="govuk-visually-hidden">Score from</span></label>' +
                '<input required="required" type="number" min="0" class="govuk-input govuk-input--width-4" data-required-errormessage="Please enter a from score." data-min-errormessage="Please enter a valid number." id="guidanceRows[' + idCount + '].scoreFrom" name="guidanceRows[' + count + '].scoreFrom" value="">' +
              '</td>' +
              '<td class="govuk-table__cell govuk-form-group">' +
                '<label class="govuk-label" for="guidanceRows[' + idCount + '].scoreTo"><span class="govuk-visually-hidden">Score to</span></label>' +
                '<input required="required" type="number" min="0" class="govuk-input govuk-input--width-4" value="" data-required-errormessage="Please enter a to score." data-min-errormessage="Please enter a valid number." id="guidanceRows[' + idCount + '].scoreTo" name="guidanceRows[' + count + '].scoreTo" value="">' +
              '</td>'
      } else {
        html += '<td class="govuk-table__cell govuk-form-group">' +
                '<label class="govuk-label" for="guidanceRows[' + idCount + '].subject"><span class="govuk-visually-hidden">Subject</span></label>' +
                '<input required="required" class="govuk-input govuk-input--width-4" data-maxlength-errormessage="Subject has a maximum length of 255 characters." data-required-errormessage="Please enter a subject." id="guidanceRows[' + idCount + '].subject" name="question.guidanceRows[' + count + '].subject" value="">' +
              '</td>'
      }
      html += '<td class="govuk-table__cell govuk-form-group">' +
              '<label class="govuk-label" for="guidanceRows[' + idCount + '].justification"><span class="govuk-visually-hidden">Justification</span></label>' +
              '<textarea required="required" rows="3" class="govuk-textarea" data-maxlength-errormessage="Justification has a maximum length of 255 characters." data-required-errormessage="Please enter a justification." id="guidanceRows[' + idCount + '].justification" name="' + (isAssessed ? '' : 'question.') + 'guidanceRows[' + count + '].justification"></textarea>' +
            '</td>' +
            '<td class="govuk-table__cell"><button class="button-clear alignright remove-guidance-row" name="remove-guidance-row" data-remove-row="guidance" value="' + count + '">Remove</button></td>'
      html += '</tr>'
      table.find('tbody').append(html)
    },
    reindexRows: function (rowSelector) {
      jQuery(rowSelector + ' [name]').each(function () {
        var inst = jQuery(this)
        if (inst.prop('name').indexOf('[') !== -1) {
          var thisIndex = inst.closest(rowSelector).index(rowSelector)
          var oldAttr = inst.attr('name')
          var oldAttrName = oldAttr.split('[')[0]
          var oldAttrElement = oldAttr.split(']')[1]
          inst.prop('name', oldAttrName + '[' + thisIndex + ']' + oldAttrElement)
        }
      })
      jQuery(rowSelector + ' [data-remove-row]').each(function () {
        var inst = jQuery(this)
        var thisIndex = inst.closest(rowSelector).index(rowSelector)
        inst.val(thisIndex)
      })
    },
    initEditor: function (el) {
      var editor = IFS.core.editor.prepareEditorHTML(el)
      // make a copy of the global wysiwyg-editor settings object and add the html link functionality
      var editorOptions = jQuery.extend(true, {}, IFS.core.editor.settings.editorOptions, {
        plugins: {'hallolink': {}}
      })
      jQuery(editor).hallo(editorOptions)
    }
  }
})()
