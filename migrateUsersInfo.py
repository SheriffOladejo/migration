import mysql.connector
import requests
import json

class User:
    def __init__(
                    self,
                    user_id,
                    firstname,
                    lastname,
                    birthday,
                    profession,
                    education,
                    city,
                    zipcode,
                    profile_bio,
                    high_school,
                    personal_website,
                    facebook,
                    twitter,
                    instagram,
                    linkedin,
                    youtube,
                    gender
                    ):
        self.user_id = user_id
        self.firstname = firstname
        self.lastname = lastname
        self.birthday = birthday
        self.profession = profession
        self.education = education
        self.city = city
        self.zipcode = zipcode
        self.profile_bio = profile_bio
        self.high_school = high_school
        self.personal_website = personal_website
        self.facebook = facebook
        self.twitter = twitter
        self.instagram = instagram
        self.linkedin = linkedin
        self.youtube = youtube
        self.gender = gender

def fetchUsers(last_id):
    print("Last id updated: " + str(last_id))
    user_list = []
    database = mysql.connector.connect(
        host = '31.172.80.88',
        port = '3306',
        user = 'sql_melanatedpeo',
        passwd = 'PRGGzZCzWRb4MNKC',
        database = 'sql_melanatedpeo')

    cursor = database.cursor()
    cursor.execute("select * from engine4_users where user_id > " + str(last_id) + " limit 1000")
    result = cursor.fetchall()
    for row in result:
        user_id = row[0]
        
        user = User(user_id,"","","","","","","","","","","","","","","","",)
        user_list.append(user)

    for user in user_list:
        cursor.execute("select * from engine4_user_fields_values where item_id = " + str(user.user_id))
        result = cursor.fetchall()
        for row in result:
            field_id = row[1]
            value = row[3]
            if field_id == 3:
                user.firstname = value
            elif field_id == 4:
                user.lastname = value
            elif field_id == 6:
                user.birthday = value
            elif field_id == 24:
                user.profession = value
            elif field_id == 25:
                user.education = value
            elif field_id == 27:
                user.city = value
            elif field_id == 65:
                user.zipcode = value
            elif field_id == 66:
                user.profile_bio = value
            elif field_id == 75:
                user.high_school = value
            elif field_id == 77:
                user.personal_website = value
            elif field_id == 78:
                user.facebook = value
            elif field_id == 79:
                user.twitter = value
            elif field_id == 80:
                user.instagram = value
            elif field_id == 81:
                user.linkedin = value
            elif field_id == 82:
                user.youtube = value;
            elif field_id == 5:
                if value == '4':
                    user.gender = '3'
                elif value == '2':
                    user.gender = '1'
                elif value == '3':
                    user.gender = '2'
    migrateUserDetails(user_list)

def migrateUserDetails(user_list):
    print(str(len(user_list)))
    database = mysql.connector.connect(
        host = '79.133.41.206',
        user = 'admin_5HXPW',
        database = 'admin_5HXPW',
        passwd = 'HPcrb3VbrylD')
    for user in user_list:
        cursor = database.cursor()
    
        query = """update Wo_Users set
                    first_name=%s,
                    last_name=%s,
                    birthday=%s,
                    working=%s,
                    city=%s,
                    zip=%s,
                    about=%s,
                    school=%s,
                    website=%s,
                    facebook=%s,
                    twitter=%s,
                    instagram=%s,
                    linkedin=%s,
                    youtube=%s,
                    gender=%s where user_id = """ + str(user.user_id)
        values = [
            str(user.firstname),
            str(user.lastname),
            str(user.birthday),
            str(user.profession),
            str(user.city),
            str(user.zipcode),
            str(user.profile_bio),
            str(user.high_school),
            str(user.personal_website),
            str(user.facebook),
            str(user.twitter),
            str(user.instagram),
            str(user.linkedin),
            str(user.youtube),
            str(user.gender)
            ]
        cursor.execute(query, values)
        database.commit()
        last_id = user.user_id
    fetchUsers(last_id)

fetchUsers(48691)
