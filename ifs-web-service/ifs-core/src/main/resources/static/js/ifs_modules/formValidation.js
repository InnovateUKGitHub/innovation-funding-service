IFS.formValidation = (function(){
    "use strict";
    var s;
    return {
        settings : {
            number : {
                fields : '[type="number"]',
                messageInvalid : 'This field should be a number'
            },
            min : {
                fields: '[min]',
                messageInvalid : 'This field should be %min% or higher'
            },
            max : {
                fields: '[max]',
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
                  firstname : 'Password should not contain your first name',
                  lastname : 'Password should not contain your last name',
                  organisation : 'Password should not contain your organisation name'
                }
            },
            email : {
                fields : '[type="email"]',
                messageInvalid : 'Please enter a valid email address'
            },
            required : {
                fields: '[required]',
                messageInvalid : 'This field cannot be left blank'
            },
            minlength : {
                fields : '[minlength]',
                messageInvalid : 'This field should contain at least %minlength% characters'
            },
            maxlength : {
                fields : '[maxlength]',
                messageInvalid : 'This field cannot contain more than %maxlength% characters'
            },
            date : {
                fields : '.date-group input',
                messageInvalid : 'Please enter a valid date'
            },
            tel : {
                fields : '[type="tel"]',
                messageInvalid : 'Please enter a valid phone number'
            },
            typeTimeout : 1500
        },
        init : function(){
            s = this.settings;
            //bind the checks if password and retyped password are equal
            jQuery('body').on('change keyup', s.passwordEqual.field1+','+s.passwordEqual.field2, function(e){
                switch(e.type){
                    case 'keyup':
                      clearTimeout(window.IFS.formValidation_timer);
                      window.IFS.formValidation_timer = setTimeout(function(){IFS.formValidation.checkEqualPasswords(true);}, s.typeTimeout);
                      break;
                    default:
                      IFS.formValidation.checkEqualPasswords(true);
                }
            });
            jQuery('body').on('change', s.passwordPolicy.fields.password, function(){IFS.formValidation.checkPasswordPolicy(jQuery(this),true);});
            jQuery('body').on('change', s.email.fields , function(){IFS.formValidation.checkEmail(jQuery(this),true);});
            jQuery('body').on('change', s.number.fields , function(){IFS.formValidation.checkNumber(jQuery(this),true);});
            jQuery('body').on('change', s.min.fields , function(){IFS.formValidation.checkMin(jQuery(this),true);});
            jQuery('body').on('change', s.max.fields , function(){IFS.formValidation.checkMax(jQuery(this),true);});
            jQuery('body').on('blur change',s.required.fields,function(){ IFS.formValidation.checkRequired(jQuery(this),true); });
            jQuery('body').on('change',s.minlength.fields,function(){ IFS.formValidation.checkMinLength(jQuery(this),true); });
            jQuery('body').on('change',s.maxlength.fields,function(){ IFS.formValidation.checkMaxLength(jQuery(this),true); });
            jQuery('body').on('change',s.tel.fields,function(){ IFS.formValidation.checkTel(jQuery(this),true); });
            jQuery('body').on('change',s.date.fields,function(){  IFS.formValidation.checkDate(jQuery(this),true); });
        },
        checkEqualPasswords : function(showMessage){
            var pw1 = jQuery(s.passwordEqual.field1);
            var pw2 = jQuery(s.passwordEqual.field2);
            var errorMessage = IFS.formValidation.getErrorMessage(pw2,'passwordEqual');

            //if both are on the page and have content (.val)
            if(pw1.length && pw2.length && pw1.val().length && pw2.val().length){
                if(pw1.val() == pw2.val()){
                    if(showMessage){
                      IFS.formValidation.setValid(pw1,errorMessage);
                      IFS.formValidation.setValid(pw2,errorMessage);
                    }
                    return true;
                }
                else {
                    if(showMessage){
                      IFS.formValidation.setInvalid(pw1,errorMessage);
                      IFS.formValidation.setInvalid(pw2,errorMessage);
                    }
                    return false;
                }
            }
        },
        checkPasswordPolicy : function(field,showMessage){
            var password = field.val();
            var confirmsToPasswordPolicy = true;
            //we only check for the policies if there is something filled in
            if(password.length){
              var uppercase = /(?=\S*?[A-Z])/;
              if(uppercase.test(password) === false){
                  if(showMessage){ IFS.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.uppercase); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.formValidation.setValid(field,s.passwordPolicy.messageInvalid.uppercase); }
              }

              var lowercase = /(?=\S*?[a-z])/;
              if(lowercase.test(password) === false){
                  if(showMessage){ IFS.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.lowercase); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.formValidation.setValid(field,s.passwordPolicy.messageInvalid.lowercase); }
              }

              var number = /(?=\S*?[0-9])/;
              if(number.test(password) === false){
                  if(showMessage){ IFS.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid.number); }
                  confirmsToPasswordPolicy = false;
              }
              else {
                  if(showMessage){ IFS.formValidation.setValid(field,s.passwordPolicy.messageInvalid.number); }
              }

              var nameCheck = ['firstname','lastname'];
              jQuery(nameCheck).each(function(index,value){
                var name = jQuery(s.passwordPolicy.fields[value]).val();
                if(name.replace(' ','').length){
                  if(password.toLowerCase().indexOf(name.toLowerCase()) > -1){
                    if(showMessage){ IFS.formValidation.setInvalid(field,s.passwordPolicy.messageInvalid[value]);}
                    confirmsToPasswordPolicy = false;
                  }
                  else {
                      if(showMessage){ IFS.formValidation.setValid(field,s.passwordPolicy.messageInvalid[value]);}
                  }
                }
              });
            }
            return confirmsToPasswordPolicy;
        },
        checkEmail : function(field,showMessage){
            //checks if the email is valid, the almost rfc compliant check. The same as the java check, see http://www.regular-expressions.info/email.html
            var email = field.val();
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i;
            var errorMessage = IFS.formValidation.getErrorMessage(field,'email');

            var validEmail = re.test(email);
            if(!validEmail){
              if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage); }
              return false;
            }
            else {
              if(showMessage) { IFS.formValidation.setValid(field,errorMessage); }
              return true;
            }
        },
        checkNumber : function(field,showMessage){
            //https://api.jquery.com/jQuery.isNumeric/
             if(!jQuery.isNumeric(field.val())){
              if(showMessage) { IFS.formValidation.setInvalid(field,s.number.messageInvalid);}
              return false;
            }
            else{
              if(showMessage) { IFS.formValidation.setValid(field,s.number.messageInvalid);}
              return true;
            }
        },
        checkMax : function(field,showMessage){
            var max = parseInt(field.attr('max'),10);
            var errorMessage = IFS.formValidation.getErrorMessage(field,'max');

            if(IFS.formValidation.checkNumber(field,true)){
              var fieldVal = parseInt(field.val(),10);
              if(fieldVal > max){
                if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
                return false;
              }
              else {
                if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
                return true;
              }
            }
        },
        checkMin : function(field,showMessage){
            var min = parseInt(field.attr('min'),10);
            var errorMessage = IFS.formValidation.getErrorMessage(field,'min');

            if(IFS.formValidation.checkNumber(field)){
              var fieldVal = parseInt(field.val(),10);
              if(fieldVal < min){
                if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
                return false;
              }
              else {
                if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
                return true;
              }
            }
        },
        checkRequired : function(field,showMessage){
            var errorMessage = IFS.formValidation.getErrorMessage(field,'required');
            if(field.is(':checkbox')){
               if(!field.prop('checked')){
                 if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
                 return false;
               }
               else {
                 if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
                 return true;
               }
            }
            else {
              if(field.val().length === 0){
                if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
                return false;
              }
              else {
                if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
                return true;
              }
            }
        },
        checkMinLength : function(field,showMessage){
            var errorMessage = IFS.formValidation.getErrorMessage(field,'minlength');
            var minlength = parseInt(field.attr('minlength'),10);
            if(field.val().length < minlength){
              if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
              return false;
            }
            else {
              if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
              return true;
            }
        },
        checkMaxLength : function(field,showMessage){
          var errorMessage = IFS.formValidation.getErrorMessage(field,'maxlength');
          var maxlength = parseInt(field.attr('maxlength'),10);
          if(field.val().length > maxlength){
            if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
            return false;
          }
          else {
            if(showMessage) {IFS.formValidation.setValid(field,errorMessage);}
            return true;
          }
        },
        checkTel : function(field,showMessage){
            var tel = field.val();
            var errorMessage = IFS.formValidation.getErrorMessage(field,'tel');
            var re = /^(?=.*[0-9])[- +()0-9]+$/;
            var validPhone = re.test(tel);

            if(!validPhone){
              if(showMessage) { IFS.formValidation.setInvalid(field,errorMessage);}
              return false;
            }
            else {
              if(showMessage) { IFS.formValidation.setValid(field,errorMessage);}
              return true;
            }
        },
        checkDate : function(field,showMessage){
          var errorMessage = IFS.formValidation.getErrorMessage(field,'date');
          var dateGroup = field.closest('.date-group');
          var d = dateGroup.find('.day input');
          var m = dateGroup.find('.month input');
          var y = dateGroup.find('.year input');

          if(IFS.formValidation.checkNumber(d,false) && IFS.formValidation.checkNumber(m,false) && IFS.formValidation.checkNumber(y,false)){
            var month = parseInt(m.val(),10);
            var day = parseInt(d.val(),10);
            var year = parseInt(y.val(),10);

            var date = new Date(year,month-1,day); //parse as date to check if it is a valid date
            if ((date.getDate() == day) && (date.getMonth() + 1 == month) && (date.getFullYear() == year)) {
                if(showMessage){ IFS.formValidation.setValid(d.add(m).add(y),errorMessage); }
                d.add(m).add(y).removeClass('js-autosave-disabled').attr('data-date',day+'-'+month+'-'+year);
                return true;
            } else {
                if(showMessage){ IFS.formValidation.setInvalid(d.add(m).add(y),errorMessage); }
                d.add(m).add(y).addClass('js-autosave-disabled').attr('data-date','');
                return false;
            }
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
               }
            }
            if(jQuery('.error-summary-list li:contains('+message+')').length){
              jQuery('.error-summary-list li:contains('+message+')').remove();
            }

            if(jQuery('.error-summary-list li').length === 0){
              jQuery('.error-summary').attr('aria-hidden',true);
            }
            jQuery(window).trigger('updateWysiwygPosition');
        }
    };
})();
