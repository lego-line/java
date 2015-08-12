import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import lejos.nxt.Motor;
import lejos.nxt.remote.*;
import lejos.pc.comm.*;

public class Main extends JFrame {
    public static void main(String[] args) throws InterruptedException {
        new Main().setVisible(true);
    }
    
    public Main() throws InterruptedException {
    	findBricks();
		initComponents();
    }
    
	Feeder feed;
	Junction junc;
    
	public void findBricks() throws InterruptedException  {		
		for (NXTInfo nxtInfo : new NXTConnectionManager().search()) {
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				comm.open(nxtInfo);
			} catch (NXTCommException e) {
				continue;
			}
			NXTCommand command = new NXTCommand(comm);
			
			if (nxtInfo.name.startsWith("Feeder")) {
				feed = new Feeder(command);
			}
			else if (nxtInfo.name.startsWith("Merge")) {
				junc = new Junction(command);
			}
			else {
				try {
					System.out.println("Renaming brick");
					command.setFriendlyName("Merge");
					command.disconnect();
				} catch (IOException e) { }
			}
		}		
	}
	
	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			System.err.println("Could not load native theme");
		}

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Feeder control");
		
		JButton button = new JButton("Feed");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(feed != null) {
					feed.feed();
				}
			}
		});
		button.setEnabled(feed != null);
        getContentPane().add(button);
		pack();
	}
}