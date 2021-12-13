import Libs.Basic_tools as bt

def purge_results(results, keywords):
    for word in results:
        for keyword in keywords:
            if keyword in word:
                break
        results.remove(word)

#Retire d'une liste de projets au format JSON les éléments issus du code html.
#La liste de projets doit être structurée comme suit : {"length" : length, "items" : [{"project" : {"title" : title, "summary" : descr, "website" : sublink, "picture" : picture, "child_friendly" : "..."}, "meeting" : {"date" : "2001-19-09", "time" : "15:00:00", "place" : place, "format" : "D"}}, {"project" : ....}]}
def purge_html(results):
    new_results=results
    for i in range(len(new_results["items"])):
        for key1 in list(new_results["items"][i]):
            for key2 in list(new_results["items"][i][key1]):
                new_results["items"][i][key1][key2] = bt.remove_block(new_results["items"][i][key1][key2], '<', '>')
                new_results["items"][i][key1][key2] = new_results["items"][i][key1][key2].replace("&nbsp", "")
                new_results["items"][i][key1][key2] = new_results["items"][i][key1][key2].replace(";", " ")
                new_results["items"][i][key1][key2] = new_results["items"][i][key1][key2].replace("\\", " ")
    return new_results

def filtrage(results):
        #results = [resultat1, ..., resultatN]
        #resultat n°i = chaîne de caractères issue d'une page web
    
        filtreNiv3 = ["conférence", "aménagement", "festival", "salon", "exposition", "action sociale"
                      "compétition", "atelier","projet", "projets", "projet participatif", "projets participatif",
                      "budget participatif", "participatif", "financement participatif", "rénovation", "rénovations",
                      "rénover", "restaurer", "restauration", "restaurations", "végétaliser", "végétalisation",
                      "sécuriser",  "piétoniser", "piétonisation", "éclairage", "art", "passage piéton", "réparer",
                      "embellir", "protéger", "signer la pétition", "faire un don", "pétition","créez"]

        filtreNiv2 = ["participer", "partager", "acheter", "favoriser", "favorisation", "améliorer", "amélioration",
                      "changer", "enjeu publique", "solidarité", "protection", "célébration", "divertissement", "rencontre",
                      "sociale", "excursion", "formation", "expérience", "monument","climat"] 

        filtreNiv1 = ["sauver", "école", "monument", "route", "rue", "avenue", "boulevard", "passage", "place",
                      "terrain", "fontaine", "inclusion", "jeu", "jeux","sauver", "école", "route", "rue", "avenue",
                      "boulevard", "passage", "place", "terrain", "inclusion", "jeu", "jeux","énergie",
                      "agriculture","productivité", "commerce", "technologie", "écologie", "sécurité", "international",
                      "santé", "éducation", "urbanisme", "musique", "arts plastiques", "arts décoratifs", "audiovisuel",
                      "spectacle vivant", "littérature", "cuisine", "mode", "math", "maths", "mathématiques", "informatique",
                      "physique", "nature", "sciences sociales", "histoire", "spiritualité", "jeux", "sport","amuser","s'amuser"]


        N1 = len(filtreNiv3) 
        N2 = len(filtreNiv2)
        N3 = len(filtreNiv1)
        filtre = [filtreNiv3, filtreNiv2, filtreNiv1] #chaînes dont une partie doit être contenue par results
        
        N = len(results)    #longueur tableau des résultats analysés
        M = N1 + N2 + N3    #longueur du tableau de filtrage
        resultsFiltre3 = [] #stockage mots filtrés avec forte assurance
        resultsFiltre2 = [] #stockage mots filtrés avec moyenne assurance
        resultsFiltre1 = [] #stockage mots filtrés avec faible assurance
        
        i = 0       #indice parcourant les mots reçus
        while i < N :
            notFound = 1
            j = 0   #indice parcourant le filtre
            while j < M :
                if (j < N1)and(results[i] == filtreNiv3[j]) :
                    resultsFiltre3.append(results[i])
                    j = j + 1
                    notFound = 0
                    continue
                if (j >= N1)and(j < N2)and(results[i] == filtreNiv2[j - N1]) :
                    resultsFiltre2.append(results[i])
                    j = j + 1
                    notFound = 0
                    continue
                if(j >= N2)and(results[i] == filtreNiv1[j - N1 - N2]) :
                    resultsFiltre1.append(results[i])
                    j = j + 1
                    notFound = 0
                    continue
                j = j + 1
            if(notFound == 1)and(len(results[i].split()) > 1):
                newResult = []
                newResult = results[i].split()
                newLength = len(newResult)
                k = 0
                h = 0
                valueResult = 0
                while h < newLength :
                    k = 0
                    while(k < M) :
                        if (k < N1)and(newResult[h].lower() == filtreNiv3[k]) :
                            k = k + 1
                            valueResult = 3
                            continue
                        if (k >= N1)and(k < (N1 + N2))and(newResult[h].lower() == filtreNiv2[k - N1]) :
                            resultsFiltre2.append(newResult[h])
                            k = k + 1
                            valueResult = 2
                            continue
                        if (k >= (N1 + N2))and(newResult[h].lower() == filtreNiv1[k - N1 - N2]) :
                            resultsFiltre1.append(newResult[h])
                            k = k + 1
                            valueResult = 1
                            continue
                        k = k  + 1
                    h = h + 1
                if(valueResult == 3):
                        resultsFiltre3.append(results[i])
                if(valueResult == 2):
                        resultsFiltre2.append(results[i])
                if(valueResult == 1):
                        resultsFiltre1.append(results[i])
            i = i + 1
        rF3 = len(resultsFiltre3)
        rF2 = len(resultsFiltre2)
        rF3 = len(resultsFiltre1)
        resultsFiltre = [resultsFiltre3, resultsFiltre2, resultsFiltre1]
        return resultsFiltre