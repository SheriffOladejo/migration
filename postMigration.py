import mysql.connector
import time
import datetime

class Post:
    def __init__(
            self,
            userId,
            recipientID,
            postText,
            postFile,
            postFileName,
            postType,
            originalPostType,
            time,
            registered,
    ):
        self.userID = userId
        self.recipientID = recipientID
        self.postText = postText
        self.postFile = postFile
        self.postFileName = postFileName
        self.postType = postType
        self.originalPostType = originalPostType
        self.time = time
        self.registered = registered


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

def getPostData(id):
    database = connect1_0()
    cursor = database.cursor()
    cursor.execute("select * from engine4_activity_actions where action_id > " + str(
        id) + " and (type = 'post' or type = 'cover_photo_update' or "
                   "type = 'profile_photo_update' or type = 'post_self_photo' or type = 'post_self') limit 5000")
    result = cursor.fetchall()

    last_id = 0

    for row in result:
        action_id = row[0]
        last_id = action_id
        postType = row[1]
        originalPostType = postType
        userID = row[3]
        recipientID = 0
        postText = row[6]
        creationDate = time.mktime(datetime.datetime.strptime(str(row[8]), "%Y-%m-%d %H:%M:%S").timetuple())
        postFile = ""
        postFileName = ""
        registered = "0/0000"

        if postType == 'post':
            postType = 'post'
        elif postType == 'cover_photo_update':
            postType = 'profile_cover_picture'
        elif postType == 'profile_photo_update':
            postType = 'profile_picture'
        elif postType == 'post_self_photo':
            postType = 'post'
        elif postType == 'post_self':
            postType = 'post'

        cursor.execute("select * from engine4_activity_attachments where action_id = " + str(action_id))
        result1 = cursor.fetchall()

        for row1 in result1:
            id = row1[3]

            cursor.execute("select * from engine4_storage_files where parent_id = " + str(id))
            result2 = cursor.fetchall()
            for row2 in result2:
                type = row2[2]
                if str(type) == 'thumb.normal' or type is None:
                    postFile = row2[9]
                    postFileName = row2[11]

        text = str(postText)
        if text[0:2] == """b'""" or text[0:2] == '''b"''':
            text = text[2:-1]

        post = Post(
            userId=userID,
            postType=str(postType),
            originalPostType=str(originalPostType),
            postFile=str(postFile),
            postFileName=str(postFileName),
            recipientID=recipientID,
            postText=str(text),
            time=creationDate,
            registered=str(registered)
        )

        database2 = connect2_0()

        _get_registered_query = """select registered from Wo_Users where user_id = %s"""
        _values = [str(post.userID)]
        _cursor = database2.cursor()
        _cursor.execute(_get_registered_query, _values)
        _result = _cursor.fetchall()
        for _row in _result:
            post.registered = str(_row[0])

        query = """insert into Wo_Posts (user_id, postType, postFile, postFileName, 
        recipient_id, postText, time, registered, postLinkTitle, postLinkContent, postPrivacy, originalPostType) values (%s, %s, %s, %s,
         %s, %s, %s, %s, %s, %s, %s, %s)"""
        values = [
            post.userID,
            post.postType,
            post.postFile,
            post.postFileName,
            post.recipientID,
            post.postText,
            post.time,
            post.registered,
            '',
            '',
            '0',
            post.originalPostType,
        ]
        cursor3 = database2.cursor()

        if post.postType == 'profile_picture':
            pfp_query = """update Wo_Users set avatar = %s where user_id = %s"""
            pfp_values = [post.postFile, str(post.userID)]
            cursor3.execute(pfp_query, pfp_values)
            database2.commit()
        elif post.postType == 'profile_cover_picture':
            pfp_query = """update Wo_Users set cover = %s where user_id = %s"""
            pfp_values = [post.postFile, str(post.userID)]
            cursor3.execute(pfp_query, pfp_values)
            database2.commit()
        else:
            cursor3.execute(query, values)
            database2.commit()
            update_query = """update Wo_Posts set post_id = id where post_id = '0'"""
            cursor3.execute(update_query)
            database2.commit()

        print("Last actionID: "+str(last_id))
    getPostData(last_id)

getPostData(304952)
