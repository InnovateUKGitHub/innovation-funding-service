IFS.core.autoSave = (function(){
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
                  autoSave_timer = setTimeout(function(){ IFS.core.autoSave.fieldChanged(e.target); }, s.typeTimeout);
                }
                else {
                    IFS.core.autoSave.fieldChanged(e.target);
                }
            });
            jQuery('body').on('change', s.inputs, function(e){
                IFS.core.autoSave.fieldChanged(e.target);
            });
        },
        fieldChanged : function (element){
            var field = jQuery(element);
            var autoSaveEnabled = !field.hasClass('js-autosave-disabled');

            if(autoSaveEnabled) {
              //make sure repeating rows process sequential per row
              var promiseListName;
              if(field.closest('[data-repeatable-row]').length){
                promiseListName = field.closest('[data-repeatable-row]').attr('id');
              }
              else {
                promiseListName =field.attr('name');
              }
              //per field we handle the request on a promise base, this means that ajax calls should be per field sequental
              //this menas we can still have async as two fields can still be processed at the same time
              //http://www.jefferydurand.com/jquery/sequential/javascript/ajax/2015/04/13/jquery-sequential-ajax-promise-deferred.html
              if(typeof(promiseList[promiseListName]) == 'undefined'){
                promiseList[promiseListName] = jQuery.when({}); //fire first promise :)
              }

              promiseList[promiseListName] = promiseList[promiseListName].then(IFS.core.autoSave.processAjax(field));
          }
        },
        getFieldJson : function(field){
          var fieldId = field.attr('id').replace('form-textarea-','');
          var applicationId = jQuery("#application_id").val();
          var fieldValue = field.val();
          var name = field.attr('name');

          if((typeof(fieldId) !== 'undefined') && (typeof(applicationId) !== 'undefined') && (typeof(name) !== 'undefined')){
              var datefield = field.attr('data-date');

              //for the 3 seperate field date that has to be send as one
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
              return jsonObj;
          }
          else {
            return false;
          }

        },
        processAjax : function(field){
          return function(){
            var data = IFS.core.autoSave.getFieldJson(field);
            var defer = jQuery.Deferred();

            if(data===false){
              defer.resolve();
              return defer.promise();
            }
            else {
            //todo Brent de Kok: refactor this beast.
            var formGroup = field.closest('.form-group');
            var formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            var startAjaxTime= new Date().getTime();


            if(formTextareaSaveInfo.length === 0){
               formGroup.find('.textarea-footer').append('<span class="textarea-save-info" />');
               formTextareaSaveInfo = formGroup.find('.textarea-save-info');
            }

            var name = data.fieldName;
            var applicationId = data.applicationId;
      	    var unsavedCostRow = name.indexOf('unsaved') > -1 ? true : false;

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
                var remainingWaitingTime = (IFS.core.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

                //transform name of costrow for persisting to database
            	  if((typeof(data.field_id) !== 'undefined') && (unsavedCostRow === true)) {
                  jQuery('body').trigger('persistUnsavedRow',[name,data.field_id]);
                }
                // set the form-saved-state
                jQuery('body').trigger('updateSerializedFormState');

                if(data.success == 'true'){
                	//save message
                    setTimeout(function(){
                        IFS.core.autoSave.clearServerSideValidationErrors(field);
                        formTextareaSaveInfo.html('Saved!');
                    }, remainingWaitingTime);
                }else{
                    setTimeout(function(){
                        IFS.core.autoSave.clearServerSideValidationErrors(field);
                        formTextareaSaveInfo.html('Saved!');
                        if(s.hideAjaxValidation === false){
                          jQuery.each(data.validation_errors, function(index, value){
                              IFS.core.formValidation.setInvalid(field,value);
                              serverSideValidationErrors.push(value);
                          });
                        }
                    }, remainingWaitingTime);
                }
            }).fail(function(data) {
                var doneAjaxTime = new Date().getTime();
                var remainingWaitingTime = (IFS.core.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));
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
                               IFS.core.formValidation.setInvalid(field,errorMessage);
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
                     IFS.core.formValidation.setInvalid(field,errorMessage);
                     formTextareaSaveInfo.html('');
                 }
           }).complete(function(){
                defer.resolve();
           });
          return defer.promise();
          }
        };
        },
        clearServerSideValidationErrors : function(field){
            for (var i = 0; i < serverSideValidationErrors.length; i++){
                 IFS.core.formValidation.setValid(field,serverSideValidationErrors[i]);
            }
        }

    };
})();
