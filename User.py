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