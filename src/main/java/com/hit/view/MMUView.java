package com.hit.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import LogDataLinkedList.BaseLink;
import LogDataLinkedList.GetPagesLink;
import LogDataLinkedList.PageFaultLink;
import LogDataLinkedList.PageReplacementLink;
import LogDataLinkedList.ProcessNumbersLink;
import LogDataLinkedList.RamCapacityLink;

public class MMUView extends Observable implements View 
{
	private Thread m_PlayAllThread;
	private BaseLink m_LogDataLinkedList;
	private JFrame m_Frame;
	private JSlider m_SpeedSlider;
	private Table m_RamPanel;
	private Integer[] ProcessNumbersInRAM;
	private JButton m_PlayButton, m_PlayAllButton;
	private JList<String> m_ProcessesList;
	private JTextField m_PageFault, m_PageReplacement;
	private JTextField m_ProcessNumberPF, m_PageNumberPF;
	private JTextField m_ProcessNumberPR, m_pageToHD, m_pageToRAM;
	
	@Override
	public void start() 
	{	
		Thread thread = new Thread()
		{
	        public void run() 
	        {
	        	createAndShowGUI();
	        }
		};
		thread.start();	
	}
	
	public void SetData(BaseLink i_LogDataLinkedList)
	{
		m_LogDataLinkedList = i_LogDataLinkedList;
	}
	
	private void intitializeMainFrame()
	{
		m_Frame = new JFrame();
		m_Frame.setLocation(200,200);
		m_Frame.setSize(784,524);
		m_Frame.setLayout(null);
		m_Frame.setAlwaysOnTop(true); 
		m_Frame.setTitle("MMU Simulator");
		m_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_SpeedSlider = new JSlider(0, 1000);
		m_SpeedSlider.setBounds(148, 235, 200, 25);
		m_SpeedSlider.setBackground(Color.white);
		m_Frame.add(m_SpeedSlider);
	}
	
	private void createRamPanel(int i_ramCapacity)
	{
		ProcessNumbersInRAM = new Integer[i_ramCapacity];
		Integer[][] processDataInRAM = new Integer[5][i_ramCapacity];
		for (int i = 0; i < i_ramCapacity; ++i)
		{
			ProcessNumbersInRAM[i] = 0;
			for (int j = 0; j < 5; ++j)
			{
				processDataInRAM[j][i] = 0;
			}
		}	
		
		m_RamPanel  = new Table(ProcessNumbersInRAM, processDataInRAM);
		for (int i = 0; i < i_ramCapacity; ++i)
		{
			m_RamPanel.Table.getColumnModel().getColumn(i).setHeaderValue(" ");
			for (int j = 0; j < 5; ++j)
			{
				m_RamPanel.Table.setValueAt(null, j, i);
			}
		}
		
		m_RamPanel.setLocation(150, 90);
		m_Frame.add(m_RamPanel);
	}
	
	private void createPlayButtons() 
	{
		m_PlayButton = new JButton("Play");
		m_PlayAllButton = new JButton("Play All");
		m_Frame.add(m_PlayButton);
		m_Frame.add(m_PlayAllButton);
		m_PlayButton.setBounds(573, 213, 80, 30);
		m_PlayAllButton.setBounds(668, 213, 80, 30);
		m_PlayButton.addActionListener(new ActionListener()
	    {
		      public void actionPerformed(ActionEvent e)
		      {
		        playButtonPressed();
		      }
		});
		m_PlayAllButton.addActionListener(new ActionListener()
	    {
		      public void actionPerformed(ActionEvent e)
		      {
		        playAllButtonPressed();
		      }
		});	
	}
	
	private void playButtonPressed() 
	{
		if (m_LogDataLinkedList != null)
		{
			int currentProcessNumber = getCurrentProcess();
			boolean processIsSelected = processIsSelected(currentProcessNumber);
			
			switch (m_LogDataLinkedList.Name)
			{
			case "PF":
				casePF(currentProcessNumber);
				break;
			case "PR":
				casePR(currentProcessNumber);
				break;
			case "GP":
				caseGP(processIsSelected);
				break;
			default:
				m_LogDataLinkedList = m_LogDataLinkedList.Next;
				playButtonPressed();
				break;
			}
			
			m_LogDataLinkedList = m_LogDataLinkedList.Next;
		}
	}
	
