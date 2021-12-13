#!/usr/bin/env python3

import os
import scrapy

def concatenate (file_write_src, file_read_src):
    file_copy = open(file_write_src, 'r')
    lines = file_copy.readlines()
    lines.pop()
    file_copy.close() 
    file_cut = open(file_write_src, 'w')
    file_cut.write("\r\n".join(lines))
    file_cut.close()
    file_write = open(file_write_src, "a")
    file_read = open(file_read_src, "r")
    for line in file_read:
        file_write.write("      "+line)
    file_write.close()
    file_read.close()
    
def create_spiders(websites_list):
    for i in websites_list :
        os.system("scrapy genspider "+i[0]+" "+i[1]+"; ")

    for i in websites_list :
        concatenate("testscrapping/spiders/"+i[0]+".py", "canonical_scraper.py")

def run_spiders():
    spiders_list = os.listdir("testscrapping/spiders/")
    for i in spiders_list:
        if not "__" in i:
            filename = i[:-3]
            os.system("scrapy crawl "+filename+" -o "+filename+".csv")

def delete_spiders():
    spiders_list = os.listdir("testscrapping/spiders/")
    for i in spiders_list:
        if not "__" in i:
            filename = i
            os.system("rm testscrapping/spiders/"+filename)
        

liste_liens = [["fundit", "fundit.fr/en/calls/region-ile-franceprojets-collaboratifs-et-recherche-et-developpement"], ["horizon_Europe", "www.iledefrance-europe.eu/opportunites/detail-actualites-opportunites/article/vous-souhaitez-coordonner-un-projet-collaboratif-horizon-europe/"], ["wikipedia", "fr.wikipedia.org/wiki/"] ,[ "les_aides", "les-aides.fr/projets/ile-de-france/"]]


delete_spiders()
create_spiders(liste_liens)
run_spiders()