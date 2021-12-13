from selenium import webdriver
from selenium.common import exceptions
import Libs.Cleaning_tools as ct
import Libs.Basic_tools as bt

class globalScraper:
    firefoxOptions = webdriver.FirefoxOptions()
    firefoxOptions.set_headless()
    driver = webdriver.Firefox(firefox_options=firefoxOptions)

    def __init__(self, father="NO FATHER", url="https://duckduckgo.com", subScrapersLevel=1, depthLevel=0):
        self.url = url
        self.subScrapersLevel = subScrapersLevel
        self.results = {}
        self.depthLevel = depthLevel
        self.father = father;

    def _get_results(self):
        return self.results

    def _get_subScrapersLevel(self):
        return self.subScrapersLevel

    def _get_depthLevel(self):
        return self.depthLevel

    def _get_father(self):
        if self._get_depthLevel() == 0:
            return self
        else:
            return self.father

    def _get_ancestor(self):
        ancestor = self
        while ancestor._get_depthLevel() > 0:
            ancestor = ancestor._get_father()
        return ancestor

    def add_results(self, resultType, newResult):
        if not (resultType in self.results):
            self.results[resultType] = []

        self.results[resultType].append(newResult)

    def get_all_classes_in_page(self):
        all_elems = self.driver.find_elements_by_css_selector('*')
        all_classes=[]
        for elem in all_elems:
            try:
                name = elem.get_attribute("class")
                if name not in all_classes:
                    all_classes.append(name)
            except exceptions.StaleElementReferenceException as e:
                print(e)
                pass
        return all_classes

    def get_locations(self):
        all_classes = self.get_all_classes_in_page()
        location_classes = []
        location_synonyms =  ["bearings", "locale", "locus", "place", "point", "position", "site", "situation", "spot", "venue", "whereabouts"]
        for classe in all_classes:
            for synonym in location_synonyms:
                if synonym in classe:
                    location_classes.append(classe)
        locationsElems = []
        for classe in location_classes:
            elems=self.driver.get_elements_by_css_selector()
            dates = [elem.get_attribute('innerHTML') for elem in elems]
            for date in dates:
                locationsElems.append(date)
        return locationsElems

    def get_dates(self):
        all_classes = self.get_all_classes_in_page()
        dates_classes = []
        date_synonyms = ["date", "age", "epoch", "era", "period", "stage", "time"]
        for classe in all_classes:
            for synonym in date_synonyms:
                if synonym in classe:
                    classe.append(dates_classes)
        datesElems = []
        for classe in dates_classes:
            elems = self.driver.get_elements_by_css_selector()
            dates = [elem.get_attribute('innerHTML') for elem in elems]
            for date in dates:
                datesElems.append(date)
        return datesElems

    def find_projects(self):
        ancestor = self._get_ancestor()
        elemsH1 = self.driver.find_elements_by_css_selector("h1")
        titles = [elem.get_attribute('innerHTML') for elem in elemsH1]
        all_classes = self.get_all_classes_in_page()
        dates_classes = []
        date_synonyms = ["date", "age", "epoch", "era", "period", "stage", "time"]
        for classe in all_classes:
            for synonym in date_synonyms:
                    dates_classes.append(classe)

        location_classes = []
        location_synonyms = ["bearings", "locale", "locus", "place", "point", "position", "site", "situation", "spot",
                             "venue", "whereabouts"]
        for classe in all_classes:
            for synonym in location_synonyms:
                    location_classes.append(classe)

        elemTimes = self.driver.find_elements_by_xpath("//h1/following-sibling::p")
        dates = [elem.get_attribute('innerHTML') for elem in elemTimes]

        elemTimes = self.driver.find_elements_by_css_selector("time")
        times = [elem.get_attribute('datetime') for elem in elemTimes]

        finalDates = bt.concatenate(dates, times)

        for i in range(len(titles)):
            if i<len(finalDates):
                ancestor.add_results("PROJECT", [titles[i], finalDates[i]])
            else:
                break

    def get_elems_inPage(self, elementsToScrap, keywords):
        ancestor = self._get_ancestor()
        for elem in elementsToScrap:
            elements = self.driver.find_elements_by_css_selector(elem)
            elementsText = [element.get_attribute('innerHTML') for element in elements]
            print("BONJOUR")
            for result in elementsText:
                print("RESULT ", result, " ADDED")
                ancestor.add_results(elem, result)


    def scrap(self, elementsToScrap, keywords):
        self.driver.get(self.url)
        #self.get_elems_inPage(elementsToScrap, keywords)
        self.find_projects()
        elems = self.driver.find_elements_by_css_selector("a")
        sublinks = [elem.get_attribute('href') for elem in elems]
        print ("SUBLINKS : ",sublinks)
        if self._get_depthLevel() < self._get_subScrapersLevel():
            for link in sublinks:
                scraper = globalScraper(url=link, father=self, depthLevel=self._get_depthLevel() + 1)
                print("SUBSCRAPER GENERATED")
                scraper.scrap(elementsToScrap, keywords)
        if self._get_depthLevel() == 0:
            results = self._get_results()
            return results


class duckDuckGoScraper(globalScraper):

    def __init__(self, moreResultsLevel, subScrapersLevel, depthLevel=0):
        self.url = "https://duckduckgo.com"
        self.moreResultsLevel = moreResultsLevel
        self.subScrapersLevel = subScrapersLevel
        self.father = "master"
        self.results = {}
        self.depthLevel = depthLevel;

    def scrap(self, search, elementsToScrap, keywords):
        self.driver.get("https://duckduckgo.com")
        self.driver.implicitly_wait(10)

        search_field = self.driver.find_element_by_id('search_form_input_homepage')
        search_field.clear()
        search_field.send_keys(search)
        search_field.submit()

        for i in range(self.moreResultsLevel):
            self.driver.find_element_by_link_text("Plus De Résultats").click()

        elems = self.driver.find_elements_by_class_name("result__a")
        sublinks=[elem.get_attribute('href') for elem in elems]
        print(sublinks)
        compteur = 0
        for sublink in sublinks:
            if compteur >= 10:
                break
            scraper = globalScraper(father = self, url = sublink, subScrapersLevel = self.subScrapersLevel, depthLevel = self.depthLevel + 1)
            scraper.scrap(elementsToScrap, keywords)
            compteur+=1


if __name__ == '__main__':
    filename = ("Resultats.csv")
    keywords = ["projet"]
    elementsToScrap = ["h1"]
    duckDuckGo = duckDuckGoScraper(moreResultsLevel=0, subScrapersLevel=1)
    duckDuckGo.scrap("Projets collaboratifs en Ile-de-France", elementsToScrap, keywords)
    results = duckDuckGo._get_results()
    print(results)
    bt.create_csv_file(filename, results)
