"""
================================================
Semi-supervised Classification on a Text Dataset
================================================

In this example, semi-supervised classifiers are trained on the 20 newsgroups
dataset (which will be automatically downloaded).

You can adjust the number of categories by giving their names to the dataset
loader or setting them to `None` to get all 20 of them.
"""


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

import xlrd
import os as os
os.chdir("C:\\Users\\Antoine\\Documents\\Télécom Paris\\PACT\\pact23\\server\\recommandation")
from glob import glob
import os.path as op

### Récupération des données

colonne_titre = 2
colonne_classification = 0
colonne_texte = 3
filenames = [".\\scraping benenova trie.xlsx", ".\\scraping demosphere trie.xls", ".\\scraping paris friendly trie.xls"]



data_text, data_y = [], []
for filename in filenames:
    myBook = xlrd.open_workbook(filename)

    mySheet = myBook.sheet_by_index(0)

    col_texte = mySheet.col(colonne_texte)
    col_class = mySheet.col(colonne_classification)

    for k in range(len(col_texte)):
        data_text.append(col_texte[k].value)
        data_y.append(int(col_class[k].value))





### Infos



count = 0
categories = []
for category in data_y:
    if(category not in categories):
        categories.append(category)
        count+=1


#data = fetch_20newsgroups(subset='train', categories=None)
print("%d documents" % len(data_text))
print("%d categories" % count)
print()



### Main

# Parameters
sdg_params = dict(alpha=1e-5, penalty='l2', loss='log')
vectorizer_params = dict(ngram_range=(1, 1), min_df=5, max_df=0.8)

# Supervised Pipeline
pipeline = Pipeline([
    ('vect', CountVectorizer(**vectorizer_params)),
    ('tfidf', TfidfTransformer()),
    ('clf', SGDClassifier(**sdg_params)),
])
# SelfTraining Pipeline
st_pipeline = Pipeline([
    ('vect', CountVectorizer(**vectorizer_params)),
    ('tfidf', TfidfTransformer()),
    ('clf', SelfTrainingClassifier(SGDClassifier(**sdg_params), verbose=True)),
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

    print("Supervised SGDClassifier on 100% of the data:")
    eval_and_print_metrics(pipeline, X_train, y_train, X_test, y_test)

    # select a mask of 20% of the train dataset
    y_mask = np.random.rand(len(y_train)) < 0.2

    # X_20 and y_20 are the subset of the train dataset indicated by the mask
    X_20, y_20 = map(list, zip(*((x, y)
                     for x, y, m in zip(X_train, y_train, y_mask) if m)))
    print("Supervised SGDClassifier on 20% of the training data:")
    eval_and_print_metrics(pipeline, X_20, y_20, X_test, y_test)

    # set the non-masked subset to be unlabeled
    y_train[~y_mask] = -1
    print("SelfTrainingClassifier on 20% of the training data (rest "
          "is unlabeled):")
    eval_and_print_metrics(st_pipeline, X_train, y_train, X_test, y_test)

    if 'CI' not in os.environ:
        # LabelSpreading takes too long to run in the online documentation
        print("LabelSpreading on 20% of the data (rest is unlabeled):")
        eval_and_print_metrics(ls_pipeline, X_train, y_train, X_test, y_test)
