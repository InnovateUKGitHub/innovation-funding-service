IFS.competition_setup = (function(){
    "use strict";
    var s;
    return {
        settings: {
          multiStream: "input[name='multipleStream']:checked",
          streamNameFields: '#competition-stream-name-fields'
        },
        init: function(){
            s = this.settings;
            IFS.competition_setup.hideStreamNameIfNoStreamSelected();
            jQuery("body").on('change', s.multiStream, function(){
                IFS.competition_setup.hideStreamNameIfNoStreamSelected();
            });
        },
        hideStreamNameIfNoStreamSelected: function(){
        	var streamNameFields = jQuery(s.streamNameFields);
        	var multiStreamValue = jQuery(s.multiStream).val();
        	if('yes' === multiStreamValue) {
        		streamNameFields.show();
        	} else {
        		streamNameFields.hide();
        	}
        }
    };
})();
