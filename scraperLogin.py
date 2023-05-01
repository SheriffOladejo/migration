import requests
from bs4 import BeautifulSoup

# Set the login credentials
username = 'demo-seem0re'
password = 'Niko12345'

# Set the login URL and target URL
login_url = 'https://demo-trade.nadex.com/iDeal/v2/security/authenticate'
target_url = 'https://demo-trade.nadex.com/target_page'

# Create a session object to persist the login cookies
session = requests.Session()

# Send a GET request to the login page to retrieve the login form data
response = session.get(login_url)
soup = BeautifulSoup(response.content, 'html.parser')

# Extract the CSRF token and any other required parameters from the login form data
csrf_token = soup.find('input', {'name': 'csrf_token'})['value']

# Send a POST request to the login page with the login credentials and CSRF token
data = {
    'username': username,
    'password': password,
    'csrf_token': csrf_token
}
response = session.post(login_url, data=data)

# Check if the login was successful by examining the response
if response.status_code != 200:
    print('Login failed')
    exit()

# Send a GET request to the target page to scrape the data
response = session.get(target_url)
soup = BeautifulSoup(response.content, 'html.parser')

# Extract the data you need from the HTML content
# For example, if the data is in a table, you could do something like this:
table = soup.find('table')
rows = table.find_all('tr')
for row in rows:
    cells = row.find_all('td')
    for cell in cells:
        print(cell.text)
