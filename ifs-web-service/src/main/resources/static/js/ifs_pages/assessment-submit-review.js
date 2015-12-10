//Brent: Did some investigation what this does as it wasn't clear to me
//Does Hide and show textarea and some basic data validation
//Hide and show of form elements are already within the gov.uk js and client side validation is nice but we should cover that more generic. 
IFS.assesment_submit_review_page = (function(){
    "use strict";
    return {
        init: function(){
          IFS.assesment_submit_review_page.suitableForFundingChange();
          IFS.assesment_submit_review_page.beforeSubmitCheck();
        },
        suitableForFundingChange : function(){
            jQuery( "#suitable-for-funding" ).change(function() {
              var optionSelected = jQuery( "#suitable-for-funding option:selected" ).val();
              var targetElement = jQuery("#recommendation-feedback-group");
              
              optionSelected = "no" ? targetElement.show() : targetElement.hide();
              jQuery("#not-suitable-feedback").prop('required', optionSelected == "no" );
            }).trigger( "change" );
        },
        beforeSubmitCheck : function(){
            jQuery( "#submission_questions" ).submit( function( event ) {
                   var recommendedValue = jQuery( "#suitable-for-funding option:selected" ).val();
                   var feedbackIsEmpty = jQuery("#not-suitable-feedback").val().trim()  === '';
                   if (recommendedValue == "no" && feedbackIsEmpty ) {
                        event.preventDefault();
                        jQuery( "#feedback-empty-error" ).text( "Please justify your decision..." ).show();
                        return false;
                   }
                   else {
                       jQuery( "#feedback-empty-error" ).hide();
                    }
            });
        }
    };
})();

