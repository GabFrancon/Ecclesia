# On importe la fonction 'get' (téléchargement) de 'requests' 
# Et la classe 'Selector' (Parsing) de 'scrapy'
from requests import get
from scrapy import Selector
# Lien de la page à scraper
url = "https://www.epaps.fr/projets/tous-les-projets/"
response = get(url, verify=False)
source = None # Le code source de la page 
if response.status_code == 200 :
    # Si la requete s'est bien passee
    source = response.text

if source :
    # Si le code source existe
    selector = Selector(text=source)
    titles = selector.css("div.archive_list row ul > li")
    for title in titles:
        # level = title.css("span.tocnumber::text").extract_first()
        name = title.css("h2.article_list_title::text").extract_first()
        print(name)