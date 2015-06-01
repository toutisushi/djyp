package process;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import listener.DJYourParty;
import model.Song;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import ucar.nc2.util.net.URLencode;

public class NotificationSender implements Runnable{


//	String serverIp = "5.135.167.143";
	String serverIp = "localhost";
	private List<Song> songs;
	
	
	public NotificationSender(List<Song> songs) {
		super();
		this.songs = songs;
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
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = null;
		try {
			json = ow.writeValueAsString(songs.get(0));
			System.out.println("THREAD " + json);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Song s = new Song(1, "t", "a", "a", "p");
    	String j2 = "{\"id\" : 1,\"name\" : \"name2\",\"artist\" : \"artis2t\",\"album\" :\"a2lbum\",\"path\" : \"p2ath\"}";

    	String urlClearSong = "http://" +serverIp+ ":8080/SpringMVC/rest/djparty/clearsongsbypartyid?partyId="+songs.get(0).getPartyId();
    	RestTemplate restTemplate = new RestTemplate();
    	String responseClearSong = restTemplate.postForObject(urlClearSong , s, String.class);
    	
    	
		String url2 = "http://" +serverIp+ ":8080/SpringMVC/rest/djparty/syncsongs";
		
//		String req = "http://localhost:8080/SpringMVC/rest/djparty/syncsongs?id=1&name="+ test + "&artist=" + test2 + "&album=" + test2 + "&path=" + test2;
		
		int i =0;
		for (Song currentSong : songs)
		{
			createSong(s, j2, i, currentSong);	
		}
//		String response3 = restTemplate.postForObject(req , s, String.class);

		//    	Song response2 = restTemplate.postForObject("http://localhost:8080/SpringMVC/rest/djparty/syncsongs", s, Song.class);
    	
    	//		
//		mapper.convertValue
//		(,TypeFactory.defaultInstance().constructCollectionType(List.class, Song.class));
//		StringRequestEntity requestEntity = new StringRequestEntity(
//				json,
//			    "application/json",
//			    "UTF-8");
//
//			PostMethod postMethod = new PostMethod("http://example.com/action");
//			postMethod.setRequestEntity(requestEntity);
//
//			int statusCode = httpClient.executeMethod(postMethod);
		
//		String url="http://localhost:8080/SpringMVC/rest/djparty/syncsongs";
//
//		URL object = null;
//		try {
//			object = new URL(url);
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		}
//
//		HttpURLConnection con = null;
//		try {
//			con = (HttpURLConnection) object.openConnection();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//
//
//		try {
//			con.setRequestMethod("POST");
//		} catch (ProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
////		con.setDoInput(true);
//
//		con.setRequestProperty("Content-Type", "application/json");
//		con.setDoOutput(true);
//		
////		con.setRequestProperty("Accept", "application/json");
////		OutputStreamWriter wr = null;
////		try {
////			wr = new OutputStreamWriter(con.getOutputStream());
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//		DataOutputStream wr = null;
//		try {
//			wr = new DataOutputStream(con.getOutputStream());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			System.out.println("SENDING : " + json);
//			mapper.writeValue(wr, json);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			wr.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			wr.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		int responseCode = 0;
//		try {
//			responseCode = con.getResponseCode();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("\nSending 'POST' request to URL : " + url);
////		System.out.println("Post parameters : " + urlParameters);
//		System.out.println("Response Code : " + responseCode);
// 
//		BufferedReader in = null;
//		try {
//			in = new BufferedReader(
//			        new InputStreamReader(con.getInputStream()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String inputLine;
//		StringBuffer response = new StringBuffer();
// 
//		try {
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			in.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
// 
		
		
		
//		//print result
//		System.out.println(response.toString());
		
		
//		HttpClient httpClient = new DefaultHttpClient();
//
//		try {
//		    HttpPost request = new HttpPost(url);
//		    StringEntity params =new StringEntity("{\"id\":2,\"partyid\":1,\"songid\":2,\"userid\":\"Lam\"}");
//		    request.addHeader("content-type", "application/json");
//		    request.setHeader("User-Agent", USER_AGENT);
//		    request.addHeader("Accept","application/json");
//		    request.setEntity(params);
//			request.setDoOutput(true);
//		    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//			urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
//			urlParameters.add(new BasicNameValuePair("cn", ""));
//			urlParameters.add(new BasicNameValuePair("locale", ""));
//			urlParameters.add(new BasicNameValuePair("caller", ""));
//			urlParameters.add(new BasicNameValuePair("num", "12345"));
	 
//			request.setEntity();
	 
//		    		con.setDoInput(true);
//		    HttpResponse response = httpClient.execute(request);

		    // handle response here...
//		}catch (Exception ex) {
//		    // handle exception here
//		} finally {
//		    httpClient.getConnectionManager().shutdown();
//		}
		
//		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

//		try {
//		    HttpPost request = new HttpPost("http://yoururl");
//		    StringEntity params = new StringEntity(json.toString());
//		    request.addHeader("content-type", "application/json");
//		    request.setEntity(params);
//		    httpClient.execute(request);
//		// handle response here...
//		} catch (Exception ex) {
//		    // handle exception here
//		} finally {
//		    httpClient.close();
//		}
	}


	private void createSong(Song s, String j2, int i, Song currentSong) {
		RestTemplate restTemplate = new RestTemplate();
//			HttpHeader headers
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity request = new HttpEntity(j2, headers);
		String req = null;
		try {
			req = "http://" +serverIp+ ":8080/SpringMVC/rest/djparty/syncsongs?id=" + currentSong.getId()+ "&name="+ URLEncoder.encode(currentSong.getName(),"UTF-8") + "&artist=" + URLEncoder.encode(currentSong.getArtist(),"UTF-8") + "&album=" + URLEncoder.encode(currentSong.getAlbum(),"UTF-8") + "&path=" + URLEncoder.encode(currentSong.getPath(),"UTF-8")+ "&partyId=" +currentSong.getPartyId();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String response2 = restTemplate.postForObject(req , s, String.class);
		System.out.println("RESPONSE : "+ i+ " " + response2);
	}

}
