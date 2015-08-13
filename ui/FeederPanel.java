package ui;

import java.awt.event.*;

import javax.swing.*;
import hardware.Feeder;

public class FeederPanel extends JPanel {
	private static final long serialVersionUID = -7651630967299290601L;

	private Feeder feed;
	
	public FeederPanel(Feeder feeder) {
		this.feed = feeder;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createTitledBorder("Feeder"));
		final JButton button;
		{
			button = new JButton("Feed");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							try {
								feed.feed();
							} catch (InterruptedException e) { }
							return null;
						}
					}.execute();
				}
			});
			button.setEnabled(feed != null);
			add(button);
		}
		JPanel belt = new BeltPanel(feed.belt, null);
		add(belt);		

		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(button)
			.addComponent(belt)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addComponent(button, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(belt)
		);
	}
	
}
