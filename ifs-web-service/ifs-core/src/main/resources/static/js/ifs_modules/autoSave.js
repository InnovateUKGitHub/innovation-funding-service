IFS.autoSave = (function(){
    "use strict";
    var s; // private alias to settings
    var promiseList = {};
    var autoSave_timer;
    var serverSideValidationErrors = []; // we store the last validation message as deleting of messages is done by content as unique identifier.
                             // So if we have multiple messages it will only delete the one which contains the message that has been resolved.
    return {
        settings : {
            inputs : '.form-serialize-js input:not([type="button"],[readonly="readonly"],[type="hidden"])',
            textareas : '.form-serialize-js textarea:not([readonly="readonly"])',
            typeTimeout : 500,
            minimumUpdateTime : 1000,
            hideAjaxValidation : ''// the minimum time between the ajax request, and displaying the result of the ajax call.
        },
        init : function(){
            s = this.settings;

            s.hideAjaxValidation = jQuery('input[type="hidden"][value="hideAjaxValidation"]').length ? true : false;

            jQuery('body').on('change keyup', s.textareas, function(e){
                if(e.type == 'keyup'){
                  //wait until the user stops typing
                  clearTimeout(autoSave_timer);
                  autoSave_timer = setTimeout(function(){ IFS.autoSave.fieldChanged(e.target); }, s.typeTimeout);
                }
                else {
                    IFS.autoSave.fieldChanged(e.target);
                }
            });
            jQuery('body').on('change', s.inputs, function(e){
                IFS.autoSave.fieldChanged(e.target);
            });
        },
        fieldChanged : function (element){
            var field = jQuery(element);
            var name = field.attr('name');
            var fieldId = field.attr('id').replace('form-textarea-','');
            var applicationId = jQuery("#application_id").val();
            var autoSaveEnabled = true;
            if(field.hasClass('js-autosave-disabled')){
              autoSaveEnabled = false;
            }

            if((typeof(applicationId) !== 'undefined') && (typeof(name) !== 'undefined') && autoSaveEnabled) {
              //for the 3 seperate field date that has to be send as one

              var fieldValue = field.val();
              var datefield = field.attr('data-date');
              if(typeof datefield !== typeof undefined && datefield !== false){
                fieldValue = field.attr('data-date');
                var fieldInfo = field.closest('.date-group');
                name = fieldInfo.attr('data-field-group-name');
                fieldId = fieldInfo.attr('data-field-group-id');
              }

              var jsonObj = {
                  value: fieldValue,
                  formInputId: fieldId,
                  fieldName: name,
                  applicationId: applicationId
              };
              //per field we handle the request on a promise base, this means that ajax calls should be per field sequental
              //this menas we can still have async as two fields can still be processed at the same time
              //http://www.jefferydurand.com/jquery/sequential/javascript/ajax/2015/04/13/jquery-sequential-ajax-promise-deferred.html
              if(typeof(promiseList[name]) == 'undefined'){
                promiseList[name] = jQuery.when({}); //fire first promise :)
              }
              promiseList[name] = promiseList[name].then(IFS.autoSave.processAjax(field, applicationId,jsonObj));
          }
        },
        processAjax : function(field,applicationId, data){
          return function(){
            var defer = jQuery.Deferred();
            var formGroup = field.closest('.form-group');
            var formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            var startAjaxTime= new Date().getTime();

            if(formTextareaSaveInfo.length === 0){
               formGroup.find('.textarea-footer').append('<span class="textarea-save-info" />');
               formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            }

            jQuery.ajaxProtected({
                type: 'POST',
                url: '/application/'+applicationId+'/form/saveFormElement',
                data: data,
                dataType: "json",
                beforeSend: function() {
                   formTextareaSaveInfo.html('Saving...');
               }
            })
            .done(function(data){
                var doneAjaxTime = new Date().getTime();
                var remainingWaitingTime = (IFS.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                // set the form-saved-state
                jQuery('body').trigger('updateSerializedFormState');

                 
                if(data.success == 'true'){
                	
                	// set field_id on field name
                	if(data.field_id !== null) {
                    	var name = field.attr('name');
                    	var nameDashSplit = name.split('-');
                    	if (nameDashSplit.length < 4) {
                    		field.attr('name', name + '-' + data.field_id);
                    	}
                	}
                	
                	//save message
                    setTimeout(function(){
                        IFS.autoSave.clearServerSideValidationErrors(field);
                        formTextareaSaveInfo.html('Saved!');
                    }, remainingWaitingTime);
                }else{
                    setTimeout(function(){
                        IFS.autoSave.clearServerSideValidationErrors(field);
                        formTextareaSaveInfo.html('Saved!');
                        if(s.hideAjaxValidation === false){
                          jQuery.each(data.validation_errors, function(index, value){
                              IFS.formValidation.setInvalid(field,value);
                              serverSideValidationErrors.push(value);
                          });
                        }
                    }, remainingWaitingTime);
                }
            }).fail(function(data) {
                var doneAjaxTime = new Date().getTime();
                var remainingWaitingTime = (IFS.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));
                if(typeof(data.responseJSON) !== 'undefined'){
                  setTimeout(function(){
                      var errorMessage = data.responseJSON.errorMessage;
                      if (formGroup.length) {
                          if(formTextareaSaveInfo.length){
                             formTextareaSaveInfo.html(errorMessage);
                          }
                          else {
                             var label = formGroup.find('label,legend').first();
                             if (label.length) {
                               serverSideValidationErrors.push(errorMessage);
                               IFS.formValidation.setInvalid(field,errorMessage);
                             }
                          }
                      } else {
                          field.addClass('error');
                      }
                  }, remainingWaitingTime);
               }
            }).error(function(xhr) {
                 if(typeof(xhr.responseText) !== 'undefined'){
                     var err = jQuery.parseJSON(xhr.responseText);
                     var errorMessage = err.error+' : '+err.message;
                     serverSideValidationErrors.push(errorMessage);
                     IFS.formValidation.setInvalid(field,errorMessage);
                     formTextareaSaveInfo.html('');
                 }
           }).complete(function(){
                defer.resolve();
           });
          return defer.promise();
          };
        },
        clearServerSideValidationErrors : function(field){
            for (var i = 0; i < serverSideValidationErrors.length; i++){
                 IFS.formValidation.setValid(field,serverSideValidationErrors[i]);
            }
        }

    };
})();
