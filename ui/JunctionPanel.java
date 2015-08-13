package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import hardware.Junction;

public class JunctionPanel extends JPanel {
	private static final long serialVersionUID = -7651630967299290601L;

	private Junction junc;
	
	public JunctionPanel(Junction junction) {
		this.junc = junction;

		setBorder(BorderFactory.createTitledBorder("Junction"));
		setLayout(new FlowLayout());
		{
			final JButton button = new JButton("Transfer");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					try {
						junc.transfer();
					} catch (InterruptedException e) { }
				}
			});
			add(button);
		}
		add(new BeltPanel(junc.sideBelt, "sideline"));
		add(new BeltPanel(junc.mainBelt, "mainline"));
	}
	
}
