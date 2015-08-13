package ui;

import java.awt.event.*;

import javax.swing.*;
import hardware.Junction;

public class JunctionPanel extends JPanel {
	private static final long serialVersionUID = -7651630967299290601L;

	private Junction junc;
	
	public JunctionPanel(Junction junction) {
		this.junc = junction;

		setBorder(BorderFactory.createTitledBorder("Junction"));
		
		
		final JButton button;
		{
			button = new JButton("Transfer");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							try {
								junc.transfer();
							} catch (InterruptedException e) { }
							return null;
						}
					}.execute();
				}
			});
			add(button);
		}
		JPanel side = new BeltPanel(junc.sideBelt, "sideline");
		JPanel main = new BeltPanel(junc.mainBelt, "mainline");
		
		add(side);
		add(main);
		
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(button)
			.addGroup(layout.createParallelGroup()
				.addComponent(side)
				.addComponent(main)
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addComponent(button, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addComponent(side)
				.addComponent(main)
			)
		);
	}
	
}
