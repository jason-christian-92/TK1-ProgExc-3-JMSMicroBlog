package core;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import objects.Consumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import iface.IModel;

public class Model implements IModel{

	//status code for subscribers
	public static final int SUBSCRIBE_FAIL=-1;
	public static final int SUBSCRIBE_SUCCESS=1;
	public static final int SUBSCRIBE_ALREADY=2;
	
	//status code for publishers
	public static final int PUBLISH_SUCCESS=1;
	public static final int PUBLISH_FAIL=-1;
	
	//connection variables
	private ActiveMQConnectionFactory conFactory;
	private Connection con;
	private Session session;
	private MessageProducer ownProducer;
	
	//arraylist of subscriptions
	private ArrayList<Consumer> consumers;
	
	//reference to controller
	private Controller reference;
	private String name;
	
	//constructor
	public Model(Controller controller, String name) throws JMSException{
		consumers = new ArrayList<Consumer>();
		reference = controller;
		this.name = name;
		
		conFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
		con = conFactory.createConnection();
		con.start();
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//create producer for his/her own username
		Topic ownTopic = session.createTopic(name);
		ownProducer = session.createProducer(ownTopic);
	}
	
	//method to get the list of subscriptions (topic/user names)
	public String[] getSubscriptions() throws JMSException{
		String[] result = new String[consumers.size()];
		for (int i = 0 ; i < consumers.size() ; i++){
			result[i] = consumers.get(i).getTopicName();
		}
		return result;
	}
	
	@Override
	public int subscribeTo(String name){
		// TODO Auto-generated method stub
		try {
			//check if the user already subscribed
			if (!isSubscribedTo(name)){
				Consumer consumer = new Consumer();
				consumer.setTopicName(session, name, reference);
				consumers.add(consumer);
				return SUBSCRIBE_SUCCESS;
			} else 
				return SUBSCRIBE_ALREADY;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SUBSCRIBE_FAIL;
		}
	}
	
	@Override
	public int sendNotification(String from, String content, String[] toTopic) {
		// TODO Auto-generated method stub
		try{
			//set several string properties as tags and publisher's name.
			TextMessage msg = session.createTextMessage(content);
			String tags = "";
			for (int i = 0 ; i < toTopic.length ; i++){
				tags += toTopic[i];
				if (i < toTopic.length-1) tags+=",";
			}
			msg.setStringProperty("tags", tags);
			msg.setStringProperty("from", from);
			
			//send to subscribers who subscribed to the producer
			ownProducer.send(msg);
			System.out.println("sending to users subscribing to: "+name);
			
			//then iterate to the list of targets and send to all subscribers
			//who subscribe to the topic.
			for (int i = 0 ; i < toTopic.length ; i++){
				System.out.println("sending to users subscribing to: "+toTopic[i]);
				Topic topic = session.createTopic(toTopic[i]);
				MessageProducer producer = session.createProducer(topic);
				producer.send(msg);
				producer.close();
			}
			return PUBLISH_SUCCESS;
		}catch(JMSException ex){
			ex.printStackTrace();
			return PUBLISH_FAIL;
		}
	}

	@Override
	public boolean isSubscribedTo(String name) {
		// TODO Auto-generated method stub
		try{
			for (Consumer consumer:consumers){
				if (consumer.getTopicName().equals(name))
					return true;
			}
		}catch(JMSException ex){
			ex.printStackTrace();
		}
		return false;	
	}

}
