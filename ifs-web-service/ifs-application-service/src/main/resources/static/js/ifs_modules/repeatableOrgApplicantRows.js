// Set up the handlers for adding and removing additional rows for inviting assessors
IFS.application.repeatableOrgApplicantRows = (function () {
  'use strict'

  return {
    init: function () {
      // prevent 'enter' key adding new rows by changing button type
      jQuery('button[data-repeatable-rowcontainer]').attr('type', 'button')

      jQuery('body').on('click', '[data-repeatable-rowcontainer]', function (e) {
        e.preventDefault()
        IFS.application.repeatableOrgApplicantRows.addRow(this)
        IFS.application.repeatableOrgApplicantRows.showHideAddRowButton(false)
      })
      jQuery('body').on('click', '.remove-another-row', function (e) {
        e.preventDefault()
        IFS.application.repeatableOrgApplicantRows.removeRow(this)
        IFS.application.repeatableOrgApplicantRows.showHideAddRowButton(true)
      })
    },
    addRow: function (el) {
      var newRow
      var target = jQuery(el).attr('data-repeatable-rowcontainer')
      var uniqueRowId = jQuery(target).children('.repeatable-row').length || 0

      if (jQuery(el).data('applicant-table') === 'update-org') {
        newRow = jQuery('<tr class="repeatable-row">' +
          '<td class="form-group">' +
          '<label for="stagedInvite.name"><span class="visually-hidden">Applicant name</span></label>' +
          '<input class="form-control width-full" type="text" ' +
          'id="stagedInvite.name" ' +
          'name="stagedInvite.name" value="" ' +
          'data-required-errormessage="Please enter a name." required="required" />' +
          '</td>' +
          '<td class="form-group">' +
          '<label for="stagedInvite.email"><span class="visually-hidden">Applicant email</span></label>' +
          '<input class="form-control width-full" type="email" ' +
          'id="stagedInvite.email" ' +
          'name="stagedInvite.email" value="" ' +
          'data-required-errormessage="Please enter an email address." required="required" />' +
          '</td>' +
          '<td><button class="button" name="executeStagedInvite" value="true" type="submit">Invite</button></td>' +
          '<td class="alignright">' +
          '<button class="remove-another-row buttonlink" name="removeInvite" type="button" value="0">Remove</button>' +
          '</td>' +
          '</tr>')
      } else {
        newRow = jQuery('<tr class="repeatable-row">' +
          '<td class="form-group">' +
          '<label for="applicants[' + uniqueRowId + '].name"><span class="visually-hidden">Applicant name</span></label>' +
          '<input class="form-control width-full" type="text" ' +
          'id="applicants[' + uniqueRowId + '].name" ' +
          'name="applicants[' + uniqueRowId + '].name" value="" ' +
          'data-required-errormessage="Please enter a name." required="required" />' +
          '</td>' +
          '<td class="form-group">' +
          '<label for="applicants[' + uniqueRowId + '].email"><span class="visually-hidden">Applicant email</span></label>' +
          '<input class="form-control width-full" type="email" ' +
          'id="applicants[' + uniqueRowId + '].email" ' +
          'name="applicants[' + uniqueRowId + '].email" value="" ' +
          'data-required-errormessage="Please enter an email address." required="required" />' +
          '</td>' +
          '<td class="alignright">' +
          '<button class="remove-another-row buttonlink" name="removeInvite" type="button" value="0">Remove</button>' +
          '</td>' +
          '</tr>')
      }
      // insert the new row with the correct values and move focus to the first field to aid keyboard users
      jQuery(target).append(newRow)
      jQuery(newRow).find('input').first().focus()
    },
    removeRow: function (el) {
      var element = jQuery(el)
      var rowParent = element.closest('tbody')

      // remove  the errors in the errorsummary that were linked to the fields that are now being removed
      rowParent.find('input').each(function () {
        var id = jQuery(this).attr('id')
        var errors = jQuery('.error-summary-list [href="#' + id + '"]')
        if (errors.length) {
          errors.parent().remove()
        }
      })
      var hasSummaryErrors = jQuery('.error-summary-list li').length > 0
      if (!hasSummaryErrors) {
        jQuery('.error-summary').attr('aria-hidden', 'true')
      }

      // must remove row before getting row information to correctly count remaining rows
      element.closest('tr').remove()

      var rows = jQuery(rowParent).children('.repeatable-row')
      // re-number rows to ensure no empty/missing data is created server-side
      jQuery(rows).each(function (rowIndex) {
        var el = jQuery(this)
        var rowsLabels = el.find('label')
        var rowsInputs = el.find('input')

        jQuery(rowsLabels).each(function () {
          // regex will replace 1 or more numbers in the string with the new index value
          var rowLabel = jQuery(this)
          var newFor = rowLabel.attr('for').replace(/\d+/g, rowIndex)
          rowLabel.attr('for', newFor)
        })

        jQuery(rowsInputs).each(function () {
          // regex will replace 1 or more numbers in the string with the new index value
          var rowInput = jQuery(this)
          var newId = rowInput.attr('id').replace(/\d+/g, rowIndex)
          var newName = rowInput.attr('name').replace(/\d+/g, rowIndex)

          rowInput.attr({
            'id': newId,
            'name': newName
          })
        })
      })
    },
    removeRowButton: function () {
      var addRowButton = jQuery("[name='addStagedInvite']")
      addRowButton.remove()
    }
  }
})()
