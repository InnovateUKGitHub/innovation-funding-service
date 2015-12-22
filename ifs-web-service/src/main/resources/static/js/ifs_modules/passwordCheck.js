//checks if passwords are equal, if there is no js it will be done on submit
IFS.passwordCheck = (function(){
    "use strict";
    var s; 
    return {
        settings : {
            passwordOne : '[name="password"]',
            passwordTwo : '[name="retypedPassword"]',
            typeTimeout : 800
        },
        init : function(){
            s = this.settings; 
            jQuery('body').on('change', s.passwordOne+','+s.passwordTwo , function(){ 
                IFS.passwordCheck.checkPasswords();
            });
            jQuery('body').on('keyup', s.passwordOne+','+s.passwordTwo , function(){ 
                clearTimeout(window.IFS.passwordCheck_timer);
                window.IFS.passwordCheck_timer = setTimeout(function(){ IFS.passwordCheck.checkPasswords(); }, s.typeTimeout);
            });
        },
        checkPasswords : function(){
            var pw1 = jQuery(s.passwordOne).val();
            var pw2 = jQuery(s.passwordTwo).val();

            if(pw1.length && pw2.length){
                if(pw1 == pw2){
                    IFS.passwordCheck.setValid();
                }
                else {
                    IFS.passwordCheck.setInvalid();
                }
            }
        },
        setInvalid : function(){
            var fieldsFormGroup = jQuery(s.passwordOne).add(s.passwordTwo).closest('.form-group');
            var message = "Passwords must match";
            var html = '<span class="error-message">'+message+'</span>';

            fieldsFormGroup.addClass('error');

            if(fieldsFormGroup.find('.error-message').length === 0){
                fieldsFormGroup.find('label').append(html);
            }
        },
        setValid : function(){
            var fieldsFormGroup = jQuery(s.passwordOne).add(s.passwordTwo).closest('.form-group');
            if(fieldsFormGroup.hasClass('error')){
               fieldsFormGroup.removeClass('error');
               fieldsFormGroup.find('.error-message').remove();
            }
        }
    };
})();
