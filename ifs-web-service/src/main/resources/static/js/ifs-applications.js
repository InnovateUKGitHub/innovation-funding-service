/* jshint strict: true, undef: true, unused: true */
/* globals $: false, IFS: false */

//
// Handlers for single section refreshing when assigning questions to users on the Application Overview
//
$(function() {

    "use strict";

    var handleAssignQuestionFragmentReload = function(e) {

        var button = $(this);
        var form = button.closest('form.application-overview');
        var sectionToUpdate = button.closest('li.section[data-question-id]');
        var sectionId = sectionToUpdate.attr('data-section-id');

        var handleFormPost = function (data) {

            var htmlReplacement = $('<div>' + data + '</div>');
            var questionId = sectionToUpdate.attr('data-question-id');
            var replacement = htmlReplacement.find('li.section[data-question-id=' + questionId + ']');
            sectionToUpdate.replaceWith(replacement);

            IFS.collapsibleWithinScope(replacement);
        };

        $.ajax({
            type: "POST",
            // TODO DW - shouldn't have to pass the sectionId via a request parameter - it should instead be made available by the "name" and "value" params on the clicked button, as per the questionId
            url: '?singleFragment=true&sectionId=' + sectionId,
            data: form.serialize() + '&' + button.attr('name') + '=' + button.attr('value'),
            success: handleFormPost
        });

        e.preventDefault();
        return false;
    };

    $(document).on('click', 'form.application-overview [name="assign_question"]', handleAssignQuestionFragmentReload);

});