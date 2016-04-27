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
            var questionId = sectionToUpdate.attr('id');
            var sectionId;
            if(sectionToUpdate.closest('section[id^="section-"]').length){
                sectionId = sectionToUpdate.closest('section[id^="section-"]').attr('id').replace('section-','');
            }
            if(sectionToUpdate.length && sectionId && questionId){
              jQuery.ajaxProtected({
                    type: "POST",
                    beforeSend : function(){
                        if(typeof(IFS.progressiveSelect.hideAll) == 'function'){ IFS.progressiveSelect.hideAll();  }
                        sectionToUpdate.find('.assign-button').html('Assigning to <strong>'+button.text()+'</strong>...');
                        sectionToUpdate.find('img.section-status').remove();
                    },
                    url: '?singleFragment=true&sectionId=' + sectionId,
                    data: form.serialize() + '&' + button.attr('name') + '=' + button.attr('value'),
                    success: function(data) {
                      var htmlReplacement = jQuery('<div>' + data + '</div>');
                      var replacement = htmlReplacement.find('#' + questionId);
                      sectionToUpdate.replaceWith(replacement);
                      if(typeof(IFS.progressiveSelect.initDropDownHTML) == 'function'){
                        var dropdown = replacement.find(".assign-button");
                        var select =  replacement.find('select.prog-menu');
                        IFS.progressiveSelect.initDropDownHTML(dropdown);
                        IFS.progressiveSelect.selectToListHTML(select);
                      }
                    }
                });
                e.preventDefault();
                return false;
            }
        }
    };
})();
