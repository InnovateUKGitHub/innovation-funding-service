/**
 *  jQuery plugin to provide an Ajax request supplying the CSRF token in the request header.
 *  Use this instead of globally applying the header using $.ajaxSetup which would affect all requests using jQuery, not just those going to IFS.
 *  Adapted from Django's guide to it's Double Cookie Submission prevention method - https://docs.djangoproject.com/ja/1.9/ref/csrf
 */
(function ($) {
  "use strict";
  $.ajaxProtected = function (options) {

    var CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    var CSRF_COOKIE_NAME = "CSRF-TOKEN";
    var beforeSend = options.beforeSend;

    /**
     * Check if the method requires CSRF protection.
     * @param method the HTTP method
     * @returns {boolean} true if the method requires CSRF protection, otherwise false.
     */
    function csrfProtectedMethod(method) {
      return !(/^(GET|HEAD|TRACE|OPTIONS)$/.test(method));
    }

    /**
     * Get the CSRF token from the cookie.
     * @returns {string}
     */
    function csrfToken() {
      return Cookies.get(CSRF_COOKIE_NAME);
    }


    options.beforeSend = function (xhr, settings) {
      if (csrfProtectedMethod(settings.type) && !this.crossDomain) {
        xhr.setRequestHeader(CSRF_HEADER_NAME, csrfToken());
      }

      // call the existing beforeSend function if it was defined
      if (typeof beforeSend !== 'undefined') {
        beforeSend.call(this, arguments);
      }
    };

    return this.ajax(options);
  };
}(jQuery));
