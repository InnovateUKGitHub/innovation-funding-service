IFS.core.upload = (function() {
  "use strict";
  var s; // private alias to settings
  return {
    settings : {
      uploadEl : '[type="file"][class="inputfile"]'
    },
    init : function() {
      s = this.settings;
      jQuery('body').on('change', s.uploadEl, function() {
        var fileInputId = jQuery(this).attr("id");
        jQuery('[data-for-file-upload="' + fileInputId + '"]').click();
      });
    }
  };
})();