	private void casePF(int i_processNumber)
	{
		Integer PageFaultAmount = Integer.valueOf(m_PageFault.getText()) + 1;
		m_PageFault.setText(PageFaultAmount.toString());
		m_ProcessNumberPF.setText(Integer.valueOf(i_processNumber + 1).toString());
		m_PageNumberPF.setText(Integer.valueOf(((PageFaultLink)m_LogDataLinkedList).PageFault).toString());
	}
	
	private void casePR(int i_processNumber)
	{
		Integer PageReplacementAmount = Integer.valueOf(m_PageReplacement.getText()) + 1;
		m_PageReplacement.setText(PageReplacementAmount.toString());
		m_ProcessNumberPR.setText(Integer.valueOf(i_processNumber + 1).toString());
		m_pageToHD.setText(Integer.valueOf(((PageReplacementLink)m_LogDataLinkedList).MoveToHD).toString());
		if (((PageReplacementLink)m_LogDataLinkedList).MoveToRAM != -1)
		{
			m_pageToRAM.setText(Integer.valueOf(((PageReplacementLink)m_LogDataLinkedList).MoveToRAM).toString());
		}
		else
		{
			m_pageToRAM.setText(" ");
		}
	}
	
	private void caseGP(boolean i_processIsSelected)
	{
		int locationInRAM = getLocationInRAM(m_LogDataLinkedList);
		ProcessNumbersInRAM[locationInRAM] = ((GetPagesLink)m_LogDataLinkedList).PageID;
		if (i_processIsSelected)
		{
			m_RamPanel.Table.getColumnModel().getColumn(locationInRAM).setHeaderValue(((GetPagesLink)m_LogDataLinkedList).PageID);
			m_RamPanel.repaint();
			for (int i = 0; i < 5; ++i)
			{
				if (((GetPagesLink)m_LogDataLinkedList).Data.length > i)
				{
					m_RamPanel.Table.setValueAt(((GetPagesLink)m_LogDataLinkedList).Data[i], i, locationInRAM);
				}
				else
				{
					m_RamPanel.Table.setValueAt(0, i, locationInRAM);
				}
			}	
		}
		else
		{
			m_RamPanel.Table.getColumnModel().getColumn(locationInRAM).setHeaderValue(null);
			m_RamPanel.repaint();
			for (int i = 0; i < 5; ++i)
			{
				m_RamPanel.Table.setValueAt(null, i, locationInRAM);
			}
		}
	}
	
	private int getCurrentProcess() 
	{
		int processNumber = -1;
		BaseLink listPointer = m_LogDataLinkedList;
		
		while (listPointer != null)
		{
			if (listPointer.Name.equals("GP"))
			{
				processNumber = ((GetPagesLink)listPointer).Process;
				break;
			}
	
			listPointer = listPointer.Next;
		}
		
		return processNumber;
	}

	private boolean processIsSelected(int i_processNumber)
	{
		boolean valueToReturn = false;
		int[] selectedProcesses = m_ProcessesList.getSelectedIndices();
		
		for(int i = 0; i < selectedProcesses.length; ++i)
		{
			if(selectedProcesses[i] == i_processNumber)
			{
				valueToReturn = true;
				break;
			}
		}
		
		return valueToReturn;
	}
	
	private int getLocationInRAM(BaseLink i_DataPointer) 
	{
		int indexToReturn = -1;
		int numberToSearch = -1;
		
		if (i_DataPointer.Prev.Name.equals("PR"))
		{
			numberToSearch = ((PageReplacementLink)i_DataPointer.Prev).MoveToHD;
		}
		else if (i_DataPointer.Prev.Name.equals("GP"))
		{
			numberToSearch = ((GetPagesLink)i_DataPointer).PageID;
		}
		else if (i_DataPointer.Prev.Name.equals("PF"))
		{
			numberToSearch = 0;
		}
		else
		{
			numberToSearch = getLocationInRAM(i_DataPointer.Prev);
		}

		for(int i = 0; i < ProcessNumbersInRAM.length; ++i)
		{
			if (ProcessNumbersInRAM[i] == numberToSearch)
			{
				indexToReturn = i;
				break;
			}
		}
		
		return indexToReturn;
	}

