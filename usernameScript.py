import mysql.connector
import re

def connect2_0():
    database = mysql.connector.connect(
        host='79.133.41.206',
        user='admin_l5ERv',
        database='admin_l5ERv',
        passwd='irNdhZkzu8AU')
    return database

def has_special_characters(text):
    pattern = r'[!@#$%^&*+~\;/(),.?":{}|<>]'
    match = re.search(pattern, text)
    return bool(match)

def remove_special_characters(text):
    pattern = r'[^a-zA-Z0-9\s]'
    return re.sub(pattern, '', text)

def getUsernames():
    db2 = connect2_0()
    cursor = db2.cursor()
    cursor.execute("select user_id, username, email from Wo_Users")

    result = cursor.fetchall()
    for row in result:
        user_id = row[0]
        username = str(row[1])

        if has_special_characters(username):
            _db2 = connect2_0()
            cursor2 = _db2.cursor()
            username = remove_special_characters(username)
            query = """update Wo_Users set username = %s where user_id = %s"""
            values = [username, str(user_id)]
            cursor2.execute(query, values)
            _db2.commit()

getUsernames()
