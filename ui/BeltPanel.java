package ui;

import javax.swing.*;
import javax.swing.event.*;

import hardware.Belt;

public class BeltPanel extends JPanel {
	private static final long serialVersionUID = -4081099368456253292L;


	public BeltPanel(final Belt belt, String name) {
		{
			final JSlider slider = new JSlider(0, (int) belt.getMaxSpeed());
			slider.setValue((int) belt.getSpeed());
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					belt.setSpeed(slider.getValue());
					System.out.println("Set speed");
				}
			});
			add(slider);
		}
		{
			final JToggleButton button = new JToggleButton(
				"Convey" + (name == null ? "" : " " + name)
			);
			button.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (button.getModel().isSelected()) {
                        belt.enable();
                    } else {
                        belt.disable();
                    }
                }
            });
			add(button);
		}
	}
}
