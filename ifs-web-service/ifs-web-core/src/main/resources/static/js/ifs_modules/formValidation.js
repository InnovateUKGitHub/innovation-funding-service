IFS.core.formValidation = (function(){
  "use strict";
  var s;
  return {
    settings : {
      number : {
        fields : '[type="number"]:not([data-date])',
        messageInvalid : 'This field should be a number'
      },
      min : {
        fields: '[min]:not([data-date])',
        messageInvalid : 'This field should be %min% or higher'
      },
      max : {
        fields: '[max]:not([data-date])',
        messageInvalid : 'This field should be %max% or lower'
      },
      passwordEqual: {
        field1 : '[name="password"]',
        field2 : '[name="retypedPassword"]',
        messageInvalid : 'Passwords must match'
      },
      passwordPolicy : {
        fields : {
          password : '[name="password"],[name="retypedPassword"]',
          firstname : '#firstName',
          lastname : '#lastName'
        },
        messageInvalid : {
          lowercase : 'Password must contain at least one lower case letter',
          uppercase : 'Password must contain at least one upper case letter',
          number : 'Password must contain at least one number',
          name : 'Password should not contain either your first or last name',
          organisation : 'Password should not contain your organisation name',
          tooWeak : 'Password is too weak'
        }
      },
      email : {
        fields : '[type="email"]',
        messageInvalid : "Please enter a valid email address"
      },
      required : {
        fields: '[required]:not([data-date])',
        messageInvalid : "This field cannot be left blank"
      },
      minlength : {
        fields : '[minlength]',
        messageInvalid : "This field should contain at least %minlength% characters"
      },
      maxlength : {
        fields : '[maxlength]',
        messageInvalid : "This field cannot contain more than %maxlength% characters"
      },
      minwordslength : {
        fields : '[data-minwordslength]',
        messageInvalid : "This field has a minimum number of words"
      },
      maxwordslength : {
        fields : '[data-maxwordslength]',
        messageInvalid : "This field has a maximum number of words"
      },
      date : {
        fields : '.date-group input',
        messageInvalid : {
          invalid : "Please enter a valid date",
          future : "Please enter a future date"
        }
      },
      tel : {
        fields : '[type="tel"]',
        messageInvalid : "Please enter a valid phone number"
      },
      typeTimeout : 1500,
      html5validationMode : false
    },
    init : function(){
      s = this.settings;
      s.html5validationMode =  IFS.core.formValidation.checkHTML5validationMode();
      IFS.core.formValidation.initValidation();
    },
    initValidation : function(){
      //bind the checks if password and retyped password are equal
      jQuery('body').on('change keyup ifsValidate', s.passwordEqual.field1+','+s.passwordEqual.field2, function(e){
        switch(e.type){
          case 'keyup':
            clearTimeout(window.IFS.core.formValidationTimer);
            window.IFS.core.formValidationTimer = setTimeout(function(){IFS.core.formValidation.checkEqualPasswords(true);}, s.typeTimeout);
            break;
          default:
            IFS.core.formValidation.checkEqualPasswords(true);
        }
      });
      jQuery('body').on('change', s.passwordPolicy.fields.password, function(){IFS.core.formValidation.checkPasswordPolicy(jQuery(this), true);});
      jQuery('body').on('change', s.email.fields , function(){IFS.core.formValidation.checkEmail(jQuery(this), true);});
      jQuery('body').on('change', s.number.fields , function(){IFS.core.formValidation.checkNumber(jQuery(this), true);});
      jQuery('body').on('change', s.min.fields , function(){IFS.core.formValidation.checkMin(jQuery(this), true);});
      jQuery('body').on('change', s.max.fields , function(){IFS.core.formValidation.checkMax(jQuery(this), true);});
      jQuery('body').on('blur change', s.required.fields, function(){ IFS.core.formValidation.checkRequired(jQuery(this), true); });
      jQuery('body').on('change', s.minlength.fields, function(){ IFS.core.formValidation.checkMinLength(jQuery(this), true); });
      jQuery('body').on('change', s.maxlength.fields, function(){ IFS.core.formValidation.checkMaxLength(jQuery(this), true); });
      jQuery('body').on('change', s.minwordslength.fields, function(){ IFS.core.formValidation.checkMinWordsLength(jQuery(this), true); });
      jQuery('body').on('change', s.maxwordslength.fields, function(){ IFS.core.formValidation.checkMaxWordsLength(jQuery(this), true); });
      jQuery('body').on('change', s.tel.fields, function(){ IFS.core.formValidation.checkTel(jQuery(this), true); });
      jQuery('body').on('change', s.date.fields, function(){  IFS.core.formValidation.checkDate(jQuery(this), true); });

      //set data attribute on date fields
      //which has the combined value of the dates
      //and also makes sure that other vaidation doesn't get triggered
      jQuery(s.date.fields).attr({'data-date':'', 'data-autosave-disabled':''});

      //will only work on html5 validation browsers
      jQuery('form:not([novalidate]) input').on('invalid', function(){
        jQuery(this).trigger('change');
      });
      IFS.core.formValidation.betterMinLengthSupport();
    },
    checkEqualPasswords : function(showMessage){
      var pw1 = jQuery(s.passwordEqual.field1);
      var pw2 = jQuery(s.passwordEqual.field2);
      var errorMessage = IFS.core.formValidation.getErrorMessage(pw2, 'passwordEqual');

      //if both are on the page and have content (.val)
      if(pw1.length && pw2.length && pw1.val().length && pw2.val().length){
        if(pw1.val() == pw2.val()){
          if(showMessage){
            IFS.core.formValidation.setValid(pw1, errorMessage);
            IFS.core.formValidation.setValid(pw2, errorMessage);
          }
          return true;
        }
        else {
          if(showMessage){
            IFS.core.formValidation.setInvalid(pw1, errorMessage);
            IFS.core.formValidation.setInvalid(pw2, errorMessage);
          }
          return false;
        }
      }
      return false;
    },
    checkPasswordPolicy : function(field, showMessage){
      var password = field.val();
      var confirmsToPasswordPolicy = true;

      //we only check for the policies if there is something filled in
      if(password.length){
        var upperCaseErrorMessage =  IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-uppercase');
        var lowerCaseErrorMessage =  IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-lowercase');
        var numberErrorMessage =  IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-number');
        var nameErrorMessage =  IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-name');

        var uppercase = /(?=\S*?[A-Z])/;
        if(uppercase.test(password) === false){
          if(showMessage){ IFS.core.formValidation.setInvalid(field, upperCaseErrorMessage); }
          confirmsToPasswordPolicy = false;
        }
        else {
          if(showMessage){ IFS.core.formValidation.setValid(field, upperCaseErrorMessage); }
        }

        var lowercase = /(?=\S*?[a-z])/;
        if(lowercase.test(password) === false){
          if(showMessage){ IFS.core.formValidation.setInvalid(field, lowerCaseErrorMessage); }
          confirmsToPasswordPolicy = false;
        }
        else {
          if(showMessage){ IFS.core.formValidation.setValid(field, lowerCaseErrorMessage); }
        }

        var number = /(?=\S*?[0-9])/;
        if(number.test(password) === false){
          if(showMessage){ IFS.core.formValidation.setInvalid(field, numberErrorMessage); }
          confirmsToPasswordPolicy = false;
        }
        else {
          if(showMessage){ IFS.core.formValidation.setValid(field, numberErrorMessage); }
        }

        var firstname = jQuery(s.passwordPolicy.fields.firstname).val();
        var lastname = jQuery(s.passwordPolicy.fields.lastname).val();
        if(firstname.replace(' ', '').length || lastname.replace(' ', '').length){
          if((password.toLowerCase().indexOf(firstname.toLowerCase()) > -1) || (password.toLowerCase().indexOf(lastname.toLowerCase()) > -1)){
            if(showMessage){ IFS.core.formValidation.setInvalid(field, nameErrorMessage);}
            confirmsToPasswordPolicy = false;
          }
          else {
            if(showMessage){ IFS.core.formValidation.setValid(field, nameErrorMessage);}
          }
        }
      }

      //onchange clear tooWeakPassword message as this is validated in the back end.
      IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'passwordPolicy-tooWeak'));

      return confirmsToPasswordPolicy;
    },
    checkEmail : function(field, showMessage){
      //checks if the email is valid, the almost rfc compliant check. The same as the java check, see http://www.regular-expressions.info/email.html
      var email = field.val();
      var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i;
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'email');

      var validEmail = re.test(email);
      if(!validEmail){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage); }
        return false;
      }
      else {
        if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage); }
        return true;
      }
    },
    checkNumber : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'number');
      //In modern browsers the number field doesn't allow text input
      //When inserting a string like "test" the browser converts this to an empty string "" (this is the specced behaviour)
      //An empty string is returned as true therefore
      //http://stackoverflow.com/questions/18852244/how-to-get-the-raw-value-an-input-type-number-field
      if(s.html5validationMode){
        var domField = field[0];
        if(domField.validity.badInput === true || domField.validity.stepMismatch === true){
          if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage); }
          return false;
        }
        else {
          if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage); }
          return true;
        }
      }
      else {
        //old browser mode
        //https://api.jquery.com/jQuery.isNumeric for what this checks
        var value = field.val();
        var wholeNumber = (value.indexOf(',') == -1) && (value.indexOf('.') == -1);
        if(!jQuery.isNumeric(value) || !wholeNumber){
          if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage); }
          return false;
        }
        else{
          if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage); }
          return true;
        }
      }
    },
    checkMax : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'max');
      if(s.html5validationMode){
        //html5 validation api
        var domField = field[0];
        if(domField.validity.rangeOverflow === true){
          if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
          return false;
        }
        else {
          if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
          return true;
        }
      }
      else {
        //classic mode
        var max = parseInt(field.attr('max'), 10);
        if(IFS.core.formValidation.checkNumber(field, true)){
          var fieldVal = parseInt(field.val(), 10);
          if(fieldVal > max){
            if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
            return false;
          }
          else {
            if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
            return true;
          }
        }
      }
    },
    checkMin : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'min');
      if(s.html5validationMode){
        var domField = field[0];
        if(domField.validity.rangeUnderflow === true){
          if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
          return false;
        }
        else {
          if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
          return true;
        }
      }
      else {
        var min = parseInt(field.attr('min'), 10);
        if(IFS.core.formValidation.checkNumber(field)){
          var fieldVal = parseInt(field.val(), 10);
          if(fieldVal < min){
            if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
            return false;
          }
          else {
            if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
            return true;
          }
        }
      }
    },
    checkRequired : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'required');

      if(field.val() !== null){
        if(field.is(':checkbox,:radio')){
          var name = field.attr("name");
          if(typeof(name) !== 'undefined'){
            var fieldGroup = jQuery('[name="'+name+'"]');
            if(jQuery('[name="'+name+'"]:checked').length === 0){
              if(showMessage) {
                fieldGroup.each(function(){ IFS.core.formValidation.setInvalid(jQuery(this), errorMessage); });
              }
              return false;
            }
            else {
              if(showMessage) {
                fieldGroup.each(function(){ IFS.core.formValidation.setValid(jQuery(this), errorMessage); });
              }
              return true;
            }
          }
        }
        else {
          if(field.val().length === 0){
            if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
            return false;
          }
          else {
            if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
            return true;
          }
        }
      }
    },
    betterMinLengthSupport : function(){
      //if the minlenght is not implemented in the browser we use pattern which is more widely supported
      if((s.html5validationMode) && (typeof(jQuery('input')[0].validity.tooShort) == 'undefined')){
        jQuery(s.minlength.fields).each(function(){
          var field = jQuery(this);
          var minlength = parseInt(field.attr('minlength'), 10);
          field.attr('pattern', '.{'+minlength+',}');
        });
      }
    },
    checkMinLength : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'minlength');
      var minlength = parseInt(field.attr('minlength'), 10);
      if((field.val().length > 0) && (field.val().length < minlength)){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
        return false;
      }
      else {
        if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
        return true;
      }
    },
    checkMaxLength : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'maxlength');
      var maxlength = parseInt(field.attr('maxlength'), 10);
      if(field.val().length > maxlength){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
        return false;
      }
      else {
        if(showMessage) {IFS.core.formValidation.setValid(field, errorMessage);}
        return true;
      }
    },
    checkMinWordsLength : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'minwordslength');
      var minWordsLength = parseInt(field.attr('data-minwordslength'), 10);
      var value = field.val();
      var words = IFS.core.formValidation.countWords(value);

      if((words.length > 0) && (words.length < minWordsLength)){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
        return false;
      }
      else {
        if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
        return true;
      }
    },
    checkMaxWordsLength : function(field, showMessage){
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'maxwordslength');
      var maxWordsLength = parseInt(field.attr('data-maxwordslength'), 10);
      var value = field.val();
      var words = IFS.core.formValidation.countWords(value);

      if(words.length > maxWordsLength){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
        return false;
      }
      else {
        if(showMessage) {IFS.core.formValidation.setValid(field, errorMessage);}
        return true;
      }
    },
    checkTel : function(field, showMessage){
      var tel = field.val();
      var errorMessage = IFS.core.formValidation.getErrorMessage(field, 'tel');
      var re = /^(?=.*[0-9])[- +()0-9]+$/;
      var validPhone = re.test(tel);

      if(!validPhone){
        if(showMessage) { IFS.core.formValidation.setInvalid(field, errorMessage);}
        return false;
      }
      else {
        if(showMessage) { IFS.core.formValidation.setValid(field, errorMessage);}
        return true;
      }
    },
    checkDate : function(field, showMessage){
      var dateGroup = field.closest('.date-group');
      field.addClass('js-visited');
      var valid;

      var d = dateGroup.find('.day input');
      var m = dateGroup.find('.month input');
      var y = dateGroup.find('.year input');
      var addWeekDay = dateGroup.find('.js-addWeekDay');

      var allFields = d.add(m).add(y);
      var fieldsVisited = (d.hasClass('js-visited') && m.hasClass('js-visited') && y.hasClass('js-visited'));
      var filledOut = ((d.val().length > 0) && (m.val().length > 0) && (y.val().length > 0));
      var validNumbers = IFS.core.formValidation.checkNumber(d, false) && IFS.core.formValidation.checkNumber(m, false) && IFS.core.formValidation.checkNumber(y, false);
      var invalidErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-invalid');

      if(validNumbers && filledOut){
        var month = parseInt(m.val(), 10);
        var day = parseInt(d.val(), 10);
        var year = parseInt(y.val(), 10);
        var date = new Date(year, month-1, day); //parse as date to check if it is a valid date

        if ((date.getDate() == day) && (date.getMonth() + 1 == month) && (date.getFullYear() == year)) {
          valid = true;

          if(showMessage){ IFS.core.formValidation.setValid(allFields, invalidErrorMessage); }

          allFields.attr('data-date', day+'-'+month+'-'+year);
          //adding day of week which is not really validation
          //so could be better of somehwere else
          if(addWeekDay.length){
            var days = ['Sun', 'Mon', 'Tues', 'Wed', 'Thurs', 'Fri', 'Sat'];
            var weekday = days[date.getDay()];
            addWeekDay.text(weekday);
          }
          field.trigger('ifsAutosave');

          if(dateGroup.is("[data-future-date]")){
            valid = IFS.core.formValidation.checkFutureDate(dateGroup, date, showMessage);
          }
        } else {
          if(showMessage){ IFS.core.formValidation.setInvalid(allFields, invalidErrorMessage); }
          allFields.attr({'data-date':''});
          valid = false;
        }
      }
      else if (filledOut || fieldsVisited){
        if(showMessage){ IFS.core.formValidation.setInvalid(allFields, invalidErrorMessage); }
        allFields.attr({'data-date':''});
        valid = false;
      }
      else {
        valid = false;
      }

      if(!valid && addWeekDay.length){
        addWeekDay.text('-');
      }

      return valid;
    },
    checkFutureDate : function(dateGroup, date, showMessage){
      var futureErrorMessage = IFS.core.formValidation.getErrorMessage(dateGroup, 'date-future');
      var allFields = dateGroup.find('.day input, .month input, .year input');
      var futureDate;
      if(jQuery.trim(dateGroup.attr('data-future-date')).length === 0){
        //if no future date is set we assume today
        futureDate = new Date();
      }
      else {
        var futureDateString = dateGroup.attr('data-future-date').split('-');
        var futureDay = parseInt(futureDateString[0], 10);
        var futureMonth = parseInt(futureDateString[1], 10)-1;
        var futureYear = parseInt(futureDateString[2], 10);
        futureDate = new Date(futureYear, futureMonth, futureDay);
      }
      if(futureDate.setHours(0, 0, 0, 0) <= date.setHours(0, 0, 0, 0)){
        if(showMessage){ IFS.core.formValidation.setValid(allFields, futureErrorMessage); }
        return true;
      }
      else {
        if(showMessage){ IFS.core.formValidation.setInvalid(allFields, futureErrorMessage); }
        return false;
      }
    },
    getErrorMessage : function(field, type){
      //first look if there is a custom message defined on the element
      var errorMessage = field.attr('data-'+type+'-errormessage');

      //support for submessages for one type (i.e date invalid / date future) and different password policys
      var subtype;
      if(type.indexOf('-') !== -1){
        var types = type.split('-');
        type = types[0];
        subtype = types[1];
      }
      else {
        type = type;
        subtype = false;
      }

      //if there is no data-errormessage we use the default messagging defined in the settings object
      if (typeof(errorMessage) == 'undefined') {
        if(subtype === false){
          errorMessage = s[type].messageInvalid;
        }
        else {
          errorMessage = s[type].messageInvalid[subtype];
        }
      }
      //replace value so we can have text like; this cannot be under %max%
      if(errorMessage.indexOf('%'+type+'%') !== -1){
        errorMessage = errorMessage.replace('%'+type+'%', field.attr(type));
      }
      return errorMessage;
    },
    setInvalid : function(field, message){
      var formGroup = field.closest('.form-group,tr.form-group-row');
      var name = IFS.core.formValidation.getIdentifier(field);

      if(formGroup.length){
        if(s.html5validationMode){ field[0].setCustomValidity(message);}
        formGroup.addClass('error');

        //if the message isn't in this formgroup yet we will add it, a form-group can have multiple errors.
        var errorEl = formGroup.find('[data-errorfield="'+name+'"]:contains("'+message+'"),.error-message:not([data-errorfield]):contains("'+message+'")');
        if(errorEl.length === 0){
          field.addClass('field-error');
          var html = '<span data-errorfield="'+name+'" class="error-message">'+message+'</span>';
          formGroup.find('legend,label,[scope="row"]').first().append(html);
        }
      }

      if(jQuery('ul.error-summary-list [data-errorfield="'+name+'"]:contains('+message+')').length === 0){
        jQuery('.error-summary-list').append('<li data-errorfield="'+name+'">'+message+'</li>');
      }

      jQuery('.error-summary').attr('aria-hidden', false);
      jQuery(window).trigger('updateWysiwygPosition');
    },
    setValid : function(field, message){
      var formGroup = field.closest('.form-group.error,tr.form-group-row.error');
      var errorSummary = jQuery('.error-summary-list');
      var name = IFS.core.formValidation.getIdentifier(field);

      if(formGroup.length){
        //client side remove in form group
        formGroup.find('[data-errorfield="'+name+'"]:contains("'+message+'")').remove();
        //server side remove in form group
        formGroup.find('.error-message:not([data-errorfield]):contains("'+message+'")').first().remove();

        //if this was the last error we remove the error styling
        if(formGroup.find('[data-errorfield],.error-message:not([data-errorfield])').length === 0){
          formGroup.removeClass('error');
        }
        if(formGroup.find('[data-errorfield="'+name+'"]').length === 0) {
          field.removeClass('field-error');
          if(s.html5validationMode){
            jQuery('[name="'+name+'"]').each(function(){
              this.setCustomValidity('');
            });
          }

        }
      }

      if(errorSummary.length){
        //remove clientside in summary
        errorSummary.find('[data-errorfield="'+name+'"]:contains('+message+')').remove();
        //remove server side in summary
        errorSummary.find('li:not([data-errorfield]):contains("'+message+'")').first().remove();
      }
      if(jQuery('.error-summary-list li:not(.list-header)').length === 0){
        jQuery('.error-summary').attr('aria-hidden', 'true');
      }
      jQuery(window).trigger('updateWysiwygPosition');
    },
    getIdentifier : function(el){
      if(el.is('[data-date]')){
        el =  el.closest('.date-group').find('input[type="hidden"]');
      }
      if(el.prop('name').length) {
        return  el.prop('name');
      }
      else if(el.prop('id').length){
        return el.prop('id');
      }
      return false;
    },
    checkHTML5validationMode : function(){
      var testField =jQuery('input');
      if(testField.length){
        if(typeof(testField[0].validity) !== 'undefined'){
          return true;
        }
        else {
          return false;
        }
      }
    },
    countWords : function(value){
      var val = value;

      if(typeof(val) !== 'undefined'){
        //regex = replace newlines with space \r\n, \n, \r
        val = value.replace(/(\r\n|\n|\r)/gm, " ");
        //remove markdown lists ('* ','1. ','2. ','**','_') from markdown as it influences word count
        val = value.replace(/([[0-9]+\.\ |\*\ |\*\*|_)/gm, "");

        return jQuery.trim(val).split(' ');
      } else {
        return false;
      }
    }
  };
})();
