# Importation des modules
import sys
import joblib

sys.path.append('../Serv_lib')
from Serv_lib import get_unclassified_project
from Serv_lib import set_classification

# Prédiction
data = get_unclassified_project()[1]['projects']  # recuperation des projets non classifié sur le serveur
data_id, data_txt = [], []
for i in range(len(data)):
    data_id.append(data[i]['id'])
    data_txt.append(data[i]['summary'])

clf_area = joblib.load('clf_area.pkl')  # chargement des classifieurs
clf_category = joblib.load('clf_category.pkl')
predicted_area = clf_area.predict(data_txt)  # prédiction de la classification
predicted_category = clf_category.predict(data_txt)

# Conversion chiffre --> label
areas_label = ['Energie', 'Agriculture', 'Travail', 'Commerce', 'Technologie', 'Ecologie', 'Sécurité', 'International',
               'Santé', 'Education', 'Urbanisme', 'Musique', 'Arts plastiques', 'Arts décoratifs', 'Audiovisuel',
               'Spectacle vivant', 'Littérature', 'Cuisine', 'Mode', 'Math/Informatique', 'Physique', 'Nature',
               'Sciences sociales', 'Histoire', 'Sport']
categories_label = ['Conférence', 'Aménagement', 'Festival/Célebration', 'Divertissement', 'Rencontre',
                    'Salon/Exposition', 'Action sociale', 'Compétition', 'Excursion', 'Atelier/Formation', 'Expérience']

dico_a, dico_c, areas, categories, items = [], [], [], [], []

for k in range(len(areas_label)):
    dico_a += [(k + 1, areas_label[k])]

for k in range(len(categories_label)):
    dico_c += [(k + 1, categories_label[k])]

dico_a, dico_c = dict(dico_a), dict(dico_c)

for k in range(len(predicted_area)):
    areas.append(dico_a.get(predicted_area[k]))
    categories.append(dico_c.get(predicted_category[k]))

# Création du Json

for i in range(len(data)):
    items.append({'project': data_id[i], 'area': [areas[i]], 'category': [categories[i]]})

result = {'items': items, 'length': len(data)}

set_classification(result)
