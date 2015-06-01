package listener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import model.Party;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class PartyScreen  extends JFrame implements ActionListener {

	private String serverIp;
	private DJYourParty djyp;
	private Image backgroundImage;
	private Party party;
	Map<String,Integer> partyMap;
	JComboBox partyComboBox;
	JButton enterButton;
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getSource() == enterButton){
		djyp.setCurrentParty(new Party(partyMap.get((String) partyComboBox.getSelectedItem()), (String)partyComboBox.getSelectedItem()));
		synchronized (this) {
			djyp.doNotify();
		}
		}
		
	}
	public PartyScreen(DJYourParty djpy) 
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
		selectPartyArea.setText("Select a Party");
		selectPartyArea.setAlignmentX(CENTER_ALIGNMENT);
		selectPartyArea.setEditable(false);
		selectPartyArea.setBackground(new Color(255,255,255,0));
		globalPane.add(selectPartyArea);
		globalPane.setVisible(true);
		
		URL url = null;
		try {
			url = new URL("http://" +serverIp+ ":8080/SpringMVC/rest/djparty/getpartybycreatorid?userid="+djpy.getCurrentUser().getId());

		HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		System.out.println(url);
		if (conn.getResponseCode() != 200) {
//			throw new RuntimeException("Failed : HTTP error code : "
//					+ conn.getResponseCode());
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
//		HashMap<Integer,Integer> mapVotes = new HashMap<Integer,Integer>();
		List<Party> partyList = mapper.readValue(msgReceived, TypeFactory.defaultInstance().constructCollectionType(List.class, Party.class));
		partyMap = new HashMap<String, Integer>();
		String[] partyNames = new String[partyList.size()];
		int i=0;
		for (Party p : partyList)
		{
			partyMap.put(p.getName(), p.getId());
			partyNames[i] = p.getName();
			i++;
		}

		
		

		partyComboBox= new JComboBox(partyNames);
		partyComboBox.setSelectedIndex(0);
		partyComboBox.addActionListener(this);
		enterButton = new JButton("Enter");
		enterButton.addActionListener(this);
		
		globalPane.add(partyComboBox);
		
//		JTextPane createPartyArea = new JTextPane();
//		createPartyArea.setText("Create a Party");
//		createPartyArea.setAlignmentX(CENTER_ALIGNMENT);
//		createPartyArea.setEditable(false);
//		createPartyArea.setBackground(new Color(255,255,255,0));
//		globalPane.add(createPartyArea);
		
		
		globalPane.add(enterButton);
		
		add(globalPane);
		pack();
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}

}
