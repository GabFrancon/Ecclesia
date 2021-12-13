from selenium import webdriver
from webdrivermanager import GeckoDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions
from selenium.webdriver.common.by import By
from time import sleep
from Libs.Serv_lib import add_projects
from Libs.Cleaning_tools import purge_html

class airbnbScraper():

    def __init__(self):
        firefoxOptions = webdriver.FirefoxOptions()
        firefoxOptions.set_headless()
        self.driver = webdriver.Firefox(firefox_options=firefoxOptions)

    #Clique sur un projet et récupère toutes les informations nécessaires.
    def get_sub_informations(self, sublink):
        self.driver.get(sublink)
        self.driver.implicitly_wait(2)
        self.driver.find_element_by_class_name('_ejra3kg').click()
        title = self.driver.find_element_by_class_name('_14i3z6h').get_attribute('innerHTML')
        descr = self.driver.find_element_by_css_selector('._1d784e5 span').get_attribute('innerHTML')
        picture = self.driver.find_element_by_class_name('_6tbg2q').get_attribute('src')
        date = self.driver.find_element_by_class_name('_10esu7').get_attribute('innerHTML')
        hour = self.driver.find_element_by_class_name('_udw1qr').get_attribute('innerHTML')
        place = self.driver.find_element_by_class_name('_11waoz2').get_attribute('innerHTML')
        return {"project" : {"title" : title, "summary" : descr, "website" : sublink, "picture" : picture, "child_friendly" : "N"}, "meeting" : {"date" : "2001-19-09", "time" : "15:00:00", "place" : place, "format" : "D"}}

    def build_date(self, date):
        year = "2021"
        day = date[5:7]
        if day[1] == " ": 
            day[1] = day[0]
            day[0] = "0"
        month = date[7:]
        if month == "janvier": 
            month = "01"
        elif month == "fevrier":
            month = "02"
        elif month == "mars":
            month = "03"
        elif month ==  "avril":
            month = "04"
        elif month ==  "mai":
            month = "05"
        elif month ==  "juin":
            month = "06"
        elif month ==  "juillet":
            month = "07"
        elif month ==  "aout":
            month = "08"
        elif month ==  "septembre":
            month = "09"
        elif month ==  "octobre":
            month = "10"
        elif month ==  "novembre":
            month = "11"
        elif month ==  "decembre":
            month = "12"
        return year+"-"+month+"-"+day

    def build_hour(self, hour):
        pass

    def get_all_projects_France(self):
        wait = WebDriverWait(self.driver, 30)
        self.driver.get("https://www.airbnb.fr/s/experiences?click_referer=t%3ASEE_ALL%7Csid%3A153ebe1b-490f-496d-bab7-6474465ef89b%7Cst%3AEXPERIENCES_HIGHLIGHTED_CATEGORY_GROUPING&refinement_paths%5B%5D=%2Fexperiences%2FKG%2FTag%3A6951&last_search_session_id=153ebe1b-490f-496d-bab7-6474465ef89b&search_type=section_navigation")
        cookiesButton = wait.until(expected_conditions.element_to_be_clickable((By.CLASS_NAME, "_1qnlffd6")))
        cookiesButton.click()
        '''
        self.driver.find_element_by_class_name("_t6p96s").click()
        self.driver.find_element_by_id("filterItem-host_language-checkbox-experience_languages-2").click()
        #element = wait.until(expected_conditions.element_to_be_clickable((By.CLASS_NAME, "_m095vcq")))
        #element.click()
        self.driver.find_element_by_class_name("_m095vcq").click()
        '''


        #wait.until(expected_conditions.element_to_be_clickable((By.CLASS_NAME, "_sqvp1j")))
        sleep(2)
        sublinksElems = self.driver.find_elements_by_class_name("_1jhvjuo")
        sublinks = [sublink.get_attribute('href') for sublink in sublinksElems]
        print("SUBLINKS : ", sublinks)
        projectsList = []
        for sublink in sublinks:
            project = self.get_sub_informations(sublink)
            projectsList.append(project)
        return projectsList

    def build_projects(projectsList):
        projects = {}
        projects["length"] = len(projectsList)
        projects["items"] = projectsList
        return projects

    def add_projects(self):
        projects = purge_html(self.build_projects(self.get_all_projects_France()[0]))
        response = add_projects(projects)
        print(response)


if __name__ == '__main__':
    scraper = airbnbScraper()
    print(scraper.get_all_projects_France())
    scraper.add_projects()



