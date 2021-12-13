"""
================================================
Semi-supervised Classification on a Text Dataset
================================================

In this example, semi-supervised classifiers are trained on the 20 newsgroups
dataset (which will be automatically downloaded).

You can adjust the number of categories by giving their names to the dataset
loader or setting them to `None` to get all 20 of them.
"""
import os
os.chdir("C:\\Users\\Antoine\\Documents\\Télécom Paris\\PACT\\pact23\\server\\recommandation")

import numpy as np


from sklearn.datasets import fetch_20newsgroups
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.preprocessing import FunctionTransformer
from sklearn.linear_model import SGDClassifier
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.semi_supervised import SelfTrainingClassifier
from sklearn.semi_supervised import LabelSpreading
from sklearn.metrics import f1_score
from sklearn.neural_network import MLPClassifier
import joblib
import os


# Récupération des données

colonne_titre = 2
colonne_classification = 0
colonne_texte = 3
path=os.getcwd()
filenames = [path+"\\scraping benenova trie.xlsx", path+"\\scraping demosphere trie.xls", path+"\\scraping paris friendly trie.xls"]

import xlrd
import os as os


data_text, data_y = [], []
for filename in filenames:
    print(filename)
    myBook = xlrd.open_workbook(filename)

    mySheet = myBook.sheet_by_index(0)

    col_texte = mySheet.col(colonne_texte)
    col_class = mySheet.col(colonne_classification)

    for k in range(len(col_texte)):
        data_text.append(col_texte[k].value)
        data_y.append(int(col_class[k].value))

invented_txt=["Le projet de Loi Climat déposé par le gouvernement est loin d'être à la hauteur de l'urgence écologique. Pourtant, c'est le dernier texte législatif du quinquennat consacré à l'environnement. Le 28 mars, à la veille de l'entrée du texte de loi à l'Assemblée Nationale, nous nous mobilisons pour exiger une loi ambitieuse et défendre les mesures proposées par les 150 membres de la Convention citoyenne pour le Climat.",
 "Nous savons qu'aujourd'hui les conditions dans lesquelles nous travaillons prennent de plus en plus d'importance, et que l'environnement est un facteur important pour la productivité d'un salarié. En effet, plus un salarié se sent bien dans son entreprise, plus il va proposer de solutions et va s'impliquer davantage. C'est pourquoi il est important pour une entreprise d'avoir des openspaces, des locaux, et des salles de détente bien aménagés. Le choix de la décoration et du mobilier rentre donc en jeu pour mettre à l'aise les équipes.",
 "Ballet le plus joué au monde, Le Lac des Cygnes est de retour en 2021 pour une grande tournée en France et en Europe. A l'image des années précédentes, une nouvelle compagnie russe présentera cet intemporel chef-d'œuvre classique. Ce ballet nous plonge dans la folle histoire d'amour du Prince Siegfried et de la Princesse Odette. Cette dernière est malheureusement prisonnière du célèbre sort du magicien Rothbart : elle se transforme en cygne le jour et redevient femme la nuit. Seule la promesse d'un amour éternel pourra la libérer de cet ensorcellement. Créé en 1875 par le compositeur russe Piotr Tchaïkovsky, c'est en 1895 avec la reprise du chorégraphe Marius Petipa que Le Lac des Cygnes deviendra le plus grand succès classique de tous les temps.",
 "La Paris Games Week revient pour une nouvelle édition au Parc des expositions de la Porte de Versailles. Entièrement dédié à l'univers du jeu vidéo, ce salon qui reçoit chaque année davantage de visiteurs, réunit les joueurs, les passionnés mais aussi les professionnels du milieu. Rendez-vous incontournable des gamers, la PGW est l'occasion de découvrir des nouveautés, d'assister à des conférences et des sessions de gameplay, de participer à des tournois et d'avoir accès à des informations exclusives !",
 "Apprenez pas à pas les secrets de la maroquinerie. Diane vous proposera trois modèles de sacs : le sac seau et pochette enveloppe et un modèle qui change toute les saisons. Ensuite, le cuir ! Vous choisirez, parmi les pièces de cuirs prédécoupées et travaillées au préalable, les textures et couleurs qui vous ressemblent. C'est une expérience très sensitive ! Enfin l'assemblage, toutes les coutures sont faites à la main : la couture sellier, celle-là même employée chez Hermès. Personnalisez votre sac avec des rivets ou pompons et signez-le à l'intérieur pour repartir avec un sac unique et de superbes souvenirs.",
 "A la Taverne Médiévale vous avez la possibilité de voyager dans le temps pendant toute une soirée et de revivre les fêtes atypiques du Moyen-Age. La Taverne Médiévale est depuis 15 ans un lieu emblématique où vous y trouverez musiciens, artisans, jongleurs et cracheurs de feu. Tous les 2ème et 4ème jeudis du mois, la Taverne Médiévale reconstitue des moments de l'histoire dans un univers fantastique. Les soirées à la Taverne sont rythmées de danses, spectacles et jeux tout en vous permettant de vous restaurer avec des plats médiévaux agrémentés de vins traditionnels. La Taverne Médiévale fait preuve d'une communication originale, sortant de l'ordinaire, en proposant des soirées à thèmes sous forme d'histoire du Moyen-Age et animées de jeux de piste et d'épreuves."]

