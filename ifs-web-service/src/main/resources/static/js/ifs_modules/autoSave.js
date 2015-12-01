/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false, clearTimeout: false,window:false */

var ifs_autoSave = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            inputs : '.form-serialize-js input:not([type="button"],[readonly="readonly"])',
            textareas : '.form-serialize-js textarea:not([readonly="readonly"])',
            typeTimeout : 500,
            minimumUpdateTime : 1000 // the minimum time between the ajax request, and displaying the result of the ajax call.
        },
        init : function(){
            s = this.settings;
            var saveFields = s.inputs+','+s.textareas;

            jQuery('body').on('change', saveFields, function(e){ 
                ifs_autoSave.fieldChanged(e.target);
            });
            //wait until the user stops typing 
            jQuery('body').on('keyup', saveFields, function(e) { 
                clearTimeout(window.ifs_autoSave_timer);
                window.ifs_autoSave_timer = setTimeout(function(){ ifs_autoSave.fieldChanged(e.target); }, s.typeTimeout);
            });
        },
        fieldChanged : function (element){
            var field = jQuery(element);
            var fieldId = field.attr('id');

            var jsonObj = {
                value: field.val(),
                formInputId: fieldId,
                fieldName: field.attr('name'),
                applicationId: jQuery(".form-serialize-js #application_id").val()
            };

             var formState = jQuery('.form-serialize-js').serialize();
             var formGroup = field.closest('.form-group');
             var validationMessages = formGroup.find(".validation-messages").first();
             var formTextareaSaveInfo = formGroup.find('.textarea-save-info');
             var startAjaxTime= new Date().getTime();

             if(formTextareaSaveInfo.length === 0){
                formGroup.find('.textarea-footer').append('<span class="textarea-save-info" />');
                formTextareaSaveInfo = formGroup.find('.textarea-save-info');
             }

             jQuery.ajax({
                 type: 'POST',
                 url: "/application-form/saveFormElement",
                 data: jsonObj,
                 dataType: "json",
                 beforeSend: function() {
                    formTextareaSaveInfo.html('Saving...');
                }
             })
             .done(function(data){
                 var doneAjaxTime = new Date().getTime();
                 var remainingWaitingTime = (this.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                 // set the form-saved-state
                 jQuery('.form-serialize-js').data('serializedFormState',formState);
                 field.removeClass('error');
                 formGroup.removeClass('error');
                  
                  //save message
                 if(data.success == 'true'){
                     setTimeout(function(){
                         ifs_autoSave.removeValidationError(formGroup);
                         formTextareaSaveInfo.html('Saved!');
                     }, remainingWaitingTime);
                 }else{
                     setTimeout(function(){
                         formTextareaSaveInfo.html("Invalid input, not saved.");
                         ifs_autoSave.removeValidationError(formGroup);
                         jQuery.each(data.validation_errors, function(index, value){
                             validationMessages.append('<span class="error-message">' + value + '</span>');
                         });
                         formGroup.addClass('error');
                     }, remainingWaitingTime);
                 }
             }).fail(function(data) {
                 var doneAjaxTime = new Date().getTime();
                 var remainingWaitingTime = (this.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                 setTimeout(function(){
                     ifs_autoSave.removeValidationError(formGroup);

                     var errorMessage = data.responseJSON.errorMessage;
                     if (formGroup.length) {
                         formGroup.addClass('error');
                         if(formTextareaSaveInfo.length){
                            formTextareaSaveInfo.html(errorMessage);
                         }
                         else {
                            var label = formGroup.find('label').first();
                            if (label.length) {
                              validationMessages.append('<span class="error-message">' + errorMessage + '</span>');
                            }
                         }
                     } else {
                         field.addClass('error');
                     }
                 }, remainingWaitingTime);
             });
        },
        removeValidationError: function (formGroup){
            formGroup.removeClass('error');
            formGroup.find('.error-message').remove();
        }
    };
})();
