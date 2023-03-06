import requests
from spaces import Client
from boto3 import session
from botocore.client import Config

class Migration:

    def __int__(self):
        pass

    def connectDigitalOcean(self):
        ACCESS_ID = 'spaces_migration'
        SECRET_KEY = 'DO008DNDZE9L2WMAT2TL'

        _session = session.Session()

        client = Client(
            region_name='nyc3',  # Required
            space_name='melanatedpeople',
            # Optional, can be set in spaces/env.yaml and/or be updated with <client>.set_space(space_name)
            public_key=ACCESS_ID,
            # Required, but can set key in spaces/env.yaml
            secret_key=SECRET_KEY,  # Required, but can set key in spaces/env.yaml

            # If any of region_name, public_key or secret_key are not provided, Client will override all values with env.yaml values.

        )

v = Migration()
v.connectDigitalOcean()