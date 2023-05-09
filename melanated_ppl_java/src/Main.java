import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB1 = "jdbc:mysql://79.133.41.206:3306/admin_melanatedpeo";
    // Database credentials
    static final String DB1_USER = "admin_melanatedpeo";
    static final String DB1_PASS = "bf73jg0wufm0gs";

    static final String DB2 = "jdbc:mysql://79.133.41.206:3306/admin_5HXPW";
    // Database credentials
    static final String DB2_USER = "admin_5HXPW";
    static final String DB2_PASS = "HPcrb3VbrylD";

    static long startTime = System.currentTimeMillis() / 1000;

    private static Connection connect1_0() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB1, DB1_USER, DB1_PASS);
        }
        catch (Exception e ) {
            System.err.println("An error occurred: " + e.toString());
        }
        return conn;
    }

    private static Connection connect2_0() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB2, DB2_USER, DB2_PASS);
        }
        catch (Exception e ) {
            System.err.println("An error occurred: " + e.toString());
        }
        return conn;
    }

    private static void getData() {
        int last_id = 0;
        Connection conn = connect2_0();
        try {
            Statement stmt = conn.createStatement();
            String query = "select user_id from Wo_Users where friends_copied = 1 order by user_id desc limit 1";
            ResultSet result = stmt.executeQuery(query);
            while (result.next()) {
                last_id = result.getInt("user_id");
            }
            System.out.println("last id is: " + last_id);

            Connection conn1 = connect1_0();
            Statement stmt1 = conn1.createStatement();
            String query1 = "select * from engine4_users where user_id > " + last_id + " limit 5000";
            ResultSet result1 = stmt1.executeQuery(query1);
            while (result1.next()) {
                int user_id = result1.getInt("user_id");
                last_id = user_id;

                ArrayList<Integer> _followers = getFollowers(user_id);
                ArrayList<Integer> _following = getFollowing(user_id);

                JSONArray followers = new JSONArray(_followers);
                JSONArray following = new JSONArray(_following);

                String query2 = "select * from Wo_Users where user_id = " + user_id;
                ResultSet result2 = stmt.executeQuery(query2);
                while (result2.next()) {
                    JSONObject details = new JSONObject(result2.getString("details"));
                    JSONObject sidebar_data = new JSONObject();
                    if (result2.getString("sidebar_data") == null) {
                        JSONArray arr = new JSONArray();
                        sidebar_data.put("following_data", arr);
                        sidebar_data.put("followers_data", arr);
                        sidebar_data.put("likes_data", arr);
                        sidebar_data.put("groups_data", arr);
                        sidebar_data.put("mutual_friends_data", arr);
                    }
                    else {
                        sidebar_data = new JSONObject(result2.getString("sidebar_data"));
                    }
                    sidebar_data.put("following_data", following);
                    sidebar_data.put("followers_data", followers);

                    details.put("following_count", _following.size());
                    details.put("followers_count", _followers.size());


                    String query3 = """
                            update Wo_Users set friends_copied = ?, details = ?, sidebar_data = ? where user_id = ?
                            """;
                    PreparedStatement statement = conn.prepareStatement(query3);
                    statement.setInt(1, 1);
                    statement.setString(2, details.toString());
                    statement.setString(3, sidebar_data.toString());
                    statement.setInt(4, user_id);
                    statement.executeUpdate();
                }
            }
        }
        catch (Exception e) {
            System.err.println("getData: An error occurred: " + e.toString());
        }
    }

    private static void updateWoFollowings(int user_id, ArrayList<Integer> followings) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (timestamp - startTime <= 14400) {
            try {
                Connection conn = connect2_0();
                Statement stmt = conn.createStatement();
                String query = "select * from Wo_Followers where follower_id = " + user_id +
                        " order by id desc limit 1";
                ResultSet result = stmt.executeQuery(query);
//                int last_following_id = 0;
//                while (result.next()) {
//                    last_following_id = result.getInt("following_id");
//                }
//                if (last_following_id != 0) {
//                    int index = followings.indexOf(new Integer(last_following_id)) + 1;
//                    String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
//                    for (int i = index; i < followings.size(); i++) {
//                        String following_id = String.valueOf(followings.get(i));
//                        if (i == followings.size() - 1) {
//                            insert_query += "(" + following_id + ", " + user_id + ", 1)";
//                        }
//                        else {
//                            insert_query += "(" + following_id + ", " + user_id + ", 1), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
//                else {
//                    String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
//                    for (int i = 0; i < followings.size(); i++) {
//                        String following_id = String.valueOf(followings.get(i));
//                        if (i == followings.size() - 1) {
//                            insert_query += "(" + following_id + ", " + user_id + ", 1)";
//                        }
//                        else {
//                            insert_query += "(" + following_id + ", " + user_id + ", 1), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
                String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
                for (int i = 0; i < followings.size(); i++) {
                    String following_id = String.valueOf(followings.get(i));
                    if (i == followings.size() - 1) {
                        insert_query += "(" + following_id + ", " + user_id + ", 1)";
                    }
                    else {
                        insert_query += "(" + following_id + ", " + user_id + ", 1), ";
                    }
                }
                stmt.execute(insert_query);
            }
            catch (Exception e) {
                System.err.println("updateWoFollowings: An error occurred: " + e.toString());
            }
        }
    }

    private static void updateWoFollowers(int user_id, ArrayList<Integer> followers) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (timestamp - startTime <= 14400) {
            try {
                Connection conn = connect2_0();
                Statement stmt = conn.createStatement();
                String query = "select * from Wo_Followers where following_id = " + user_id +
                        " order by id desc limit 1";
                ResultSet result = stmt.executeQuery(query);
//                int last_follower_id = 0;
//                while (result.next()) {
//                    last_follower_id = result.getInt("follower_id");
//                }
//                if (last_follower_id != 0) {
//                    int index = followers.indexOf(new Integer(last_follower_id)) + 1;
//                    String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
//                    for (int i = index; i < followers.size(); i++) {
//                        String follower_id = String.valueOf(followers.get(i));
//                        if (i == followers.size() - 1) {
//                            insert_query += "(" + user_id + ", " + follower_id + ", 1)";
//                        }
//                        else {
//                            insert_query += "(" + user_id + ", " + follower_id + ", 1), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
//                else {
//                    String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
//                    for (int i = 0; i < followers.size(); i++) {
//                        String follower_id = String.valueOf(followers.get(i));
//                        if (i == followers.size() - 1) {
//                            insert_query += "(" + user_id + ", " + follower_id + ", 1)";
//                        }
//                        else {
//                            insert_query += "(" + user_id + ", " + follower_id + ", 1), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
                String insert_query = "insert into Wo_Followers (following_id, follower_id, active) values ";
                for (int i = 0; i < followers.size(); i++) {
                    String follower_id = String.valueOf(followers.get(i));
                    if (i == followers.size() - 1) {
                        insert_query += "(" + user_id + ", " + follower_id + ", 1)";
                    }
                    else {
                        insert_query += "(" + user_id + ", " + follower_id + ", 1), ";
                    }
                }
                stmt.execute(insert_query);
            }
            catch (Exception e) {
                System.err.println("updateWoFollowers: An error occurred: " + e.toString());
            }
        }
    }

    private static void updateWoFollowerActivities(int user_id, ArrayList<Integer> followers) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (timestamp - startTime <= 14400) {
            try {
                Connection conn = connect2_0();
                Statement stmt = conn.createStatement();
                String query = "select * from Wo_Activities where follow_id = " + user_id +
                        " order by id desc limit 1";
                ResultSet result = stmt.executeQuery(query);
                int last_follower_id = 0;
//                while (result.next()) {
//                    last_follower_id = result.getInt("user_id");
//                }
//                if (last_follower_id != 0) {
//                    int index = followers.indexOf(new Integer(last_follower_id)) + 1;
//                    String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
//                    for(int i = index; i < followers.size(); i++) {
//                        String follower_id = String.valueOf(followers.get(i));
//                        String time = String.valueOf(timestamp);
//                        if (i == followers.size() - 1) {
//                            insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + ")";
//                        }
//                        else {
//                            insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + "), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
//                else {
//                    String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
//                    for(int i = 0; i < followers.size(); i++) {
//                        String follower_id = String.valueOf(followers.get(i));
//                        String time = String.valueOf(timestamp);
//                        if (i == followers.size() - 1) {
//                            insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + ")";
//                        }
//                        else {
//                            insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + "), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
                String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
                for(int i = 0; i < followers.size(); i++) {
                    String follower_id = String.valueOf(followers.get(i));
                    String time = String.valueOf(timestamp);
                    if (i == followers.size() - 1) {
                        insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + ")";
                    }
                    else {
                        insert_query += "(" + follower_id + ", " + user_id + ", 'following', " + time + "), ";
                    }
                }
                stmt.execute(insert_query);
            }
            catch (Exception e) {
                System.err.println("updateWoFollowerActivities: An error occurred: " + e.toString());
            }
        }
    }

    private static void updateWoFollowingActivities(int user_id, ArrayList<Integer> followings) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (timestamp - startTime <= 14400) {
            try {
                Connection conn = connect2_0();
                Statement stmt = conn.createStatement();
                String query = "select * from Wo_Activities where user_id = " + user_id +
                        " order by id desc limit 1";
                ResultSet result = stmt.executeQuery(query);
//                int last_follow_id = 0;
//                while (result.next()) {
//                    last_follow_id = result.getInt("follow_id");
//                }
//                if (last_follow_id != 0) {
//                    int index = followings.indexOf(new Integer(last_follow_id)) + 1;
//                    String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
//                    for(int i = index; i < followings.size(); i++) {
//                        String following_id = String.valueOf(followings.get(i));
//                        String time = String.valueOf(timestamp);
//                        if (i == followings.size() - 1) {
//                            insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + ")";
//                        }
//                        else {
//                            insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + "), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
//                else {
//                    String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
//                    for(int i = 0; i < followings.size(); i++) {
//                        String following_id = String.valueOf(followings.get(i));
//                        String time = String.valueOf(timestamp);
//                        if (i == followings.size() - 1) {
//                            insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + ")";
//                        }
//                        else {
//                            insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + "), ";
//                        }
//                    }
//                    stmt.execute(insert_query);
//                }
                String insert_query = "insert into Wo_Activities (user_id, follow_id, activity_type, time) values ";
                for(int i = 0; i < followings.size(); i++) {
                    String following_id = String.valueOf(followings.get(i));
                    String time = String.valueOf(timestamp);
                    if (i == followings.size() - 1) {
                        insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + ")";
                    }
                    else {
                        insert_query += "(" + user_id + ", " + following_id + ", 'following', " + time + "), ";
                    }
                }
                stmt.execute(insert_query);
            }
            catch (Exception e) {
                System.err.println("updateWoFollowingActivities: An error occurred: " + e.toString());
            }
        }
    }


    private static ArrayList<Integer> getFollowing(int user_id) {
        ArrayList<Integer> following = new ArrayList<>();
        try {
            Connection conn = connect1_0();
            Statement stmt = null;
            stmt = conn.createStatement();
            String query = "select resource_id from engine4_user_membership where user_id = " + user_id +
                    " and resource_approved = '1' and active = '1' and user_approved = '1'";
            ResultSet result = stmt.executeQuery(query);
            while (result.next()) {
                int following_id = result.getInt("resource_id");
                following.add(following_id);
            }
        }
        catch (Exception e) {
            System.err.println("getFollowing: An error occurred: " + e.toString());
        }
        updateWoFollowings(user_id, following);
        updateWoFollowingActivities(user_id, following);

        return following;
    }

    private static ArrayList<Integer> getFollowers(int user_id) {
        ArrayList<Integer> followers = new ArrayList<>();
        try {
            Connection conn = connect1_0();
            Statement stmt = null;
            stmt = conn.createStatement();
            String query = "select resource_id from engine4_user_membership where user_id = " + user_id +
                    " and resource_approved = '1' and active = '1' and user_approved = '1'";
            ResultSet result = stmt.executeQuery(query);
            while (result.next()) {
                int follower_id = result.getInt("resource_id");
                followers.add(follower_id);
            }
        }
        catch (Exception e) {
            System.err.println("getFollowers: An error occurred: " + e.toString());
        }

        updateWoFollowers(user_id, followers);
        updateWoFollowerActivities(user_id, followers);

        return followers;
    }

    public static void main(String[] args) {
        getData();
    }

}