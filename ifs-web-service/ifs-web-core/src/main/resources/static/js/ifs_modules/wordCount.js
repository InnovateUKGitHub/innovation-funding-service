IFS.core.wordCount = (function() {
  "use strict";
  var s;
  var typeTimeout;
  return {
    settings : {
      wordcountEl : ".word-count textarea",
      typeTimeout : 500
    },
    init : function() {
      s = this.settings;
      jQuery('body').on('change keyup', s.wordcountEl, function(e) {
        if(e.type == 'keyup'){
          clearTimeout(typeTimeout);
          typeTimeout = setTimeout(function() { IFS.core.wordCount.updateWordCount(e.target); }, s.typeTimeout);
        }
        else {
          IFS.core.wordCount.updateWordCount(e.target);
        }
      });
    },
    updateWordCount : function(textarea) {
      var field = jQuery(textarea);
      var value = field.val();

      //regex = replace newlines with space \r\n, \n, \r
      value = value.replace(/(\r\n|\n|\r)/gm, " ");
      //remove markdown lists ('* ','1. ','2. ','**','_') from markdown as it influences word count
      value = value.replace(/([[0-9]+\.\ |\*\ |\*\*|_)/gm, "");

      var words = jQuery.trim(value).split(' ');
      var count = 0;
      //for and not foreach becuase of ie7 performance.
      for (var i = 0; i < words.length; i++) {
        if(words[i].length > 0){
          count++;
        }
      }
      var delta = field.attr('data-max_words') - count;
      var countDownEl = field.parents(".word-count").find(".count-down");
      countDownEl.html('Words remaining: '+delta);
      if(delta < 0){
        countDownEl.removeClass("positive").addClass("negative");
      }else{
        countDownEl.removeClass("negative").addClass("positive");
      }
    }
  };
})();
