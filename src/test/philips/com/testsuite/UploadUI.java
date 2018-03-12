package test.philips.com.testsuite;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class UploadUI {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UploadUI window = new UploadUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UploadUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 717, 489);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnUploadToTfs = new JButton("Browse");
		btnUploadToTfs.setBounds(578, 21, 113, 23);
		btnUploadToTfs.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
           JFrame newGameWindow = new JFrame("A new game!");
           newGameWindow.setVisible(true);
		   JFileChooser fileChooser = new JFileChooser();
		   fileChooser.showOpenDialog(newGameWindow);
		   fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		   int result = fileChooser.showOpenDialog(newGameWindow);
			if (result == JFileChooser.APPROVE_OPTION) {
			  File selectedFile = fileChooser.getSelectedFile();
			  System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			}
	        newGameWindow.add(fileChooser);
	        newGameWindow.pack();
	   }
		});	
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnUploadToTfs);
		
		textField = new JTextField();
		textField.setBounds(106, 22, 455, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Excel File Location");
		lblNewLabel.setBounds(10, 25, 97, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JButton button = new JButton("Upload To TFS");
		button.setBounds(238, 416, 197, 23);
		frame.getContentPane().add(button);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 368, 681, 14);
		frame.getContentPane().add(progressBar);
	}
}
