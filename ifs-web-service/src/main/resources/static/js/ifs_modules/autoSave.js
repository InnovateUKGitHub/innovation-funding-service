/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false, clearTimeout: false,window:false */

var ifs_autoSave = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            inputs : '.form-serialize-js input:not([type="button"],[readonly="readonly"])',
            textareas : '.form-serialize-js textarea:not([readonly="readonly"])',
            typeTimeout : 500
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
                questionId: fieldId,
                fieldName: field.attr('name'),
                applicationId: jQuery(".form-serialize-js #application_id").val()
             };

             var formState = jQuery('.form-serialize-js').serialize();
             var formGroup = field.closest('.form-group');

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

                // set the form-saved-state
                jQuery('.form-serialize-js').data('serializedFormState',formState);
                 field.removeClass('error');
                 formGroup.removeClass('error');
                  
                  //save message
                 if(data.success == 'true'){
                    if((doneAjaxTime-startAjaxTime) < 1500) {
                        setTimeout(function(){
                            formGroup.removeClass('error');
                           formTextareaSaveInfo.html('Saved!');
                        },1500);
                    } else {
                        formGroup.removeClass('error');
                        formTextareaSaveInfo.html('Saved!');
                    }
                 }else{
                    formTextareaSaveInfo.html(data.validation_errors);
                    formGroup.addClass('error');
                 }
             }).fail(function(data) {
                 var errorMessage = data.responseJSON.errorMessage;
                 if (formGroup.length) {
                     formGroup.addClass('error');
                     if(formTextareaSaveInfo.length){
                        formTextareaSaveInfo.html(errorMessage);
                     }
                     else {
                        var label = formGroup.find('label').first();
                        if (label.length) {
                          label.append('<span class="error-message" id="error-message-' + fieldId + '">' + errorMessage + '</span>');
                        }  
                     }
                 } else {
                     field.addClass('error');
                 }
             });
        }
    };
})();
