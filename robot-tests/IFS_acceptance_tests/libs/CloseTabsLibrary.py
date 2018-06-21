import selenium.webdriver as webdriver
import selenium.webdriver.support.ui as ui
from selenium.webdriver.common.keys import Keys
from robot.libraries.BuiltIn import BuiltIn

se2lib = BuiltIn().get_library_instance('Selenium2Library')

def get_webdriver_instance():
    return se2lib._current_browser()

def close_tabs():
    get_webdriver_instance().switch_to.(get_webdriver_instance().window_handles[0])
    get_webdriver_instance().close()
    get_webdriver_instance().switch_to.window(get_webdriver_instance().window_handles[-1])
