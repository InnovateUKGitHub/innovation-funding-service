// Set up the handlers for adding and removing additional rows for inviting assessors
IFS.competitionManagement.repeatableRows = (function () {
  'use strict'

  return {
    init: function () {
      // prevent 'enter' key adding new rows by changing button type
      jQuery('button[data-repeatable-rowcontainer]').attr('type', 'button')

      jQuery('body').on('click', '[data-repeatable-rowcontainer]', function (e) {
        e.preventDefault()

        IFS.competitionManagement.repeatableRows.addRow(this, e)
      })
      jQuery('body').on('click', '.remove-another-row', function (e) {
        e.preventDefault()

        IFS.competitionManagement.repeatableRows.removeRow(this, e)
      })
    },
    addRow: function (el) {
      var target = jQuery(el).attr('data-repeatable-rowcontainer')
      var uniqueRowId = jQuery(target).children().length || 0
      var newRow = jQuery('<tr class="form-group-row-validated">' +
        '<td class="width-40-percent form-group">' +
        '<label></label>' +
        '<input aria-labelledby="invite-label-assessor-name" class="form-control width-full" type="text" ' +
        'id="invites[' + uniqueRowId + '].name" ' +
        'name="invites[' + uniqueRowId + '].name" value="" ' +
        'minlength="2" ' +
        'maxlength="70" ' +
        'pattern="\\D{2,}" ' +
        'required="required" ' +
        'data-required-errormessage="Please enter a name." ' +
        'data-pattern-errormessage="Please enter a valid name." /> ' +
        '</td>' +
        '<td class="width-40-percent form-group">' +
        '<label></label>' +
        '<input aria-labelledby="invite-label-assessor-email" class="form-control width-full" type="email" ' +
        'id="invites' + uniqueRowId + '.email" ' +
        'name="invites[' + uniqueRowId + '].email" value="" ' +
        'data-required-errormessage="Please enter an email address." required="required" />' +
        '</td>' +
        '<td class="alignright width-20-percent">' +
        '<button class="remove-another-row buttonlink" name="removeNewUser" type="button" value="0">Remove</button>' +
        '</td>' +
        '</tr>')

      // insert the new row with the correct values and move focus to the first field to aid keyboard users
      jQuery(target).append(newRow)
      jQuery(newRow).find('input').first().focus()
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
          var $rowInput = jQuery(this)
          var newId = $rowInput.attr('id').replace(/\d+/g, rowIndex)
          var newName = $rowInput.attr('name').replace(/\d+/g, rowIndex)

          $rowInput.attr({
            'id': newId,
            'name': newName
          })
        })
      })
    }
  }
})()
