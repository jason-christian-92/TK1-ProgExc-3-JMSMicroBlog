package objects;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

public class Consumer {

	private Topic topic;
	private MessageConsumer consumer;
	
	public Consumer(){ }
	
	public void setTopicName(Session session, String topicName, MessageListener listener) throws JMSException{
		topic = session.createTopic(topicName);
		consumer = session.createConsumer(topic);
		consumer.setMessageListener(listener);
	}
	
	public String getTopicName() throws JMSException{
		return topic.getTopicName();
	}
}
