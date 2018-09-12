// Set up the handlers for adding and removing additional rows for declaration of interest fields
IFS.assessment.repeatableDeclarationRows = (function () {
  'use strict'

  return {
    init: function () {
      // prevent 'enter' key adding new rows by changing button type
      jQuery('button[data-repeatable-rowcontainer]').attr('type', 'button')

      jQuery('body').on('click', '[data-repeatable-rowcontainer]', function (e) {
        if (jQuery(this).is(':radio')) {
          // Radio button toggle has been clicked, add first row if there isn't one
          var target = jQuery(this).attr('data-repeatable-rowcontainer')

          if (jQuery(target).children().length === 0) {
            IFS.assessment.repeatableDeclarationRows.addRow(this, false)
          }
        } else {
          e.preventDefault()

          IFS.assessment.repeatableDeclarationRows.addRow(this, true)
        }
      })
      jQuery('body').on('click', '.remove-another-row', function (e) {
        e.preventDefault()

        IFS.assessment.repeatableDeclarationRows.removeRow(this, true)
      })
    },
    addRow: function (el, focus) {
      var newRow
      var target = jQuery(el).attr('data-repeatable-rowcontainer')
      var uniqueRowId = jQuery(target).children().length || 0
      if (jQuery(el).attr('name') === 'hasAppointments' || jQuery(el).attr('name') === 'addAppointment') {
        newRow = jQuery('<tr class="govuk-table__row form-group-row-validated">' +
          '<td class="govuk-table__cell govuk-form-group">' +
          '<label></label>' +
          '<input aria-labelledby="aria-position-org" class="govuk-input appointment-field" type="text" ' +
          'id="appointments' + uniqueRowId + '.organisation" ' +
          'name="appointments[' + uniqueRowId + '].organisation" value="" ' +
          'data-required-errormessage="Please enter an organisation." required="required" />' +
          '</td>' +
          '<td class="govuk-table__cell govuk-form-group">' +
          '<label></label>' +
          '<input aria-labelledby="aria-position-pos" class="govuk-input appointment-field" type="text" ' +
          'id="appointments' + uniqueRowId + '.position" ' +
          'name="appointments[' + uniqueRowId + '].position" value="" ' +
          'data-required-errormessage="Please enter a position." required="required" />' +
          '</td>' +
          '<td class="govuk-table__cell">' +
          '<button class="remove-another-row button-clear" name="removeAppointment" type="button" value="0">Remove</button>' +
          '</td>' +
          '</tr>')
      } else {
        newRow = jQuery('<tr class="govuk-table__row form-group-row-validated">' +
          '<td class="govuk-table__cell govuk-form-group">' +
          '<label class="govuk-label">Relation</label>' +
          '<input aria-labelledby="aria-family-rel" class="govuk-input family-affiliation-field" type="text" ' +
          'id="familyAffiliations' + uniqueRowId + '.relation" ' +
          'name="familyAffiliations[' + uniqueRowId + '].relation" value="" ' +
          'data-required-errormessage="Please enter a relation." required="required" />' +
          '</td>' +
          '<td class="govuk-table__cell govuk-form-group">' +
          '<label class="govuk-label">Organisation</label>' +
          '<input aria-labelledby="aria-family-org" class="govuk-input family-affiliation-field" type="text" ' +
          'id="familyAffiliations' + uniqueRowId + '.organisation" ' +
          'name="familyAffiliations[' + uniqueRowId + '].organisation" value="" ' +
          'data-required-errormessage="Please enter an organisation." required="required" />' +
          '</td>' +
          '<td class="govuk-table__cell govuk-form-group">' +
          '<label class="govuk-label">Position</label>' +
          '<input aria-labelledby="aria-family-pos" class="govuk-input family-affiliation-field" type="text" ' +
          'id="familyAffiliations' + uniqueRowId + '.position" ' +
          'name="familyAffiliations[' + uniqueRowId + '].position" value="" ' +
          'data-required-errormessage="Please enter a position." required="required" />' +
          '</td>' +
          '<td class="govuk-table__cell">' +
          '<button class="remove-another-row button-clear" name="removeFamilyMemberAffiliation" type="button" value="1">Remove</button>' +
          '</td>' +
          '</tr>')
      }

      // insert the new row with the correct values and move focus to the first field to aid keyboard users
      newRow.appendTo(target)

      // only move focus if required. We don't want to move focus when clicking the radio button
      if (focus === true) {
        newRow.find('input').first().focus()
      }
    },
    removeRow: function (el) {
      var $element = jQuery(el)
      var rowParent = $element.closest('tbody')
      var rows

      // must remove row before getting row information to correctly count remaining rows
      $element.closest('tr').remove()

      rows = jQuery(rowParent).children()

      // re-number rows to ensure no empty/missing data is created server-side
      jQuery(rows).each(function (rowIndex) {
        var rowsInputs = jQuery(this).find('input')

        jQuery(rowsInputs).each(function () {
          // regex will replace 1 or more numbers in the string with the new index value
          var newId = jQuery(this).attr('id').replace(/\d+/g, rowIndex)
          var newName = jQuery(this).attr('name').replace(/\d+/g, rowIndex)

          jQuery(this).attr({
            'id': newId,
            'name': newName
          })
        })
      })
    }
  }
})()
