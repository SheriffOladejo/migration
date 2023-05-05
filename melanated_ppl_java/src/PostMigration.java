import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

    }

    private void getData() {
        int last_id = 0;

        try {
            Connection conn2 = connect2_0();
            Statement stmt2 = conn2.createStatement();
            String query = "select action_id from Wo_Posts order by action_id desc limit 1";
            ResultSet result = stmt2.executeQuery(query);
            while (result.next()) {
                last_id = result.getInt("action_id");
            }
            System.out.println("last id is: " + last_id);


            Connection conn1 = connect1_0();
            Statement stmt1 = conn1.createStatement();
            //String query = "select * frm ";
        }
        catch (Exception e) {
            System.err.println("getData: An error occurred: " + e.toString());
        }

    }

}


class Post {
    private String actionId;
    private String userId;
    private String recipientId;
    private String postText;
    private String postFile;
    private String postFileName;
    private String postType;
    private String originalPostType;
    private String time;
    private String registered;

    public Post(String actionId, String userId, String recipientId, String postText, String postFile, String postFileName, String postType, String originalPostType, String time, String registered) {
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

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }
}