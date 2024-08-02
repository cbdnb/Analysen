/**
 *
 */
package utils.DB;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;

/**
 * @author baumann
 *
 */
public class GUI {

	private JFrame frame;
	JLabel lblTitel;
	JPanel panel;
	JLabel lblQuellen;
	JButton btnErzeuge;
	JButton btnAbbruch;
	JLabel lblTabellen;
	JScrollPane scrollPaneSources;
	JList<String> listSources;
	JScrollPane scrollPaneTables;
	JList<TableGenerator> listTables;

	/**
	 * Launch the application.
	 *
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 727, 455);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lblTitel = new JLabel("Erzeuge Datenbanken");
		lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblTitel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblTitel, BorderLayout.NORTH);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		lblQuellen = new JLabel("Vorhandene Datenquellen:");
		lblQuellen.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQuellen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblQuellen.setBounds(24, 74, 162, 39);
		panel.add(lblQuellen);

		scrollPaneSources = new JScrollPane();
		scrollPaneSources.setBounds(208, 19, 340, 151);
		panel.add(scrollPaneSources);
		listSources = new JList<String>();
		final DefaultListModel<String> sourcesModel = new DefaultListModel<>();
		listSources.setModel(sourcesModel);
		for (final String source : GND_DB_UTIL.getAvailableSources()) {
			sourcesModel.addElement(source);
		}
		scrollPaneSources.setViewportView(listSources);

		lblTabellen = new JLabel("Implementierte Tabellen:");
		lblTabellen.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTabellen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTabellen.setBounds(39, 225, 147, 27);
		panel.add(lblTabellen);

		scrollPaneTables = new JScrollPane();
		scrollPaneTables.setBounds(208, 181, 340, 130);
		panel.add(scrollPaneTables);
		listTables = new JList<>();
		final DefaultListModel<TableGenerator> tablesModel = new DefaultListModel<>();
		listTables.setModel(tablesModel);
		for (final TableGenerator tableGenerator : GND_DB_UTIL.IMPLEMENTED_TABLES) {
			tablesModel.addElement(tableGenerator);
		}
		scrollPaneTables.setViewportView(listTables);

		btnErzeuge = new JButton("Erzeuge DB");
		btnErzeuge.addActionListener(new BtnErzeugeActionListener());
		btnErzeuge.setBounds(146, 331, 124, 23);
		panel.add(btnErzeuge);

		btnAbbruch = new JButton("Abbruch");
		btnAbbruch.addActionListener(a -> System.exit(0));
		btnAbbruch.setBounds(407, 331, 89, 23);
		panel.add(btnAbbruch);
	}

	private class BtnErzeugeActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final List<String> sources = listSources.getSelectedValuesList();
			final List<TableGenerator> tables = listTables
					.getSelectedValuesList();
			GND_DB_UTIL.createTables(sources, tables);
			JOptionPane.showMessageDialog(null, "fertig");
		}
	}
}
