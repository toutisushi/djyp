package listener;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.Party;
import model.Song;
import model.User;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import process.NotificationSender;
import process.NotificationWatcher;
import process.SoundJLayer;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertRed;

import dao.TableManager;
import eu.hansolo.custom.SteelCheckBox;
import eu.hansolo.tools.ColorDef;

public class CurrentMusic extends JFrame implements ActionListener {

	JButton joinParty, close, play, pause, open, addToPlaylist,
			removeFromPlaylist, next;
	JTable tableLibrary, tableQueue;
	JLabel onlineStatus;
	JPanel jp1, jp3, jp5RIGHT, jp5LEFT, jp5Container;
	SteelCheckBox toggle;
	Thread threadNotificationSender;
	JLabel label = new JLabel();
	static List<String[]> listArray1 = new ArrayList<String[]>();
	List<String[]> listArray2 = new ArrayList<String[]>();
	static SoundJLayer p;
	Object columnNames[] = { "Song Name", "Artist", "Album" };
	TableManager tableManager;
	Party currentParty;
	User currentUser;
	JTextArea jtextCurrentSong;

	private Image backgroundImage;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == joinParty)
			label.setText("Done!");
		else if (e.getSource() == close)
			System.exit(0);
		else if (e.getSource() == play) {
			// p.play((String) tableQueue.getValueAt(0, 4));
			// jtextCurrentSong.setText((String)tableQueue.getValueAt(0, 1));
			executeNextSong();
		} else if (e.getSource() == pause)
			p.stop();
		else if (e.getSource() == open) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("Scanning dir for MP3: "
						+ chooser.getSelectedFile().getAbsolutePath());
				listArray1 = new ArrayList<String[]>();
				scanFolderForMP3(chooser.getSelectedFile());
			}
			label.setText(chooser.getSelectedFile().getAbsolutePath());
			DefaultTableModel modelPlaylist = (DefaultTableModel) tableLibrary
					.getModel();
			// tableManager.clearSongTable();
			tableManager.clearSongParty(getCurrentParty().getId());
			for (int i = 0; i < listArray1.size(); i++) {
				tableManager.insertSongLibrary(listArray1.get(i)[0],
						listArray1.get(i)[1], listArray1.get(i)[2],
						listArray1.get(i)[3], getCurrentParty().getId());

			}
			modelPlaylist.setRowCount(0);

			populateTableLibraryFromDB();

			synchronizeSongs(tableManager.getSongLibrary(getCurrentParty()
					.getId()));
		} else if (e.getSource() == addToPlaylist) {
			DefaultTableModel model = (DefaultTableModel) tableQueue.getModel();
			model.insertRow(
					model.getRowCount(),
					new Object[] {
							tableLibrary.getValueAt(
									tableLibrary.getSelectedRow(), 0),
							tableLibrary.getValueAt(
									tableLibrary.getSelectedRow(), 1),
							tableLibrary.getValueAt(
									tableLibrary.getSelectedRow(), 2),
							tableLibrary.getValueAt(
									tableLibrary.getSelectedRow(), 3),
							tableLibrary.getValueAt(
									tableLibrary.getSelectedRow(), 4), 1 });

			sortTable(model);
		} else if (e.getSource() == next) {

			// DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
			// .getModel();
			// modelQueue.removeRow(0);
			// if (modelQueue.getRowCount() != 0) {
			// p.next((String) tableQueue.getValueAt(0, 4));
			// }else
			// {
			// p.stop();
			// }
//			p.stop();
			skipSong();

		} else if (e.getSource() == toggle) {

			if (toggle.getText().equals("ONLINE")) {
				System.out.println("GO OFFLINE");
				toggle.setForeground(Color.RED);
				toggle.setText("OFFLINE");
				// toggle.setBackground(new Color(255,255,255,0));
				threadNotificationSender.interrupt();

				// threadNotificationSender.currentThread().teinterrupt();

				threadNotificationSender.isInterrupted();
				toggle.repaint();
				jp5RIGHT.repaint();
				jp5Container.repaint();
				jp3.revalidate();
				jp3.repaint();
			} else {

				// toggle.setBackground(new Color(255,255,255,0));
				NotificationWatcher nw = new NotificationWatcher(this);
				threadNotificationSender = new Thread(nw);
				threadNotificationSender.start();
				System.out.println("GO ONLINE");
				toggle.setForeground(Color.GREEN);
				toggle.setText("ONLINE");
				// toggle.repaint();
				toggle.repaint();
				jp3.revalidate();
				jp3.repaint();
				jp5Container.repaint();
				jp5RIGHT.repaint();

				// SwingUtilities.invokeAndWait(arg0);
			}
		}
		repaint();
	}

	private void sortTable(DefaultTableModel model) {
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		tableQueue.setRowSorter(sorter);
		List<RowSorter.SortKey> sortkeys = new ArrayList<RowSorter.SortKey>();
		int indexColumnSorter = 5;
		sortkeys.add(new RowSorter.SortKey(indexColumnSorter,
				SortOrder.DESCENDING));
		sorter.setSortKeys(sortkeys);
		sorter.sort();
	}

	public CurrentMusic(User currentUser, Party currentParty) {
		com.apple.eawt.Application.getApplication().setDockIconImage(
				new ImageIcon(getClass().getResource("/DjYourParty.png"))
						.getImage());
		// com.apple.eawt.Application.getApplication().setDockIconImage( new
		// ImageIcon(getClass().getResource( "/testBG.png" )).getImage());
		// Image img = new ImageIcon("abc.png").getImage(); // your desired
		// image
		// Application app = Application.getApplication();
		// app.setDockIconImage(img);
		// setLocationRelativeTo(null);
		// p=new Sound
		this.currentParty = currentParty;
		this.currentUser = currentUser;
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/testBG.png")));
		try {
			backgroundImage = ImageIO.read(getClass()
					.getResource("/testBG.png"));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
		try {
			PlasticLookAndFeel.setTabStyle("Metal");
			PlasticLookAndFeel.setPlasticTheme(new DesertRed());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableManager = new TableManager();
		DefaultTableModel dtm = new DefaultTableModel(0, 0);
		String headerLibrary[] = new String[] { "Id", "Song Name", "Artist",
				"Album", "Path" };
		String headerQueue[] = new String[] { "Id", "Song Name", "Artist",
				"Album", "Path", "Vote" };
		dtm.setColumnIdentifiers(headerLibrary);

		tableLibrary = new JTable(dtm);

		tableLibrary.getColumnModel().getColumn(4).setMinWidth(0);
		tableLibrary.getColumnModel().getColumn(4).setMaxWidth(0);
		tableLibrary.getColumnModel().getColumn(0).setMinWidth(0);
		tableLibrary.getColumnModel().getColumn(0).setMaxWidth(0);

		tableLibrary.getTableHeader().setVisible(true);
		JScrollPane jspTableLibrary = new JScrollPane(tableLibrary);
		open = new JButton("Scan Folder");

		DefaultTableModel dtm2 = new DefaultTableModel(0, 0);
		dtm2.setColumnIdentifiers(headerQueue);

		tableQueue = new JTable(dtm2);
		// TableRowSorter<TableModel> sorter = new
		// TableRowSorter<TableModel>(dtm2);
		// sorter.setComparator(5, new Comparator<Object>() {
		//
		// @Override
		// public int compare(Object o1, Object o2) {
		// System.out.println(o1.toString() + " " + o2.toString());
		// return 1;
		// }
		// });
		// tableQueue.setRowSorter(sorter);
		tableQueue.getTableHeader().setVisible(true);
		tableQueue.getColumnModel().getColumn(4).setMinWidth(0);
		tableQueue.getColumnModel().getColumn(4).setMaxWidth(0);
		tableQueue.getColumnModel().getColumn(0).setMinWidth(0);
		tableQueue.getColumnModel().getColumn(0).setMaxWidth(0);
		JScrollPane jspTableQueue = new JScrollPane(tableQueue);

		FlowLayout cl = new FlowLayout();
		setLayout(cl);
		jp1 = new JPanel();
		jp1.setBackground(new Color(255, 255, 255, 0));
		JPanel jp2 = new JPanel();

		jp3 = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponents(g);

				// Draw the background image.
				g.drawImage(backgroundImage, 0, 0, this);
			}

		};
		JPanel jpTables = new JPanel();
		jpTables.setBackground(new Color(255, 255, 255, 0));
		play = new JButton("Play");

		play.setSize(5, 5);
		// add(play);
		pause = new JButton("Pause");
		joinParty = new JButton("Join a party");
		jp1.add(joinParty);
		jp1.add(play);
		jp1.add(pause);
		jp1.add(open);
		jp1.add(next = new JButton("Next"));
		FlowLayout gl = new FlowLayout(FlowLayout.RIGHT);
		jp1.setLayout(gl);
		close = new JButton("Close");
		jp1.add(close);

		jpTables.add(jspTableLibrary);
		jpTables.add(addToPlaylist = new JButton("Add To Playlist"));
		toggle = new SteelCheckBox();
		toggle.setBackground(new Color(255, 255, 255, 0));
		toggle.setText("OFFLINE");
		toggle.setRised(true);
		toggle.setColored(true);
		toggle.setSelectedColor(ColorDef.GREEN);
		toggle.setForeground(Color.RED);
		toggle.setAlignmentX(RIGHT_ALIGNMENT);
		toggle.addActionListener(this);
		JTextArea jtext = new JTextArea();
		jtext.setText("User : " + getCurrentUser().getName());
		jtext.setAlignmentX(LEFT_ALIGNMENT);
		jtext.setBackground(new Color(255, 255, 255, 0));
		JPanel jpButtons = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponents(g);

				// Draw the background image.
				g.drawImage(backgroundImage, 0, 0, this);
			}

		};

		JTextArea jtextParty = new JTextArea();
		jtextParty.setText("Party : " + getCurrentParty().getName());
		jtextParty.setAlignmentX(LEFT_ALIGNMENT);
		jtextParty.setBackground(new Color(255, 255, 255, 0));

		BoxLayout LayoutButtons = new BoxLayout(jpButtons, BoxLayout.X_AXIS);

		jp5RIGHT = new JPanel();
		jp5RIGHT.setLayout(new FlowLayout(FlowLayout.RIGHT));
		jp5RIGHT.add(toggle);
		jp5RIGHT.setAlignmentX(RIGHT_ALIGNMENT);
		jp5RIGHT.setVisible(true);
		jp5RIGHT.setBackground(new Color(255, 255, 255, 0));

		jp5LEFT = new JPanel();
		jp5LEFT.setAlignmentX(LEFT_ALIGNMENT);
		jp5LEFT.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp5LEFT.add(jtext);
		jp5LEFT.setBackground(new Color(255, 255, 255, 0));
		jp5LEFT.setVisible(true);
		jp5LEFT.add(jtextParty);
		jpButtons.setLayout(LayoutButtons);
		jpButtons.add(jp1);

		// jp1.add(toggle);

		jp5Container = new JPanel();
		jp5Container.setBackground(new Color(255, 255, 255, 0));
		BoxLayout rightCornerLayout = new BoxLayout(jp5Container,
				BoxLayout.X_AXIS);
		jp5Container.setLayout(rightCornerLayout);
		jp5Container.add(jp5LEFT);
		jp5Container.add(jp5RIGHT);
		jpButtons.add(jp5Container);

		jpTables.add(jspTableQueue);
		SongEventListener sel = new SongEventListener();
		play.addActionListener(sel);
		close.addActionListener(this);
		pause.addActionListener(this);
		play.addActionListener(this);
		open.addActionListener(this);
		addToPlaylist.addActionListener(this);
		next.addActionListener(this);

		jp2.add(label);
		BoxLayout glGlobal = new BoxLayout(jp3, BoxLayout.Y_AXIS);
		jp3.setLayout(glGlobal);
		// jp3.add(jp1);
		// jp3.add(jp5);
		jp3.add(jpButtons);

		jtextCurrentSong = new JTextArea();
		jtextCurrentSong.setText("Current Song : "
				+ getCurrentParty().getName());
		jtextCurrentSong.setAlignmentX(LEFT_ALIGNMENT);
		jtextCurrentSong.setBackground(new Color(255, 255, 255, 0));
		jtextCurrentSong.setVisible(true);
		jp3.add(jtextCurrentSong);

		jp3.add(jpTables);
		jp3.add(jp2);
		add(jp3);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		// setVisible(true);
		TableManager.initTables();
		populateTableLibraryFromDB();
		p = new SoundJLayer(this);
	}

	static private String[] getMP3FileInfos(File f) {
		FileInputStream fis = null;
		String[] result = new String[3];
		try {

			fis = new FileInputStream(f);
			InputStream input = new FileInputStream(f);
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();

			try {
				parser.parse(input, handler, metadata, parseCtx);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			input.close();
			fis.close();
			// List all metadata
			String[] metadataNames = metadata.names();

			for (String name : metadataNames) {
				if (name.toLowerCase().contains("album")) {
					Charset.forName("UTF-8").encode(metadata.get(name));
					result[2] = metadata.get(name).toString();
				}
				if (name.toLowerCase().contains("artist")) {
					result[1] = metadata.get(name);
				}
				if (name.toLowerCase().contains("title")) {
					result[0] = metadata.get(name);
				}

			}

			try {
				MP3File mp3file = new MP3File(f);
				ID3v1 tags = mp3file.getID3v1Tag();
				if (tags != null && tags.getAlbumTitle() != null
						&& !tags.getAlbumTitle().equals("")) {
					// Charset.forName("UTF-8").encode(tags.getAlbumTitle());
					// String newString = new
					// String(tags.getAlbumTitle().getBytes("UTF-16"), "UTF-16")
					result[2] = tags.getAlbumTitle();
				}
			} catch (TagException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (result[0] == null) {
				result[0] = f.getName().substring(0, f.getName().length() - 4);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	// public static void main(String argv[]) {
	// CurrentMusic cm = new CurrentMusic();
	// p = new SoundJLayer(cm);
	// }

	private String[][] addRowToTable(String songName, String Artiste,
			String album, String path) {
		String[] row = new String[4];
		row[0] = songName;

		row[1] = Artiste;
		row[2] = album;
		row[3] = path;
		listArray2.add(row);
		String[][] t = new String[3][listArray2.size()];
		for (int i = 0; i < listArray2.size(); i++) {
			t[0][i] = listArray2.get(i)[0];
			t[1][i] = listArray2.get(i)[1];
			t[2][i] = listArray2.get(i)[2];
			t[3][i] = listArray2.get(i)[2];
		}
		return t;
	}

	static void scanFolderForMP3(File dir) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				System.out.println(f.getName());
				if (f.getName().contains(".mp3")) {
					String[] infos = getMP3FileInfos(f);
					addRowTolistLibrary(infos[0], infos[1], infos[2],
							f.getAbsolutePath(), listArray1);
				}

			}
		}

	}

	static private String[][] addRowTolistLibrary(String songName,
			String Artiste, String album, String path, List<String[]> array) {
		String[] row = new String[4];
		row[0] = songName;

		row[1] = Artiste;
		row[2] = album;
		row[3] = path;

		array.add(row);
		String[][] t = new String[4][array.size()];
		for (int i = 0; i < array.size(); i++) {
			t[0][i] = array.get(i)[0];
			t[1][i] = array.get(i)[1];
			t[2][i] = array.get(i)[2];
			t[3][i] = array.get(i)[3];
		}
		return t;
	}

	static private String[][] removeFirstRowFromList(List<String[]> array) {
		array.remove(0);
		String[][] t = new String[4][array.size()];
		for (int i = 0; i < array.size(); i++) {
			t[0][i] = array.get(i)[0];
			t[1][i] = array.get(i)[1];
			t[2][i] = array.get(i)[2];
			t[3][i] = array.get(i)[3];
		}
		return t;
	}

	public void notifySongFinished() {
		DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
				.getModel();
		modelQueue.removeRow(0);
		if (modelQueue.getRowCount() != 0) {
			executeNextSong();
		}
	}

	private void executeNextSong() {
		DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
				.getModel();
		if (modelQueue.getRowCount() != 0) {
			p.play((String) tableQueue.getValueAt(0, 4));
			jtextCurrentSong.setText((String) tableQueue.getValueAt(0, 1));

			modelQueue.removeRow(0);
		} else {
			p.stop();
		}
	}

	private void skipSong() {
		
		DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
				.getModel();
		if (modelQueue.getRowCount() != 0) {
			p.stop();
			p.setPausedOnFrame(0);
			p.play((String) tableQueue.getValueAt(0, 4));
			jtextCurrentSong.setText((String) tableQueue.getValueAt(0, 1));
			modelQueue.removeRow(0);
			
		} else {
			p.stop();
		}
	}

	public void notifyVote(HashMap<Integer, Integer> votes) {
		DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
				.getModel();
		Vector v = modelQueue.getDataVector();
		for (int i = 0; i < v.size(); i++) {
			Object[] tab = ((Vector) v.get(i)).toArray();
			int songId;
			if (tab[0] instanceof String) {
				songId = Integer.parseInt((String) tab[0]);
			} else {
				songId = (Integer) tab[0];
			}

			if (votes.containsKey(songId)) {
				if (modelQueue.getValueAt(i, 5) == null) {
					modelQueue.setValueAt(1, i, 5);
				} else {
					int actualVote = (Integer) modelQueue.getValueAt(i, 5) + 1;

					modelQueue.setValueAt(actualVote++, i, 5);
				}
				votes.remove(songId);
			}
		}

		for (Integer songId : votes.keySet()) {
			System.out.println("NEW SONG TO ADD ");
			Song s = tableManager.getSongFromId(songId);
			if (s != null) {
				modelQueue.addRow(new Object[] { s.getId(), s.getName(),
						s.getArtist(), s.getAlbum(), s.getPath(), 1 });
			}
		}

		System.out.println("METHODE NOTIFY" + v);
		sortTable(modelQueue);
	}

	public void populateTableLibraryFromDB() {
		ResultSet rs = tableManager.getSongLibraryResultSet(currentParty
				.getId());

		DefaultTableModel modelLibrary = (DefaultTableModel) tableLibrary
				.getModel();
		try {
			while (rs.next()) {
				modelLibrary.addRow(new Object[] { rs.getInt(1),
						rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5) });
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ColumnSorter implements Comparator {
		int colIndex;

		ColumnSorter(int colIndex) {
			this.colIndex = colIndex;
		}

		public int compare(Object a, Object b) {
			Vector v1 = (Vector) a;
			Vector v2 = (Vector) b;
			Object o1 = v1.get(colIndex);
			Object o2 = v2.get(colIndex);
			Integer val1 = null;
			Integer val2 = null;

			if (o1 == null) {
				val1 = 0;
			} else {
				if (o1 instanceof String) {
					val1 = Integer.parseInt((String) o1);
				} else {
					val1 = (Integer) o1;

				}

			}
			if (o2 == null) {
				val2 = 0;
			} else {
				if (o2 instanceof String) {
					val2 = Integer.parseInt((String) o2);
				} else {
					val2 = (Integer) o2;

				}
			}

			return val1.compareTo(val2);

		}
	}

	public void synchronizeSongs(List<Song> songs) {
		NotificationSender nw = new NotificationSender(songs);
		Thread t = new Thread(nw);
		t.start();
	}

	public Party getCurrentParty() {
		return currentParty;
	}

	public void setCurrentParty(Party currentParty) {
		this.currentParty = currentParty;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}

}
