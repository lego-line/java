package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import hardware.Feeder;

public class FeederPanel extends JPanel {
	private static final long serialVersionUID = -7651630967299290601L;

	private Feeder feed;
	
	public FeederPanel(Feeder feeder) {
		this.feed = feeder;

		setLayout(new FlowLayout());
		
		setBorder(BorderFactory.createTitledBorder("Feeder"));
		{
			final JButton button = new JButton("Feed");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					try {
						feed.feed();
					} catch (InterruptedException e) { }
				}
			});
			button.setEnabled(feed != null);
			add(button);
		}
		{
			final JToggleButton button = new JToggleButton("Convey");
			button.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (button.getModel().isSelected()) {
                        feed.belt.forward();
                    } else {
                        feed.belt.flt();
                    }
                }
            });
			add(button);
		}
	}
	
}
