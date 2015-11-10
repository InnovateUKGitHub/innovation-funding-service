/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false*/

var ifs_autoSave = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            inputFields : jQuery('.form-serialize-js input').not('[type="button"],[readonly="readonly"]'),
            textareas : jQuery('.form-serialize-js textarea').not('[readonly="readonly"]')
        },
        init : function(){
            s = this.settings;
            
            var fields = s.inputFields.add(s.textareas);
            this.initAutosaveElements(fields);
        },
        initAutosaveElements : function(fields){
            var options = {
                callback: function () { ifs_autoSave.fieldChanged(this);  },
                wait: 500,
                highlight: false,
                captureLength: 1
            };

            fields.typeWatch(options);
            fields.off('change').on('change', function(e) {
                ifs_autoSave.fieldChanged(e.target);
            });
        },
        fieldChanged : function (element){

            var field = jQuery(element);
            var fieldId = field.attr('id');

            var jsonObj = {
                value: element.value,
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
                           formTextareaSaveInfo.html('Saved!');
                        },1500);
                    } else {
                        formTextareaSaveInfo.html('Saved!');
                    }
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
