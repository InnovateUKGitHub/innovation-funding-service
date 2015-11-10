/* jshint strict: true, undef: true, unused: true */
/* globals jQuery : false, window : false*/

var ifs_assesment_feedback_page = (function(){
    "use strict";
    return {
		init : function(){
		    jQuery('body').on('change', '[id ^= "assessor-question-feedback-text-"],[data-feedback-value]', ifs_assesment_feedback_page.handleAssessorFeedbackUpdate);
		    jQuery('body').on('keyup', '[id ^= "assessor-question-feedback-"]', ifs_assesment_feedback_page.registerKeyupCallback(ifs_assesment_feedback_page.handleAssessorFeedbackFieldChangeOnField, 500));
		},
	    registerKeyupCallback : function(callback, keyupDelay) {
	        return function(e) {
	            var field = jQuery(this);
	            var existingCountdown = field.data('keyupListener');
	            if (typeof existingCountdown !== 'undefined') {
	                window.clearTimeout(existingCountdown);
	            }
	            field.data('keyupListener', window.setTimeout(function() {
	                callback(field, e);
	                field.data('keyupListener', null);
	            }, keyupDelay));
	        };
	    },
	    handleAssessorFeedbackFieldChangeOnField : function(field) {

	        var formUrl = field.closest('form').attr('action');
	        var feedbackContainer = field.closest('[data-response-id]');
	        var responseId = feedbackContainer.attr('data-response-id');

	        var feedbackText = feedbackContainer.find('[id ^= "assessor-question-feedback-text-"]').val();
	        var feedbackValue = feedbackContainer.find('[data-feedback-value]').val();

	        jQuery.ajax({
	            type: "PUT",
	            url: formUrl + '/response/' + responseId + '?feedbackText=' + feedbackText + '&feedbackValue=' + feedbackValue
	        });
	    },
	    handleAssessorFeedbackUpdate : function() {
	        var field = jQuery(this);
	        return ifs_assesment_feedback_page.handleAssessorFeedbackFieldChangeOnField(field);
	    }
    };
})();

