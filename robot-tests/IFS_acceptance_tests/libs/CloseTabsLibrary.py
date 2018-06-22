#from selenium import webdriver
# from selenium.webdriver.common.by import By
# from selenium.webdriver.support.ui import WebDriverWait
# from selenium.webdriver.support import expected_conditions as EC
from robot.libraries.BuiltIn import BuiltIn

se2lib = BuiltIn().get_library_instance('Selenium2Library')

def get_webdriver_instance():
    return se2lib._current_browser()  # This is used because switch_to needs a webdriver instance, not a Selenium2Library instance.

def switch_to_last_opened_tab_then_close_it():
    get_webdriver_instance().switch_to.window(get_webdriver_instance().window_handles[-1])  # Switches to the last opened tab.
    get_webdriver_instance().close()
    get_webdriver_instance().switch_to.window(get_webdriver_instance().window_handles[0])   # Switches back to the main tab.
