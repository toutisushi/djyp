package listener;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import model.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class SignupScreen extends JFrame implements ActionListener {

	private Image backgroundImage;
	private static String OK = "ok";
	private DJYourParty djyp;
	private JPasswordField passwordField;
	private JTextField loginField;
	private String serverIp;

	public SignupScreen() throws HeadlessException {
	}

	public SignupScreen(DJYourParty djpy) {
		super();
		if (DJYourParty.deployMode) {
			serverIp = "5.135.167.143";
		} else {
			serverIp = "localhost";
		}
		com.apple.eawt.Application.getApplication().setDockIconImage(
				new ImageIcon(getClass().getResource("/DjYourParty.png"))
						.getImage());
		setDjyp(djpy);

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
		JTextPane signupArea = new JTextPane();
		signupArea.setText("Sign In");
		signupArea.setAlignmentX(CENTER_ALIGNMENT);
		signupArea.setEditable(false);
		signupArea.setBackground(new Color(255,255,255,0));
		 
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "SansSerif");
		StyleConstants.setFontSize(attribs, 15);
		signupArea.setParagraphAttributes(attribs,true);
//		signupArea.setAl
//		signupArea.setSize(100, 10);
		// Create everything.
		passwordField = new JPasswordField(10);
		passwordField.setActionCommand(OK);
		passwordField.addActionListener(this);

		loginField = new JTextField(10);
		JLabel passwordlabel = new JLabel("Enter the password: ");
		passwordlabel.setLabelFor(passwordField);

		JLabel loginlabel = new JLabel("Enter the login: ");
		loginlabel.setLabelFor(loginField);

		JButton button = new JButton("Enter");
		button.addActionListener(this);
		// JComponent buttonPane = createButtonPanel();

		// Lay out everything.
		JPanel textPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
//		textPane.add(signupArea);
		textPane.add(loginlabel);
		textPane.add(loginField);
		textPane.add(passwordlabel);
		textPane.add(passwordField);
		textPane.setBackground(new Color(255,255,255,0));

		textPane.setVisible(true);
		globalPane.add(signupArea);
		globalPane.add(textPane);
		globalPane.add(button);
//		
		globalPane.setVisible(true);
		add(globalPane);
		pack();
		
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("User :" + loginField.getText() + " " + "trying to connect with password " + passwordField.getPassword().toString());
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//		headers.add("Content-Type", MediaType.ALL_VALUE);
//		headers.add("Accept", MediaType.ALL_VALUE);
		HttpEntity request = new HttpEntity("", headers);
		String req = null;
		req = "http://" +serverIp+ ":8080/SpringMVC/rest/djparty/connect?user=" + loginField.getText()+ "&password="+ passwordField.getPassword().toString();
		Integer response2 = restTemplate.postForObject(req , "", Integer.class);
		System.out.println("RESPONSE : "+ response2);
		if(response2 == -1)
		{
			djyp.setLoginSuccess(false);	
		}else
		{
			djyp.setLoginSuccess(true);
			djyp.setCurrentUser(new User(response2, loginField.getText()));
		}
		
		synchronized (this) {
			djyp.doNotify();
		}
	

	}

	public DJYourParty getDjyp() {
		return djyp;
	}

	public void setDjyp(DJYourParty djyp) {
		this.djyp = djyp;
	}

}
