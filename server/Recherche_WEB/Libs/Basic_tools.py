import csv


#Trouve les occurences d'un bloc "element" dans une chaîne de caractère.
def find_occurences(string, element):
    length = len(string)
    compteur = 0
    position_list = []
    while compteur < length - len(element)+1:
        compteur_block = 0
        begin_position = compteur
        while compteur_block < len(element):
            if string[compteur + compteur_block] == element[compteur_block]:
                compteur_block += 1
            else:
                break
        if compteur_block == len(element):
            position_list.append(begin_position)
            compteur += compteur_block
        else:
            compteur += 1
    return (position_list)

#Enleve tous les blocs commençant par "begin_element" et terminant par "end_element" dans la chaîne de caractère "string".
def remove_block(string, begin_element, end_element):
    new_string = string
    begin_positions = find_occurences(string, begin_element)
    end_positions = find_occurences(string, end_element)
    diff = len(begin_positions) - len(end_positions)
    if diff < 0:
        end_positions = end_positions[:len(begin_positions)]
    if diff > 0:
        begin_positions = begin_positions[:len(end_positions)]
    for i in range(len(begin_positions)):
        begin = max(begin_positions[i], 0)
        end = min(end_positions[i] + len(end_element), len(string))
        new_string = new_string[:begin] + '#' * (end - begin) + new_string[end:]
    new_string = new_string.replace('#', '')
    return new_string

def create_csv_file(filename, dict):
    with open(filename, 'w', newline='') as csv_file:
        writer = csv.writer(csv_file)
        for type in dict:
            list = [type] + dict[type]
            writer.writerow(list)

#Renvoie une liste correspondant à la concaténation de list1 et de list2.
def concatenate(list1, list2):
    final_list = list1
    for i in list2:
        final_list.append(i)
    return final_list