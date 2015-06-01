package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Song;

public class TableManager {

	static public void initTables() {
		ResultSet rs = null;
		Connection connection = getConnection();
		try {
//			 connection.prepareStatement("drop table songs if exists;").execute();
			connection
					.prepareStatement(
							"create table if not exists songs(id integer primary key identity, name varchar(60),artist varchar(60),album varchar(60),path varchar(200),partyid integer);")
					.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			rs = connection.prepareStatement("select id, name from songs;")
					.executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
		while (rs.next()){


			System.out.println(String.format("ID:%1d,Name : 1%s", rs.getInt(1),
					rs.getString(2)));
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Connection getConnection() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:hsqldb:file:djparty", "dj", "");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return connection;
	}

	public ArrayList<Song> getSongLibrary(int partyId) {
		Connection connection = getConnection();
		ResultSet rs = null;
		ArrayList<Song> result = new ArrayList<Song>();

		try {
			rs = connection.prepareStatement(
					"select id, name,artist,album,path,partyid from songs where partyid="+partyId+";")
					.executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			while (rs.next())
			{
				Song res = new Song(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),rs.getInt(6));
				result.add(res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	public ResultSet getSongLibraryResultSet(int partyId) {
		Connection connection = getConnection();
		ResultSet rs = null;
		ArrayList<Song> result = new ArrayList<Song>();

		try {
			rs = connection.prepareStatement(
					"select id, name,artist,album,path from songs where partyid="+partyId+ ";")
					.executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return rs;
	}

	
	public void insertSongLibrary(String name, String artist, String album,
			String path, int partyid) {
		Connection connection = getConnection();

		try {
			connection.prepareStatement(
					"insert into songs(name,artist,album,path,partyid)" + " values ('"
							+ name + "','" + artist + "','" + album + "','"
							+ path + "','" + partyid + "');").execute();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Song getSongFromId(Integer songId) {
		
		Connection connection = getConnection();
		ResultSet rs = null;

		try {
			rs = connection.prepareStatement(
					"select id, name,artist,album,path from songs where id="
							+ songId + ";").executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			if (rs.isBeforeFirst()){
			rs.next();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Song res = null;
		try {
			res = new Song(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		return res;
	}
	
	public void clearSongTable() {
		
		Connection connection = getConnection();

		try {
			connection.prepareStatement(
					"truncate table songs;").execute();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	
	public void clearSongParty(int partyId) {
		
		Connection connection = getConnection();

		try {
			connection.prepareStatement(
					"delete from songs where partyid="+partyId+";").execute();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}