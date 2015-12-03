package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.Utilities;

public class Controller extends JFrame implements MessageListener{

	//view and model
	private View view;
	private Model model;
	
	//already have '@' for the first character
	private String name;
	
	//action listener for showing the list of subscriptions
	private ActionListener btnViewSubsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try{
				String msg="";
				String[] subscriptions = model.getSubscriptions();
				if (subscriptions.length == 0){
					msg="You have no subscriptions!";
				} else {
					for (int i = 0 ; i < subscriptions.length ; i++){
						msg+=subscriptions[i]+"  ";
					}
				}
				JOptionPane.showMessageDialog(null, msg, "Subscriptions", JOptionPane.PLAIN_MESSAGE);
			}catch(JMSException ex){
				ex.printStackTrace();
				view.setStatus("There's a problem with message broker!");
			}
		}
	};
	
	//action listener for publishing a message
	private ActionListener btnPubListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			view.setStatus("sending notification...");
			String content = view.getMessageContent();
			String[] topics = Utilities.StringToTopicsArray(view.getTopicsToPublish().toLowerCase());
			
			//check the content and the topics
			if (topics == null){
				view.setStatus("Topics must be separated by (,) and no spaces!");
				return;
			}
			
			if (content.isEmpty()){
				view.setStatus("Cannot send empty message!");
				return;
			}
			
			//send the notification
			int stat = model.sendNotification(name, content, topics);
			if (stat == Model.PUBLISH_SUCCESS){
				view.addEntry(name, content, view.getTopicsToPublish());
				view.setStatus("notification was successfully sent!");
				view.clearPublisherField();
			} else {
				view.setStatus("Failed to send message!");
			}
		}
	};
	
	//action listener for subscribing to a topic/user
	private ActionListener btnSubListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String topic = view.getTopicToSubscribe().toLowerCase();
			//check textfield first
			if (topic.isEmpty()){
				view.setStatus("subscriber field must not be empty!!");
				return;
			}
			//check for any non-alphanumerical characters
			if (Utilities.isTopicStringClean(topic)){
				int stat = model.subscribeTo(topic);
				if (stat == Model.SUBSCRIBE_SUCCESS){
					view.setStatus("successfully subscribed to "+topic+"!");
				} else if (stat == Model.SUBSCRIBE_FAIL){
					view.setStatus("Failed to subscribe!");
				} else if (stat == Model.SUBSCRIBE_ALREADY){
					view.setStatus("you've already subscribed to "+topic+"!");
				}
			
			} else {
				view.setStatus("topic/name can only contain alphanumerical chars after #/@ and no spaces!");
			}
		}
	};
	
	//constructor
	public Controller(String name) throws JMSException{
		this.name = name;
		model = new Model(this, name);
		setupGUI();
	}
	
	//main method
	public static void main(String[] args){
		String name = "";
		do{
			//show the prompt dialog, and resolve until the name is correct
			/*
			 * 	- no non-alphanumerical characters (*,^,&,%, etc.)
			 * 	- no spaces
			 * 	- '@' will be appended, so no need to add '@'
			 */
			name = "@"+Utilities.promptUsername().toLowerCase();
		} while(!Utilities.isTopicStringClean(name));
		
		try {
			new Controller(name);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//setting GUI
	private void setupGUI(){
		view = new View(this);
		view.setPubButtonListener(btnPubListener);
		view.setSubButtonListener(btnSubListener);
		view.setViewSubsButtonListener(btnViewSubsListener);
		this.setSize(625, 550);
		this.setTitle("JMS Microblog - "+name);
		this.setVisible(true);
		this.add(view);
	}

	//listener for incoming notifications.
	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		try{
			TextMessage msg = (TextMessage) arg0;
			String from = msg.getStringProperty("from");
			String tags = msg.getStringProperty("tags");
			String content = msg.getText();
			
			//add the notification to the table
			view.addEntry(from, content, tags);
		}catch(JMSException ex){
			ex.printStackTrace();
		}
	}
	
}