	@SuppressWarnings("deprecation")
	private void playAllButtonPressed() 
	{
		if (m_PlayAllThread == null)
		{
			m_PlayAllThread = new Thread()
			{
		        public void run() 
		        {
		    		while(m_LogDataLinkedList != null)
		    		{
		    			playButtonPressed();
		    			try {
							Thread.sleep(m_SpeedSlider.getValue());
						} catch (InterruptedException e) {
						}
		    		}
		    		
		    		if (m_LogDataLinkedList == null)
		    		{
		    			m_PlayAllButton.setText("Play All");
		    			m_PlayAllButton.setEnabled(false);
		    			m_PlayButton.setEnabled(false);
		    		}
		        }
			};
			
			m_PlayAllThread.start();
			m_PlayAllButton.setText("Pause");
		}
		else
		{
			m_PlayAllThread.stop();
			m_PlayAllThread = null;	
			m_PlayAllButton.setText("Play All");
		}
	}
	
	private void createProcessesList(int i_processNumbers) 
	{	
		DefaultListModel<String> listModel = new DefaultListModel<>(); 
		
		for(int i = 1; i <= i_processNumbers; ++i)
		{
			listModel.addElement("Process " + i); 
		}
		
		m_ProcessesList = new JList<>(listModel);
		m_ProcessesList.setAutoscrolls(true);
		m_ProcessesList.setFont(new Font("Arial", Font.PLAIN, 14));
		m_ProcessesList.setBackground(Color.white);
		m_ProcessesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane ScrollPane = new JScrollPane(m_ProcessesList);
		ScrollPane.setBounds(17, 90, 100, 140);
		m_Frame.add(ScrollPane);
	}
	
	private void createPagesCounters() 
	{
		m_PageFault = new JTextField("0"); 
		m_PageReplacement = new JTextField("0");	
		m_PageFault.setBounds(238, 391, 40, 25);
		m_PageReplacement.setBounds(663, 424, 40, 25);
		m_PageFault.setFont(new Font("Arial", Font.PLAIN, 14));
		m_PageReplacement.setFont(new Font("Arial", Font.PLAIN, 14));
		m_ProcessNumberPF = new JTextField();
		m_PageNumberPF = new JTextField();
		m_ProcessNumberPR = new JTextField();
		m_pageToHD = new JTextField();
		m_pageToRAM= new JTextField();	
		m_ProcessNumberPF.setBounds(172, 326, 40, 25);
		m_PageNumberPF.setBounds(159, 357, 40, 25);
		m_ProcessNumberPR.setBounds(545, 327, 40, 25);
		m_pageToHD.setBounds(560, 358, 40, 25);
		m_pageToRAM.setBounds(525, 391, 40, 25);		
		m_ProcessNumberPF.setFont(new Font("Arial", Font.PLAIN, 14));
		m_PageNumberPF.setFont(new Font("Arial", Font.PLAIN, 14));
		m_ProcessNumberPR.setFont(new Font("Arial", Font.PLAIN, 14));
		m_pageToHD.setFont(new Font("Arial", Font.PLAIN, 14));
		m_pageToRAM.setFont(new Font("Arial", Font.PLAIN, 14));
		m_Frame.add(m_PageFault);
		m_Frame.add(m_PageReplacement);	
		m_Frame.add(m_ProcessNumberPF);
		m_Frame.add(m_PageNumberPF);
		m_Frame.add(m_ProcessNumberPR);
		m_Frame.add(m_pageToHD);
		m_Frame.add(m_pageToRAM);
	}
	
	public void createAndShowGUI()
	{
		int ramCapacity = ((RamCapacityLink)m_LogDataLinkedList.Next).Capacity;
		int processNumbers = ((ProcessNumbersLink)m_LogDataLinkedList.Next.Next).ProcessNumbers;
		
		m_LogDataLinkedList = m_LogDataLinkedList.Next.Next.Next;
		intitializeMainFrame();
		createRamPanel(ramCapacity);
		createPlayButtons();
		createProcessesList(processNumbers);
		createPagesCounters();
		JLabel background = new JLabel(new ImageIcon("src/Background.png"));
		background.setBounds(0, 0, 784, 524);
		m_Frame.add(background);
		m_Frame.setVisible(true);
	}
}
