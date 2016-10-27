IFS.core.autoSave = (function(){
  "use strict";
  var s; // private alias to settings
  var promiseList = {};
  var autoSaveTimer;
  var serverSideValidationErrors = [];
  // we store the last validation message as deleting of messages is done by content as unique identifier.
  // So if we have multiple messages it will only delete the one which contains the message that has been resolved.

  return {
    settings : {
      inputs : '[data-autosave] input:not([type="button"],[readonly="readonly"],[type="hidden"],[data-autosave-disabled])',
      select : '[data-autosave] select:not([readonly="readonly"],[data-autosave-disabled])',
      textareas : '[data-autosave] textarea:not([readonly="readonly"],[data-autosave-disabled])',
      typeTimeout : 500,
      minimumUpdateTime : 800,// the minimum time between the ajax request, and displaying the result of the ajax call.
      ajaxTimeOut : 15000
    },
    init : function(){
      s = this.settings;
      jQuery('[data-autosave]').attr('data-save-status', 'done');
      jQuery('body').on('change keyup', s.textareas, function(e){
        if(e.type == 'keyup'){
          //wait until the user stops typing
          clearTimeout(autoSaveTimer);
          autoSaveTimer = setTimeout(function(){ IFS.core.autoSave.fieldChanged(e.target); }, s.typeTimeout);
        }
        else {
          IFS.core.autoSave.fieldChanged(e.target);
        }
      });
      jQuery('body').on('change', s.inputs+','+s.select, function(e){
        IFS.core.autoSave.fieldChanged(e.target);
      });
      //events for other javascripts
      jQuery('body').on('ifsAutosave', function(e){
        IFS.core.autoSave.fieldChanged(e.target);
      });
    },
    fieldChanged : function (element){
      var field = jQuery(element);

      //per field we handle the request on a promise base, this means that ajax calls should be per field sequental
      //this menas we can still have async as two fields can still be processed at the same time
      //http://www.jefferydurand.com/jquery/sequential/javascript/ajax/2015/04/13/jquery-sequential-ajax-promise-deferred.html
      var promiseListName;
      if(field.closest('[data-repeatable-row]').length){
        //make sure repeating rows process sequential per row
        promiseListName = field.closest('[data-repeatable-row]').prop('id');
      }
      else {
        promiseListName =field.prop('name');
      }
      if(typeof(promiseList[promiseListName]) == 'undefined'){
        promiseList[promiseListName] = jQuery.when({}); //fire first promise :)
      }

      promiseList[promiseListName] = promiseList[promiseListName].then(IFS.core.autoSave.processAjax(field));
    },
    getPostObject : function(field, form){
      //traversing from field as we might get the situation in the future where we have 2 different type autosaves on 1 page within two seperate <form>'s
      var applicationId = jQuery("#application_id").val();
      var saveType = form.attr('data-autosave');
      var jsonObj;
      var fieldInfo;
      var dateField;
      switch(saveType){
        case 'application':
          dateField = field.is('[data-date]');
          if(dateField){
            fieldInfo = field.closest('.date-group').find('input[type="hidden"]');
            jsonObj = {
              applicationId: applicationId,
              value: field.attr('data-date'),
              formInputId: fieldInfo.prop('id'),
              fieldName:  fieldInfo.prop('name')
            };
          }
          else {
            jsonObj = {
              applicationId: applicationId,
              value: field.val(),
              formInputId: field.prop('id').replace('form-textarea-', ''),
              fieldName: field.prop('name')
            };
          }
          break;
        case 'fundingDecision':
          jsonObj = {
            applicationId: field.prop('name'),
            fundingDecision: field.val()
          };
          break;
        case 'compSetup':
          dateField = field.is('[data-date]');
          var objectId = form.attr('data-objectid');
          if(dateField){
            fieldInfo = field.closest('.date-group').find('input[type="hidden"]');
            jsonObj = {
              objectId: objectId,
              value: field.attr('data-date'),
              fieldName: fieldInfo.prop('name')
            };
          } else {
            jsonObj = {
              objectId: objectId,
              fieldName: field.prop('name'),
              value: field.val()
            };
          }
          break;
        case 'assessorFeedback':
          jsonObj = {
            value : field.val()
          };
          break;
        default :
          jsonObj = false;
      }
      return jsonObj;
    },
    getUrl : function(field, form){
      var saveType = form.attr('data-autosave');
      var url;
      var competitionId;

      switch(saveType){
        case 'application':
          var applicationId = jQuery("#application_id").val();
          url ='/application/'+applicationId+'/form/saveFormElement';
          break;
        case 'fundingDecision':
          competitionId = field.attr('data-competition');
          url = '/management/funding/' + competitionId;
          break;
        case 'compSetup':
          competitionId = form.attr('data-competition');
          var section = form.attr('data-section');
          var subsection = form.attr('data-subsection');
          if(subsection != null && subsection != '') {
            url = '/management/competition/setup/' + competitionId + '/section/' + section + '/sub/' + subsection + '/saveFormElement';
          } else {
            url = '/management/competition/setup/' + competitionId + '/section/' + section + '/saveFormElement';
          }
          break;
        case 'assessorFeedback':
          var formInputId = field.closest('.question').prop('id').replace('form-input-', '');
          var assessmentId = form.attr('action').split('/')[2];
          url = '/assessment/'+assessmentId+'/formInput/'+formInputId;
          break;
        default:
          url = false;
      }
      return url;
    },
    processAjax : function(field){
      return function(){
        var form = field.closest('[data-autosave]');
        form.attr('data-save-status', 'progress');
        var data = IFS.core.autoSave.getPostObject(field, form);
        var url = IFS.core.autoSave.getUrl(field, form);
        var defer = jQuery.Deferred();

        if(data === false || url === false){
          defer.resolve();
          return defer.promise();
        }

        var name = field.prop('name');
        var formGroup = field.closest('.form-group');
        var autoSaveInfo = formGroup.find('.autosave-info');
        var startAjaxTime= new Date().getTime();

        if(autoSaveInfo.length === 0){
          formGroup.find('.textarea-footer').append('<span class="autosave-info" />');
          autoSaveInfo = formGroup.find('.autosave-info');
        }

        jQuery.ajaxProtected({
          type: 'POST',
          url: url,
          data: data,
          dataType: "json",
          beforeSend: function() {
            autoSaveInfo.html('Saving...');
          },
          timeout: s.ajaxTimeOut
        })
        .done(function(data){
          var doneAjaxTime = new Date().getTime();
          var remainingWaitingTime = (IFS.core.autoSave.settings.minimumUpdateTime-(doneAjaxTime-startAjaxTime));

          //transform name of costrow for persisting to database

          //disable camelcase as validation_errors is coming from the server
          //would be nice to fix this on the server
          // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
          if(typeof(data.field_id) !== 'undefined') {
            jQuery('body').trigger('persistUnsavedRow', [name, data.field_id]);
          }
          // jscs:enable requireCamelCaseOrUpperCaseIdentifiers

          // set the form-saved-state
          jQuery('body').trigger('updateSerializedFormState');

          //save message
          setTimeout(function(){
            IFS.core.autoSave.populateValidationErrorsOnPageLoad(field);
            IFS.core.autoSave.clearServerSideValidationErrors(field);
            autoSaveInfo.html('Saved!');

            //disable camelcase as validation_errors is coming from the server
            //would be nice to fix this on the server
            // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
            if(typeof(data.validation_errors) !== 'undefined'){
              jQuery.each(data.validation_errors, function(index, value){
                IFS.core.formValidation.setInvalid(field, value);
                serverSideValidationErrors.push(value);
              });
              // jscs:enable requireCamelCaseOrUpperCaseIdentifiers
            }
          }, remainingWaitingTime);
        }).fail(function(jqXHR, data) {
          if(autoSaveInfo.length){
            //ignore incomplete requests, likely due to navigating away from the page
            if (jqXHR.readyState < 4) {
              return true;
            } else {
              var errorMessage = IFS.core.autoSave.getErrorMessage(data);
              autoSaveInfo.html('<span class="error-message">'+errorMessage+'</span>');
            }
          }
        }).always(function(){
          form.attr('data-save-status', 'done');
          defer.resolve();
        });

        return defer.promise();
      };
    },
    getErrorMessage : function(data){
      //when something goes wrong server side this will not show validation messages but system errors like timeouts
      var errorMessage;
      if((typeof(data.responseJson) !== 'undefined') && (typeof(data.responseJson.errorMessage) !== 'undefined')){
        errorMessage = data.responseJson.errorMessage;
      }
      else if(data.statusText == 'timeout'){
        errorMessage = "The server is slow responding, your data is not saved";
      }
      else {
        errorMessage = "Something went wrong when saving your data";
      }
      return errorMessage;
    },
    clearServerSideValidationErrors : function(field){
      for (var i = 0; i < serverSideValidationErrors.length; i++){
        IFS.core.formValidation.setValid(field, serverSideValidationErrors[i]);
      }
    },
    populateValidationErrorsOnPageLoad : function(field){
      var formGroup = field.closest('.form-group.error');
      if(formGroup.find('.error-message').text().length > 0 && serverSideValidationErrors.length === 0){
        var errormsgonLoad = formGroup.find('.error-message').text();
        serverSideValidationErrors.push(errormsgonLoad);
      }
    }

  };
})();
