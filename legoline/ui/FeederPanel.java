package legoline.ui;

import java.awt.event.*;

import javax.swing.*;

import legoline.hardware.Feeder;
import legoline.hardware.ObjectDetector;

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
		
		final JLabel inSensorLabel = new JLabel("In:");
		add(inSensorLabel);
		final JLabel inSensorReading = new JLabel();
		add(inSensorReading);
		final JCheckBox inSensorPresent = new JCheckBox("pallet?");
		inSensorPresent.setEnabled(false);
		add(inSensorPresent);

		final JLabel outSensorLabel = new JLabel("Out:");
		add(outSensorLabel);
		final JLabel outSensorReading = new JLabel();
		add(outSensorReading);
		final JCheckBox outSensorPresent = new JCheckBox("pallet?");
		outSensorPresent.setEnabled(false);
		add(outSensorPresent);
		
		new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					boolean palletIn = feed.inSensor.hasObject();
					boolean palletOut = feed.outSensor.hasObject();
					inSensorPresent.setSelected(palletIn);
					outSensorPresent.setSelected(palletOut);
					
					ObjectDetector.ReadingPair in = feed.inSensor.getLastReading();
					ObjectDetector.ReadingPair out = feed.outSensor.getLastReading();
					inSensorReading.setText(in.lit + ", " + in.unlit);
					outSensorReading.setText(out.lit + ", " + out.unlit);
				}
				catch(InterruptedException ie) { }
			}
		}).start();
		
		JPanel belt = new BeltPanel(feed.belt, null);
		add(belt);		

		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(button)
			.addComponent(belt)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(inSensorLabel)
					.addComponent(outSensorLabel)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(inSensorReading)
					.addComponent(outSensorReading)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(inSensorPresent)
					.addComponent(outSensorPresent)
				)
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addComponent(button, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(belt)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(inSensorLabel)
					.addComponent(inSensorReading)
					.addComponent(inSensorPresent)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(outSensorLabel)
					.addComponent(outSensorReading)
					.addComponent(outSensorPresent)
				)
			)
		);
	}
	
}
