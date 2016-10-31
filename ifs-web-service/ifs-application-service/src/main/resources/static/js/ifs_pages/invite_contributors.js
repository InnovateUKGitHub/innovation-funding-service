/*jshint multistr: true */
IFS.application.invites = (function() {
  "use strict";
  var s;
  return {
    settings : {
      formid : '.contributorsForm',
      inputs :  '.contributorsForm input',
      addRow : '.contributorsForm .add-another-row',
      removeRow : '.contributorsForm .remove-another-row',
      addOrg : '[name="add_partner"]',
      partnerName : '.js-partner-name'
    },
    init : function() {
      s = this.settings;
      jQuery('body').on('change', s.inputs, function() {
        IFS.application.invites.saveToCookie();
      });
      jQuery('body').on('click', s.addRow, function(e) {
        IFS.application.invites.addRow(e);
      });
      jQuery('body').on('click', s.removeRow, function(e) {
        IFS.application.invites.removeRow(e);
      });
      jQuery('body').on('click', s.addOrg, function(e) {
        IFS.application.invites.addOrg(e);
      });
      jQuery('body').on('keyup', s.partnerName, function(e) {
        IFS.application.invites.parterNameInHeader(e.target);
      });
      //fill out once at pageload
      jQuery(s.partnerName).each(function() {
        IFS.application.invites.parterNameInHeader(this);
      });
    },
    saveToCookie : function() {
      //cookie is updated simply by having an ajax call go to the server
      var data = (jQuery(s.formid).serialize());
      jQuery.ajaxProtected({
        url: window.location.href,
        method: "POST",
        dataType : 'json', //for requests headers so that the back-end can give a version without 302 redirect
        data: data
      });
    },
    addRow : function(e) {
      e.preventDefault();
      var orgContainer = jQuery(e.target).closest('[data-invite-org]');
      var orgId = orgContainer.index();
      var rowId = orgContainer.find('[data-invite-row]').length;
      var html = '<tr data-invite-row>\
      <td class="form-group">\
      <label for="organisations'+orgId+'.invites'+rowId+'.personName"><span class="visuallyhidden">Name</span></label>\
      <input type="text" id="organisations'+orgId+'.invites'+rowId+'.personName" class="form-control width-full" value="" placeholder="name" name="organisations['+orgId+'].invites['+rowId+'].personName"  />\
      </td>\
      <td class="form-group">\
      <label for="organisations'+orgId+'.invites'+rowId+'.email"><span class="visuallyhidden">Email</span></label>\
      <input type="email" class="form-control width-full" value="" placeholder="name@company.co.uk" name="organisations['+orgId+'].invites['+rowId+'].email"  />\
      </td>\
      <td class="alignright"><button value="'+orgId+'_'+rowId+'" name="remove_person" type="submit" class="remove-another-row buttonlink">Remove</button></td>\
      </tr>';
      orgContainer.find('tbody').append(html);
      jQuery('[id="organisations'+orgId+'.invites'+rowId+'.personName"]').focus();
      IFS.application.invites.saveToCookie();
    },
    addOrg : function(e) {
      e.preventDefault();
      var currentOrgs = jQuery('[data-invite-org]');
      var orgId = currentOrgs.length;
      var html = '<li data-invite-org>\
      <h2 class="heading-medium">Partner Organisation<span></span></h2>\
      <div class="form-group">\
      <label for="organisations'+orgId+'.organisationName" class="form-label"><span>Organisation Name</span></label>\
      <input type="text" id="organisations'+orgId+'.organisationName" value="" name="organisations['+orgId+'].organisationName"  placeholder="Name of the partner company" class="form-control js-partner-name" />\
      </div>\
      <table>\
      <thead><tr>\
      <th>Applicant name</th>\
      <th>Email</th>\
      <th>&nbsp;</th>\
      </tr></thead>\
      <tbody>\
      <tr data-invite-row>\
      <td class="form-group">\
      <label for="organisations'+orgId+'.invites0.personName"><span class="visuallyhidden">Name</span></label>\
      <input type="text" value="" id="organisations'+orgId+'.invites0.personName" name="organisations['+orgId+'].invites[0].personName"  placeholder="name" class="form-control width-full" />\
      </td>\
      <td class="form-group">\
      <label for="organisations'+orgId+'.invites0.email"><span class="visuallyhidden">Email</span></label>\
      <input type="email" value="" id="organisations'+orgId+'.invites0.email" name="organisations['+orgId+'].invites[0].email" placeholder="name@company.co.uk" class="form-control width-full" />\
      </td>\
      <td class="alignright"><button value="'+orgId+'_0" name="remove_person" type="submit" class="remove-another-row buttonlink">Remove</button></td>\
      </tr>\
      </tbody>\
      </table>\
      <p class="alignright">\
      <button value="'+orgId+'" name="add_person" type="submit" class="add-another-row buttonlink">Add another person</button>\
      </p>\
      </li>';

      currentOrgs.last().after(html);
      jQuery('[id="organisations'+orgId+'.organisationName"]').focus();
      IFS.application.invites.saveToCookie();
    },
    recountRows : function() {
      jQuery('[data-invite-org]').each(function(orgId, orgEl) {
        //update organisations
        jQuery(orgEl).find('[name*="organisations["]').each(function() {
          var newName = jQuery(this).attr('name').split('.');
          newName[0] = 'organisations['+orgId+']';
          newName = newName.join('.');
          jQuery(this).attr('name', newName);
        });
        //update invite rows
        jQuery(orgEl).find('[data-invite-row]').each(function(inviteeId, inviteeRow) {
          jQuery(inviteeRow).find('[name*="invites["]').each(function() {
            var newName = jQuery(this).attr('name').split('.');
            newName[1] = 'invites['+inviteeId+']';
            newName = newName.join('.');
            jQuery(this).attr('name', newName);
          });
        });
      });
      IFS.application.invites.saveToCookie();
    },
    removeRow : function(e) {
      e.preventDefault();
      var button = jQuery(e.target);
      //if there is only one row left we remove the organisation from the form
      if(button.closest('[data-invite-org]').find('tbody tr').length == 1){
        button.closest('[data-invite-org]').remove();
      }
      else {
        button.closest('tr').remove();
      }
      IFS.application.invites.recountRows();
    },
    parterNameInHeader : function(element) {
      var input = jQuery(element);
      var header = input.closest('[data-invite-org]').children('h2');
      if(header.children('span').length === 0){
        header.append(' <span></span>');
      }
      var output = input.val().length ? '"'+input.val()+'"' : '';
      header.children('span').text(output);
    }
  };
})();
