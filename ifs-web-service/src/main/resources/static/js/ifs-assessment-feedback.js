/* jshint strict: true, undef: true, unused: true */
/* globals $: false */
$(function() {
    "use strict";

    var handleAssessorFeedbackFieldChange = function(parameterName) {

        return function() {

            var field = $(this);
            var formUrl = field.closest('form').attr('action');
            var feedbackContainer = field.closest('[data-response-id]');
            var responseId = feedbackContainer.attr('data-response-id')
                ;
            $.ajax({
                type: "PUT",
                url: formUrl + '/response/' + responseId + '?' + parameterName + '=' + field.val()
            });
        };
    };

    $('body').on('change', '[id ^= "assessor-question-score-"]', handleAssessorFeedbackFieldChange('score'));
    $('body').on('change', '[id ^= "assessor-question-confirmation-"]', handleAssessorFeedbackFieldChange('confirmationAnswer'));
    $('body').on('change', '[id ^= "assessor-question-feedback-"]', handleAssessorFeedbackFieldChange('feedbackText'));
});