import sys
from multiprocessing import Queue
from selenium import webdriver
from time import sleep
from selenium.common.exceptions import NoSuchElementException
from Libs.Cleaning_tools import purge_html
from Libs.Serv_lib import add_projects


class benenovaScraper:

    def __init__(self, moreResultsLevel):
        self.results = {}
        self.moreResultsLevel = moreResultsLevel
        firefoxOptions = webdriver.FirefoxOptions()
        firefoxOptions.set_headless()
        self.driver = webdriver.Firefox(firefox_options=firefoxOptions)

    #Construit la description complète du projet à partir de plusieurs paragraphes.
    def build_description(self, descrList):
        finalDescr = ""
        for descr in descrList:
            finalDescr+=descr
        return finalDescr

    #Renvoie la date au format "2021-09-19"
    def build_date(self, date):
        day = date[0:2]
        month = date[3:5]
        year = "20"+date[6:8]
        return year+"-"+month+"-"+day

    #Clique sur un projet pour récupérer sa date et sa description.
    def get_sub_informations(self, sublink):
        self.driver.get(sublink)
        elemDescr = self.driver.find_elements_by_css_selector("#opportunity-detail-description p span")
        descr = [elem.get_attribute("innerHTML") for elem in elemDescr]
        descr = self.build_description(descr)
        try:
            elemPlace = self.driver.find_element_by_css_selector(
                "p[style='background-color: #6D5E48; padding-left: 30px; padding-right:30px; padding-top: 12px; padding-bottom: 12px; border-radius: 5px; text-align: justify; color:#fff; font-size: 16px;']")
            place = elemPlace.get_attribute('innerText')[4:]
        except NoSuchElementException:
            try:
                elemPlace = self.driver.find_element_by_css_selector(
                    "p[style='background-color: rgb(109, 94, 72); padding: 12px 30px; border-radius: 5px; color: rgb(255, 255, 255); font-size: 16px; text-align: justify;']")
                place = elemPlace.get_attribute('innerText')[4:]
            except:
                place = "NO PLACE"

        return {"descr" : descr, "place" : place}

    def scrap(self):
        sleep(3)
        self.driver.get("https://www.benenova.fr/actions")
        self.driver.find_element_by_class_name("view-more-link").click()
        sleep(10)
        for i in range (self.moreResultsLevel):
            sleep(1)
            for i in self.driver.find_elements_by_class_name("view-more-link"):
                i.click()

        #On récupère les dates de chaque projet.
        elemsDates = self.driver.find_elements_by_class_name("date_time")
        dates = [self.build_date(elem.get_attribute('innerHTML')[:-5]) for elem in elemsDates]

        #On récupère les heures de chaque projet.
        hours = [elem.get_attribute('innerHTML')[-5:] for elem in elemsDates]

        #On récupère les adresses de chaque projet.
        #elemsLocation = self.driver.find_elements_by_class_name("location")
        #locations = [elem.get_attribute('innerHTML') for elem in elemsLocation]

        #On récupère les titres de chaque projet.
        elemsTitles = self.driver.find_elements_by_css_selector(".title a")
        titles = [elem.get_attribute('innerHTML') for elem in elemsTitles]

        #On récupère les images de chaque projet.
        elemPictures = self.driver.find_elements_by_class_name("image-background")
        pictures = [elem.get_attribute('style')[34:-31] for elem in elemPictures]
        
        #On récupère les liens de chaque projet.
        sublinks = [elem.get_attribute("href") for elem in elemsTitles]
        
        
        #On parcourt chacun des projets pour trouver la description et l'adresse.

        descriptions = []
        places = []
        for sublink in sublinks:
            infos = self.get_sub_informations(sublink)
            descriptions.append(infos["descr"])
            places.append(infos["place"])

        projects = self.build_projects(titles, places, descriptions, dates, hours, sublinks, pictures)
        return projects

    #A partir d'une liste de noms de projets, de liens de projets, de dates, d'heures, d'adresses et de descriptions
    #renvoie une liste de projets au format suivant : 
    #{"length" : length, "items" : [{"project" : {"title" : title, "summary" : descr, "website" : sublink, "picture" : picture, "child_friendly" : "..."}, "meeting" : {"date" : "2001-19-09", "time" : "15:00:00", "place" : place, "format" : "D"}}, {"project" : ....}]}
    def build_projects(self, titlesList, placesList, descrList, datesList, hoursList, sublinksList, picturesList):
        projects = {}
        length = min(len(titlesList), len(placesList), len(descrList), len(datesList), len(hoursList), len(sublinksList), len(picturesList))
        projects["length"] = length
        projects["items"] = []
        for i in range(length):
            dictProject = {}
            dictProject["project"] = {}
            dictProject["project"]["title"] = titlesList[i]
            dictProject["project"]["summary"] = descrList[i]
            dictProject["project"]["website"] = sublinksList[i]
            dictProject["project"]["picture"] = picturesList[i]
            dictProject["project"]["child_friendly"]="N"
            dictProject["meeting"] = {}
            dictProject["meeting"]["date"] = datesList[i]
            dictProject["meeting"]["time"] = hoursList[i]
            dictProject["meeting"]["place"] = placesList[i]
            dictProject["meeting"]["format"] = "D"
            projects["items"].append(dictProject)

        return projects

    def save_results(self, projects):
        results = projects["items"]
        file = open("Sauvegarde.txt", "w")
        for project in results:
            file.write("NEW PROJECT")
            for type in project:
                for elem in project[type]:
                    file.write(project[type][elem])

    #Ajoute les projets à la base de données.
    def add_projects(self, projects):
        response = add_projects(projects)
        print(response)



if __name__ == '__main__':
    benenovaScraper = benenovaScraper(int(sys.argv[1]))
    projects = benenovaScraper.scrap()
    print(projects)
    benenovaScraper.add_projects(projects)


