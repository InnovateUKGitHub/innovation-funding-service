
$(function() {
    $( "#suitable-for-funding" ).change(function() {
        var optionSelected = $( "#suitable-for-funding option:selected" ).val();
        var targetElement = $("#recommendation-feedback-group");
        optionSelected == "no" ? targetElement.show() : targetElement.hide();
        $("#not-suitable-feedback").prop('required', optionSelected == "no" );
      }).trigger( "change" );
});

$(function() {
    $( "#not-suitable-feedback" ).change(function() {
       var charactersLeft = maxCharacters - $("#not-suitable-feedback").val().length;
        $("#feedbackWordCount").text(charactersLeft);

     }).trigger( "change" );
});

$(function() {
    $( "#summary-comments" ).change(function() {
       var charactersLeft = maxCharacters - $("#summary-comments").val().length;
        $("#commentsWordCount").text(charactersLeft);
     }).trigger( "change" );
});




var maxCharacters = 350;


//
//function wordCount( val ){
//    return {
//        charactersNoSpaces : val.replace(/\s+/g, '').length,
//        characters         : val.length,
//        words              : val.match(/\S+/g).length,
//        lines              : val.split(/\r*\n/).length
//    };
//}




