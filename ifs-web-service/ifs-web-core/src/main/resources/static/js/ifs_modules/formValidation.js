IFS.core.formValidation = (function () {
  'use strict'
  var s
  return {
    settings: {
      number: {
        fields: '[type="number"]:not([data-date],[readonly])',
        messageInvalid: 'This field can only accept whole numbers.'
      },
      min: {
        fields: '[min]:not([data-date],[readonly])',
        messageInvalid: 'This field should be %min% or higher.'
      },
      max: {
        fields: '[max]:not([data-date],[readonly])',
        messageInvalid: 'This field should be %max% or lower.'
      },
      range: {
        fields: '[data-range-min]:not([readonly])',
        messageInvalid: 'This field must be between %min% and %max%.'
      },
      passwordPolicy: {
        fields: {
          password: '[name="password"]',
          firstname: '#firstName',
          lastname: '#lastName'
        },
        messageInvalid: {
          tooWeak: 'Password is too weak.',
          containsName: 'Password should not contain either your first or last name.'
        }
      },
      containsLowercase: {
        messageInvalid: 'Password must contain at least one lower case letter.'
      },
      containsUppercase: {
        messageInvalid: 'Password must contain at least one upper case letter.'
      },
      containsNumber: {
        messageInvalid: 'Password must contain at least one number.'
      },
      email: {
        fields: '[type="email"]:not([readonly])',
        messageInvalid: {
          invalid: 'Please enter a valid email address.',
          duplicate: 'The email address is already registered with us. Please sign into your account.'
        }
      },
      required: {
        fields: '[required]:not([data-date],[readonly],[name="password"],.autocomplete__input)',
        messageInvalid: 'This field cannot be left blank.'
      },
      requiredGroup: {
        fields: '[data-required-group]:not([data-date],[readonly],[name="password"])',
        messageInvalid: 'This field cannot be left blank.'
      },
      minlength: {
        fields: '[minlength]:not([readonly],[name="password"])',
        messageInvalid: 'This field should contain at least %minlength% characters.'
      },
      maxlength: {
        fields: '[maxlength]:not([readonly])',
        messageInvalid: 'This field cannot contain more than %maxlength% characters.'
      },
      minwordslength: {
        fields: '[data-minwordslength]',
        messageInvalid: 'This field has a minimum number of words.'
      },
      maxwordslength: {
        fields: '[data-maxwordslength]',
        messageInvalid: 'This field has a maximum number of words.'
      },
      postcode: {
        fields: '[data-postcode-errormessage]:not([readonly])',
        messageInvalid: 'Enter a valid postcode.'
      },
      date: {
        fields: '.date-group input',
        messageInvalid: {
          invalid: 'Please enter a valid date.',
          future: 'Please enter a future date.',
          past: 'Please enter a past date.'
        }
      },
      pattern: {
        fields: '[pattern]:not([readonly])',
        messageInvalid: 'Please correct this field.'
      },
      tel: {
        fields: '[type="tel"]:not([readonly])',
        messageInvalid: 'Please enter a valid phone number between 8 and 20 digits.'
      },
      lowerthan: {
        fields: '[data-lowerthan]',
        messageInvalid: 'The minimum must be smaller than the maximum.'
      },
      higherthan: {
        fields: '[data-higherthan]',
        messageInvalid: 'The maximum must be larger than the minimum.'
      },
      anyChange: {
        fields: '[data-anychange-errormessage]:not([readonly])'
      },
      typeTimeout: 300,
      // data-{{type}}-showmessage will define how the errors will be shown,
      // none = nothing happens and we are just running the check
      // visuallyhidden = in the dom, not visible for users but visible for screen readers
      // show = default behaviour, showing the error message
      displaySettings: ['none', 'visuallyhidden', 'show'],
      html5validationMode: false
    },
    init: function () {
      s = this.settings
      s.html5validationMode = IFS.core.formValidation.checkHTML5validationMode()
      IFS.core.formValidation.initValidation()
    },
    initValidation: function () {
      jQuery('body').on('blur change keyup paste ifsValidate', s.passwordPolicy.fields.password, function (e) {
        var field = jQuery(this)
        switch (e.type) {
          case 'keyup':
            clearTimeout(window.IFS.core.formValidationTimer)
            window.IFS.core.formValidationTimer = setTimeout(function () { IFS.core.formValidation.checkPasswordPolicy(jQuery(field), false) }, s.typeTimeout)
            break
          default:
            IFS.core.formValidation.checkPasswordPolicy(jQuery(field), true)
        }
      })

      jQuery('body').on('change ifsValidate', s.email.fields, function () { IFS.core.formValidation.checkEmail(jQuery(this)) })
      jQuery('body').on('blur change ifsValidate', s.number.fields, function () { IFS.core.formValidation.checkNumber(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.min.fields, function () { IFS.core.formValidation.checkMin(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.max.fields, function () { IFS.core.formValidation.checkMax(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.range.fields, function () { IFS.core.formValidation.checkRange(jQuery(this)) })
      jQuery('body').on('blur change ifsValidate', s.required.fields, function () { IFS.core.formValidation.checkRequired(jQuery(this)) })
      jQuery('body').on('blur change ifsValidate', s.requiredGroup.fields, function () { IFS.core.formValidation.checkRequired(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.minlength.fields, function () { IFS.core.formValidation.checkMinLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.maxlength.fields, function () { IFS.core.formValidation.checkMaxLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.minwordslength.fields, function () { IFS.core.formValidation.checkMinWordsLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.maxwordslength.fields, function () { IFS.core.formValidation.checkMaxWordsLength(jQuery(this)) })
      jQuery('body').on('blur change ifsValidate', s.postcode.fields, function () { IFS.core.formValidation.checkPostcode(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.tel.fields, function () { IFS.core.formValidation.checkTel(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.date.fields, function () { IFS.core.formValidation.checkDate(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.pattern.fields, function () { IFS.core.formValidation.checkPattern(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.lowerthan.fields, function () { IFS.core.formValidation.checkLowerThan(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.higherthan.fields, function () { IFS.core.formValidation.checkHigherThan(jQuery(this)) })
      jQuery('body').on('change', s.anyChange.fields, function () { IFS.core.formValidation.anyChange(jQuery(this)) })

      jQuery('body').on('change', '[data-set-section-valid]', function () {
        var section = jQuery(this).attr('data-set-section-valid')
        IFS.core.formValidation.setSectionValid(section)
      })
      // set data attribute on date fields
      // which has the combined value of the dates
      // and also makes sure that other vaidation doesn't get triggered
      jQuery(s.date.fields).attr({'data-date': '', 'data-autosave-disabled': ''})

      // will only work on html5 validation browsers
      jQuery('form:not([novalidate]) input').on('invalid', function () {
        jQuery(this).trigger('change')
      })

      IFS.core.formValidation.initFocusActions()
      jQuery('body').on('click', '.govuk-error-summary__list a', function (e) {
        e.preventDefault()
        IFS.core.formValidation.errorSummaryLinksClick(this)
      })
      IFS.core.formValidation.initDetailsErrors()
    },
    checkPasswordPolicy: function (field, errorStyles) {
      //  clear tooWeakPassword and containsName message as this is validated in the back end.
      IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-tooWeak', 'visuallyhidden'), 'show')
      IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-containsName', 'visuallyhidden'), 'show')
      // clear the customError if it has been set by a server validation error
      if (s.html5validationMode && field[0].validity.customError) {
        field[0].setCustomValidity('')
      }
      var hasUppercase = IFS.core.formValidation.checkFieldContainsUppercase(field)
      var hasLowercase = IFS.core.formValidation.checkFieldContainsLowercase(field)
      var hasNumber = IFS.core.formValidation.checkFieldContainsNumber(field)
      var isMinlength = IFS.core.formValidation.checkMinLength(field)
      var isFilledOut = IFS.core.formValidation.checkRequired(field)
      var formGroup = field.closest('.govuk-form-group')
      var conformsToPasswordPolicy = hasUppercase && hasLowercase && hasNumber && isMinlength && isFilledOut
      if (errorStyles) {
        if (conformsToPasswordPolicy) {
          formGroup.removeClass('govuk-form-group--error')
          field.removeClass('govuk-input--error')
        } else {
          formGroup.addClass('govuk-form-group--error')
          field.addClass('govuk-input--error')
        }
      }
      return conformsToPasswordPolicy
    },
    checkFieldContainsUppercase: function (field) {
      var fieldVal = field.val()
      var uppercaseDataAttribute = 'containsUppercase'

      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, uppercaseDataAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, uppercaseDataAttribute)

      var uppercase = /(?=\S*?[A-Z])/
      var hasUppercase = uppercase.test(fieldVal) !== false

      IFS.core.formValidation.setStatus(field, uppercaseDataAttribute, hasUppercase)

      if (hasUppercase) {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      }
    },
    checkFieldContainsLowercase: function (field) {
      var fieldVal = field.val()
      var lowercaseDataAttribute = 'containsLowercase'

      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, lowercaseDataAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, lowercaseDataAttribute)

      var lowercase = /(?=\S*?[a-z])/
      var hasLowercase = lowercase.test(fieldVal) !== false

      IFS.core.formValidation.setStatus(field, lowercaseDataAttribute, hasLowercase)

      if (hasLowercase) {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      }
    },
    checkFieldContainsNumber: function (field) {
      var fieldVal = field.val()
      var numberDataAttribute = 'containsNumber'

      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, numberDataAttribute)
      var numberErrorMessage = IFS.core.formValidation.getErrorMessage(field, numberDataAttribute)
      var number = /(?=\S*?[0-9])/
      var hasNumber = number.test(fieldVal) !== false

      IFS.core.formValidation.setStatus(field, numberDataAttribute, hasNumber)

      if (hasNumber) {
        IFS.core.formValidation.setValid(field, numberErrorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(field, numberErrorMessage, displayValidationMessages)
        return false
      }
    },
    checkEmail: function (field) {
      // checks if the email is valid, the almost rfc compliant check. The same as the java check, see http://www.regular-expressions.info/email.html
      var email = field.val()
      var invalidEmailAttribute = 'email-invalid'
      var duplicateEmailAttribute = 'email-duplicate'
      // disabled escape js-standard message, we might want to solve this in the future by cleaning up the regex
      var invalidErrorMessage = IFS.core.formValidation.getErrorMessage(field, invalidEmailAttribute)
      var duplicateErrorMessage = IFS.core.formValidation.getErrorMessage(field, duplicateEmailAttribute)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, 'email')
      var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i // eslint-disable-line

      // check if email value exists to avoid invalid email message on empty fields
      if (email) {
        var validEmail = emailRegex.test(email)

        // check if email address is invalid
        if (!validEmail) {
          IFS.core.formValidation.setInvalid(field, invalidErrorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, invalidErrorMessage, displayValidationMessages)
          // also set the duplicate email field to valid
          IFS.core.formValidation.setValid(field, duplicateErrorMessage, displayValidationMessages)
          return true
        }
      } else {
        IFS.core.formValidation.setValid(field, invalidErrorMessage, displayValidationMessages)
        // also set the duplicate email field to valid
        IFS.core.formValidation.setValid(field, duplicateErrorMessage, displayValidationMessages)
        return true
      }
    },
    checkNumber: function (field) {
      var numberAttribute = 'number'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, numberAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'number')
      var value = field.val()
      // In modern browsers the number field doesn't allow text input
      // When inserting a string like "test" the browser converts this to an empty string "" (this is the specced behaviour)
      // An empty string is returned as true therefore
      // http://stackoverflow.com/questions/18852244/how-to-get-the-raw-value-an-input-type-number-field
      if (s.html5validationMode) {
        var domField = field[0]
        var containsExponential = value.indexOf('e') !== -1
        if (domField.validity.badInput === true || domField.validity.stepMismatch === true || containsExponential) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        // old browser mode
        // https://api.jquery.com/jQuery.isNumeric for what this checks
        var wholeNumber = (value.indexOf(',') === -1) && (value.indexOf('.') === -1) && (value.indexOf('e') === -1)
        if (!jQuery.isNumeric(value) || !wholeNumber) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      }
    },
    checkMax: function (field) {
      var maxAttribute = 'max'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, maxAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, maxAttribute)

      if (s.html5validationMode) {
        // html5 validation api
        var domField = field[0]
        if (domField.validity.rangeOverflow === true) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        // classic mode
        var max = parseInt(field.attr('max'), 10)
        if (IFS.core.formValidation.checkNumber(field, true)) {
          var fieldVal = parseInt(field.val(), 10)
          if (fieldVal > max) {
            IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
            return false
          } else {
            IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
            return true
          }
        }
      }
    },
    checkMin: function (field) {
      var minAttribute = 'min'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, minAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, minAttribute)

      if (s.html5validationMode) {
        var domField = field[0]
        if (domField.validity.rangeUnderflow === true) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        var min = parseInt(field.attr('min'), 10)
        if (IFS.core.formValidation.checkNumber(field)) {
          var fieldVal = parseInt(field.val(), 10)
          if (fieldVal < min) {
            IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
            return false
          } else {
            IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
            return true
          }
        }
      }
    },
    checkRange: function (field) {
      var rangeAttribute = 'range'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, rangeAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, rangeAttribute)
      var min = parseInt(field.data('range-min'), 10)
      var max = parseInt(field.data('range-max'), 10)
      if (IFS.core.formValidation.checkNumber(field)) {
        var fieldVal = parseInt(field.val(), 10)
        if (fieldVal < min || fieldVal > max) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      }
    },
    anyChange: function (field) {
      jQuery.each(field[0].attributes, function (index, attribute) {
        if (attribute.name.indexOf('data-anychange-errormessage') === 0) {
          IFS.core.formValidation.setValid(field, attribute.value, 'show')
        }
      })
    },
    checkRequired: function (field) {
      var requiredAttribute = 'required'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, requiredAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, requiredAttribute)
      if (field.val() !== null) {
        var value = field.val()
        if (field.is(':checkbox,:radio')) {
          var groupID
          var groupIDtype
          if (typeof (field.attr('data-required-group')) !== 'undefined') {
            groupID = field.attr('data-required-group')
            groupIDtype = 'data-required-group'
          } else if (typeof (field.attr('name')) !== 'undefined') {
            groupID = field.attr('name')
            groupIDtype = 'name'
          } else {
            return
          }

          var fieldGroup = jQuery('[' + groupIDtype + '="' + groupID + '"]')
          if (jQuery('[' + groupIDtype + '="' + groupID + '"]:checked').length === 0) {
            fieldGroup.each(function () { IFS.core.formValidation.setInvalid(jQuery(this), errorMessage, displayValidationMessages) })
            return false
          } else {
            fieldGroup.each(function () { IFS.core.formValidation.setValid(jQuery(this), errorMessage, displayValidationMessages) })
            return true
          }

        // HTML5 number input will return "" as val() if invalid number.
        } else if (field.is(s.number.fields) && s.html5validationMode && field[0].validity.badInput) {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        } else if (field.is('select')) {
          // check if we are a group of select elements
          var selectGroup = field.closest('.govuk-form-group').find('select')
          var valid = true
          if (selectGroup.length > 1) {
            // a group of select elements
            // check if any of the select elements are invalid
            selectGroup.each(function () {
              if (jQuery(this).val().length === 0) {
                valid = false
                return false
              }
            })
            if (!valid) {
              selectGroup.each(function () { IFS.core.formValidation.setInvalid(jQuery(this), errorMessage, displayValidationMessages) })
              return false
            } else {
              selectGroup.each(function () { IFS.core.formValidation.setValid(jQuery(this), errorMessage, displayValidationMessages) })
              return true
            }
          } else {
            // single select element
            // check if the value has any characters OR if the value only contains spaces
            if (value.length === 0) {
              IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
              return false
            } else {
              IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
              return true
            }
          }
        } else {
          // check if the value has any characters OR if the value only contains spaces
          if (value.length === 0 || !value.trim()) {
            IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
            return false
          } else {
            IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
            return true
          }
        }
      }
    },
    checkMinLength: function (field) {
      var minLengthAttribute = 'minlength'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, minLengthAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, minLengthAttribute)
      var minLength = parseInt(field.attr('minlength'), 10)
      var duplicateRequiredMinLength = field.data('duplicate-required-minlength')
      var fieldVal = field.val()

      // Check if we need to validate the min length and required
      var validMinLength = duplicateRequiredMinLength ? (fieldVal.length > 0) && (fieldVal.length >= minLength) : (fieldVal.length === 0) || (fieldVal.length >= minLength)

      IFS.core.formValidation.setStatus(field, minLengthAttribute, validMinLength)
      if (validMinLength) {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      }
    },
    checkMaxLength: function (field) {
      var maxLengthAttribute = 'maxlength'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, maxLengthAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, maxLengthAttribute)
      var maxLength = parseInt(field.attr('maxlength'), 10)
      if (field.val().length > maxLength) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkMinWordsLength: function (field) {
      var minWordsLengthAttribute = 'minwordslength'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, minWordsLengthAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, minWordsLengthAttribute)
      var minWordsLength = parseInt(field.attr(minWordsLengthAttribute), 10)
      var value = field.val()
      var words = IFS.core.formValidation.countWords(value)

      if ((words.length > 0) && (words.length < minWordsLength)) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkMaxWordsLength: function (field) {
      var maxWordsLengthAttribute = 'maxwordslength'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, maxWordsLengthAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, maxWordsLengthAttribute)
      var maxWordsLength = parseInt(field.attr('data-' + maxWordsLengthAttribute), 10)
      var value = field.val()
      var words = IFS.core.formValidation.countWords(value)

      if (words.length > maxWordsLength) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkPostcode: function (field) {
      // matches postcode validation in ApplicationSectionController.java
      var postcodeAttribute = 'postcode'
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, postcodeAttribute)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, postcodeAttribute)
      var re = /^.{3,10}$/

      var postcode = field.val()
      var validPostcode = re.test(postcode)

      if (!validPostcode) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkTel: function (field) {
      var telAttribute = 'tel'
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, telAttribute)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, telAttribute)
      var re = /^$|^[\\)\\(\\+\s-]*(?:\d[\\)\\(\\+\s-]*){8,20}$/

      var tel = field.val()
      var validPhone = re.test(tel)

      if (!validPhone) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkDate: function (field) {
      var dateGroup = field.closest('.date-group')
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, 'date')

      field.addClass('js-visited')
      var valid

      var day = dateGroup.find('.day input')
      var month = dateGroup.find('.month input')
      var year = dateGroup.find('.year input')
      var hour
      if (field.closest('tr').find('.time select').length > 0) {
        hour = dateGroup.find('.time option[data-time]:selected').attr('data-time')
      } else {
        hour = dateGroup.find('.time [data-time]').attr('data-time')
      }
      var addWeekDay = dateGroup.find('.js-addWeekDay')

      var allFields = day.add(month).add(year)
      var allFieldsArray = jQuery.makeArray(allFields)
      var fieldsVisited = (day.hasClass('js-visited') && month.hasClass('js-visited') && year.hasClass('js-visited'))
      var filledOut = allFieldsArray.every(function (element) { return jQuery(element).val().length > 0 })
      var enabled = !day.is('[readonly]') || !month.is('[readonly]') || !year.is('[readonly]')
      var required = (day.attr('required') && month.attr('required') && year.attr('required'))
      var empty = allFieldsArray.every(function (element) { return jQuery(element).val().length === 0 })
      var errorSummary = jQuery('.govuk-error-summary')
      // don't show the validation messages for numbers in dates but we do check it as part of the date check
      allFields.attr({
        'data-number-showmessage': 'none',
        'data-min-showmessage': 'none',
        'data-max-showmessage': 'none'
      })
      var validNumbers = allFieldsArray.every(function (element) { return IFS.core.formValidation.checkNumber(jQuery(element)) }) &&
        IFS.core.formValidation.checkMin(year) && IFS.core.formValidation.checkMax(year)

      var invalidErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-invalid')

      if (validNumbers && filledOut) {
        var validDay = day.length ? parseInt(day.val(), 10) : 1
        var validMonth = parseInt(month.val(), 10)
        var validYear = parseInt(year.val(), 10)
        var date = new Date(validYear, validMonth - 1, validDay) // parse as date to check if it is a valid date
        if (hour !== undefined) {
          date.setHours(hour, 0, 0, 0)
        } else {
          date.setHours(0, 0, 0, 0)
        }

        if ((date.getDate() === validDay) && (date.getMonth() + 1 === validMonth) && (date.getFullYear() === validYear)) {
          valid = true
          IFS.core.formValidation.setValid(allFields, invalidErrorMessage, displayValidationMessages)

          allFields.attr('data-date', validDay + '-' + validMonth + '-' + validYear)
          // adding day of week which is not really validation
          // so could be better of somewhere else
          if (addWeekDay.length) {
            var days = ['Sun', 'Mon', 'Tues', 'Wed', 'Thurs', 'Fri', 'Sat']
            var weekday = days[date.getDay()]
            addWeekDay.text(weekday)
          }
          if (enabled) {
            field.trigger('ifsAutosave')

            if (dateGroup.is('[data-future-date]')) {
              valid = IFS.core.formValidation.checkFutureDate(dateGroup, date, displayValidationMessages)
            }
            if (dateGroup.is('[data-past-date]')) {
              valid = IFS.core.formValidation.checkPastDate(dateGroup, date, displayValidationMessages)
            }
          }
        } else {
          if (enabled) {
            IFS.core.formValidation.setInvalid(allFields, invalidErrorMessage, displayValidationMessages)
            allFields.attr({'data-date': ''})
            valid = false
          }
        }
      } else if (errorSummary.length) {
        IFS.core.formValidation.setInvalid(allFields, invalidErrorMessage, displayValidationMessages)
        valid = false
      } else if (empty && enabled) {
        field.trigger('ifsAutosave')
        if (!required) {
          valid = true
          IFS.core.formValidation.setValid(allFields, invalidErrorMessage, displayValidationMessages)
        }
      } else if ((filledOut || fieldsVisited) && enabled) {
        IFS.core.formValidation.setInvalid(allFields, invalidErrorMessage, displayValidationMessages)
        allFields.attr({'data-date': ''})
        valid = false
      } else {
        valid = false
      }

      if (!valid && addWeekDay.length) {
        addWeekDay.text('-')
      }

      return valid
    },
    checkFutureDate: function (dateGroup, date, displayValidationMessages) {
      var futureErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-future')
      var allFields = dateGroup.find('.day input, .month input, .year input')
      var futureDate
      if (jQuery.trim(dateGroup.attr('data-future-date')).length === 0) {
        // if no future date is set we assume tomorrow
        futureDate = new Date()
        futureDate.setDate(futureDate.getDate() + 1)
      } else {
        var futureDateString = dateGroup.attr('data-future-date').split('-')
        var futureDay = parseInt(futureDateString[0], 10)
        var futureMonth = parseInt(futureDateString[1], 10) - 1
        var futureYear = parseInt(futureDateString[2], 10)
        var futureHour = futureDateString.length > 2 ? parseInt(futureDateString[3], 10) : false
        futureDate = new Date(futureYear, futureMonth, futureDay)
      }
      if (futureHour) {
        futureDate.setHours(futureHour, 0, 0, 0)
      } else {
        futureDate.setHours(0, 0, 0, 0)
      }

      if (futureDate <= date) {
        IFS.core.formValidation.setValid(allFields, futureErrorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(allFields, futureErrorMessage, displayValidationMessages)
        return false
      }
    },
    checkPastDate: function (dateGroup, date, displayValidationMessages) {
      var pastErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-past')
      var allFields = dateGroup.find('.day input, .month input, .year input')
      var pastDate = new Date()
      if (pastDate.setHours(0, 0, 0, 0) >= date.setHours(0, 0, 0, 0)) {
        IFS.core.formValidation.setValid(allFields, pastErrorMessage, displayValidationMessages)
        return true
      } else {
        IFS.core.formValidation.setInvalid(allFields, pastErrorMessage, displayValidationMessages)
        return false
      }
    },
    checkPattern: function (field) {
      var patternAttribute = 'pattern'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, patternAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, patternAttribute)
      if (s.html5validationMode) {
        var domField = field[0]
        if (domField.validity.patternMismatch) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        var regex = field.attr('pattern')
        var regexObj = new RegExp(regex)
        if (!regexObj.test(field.val())) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      }
    },
    checkLowerThan: function (field) {
      var attribute = 'lowerthan'
      var lowerThanAttribute = 'higherthan'
      var lowerThan = field.data(attribute)
      var lowerThanField = jQuery('#' + lowerThan)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, attribute)
      var displayValidationMessagesLowerThan = IFS.core.formValidation.getMessageDisplaySetting(lowerThanField, lowerThanAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, attribute)
      var errorMessageLowerThan = IFS.core.formValidation.getErrorMessage(lowerThanField, lowerThanAttribute)

      var maximumValue = parseInt(lowerThanField.val())
      if (parseInt(field.val()) > maximumValue) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        IFS.core.formValidation.setInvalid(lowerThanField, errorMessageLowerThan, displayValidationMessagesLowerThan)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        IFS.core.formValidation.setValid(lowerThanField, errorMessageLowerThan, displayValidationMessagesLowerThan)
        return true
      }
    },
    checkHigherThan: function (field) {
      var attribute = 'higherthan'
      var higherThanAttribute = 'lowerthan'
      var higherThan = field.data(attribute)
      var higherThanField = jQuery('#' + higherThan)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, attribute)
      var displayValidationMessagesHigherThan = IFS.core.formValidation.getMessageDisplaySetting(higherThanField, higherThanAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, attribute)
      var errorMessageHigherThan = IFS.core.formValidation.getErrorMessage(higherThanField, higherThanAttribute)

      var minimumValue = parseInt(higherThanField.val())
      if (parseInt(field.val()) < minimumValue) {
        IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
        IFS.core.formValidation.setInvalid(higherThanField, errorMessageHigherThan, displayValidationMessagesHigherThan)
        return false
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        IFS.core.formValidation.setValid(higherThanField, errorMessageHigherThan, displayValidationMessagesHigherThan)
        return true
      }
    },
    getErrorMessage: function (field, type) {
      // first look if there is a custom message defined on the element
      var errorMessage = field.attr('data-' + type + '-errormessage')

      // support for submessages for one type (i.e date invalid / date future) and different password policys
      var subtype
      if (type.indexOf('-') !== -1) {
        var types = type.split('-')
        type = types[0]
        subtype = types[1]
      } else {
        subtype = false
      }

      // if there is no data-errormessage we use the default messagging defined in the settings object
      if (typeof (errorMessage) === 'undefined') {
        if (subtype === false) {
          errorMessage = s[type].messageInvalid
        } else {
          errorMessage = s[type].messageInvalid[subtype]
        }
      }
      // replace value so we can have text like; this cannot be under %max%
      if (errorMessage.indexOf('%' + type + '%') !== -1) {
        errorMessage = errorMessage.replace('%' + type + '%', field.attr(type))
      }
      return errorMessage
    },
    getMessageDisplaySetting: function (field, fieldattribute) {
      var display = field.is('[data-' + fieldattribute + '-showmessage]') ? field.attr('data-' + fieldattribute + '-showmessage') : 'show'
      return display
    },
    setInvalid: function (field, message, displayValidationMessages) {
      var validShowMessageValue = jQuery.inArray(displayValidationMessages, s.displaySettings) !== -1
      if (validShowMessageValue === false || displayValidationMessages === 'none') {
        return
      }

      var formInTable = field.parents('.form-in-table').length > 0

      var formGroup = field.closest('.govuk-form-group')
      var formGroupRow = field.closest('.form-group-row')
      var formGroupRowValidated = field.closest('.form-group-row-validated')
      var name = IFS.core.formValidation.getName(field)
      var id = IFS.core.formValidation.getIdentifier(field)

      var visuallyhidden = displayValidationMessages === 'visuallyhidden'

      if (formGroup.length) {
        if (s.html5validationMode) { field[0].setCustomValidity(message) }
        if (visuallyhidden === false) {
          formGroup.addClass('govuk-form-group--error')
          if (formInTable) {
            field.closest('.form-in-table').addClass('govuk-form-group--error')
          }
        }
        var errorEl = formGroup.find('.govuk-error-message:contains("' + message + '")')
        if (errorEl.length === 0) {
          if (visuallyhidden === false) {
            field.addClass('govuk-input--error')
          }
          formGroup.find('legend,label').first().after('<span class="govuk-error-message' + (visuallyhidden ? ' govuk-visually-hidden' : '') + '">' + message + '</span>')
        }
      }

      if (formGroupRowValidated) {
        if (visuallyhidden === false) { formGroupRowValidated.addClass('govuk-form-group--error') }
      }
      if (formGroupRow.length) {
        if (s.html5validationMode) { field[0].setCustomValidity(message) }
        if (visuallyhidden === false) { formGroupRow.addClass('govuk-form-group--error') }

        var linkedErrorEl = formGroupRow.find('[data-errorfield="' + name + '"]:contains("' + message + '")')
        if (linkedErrorEl.length === 0) {
          if (visuallyhidden === false) { field.addClass('govuk-input--error') }
          formGroupRow.find('legend,label,[scope="row"]').first().append('<span data-errorfield="' + name + '" class="govuk-error-message' + (visuallyhidden ? ' govuk-visually-hidden' : '') + '">' + message + '</span>')
        }
      }

      if (id.length) {
        if (jQuery('.govuk-error-summary__list [href="#' + id + '"]:contains(' + message + ')').length === 0) {
          jQuery('.govuk-error-summary__list').append('<li><a href="#' + id + '">' + message + '</a></li>')
        }
      } else {
        if (jQuery('.govuk-error-summary__list li:contains(' + message + ')').length === 0) {
          jQuery('.govuk-error-summary__list').append('<li>' + message + '</li>')
        }
      }

      jQuery('.govuk-error-summary:not([data-ignore-errors])').attr('aria-hidden', false)
      jQuery(window).trigger('updateWysiwygPosition')
      setTimeout(function () {
        IFS.core.sortingErrors.sortList()
      }, 200)
    },
    setValid: function (field, message, displayValidationMessages) {
      var validShowMessageValue = jQuery.inArray(displayValidationMessages, s.displaySettings) !== -1
      if (validShowMessageValue === false || displayValidationMessages === 'none') {
        return
      }

      var formInTable = field.parents('.form-in-table').length > 0
      var formInTableErrors = field.parents('.form-in-table').find('.govuk-input--error').length

      var formGroup = field.closest('.govuk-form-group')
      var formGroupRow = field.closest('.form-group-row')
      var formGroupRowValidated = field.closest('.form-group-row-validated')
      var errorSummary = jQuery('.govuk-error-summary__list')
      var name = IFS.core.formValidation.getName(field)
      var id = IFS.core.formValidation.getIdentifier(field)
      // if it is a .govuk-form-group we assume the basic form structure with just one field per group
      // i.e.
      // <div class="govuk-form-group">
      //      <label class="govuk-label" for="field1">
      //          FieldLabel
      //      </label>
      //      <span class="govuk-error-message">This field cannot be empty</span>
      //      <input class="govuk-input govuk-input--error" name="field1" id="field1" required />
      // </div>
      if (formGroup.length) {
        formGroup.find('.govuk-error-message:contains("' + message + '")').remove()
        // if this was the last error we remove the error styling
        if (formGroup.find('.govuk-error-message').length === 0) {
          formGroup.removeClass('govuk-form-group--error')
          field.removeClass('govuk-input--error')
          if (formInTable && formInTableErrors === 0) {
            field.closest('.form-in-table').removeClass('govuk-form-group--error')
          }
          // set corresponding radios/checkboxes valid
          if (s.html5validationMode) {
            jQuery('[name="' + name + '"]').each(function () { this.setCustomValidity('') })
          }
        }
      }
      if (formGroupRowValidated.length && formGroupRowValidated.find('.govuk-input--error').length === 0) {
        formGroupRowValidated.removeClass('govuk-form-group--error')
      }

      // if it is a .form-group-multiple there can be multiple fields within the group, all having there own validation but reporting to one label
      // the template has to output server side error messages linked to the field
      // i.e. a table
      // <tr class="form-group-row govuk-form-group--error">
      //     <th scope="row" id="rowlabel">
      //          <span>The label of this row</span>
      //          <span class="govuk-error-message" data-errorfield="field1">This field cannot be empty</span>
      //          <span class="govuk-error-message" data-errorfield="field2">This field cannot be empty</span>
      //    </th>
      //     <td><input aria-labelledby="rowlabel" type="text" name="field1" class="govuk-input govuk-input--error" required /></td>
      //     <td><input aria-labelledby="rowlabel" type="text" name="field2" class="govuk-input govuk-input--error" required /></td>
      // </tr>
      if (formGroupRow.length) {
        formGroupRow.find('[data-errorfield="' + name + '"]:contains(' + message + ')').remove()
        if (formGroupRow.find('[data-errorfield="' + name + '"]').length === 0) {
          field.removeClass('govuk-input--error')
        }
        if ((formGroupRow.find('[data-errorfield="' + name + '"]').length === 0) && (s.html5validationMode)) {
          jQuery('[name="' + name + '"]').each(function () { this.setCustomValidity('') })
        }
        if (formGroupRow.find('[data-errorfield]').length === 0) {
          formGroupRow.removeClass('govuk-form-group--error')
        }
      }
      // updating the error summary
      if (errorSummary.length) {
        if (id.length) {
          errorSummary.find('[href="#' + id + '"]:contains(' + message + ')').parent().remove()
        } else {
          errorSummary.find('li:contains(' + message + ')').remove()
        }
        if (jQuery('.govuk-error-summary__list li:not(.list-header)').length === 0) {
          jQuery('.govuk-error-summary__list li.list-header').remove()
          jQuery('.govuk-error-summary:not([data-ignore-errors])').attr('aria-hidden', 'true')
        }
      }

      jQuery(window).trigger('updateWysiwygPosition')
    },
    setSectionValid: function (section) {
      section = jQuery(section)
      section.removeClass('error')
      var inputs = section.find('.govuk-input--error')

      //  remove error messages from section + error summary
      section.find('.govuk-error-message').each(function () {
        var errorMessage = jQuery(this)
        var content = errorMessage.text()
        jQuery('.govuk-error-summary__list li:contains(' + content + ')').first().remove()
        errorMessage.remove()
      })

      jQuery.each(inputs, function () {
        jQuery(this).removeClass('govuk-input--error').val('')
        if (s.html5validationMode) {
          this.setCustomValidity('')
        }
      })
    },
    setStatus: function (field, type, status) {
      var formGroup = field.closest('.govuk-form-group,tr.form-group-row')
      var statusAttribute = 'data-' + type + '-validationStatus'
      var statusElements = formGroup.find('[' + statusAttribute + ']')
      status = status.toString()

      if (statusElements.length) {
        jQuery.each(statusElements, function () {
          jQuery(this).attr('data-valid', status)
        })
      }
    },
    getName: function (el) {
      if (el.is('[data-date]')) {
        el = el.closest('.date-group,fieldset').find('input[type="hidden"]')
      }
      if (typeof (el.attr('name')) !== 'undefined') {
        return el.attr('name')
      } else {
        return IFS.core.formValidation.getIdentifier(el)
      }
    },
    getIdentifier: function (el) {
      var formGroupRow = el.closest('.form-group-row')
      if (el.is(':radio') || el.is(':checkbox')) {
        // Ifn it is a radio/checkbox group (so more than one)
        // Then we use the legend as id otherwise just the field id
        var name = el.attr('name')
        var linkedGroupName = el.attr('data-required-group')
        if (jQuery('[name="' + name + '"]').length > 1 || jQuery('[data-required-group="' + linkedGroupName + '"]').length > 1) {
          el = el.closest('fieldset').find('legend')
        }
      }
      if (el.is('[data-date]') && formGroupRow.length) {
        el = el.closest('.form-group-row').find('legend')
      } else if (el.is('[data-date]')) {
        el = el.closest('fieldset').find('legend')
      }
      if (typeof (el.attr('id')) !== 'undefined') {
        return el.attr('id')
      }
      return ''
    },
    checkHTML5validationMode: function () {
      var testField = jQuery('input')
      if (testField.length) {
        if (typeof (testField[0].validity) !== 'undefined') {
          return true
        } else {
          return false
        }
      }
    },
    countWords: function (value) {
      var val = value

      if (typeof (val) !== 'undefined') {
        // regex = replace newlines with space \r\n, \n, \r
        val = value.replace(/(\r\n|\n|\r)/gm, ' ')
        // remove markdown lists ('* ','1. ','2. ','**','_') from markdown as it influences word count

        // disabled escape js-standard message, we might want to solve this in the future by cleaning up the regex
        val = value.replace(/([[0-9]+\.\ |\*\ |\*\*|_)/gm, '') // eslint-disable-line

        return jQuery.trim(val).split(' ')
      } else {
        return false
      }
    },
    initFocusActions: function () {
      // If there is an error summary, set focus to the summary
      var errorSummary = jQuery('.govuk-error-summary')
      if (errorSummary.length) {
        errorSummary.focus()
      } else {
        // Otherwise, set focus to the field with the error
        jQuery('.govuk-form-group--error input:not([type="hidden"])').first().focus()
      }
    },
    errorSummaryLinksClick: function (el) {
      var id = IFS.core.formValidation.removeHash(jQuery(el).attr('href'))
      var target = jQuery('[id="' + id + '"]')
      var targetVisible = IFS.core.formValidation.isVisible(target)
      var closedAccordion = target.closest('.govuk-accordion__section').not('.govuk-accordion__section--expanded')
      var closedDetails = target.closest('.govuk-details__text').not('[aria-hidden="false"]')
      var formGroupRow = target.closest('.form-group-row')
      if (targetVisible && formGroupRow.length) {
        // it is part a date group so don't put focus on the time select
        IFS.core.formValidation.scrollToElement(formGroupRow.find('input[type!=hidden]').first())
      } else if (targetVisible) {
        IFS.core.formValidation.scrollToElement(target.first())
      } else if (closedAccordion.length) {
        // it is within an accordion element and we open it and then put focus on it
        closedAccordion.addClass('govuk-accordion__section--expanded')
        IFS.core.formValidation.scrollToElement(target.first())
      } else if (closedDetails.length) {
        // it is within a detail element and we open it and then put focus on it
        var detailsWrapper = closedDetails.closest('.govuk-details')
        var summary = closedDetails.closest('.govuk-details__summary')
        detailsWrapper.attr('open', '')
        summary.attr('aria-expanded', 'true')
        closedDetails.attr('aria-hidden', 'false')
        IFS.core.formValidation.scrollToElement(target.first())
      } else {
        // if the target is invisible we put focus on an element that has the same label as the target
        // An example of this usecase is the wysiwyg editor
        var altTarget = jQuery('[aria-labelledby="' + id + '"]')
        var altTargetVisible = IFS.core.formValidation.isVisible(altTarget)
        if (altTargetVisible) {
          IFS.core.formValidation.scrollToElement(altTarget.first())
        }
      }
    },
    initDetailsErrors: function () {
      var details = jQuery('.govuk-details')
      details.each(function () {
        if (jQuery(this).find('.govuk-form-group--error').length) {
          var detailsSummary = jQuery(this).find('.govuk-details__summary')
          var detailsText = jQuery(this).find('.govuk-details__text')
          details.attr('open', '')
          detailsSummary.attr('aria-expanded', 'true')
          detailsText.attr('aria-hidden', 'false')
        }
      })
    },
    scrollToElement: function (el) {
      jQuery('html, body').animate({
        scrollTop: el.offset().top - (jQuery(window).height() / 2)
      }, {
        complete: function () {
          el.focus()
        }
      })
    },
    isVisible: function (el) {
      return !(el.is('[aria-hidden="true"]') || el.is(':visible') === false || el.length === 0)
    },
    removeHash: function (href) {
      if (href.indexOf('#') === 0) {
        return href.substring(1, href.length)
      }
      return href
    }
  }
})()
