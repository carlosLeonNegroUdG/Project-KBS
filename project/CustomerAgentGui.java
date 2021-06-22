package examples.kbb.project;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
  @author Giovanni Caire - TILAB
 */
class CustomerAgentGui extends JFrame {	
	private CustomerAgent myAgent;
	
	private JTextField partNumberField, nameField;
	
	CustomerAgentGui(CustomerAgent a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		p.add(new JLabel("Part-number product:"));
		partNumberField = new JTextField(15);
		p.add(partNumberField);
		p.add(new JLabel("Name product: "));
		nameField = new JTextField(15);
		p.add(nameField);
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Ordenar");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String partNumber = partNumberField.getText().trim().toString();
					String name = nameField.getText().trim().toString();
					myAgent.buscando(Integer.parseInt(partNumber),name);
					partNumberField.setText("");
					nameField.setText("");
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(CustomerAgentGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	

	public JTextField obtenerTextoPartNumber(){
		return partNumberField;
	}

	public JTextField obtenerTextoName(){
		return nameField;
	}
}
