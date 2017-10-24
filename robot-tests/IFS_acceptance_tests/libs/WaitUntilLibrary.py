from logging import warn
from robot.libraries.BuiltIn import BuiltIn
s2l = BuiltIn().get_library_instance('Selenium2Library')

# Use of an auto incremental integer to track the waiting per request in a dictionary
# When currently_waiting_for_keyword_to_succeed[int] is True, wait for this request.
currently_waiting_for_keyword_to_succeed = {}
auto_increment_id = 0


# a decorator that sets and unsets a special flag when performing "Wait until" keywords and enforces that the
# screenshots are only taken when failure results from a genuine test failure
def setting_wait_until_flag(func):

  def decorator(*args):
    global auto_increment_id
    global currently_waiting_for_keyword_to_succeed
    auto_increment_id += 1
    local_auto_increment = auto_increment_id

    currently_waiting_for_keyword_to_succeed[local_auto_increment] = True
    try:
      result = func(*args)
    except:
      __capture_page_screenshot_on_failure(local_auto_increment)
      raise
    finally:
      currently_waiting_for_keyword_to_succeed[local_auto_increment] = False
      del currently_waiting_for_keyword_to_succeed[local_auto_increment]
    return result

  return decorator


@setting_wait_until_flag
def wait_until_keyword_succeeds_without_screenshots(retry, retry_interval, keyword, *args):
  return BuiltIn().wait_until_keyword_succeeds(retry, retry_interval, keyword, *args)


@setting_wait_until_flag
def wait_until_element_is_visible_without_screenshots(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_visible(locator, timeout, error)


@setting_wait_until_flag
def wait_until_element_is_not_visible_without_screenshots(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_not_visible(locator, timeout, error)


@setting_wait_until_flag
def wait_until_page_contains_without_screenshots(text, timeout=None, error=None):
  return s2l.wait_until_page_contains(text, timeout, error)


@setting_wait_until_flag
def wait_until_page_contains_element_without_screenshots(locator, timeout=None, error=None):
  return s2l.wait_until_page_contains_element(locator, timeout, error)


@setting_wait_until_flag
def wait_until_page_does_not_contain_without_screenshots(text, timeout=None, error=None):
  return s2l.wait_until_page_does_not_contain(text, timeout, error)


@setting_wait_until_flag
def wait_until_element_contains_without_screenshots(locator, text, timeout=None, error=None):
  return s2l.wait_until_element_contains(locator, text, timeout, error)


@setting_wait_until_flag
def wait_until_element_does_not_contain_without_screenshots(locator, text, timeout=None, error=None):
  return s2l.wait_until_element_does_not_contain(locator, text, timeout, error)


@setting_wait_until_flag
def wait_until_element_is_enabled_without_screenshots(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_enabled(locator, timeout, error)


@setting_wait_until_flag
def run_keyword_and_ignore_error_without_screenshots(keyword, *args):
  return BuiltIn().run_keyword_and_ignore_error(keyword, *args)


@setting_wait_until_flag
def run_keyword_and_return_status_without_screenshots(keyword, *args):
  return BuiltIn().run_keyword_and_return_status(keyword, *args)

# Using the keyword Capture Page Screenshot On Failure as an autonomous keyword
# will raise the error "Keyword 'Capture Page Screenshot On Failure' could not be run on failure: KeyError: 0"
# that is because the flag is not set when this function gets called without setting_wait_until_flag and defaults to 0
def __capture_page_screenshot_on_failure(flag = 0):
  if not currently_waiting_for_keyword_to_succeed[flag]:
    capture_large_screenshot()


def capture_large_screenshot():
  currentWindow = s2l.get_window_size()
  page_height = s2l._current_browser().execute_script("return typeof(jQuery) !== 'undefined' ? jQuery(document).height() : 1080;")

  page_width = currentWindow[0]
  original_height = currentWindow[1]

  s2l.set_window_size(page_width, page_height)
  warn("Capturing a screenshot at URL " + s2l.get_location())
  s2l.capture_page_screenshot()
  s2l.set_window_size(page_width, original_height)
