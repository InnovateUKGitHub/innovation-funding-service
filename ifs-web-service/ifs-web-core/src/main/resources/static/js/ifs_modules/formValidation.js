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
                  organisation : 'Password should not contain your organisation name'
                }
            },
            email : {
                fields : '[type="email"]',
                messageInvalid : "Please enter a valid email address"
            },
            required : {
                fields: '[required]',
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
                    clearTimeout(window.IFS.core.formValidation_timer);
                    window.IFS.core.formValidation_timer = setTimeout(function(){IFS.core.formValidation.checkEqualPasswords(true);}, s.typeTimeout);
                    break;
                  default:
                    IFS.core.formValidation.checkEqualPasswords(true);
              }
          });
          jQuery('body').on('change', s.passwordPolicy.fields.password, function(){IFS.core.formValidation.checkPasswordPolicy(jQuery(this),true);});
          jQuery('body').on('change', s.email.fields , function(){IFS.core.formValidation.checkEmail(jQuery(this),true);});
          jQuery('body').on('change', s.number.fields , function(){IFS.core.formValidation.checkNumber(jQuery(this),true);});
          jQuery('body').on('change', s.min.fields , function(){IFS.core.formValidation.checkMin(jQuery(this),true);});
          jQuery('body').on('change', s.max.fields , function(){IFS.core.formValidation.checkMax(jQuery(this),true);});
          jQuery('body').on('blur change',s.required.fields,function(){ IFS.core.formValidation.checkRequired(jQuery(this),true); });
          jQuery('body').on('change',s.minlength.fields,function(){ IFS.core.formValidation.checkMinLength(jQuery(this),true); });
          jQuery('body').on('change',s.maxlength.fields,function(){ IFS.core.formValidation.checkMaxLength(jQuery(this),true); });
          jQuery('body').on('change',s.tel.fields,function(){ IFS.core.formValidation.checkTel(jQuery(this),true); });
          jQuery('body').on('change',s.date.fields,function(){ IFS.core.formValidation.checkDate(jQuery(this),true); });

          //set data attribute on date fields
          //which has the combined value of the dates
          //and also makes sure that other vaidation doesn't get triggered
          jQuery(s.date.fields).attr({'data-date':'','data-autosave-disabled':''});

          //will only work on html5 validation browsers
          jQuery('form:not([novalidate]) input').on('invalid',function(){
              jQuery(this).trigger('change');
          });
          IFS.core.formValidation.betterMinLengthSupport();
        },
        checkEqualPasswords : function(showMessage){
            var pw1 = jQuery(s.passwordEqual.field1);
            var pw2 = jQuery(s.passwordEqual.field2);
            var errorMessage = IFS.core.formValidation.getErrorMessage(pw2,'passwordEqual');

            //if both are on the page and have content (.val)
            if(pw1.length && pw2.length && pw1.val().length && pw2.val().length){
                if(pw1.val() == pw2.val()){
                    if(showMessage){
                      IFS.core.formValidation.setValid(pw1,errorMessage);
                      IFS.core.formValidation.setValid(pw2,errorMessage);
                    }
                    return true;
                }
                else {
                    if(showMessage){
                      IFS.core.formValidation.setInvalid(pw1,errorMessage);
                      IFS.core.formValidation.setInvalid(pw2,errorMessage);
                    }
                    return false;
                }
            }
            return false;
        },
        checkPasswordPolicy : function(field,showMessage){
            var password = field.val();
            var confirmsToPasswordPolicy = true;
            //we only check for the policies if there is something filled in
            if(password.length){
              var uppercase = /(?=\S*?[A-Z])/;
              if(uppercase.test(password) === false){
                  if(showMessage){ IFS.core.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.uppercase); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.core.formValidation.setValid(field,s.passwordPolicy.messageInvalid.uppercase); }
              }

              var lowercase = /(?=\S*?[a-z])/;
              if(lowercase.test(password) === false){
                  if(showMessage){ IFS.core.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.lowercase); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.core.formValidation.setValid(field,s.passwordPolicy.messageInvalid.lowercase); }
              }

              var number = /(?=\S*?[0-9])/;
              if(number.test(password) === false){
                  if(showMessage){ IFS.core.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.number); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.core.formValidation.setValid(field,s.passwordPolicy.messageInvalid.number); }
              }

              var firstname = jQuery(s.passwordPolicy.fields.firstname).val();
              var lastname = jQuery(s.passwordPolicy.fields.lastname).val();
              if(firstname.replace(' ','').length || lastname.replace(' ','').length){
                if((password.toLowerCase().indexOf(firstname.toLowerCase()) > -1) || (password.toLowerCase().indexOf(lastname.toLowerCase()) > -1)){
                  if(showMessage){ IFS.core.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.name);}
                  confirmsToPasswordPolicy = false;
                }
                else {
                    if(showMessage){ IFS.core.formValidation.setValid(field,s.passwordPolicy.messageInvalid.name);}
                }
              }
            }
            return confirmsToPasswordPolicy;
        },
        checkEmail : function(field,showMessage){
            //checks if the email is valid, the almost rfc compliant check. The same as the java check, see http://www.regular-expressions.info/email.html
            var email = field.val();
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i;
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'email');

            var validEmail = re.test(email);
            if(!validEmail){
              if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage); }
              return false;
            }
            else {
              if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage); }
              return true;
            }
        },
        checkNumber : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'number');
            //In modern browsers the number field doesn't allow text input
            //When inserting a string like "test" the browser converts this to an empty string "" (this is the specced behaviour)
            //An empty string is returned as true therefore
            //http://stackoverflow.com/questions/18852244/how-to-get-the-raw-value-an-input-type-number-field
            if(s.html5validationMode){
                var domField = field[0];
                if(domField.validity.valid === false && domField.validity.badInput === true){
                  if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage); }
                  return false;
                }
                else {
                  if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage); }
                  return true;
                }
            }
            else {
              //old browser mode
              //https://api.jquery.com/jQuery.isNumeric for what this checks
              if((field.val() > 0) && !jQuery.isNumeric(field.val())){
                if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage); }
                return false;
              }
              else{
                if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage); }
                return true;
              }
            }
        },
        checkMax : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'max');
            if(s.html5validationMode){
               //html5 validation api
               var domField = field[0];
               if(domField.validity.rangeOverflow === true){
                 if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
                 return false;
               }
               else {
                 if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
                 return true;
               }
            }
            else {
              //classic mode
              var max = parseInt(field.attr('max'),10);
              if(IFS.core.formValidation.checkNumber(field,true)){
                var fieldVal = parseInt(field.val(),10);
                if(fieldVal > max){
                  if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
                  return false;
                }
                else {
                  if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
                  return true;
                }
              }
            }
        },
        checkMin : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'min');
            if(s.html5validationMode){
              var domField = field[0];
              if(domField.validity.rangeUnderflow === true){
                if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
                return false;
              }
              else {
                if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
                return true;
              }
            }
            else {
              var min = parseInt(field.attr('min'),10);
              if(IFS.core.formValidation.checkNumber(field)){
                var fieldVal = parseInt(field.val(),10);
                if(fieldVal < min){
                  if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
                  return false;
                }
                else {
                  if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
                  return true;
                }
              }
            }
        },
        checkRequired : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'required');

            if(field.val() !== null){
              if(field.is(':checkbox,:radio')){
                var name = field.attr("name");
                if(typeof(name) !== 'undefined'){
                   var fieldGroup = jQuery('[name="'+name+'"]');
                   if(jQuery('[name="'+name+'"]:checked').length === 0){
                     if(showMessage) {
                      fieldGroup.each(function(){ IFS.core.formValidation.setInvalid(jQuery(this),errorMessage); });
                     }
                     return false;
                   }
                   else {
                     if(showMessage) {
                       fieldGroup.each(function(){ IFS.core.formValidation.setValid(jQuery(this),errorMessage); });
                     }
                     return true;
                   }
                }
              }
              else {
                if(field.val().length === 0){
                  if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
                  return false;
                }
                else {
                  if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
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
              var minlength = parseInt(field.attr('minlength'),10);
              field.attr('pattern','.{'+minlength+',}');
            });
          }
        },
        checkMinLength : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'minlength');
            var minlength = parseInt(field.attr('minlength'),10);
            if((field.val().length > 0) && (field.val().length < minlength)){
              if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
              return false;
            }
            else {
              if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
              return true;
            }
        },
        checkMaxLength : function(field,showMessage){
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'maxlength');
            var maxlength = parseInt(field.attr('maxlength'),10);
            if(field.val().length > maxlength){
              if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
              return false;
            }
            else {
              if(showMessage) {IFS.core.formValidation.setValid(field,errorMessage);}
              return true;
            }
        },
        checkTel : function(field,showMessage){
            var tel = field.val();
            var errorMessage = IFS.core.formValidation.getErrorMessage(field,'tel');
            var re = /^(?=.*[0-9])[- +()0-9]+$/;
            var validPhone = re.test(tel);

            if(!validPhone){
              if(showMessage) { IFS.core.formValidation.setInvalid(field,errorMessage);}
              return false;
            }
            else {
              if(showMessage) { IFS.core.formValidation.setValid(field,errorMessage);}
              return true;
            }
        },
        checkDate : function(field,showMessage){
          var dateGroup = field.closest('.date-group');
          field.addClass('js-visited');

          var d = dateGroup.find('.day input');
          var m = dateGroup.find('.month input');
          var y = dateGroup.find('.year input');

          var allFields = d.add(m).add(y);
          var fieldsVisited = (d.hasClass('js-visited') && m.hasClass('js-visited') && y.hasClass('js-visited'));
          var filledOut = ((d.val().length > 0) && (m.val().length > 0) && (y.val().length > 0));
          var validNumbers = IFS.core.formValidation.checkNumber(d,false) && IFS.core.formValidation.checkNumber(m,false) && IFS.core.formValidation.checkNumber(y,false);

          if(validNumbers && filledOut){
            var month = parseInt(m.val(),10);
            var day = parseInt(d.val(),10);
            var year = parseInt(y.val(),10);
            var date = new Date(year,month-1,day); //parse as date to check if it is a valid date

            if ((date.getDate() == day) && (date.getMonth() + 1 == month) && (date.getFullYear() == year)) {
                if(showMessage){ IFS.core.formValidation.setValid(allFields,s.date.messageInvalid.invalid); }
                allFields.attr('data-date',day+'-'+month+'-'+year);
                field.trigger('ifsAutosave');

                if(dateGroup.hasClass("js-future-date")){
                  var now = new Date();
                  if(now < date){
                    if(showMessage){ IFS.core.formValidation.setValid(allFields,s.date.messageInvalid.future); }
                    return true;
                  }
                  else {
                    if(showMessage){ IFS.core.formValidation.setInvalid(allFields,s.date.messageInvalid.future); }
                    return false;
                  }
                }
                return true;
            } else {
                if(showMessage){ IFS.core.formValidation.setInvalid(allFields,s.date.messageInvalid.invalid); }
                allFields.attr({'data-date':''});
                return false;
            }
          }
          else if (filledOut || fieldsVisited){
                if(showMessage){ IFS.core.formValidation.setInvalid(allFields,s.date.messageInvalid.invalid); }
                allFields.attr({'data-date':''});
                return false;
          }
          else {
            return false;
          }
        },
        getErrorMessage : function(field,type){
            //first look if there is a custom message defined on the element
            var errorMessage = field.attr('data-'+type+'-errormessage');
            //if there is no data-errormessage we use the default messagging defined in the settings object
            if (typeof(errorMessage) == 'undefined') {
              errorMessage = s[type].messageInvalid;
            }
            //replace value so we can have text like; this cannot be under %max%
            if(errorMessage.indexOf('%'+type+'%') !== -1){
                errorMessage = errorMessage.replace('%'+type+'%',field.attr(type));
            }
            return errorMessage;
        },
        setInvalid : function(field,message){
            var formGroup = field.closest('.form-group');
            if(formGroup){
                if(s.html5validationMode){ field[0].setCustomValidity(message);}
                field.addClass('field-error');
                //if the message isn't in this formgroup yet we will add it, a form-group can have multiple errors.
                var errorEl = formGroup.find('.error-message:contains("'+message+'")');
                if(errorEl.length === 0){
                    formGroup.addClass('error');
                    var html = '<span class="error-message">'+message+'</span>';
                    formGroup.find('legend,label').first().append(html);
                }
            }
            if(jQuery('ul.error-summary-list li:contains('+message+')').length === 0){
                jQuery('.error-summary-list').append('<li>'+message+'</li>');
            }
            jQuery('.error-summary').attr('aria-hidden',false);
            jQuery(window).trigger('updateWysiwygPosition');
        },
        setValid : function(field,message){
            var formGroup = field.closest('.form-group.error');
            if(formGroup){
              formGroup.find('.error-message:contains("'+message+'")').remove();

               //if this was the last error we remove the error styling
               if(formGroup.find('.error-message').length === 0){
                   formGroup.removeClass('error');
                   field.removeClass('field-error');
                   if(s.html5validationMode){ field[0].setCustomValidity('');}
               }
            }
            if(jQuery('.error-summary-list li:contains('+message+')').length){
              jQuery('.error-summary-list li:contains('+message+')').remove();
            }

            if(jQuery('.error-summary-list li').length === 0){
              jQuery('.error-summary').attr('aria-hidden',true);
            }
            jQuery(window).trigger('updateWysiwygPosition');
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
        }
    };
})();
