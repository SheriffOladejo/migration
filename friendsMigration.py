import mysql.connector
import requests
import json


def connect1_0():
    database = mysql.connector.connect(
        host='79.133.41.206',
        port='3306',
        user='admin_melanatedpeo',
        passwd='bf73jg0wufm0gs',
        database='admin_melanatedpeo')
    return database


def connect2_0():
    database = mysql.connector.connect(
        host='79.133.41.206',
        user='admin_l5ERv',
        database='admin_l5ERv',
        passwd='irNdhZkzu8AU')
    return database


def getData():
    last_id = 0

    db2 = connect2_0()
    cu = db2.cursor()
    v = [str(1)]
    cu.execute("select user_id from Wo_Users where friends_copied = 1 order by user_id desc limit 1")
    ids = cu.fetchall()
    for id in ids:
        last_id = id[0]
    print("last id is: " + str(last_id))

    database = connect1_0()
    cursor = database.cursor()

    cursor.execute("select * from engine4_users where user_id > " + str(last_id) + " limit 5000")
    users = cursor.fetchall()

    for user in users:
        user_id = user[0]
        last_id = user_id

        followers = getFollowers(last_id)
        following = getFollowing(last_id)

        database2 = connect2_0()
        cursor2 = database2.cursor()
        cursor2.execute("select * from Wo_Users where user_id = " + str(last_id))
        wo_users = cursor2.fetchall()
        for wo_user in wo_users:
            details = json.loads(str(wo_user[101]))

            sidebar_data = {}

            if wo_user[102] is None:
                sidebar_data = {"following_data": [],
                                "followers_data": [], "likes_data": [], "groups_data": [],
                                "mutual_friends_data": []}
            else:
                sidebar_data = json.loads(str(wo_user[102]))

            details["following_count"] = str(len(following))
            details["followers_count"] = str(len(followers))

            sidebar_data["following_data"] = following
            sidebar_data["followers_data"] = followers

            values = [str(1), str(json.dumps(details)), str(json.dumps(sidebar_data)), str(user_id)]
            cursor2.execute(
                "update Wo_Users set friends_copied = %s, details = %s, sidebar_data = %s where user_id = %s", values)
            database2.commit()


def getFollowers(id):
    database = connect1_0()
    cursor = database.cursor()
    cursor.execute("select * from engine4_activity_actions where object_id = " + str(id) + " and type = 'friends'")
    result = cursor.fetchall()

    followers = []

    for row in result:
        subject_id = row[3]
        followers.append(str(subject_id))

    return followers


def getFollowing(id):
    database = connect1_0()
    cursor = database.cursor()
    cursor.execute("select * from engine4_activity_actions where subject_id = " + str(id) + " and type = 'friends'")
    result = cursor.fetchall()

    following = []

    for row in result:
        object_id = row[5]
        following.append(str(object_id))

    return following


getData()
