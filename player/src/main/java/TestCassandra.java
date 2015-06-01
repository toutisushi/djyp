import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


public class TestCassandra {
	
	Cluster cluster;
	Session session;
	
	public void insertUser()
	{
		cluster = Cluster.builder().addContactPoint("5.135.167.143").build();
		session = cluster.connect("djyp");
		session.execute("INSERT INTO users (userid,name,password) VALUES (now(),'lopo','bulle')");
	}
	
	public void insertSong()
	{
//		cluster = Cluster.builder().addContactPoint("5.135.167.143").build();
//		session = cluster.connect("djyp");
		session.execute("INSERT INTO songuserlibray ( (userid,songuserid,name, artist,album,path,urlcover) VALUES ('ea611e10-f0a9-11e4-b434-abfe4ecfdcde',now(),'Couleur menthe à l'eau','Eddiy Mitchell','Album Eddy','C:/MP3','http://ia802701.us.archive.org/2/items/mbid-51f3f496-01b9-46f6-a3a3-54da87db0b71/mbid-51f3f496-01b9-46f6-a3a3-54da87db0b71-9240189573_thumb250.jpg')");
	}
	
//	create table songuserlibray (userid uuid,songuserid uuid,name text, artist text,album text,path text,urlcover text, primary key((userid))); 
	
	public void getUsers()
	{
		ResultSet results = session.execute("SELECT * FROM users");
		for (Row row : results) {
		System.out.format("%s %s\n", row.getString("name"), row.getString("password"));
		}
		
		session.close();
		cluster.close();
	}

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
		TestCassandra tc = new TestCassandra();
		tc.insertUser();
		tc.insertSong();
		tc.getUsers();
		
		

	}

}
