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
          IFS.competitionManagement.repeater.addContentGroup()
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
          IFS.core.autoSave.fieldChanged('[name="removeFunder"]')

          inst.closest('.funder-row').remove()
          IFS.competitionManagement.repeater.reindexRows('.funder-row')
          jQuery('body').trigger('recalculateAllFinances')
          break
        case 'guidance':
          jQuery('[name="removeGuidanceRow"]').val(inst.val())
          IFS.core.autoSave.fieldChanged('[name="removeGuidanceRow"]')
          inst.closest('tr').remove()
          IFS.competitionManagement.repeater.reindexRows('tr[id^="guidance-"]')
          break
        case 'innovationArea':
          inst.closest('[id^="innovation-row"]').remove()
          IFS.competitionManagement.repeater.reindexRows('.form-group[id^="innovation-row"]')
          IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
          IFS.competitionManagement.initialDetails.autosaveInnovationAreaIds()
          break
      }
    },
    addInnovationAreaRow: function () {
      var rows = jQuery('.form-group[id^="innovation-row"]')

      var count = rows.length
      var idCount = parseInt(rows.last().prop('id').split('innovation-row-')[1], 10) + 1

      var newRow = rows.first().clone()
      // clear error messages
      newRow.removeClass('error')
      newRow.find('.error-message').remove()
      // new row attribute
      newRow.prop('id', 'innovation-row-' + idCount)
      // fix label link
      newRow.find('[id^="innovationAreaCategoryId"]').prop('id', 'innovationAreaCategoryId-' + idCount)
      newRow.find('[for^="innovationAreaCategoryId"]').prop('for', 'innovationAreaCategoryId-' + idCount)

      // set the first please select as selected
      newRow.find('[selected]').removeAttr('selected')
      newRow.find('[disabled]').first().attr('selected', '')

      // hide new row label for styling
      newRow.find('.form-label').children().addClass('visuallyhidden')
      // change name attributes and empty values
      newRow.find('[name]').prop('name', 'innovationAreaCategoryIds[' + count + ']').val('')
      // add remove button
      newRow.append('<button data-remove-row="innovationArea" value="' + count + '" class="buttonlink" type="button">Remove</button>')

      rows.last().after(newRow)
      IFS.competitionManagement.initialDetails.disableAlreadySelectedOptions()
    },
    addContentGroup: function () {
      var count = parseInt(jQuery('[id^=contentGroup-row-]').length, 10) // name attribute has to be 0,1,2,3
      // id and for attributes have to be unique, gaps in count don't matter however I rather don't reindex all attributes on every remove, so we just higher the highest.
      var idCount = parseInt(jQuery('[id^=contentGroup-row-]').last().attr('id').split('contentGroup-row-')[1], 10) + 1

      var html = '<div id="contentGroup-row-' + count + '">' +
                  '<div class="form-group">' +
                    '<label class="form-label-bold" for="heading-' + idCount + '">Heading</label>' +
                    '<input class="form-control" id="heading-' + idCount + '" type="text" name="contentGroups[' + count + '].heading" />' +
                  '</div>' +
                  '<div class="form-group"><div class="textarea-wrapped">' +
                    '<label class="form-label-bold" for="content-' + idCount + '">Content</label>' +
                        '<textarea id="content-' + idCount + '" cols="30" rows="10" class="width-full form-control" data-editor="html" name="contentGroups[' + count + '].content"></textarea>' +
                    '</div></div>' +
                  '<div class="form-group upload-section">' +
                      '<input type="file" id="file-upload-' + idCount + '" class="inputfile" name="contentGroups[' + count + '].attachment" />' +
                      '<label for="file-upload-' + idCount + '" class="button-secondary extra-margin">+ Upload</label>' +
                      '<button class="button-secondary" type="submit" name="uploadFile" data-for-file-upload="file-upload-' + idCount + '" value="' + count + '">Save</button>' +
                      '<p class="uploaded-file">No file currently uploaded</p>' +
                  '</div>' +
                  '<button type="button" class="buttonlink" data-remove-row="">Remove section</button>' +
                  '<hr /></div>'
      jQuery('[id^=contentGroup-row-]').last().after(html)

      var editor = IFS.core.editor.prepareEditorHTML('#content-' + idCount)
      // make a copy of the global wysiwyg-editor settings object and add the html link functionality
      var editorOptions = jQuery.extend(true, {}, IFS.core.editor.settings.editorOptions, {
        plugins: {'hallolink': {}}
      })
      jQuery(editor).hallo(editorOptions)
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
    }
  }
})()
