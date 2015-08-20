package legoline.ui;

import java.awt.event.*;

import javax.swing.*;

import legoline.hardware.Junction;
import legoline.hardware.ObjectDetector;

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
		

		final JLabel sideInSensorLabel = new JLabel("Side in:");
		add(sideInSensorLabel);
		final JLabel sideInSensorReading = new JLabel();
		add(sideInSensorReading);
		final JCheckBox sideInSensorPresent = new JCheckBox("pallet?");
		sideInSensorPresent.setEnabled(false);
		add(sideInSensorPresent);
		
		final JLabel mainInSensorLabel = new JLabel("Main in:");
		add(mainInSensorLabel);
		final JLabel mainInSensorReading = new JLabel();
		add(mainInSensorReading);
		final JCheckBox mainInSensorPresent = new JCheckBox("pallet?");
		mainInSensorPresent.setEnabled(false);
		add(mainInSensorPresent);

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
					boolean palletSideIn = junc.sideInSensor.hasObject();
					boolean palletMainIn = junc.mainInSensor.hasObject();
					boolean palletOut = junc.outSensor.hasObject();
					sideInSensorPresent.setSelected(palletSideIn);
					mainInSensorPresent.setSelected(palletMainIn);
					outSensorPresent.setSelected(palletOut);
					
					ObjectDetector.ReadingPair sideIn = junc.sideInSensor.getLastReading();
					ObjectDetector.ReadingPair mainIn = junc.sideInSensor.getLastReading();
					ObjectDetector.ReadingPair out = junc.outSensor.getLastReading();
					sideInSensorReading.setText(sideIn.lit + ", " + sideIn.unlit);
					mainInSensorReading.setText(mainIn.lit + ", " + mainIn.unlit);
					outSensorReading.setText(out.lit + ", " + out.unlit);
				}
				catch(InterruptedException ie) { }
			}
		}).start();
		
		
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
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(sideInSensorLabel)
					.addComponent(mainInSensorLabel)
					.addComponent(outSensorLabel)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(sideInSensorReading)
					.addComponent(mainInSensorReading)
					.addComponent(outSensorReading)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(sideInSensorPresent)
					.addComponent(mainInSensorPresent)
					.addComponent(outSensorPresent)
				)
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addComponent(button, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addComponent(side)
				.addComponent(main)
			)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(sideInSensorLabel)
					.addComponent(sideInSensorReading)
					.addComponent(sideInSensorPresent)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(mainInSensorLabel)
					.addComponent(mainInSensorReading)
					.addComponent(mainInSensorPresent)
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
