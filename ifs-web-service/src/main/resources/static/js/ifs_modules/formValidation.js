IFS.formValidation = (function(){
    "use strict";
    var s; 
    return {
        settings : {
            number : {
                fields : '[type="number"]',
                messageInvalid : 'This field should be a number'
            },
            password: {
                field1 : '[name="password"]',
                field2 : '[name="retypedPassword"]',
                messageInvalid : 'Passwords must match'
            },
            typeTimeout : 800
        },
        init : function(){
            s = this.settings; 

            IFS.formValidation.initPasswordCheck(); //checks if password and retyped password are equal
            IFS.formValidation.initNumberCheck();   //checks if it is a number by using jQuery.isNumeric (https://api.jquery.com/jQuery.isNumeric/)
        },
        initPasswordCheck : function(){
            var passwordFields = s.password.field1+','+s.password.field2;

            jQuery('body').on('change', passwordFields, function(){ 
                IFS.formValidation.checkPasswords(jQuery(this));
            });

            jQuery('body').on('keyup', passwordFields , function(){ 
                clearTimeout(window.IFS.formValidation_timer);
                window.IFS.formValidation_timer = setTimeout(function(){ 
                    IFS.formValidation.checkPasswords(jQuery(this)); 
                }, s.typeTimeout);
            });
        },
        initNumberCheck : function(){
            jQuery('body').on('change', s.number.fields , function(){ 
                IFS.formValidation.checkNumber(jQuery(this));
            });
        },
        checkPasswords : function(){
            var pw1 = jQuery(s.password.field1);
            var pw2 = jQuery(s.password.field2);

            if(pw1.val().length && pw2.val().length){
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
        checkNumber : function(field){
            if(jQuery.isNumeric(field.val())){
                IFS.formValidation.setValid(field,s.number.messageInvalid);
            }
            else {
                IFS.formValidation.setInvalid(field,s.number.messageInvalid);
            }
        },
        setInvalid : function(field,message){
            var formGroup = field.closest('.form-group');
            if(formGroup){
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
               formGroup.find('.error-message:contains("'+message+'")').remove();
               
               //if this was the last error we remove this one
               if(formGroup.find('.error-message').length === 0){
                   formGroup.removeClass('error');
               }
            }
        }
    };
})();
