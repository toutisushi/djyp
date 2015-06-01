package listener;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import model.Party;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class CreatePartyScreen   extends JFrame implements ActionListener {

	private String serverIp;
	private DJYourParty djyp;
	private Image backgroundImage;
	JButton createButton;
	JTextField partyNameField;
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		CreatePartyScreen cps = new CreatePartyScreen(new DJYourParty());
//		cps.setVisible(true);
//	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == createButton) {
//			URL url = null;
//			try {
////				url = new URL(
////						"http://"
////								+ serverIp
////								+ ":8080/SpringMVC/rest/djparty/createparty?userid="+ djyp.getCurrentUser().getId() + "&partyname="+URLEncoder.encode(partyNameField.getText(),"UTF-8")+"");
//				url = new URL(
//						"http://"
//								+ serverIp
//								+ ":8080/SpringMVC/rest/djparty/createparty?userid="+ djyp.getCurrentUser().getId() + "&partyname="+partyNameField.getText());
//
//				HttpURLConnection conn = (HttpURLConnection) url
//						.openConnection();
//				conn.setRequestMethod("GET");
//				conn.setRequestProperty("Accept", "application/json");
//				System.out.println(url);
//				if (conn.getResponseCode() != 200) {
//					// throw new RuntimeException("Failed : HTTP error code : "
//					// + conn.getResponseCode());
//					System.out.println("Server is unreachable");
//				}
//
//				BufferedReader br = new BufferedReader(new InputStreamReader(
//						(conn.getInputStream())));
//
//				String output;
//				String msgReceived = "";
//				System.out.println("Output from Server .... \n");
//				while ((output = br.readLine()) != null) {
//					System.out.println(output);
//					msgReceived = msgReceived + output;
//				}
//
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			RestTemplate restTemplate = new RestTemplate();
//			HttpHeader headers
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		headers.add("Content-Type", MediaType.ALL_VALUE);
//		headers.add("Accept", MediaType.ALL_VALUE);
		HttpEntity request = new HttpEntity("", headers);
		String req = null;
		try {
			req = "http://"
				+ serverIp
				+ ":8080/SpringMVC/rest/djparty/createparty?userid="+ djyp.getCurrentUser().getId() + "&partyname="+URLEncoder.encode(partyNameField.getText(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer response2 = restTemplate.postForObject(req , "", Integer.class);
		System.out.println("RESPONSE : " + response2);
		djyp.setCurrentParty(new Party(response2, partyNameField.getText()));
		djyp.doNotify();
		
		}
	}

	public CreatePartyScreen(DJYourParty djpy) 
	{
		super();
		this.djyp=djpy;
		if (DJYourParty.deployMode) {
			serverIp = "5.135.167.143";
		} else {
			serverIp = "localhost";
		}
		com.apple.eawt.Application.getApplication().setDockIconImage(
				new ImageIcon(getClass().getResource("/DjYourParty.png"))
						.getImage());

		try {
			backgroundImage = ImageIO.read(getClass()
					.getResource("/testBG.png"));

		} catch (IOException e1) {
		}
		// Use the default FlowLayout.
		// controllingFrame = f;

		JPanel globalPane  = new JPanel(){
			@Override
			  public void paintComponent(Graphics g) {
				    super.paintComponents(g);

				    // Draw the background image.
				    g.drawImage(backgroundImage, 0, 0, this);
				  }
		};
		BoxLayout bl = new BoxLayout(globalPane,BoxLayout.Y_AXIS);
		globalPane.setLayout(bl);
		JTextPane selectPartyArea = new JTextPane();
		selectPartyArea.setText("Create a Party");
		selectPartyArea.setAlignmentX(CENTER_ALIGNMENT);
		selectPartyArea.setEditable(false);
		selectPartyArea.setBackground(new Color(255,255,255,0));
		
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "SansSerif");
		StyleConstants.setFontSize(attribs, 15);
		selectPartyArea.setParagraphAttributes(attribs,true);
		
		globalPane.add(selectPartyArea);
		globalPane.setVisible(true);
		
//		JTextPane signupArea = new JTextPane();
//		signupArea.setText("Sign In");
//		signupArea.setAlignmentX(CENTER_ALIGNMENT);
//		signupArea.setEditable(false);
//		signupArea.setBackground(new Color(255,255,255,0));
		
		
//		ObjectMapper mapper = new ObjectMapper();
//		HashMap<Integer,Integer> mapVotes = new HashMap<Integer,Integer>();
//		List<Party> partyList = mapper.readValue(msgReceived, TypeFactory.defaultInstance().constructCollectionType(List.class, Party.class));
//		partyMap = new HashMap<String, Integer>();
//		String[] partyNames = new String[partyList.size()];
//		int i=0;
//		for (Party p : partyList)
//		{
//			partyMap.put(p.getName(), p.getId());
//			partyNames[i] = p.getName();
//			i++;
//		}
		JPanel textPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textPane.setBackground(new Color(255,255,255,0));
		JTextPane partyNameLabel = new JTextPane();
		partyNameLabel.setText("Party Name");
		partyNameLabel.setEditable(false);
		partyNameLabel.setBackground(new Color(255,255,255,0));

		partyNameField = new JTextField(20);
		
		createButton = new JButton("Create");
		createButton.addActionListener(this);
		
		globalPane.add(selectPartyArea);
		
		textPane.add(partyNameLabel);
		textPane.add(partyNameField);
		globalPane.add(textPane);
		
		globalPane.add(createButton);
		
		add(globalPane);
		pack();
	
	}
	
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
