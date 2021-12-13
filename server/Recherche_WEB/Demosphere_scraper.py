from socket import CAN_EFF_MASK
from selenium import webdriver
import json
import sys
sys.path.append('../Serv_lib')
from Serv_lib import add_projects
from Libs.Cleaning_tools import purge_html


class demospherScraper():

    def __init__(self):
        firefoxOptions = webdriver.FirefoxOptions()
        firefoxOptions.set_headless()
        self.driver = webdriver.Firefox(firefox_options=firefoxOptions)

    #Sur le site, le mois est indiqué en toute lettre. Cette fonction retourne le nombre correspondant.
    def build_month(self, month):
        if month == "janvier": 
            return("01")
        elif month == "fevrier":
            return("02")
        elif month == "mars":
            return("03")
        elif month ==  "avril":
            return("04")
        elif month ==  "mai":
            return("05")
        elif month ==  "juin":
            return("06")
        elif month ==  "juillet":
            return("07")
        elif month ==  "aout":
            return("08")
        elif month ==  "septembre":
            return("09")
        elif month ==  "octobre":
            return("10")
        elif month ==  "novembre":
            return("11")
        elif month ==  "decembre":
            return("12")

    #Comme le jour et le mois sont récupérés séparément, on doit ensuite reconstruire la date pour obtenir le format demandé par
    #le module BDD. A partir d'une liste de mois et de jours, cette fonction retourne une liste de dates au bon format.
    def build_dates(self, daysList, monthsList):
        datesList = []
        for i in range(min(len(daysList), len(monthsList))):
            day = daysList[i][-2:]
            if day[1] == " ":
                day = day[0]
            month = self.build_month(monthsList[i][1:-1])
            date = "2021-"+month+"-"+day
            datesList.append(date)
        return datesList
        

    #De la même façon, cette fonction renvoie une liste d'heures on bon format.
    def build_hours(self, hoursList):
        new_hoursList=[]
        for hour in hoursList:
            new_hoursList.append(hour[:-1]+":00")
        return new_hoursList

    #A partir d'une liste de noms de projets, de liens de projets, de dates, d'heures, d'adresses et de descriptions
    #renvoie une liste de projets au format suivant : 
    #{"length" : length, "items" : [{"project" : {"title" : title, "summary" : descr, "website" : sublink, "picture" : picture, "child_friendly" : "..."}, "meeting" : {"date" : "2001-19-09", "time" : "15:00:00", "place" : place, "format" : "D"}}, {"project" : ....}]}
    def build_projects(self, titlesList, placesList, descrList, datesList, hoursList, sublinksList):
        projects = {}
        length = min(len(titlesList), len(placesList), len(descrList), len(datesList), len(hoursList), len(sublinksList))
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
            dictProject["meeting"]["end_date"]="null"
            dictProject["meeting"]["time"] = hoursList[i]
            dictProject["meeting"]["place"] = placesList[i]
            dictProject["meeting"]["format"] = "D"
            projects["items"].append(dictProject)

        projects = purge_html(projects)
        
        return projects
    
    #Pour obtenir l'adresse et la description de chaque projet, il est nécessaire de cliquer sur le lien du projet. Cette méthode charge donc la page correspondant
    #au projet et récupère l'adresse et la description.
    def get_subinformations(self, sublink):
        self.driver.get(sublink)
        adress = self.driver.find_element_by_css_selector(".address-text a").get_attribute("innerHTML")
        descriptionBlocks = self.driver.find_elements_by_css_selector("#textPart0 p")
        description = ""
        #On saute le premier bloc pour éviter d'avoir le lien du site en double.
        for i in range(1, len(descriptionBlocks)-2):
            description+=(descriptionBlocks[i].get_attribute('innerHTML'))
        return [description, adress]

    #Récupère sur la page principale la liste des liens vers les différents projets, leur nom, leur date, leur description et leur adresse.
    def scrap_basic_infos(self):
        self.driver.get("https://paris.demosphere.net/#")

        #Récupère la liste des titres de chaque projet.
        titlesElems = self.driver.find_elements_by_css_selector(".c2 a")
        titlesString = [title.get_attribute('innerHTML') for title in titlesElems]

        #Récupère la liste des liens de chaque projet.
        sublinks = [title.get_attribute('href') for title in titlesElems]

        #Récupère le mois et le jour de chaque projet.
        monthsElems = self.driver.find_elements_by_css_selector(".month")
        monthsString = [month.get_attribute('innerHTML') for month in monthsElems]
        daysElems = self.driver.find_elements_by_css_selector(".wday")
        daysString = [day.get_attribute('innerHTML') for day in daysElems]

        #Construit une lsite de dates au bon format.
        datesList = self.build_dates(daysString, monthsString)

        #Récupère l'heure de chaque projet.
        hoursElems = self.driver.find_elements_by_css_selector(".c1")
        hoursString = [hour.get_attribute('innerHTML') for hour in hoursElems]

        #Construit une liste d'heures au bon format.
        hoursList = self.build_hours(hoursString)

        #Récupère la description et l'adresse de chaque projet.
        placesList=[]
        descriptionsList=[]
        for sublink in sublinks:
            descr = self.get_subinformations(sublink)[0]
            place = self.get_subinformations(sublink)[1]
            descriptionsList.append(descr)
            placesList.append(place)

        projectLists = self.build_projects(titlesString, placesList, descriptionsList, datesList, hoursList, sublinks)

        return projectLists

    #Ajoute les projets à la base de données.
    def add_projects(self):
        response = add_projects(self.projectLists)
        print(response)

if __name__ == '__main__':
    scraper = demospherScraper()
    projects = scraper.scrap_basic_infos()
    print(projects)
    add_projects(projects)