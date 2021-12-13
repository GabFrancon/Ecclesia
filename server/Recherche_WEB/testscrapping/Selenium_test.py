from time import sleep
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import csv

'''
driver = webdriver.Firefox()
driver.get("https://duckduckgo.com")

sleep(5)

search_field = driver.find_element_by_id('search_form_input_homepage')
search_field.clear()

search_field.send_keys('projets collaboratifs ile de france')
search_field.submit()
sleep(5)
driver.find_element_by_link_text("Plus De Résultats").click()
sleep(5)
# driver.find_element_by_link_text("Plus De Résultats").click()
# sleep(5)
# driver.find_element_by_link_text("Plus De Résultats").click()
# sleep(5)
# driver.find_element_by_link_text("Plus De Résultats").click()
# sleep(5)

elems = driver.find_elements_by_class_name("result__a")
print(elems)
links = [elem.get_attribute('href') for elem in elems]
names = [elem.get_attribute('innerHTML') for elem in elems]
for i in links:
    print("Lien : ", i)
for i in names:
    print("Nom : ", i)


def concatenate_lists(list1, list2):
    concatenated_list = []
    if len(list1) != len(list2):
        return ("Error : lists have different length")
    else:
        i = 0
        while i < len(list1):
            concatenated_list.append([])
            concatenated_list[i].append(list1[i])
            concatenated_list[i].append(list2[i])
            i += 1
        return concatenated_list


scrapers_list = concatenate_lists(links, names)


class scraper:
    firefoxOptions = webdriver.FirefoxOptions()
    firefoxOptions.set_headless()
    driver = webdriver.Firefox(firefox_options=firefoxOptions)

    def __init__(self, url):
        self.url = url

    def _get_url(self):
        return self.url

    def add_new_result(self, newResult, key):
        if self._get_Father() == "master":
            self.results[key].append(newResult)
            print("RESULTS : ", self.results)
            print("NEW RESULT ", newResult, "ADDED TO ", key)
        else:
            self._get_Father().add_new_result(newResult, key)

    def scrap(self, elementsToScrap):
        self.driver.get(self.url)
        results = resultTable()
        for type in elementsToScrap:
            elems = self.driver.find_elements_by_css_selector(type)
            elemsInnerText = [elem.get_attribute('innerHTML') for elem in elems]
            for text in elemsInnerText:
                results.add_result(text, type)
        return(results)


    def _get_sublinks(self):
        self.driver.get(self.url)
        try:
            hypertextElements = self.driver.find_elements_by_css_selector("a")
        except:
            pass
        try:
            sublinks = [elem.get_attribute('href') for elem in hypertextElements]
            return sublinks
        except:
            pass
        return("NO SUBLINKS")
    


class duckDuckGoScraper(scraper):
    father = "master"
    url = "https://duckduckgo.com"

    def __init__(self, elements, resultsLevel, scrapLevel):
        self.moreResultsLevel = resultsLevel
        self.subScrapersLevel = scrapLevel
        self.elements_to_scrap = elements
        for i in elements:
            self.results[i] = []

    def scrap(self):
        self.driver.get("https://duckduckgo.com")
        self.driver.implicitly_wait(10)

        search_field = self.driver.find_element_by_id('search_form_input_homepage')
        search_field.clear()
        search_field.send_keys('projets collaboratifs ile de france')
        search_field.submit()

        for i in range(self.moreResultsLevel):
            self.driver.find_element_by_link_text("Plus De Résultats").click()

        elems = self.driver.find_elements_by_class_name("result__a")
        self.sublinks=[elem.get_attribute('href') for elem in elems]
        self.generate_subscrapers()

class resultTable():
    results={}

    def _get_results(self):
        return results

    def add_result(self, newResult, resultType):

        if not (resultType in results):
            results[resultType]=[]

        results[resultType].append(newResult)

    def purge(self, keyWordsList):
        for key in results:
            for keyWord in keyWordsList:
                for result in results[key]:
                    if keyWord not in result:
                        results[key].remove(result)
                        break
'''


def purge_results(results, keywords):
    for word in results:
        for keyword in keywords:
            if keyword in word:
                break
        results.remove(word)

def create_csv_file(filename, dict):
    with open(filename, 'w', newline='') as csv_file:
        writer = csv.writer(csv_file)
        for type in dict:
            list = [type] + dict[type]
            writer.writerow(list)




class globalScraper:
    firefoxOptions = webdriver.FirefoxOptions()
    firefoxOptions.set_headless()
    driver = webdriver.Firefox(firefox_options=firefoxOptions)
    driver = webdriver.Firefox()

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

    def get_elems_inPage(self, elementsToScrap, keywords):
        ancestor = self._get_ancestor()
        for elem in elementsToScrap:
            elements = self.driver.find_elements_by_css_selector(elem)
            elementsText = [element.get_attribute('innerHTML') for element in elements]
            print("BONJOUR")
            for result in elementsText:
                print("RESULT ", result, " ADDED")
                ancestor.add_results(result, elem)

    def scrap(self, elementsToScrap, keywords):
        self.driver.get(self.url)
        self.get_elems_inPage(elementsToScrap, keywords)
        try:
            elems = self.driver.find_element_by_css_selector("a")
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
        except:
            print("NO SUBLINKS")


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
        search_field.send_keys('projets collaboratifs ile de france')
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
    create_csv_file(filename, results)


