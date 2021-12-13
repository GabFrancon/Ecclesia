import Serv_lib as serv
import pprint

#Web_search
#pprint.pprint(serv.add_projects(json.dumps({"length":1,"items":[{"project":{"title":"ExempleTitre","sumary":"exemple summary"},"meeting":{"exists":False}}]})))

#Classification
"""
print("unclassified : ")
pprint.pprint(serv.get_unclassified_project()[0])
print('\n')
print("get all likes :")
pprint.pprint(serv.get_all_likes())
"""
#Web_search
#pprint.pprint(serv.add_projects(INSERTJSONEXEMPLE))

pprint.pprint(serv.jwt_manag())
