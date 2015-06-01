package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import listener.CurrentMusic;
import listener.DJYourParty;
import model.Vote;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class NotificationWatcher implements Runnable {

	CurrentMusic cm;
//	String serverIp = "5.135.167.143";
	String serverIp = "localhost";
	public NotificationWatcher(CurrentMusic cm) {
		super();
		this.cm = cm;
		if (DJYourParty.deployMode)
		{
			serverIp = "5.135.167.143";
		}else
		{
			serverIp = "localhost";
		}
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {
			try {

				URL url = new URL("http://" +serverIp+ ":8080/SpringMVC/rest/djparty/getvotes?partyId=1&time=toto");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				System.out.println(url);
				if (conn.getResponseCode() != 200) {
//					throw new RuntimeException("Failed : HTTP error code : "
//							+ conn.getResponseCode());
					System.out.println("Server is unreachable");
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				String msgReceived ="";
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
					msgReceived = msgReceived + output;
				}
				ObjectMapper mapper = new ObjectMapper();
				HashMap<Integer,Integer> mapVotes = new HashMap<Integer,Integer>();
				List<Vote> voteList = mapper.readValue(msgReceived, TypeFactory.defaultInstance().constructCollectionType(List.class, Vote.class));
				for (Vote v : voteList)
				{
					System.out.println("User : " + v.getUserid() + " voted for song : " + v.getSongid() + "in the party : " + v.getPartyid());
					if(mapVotes.containsKey(v.getSongid()))
					{
						int voteCount = mapVotes.get(v.getSongid());
						mapVotes.put(v.getSongid(), voteCount++);
					}
					else
					{
						mapVotes.put(v.getSongid(), 1);
					}
				}
				cm.notifyVote(mapVotes);
			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("INTERRUTED");
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}
}
