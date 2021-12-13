from selenium import webdriver
import requests
import Libs.Basic_tools
import sys
sys.path.append('../Serv_lib')
from Serv_lib import add_projects

def add_projects(projectLists):
    response = add_projects(projectLists)
    print(response)

class ParisFriendlyScraper:

    def __init__(self):
        firefoxOptions = webdriver.FirefoxOptions()
        firefoxOptions.set_headless()
        self.driver = webdriver.Firefox(firefox_options=firefoxOptions)
        self.driver = webdriver.Firefox()

    def get_sub_informations(self, sublink):
        self.driver.get(sublink)
        title = self.driver.find_element_by_css_selector('.title h1').get_attribute('innerHTML')
        descr = self.driver.find_element_by_css_selector('.content p').get_attribute('innerText')
        informations = self.driver.find_elements_by_css_selector('.details ul li p')
        date = informations[0].get_attribute('innerText')
        place = informations[-1].get_attribute('innerText')
        project = {"title" : title, "descr" : descr, "date" : date, "place" : place}
        return project

    def purge_list(self, list):
        purgedList = []
        for element in list:
            if element not in purgedList:
                purgedList.append(element)
        return purgedList

    def build_projects(self, titlesList, placesList, descrList, datesList, sublinksList):
        projects = {}
        length = min(len(titlesList), len(placesList), len(descrList), len(datesList), len(sublinksList), len(picturesList))
        projects["length"] = length
        projects["items"] = []
        for i in range(length):
            dictProject = {}
            dictProject["project"] = {}
            dictProject["project"]["title"] = titlesList[i]
            dictProject["project"]["summary"] = descrList[i]
            dictProject["project"]["website"] = sublinksList[i]
            dictProject["project"]["picture"] = ""
            dictProject["project"]["child_friendly"]="N"
            dictProject["meeting"] = {}
            dictProject["meeting"]["date"] = datesList[i]
            dictProject["meeting"]["time"] = ""
            dictProject["meeting"]["place"] = placesList[i]
            dictProject["meeting"]["format"] = "distanciel"
            projects["items"].append(dictProject)

        return projects

    def scrap(self):
        self.driver.get("https://www.paris-friendly.fr/loisirs-paris/visite-paris")
        sublinksElems = self.driver.find_elements_by_css_selector(".item a")
        sublinks = [sublink.get_attribute('href') for sublink in sublinksElems]
        goodSublinks = []
        for sublink in sublinks:
            if '.php' not in sublink and 'html' in sublink:
                goodSublinks.append(sublink)
        goodSublinks = self.purge_list(goodSublinks)
        datesList = []
        locationsList = []
        descrList = []
        titlesList = []
        for sublink in goodSublinks:
            project = self.get_sub_informations(sublink)
            datesList.append(project["date"])
            locationsList.append(project["place"])
            descrList.append(project["descr"])
            titlesList.append(project["title"])
        
        self.projectsList = self.build_projects(titlesList, locationsList, descrList, datesList, goodSublinks)

        return projectsList

if __name__ == '__main__':
    scraper = ParisFriendlyScraper()
    projectsList = scraper.scrap()
    add_projects(projectsList)

