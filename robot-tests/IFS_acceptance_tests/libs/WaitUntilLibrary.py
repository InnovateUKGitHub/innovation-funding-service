import traceback

from logging import warn
from robot.libraries.BuiltIn import BuiltIn
s2l = BuiltIn().get_library_instance('Selenium2Library')

currently_waiting_for_keyword_to_succeed = False



# a decorator that sets and unsets a special flag when performing "Wait until" keywords and enforces that the
# screenshots are only taken when failure results from a genuine test failure
def setting_wait_until_flag(func):

  def decorator(*args, **kwargs):

    global currently_waiting_for_keyword_to_succeed

    currently_waiting_for_keyword_to_succeed = True
    try:
      result = func(*args, **kwargs)
    except:
      do_capture_page_screenshot()
      raise
    finally:
      currently_waiting_for_keyword_to_succeed = False
    return result

  return decorator


@setting_wait_until_flag
def ifs_wait_until_keyword_succeeds(retry, retry_interval, keyword, *args):
  return BuiltIn().wait_until_keyword_succeeds(retry, retry_interval, keyword, *args)


@setting_wait_until_flag
def ifs_wait_until_element_is_visible(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_visible(locator, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_element_is_not_visible(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_not_visible(locator, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_page_contains(text, timeout=None, error=None):
  return s2l.wait_until_page_contains(text, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_page_contains_element(locator, timeout=None, error=None):
  return s2l.wait_until_page_contains_element(locator, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_page_does_not_contain(text, timeout=None, error=None):
  return s2l.wait_until_page_does_not_contain(text, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_element_contains(locator, text, timeout=None, error=None):
  return s2l.wait_until_element_contains(locator, text, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_element_does_not_contain(locator, text, timeout=None, error=None):
  return s2l.wait_until_element_does_not_contain(locator, text, timeout, error)


@setting_wait_until_flag
def ifs_wait_until_element_is_enabled(locator, timeout=None, error=None):
  return s2l.wait_until_element_is_enabled(locator, timeout, error)


@setting_wait_until_flag
def ifs_run_keyword_and_ignore_error(keyword, *args):
  return BuiltIn().run_keyword_and_ignore_error(keyword, *args)


@setting_wait_until_flag
def ifs_run_keyword_and_return_status(keyword, *args):
  return BuiltIn().run_keyword_and_return_status(keyword, *args)


def capture_page_screenshot_on_failure():
  if not currently_waiting_for_keyword_to_succeed:
    do_capture_page_screenshot()


def do_capture_page_screenshot():
  s2l.set_window_size(1920, 8000)
  warn("Capturing a screenshot")
  s2l.capture_page_screenshot()
  s2l.set_window_size(1920, 1080)