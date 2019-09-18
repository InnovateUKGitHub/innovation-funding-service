IFS.core.autoSave = (function () {
  'use strict'
  var s // private alias to settings
  var promiseList = {}
  var autoSaveTimer

  // we store the last validation message as deleting of messages is done by content as unique identifier.
  // So if we have multiple messages it will only delete the one which contains the message that has been resolved.

  return {
    settings: {
      inputs: '[data-autosave] input:not([type="button"],[type="file"],[readonly="readonly"],[type="hidden"],[data-autosave-disabled])',
      select: '[data-autosave] select:not([readonly="readonly"],[data-autosave-disabled])',
      textareas: '[data-autosave] textarea:not([readonly="readonly"],[data-autosave-disabled])',
      typeTimeout: 500,
      minimumUpdateTime: 200, // the minimum time between the ajax request, and displaying the result of the ajax call.
      ajaxTimeOut: 15000
    },
    init: function () {
      s = this.settings
      jQuery('[data-autosave]').attr('data-save-status', 'done')
      jQuery('body').on('change keyup', s.textareas, function (e) {
        if (e.type === 'keyup') {
          // wait until the user stops typing
          clearTimeout(autoSaveTimer)
          autoSaveTimer = setTimeout(function () { IFS.core.autoSave.fieldChanged(e.target) }, s.typeTimeout)
        } else {
          IFS.core.autoSave.fieldChanged(e.target)
        }
      })
      jQuery('body').on('change', s.inputs + ',' + s.select, function (e) {
        IFS.core.autoSave.fieldChanged(e.target)
      })
      // events for other javascripts
      jQuery('body').on('ifsAutosave', function (e) {
        if (jQuery(e.target).parents('[data-autosave]').size()) {
          IFS.core.autoSave.fieldChanged(e.target)
        }
      })
    },
    fieldChanged: function (element) {
      var field = jQuery(element)

      // per field we handle the request on a promise base, this means that ajax calls should be per field sequental
      // this means we can still have async as two fields can still be processed at the same time
      // http://www.jefferydurand.com/jquery/sequential/javascript/ajax/2015/04/13/jquery-sequential-ajax-promise-deferred.html
      var promiseListName
      if (field.closest('[data-repeatable-row]').length) {
        // make sure repeating rows process sequential per row
        var rowContainer = field.closest('[data-repeatable-row]')
        if (rowContainer.attr('data-repeatable-row').startsWith('unsaved') && field.val() === '') {
          return
        }
        promiseListName = rowContainer.prop('id')
      } else {
        promiseListName = field.prop('name')
      }
      if (typeof (promiseList[promiseListName]) === 'undefined') {
        promiseList[promiseListName] = jQuery.when({}) // fire first promise :)
      }
      promiseList[promiseListName] = promiseList[promiseListName].then(IFS.core.autoSave.processAjax(field))
    },
    getPostObject: function (field, form) {
      // traversing from field as we might get the situation in the future where we have 2 different type autosaves on 1 page within two seperate <form>'s
      var applicationId = jQuery('#application_id').val()
      var saveType = form.attr('data-autosave')
      var digitRegex = /\d+/
      var jsonObj
      var fieldInfo
      var dateField
      var dateValue
      var list
      var listValues
      switch (saveType) {
        case 'application':
          dateField = field.is('[data-date]')
          if (dateField) {
            fieldInfo = field.closest('.date-group').find('input[type="hidden"]:not(.day)')
            dateValue = fieldInfo.attr('data-date-month-year') !== 'undefined' ? field.attr('data-date').substring(2) : field.attr('data-date')
            jsonObj = {
              applicationId: applicationId,
              value: dateValue,
              formInputId: fieldInfo.prop('id'),
              fieldName: fieldInfo.prop('name')
            }
          } else if (!IFS.core.autoSave.isIdAutoSaveApplicantField(field) && digitRegex.test(field.prop('id'))) {
            jsonObj = {
              applicationId: applicationId,
              value: field.val(),
              formInputId: field.prop('id').match(digitRegex)[0],
              fieldName: field.prop('name')
            }
          } else {
            jsonObj = {
              applicationId: applicationId,
              value: field.val(),
              formInputId: field.prop('id'),
              fieldName: field.prop('name')
            }
          }
          break
        case 'fundingDecision':
          jsonObj = {
            applicationId: field.prop('name'),
            fundingDecision: field.val()
          }
          break
        case 'compSetup':
          dateField = field.is('[data-date]')
          list = field.is('[data-autosave-list]')
          var objectId = form.attr('data-objectid')
          if (dateField) {
            fieldInfo = field.closest('.date-group').find('input[type="hidden"]')
            jsonObj = {
              objectId: objectId,
              value: field.attr('data-date'),
              fieldName: fieldInfo.prop('name')
            }
          } else if (list) {
            listValues = jQuery.map(field.closest('fieldset').find(':checkbox:checked'), function (n) {
              return n.value
            }).join(',')
            jsonObj = {
              objectId: objectId,
              fieldName: field.prop('name'),
              value: listValues
            }
          } else {
            jsonObj = {
              objectId: objectId,
              fieldName: field.prop('name'),
              value: field.val()
            }
          }
          break
        case 'assessorFeedback':
          jsonObj = {
            value: field.val()
          }
          break
        case 'autosaveFormPost':
          jsonObj = form.serialize()
          break
        default :
          jsonObj = {
            field: field.attr('name'),
            value: field.val()
          }
      }
      return jsonObj
    },
    getUrl: function (field, form) {
      var saveType = form.attr('data-autosave')
      var url
      var competitionId

      switch (saveType) {
        case 'application':
          var applicationId = jQuery('#application_id').val()
          competitionId = jQuery('#competition_id').val()
          url = '/application/' + applicationId + '/form/' + competitionId + '/saveFormElement'
          break
        case 'fundingDecision':
          competitionId = field.attr('data-competition')
          url = '/management/funding/' + competitionId
          break
        case 'compSetup':
          competitionId = form.attr('data-competition')
          var section = form.attr('data-section')
          var subsection = form.attr('data-subsection')
          if (typeof (subsection) !== 'undefined') {
            url = '/management/competition/setup/' + competitionId + '/section/' + section + '/sub/' + subsection + '/saveFormElement'
          } else {
            url = '/management/competition/setup/' + competitionId + '/section/' + section + '/saveFormElement'
          }
          break
        case 'assessorFeedback':
          var formInputId = field.closest('.question').prop('id').replace('form-input-', '')
          var assessmentId = form.attr('action').split('/')[2]
          url = '/assessment/' + assessmentId + '/formInput/' + formInputId
          break
        case 'autosaveFormPost':
          url = form.attr('action') + '/auto-save'
          break
        default:
          url = saveType
      }
      return url
    },
    processAjax: function (field) {
      return function () {
        var form = field.closest('[data-autosave]')
        form.attr('data-save-status', 'progress')
        var data = IFS.core.autoSave.getPostObject(field, form)
        var url = IFS.core.autoSave.getUrl(field, form)
        var defer = jQuery.Deferred()

        if (data === false || url === false) {
          defer.resolve()
          return defer.promise()
        }

        var name = field.prop('name')
        var formGroup = field.closest('.govuk-form-group')
        var autoSaveInfo = formGroup.find('.autosave-info')
        var startAjaxTime = new Date().getTime()

        if (autoSaveInfo.length === 0) {
          formGroup.find('.textarea-footer').append('<span class="autosave-info" />')
          autoSaveInfo = formGroup.find('.autosave-info')
        }

        jQuery.ajaxProtected({
          type: 'POST',
          url: url,
          data: data,
          dataType: 'json',
          beforeSend: function () {
            autoSaveInfo.html('Saving...')
          },
          timeout: s.ajaxTimeOut
        })
          .done(function (data) {
            var doneAjaxTime = new Date().getTime()
            var remainingWaitingTime = (IFS.core.autoSave.settings.minimumUpdateTime - (doneAjaxTime - startAjaxTime))

            // transform name of costrow for persisting to database
            if (typeof (data.fieldId) !== 'undefined') {
              jQuery('body').trigger('persistUnsavedRow', [name, data.fieldId])
            }

            // set the form-saved-state
            jQuery('body').trigger('updateSerializedFormState')

            // save message
            setTimeout(function () {
              autoSaveInfo.html('Saved!')

              // update the update details section if it exists
              if (jQuery('[data-update-date]').length !== 0) {
                var applicationId = jQuery('[data-update-date]').data('application-id')
                var url = '/application/' + applicationId + '/form/update_time_details'
                // do a replace of the updatedetails based on return of ajax request to correct time and author
                jQuery.get(url, function (fragment) {
                  if (fragment) {
                    jQuery('[data-update-date]').replaceWith(fragment)
                  }
                })
              }
            }, remainingWaitingTime)
          }).fail(function (jqXHR, data) {
            if (autoSaveInfo.length) {
              // ignore incomplete requests, likely due to navigating away from the page
              if (jqXHR.readyState < 4) {
                return true
              } else {
                var errorMessage = IFS.core.autoSave.getErrorMessage(data)
                autoSaveInfo.html('<span class="govuk-error-message">' + errorMessage + '</span>')
              }
            }
          }).always(function () {
            defer.resolve()
            var inProgress = false
            jQuery.each(promiseList, function (key, value) {
              if (inProgress) return
              if (value.state() !== 'resolved') {
                inProgress = true
              }
            })
            if (!inProgress) {
              form.attr('data-save-status', 'done')
            }
          })

        return defer.promise()
      }
    },
    getErrorMessage: function (data) {
      // when something goes wrong server side this will not show validation messages but system errors like timeouts
      var errorMessage
      if ((typeof (data.responseJson) !== 'undefined') && (typeof (data.responseJson.errorMessage) !== 'undefined')) {
        errorMessage = data.responseJson.errorMessage
      } else if (data.statusText === 'timeout') {
        errorMessage = 'The server is slow responding, your data is not saved'
      } else {
        errorMessage = 'Something went wrong when saving your data'
      }
      return errorMessage
    },
    isIdAutoSaveApplicantField: function (field) {
      var id = field.prop('id')
      return id.indexOf('application') !== -1 ||
       id.indexOf('financePosition') !== -1 ||
       id.indexOf('cost') !== -1
    }
  }
})()
