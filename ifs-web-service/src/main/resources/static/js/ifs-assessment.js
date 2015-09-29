
$(function() {
    $( "#suitable-for-funding" ).change(function() {
        var optionSelected = $( "#suitable-for-funding option:selected" ).val();
        var targetElement = $("#recommendation-feedback-group");
        optionSelected == "no" ? targetElement.show() : targetElement.hide();
        $("#not-suitable-feedback").prop('required', optionSelected == "no" );
      }).trigger( "change" );
});

