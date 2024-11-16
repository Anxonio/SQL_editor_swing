

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JTree;
import javax.sound.midi.VoiceStatus;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTable;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ArrayList;
public class Zavrsni extends JFrame {
	
	private final String JDBC_URL = "jdbc:sqlite:database2.db";
	private String text_retrieved;
	public int column_count = 3;
	public ResultSet resultSet;
	public ResultSetMetaData rsMetaData;
	public JTextArea textArea;
	JPanel treePanel;
	public int rowsAffected = 0;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	ArrayList<DefaultMutableTreeNode> parentNodes;
	ArrayList<DefaultMutableTreeNode> childNodes;
	JTable queryTable;
	DatabaseMetaData databaseMetaData;
	private int number_of_rows = 0;
	ResultSet table_nameSet;
	DefaultTableModel tableModel;
	ArrayList<JTree> trees;
	ArrayList<String> table_names_list;
	int tableCount = 0;
	Object [][] tableData = {
			{"Ante", 1, "Antic"},
			{"Mate", 2, "Matic"},
			{"Jure", 3, "Juric"}
			
	};
	String[] colHeadings;
	
	JOptionPane popuPane;
	
	int test_column_count;
	
	
	public String table_data[][];
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Zavrsni frame = new Zavrsni();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void getTableNames() {
		
		
		try (Connection connection = DriverManager.getConnection(JDBC_URL);
				Statement statement = connection.createStatement()){
			
			table_names_list = new ArrayList<String>();
			String []table_name_arr = {"TABLE"};
			try {
				databaseMetaData = connection.getMetaData();
				table_nameSet = databaseMetaData.getTables(null, null, "%", table_name_arr);
				while (table_nameSet.next()){
					table_names_list.add(table_nameSet.getString("TABLE_NAME"));
				
				}
				
				System.out.print(table_names_list);
				
				
				
			} catch (Exception e) {
				
			}
			
		} catch (Exception e) {
			
		}
		

	}
	
	
	

	
	
	public void fillSidePannel() {
		
		
		
		 
	    try (Connection connection = DriverManager.getConnection(JDBC_URL);
	    		Statement statement = connection.createStatement()){
	    	// metoda koja popunjava lijevi Jpannel sa hijerarhijsko Jtree strukturom gdje je ParentNode = TableName, a childNodes su stupci
			treePanel.removeAll();
			trees = new ArrayList<JTree>();
			parentNodes = new ArrayList<DefaultMutableTreeNode>();
			childNodes = new ArrayList<DefaultMutableTreeNode>();
			for(int i = 0;i<table_names_list.size();i++) {
				parentNodes.add(new DefaultMutableTreeNode(table_names_list.get(i)));
				try {
					String query = "SELECT * FROM " + table_names_list.get(i) + " LIMIT 1";
					resultSet = statement.executeQuery(query);
					rsMetaData = resultSet.getMetaData();
					
					int col_count = rsMetaData.getColumnCount();
					
					
					for(int j = 1;j<=col_count;j++) {
						String col_type = rsMetaData.getColumnTypeName(j);
						String col_name = rsMetaData.getColumnName(j);
						parentNodes.get(i).add(new DefaultMutableTreeNode(col_name + " " + col_type));
						
						
						
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
			
			
		for(int x = 0;x<parentNodes.size();x++) {
			trees.add(new JTree(parentNodes.get(x)));
			treePanel.add(new JScrollPane(trees.get(x)));
			
		}
		
		treePanel.revalidate();
		treePanel.repaint();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	
		
		
		
	}
	
	public void refreshTreePanel() {
		getTableNames();
		fillSidePannel();
	}


	
	

	public void runSql() {
	    text_retrieved = textArea.getText().trim().toLowerCase();
	    
	   
	    try (Connection connection = DriverManager.getConnection(JDBC_URL);
	         Statement statement = connection.createStatement()) {

	        if (text_retrieved.startsWith("select")) {
	           
	            try (ResultSet resultSet = statement.executeQuery(text_retrieved)) {
	                rsMetaData = resultSet.getMetaData();
	                test_column_count = rsMetaData.getColumnCount();
	                colHeadings = new String[test_column_count];

	                for (int i = 1; i <= test_column_count; i++) {
	                    colHeadings[i - 1] = rsMetaData.getColumnName(i);
	                }

	                tableModel = new DefaultTableModel() {
	                    @Override
	                    public boolean isCellEditable(int row, int column) {
	                        return false;
	                    }
	                };

	                tableModel.setColumnIdentifiers(colHeadings);

	                while (resultSet.next()) {
	                    Object[] rowData = new Object[test_column_count];
	                    for (int i = 1; i <= test_column_count; i++) {
	                        rowData[i - 1] = resultSet.getObject(i);
	                    }
	                    tableModel.addRow(rowData);
	                }

	                queryTable.setModel(tableModel);
	                refreshTreePanel();
	            }
	        } else {
	           	            rowsAffected = statement.executeUpdate(text_retrieved);
	            System.out.println("Rows affected: " + rowsAffected);

	            if (!connection.getAutoCommit()) {
	                connection.commit();
	            }

	            
	        }

	    } catch (SQLException e) {
	        System.out.print(e.getMessage());
	    }
	}


	/**
	 * Create the frame.
	 */
	public Zavrsni() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setSize(1000, 600);
		this.setTitle("My Sql Editor");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

	

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		popuPane = new JOptionPane();
		
		

		queryTable = new JTable(tableModel);
		
		
		 treePanel = new JPanel(new GridLayout(number_of_rows, 1));
		
		

		
		
	

		contentPane.add(new JScrollPane(treePanel), BorderLayout.WEST);

		JPanel northPanel = new JPanel(new BorderLayout());
		contentPane.add(northPanel, BorderLayout.NORTH);

		JButton btnRun = new JButton("Run");
		btnRun.setPreferredSize(new Dimension(80,30));
		northPanel.add(btnRun, BorderLayout.EAST);

		textArea = new JTextArea();


		contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
		contentPane.add(new JScrollPane(queryTable), BorderLayout.SOUTH);
		
		textArea.setFont(new Font("Dialog", Font.PLAIN,20));
		

		
		
		refreshTreePanel();
		
		
		btnRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				runSql();
				refreshTreePanel();
			
				
				
			}
		});
		
	
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
