from __future__ import print_function

import mysql.connector
import json
import time
import requests

import clicksend_client
from clicksend_client import SmsMessage
from clicksend_client.rest import ApiException

def connect2_0():
    database = mysql.connector.connect(
        host='185.66.89.236',
        user='gjftpbih',
        database='gjftpbih_sms_app',
        passwd='Ih805Lep1o')
    return database


def _getData():
    list = []
    configuration = clicksend_client.Configuration()
    configuration.username = 'vanunalarisa@gmail.com'
    configuration.password = '3F00AD78-3F5B-EAF2-6537-8E8A9AA124AD'

    # create an instance of the API class
    api_instance = clicksend_client.SMSApi(clicksend_client.ApiClient(configuration))

    # If you want to explictly set from, add the key _from to the message.

    database = connect2_0()
    cursor = database.cursor()
    cursor.execute("select * from t_app where sent = 'false'")
    messages = cursor.fetchall()
    for m in messages:
        text = m[1]
        timestamp = int(m[2])
        recipient_number = m[4]
        sender = m[6]

        print(sender)

        if timestamp / 1000 <= time.time():
            sms_message = SmsMessage(source="php",
                                     body=text,
                                     to=recipient_number,
                                     _from=sender,
                                     )
            list.append(sms_message)

    sms_messages = clicksend_client.SmsMessageCollection(messages=list)

    try:
        # Send sms message(s)
        api_response = api_instance.sms_send_post(sms_messages)
        print(api_response)
    except ApiException as e:
        print("Exception when calling SMSApi->sms_send_post: %s\n" % e)

def getData():
    list = []
    base_url = "https://rest.clicksend.com/v3/sms/send"
    database = connect2_0()
    cursor = database.cursor()
    cursor.execute("select * from t_app where sent = 'false'")
    messages = cursor.fetchall()
    for m in messages:
        text = m[1]
        timestamp = int(m[2])
        recipient_number = m[4]
        sender = m[6]

        if timestamp / 1000 <= time.time():
            _json = {"source": "sdk", "body": text, "to": recipient_number, "from": sender}
            list.append(_json)
        print(str(list))

    headers = {
        "Authorization": "Basic 3F00AD78-3F5B-EAF2-6537-8E8A9AA124AD",
        "Content-Type": "application/json"
    }

    resp = requests.post(base_url, data=json.dumps(list), headers=headers)
    print(resp)

getData()