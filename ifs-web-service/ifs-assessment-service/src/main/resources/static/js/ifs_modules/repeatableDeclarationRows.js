// Set up the handlers for adding and removing additional rows for declaration of interest fields
IFS.assessment.repeatableDeclarationRows = (function() {
  'use strict';

  return {
    init: function() {
      jQuery('body').on('click', '[data-repeatable-rowcontainer]', function(e) {
        e.preventDefault();

        IFS.assessment.repeatableDeclarationRows.addRow(this, e);
      });
      jQuery('body').on('click', '.remove-another-row', function(e) {
        e.preventDefault();

        IFS.assessment.repeatableDeclarationRows.removeRow(this, e);
      });
    },
    addRow : function(el) {
      var newRow;
      var target = jQuery(el).attr('data-repeatable-rowcontainer');
      var uniqueRowId = jQuery(target).children().length || 0;
      if(jQuery(el).attr('name') === 'addAppointment'){
        newRow = '<tr>' +
        '<td class="form-group">' +
        '<input aria-labelledby="aria-position-org" class="form-control width-full" type="text" id="appointments' + uniqueRowId + '.organisation" name="appointments[' + uniqueRowId + '].organisation" value="" />' +
        '</td>' +
        '<td class="form-group">' +
        '<input aria-labelledby="aria-position-pos" class="form-control width-full" type="text" id="appointments' + uniqueRowId + '.position" name="appointments[' + uniqueRowId + '].position" value="" />' +
        '</td>' +
        '<td>' +
        '<button class="remove-another-row buttonlink" name="removeAppointment" type="button" value="0">Remove</button>' +
        '</td>' +
        '</tr>';
      }else{
        newRow = '<tr>' +
        '<td class="form-group">' +
        '<input aria-labelledby="aria-family-rel" class="form-control width-full" type="text" id="familyAffiliations' + uniqueRowId + '.relation" name="familyAffiliations[' + uniqueRowId + '].relation" value="" />' +
        '</td>' +
        '<td class="form-group">' +
        '<input aria-labelledby="aria-family-org" class="form-control width-full" type="text" id="familyAffiliations' + uniqueRowId + '.organisation" name="familyAffiliations[' + uniqueRowId + '].organisation" value="" />' +
        '</td>' +
        '<td class="form-group">' +
        '<input aria-labelledby="aria-family-pos" class="form-control width-full" type="text" id="familyAffiliations' + uniqueRowId + '.position" name="familyAffiliations[' + uniqueRowId + '].position" value="" />' +
        '</td>' +
        '<td>' +
        '<button class="remove-another-row buttonlink" name="removeFamilyMemberAffiliation" type="button" value="1">Remove</button>' +
        '</td>' +
        '</tr>';
      }

      //insert the new row with the correct values and move focus to the first field to aid keyboard users
      jQuery(target).append(newRow);
      jQuery(newRow).find('input').first().focus();
    },
    removeRow : function(el) {
      var rowParent = jQuery(el).closest('tbody');
      var rows = [];

      jQuery(el).closest('tr').remove();

      //re-number rows to ensure no empty/missing data is created server-side
      rows = jQuery(rowParent).children();

      if(rows.length !== 0){
        jQuery(rows).each(function(rowIndex) {
          var rowsInputs = jQuery(this).find('input');

          jQuery(rowsInputs).each(function() {
            //regex will replace 1 or more numbers in the string with the new index value
            var newId = jQuery(this).attr('id').replace(/\d+/g, rowIndex);
            var newName = jQuery(this).attr('name').replace(/\d+/g, rowIndex);

            jQuery(this).attr({
              'id': newId,
              'name': newName
            });
          });
        });
      }
    }
  };
})();
