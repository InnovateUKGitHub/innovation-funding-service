
$(function() {
    $( "#suitable-for-funding" ).change(function() {
        var optionSelected = $( "#suitable-for-funding option:selected" ).val();
        var targetElement = $("#recommendation-feedback-group");
        optionSelected == "no" ? targetElement.show() : targetElement.hide();
        $("#not-suitable-feedback").prop('required', optionSelected == "no" );
      }).trigger( "change" );
});

$(function() {
    $( "#submission_questions" ).submit( function( event ) {
           var recommendedValue = $( "#suitable-for-funding option:selected" ).val();
           var feedbackIsEmpty = $("#not-suitable-feedback").val().trim()  == '';


           if ( recommendedValue == "no" && feedbackIsEmpty ) {
                event.preventDefault();
                $( "#feedback-empty-error" ).text( "Please justify your decision..." ).show();
                return false;
           }
           else
               $( "#feedback-empty-error" ).hide();


    });
 });


//
//function wordCount( val ){
//    return {
//        charactersNoSpaces : val.replace(/\s+/g, '').length,
//        characters         : val.length,
//        words              : val.match(/\S+/g).length,
//        lines              : val.split(/\r*\n/).length
//    };
//}




