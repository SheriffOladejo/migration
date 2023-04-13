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


def connect2_0():
    database = mysql.connector.connect(
        host='79.133.41.206',
        user='admin_l5ERv',
        database='admin_l5ERv',
        passwd='irNdhZkzu8AU')
    return database


def migrateUsers():
    user_list = []

    database = mysql.connector.connect(
        host='79.133.41.206',
        port='3306',
        user='admin_melanatedpeo',
        passwd='bf73jg0wufm0gs',
        database='admin_melanatedpeo')

    last_id = 0

    db2 = connect2_0()
    cu = db2.cursor()
    cu.execute("select user_id from Wo_Users order by user_id desc limit 1")
    ids = cu.fetchall()
    for id in ids:
        last_id = id[0]
    print("last id is: " + str(last_id))

    cursor = database.cursor()
    cursor.execute("select * from engine4_users where user_id > " + str(last_id) + " limit 5000")
    result = cursor.fetchall()
    for row in result:
        user_id = row[0]
        last_id = user_id
        email = row[1]
        username = row[2]
        if username is None:
            username = email
        location = row[33]
        password = row[7]
        displayname = row[3]
        name_list = displayname.split(' ')
        firstname = ''
        lastname = ''

        if (len(name_list) == 1):
            firstname = name_list[0]
        elif (len(name_list) == 2):
            firstname = name_list[0]
            lastname = name_list[1]
        elif (len(name_list) == 3):
            firstname = name_list[0]
            lastname = str(name_list[1]) + ' ' + str(name_list[2])

        city = ''
        state = ''
        country = ''

        location_list = location.split(',')
        if (len(location_list) == 1):
            city = location_list[0]
        elif (len(location_list) == 2):
            city = location_list[0]
            state = location_list[1]
        elif (len(location_list) == 3):
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

        if (len(user_list) != 500):
            user_list.append(user)
        else:
            break

    database2 = mysql.connector.connect(
        host='79.133.41.206',
        user='admin_l5ERv',
        database='admin_l5ERv',
        passwd='irNdhZkzu8AU')

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
    user_list = []
    migrateUsers()


migrateUsers()