invented_y = [6, 3, 16, 25, 19, 24]

data_text += invented_txt
data_y += invented_y

# Main
count = 0
categories = []
for category in data_y:
    if category not in categories:
        categories.append(category)
        count += 1


#data = fetch_20newsgroups(subset='train', categories=None)
print("%d documents" % len(data_text))
print("%d categories" % count)
print()

# Parameters
sdg_params = dict(alpha=1e-5, penalty='l2', loss='log')
vectorizer_params = dict(ngram_range=(1, 2), min_df=5, max_df=0.8)

# Supervised Pipeline
pipeline = Pipeline([
    ('vect', CountVectorizer(**vectorizer_params)),
    ('tfidf', TfidfTransformer()),
    ('clf', MLPClassifier(alpha = 0.1, max_iter=1000)),
])
# SelfTraining Pipeline
st_pipeline = Pipeline([
    ('vect', CountVectorizer(**vectorizer_params)),
    ('tfidf', TfidfTransformer()),
    ('clf', MLPClassifier(alpha = 0.1, max_iter=1000)),
])
# LabelSpreading Pipeline
ls_pipeline = Pipeline([
    ('vect', CountVectorizer(**vectorizer_params)),
    ('tfidf', TfidfTransformer()),
    # LabelSpreading does not support dense matrices
    ('todense', FunctionTransformer(lambda x: x.todense())),
    ('clf', LabelSpreading()),
])


def eval_and_print_metrics(clf, X_train, y_train, X_test, y_test):
    print("Number of training samples:", len(X_train))
    print("Unlabeled samples in training set:",
          sum(1 for x in y_train if x == -1))
    clf.fit(X_train, y_train)
    y_pred = clf.predict(X_test)
    print("Micro-averaged F1 score on test set: "
          "%0.3f" % f1_score(y_test, y_pred, average='micro'))
    print("-" * 10)
    print()


if __name__ == "__main__":
    X, y = data_text,np.array(data_y)
    X_train, X_test, y_train, y_test = train_test_split(X, y)

    print("Machine Learning on 100% of the data:")
    eval_and_print_metrics(pipeline, X_train, y_train, X_test, y_test)

    # select a mask of 20% of the train dataset
    y_mask = np.random.rand(len(y_train)) < 0.7

    # X_20 and y_20 are the subset of the train dataset indicated by the mask
    X_20, y_20 = map(list, zip(*((x, y)
                     for x, y, m in zip(X_train, y_train, y_mask) if m)))
    print("Supervised SGDClassifier on 80% of the training data:")
    eval_and_print_metrics(pipeline, X_20, y_20, X_test, y_test)

    # set the non-masked subset to be unlabeled
    y_train[~y_mask] = -1
    print("SelfTrainingClassifier on 80% of the training data (rest "
          "is unlabeled):")
    eval_and_print_metrics(st_pipeline, X_train, y_train, X_test, y_test)

    if 'CI' not in os.environ:
        # LabelSpreading takes too long to run in the online documentation
        print("LabelSpreading on 80% of the data (rest is unlabeled):")
        eval_and_print_metrics(ls_pipeline, X_train, y_train, X_test, y_test)


##sauvegarde du modèle
joblib.dump(pipeline, 'clf_area.pkl')
