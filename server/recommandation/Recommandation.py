#!/usr/bin/env python
# coding: utf-8


import pandas as pd
from surprise import Dataset
from surprise import Reader
from surprise import KNNWithMeans
from surprise.model_selection import GridSearchCV
import requests
import sys
sys.path.append('../Serv_lib')
from Serv_lib import get_all_likes
from Serv_lib import set_recommandation


likes=get_all_likes()[1]['likes']

users=[]
projects=[]
ratings=[]
for x in likes:
    users.append(x['user_id'])
    projects.append(x['project_id'])
    ratings.append(int(x['opinion']))
    


ratings_dict={"item":projects,"user": users,"rating":ratings}

#ratings_dict={"item":['0','1','2','1','2','3','0','4'],"user":[1,1,1,2,2,2,3,3],"rating":[0,0.5,1,0,1,0.1,1,0]}
df=pd.DataFrame(ratings_dict)
reader=Reader(rating_scale=(0,1))
data = Dataset.load_from_df(df[["user","item","rating"]], reader)

                            



sim_options={"name": ["msd","cosine"],"min_support":[2],"user_based": [True,False],}
param_grid = {"sim_options" : sim_options}
gs=GridSearchCV(KNNWithMeans, param_grid, measures=["rmse"],)
gs.fit(data)
algo=KNNWithMeans(sim_options=gs.best_params["rmse"])
print(gs.best_params["rmse"])



trainingSet=data.build_full_trainset()
algo.fit(trainingSet)


done=[]
predicted=[]
for x in users:
    for y in projects:
        if not((x,y) in done):
            if not((algo.predict(str(x),str(y))[4]['was_impossible'])):
                done.append((x,y))
                predicted.append({'user':x,'project':y,'grade':algo.predict(str(x),str(y))[3]})
                
           


result={'items':predicted,'length': len(predicted)}


#a=set_recommandation(result)







