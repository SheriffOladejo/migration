import mysql.connector
import requests
import json

class User:
    def __init__(self, user_id, username, city, state, country):
        self.username = username
        self.user_id = user_id
        self.city = city
        self.state = state
        self.country = country

def connect1_0():
    database = mysql.connector.connect(
        host = '31.172.80.88',
        port = '3306',
        user = 'sql_melanatedpeo',
        passwd = 'PRGGzZCzWRb4MNKC',
        database = 'sql_melanatedpeo')
    print(database.is_connected())


def connect2_0():
    database = mysql.connector.connect(
        host = '79.133.41.206',
        user = 'admin_5HXPW',
        database = 'admin_5HXPW',
        passwd = 'HPcrb3VbrylD')
    print(database.is_connected())

def getLocationData():

    user_list = []
    
    database = mysql.connector.connect(
        host = '31.172.80.88',
        port = '3306',
        user = 'sql_melanatedpeo',
        passwd = 'PRGGzZCzWRb4MNKC',
        database = 'sql_melanatedpeo')

    cursor = database.cursor()
    cursor.execute("select * from engine4_users where location != ''")
    result = cursor.fetchall()
    for row in result:
        user_id = row[0]
        username = row[1]
        
        location = row[33]
        location_list = location.split(',')

        city = ''
        state = ''
        country = ''

        if(len(location_list) == 1):
            city = location_list[0]
        elif(len(location_list) == 2):
            city = location_list[0]
            state = location_list[1]
        elif(len(location_list) == 3):
            city = location_list[0]
            state = location_list[1]
            country = location_list[2]
        
        user = User(
            user_id,
            username,
            city,
            state,
            country)
        user_list.append(user)
        #print(" " + str(user.username) + ": " + str(user.city) + "," + str(user.state) + "," + str(user.country))


    database2 = mysql.connector.connect(
        host = '79.133.41.206',
        user = 'admin_5HXPW',
        database = 'admin_5HXPW',
        passwd = 'HPcrb3VbrylD')
    
    for user in user_list:
        cursor = database2.cursor()
        query = """update Wo_Users set city=%s, state=%s where email=%s"""
        values = [str(user.city), str(user.state), str(user.username)]
        cursor.execute(query, values)
        database2.commit()
        print("Updated: " + str(user.username))
    

getLocationData()





















        
