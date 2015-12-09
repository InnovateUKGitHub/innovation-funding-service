// Handlers for single section refreshing when assigning questions to users on the Application Overview
// dependency on ifs_collpasible
IFS.application_page = (function(){
    "use strict";
    return {
        init: function(){
            jQuery(document).on('click', 'form.application-overview [name="assign_question"]', IFS.application_page.handleAssignQuestionFragmentReload);
        },
         handleAssignQuestionFragmentReload : function(e) {
            var button = jQuery(this);
            var form = button.closest('form.application-overview');
            var sectionToUpdate = button.closest('li.section');
            var sectionId;
            if(sectionToUpdate.closest('section').prop('id').indexOf('section-') !== -1){
                sectionId = sectionToUpdate.closest('section').attr('id').replace('section-','');
            }
            var handleFormPost = function (data) {
                var htmlReplacement = jQuery('<div>' + data + '</div>');
                var questionId = sectionToUpdate.attr('id');
                var replacement = htmlReplacement.find('#' + questionId);
                sectionToUpdate.replaceWith(replacement);
                IFS.collapsible.collapsibleWithinScope(replacement);
            };
            if(sectionToUpdate.length && sectionId){
              jQuery.ajax({
                    type: "POST",
                    // TODO DW - shouldn't have to pass the sectionId via a request parameter - it should instead be made available by the "name" and "value" params on the clicked button, as per the questionId
                    url: '?singleFragment=true&sectionId=' + sectionId,
                    data: form.serialize() + '&' + button.attr('name') + '=' + button.attr('value'),
                    success: function(data) {
                        handleFormPost(data);
                    }
                });  
            }
            e.preventDefault();
            return false;
        }
    };
})();


