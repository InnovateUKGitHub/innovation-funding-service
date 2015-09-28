/* jshint strict: true, undef: true, unused: true */
/* globals $: false, worthIFS: false */

//
// Handlers for single section refreshing when assigning questions to users on the Application Overview
//
$(function() {

    "use strict";

    var handleAssignQuestionFragmentReload = function(e) {

        var link = $(this);
        var form = link.closest('form.application-overview');
        var sectionToUpdate = link.closest('li.section[data-question-id]');
        var sectionId = sectionToUpdate.attr('data-section-id');

        var handleFormPost = function (data) {

            var htmlReplacement = $('<div>' + data + '</div>');
            var questionId = sectionToUpdate.attr('data-question-id');
            var replacement = htmlReplacement.find('li.section[data-question-id=' + questionId + ']');
            sectionToUpdate.replaceWith(replacement);

            worthIFS.collapsibleWithinScope(replacement);
        };

        $.ajax({
            type: "POST",
            url: '?singleFragment=true&sectionId=' + sectionId,
            data: form.serialize() + '&' + link.attr('name') + '=' + link.attr('value'),
            success: handleFormPost
        });

        e.preventDefault();
        return false;
    };

    $(document).on('click', 'form.application-overview [name="assign_question"]', handleAssignQuestionFragmentReload);

});