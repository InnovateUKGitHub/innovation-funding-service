IFS.core.upload = (function () {
  'use strict'
  var s // private alias to settings
  var promise = jQuery.when({})
  var successHandlers = []
  return {
    settings: {
      uploadEl: '[type="file"][class="inputfile"]',
      wrapper: '.ajax-upload',
      uploadButtonName: 'data-js-upload-button-name',
      uploadFileInput: 'data-js-upload-file-input',
      numberOfFiles: 'data-js-number-of-files',
      oneAtATime: 'data-js-upload-one-at-a-time',
      maxSize: 'data-js-max-size',
      enableButtonOnSuccess: 'data-js-enabled-on-file-upload',
      toggleOnSuccess: 'data-js-toggle-on-file-upload',
      successRow: '<li class="success">' +
                    '<div class="file-row">' +
                      '<a href="$href" target="_blank" class="govuk-link">$text (Opens in a new window)</a>' +
                      '<button class="button-clear remove-file">Remove</button>' +
                    '</div>' +
                  '</li>',
      errorRow: '<li class="error">' +
                  '<div class="govuk-error-message">$error The file was not uploaded.</div>' +
                  '<div class="file-row">$text<button class="button-clear remove-file">Remove</button>' +
                  '</div>' +
                '</li>',
      pendingRow: '<li class="pending">' +
                    '<div class="file-row">$text' +
                      '<p class="saving">Uploading<span>.</span><span>.</span><span>.</span></p>' +
                    '</div>' +
                  '</li>',
      uploadView: '<input type="file" id="$id" name="$uploadFileInput" class="inputfile">' +
                  '<label for="$id" class="button-secondary govuk-!-margin-top-6">Upload</label>' +
                  '<button name="$uploadButtonName" class="button-secondary" data-for-file-upload="$id"></button>'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('change', s.uploadEl, function () {
        if (typeof window.FormData !== 'undefined' && jQuery(this).closest(s.wrapper).length) {
          IFS.core.upload.ajaxFileUpload(jQuery(this))
        } else {
          IFS.core.upload.triggerFormSubmission(jQuery(this))
        }
      })
      jQuery('body').on('click', 'button.remove-file', function (e) {
        e.preventDefault()
        IFS.core.upload.removeFile(jQuery(this))
      })
    },
    registerSuccessHandler: function (handler) {
      successHandlers.push(handler)
    },
    ajaxFileUpload: function (fileInput) {
      var wrapper = fileInput.closest(s.wrapper)
      var submitButton = IFS.core.upload.getButton(fileInput)
      var file = fileInput.get(0).files[0]
      IFS.core.upload.clearMessages(wrapper, 'li.error')
      var pendingRow = IFS.core.upload.addMessage(wrapper, IFS.core.template.replaceInTemplate(s.pendingRow, {text: file.name}))

      var formData = new window.FormData(wrapper.closest('form').get(0))
      formData.append(fileInput.attr('name'), file)
      formData.append(submitButton.attr('name'), submitButton.attr('value') || '')

      if (wrapper.get(0).hasAttribute(s.oneAtATime)) {
        promise = promise.then(IFS.core.upload.doAjaxUpload(wrapper, file, pendingRow, formData))
      } else {
        IFS.core.upload.doAjaxUpload(wrapper, file, pendingRow, formData)()
      }
    },
    doAjaxUpload: function (wrapper, file, pendingRow, formData) {
      return function () {
        return jQuery.ajaxProtected({
          type: 'POST',
          url: wrapper.closest('form').attr('action'),
          success: function (data) {
            pendingRow.remove()
            IFS.core.upload.processAjaxResult(wrapper, file, data)
            IFS.core.upload.resetFileInput(wrapper)
          },
          error: function (error) {
            pendingRow.remove()
            console.log(error)
            var errorMessage = 'Internal server error.'
            if (error.status === 413) {
              var maxSize = wrapper.attr(s.maxSize)
              if (maxSize) {
                errorMessage = 'Your upload must be less than ' + maxSize + ' in size.'
              } else {
                errorMessage = 'The file you submitted is too large. Please limit it to the sizes specified.'
              }
            }
            var row = IFS.core.template.replaceInTemplate(s.errorRow, {
              text: file.name,
              error: errorMessage
            })
            IFS.core.upload.addMessage(wrapper, row)
            IFS.core.upload.resetFileInput(wrapper)
          },
          processData: false,
          contentType: false,
          data: formData
        })
      }
    },
    triggerFormSubmission: function (fileInput) {
      IFS.core.upload.getButton(fileInput).click()
    },
    getButton: function (fileInput) {
      var fileInputId = fileInput.attr('id')
      return jQuery('[data-for-file-upload="' + fileInputId + '"]')
    },
    processAjaxResult: function (wrapper, file, data) {
      var html = jQuery(data)
      var errorMessage = html.find('ul.govuk-error-summary__list li')
      var row
      if (errorMessage.length) {
        row = IFS.core.template.replaceInTemplate(s.errorRow, {
          text: file.name,
          error: errorMessage.text()
        })
      } else {
        IFS.core.upload.replaceMessageListWithResponse(wrapper, data, file.name)
        IFS.core.upload.findMatchingDataAttrs(wrapper, s.enableButtonOnSuccess).prop('disabled', false)
        IFS.core.upload.findMatchingDataAttrs(wrapper, s.toggleOnSuccess).toggle()
        jQuery.each(successHandlers, function () {
          this(html, wrapper)
        })
      }
      IFS.core.upload.addMessage(wrapper, row)
      IFS.core.upload.toggleUploadView(wrapper)
    },
    getMessageList: function (wrapper) {
      var messageList = wrapper.find('ul.file-list')
      if (messageList.length) {
        return messageList
      }
      wrapper.find('input:file').before('<ul class="govuk-list file-list"></ul>')
      return wrapper.find('ul.file-list')
    },
    addMessage: function (wrapper, message) {
      var messageList = IFS.core.upload.getMessageList(wrapper)
      var appendable = jQuery(message)
      messageList.append(appendable)
      IFS.core.upload.toggleNoFileMessage(messageList)
      return appendable
    },
    clearMessages: function (wrapper, selector) {
      var messageList = IFS.core.upload.getMessageList(wrapper)
      messageList.find(selector).remove()
      IFS.core.upload.toggleNoFileMessage(messageList)
    },
    toggleNoFileMessage: function (messageList) {
      var noFileMessage = messageList.siblings('p.no-file-uploaded')
      if (messageList.find('li').length) {
        noFileMessage.remove()
      } else {
        messageList.before('<p class="govuk-body no-file-uploaded">No file currently uploaded.</p>')
      }
    },
    toggleUploadView: function (wrapper) {
      var display = wrapper.find('li.success').length < wrapper.attr(s.numberOfFiles)
      if (display) {
        if (!wrapper.find('input:file').length) {
          var guid = IFS.core.template.guidGenerator()
          var html = IFS.core.template.replaceInTemplate(s.uploadView, {
            id: guid,
            uploadButtonName: wrapper.attr(s.uploadButtonName),
            uploadFileInput: wrapper.attr(s.uploadFileInput)
          })
          wrapper.append(html)
        }
      } else {
        wrapper.find('input:file, label, button[data-for-file-upload]')
          .remove()
      }
    },
    removeFile: function (removeButton) {
      var row = removeButton.closest('li')
      var wrapper = row.closest(s.wrapper)
      removeButton.replaceWith('<p class="saving">Removing<span>.</span><span>.</span><span>.</span></p>')
      if (row.hasClass('success')) {
        var removeName = removeButton.attr('name')
        var removeValue = removeButton.attr('value')
        var formData = new window.FormData(wrapper.closest('form').get(0))
        formData.append(removeName, removeValue)
        jQuery.ajaxProtected({
          type: 'POST',
          url: wrapper.closest('form').attr('action'),
          success: function (data) {
            IFS.core.upload.afterRemoveFile(row, wrapper)
            if (!wrapper.find('li').length) {
              IFS.core.upload.findMatchingDataAttrs(wrapper, s.enableButtonOnSuccess).prop('disabled', true)
              IFS.core.upload.findMatchingDataAttrs(wrapper, s.toggleOnSuccess).toggle()
            }
          },
          error: function (error) {
            console.error(error)
          },
          processData: false,
          contentType: false,
          data: formData
        })
      } else {
        IFS.core.upload.afterRemoveFile(row, wrapper)
      }
    },
    /*
        Find elements with data attributes that have a blank value or the id of the wrapper.
     */
    findMatchingDataAttrs: function (wrapper, attr) {
      return jQuery('[' + attr + '=""],[' + attr + '="' + wrapper.attr('id') + '"]')
    },
    afterRemoveFile: function (row, wrapper) {
      var messageList = IFS.core.upload.getMessageList(wrapper)
      row.remove()
      IFS.core.upload.toggleNoFileMessage(messageList)
      IFS.core.upload.toggleUploadView(wrapper)
    },
    resetFileInput: function (wrapper) {
      // wrapper.wrap('<form>').closest('form').get(0).reset()
      // wrapper.unwrap()
    },
    replaceMessageListWithResponse: function (wrapper, data, filename) {
      var index = jQuery(s.wrapper).index(wrapper)
      var wrapperInResponse = jQuery(data).find(s.wrapper).eq(index)
      var messageToInsert = wrapperInResponse.find('.success:last')
      var messageListToInsertInto = IFS.core.upload.getMessageList(wrapper)
      messageListToInsertInto.append(messageToInsert)
    }
  }
})()
