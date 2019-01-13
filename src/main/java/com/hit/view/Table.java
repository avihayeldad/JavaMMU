package com.hit.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Table extends JPanel
{
	private static final long serialVersionUID = 1L;
	public JTable Table;
	
    public Table(Object[] i_Title, Object[][] i_Data)
    {
        JScrollPane ScrollPane;
        
        setLayout(null);
        setBounds(15, 15, 600, 107);    
        Table = new JTable(i_Data, i_Title);
        ScrollPane = new JScrollPane(Table);
        add(ScrollPane);
        ScrollPane.setBounds(0, 0, 600, 105);
        JTableHeader Theader = Table.getTableHeader();       
        Theader.setBackground(new Color(0, 102, 204)); // change the Background color
        Theader.setForeground(Color.WHITE); // change the Foreground       
        Theader.setFont(new Font("Arial", Font.BOLD, 14));; // font name style size
        ((DefaultTableCellRenderer)Theader.getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER); // center header text      
        Table.setFont(new Font("Arial", Font.PLAIN, 14));
    }
}
