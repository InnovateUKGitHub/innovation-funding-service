import time
import robot
import os
from robot.libraries.BuiltIn import BuiltIn
s2l = BuiltIn().get_library_instance('Selenium2Library')

class IFS_Selenium_Extension:
    

    def benchmarking_is_set_up(self, timeout=None):
      """Sets up the benchmarking test by deleting the old report, if it exists, and raising
      any other errors"""
      try:
        os.remove('benchmarking_report.txt') 
      except OSError as e: 
          if e.errno != errno.ENOENT:
            raise 

    def time_for_condition(self, condition, benchmark_step, timeout=None, error=None):
        """Waits until the given `condition` is true or `timeout` expires,
        and then reports the time taken to a custom file.

        The `condition` can be arbitrary JavaScript expression but must contain a 
        return statement (with the value to be returned) at the end.
        See `Execute JavaScript` for information about accessing the
        actual contents of the window through JavaScript.

        `error` can be used to override the default error message.

        See `introduction` for more information about `timeout` and its
        default value.

        See also `Time Until Page Contains`, `Time Until Page Contains
        Element`, `Time Until Element Is Visible` and BuiltIn keyword
        `Wait Until Keyword Succeeds`.
        """
        if not error:
            error = "Condition '%s' did not become true in <TIMEOUT>" % condition
        self._time_until(timeout, error, benchmark_step,
                         lambda: s2l._current_browser().execute_script(condition) == True)


    def time_until_page_contains(self, text, benchmark_step, timeout=None, error=None):
        """Waits until `text` appears on current page, and then reports
        the time taken to a custom file.

        Fails if `timeout` expires before the text appears. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains Element`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        if not error:
            error = "Text '%s' did not appear in <TIMEOUT>" % text
        self._time_until(timeout, error, benchmark_step, s2l._is_text_present, text)

    def time_until_page_does_not_contain(self, text, benchmark_step, timeout=None, error=None):
        """Waits until `text` disappears from current page. and then reports
        the time taken to a custom file.

        Fails if `timeout` expires before the `text` disappears. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        def check_present():
            present = s2l._is_text_present(text)
            if not present:
                return
            else:
                return error or "Text '%s' did not disappear in %s" % (text, self._format_timeout(timeout))
        self._time_until_no_error(timeout, check_present, benchmark_step)

    def time_until_page_contains_element(self, locator, benchmark_step, timeout=None, error=None):
        """Waits until element specified with `locator` appears on current page,
        and then reports the time taken to a custom file.

        Fails if `timeout` expires before the element appears. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        if not error:
            error = "Element '%s' did not appear in <TIMEOUT>" % locator
        self._time_until(timeout, error, benchmark_step, s2l._is_element_present, locator)

    def time_until_page_does_not_contain_element(self, locator, benchmark_step, timeout=None, error=None):
        """Waits until element specified with `locator` disappears from current page,
        and then reports the time taken to a custom file.

        Fails if `timeout` expires before the element disappears. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        def check_present():
            present = s2l._is_element_present(locator)
            if not present:
                return
            else:
                return error or "Element '%s' did not disappear in %s" % (locator, self._format_timeout(timeout))
        self._time_until_no_error(timeout, check_present, benchmark_step)

    def time_until_element_is_visible(self, locator, benchmark_step, timeout=None, error=None):
        """Waits until element specified with `locator` is visible, and
        then reports the time taken to a custom file.

        Fails if `timeout` expires before the element is visible. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time Until Page Contains
        Element`, `Time For Condition` and BuiltIn keyword `Wait Until Keyword
        Succeeds`.
        """
        def check_visibility():
            visible = s2l._is_visible(locator)
            if visible:
                return
            elif visible is None:
                return error or "Element locator '%s' did not match any elements after %s" % (locator, self._format_timeout(timeout))
            else:
                return error or "Element '%s' was not visible in %s" % (locator, self._format_timeout(timeout))
        self._time_until_no_error(timeout, check_visibility, benchmark_step)
    
    def time_until_element_is_not_visible(self, locator, benchmark_step, timeout=None, error=None):
        """Waits until element specified with `locator` is not visible, and
        then reports the time taken to a custom file.

        Fails if `timeout` expires before the element is not visible. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time Until Page Contains
        Element`, `Time For Condition` and BuiltIn keyword `Wait Until Keyword
        Succeeds`.
        """
        def check_hidden():
            visible = s2l._is_visible(locator)
            if not visible:
                return
            elif visible is None:
                return error or "Element locator '%s' did not match any elements after %s" % (locator, self._format_timeout(timeout))
            else:
                return error or "Element '%s' was still visible in %s" % (locator, self._format_timeout(timeout))
        self._time_until_no_error(timeout, check_hidden, benchmark_step)

    def time_until_element_is_enabled(self, locator, benchmark_step, timeout=None, error=None):
        """Waits until element specified with `locator` is enabled, and
        then reports the time taken to a custom file.

        Fails if `timeout` expires before the element is enabled. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time Until Page Contains
        Element`, `Time For Condition` and BuiltIn keyword `Wait Until Keyword
        Succeeds`.
        """
        def check_enabled():
            element = s2l._element_find(locator, True, False)
            if not element:
                return error or "Element locator '%s' did not match any elements after %s" % (locator, self._format_timeout(timeout))

            enabled = not element.get_attribute("disabled")
            if enabled:
                return
            else:
                return error or "Element '%s' was not enabled in %s" % (locator, self._format_timeout(timeout))

        self._time_until_no_error(timeout, check_enabled, benchmark_step)

    def time_until_element_contains(self, locator, text, benchmark_step, timeout=None, error=None):
        """Waits until given element contains `text`, and then reports
        the time taken to a custom file.

        Fails if `timeout` expires before the text appears on given element. See
        `introduction` for more information about `timeout` and its
        default value.
*
        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time Until Page Contains Element`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        element = s2l._element_find(locator, True, True)
        def check_text():
            actual = element.text
            if text in actual:
                return
            else:
                return error or "Text '%s' did not appear in %s to element '%s'. " \
                            "Its text was '%s'." % (text, self._format_timeout(timeout), locator, actual)
        self._time_until_no_error(timeout, check_text, benchmark_step)


    def time_until_element_does_not_contain(self, locator, text, benchmark_step, timeout=None, error=None):
        """Waits until given element does not contain `text`, and then reports
        the time taken to a custom file.

        Fails if `timeout` expires before the text disappears from given element. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Time Until Page Contains`, `Time Until Page Contains Element`, `Time For Condition`,
        `Time Until Element Is Visible` and BuiltIn keyword `Wait Until
        Keyword Succeeds`.
        """
        element = s2l._element_find(locator, True, True)
        def check_text():
            actual = element.text
            if not text in actual:
                return
            else:
                return error or "Text '%s' did not disappear in %s from element '%s'." % (text, self._format_timeout(timeout), locator)
        self._time_until_no_error(timeout, check_text, benchmark_step)

    # Private

    def _time_until(self, timeout, error, benchmark_step, function, *args):
        error = error.replace('<TIMEOUT>', self._format_timeout(timeout))
        def wait_func():
            return None if function(*args) else error
        self._time_until_no_error(timeout, wait_func, benchmark_step)

    def _time_until_no_error(self, timeout, wait_func, benchmark_step, *args):
        timeout = robot.utils.timestr_to_secs(timeout) if timeout is not None else s2l._timeout_in_secs
        start_time = time.time()
        max_time = start_time + timeout
        while True:
            timeout_error = wait_func(*args)
            if not timeout_error:
                step_time = (time.time() - start_time)
                step_report = str(benchmark_step) + "    " + str(step_time) + "\n"
                with open('benchmarking_report.txt','a+') as f: f.write(str(step_report))
                return
            if time.time() > max_time:
                raise AssertionError(timeout_error)
            time.sleep(0.2)

    def _format_timeout(self, timeout):
        timeout = robot.utils.timestr_to_secs(timeout) if timeout is not None else s2l._timeout_in_secs
        return robot.utils.secs_to_timestr(timeout)
