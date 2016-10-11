// Set up the handlers for adding and removing additional rows for declaration of interest fields
IFS.assessment.repeatableDeclarationRows = (function() {
    'use strict';

    return {
        init: function(){
            jQuery('body').on('click', '[data-repeatable-rowcontainer]', function(e){
                e.preventDefault();

                IFS.assessment.repeatableDeclarationRows.addRow(this,e);
            });
            jQuery('body').on('click', '.remove-another-row',function(e){
                e.preventDefault();

                IFS.assessment.repeatableDeclarationRows.removeRow(this,e);
            });
        },
        addRow : function(el){
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
        removeRow : function(el){
            jQuery(el).closest('tr').remove();
        }
    };
})();
