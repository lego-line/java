package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import hardware.Belt;

public class BeltPanel extends JPanel {
	private static final long serialVersionUID = -4081099368456253292L;


	public BeltPanel(final Belt belt, String name) {

		final JButton stepButton;
		{
			stepButton = new JButton("Forward one pallet");
			stepButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new SwingWorker<Void, Void>() {
						protected Void doInBackground() throws Exception {
							belt.advance(8);
							return null;
						}
					}.execute();
				}
			});
			add(stepButton);
		}
		
		final JSlider slider;
		{
			final float scaleFactor = 2f;
			final int sliderMax = (int) (belt.getFullyChargedMaxSpeed()*scaleFactor);
			slider = new JSlider(0, sliderMax);
			slider.setMinimum(0);
			slider.setMaximum(sliderMax);
			slider.setValue((int) (belt.getSpeed() * scaleFactor));
			slider.setMinorTickSpacing(1);
		    slider.setMajorTickSpacing(4);
		    slider.setPaintTicks(true);
		    slider.setSnapToTicks(true);
		    {
			    Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
			    for(int i = 0; i < sliderMax; i += 4)
			    	labels.put(i, new JLabel(String.format("%.0f", i / scaleFactor)));
			    slider.setLabelTable(labels);
		    }
	        slider.setPaintLabels(true);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					belt.setSpeed(slider.getValue() / 2f);
					System.out.println("Set speed");
				}
			});
			add(slider);
		}
		final JToggleButton enableButton;
		{
			enableButton = new JToggleButton("Run");
			enableButton.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (enableButton.getModel().isSelected()) {
                        belt.enable();
                        stepButton.setEnabled(false);
                    } else {
                        belt.disable();
                        stepButton.setEnabled(true);
                    }
                }
            });
			add(enableButton);
		}
		final JLabel headerLabel = new JLabel((name == null ? "" : name + " ") + "conveyor:");
		
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addComponent(headerLabel)
			.addGroup(layout.createSequentialGroup()
				.addComponent(slider)
				.addGroup(layout.createParallelGroup()
					.addComponent(enableButton)
					.addComponent(stepButton)
				)
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(headerLabel)
			.addGroup(layout.createParallelGroup()
				.addComponent(slider)
				.addGroup(layout.createSequentialGroup()
					.addComponent(enableButton)
					.addComponent(stepButton)
				)
			)
		);
	}
}
