import selenium.webdriver as webdriver
import selenium.webdriver.support.ui as ui
from selenium.webdriver.common.keys import Keys
from robot.libraries.BuiltIn import BuiltIn
import BuiltIn

s2l = BuiltIn().get_library_instance('Selenium2Library')

def open_close_tab():
    s2l.switch_to.window(s2l.window_handles[1])
    s2l.close()
    s2l.switch_to.window(s2l.window_handles[0])

close_tabs = open_close_tab()