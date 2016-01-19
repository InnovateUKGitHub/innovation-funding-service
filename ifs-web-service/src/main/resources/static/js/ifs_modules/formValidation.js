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
                messageInvalid : 'This field should be %min%% or higher'
            },
            max : {
                fields: '[max]',
                messageInvalid : 'This field should be %max%% or lower'
            },
            password: {
                field1 : '[name="password"]',
                field2 : '[name="retypedPassword"]',
                messageInvalid : 'Passwords must match'
            },
            email : {
                fields : '[type="email"]',
                messageInvalid : "Please enter a valid emailaddress"
            },
            typeTimeout : 800
        },
        init : function(){
            s = this.settings;
            IFS.formValidation.initPasswordCheck(); //checks if password and retyped password are equal
            IFS.formValidation.initNumberCheck();   //checks if it is a number by using jQuery.isNumeric (https://api.jquery.com/jQuery.isNumeric/)
            IFS.formValidation.initEmailCheck();   //checks if the email is valid, the almost rfc compliant check. The same as the java check, see http://www.regular-expressions.info/email.html
            IFS.formValidation.initMinCheck();
            IFS.formValidation.initMaxCheck();
        },
        initPasswordCheck : function(){
            jQuery('body').on('change keyup', s.password.field1+','+s.password.field2, function(e){
                if(e.type == 'keyup'){
                    clearTimeout(window.IFS.formValidation_timer);
                    window.IFS.formValidation_timer = setTimeout(function(){
                        IFS.formValidation.checkPasswords();
                    }, s.typeTimeout);
                }
                else {
                    IFS.formValidation.checkPasswords();
                }
            });
        },
        checkPasswords : function(){
            var pw1 = jQuery(s.password.field1);
            var pw2 = jQuery(s.password.field2);

            //if both are on the page and have content (.val)
            if(pw1.length && pw2.length && pw1.val().length && pw2.val().length){
                if(pw1.val() == pw2.val()){
                    IFS.formValidation.setValid(pw1,s.password.messageInvalid);
                    IFS.formValidation.setValid(pw2,s.password.messageInvalid);
                }
                else {
                    IFS.formValidation.setInvalid(pw1,s.password.messageInvalid);
                    IFS.formValidation.setInvalid(pw2,s.password.messageInvalid);
                }
            }
        },
        initEmailCheck : function(){
            jQuery('body').on('change keyup', s.email.fields , function(e){
                var el = jQuery(e.target);

                if(e.type == 'keyup'){
                    clearTimeout(window.IFS.formValidation_timer);
                    window.IFS.formValidation_timer = setTimeout(function(){
                        IFS.formValidation.checkEmail(el);
                    }, s.typeTimeout);
                }
                else {
                    IFS.formValidation.checkEmail(el);
                }
            });
        },
        checkEmail : function(field){
            var email = field.val();
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i;
            var validEmail = re.test(email);
            if(validEmail){
                IFS.formValidation.setValid(field,s.email.messageInvalid);
            }
            else {
                IFS.formValidation.setInvalid(field,s.email.messageInvalid);
            }
        },
        initNumberCheck : function(){
            jQuery('body').on('change', s.number.fields , function(){
                IFS.formValidation.checkNumber(jQuery(this));
            });
        },
        checkNumber : function(field){
            if(jQuery.isNumeric(field.val())){
              IFS.formValidation.setValid(field,s.number.messageInvalid);
              return true;
            }
            else{
              IFS.formValidation.setInvalid(field,s.number.messageInvalid);
              return false;
            }
        },
        initMaxCheck : function(){
            jQuery('body').on('change', s.max.fields , function(){
                IFS.formValidation.checkMax(jQuery(this));
            });
        },
        checkMax : function(field){
            var max = parseInt(field.attr('max'));

            if(IFS.formValidation.checkNumber(field)){
              var fieldVal = parseInt(field.val());
              if(fieldVal > max){
                IFS.formValidation.setInvalid(field,s.max.messageInvalid.replace('%max%',max));
              }
              else {
                IFS.formValidation.setValid(field,s.max.messageInvalid.replace('%max%',max));
              }
            }

        },
        initMinCheck : function(){
            jQuery('body').on('change', s.min.fields , function(){
                IFS.formValidation.checkMin(jQuery(this));
            });
        },
        checkMin : function(field){
            var min = parseInt(field.attr('min'));

            if(IFS.formValidation.checkNumber(field)){
              var fieldVal = parseInt(field.val());
              if(fieldVal < min){
                IFS.formValidation.setInvalid(field,s.min.messageInvalid.replace('%min%',min));
              }
              else {
                IFS.formValidation.setValid(field,s.min.messageInvalid.replace('%min%',min));
              }
            }
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
                    formGroup.find('label').first().append(html);
                }
            }
        },
        setValid : function(field,message){
            var formGroup = field.closest('.form-group.error');
            if(formGroup){
              field.removeClass('field-error');

               formGroup.find('.error-message:contains("'+message+'")').remove();

               //if this was the last error we remove this one
               if(formGroup.find('.error-message').length === 0){
                   formGroup.removeClass('error');
               }
            }
        }
    };
})();
