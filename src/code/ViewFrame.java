package code;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.jnativehook.keyboard.NativeKeyEvent;

import code.interfaces.DatabaseInterface;

public class ViewFrame extends JFrame {

	public JTextField fileName;
	public JFrame cur;
	public JTextField dbString;

	public JLabel load;
	public JButton browse;
	public JButton run;

	public JButton next;
	public JButton nextDynamic;
	public JLabel curLabel;
	public JTextPane txt;

	public JPanel one;
	public JPanel two;
	public JPanel three;
	
	private ViewFrame curView;

	public static final String CUR_START = "Current Page: ";

	public GlobalKeyListener keyListener;

	public void setKeyListener(GlobalKeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public ViewFrame() {
		super("Accessibility Checker");
		cur = this;
		curView = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		one = panel;
		add(panel);
		panel.add(load = new JLabel("Load Config File: "));
		panel.add(fileName = new JTextField(12));


		browse = new JButton("...");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Browse the folder to process");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileName.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		panel.add(browse);

		panel = new JPanel();
		two = panel;
		add(panel);
		//panel.add(new JLabel("DB Connection String: "));
		panel.add(dbString = new JTextField(12));
		dbString.setVisible(false);
		if (new File("db_store").exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File("db_store")));
				dbString.setText(br.readLine());
				br.close();
			} catch (Exception e) {  }
		}

		run = new JButton("Run");
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!RunChecks.driverExists) {
					JOptionPane.showMessageDialog(cur, "Could not find chromedriver! Expected 'chromedriver.exe' to be found in same directory as program being run from. Please download from https://chromedriver.storage.googleapis.com/2.45/chromedriver_win32.zip", "Big oh noes!", JOptionPane.ERROR_MESSAGE);
				} else {
					switchToRun();
				}
			}
		});
		panel = new JPanel();
		three = panel;
		add(panel);
		panel.add(run);

		pack();
		setVisible(true);
	}

	public void displayFinish() {
		JOptionPane.showMessageDialog(cur, "All checks are now complete!", "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void switchToRun() {
		try {
			RuntimeConfig cf = RunChecks.getConfigFromFile(fileName.getText());
			db = null;
			if (dbString.getText().trim().length() != 0) {
				db = new DatabaseInterface(dbString.getText());
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("db_store")));
				bw.write(dbString.getText());
				bw.close();
			}

			this.remove(one);
			this.remove(two);
			this.remove(three);

			JPanel panel = new JPanel();
			add(panel);
			panel.add(curLabel = new JLabel(CUR_START));

			panel = new JPanel();
			add(panel);
			panel.add(next = new JButton("Login Complete (F2)"));
			next.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					keyListener.lastKey = NativeKeyEvent.VC_F2;
				}
			});

			panel.add(nextDynamic = new JButton("Run Dynamic Check (F4)"));
			next.setEnabled(false);
			nextDynamic.setEnabled(false);
			nextDynamic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					keyListener.lastKey = NativeKeyEvent.VC_F4;
				}
			});

			panel = new JPanel();
			add(panel);
			txt = new JTextPane();
			txt.setPreferredSize(new Dimension(300, 300));
			txt.setEditable(false);
			JScrollPane jsp = new JScrollPane(txt);
			panel.add(jsp, BorderLayout.CENTER);

			pack();
			repaint();
			(new Thread(new Runnable() {
				public void run() {
					try {
						CheckList cl = new CheckList();
						cl.setGUI(curView);
						if (db != null) {
							cl.runChecksAtURLs(cf, db);
						} else {
							cl.runChecksAtURLs(cf);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(cur, e.toString(), "Whoopsie!", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			})).start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(cur, e.toString(), "Whoopsie!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private DatabaseInterface db;

	public void log(String line) {
		txt.setText(txt.getText() + "\r\n" + line);
	}
}
