

public class TestAmazon {



//	    public static ImageIcon getAlbumArtImageIcon(
//	            String artist, String album, String accessKeyId) throws Exception {
////	    	  String coverUrL = "http://coverartarchive.org/release/"+albumId+"/";
//	    	  String coverUrL ="http://ws.audioscrobbler.com/2.0/?method=album.search&album=half%20the%20city&api_key=e73f0cce21faef4b91f310f97afcdf9c&format=json";
////			  http://coverartarchive.org/release/ff16193a-2d4d-43f1-a5be-aa569ec87fa0/
//////		      http://coverartarchive.org/release/c04730ea-87cb-478b-a256-08c0561d20e6/
//		      
//		  	URL objCover = new URL(coverUrL);
//			HttpURLConnection conCover = (HttpURLConnection) objCover.openConnection();
//	 
//			// optional default is GET
//			conCover.setRequestMethod("GET");
//	 
//			//add request header
//			conCover.setRequestProperty("User-Agent", USER_AGENT);
//	 
//			int responseCodeCover = conCover.getResponseCode();
//			System.out.println("\nSending 'GET' request to URL : " + coverUrL);
//			System.out.println("Response Code : " + responseCodeCover);
//	 
//			BufferedReader inCover = new BufferedReader(
//			        new InputStreamReader(conCover.getInputStream()));
//			String inputLineCover;
//			StringBuffer responseCover = new StringBuffer();
//	 
//			while ((inputLineCover = inCover.readLine()) != null) {
//				responseCover.append(inputLineCover);
//			}
//			
//				inCover.close();
//		
//	 
//			//print result
//			System.out.println(responseCover.toString());
//			JSONObject objectJson = new JSONObject(responseCover.toString());
//			JSONArray jsonArray = objectJson.getJSONArray("images");
//			JSONObject jsonObjCover = (JSONObject) jsonArray.get(0);
//			JSONObject thumbnails = (JSONObject) jsonObjCover.get("thumbnails");
//			String smallCover= thumbnails.getString("small");


//	    }
}
