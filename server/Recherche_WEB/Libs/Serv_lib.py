import requests 
import json

serv_url = "https://pact2321.r2.enst.fr/"
def makeRequest(phpScript,data=None):
    if data== None :
        req = requests.post(serv_url + phpScript)
    else :
        req = requests.post(serv_url + phpScript,json=data)
    
    if req.status_code == 200 :
        content = str (req.content.decode(encoding='UTF-8'))
        return (True,json.loads(content[content.find('{'):content.rfind('}')+1]) ) 
    
    return (False, None)

# ------ Web_search ------ #
# param ex : {"length":12,"items":[{"project":{...},"meeting":{...}}, ... ,{"project":{...},"meeting":{...}}]}
def add_projects(projects):
    return makeRequest("add_projects.php",projects)


#  ------ classif  ------ #
#return = {"length":12,"projects":[{"id": 12,"title":"TitreA","summary":"DescriptionA"}, ... , {"id": 32,"title":"TitreB","summary":"DescriptionB"}]}
def get_unclassified_project():
    return makeRequest("get_unclassified_projects.php")

# param ex : {"length":12,"items":[{"project":143,"area":["technologie","science"],"category":["cat1","cat2"]}, ... ,{"project":143,"area":["technologie","science"],"category";["cat1","cat2"]}]}
def set_classification(projects):
    return makeRequest("set_classification.php",projects)


# ------ Recom ------ #
#return = {"length":12,"items":[{"project":123,"user":234,"grade":1}, ... , {"project":453,"user":234,"grade":-1}]}
def get_all_likes():
    return makeRequest("get_all_likes.php")


# param ex : {"length":12,"items":[{"project":143,"user":123,"grade":3}, ... ,{"project":193,"user":193,"grade":8}]}
def set_recommandation(projects):
    return makeRequest("set_recommandation",projects)


