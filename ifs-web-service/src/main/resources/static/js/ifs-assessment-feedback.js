/* jshint strict: true, undef: true, unused: true */
/* globals $: false */
$(function() {
    "use strict";

    // TODO DW - consider suggesting as an alternative to the use of "typeWatch" plugin as used in the Application Form autosave javascript in ifs.js, as
    // it is event delegation-based rather than needing to be registered directly on DOM elements, and is therefore kinder to pages that dynamically
    // update their contents
    var registerKeyupCallback = function(callback, keyupDelay) {

        return function(e) {
            var field = $(this);
            var existingCountdown = field.data('keyupListener');
            if (typeof existingCountdown !== 'undefined') {
                window.clearTimeout(existingCountdown);
            }
            field.data('keyupListener', window.setTimeout(function() {
                callback(field, e);
                field.data('keyupListener', null);
            }, keyupDelay));
        };
    };

    var handleAssessorFeedbackFieldChangeOnField = function(field) {

        var formUrl = field.closest('form').attr('action');
        var feedbackContainer = field.closest('[data-response-id]');
        var responseId = feedbackContainer.attr('data-response-id');

        var feedbackText = feedbackContainer.find('[id ^= "assessor-question-feedback-text-"]').val();
        var feedbackValue = feedbackContainer.find('[data-feedback-value]').val();

        $.ajax({
            type: "PUT",
            url: formUrl + '/response/' + responseId + '?feedbackText=' + feedbackText + '&feedbackValue=' + feedbackValue
        });
    };

    var handleAssessorFeedbackUpdate = function() {
        var field = $(this);
        return handleAssessorFeedbackFieldChangeOnField(field);
    };

    $('body').on('change', '[id ^= "assessor-question-feedback-text-"],[data-feedback-value]', handleAssessorFeedbackUpdate);

    $('body').on('keyup', '[id ^= "assessor-question-feedback-"]', registerKeyupCallback(handleAssessorFeedbackFieldChangeOnField, 500));
});