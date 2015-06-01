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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ChooseActionScreen   extends JFrame implements ActionListener {

	private String serverIp;
	private DJYourParty djyp;
	private Image backgroundImage;
	JButton createButton;
	JButton joinButton;
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == joinButton) {
			djyp.setCreation(false);

		} else {
			djyp.setCreation(true);
		}
		djyp.setChoiceMade(true);
		synchronized (this) {
			djyp.doNotify();
		}

	}

	public ChooseActionScreen(DJYourParty djpy) 
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
		selectPartyArea.setText("Make a Choice");
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
		
		URL url = null;
		try {
			url = new URL("http://" +serverIp+ ":8080/SpringMVC/rest/djparty/getpartybycreatorid?userid=1");

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
		
		JPanel textPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textPane.setBackground(new Color(255,255,255,0));
		createButton = new JButton("Create a Party");
		createButton.addActionListener(this);
		joinButton = new JButton("Manage a Party");
		joinButton.addActionListener(this);
		globalPane.add(selectPartyArea);
		
		textPane.add(createButton);
		textPane.add(joinButton);
		
		globalPane.add(textPane);
		
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
