package legoline;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;

import legoline.hardware.*;
import legoline.ui.*;

public class Main extends JFrame {
	private static final long serialVersionUID = -5025003895547053468L;

	public static void main(String[] args) throws InterruptedException, IOException {
        new Main().setVisible(true);
    }
    
    public Main() throws InterruptedException, IOException {
    	line = LegoLine.createFromConnectedNXTs();
		initComponents();
		line.initialize();
    }
    
	final LegoLine line;
    
	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not load native theme");
		}

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Legoline control");
		
		final Container content = getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		for (Feeder feeder : line.feeders)
			content.add(new FeederPanel(feeder));
		for (Junction junction : line.junctions)
			content.add(new JunctionPanel(junction));
		pack();
	}
}