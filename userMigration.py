import mysql.connector
import requests
import json


class User:
    def __init__(
                self,
                user_id,
                username,
                email,
                password,
                first_name,
                last_name,
                city,
                state,
                country
             ):
        self.user_id = user_id
        self.username = username
        self.email = email
        self.password = password
        self.first_name = first_name
        self.last_name = last_name
        self.city = city
        self.state = state
        self.country = country
        

last_id = 136743

def migrateUsers():
    user_list = []

    database = mysql.connector.connect(
        host = '31.172.80.88',
        port = '3306',
        user = 'sql_melanatedpeo',
        passwd = 'PRGGzZCzWRb4MNKC',
        database = 'sql_melanatedpeo')

    cursor = database.cursor()
    cursor.execute("select * from engine4_users where user_id > " + str(last_id))
    result = cursor.fetchall()
    for row in result:
        user_id = row[0]
        username = row[2]
        location = row[33]
        password = row[7]
        
        email = row[1]
        displayname = row[3]
        name_list = displayname.split(' ')
        firstname = ''
        lastname = ''

        if(len(name_list) == 1):
            firstname = name_list[0]
        elif(len(name_list) == 2):
            firstname = name_list[0]
            lastname = name_list[1]
        elif(len(name_list) == 3):
            firstname = name_list[0]
            lastname = str(name_list[1]) + ' ' + str(name_list[2])

        city = ''
        state = ''
        country = ''

        location_list = location.split(',')
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
            email,
            password,
            firstname,
            lastname,
            city,
            state,
            country)

        if(len(user_list) != 500):
            user_list.append(user)
        else:
            break

    database2 = mysql.connector.connect(
        host = '79.133.41.206',
        user = 'admin_5HXPW',
        database = 'admin_5HXPW',
        passwd = 'HPcrb3VbrylD')

    for user in user_list:
        cursor = database2.cursor()
        query = """insert into Wo_Users (user_id, username, email, password, first_name,
                        last_name, city, state) values (%s,%s,%s,%s,%s,%s,%s,%s)"""
        values = [
                user.user_id,
                str(user.username),
                str(user.email),
                str(user.password),
                str(user.first_name),
                str(user.last_name),
                str(user.city),
                str(user.state)
            ]
        cursor.execute(query, values)
        database2.commit()
        print("Last id updated: " + str(user.user_id))

migrateUsers()
        
    
