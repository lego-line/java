package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

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
		{
			final JToggleButton button = new JToggleButton("Convey sideline");
			button.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (button.getModel().isSelected()) {
                        junc.sideBelt.enable();
                    } else {
                        junc.sideBelt.disable();
                    }
                }
            });
			add(button);
		}
		{
			final JToggleButton button = new JToggleButton("Convey mainline");
			button.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (button.getModel().isSelected()) {
                        junc.mainBelt.enable();
                    } else {
                        junc.mainBelt.disable();
                    }
                }
            });
			add(button);
		}
	}
	
}
