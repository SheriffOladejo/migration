import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostMigration {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB1 = "jdbc:mysql://79.133.41.206:3306/admin_melanatedpeo";
    // Database credentials
    static final String DB1_USER = "admin_melanatedpeo";
    static final String DB1_PASS = "bf73jg0wufm0gs";

    static final String DB2 = "jdbc:mysql://79.133.41.206:3306/admin_l5ERv";
    // Database credentials
    static final String DB2_USER = "admin_l5ERv";
    static final String DB2_PASS = "irNdhZkzu8AU";

    static long startTime = System.currentTimeMillis() / 1000;

    static ArrayList<Post> post_list = new ArrayList<>();

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

    public static void main (String args[]) {
        getData();
    }

    private static void getData() {
        int last_id = 0;

        try {
            Connection conn2 = connect2_0();
            Statement stmt2 = conn2.createStatement();
            String query2 = "select action_id from Wo_Posts order by action_id desc limit 1";
            ResultSet result2 = stmt2.executeQuery(query2);
            while (result2.next()) {
                last_id = result2.getInt("action_id");
            }
            System.out.println("last id is: " + last_id);

            Connection conn1 = connect1_0();
            Statement stmt1 = conn1.createStatement();
            String query1 = "select * from engine4_activity_actions where action_id > " +
                    last_id + " and Date(date) > '2021-01-01 00:00:00' and (type = 'post' or type = 'post_self_photo' or type = 'post_self') limit 5000";
            ResultSet result1 = stmt1.executeQuery(query1);

            StringBuilder _query2 = new StringBuilder("insert into Wo_Posts (user_id, action_id, postType, postFile, postFileName, " +
                    "recipient_id, postText, time, registered, postLinkTitle, postLinkContent, postPrivacy," +
                    " originalPostType) values");

            int count = 0;
            while (result1.next()) {
                int action_id = result1.getInt("action_id");
                last_id = action_id;
                String postType = result1.getString("type");
                String originalPostType = postType;
                int userID = result1.getInt("subject_id");
                int recipientID = result1.getInt("object_id");
                String postText = result1.getString("body");
                String s = postText.replace("\\n", "<br>");

                String _date = result1.getString("date");

                SimpleDateFormat formatter
                        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = formatter.parse(_date);
                long creationDate = date.getTime() / 1000;

                String postFile = "";
                String postFileName = "";
                String registered = "0/0000";

                if (postType.equals("post_self_photo")) {
                    postType = "post";
                }
                else if (postType.equals("post_self")) {
                    postType = "post";
                }

                query1 = "select * from engine4_activity_attachments where action_id = " + action_id;
                ResultSet _result1 = stmt1.executeQuery(query1);

                while (_result1.next()) {
                    int id = _result1.getInt("subject_id");

                    query1 = "select * from engine4_storage_files where parent_id = " + id;
                    ResultSet __result1 = stmt1.executeQuery(query1);

                    while (__result1.next()) {
                        String type = __result1.getString("type");
                        if (type == null || type.equals("thumb.normal") || type.isEmpty()) {
                            postFile = __result1.getString("storage_path");
                            postFileName = __result1.getString("name");
                        }
                    }
                }

                if (postText.length() >= 3) {
                    if (postText.substring(0, 2).equals("b'") || postText.substring(0, 2).equals("b\"")) {
                        postText = postText.substring(2, postText.length() - 1);
                    }
                }

                Post post = new Post();
                post.setActionId(action_id);
                post.setUserId(userID);
                post.setPostType(postType);
                post.setOriginalPostType(originalPostType);
                post.setPostFile(postFile);
                post.setPostFileName(postFileName);
                post.setRecipientId(recipientID);
                post.setPostText(postText);
                post.setTime(creationDate);
                post.setRegistered(registered);

                String _get_registered_query = "select registered from Wo_Users where user_id = " + post.getUserId();
                ResultSet _result2 = stmt2.executeQuery(_get_registered_query);

                while (_result2.next()) {
                    post.setRegistered(_result2.getString("registered"));
                }

                if (count < 5000) {
                    _query2.append(String.format(" (%d, %d, %s, %s, %s, %d, %s, %d, %s, %s, %s, %s, %s), ",
                            post.getUserId(),
                            post.getActionId(),
                            post.getPostType(),
                            post.getPostFile(),
                            post.getPostFileName(),
                            post.getRecipientId(),
                            post.getPostText(),
                            post.getTime(),
                            post.getRegistered(),
                            "",
                            "",
                            "0",
                            post.getOriginalPostType()));
                }
                else if (count == 5000) {
                    _query2.append(String.format(" (%d, %d, %s, %s, %s, %d, %s, %d, %s, %s, %s, %s, %s)",
                            post.getUserId(),
                            post.getActionId(),
                            post.getPostType(),
                            post.getPostFile(),
                            post.getPostFileName(),
                            post.getRecipientId(),
                            post.getPostText(),
                            post.getTime(),
                            post.getRegistered(),
                            "",
                            "",
                            "0",
                            post.getOriginalPostType()));
                }

                count++;

            }

        }
        catch (Exception e) {
            System.err.println("getData: An error occurred: " + e.toString());
        }

    }

}


class Post {
    private int actionId;
    private int userId;
    private int recipientId;
    private String postText;
    private String postFile;
    private String postFileName;
    private String postType;
    private String originalPostType;
    private long time;
    private String registered;

    public Post(int actionId, int userId, int recipientId, String postText, String postFile, String postFileName, String postType, String originalPostType, long time, String registered) {
        this.actionId = actionId;
        this.userId = userId;
        this.recipientId = recipientId;
        this.postText = postText;
        this.postFile = postFile;
        this.postFileName = postFileName;
        this.postType = postType;
        this.originalPostType = originalPostType;
        this.time = time;
        this.registered = registered;
    }

    public Post () {}

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostFile() {
        return postFile;
    }

    public void setPostFile(String postFile) {
        this.postFile = postFile;
    }

    public String getPostFileName() {
        return postFileName;
    }

    public void setPostFileName(String postFileName) {
        this.postFileName = postFileName;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getOriginalPostType() {
        return originalPostType;
    }

    public void setOriginalPostType(String originalPostType) {
        this.originalPostType = originalPostType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }
}