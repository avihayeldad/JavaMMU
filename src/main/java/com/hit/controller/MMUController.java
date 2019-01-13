package com.hit.controller;

import java.util.Observable;
import java.util.Observer;
import com.hit.driver.CLI;
import com.hit.model.MMUModel;
import com.hit.model.Model;
import com.hit.view.MMUView;
import com.hit.view.View;
import LogDataLinkedList.BaseLink;

public class MMUController implements Controller, Observer
{
	private Model m_model;
	private View m_view;
	
	public MMUController(Model i_model, View i_view)
	{
		m_model = i_model;
		m_view = i_view;
	}

	@Override
	public void update(Observable i_observable, Object i_arguments) 
	{
		if (i_observable instanceof View)
		{
			if (i_observable.getClass() == CLI.class)
			{
				if (((String[])i_arguments)[0] == "start")
				{
						Thread thread = new Thread()
						{
					        public void run() 
					        {
					        	m_model.start(new String[] {((String[])i_arguments)[1], ((String[])i_arguments)[2]});
					        }
						};
						thread.start();	
				}
				else if (((String[])i_arguments)[0] == "stop")
				{
					MMUModel.Pool.shutdownNow();
				}
			}
		}
		else if (i_observable instanceof Model)
		{
			BaseLink LogDataLinkedList = ((MMUModel)m_model).createLinkedListByLogFile();
			((MMUView)m_view).SetData(LogDataLinkedList);
			m_view.start();
		}	
	}
}
