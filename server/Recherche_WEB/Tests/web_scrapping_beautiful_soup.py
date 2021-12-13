# Importer les modules
import requests
import csv
from bs4 import BeautifulSoup
# Adresse du site Internet
url = "https://www.epaps.fr/projets/tous-les-projets/"
# Exécuter la requête GET
response = requests.get(url, verify=False)
# Parser le document HTML BeautifulSoup obtenu à partir du code source
html = BeautifulSoup(response.content, 'html.parser')
# Extraire toutes les citations et tous les auteurs du document HTML
titles_html = html.find_all('h2', class_="article_list_title")
descr_html = html.find_all('div', class_="article_list_content contrib")
# Rassembler les citations dans une liste
titles = list()
for title in titles_html:
    titles.append(title.text)
# Rassembler les auteurs dans une liste
descr = list()
for descr in descr_html:
    descr.append(descr.text) 
# Pour tester : combiner et afficher les entrées des deux listes
for t in zip(titles, descr):
	print(t)
# Enregistrer les citations et les auteurs dans un fichier CSV dans le répertoire actuel
# Ouvrez le fichier avec Excel / LibreOffice, etc.
with open('./zitate.csv', 'w') as csv_file:
    csv_writer = csv.writer(csv_file, dialect='excel')
    csv_writer.writerows(zip(titles, descr))
