IFS.assesment_feedback_page = (function(){
    "use strict";
    return {
		init : function(){
		    jQuery('body').on('change', '[id ^= "assessor-question-feedback-text-"],[data-feedback-value]', IFS.assesment_feedback_page.handleAssessorFeedbackUpdate);
		    jQuery('body').on('keyup', '[id ^= "assessor-question-feedback-"]', IFS.assesment_feedback_page.registerKeyupCallback(IFS.assesment_feedback_page.handleAssessorFeedbackFieldChangeOnField, 500));
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

	        //feedback
	        var formGroup = field.closest('.form-group');
	        var formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            var startAjaxTime= new Date().getTime();

            if(formTextareaSaveInfo.length === 0){
                formGroup.find('.textarea-footer').append('<span class="textarea-save-info" />');
                formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            }

	        jQuery.ajax({
	            type: "PUT",
	            url: formUrl + '/response/' + responseId + '?feedbackText=' + feedbackText + '&feedbackValue=' + feedbackValue,
                beforeSend: function() {
                    formTextareaSaveInfo.html('Saving...');
                }
	        }).
	        done(function(data){
                var doneAjaxTime = new Date().getTime();

	        	if(data.message == 'ok'){
                    if((doneAjaxTime-startAjaxTime) < 1500) {
                        setTimeout(function(){
                           formTextareaSaveInfo.html('Saved!');
                        },1500);
                    } else {
                        formTextareaSaveInfo.html('Saved!');
                    }	
	        	} else {
	        		formTextareaSaveInfo.html(data.message).css({'color':'red'});
	        	}
	        });
	    },
	    handleAssessorFeedbackUpdate : function() {
	        var field = jQuery(this);
	        return IFS.assesment_feedback_page.handleAssessorFeedbackFieldChangeOnField(field);
	    }
    };
})();

