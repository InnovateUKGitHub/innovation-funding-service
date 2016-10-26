// Handlers for single section refreshing when assigning questions to users on the Application Overview
// dependency on ifs_collpasible
IFS.application.applicationPage = (function() {
  "use strict";
  return {
    init: function() {
      jQuery(document).on('click', 'form.application-overview [name="assign_question"]', IFS.application.applicationPage.handleAssignQuestionFragmentReload);
    },
    handleAssignQuestionFragmentReload : function(e) {
      var button = jQuery(this);
      var form = button.closest('form.application-overview');
      var sectionToUpdate = button.closest('li.section');
      var questionId = sectionToUpdate.attr('id');
      var sectionId;
      if(sectionToUpdate.closest('section[id^="section-"]').length){
        sectionId = sectionToUpdate.closest('section[id^="section-"]').attr('id').replace('section-', '');
      }
      if(sectionToUpdate.length && sectionId && questionId){
        jQuery.ajaxProtected({
          type: "POST",
          beforeSend : function() {
            if(typeof(IFS.application.progressiveSelect.hideAll) == 'function'){ IFS.application.progressiveSelect.hideAll();  }
            //hide the assign button and add an assigning to... text
            var assignButtonContainer =  sectionToUpdate.find('.assign-button');
            assignButtonContainer.children('button').attr('aria-hidden', 'true');
            sectionToUpdate.find('img.section-status').attr('aria-hidden', 'true');
            assignButtonContainer.find('.reassign-status').remove();
            assignButtonContainer.append('<div class="reassign-status">Assigning to <strong>'+button.text()+'</strong>...</div>');
          },
          url: '?singleFragment=true&sectionId=' + sectionId,
          data: form.serialize() + '&' + button.attr('name') + '=' + button.attr('value'),
          timeout: 15000,
          success: function(data) {
            var htmlReplacement = jQuery('<div>' + data + '</div>');
            var replacement = htmlReplacement.find('#' + questionId);
            sectionToUpdate.replaceWith(replacement);
            if(typeof(IFS.application.progressiveSelect.initDropDownHTML) == 'function'){
              var dropdown = replacement.find(".assign-button");
              var select =  replacement.find('select.prog-menu');
              IFS.application.progressiveSelect.initDropDownHTML(dropdown);
              IFS.application.progressiveSelect.selectToListHTML(select);
            }
          }
        }).fail(function(data) {
          var errorMessage = IFS.core.autoSave.getErrorMessage(data);
          if(errorMessage){
            var assignButtonContainer =  sectionToUpdate.find('.assign-button');
            assignButtonContainer.children('button').attr('aria-hidden', 'false');
            sectionToUpdate.find('img.section-status').attr('aria-hidden', 'false');
            sectionToUpdate.find('.reassign-status').html('<div class="error-message">'+errorMessage+'</div>');
          }
        });
        e.preventDefault();
        return false;
      }
    }
  };
})();
