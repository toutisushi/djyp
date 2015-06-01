package listener;

import java.awt.event.WindowEvent;

import model.Party;
import model.User;
import process.SoundJLayer;

 class WaitingLock
{
	
}

public class DJYourParty {
	static public Boolean deployMode = Boolean.FALSE;
	static SoundJLayer p;
	private User currentUser;
	private Party currentParty;
	
	private boolean loginSuccess;

	private boolean choiceMade;
	private boolean creation;
	WaitingLock waitingLock = new WaitingLock();
	
	public WaitingLock getWaitingLock() {
		return waitingLock;
	}
	public void setWaitingLock(WaitingLock waitingLock) {
		this.waitingLock = waitingLock;
	}
	public boolean isLoginSuccess() {
		return loginSuccess;
	}
	public void setLoginSuccess(boolean loginSuccess) {
		this.loginSuccess = loginSuccess;
	}
	public boolean isChoiceMade() {
		return choiceMade;
	}
	public void setChoiceMade(boolean choiceMade) {
		this.choiceMade = choiceMade;
	}
	public boolean isCreation() {
		return creation;
	}
	public void setCreation(boolean creation) {
		this.creation = creation;
	}
	
	
	public void doWait()
	{
		try {
			synchronized (waitingLock) {
				waitingLock.wait();
			}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void doNotify()
	{
		synchronized (waitingLock) {
			waitingLock.notify();
		}
	}	
	

	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	public Party getCurrentParty() {
		return currentParty;
	}
	public void setCurrentParty(Party currentParty) {
		this.currentParty = currentParty;
	}
	public static void main(String[] args) {
		
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "DJYourParty");
		DJYourParty djyp = new DJYourParty();
		SignupScreen ss = new SignupScreen(djyp);
		ChooseActionScreen cas = new ChooseActionScreen(djyp);
		while(!djyp.isLoginSuccess())
		{
		ss.setLocationRelativeTo(null);
		ss.setVisible(true);
		djyp.doWait();	
		}

		ss.dispatchEvent(new WindowEvent(ss, WindowEvent.WINDOW_CLOSING));
		while (!djyp.isChoiceMade()) {
			cas.setLocationRelativeTo(null);
			cas.setVisible(true);
			djyp.doWait();
		}
		
		
		cas.dispatchEvent(new WindowEvent(cas, WindowEvent.WINDOW_CLOSING));
		
		
		if (djyp.isCreation()) {

			CreatePartyScreen cps = new CreatePartyScreen(djyp);
			cps.setLocationRelativeTo(null);
			cps.setVisible(true);
			djyp.doWait();
			cps.dispatchEvent(new WindowEvent(cps, WindowEvent.WINDOW_CLOSING));	
		}
		else
		{
			PartyScreen ps = new PartyScreen(djyp);
			ps.setLocationRelativeTo(null);
			ps.setVisible(true);
			djyp.doWait();
			ps.dispatchEvent(new WindowEvent(ss, WindowEvent.WINDOW_CLOSING));	
		}
		
		
		CurrentMusic cm = new CurrentMusic(djyp.getCurrentUser(),djyp.getCurrentParty());
		
		
		cm.setLocationRelativeTo(null);
		cm.setVisible(true);
		
	}
	
}


