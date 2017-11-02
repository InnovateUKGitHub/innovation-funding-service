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
      passwordPolicy: {
        fields: {
          password: '[name="password"]',
          firstname: '#firstName',
          lastname: '#lastName'
        },
        messageInvalid: {
          tooWeak: 'Password is too weak.'
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
        messageInvalid: 'Please enter a valid email address.'
      },
      required: {
        fields: '[required]:not([data-date],[readonly],[name="password"])',
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
      date: {
        fields: '.date-group input',
        messageInvalid: {
          invalid: 'Please enter a valid date.',
          future: 'Please enter a future date.',
          past: 'Please enter a past date.'
        }
      },
      pattern: {
        fields: '[pattern]:not([readonly])', // minlength is also using pattern as fallback, but in that case we want to show minlength message and not pattern.
        messageInvalid: 'Please correct this field.'
      },
      tel: {
        fields: '[type="tel"]:not([readonly])',
        messageInvalid: 'Please enter a valid phone number.'
      },
      typeTimeout: 300,
      // data-{{type}}-showmessage will define how the errors will be shown,
      // none = nothing happens and we are just running the check
      // visuallyhidden = in the dom, not visible for users but visible for screen readers
      // show = default behaviour, showing the error message
      dispaySettings: ['none', 'visuallyhidden', 'show'],
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
      jQuery('body').on('change ifsValidate', s.number.fields, function () { IFS.core.formValidation.checkNumber(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.min.fields, function () { IFS.core.formValidation.checkMin(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.max.fields, function () { IFS.core.formValidation.checkMax(jQuery(this)) })
      jQuery('body').on('blur change ifsValidate', s.required.fields, function () { IFS.core.formValidation.checkRequired(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.minlength.fields, function () { IFS.core.formValidation.checkMinLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.maxlength.fields, function () { IFS.core.formValidation.checkMaxLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.minwordslength.fields, function () { IFS.core.formValidation.checkMinWordsLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.maxwordslength.fields, function () { IFS.core.formValidation.checkMaxWordsLength(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.tel.fields, function () { IFS.core.formValidation.checkTel(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.date.fields, function () { IFS.core.formValidation.checkDate(jQuery(this)) })
      jQuery('body').on('change ifsValidate', s.pattern.fields, function () { IFS.core.formValidation.checkPattern(jQuery(this)) })

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
      jQuery('body').on('click', '.error-summary-list a', function (e) {
        e.preventDefault()
        IFS.core.formValidation.errorSummaryLinksClick(this)
      })
    },
    checkPasswordPolicy: function (field, errorStyles) {
      var hasUppercase = IFS.core.formValidation.checkFieldContainsUppercase(field)
      var hasNumber = IFS.core.formValidation.checkFieldContainsNumber(field)
      var isMinlength = IFS.core.formValidation.checkMinLength(field)
      var isFilledOut = IFS.core.formValidation.checkRequired(field)
      var formGroup = field.closest('.form-group')
      var confirmsToPasswordPolicy = hasUppercase && hasNumber && isMinlength && isFilledOut
      if (errorStyles) {
        if (confirmsToPasswordPolicy) {
          formGroup.removeClass('form-group-error')
          field.removeClass('form-control-error')
          //  clear tooWeakPassword message as this is validated in the back end.
          IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-tooWeak', 'visuallyhidden'))
          // clear the customError if it has been set by a server validation error
          if (s.html5validationMode && field[0].validity.customError) {
            field[0].setCustomValidity('')
          }
        } else {
          formGroup.addClass('form-group-error')
          field.addClass('form-control-error')
        }
      }
      return confirmsToPasswordPolicy
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
      var emailAttribute = 'email'
      // disabled escape js-standard message, we might want to solve this in the future by cleaning up the regex
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, emailAttribute)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, emailAttribute)
      var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i // eslint-disable-line

      // check if email value exists to avoid invalid email message on empty fields
      if (email) {
        var validEmail = emailRegex.test(email)

        // check if email address is invalid
        if (!validEmail) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
        return true
      }
    },
    checkNumber: function (field) {
      var numberAttribute = 'number'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, numberAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'number')
      // In modern browsers the number field doesn't allow text input
      // When inserting a string like "test" the browser converts this to an empty string "" (this is the specced behaviour)
      // An empty string is returned as true therefore
      // http://stackoverflow.com/questions/18852244/how-to-get-the-raw-value-an-input-type-number-field
      if (s.html5validationMode) {
        var domField = field[0]
        if (domField.validity.badInput === true || domField.validity.stepMismatch === true) {
          IFS.core.formValidation.setInvalid(field, errorMessage, displayValidationMessages)
          return false
        } else {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        }
      } else {
        // old browser mode
        // https://api.jquery.com/jQuery.isNumeric for what this checks
        var value = field.val()
        var wholeNumber = (value.indexOf(',') === -1) && (value.indexOf('.') === -1)
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
    checkRequired: function (field) {
      var requiredAttribute = 'required'
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, requiredAttribute)
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, requiredAttribute)

      if (field.val() !== null) {
        if (field.is(':checkbox,:radio')) {
          var name = field.attr('name')
          if (typeof (name) !== 'undefined') {
            var fieldGroup = jQuery('[name="' + name + '"]')
            if (jQuery('[name="' + name + '"]:checked').length === 0) {
              fieldGroup.each(function () { IFS.core.formValidation.setInvalid(jQuery(this), errorMessage, displayValidationMessages) })
              return false
            } else {
              fieldGroup.each(function () { IFS.core.formValidation.setValid(jQuery(this), errorMessage, displayValidationMessages) })
              return true
            }
          }
        // HTML5 number input will return "" as val() if invalid number.
        } else if (field.is(s.number.fields) && s.html5validationMode && field[0].validity.badInput) {
          IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          return true
        } else {
          if (field.val().length === 0) {
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
      var minlength = parseInt(field.attr('minlength'), 10)
      var fieldVal = field.val()
      var validMinlength = (fieldVal.length > 0) && (fieldVal.length >= minlength)

      IFS.core.formValidation.setStatus(field, minLengthAttribute, validMinlength)
      if (validMinlength) {
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
      var maxlength = parseInt(field.attr('maxlength'), 10)
      if (field.val().length > maxlength) {
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
    checkTel: function (field) {
      var telAttribute = 'tel'
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, telAttribute)
      var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, telAttribute)
      var re = /^(?=.*[0-9])[- +()0-9]+$/
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

      var d = dateGroup.find('.day input')
      var m = dateGroup.find('.month input')
      var y = dateGroup.find('.year input')
      var h
      if (field.closest('tr').find('.time select').length > 0) {
        h = dateGroup.find('.time option[data-time]:selected').attr('data-time')
      } else {
        h = dateGroup.find('.time [data-time]').attr('data-time')
      }
      var addWeekDay = dateGroup.find('.js-addWeekDay')

      var allFields = d.add(m).add(y)
      var fieldsVisited = (d.hasClass('js-visited') && m.hasClass('js-visited') && y.hasClass('js-visited'))
      var filledOut = ((d.val().length > 0) && (m.val().length > 0) && (y.val().length > 0))
      var enabled = !d.is('[readonly]') || !m.is('[readonly]') || !y.is('[readonly]')

      // don't show the validation messages for numbers in dates but we do check it as part of the date check
      allFields.attr({
        'data-number-showmessage': 'none',
        'data-min-showmessage': 'none',
        'data-max-showmessage': 'none'
      })
      var validNumbers = IFS.core.formValidation.checkNumber(d) && IFS.core.formValidation.checkNumber(m) &&
        IFS.core.formValidation.checkNumber(y) && IFS.core.formValidation.checkMin(y) &&
        IFS.core.formValidation.checkMax(y)
      var invalidErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-invalid')

      if (validNumbers && filledOut) {
        var month = parseInt(m.val(), 10)
        var day = parseInt(d.val(), 10)
        var year = parseInt(y.val(), 10)
        var date = new Date(year, month - 1, day) // parse as date to check if it is a valid date
        if (h !== undefined) {
          date.setHours(h, 0, 0, 0)
        } else {
          date.setHours(0, 0, 0, 0)
        }

        if ((date.getDate() === day) && (date.getMonth() + 1 === month) && (date.getFullYear() === year)) {
          valid = true

          IFS.core.formValidation.setValid(allFields, invalidErrorMessage, displayValidationMessages)

          allFields.attr('data-date', day + '-' + month + '-' + year)
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
      var validShowMessageValue = jQuery.inArray(displayValidationMessages, s.dispaySettings) !== -1
      if (validShowMessageValue === false || displayValidationMessages === 'none') {
        return
      }

      var formGroup = field.closest('.form-group')
      var formGroupRow = field.closest('.form-group-row')
      var name = IFS.core.formValidation.getName(field)
      var id = IFS.core.formValidation.getIdentifier(field)

      var visuallyhidden = displayValidationMessages === 'visuallyhidden'

      if (formGroup.length) {
        if (s.html5validationMode) { field[0].setCustomValidity(message) }
        if (visuallyhidden === false) { formGroup.addClass('form-group-error') }
        var errorEl = formGroup.find('.error-message:contains("' + message + '")')
        if (errorEl.length === 0) {
          if (visuallyhidden === false) { field.addClass('form-control-error') }
          formGroup.find('legend,label').first().append('<span class="error-message' + (visuallyhidden ? ' visuallyhidden' : '') + '">' + message + '</span>')
        }
      }

      if (formGroupRow.length) {
        if (s.html5validationMode) { field[0].setCustomValidity(message) }
        if (visuallyhidden === false) { formGroupRow.addClass('form-group-error') }

        var linkedErrorEl = formGroupRow.find('[data-errorfield="' + name + '"]:contains("' + message + '")')
        if (linkedErrorEl.length === 0) {
          if (visuallyhidden === false) { field.addClass('form-control-error') }
          formGroupRow.find('legend,label,[scope="row"]').first().append('<span data-errorfield="' + name + '" class="error-message' + (visuallyhidden ? ' visuallyhidden' : '') + '">' + message + '</span>')
        }
      }

      if (id.length) {
        if (jQuery('.error-summary-list [href="#' + id + '"]:contains(' + message + ')').length === 0) {
          jQuery('.error-summary-list').append('<li><a href="#' + id + '">' + message + '</a></li>')
        }
      } else {
        if (jQuery('.error-summary-list li:contains(' + message + ')').length === 0) {
          jQuery('.error-summary-list').append('<li>' + message + '</li>')
        }
      }

      jQuery('.error-summary:not([data-ignore-errors])').attr('aria-hidden', false)
      jQuery(window).trigger('updateWysiwygPosition')
    },
    setValid: function (field, message, displayValidationMessages) {
      var validShowMessageValue = jQuery.inArray(displayValidationMessages, s.dispaySettings) !== -1
      if (validShowMessageValue === false || displayValidationMessages === 'none') {
        return
      }
      var formGroup = field.closest('.form-group')
      var formGroupRow = field.closest('.form-group-row')
      var errorSummary = jQuery('.error-summary-list')
      var name = IFS.core.formValidation.getName(field)
      var id = IFS.core.formValidation.getIdentifier(field)

      // if it is a .form-group we assume the basic form structure with just one field per group
      // i.e.
      // <div class="form-group">
      //      <label for="field1">
      //          <span>FieldLabel</span>
      //          <span class="error-message">This field cannot be empty</span>
      //      </label>
      //      <input class="form-control form-control-error" name="field1" id="field1" required />
      // </div>
      if (formGroup.length) {
        formGroup.find('.error-message:contains("' + message + '")').remove()
        // if this was the last error we remove the error styling
        if (formGroup.find('.error-message').length === 0) {
          formGroup.removeClass('form-group-error')
          field.removeClass('form-control-error')
          // set corresponding radios/checkboxes valid
          if (s.html5validationMode) {
            jQuery('[name="' + name + '"]').each(function () { this.setCustomValidity('') })
          }
        }
      }
      // if it is a .form-group-multiple there can be multiple fields within the group, all having there own validation but reporting to one label
      // the template has to output server side error messages linked to the field
      // i.e. a table
      // <tr class="form-group-row form-group-error">
      //     <th scope="row" id="rowlabel">
      //          <span>The label of this row</span>
      //          <span class="error-message" data-errorfield="field1">This field cannot be empty</span>
      //          <span class="error-message" data-errorfield="field2">This field cannot be empty</span>
      //    </th>
      //     <td><input aria-labelledby="rowlabel" type="text" name="field1" class="form-control form-control-error" required /></td>
      //     <td><input aria-labelledby="rowlabel" type="text" name="field2" class="form-control form-control-error" required /></td>
      // <tr>
      if (formGroupRow.length) {
        formGroupRow.find('[data-errorfield="' + name + '"]:contains(' + message + ')').remove()
        if (formGroupRow.find('[data-errorfield="' + name + '"]').length === 0) {
          field.removeClass('form-control-error')
        }
        if ((formGroupRow.find('[data-errorfield="' + name + '"]').length === 0) && (s.html5validationMode)) {
          jQuery('[name="' + name + '"]').each(function () { this.setCustomValidity('') })
        }
        if (formGroupRow.find('[data-errorfield]').length === 0) {
          formGroupRow.removeClass('form-group-error')
        }
      }

      // updating the error summary
      if (errorSummary.length) {
        if (id.length) {
          errorSummary.find('[href="#' + id + '"]:contains(' + message + ')').parent().remove()
        } else {
          errorSummary.find('li:contains(' + message + ')').remove()
        }
        if (jQuery('.error-summary-list li:not(.list-header)').length === 0) {
          jQuery('.error-summary:not([data-ignore-errors])').attr('aria-hidden', 'true')
        }
      }

      jQuery(window).trigger('updateWysiwygPosition')
    },
    setSectionValid: function (section) {
      section = jQuery(section)
      section.removeClass('error')
      var inputs = section.find('.form-control-error')

      //  remove error messages from section + error summary
      section.find('.error-message').each(function () {
        var errorMessage = jQuery(this)
        var content = errorMessage.text()
        jQuery('.error-summary-list li:contains(' + content + ')').first().remove()
        errorMessage.remove()
      })

      jQuery.each(inputs, function () {
        jQuery(this).removeClass('form-control-error').val('')
        if (s.html5validationMode) {
          this.setCustomValidity('')
        }
      })
    },
    setStatus: function (field, type, status) {
      var formGroup = field.closest('.form-group,tr.form-group-row')
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
      if (el.is(':radio') || el.is(':checkbox')) {
        // Ifn it is a radio/checkbox group (so more than one)
        // Then we use the legend as id otherwise just the field id
        var name = el.attr('name')
        if (jQuery('[name="' + name + '"]').length > 1) {
          el = el.closest('fieldset').find('legend')
        }
      }
      if (el.is('[data-date]')) {
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
      var errorSummary = jQuery('.error-summary')
      if (errorSummary.length) {
        errorSummary.focus()
      } else {
        // Otherwise, set focus to the field with the error
        jQuery('.form-group-error input:not([type="hidden"])').first().focus()
      }
    },
    errorSummaryLinksClick: function (el) {
      var id = IFS.core.formValidation.removeHash(jQuery(el).attr('href'))
      var target = jQuery('[id="' + id + '"]')
      var targetVisible = IFS.core.formValidation.isVisible(target)
      var closedCollapsible = target.closest(IFS.core.collapsible.settings.collapsibleEl).not('.' + IFS.core.collapsible.settings.expandedClass)
      if (targetVisible) {
        target.first().focus()
      } else if (closedCollapsible.length) {
        // it is within a collapsible element and we open it and then put focus on it
        var stateless = closedCollapsible.hasClass(IFS.core.collapsible.settings.statelessClass)
        IFS.core.collapsible.toggleCollapsible(closedCollapsible.find('button[aria-controls]'), stateless)
        target.first().focus()
      } else {
        // if the target is invisible we put focus on an element that has the same label as the target
        // An example of this usecase is the wysiwyg editor
        var altTarget = jQuery('[aria-labelledby="' + id + '"]')
        var altTargetVisible = IFS.core.formValidation.isVisible(altTarget)
        if (altTargetVisible) {
          altTarget.first().focus()
        }
      }
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
