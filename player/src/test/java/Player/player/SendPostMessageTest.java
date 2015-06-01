package Player.player;

import model.Song;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class SendPostMessageTest {
//	static String serverIp = "5.135.167.143";
	static String serverIp = "localhost";
	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
//		HttpHeader headers
	HttpHeaders headers = new HttpHeaders();
	
	headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//	headers.add("Content-Type", MediaType.ALL_VALUE);
//	headers.add("Accept", MediaType.ALL_VALUE);
//	HttpEntity request = new HttpEntity(j2, headers);
	String req = null;
	req = "http://" +serverIp+ ":8080/SpringMVC/rest/djparty/voteforsong?partyId=1&songId=135&userId=1";
	String response2 = restTemplate.postForObject(req , new Song(), String.class);
	}

}
