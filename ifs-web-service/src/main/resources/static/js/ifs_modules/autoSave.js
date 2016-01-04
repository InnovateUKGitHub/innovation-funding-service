IFS.autoSave = (function(){
    "use strict";
    var s; // private alias to settings 
    var serverSideValidationErrors = []; // we store the last validation message as deleting of messages is done by content as unique identifier.
                             // So if we have multiple messages it will only delete the one which contains the message that has been resolved.
    return {
        settings : {
            inputs : '.form-serialize-js input:not([type="button"],[readonly="readonly"])',
            textareas : '.form-serialize-js textarea:not([readonly="readonly"])',
            typeTimeout : 500,
            minimumUpdateTime : 1000, // the minimum time between the ajax request, and displaying the result of the ajax call.
        },
        init : function(){
            s = this.settings;
            var saveFields = s.inputs+','+s.textareas;
            jQuery('body').on('change', saveFields, function(e){ 
                IFS.autoSave.fieldChanged(e.target);
            });
            //wait until the user stops typing 
            jQuery('body').on('keyup', saveFields, function(e) { 
                clearTimeout(window.IFS.autoSave_timer);
                window.IFS.autoSave_timer = setTimeout(function(){ IFS.autoSave.fieldChanged(e.target); }, s.typeTimeout);
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

             var formGroup = field.closest('.form-group');
             var formState = jQuery('.form-serialize-js').serialize();
             var formTextareaSaveInfo = formGroup.find('.textarea-save-info');
             var startAjaxTime= new Date().getTime();
             var applicationId = jQuery("#application_id").val();

             if(formTextareaSaveInfo.length === 0){
                formGroup.find('.textarea-footer').append('<span class="textarea-save-info" />');
                formTextareaSaveInfo = formGroup.find('.textarea-save-info');
             }

             if(typeof(applicationId) !== 'undefined') {
                 jQuery.ajax({
                     type: 'POST',
                     url: '/application/'+applicationId+'/form/saveFormElement',
                     data: jsonObj,
                     dataType: "json",
                     beforeSend: function() {
                        formTextareaSaveInfo.html('Saving...');
                    }
                 })
                 .done(function(data){
                     var doneAjaxTime = new Date().getTime();
                     var remainingWaitingTime = (IFS.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                     // set the form-saved-state
                     jQuery('.form-serialize-js').data('serializedFormState',formState);
                      
                      //save message
                     if(data.success == 'true'){
                         setTimeout(function(){
                             IFS.autoSave.clearServerSideValidationErrors(field);
                             formTextareaSaveInfo.html('Saved!');
                         }, remainingWaitingTime);
                     }else{
                         setTimeout(function(){
                             IFS.autoSave.clearServerSideValidationErrors(field);
                             formTextareaSaveInfo.html("Invalid input, but saved anyway.");
                             jQuery.each(data.validation_errors, function(index, value){
                                 IFS.formValidation.setInvalid(field,value);
                                 serverSideValidationErrors.push(value);
                             });
                         }, remainingWaitingTime);
                     }
                 }).fail(function(data) {
                     var doneAjaxTime = new Date().getTime();
                     var remainingWaitingTime = (IFS.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                     setTimeout(function(){
                         var errorMessage = data.responseJSON.errorMessage;
                         if (formGroup.length) {
                             if(formTextareaSaveInfo.length){
                                formTextareaSaveInfo.html(errorMessage);
                             }
                             else {
                                var label = formGroup.find('label').first();
                                if (label.length) {
                                  serverSideValidationErrors.push(errorMessage);
                                  IFS.formValidation.setInvalid(field,errorMessage);
                                }
                             }
                         } else {
                             field.addClass('error');
                         }
                     }, remainingWaitingTime);
                 }).error(function(xhr) {
                      if(typeof(xhr.responseText) !== 'undefined'){
                          var err = JSON.parse(xhr.responseText);
                          var errorMessage = err.error+' : '+err.message;
                          serverSideValidationErrors.push(errorMessage);
                          IFS.formValidation.setInvalid(field,errorMessage);
                          formTextareaSaveInfo.html('');
                      }
                });
             }
        },
        clearServerSideValidationErrors : function(field){
            for (var i = 0; i < serverSideValidationErrors.length; i++){
                 IFS.formValidation.setValid(field,serverSideValidationErrors[i]);
            }
        }

    };
})();
