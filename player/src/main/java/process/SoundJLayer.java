package process;

import java.io.IOException;
import java.net.MalformedURLException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;
import listener.CurrentMusic;


public class SoundJLayer extends PlaybackListener implements Runnable
{
    private String filePath;
    private AdvancedPlayer player;
    private Thread playerThread;
	private int pausedOnFrame = 0;
	private CurrentMusic cm;

    public SoundJLayer(CurrentMusic cm)
    {
        this.cm = cm;
    }

    
    
    public void stop()
    {
    	player.stop();
    	
    }

	public void next(String path) {
		if (player == null) {
			String urlAsString = "file:///"

			+ "/" + path;
			try {
				this.player = new AdvancedPlayer(
						new java.net.URL(urlAsString).openStream(),
						javazoom.jl.player.FactoryRegistry.systemRegistry()
								.createAudioDevice());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			player.stop();
			setPausedOnFrame(0);
			play(path);
		}

	}
    
    
    public int getPausedOnFrame() {
		return pausedOnFrame;
	}



	public void setPausedOnFrame(int pausedOnFrame) {
		this.pausedOnFrame = pausedOnFrame;
	}



	public void play(String path)
    {
        try
        {
            String urlAsString = 
                "file:///" 
                + "/" 
                + path;
            this.player = new AdvancedPlayer
            (
                new java.net.URL(urlAsString).openStream(),
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
            );

            this.player.setPlayBackListener(this);

            this.playerThread = new Thread(this, "AudioPlayerThread");

            this.playerThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // PlaybackListener members

    public void playbackStarted(PlaybackEvent playbackEvent)
    {
        System.out.println("playbackStarted");
    }

    public void playbackFinished(PlaybackEvent playbackEvent)
    {
    	if (player.isComplete()){
        System.out.println("playbackEnded");
        cm.notifySongFinished();
        
        System.out.println("Chanson terminée");
    	}
    	else
    	{
    		pausedOnFrame = playbackEvent.getFrame();
            System.out.println("Chanson En cours mise en pause");
    	}
//        playbackEvent.getSource().
        
    }    

    
    // Runnable members

    public void run()
    {
        try
        {
        	if (pausedOnFrame == 0){
            this.player.play();
        	}else
        	{
        		player.play(pausedOnFrame, Integer.MAX_VALUE);
        	}
        }
        catch (javazoom.jl.decoder.JavaLayerException ex)
        {
            ex.printStackTrace();
        }

    }
}
