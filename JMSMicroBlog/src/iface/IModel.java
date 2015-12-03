/**
 * 
 */
package iface;

public interface IModel {

	//subscriber
	/*
	 * Function to subscribe to a certain topic/user
	 * @param	name	the name of the topic/user. Should already include @/#
	 * @return	[int] status code. See the implementation (Model.java)
	 */
	public int subscribeTo(String name);
	/*
	 * Function to check whether a topic/user is already subscribed to
	 * @param	name	the name of the topic/user. Should already include @/#
	 * @return	[int] status code. See the implementation (Model.java)
	 */
	public boolean isSubscribedTo(String name);
	
	//publisher
	/*
	 * Function to send messages to subscribers
	 * @param	from		the publisher's name
	 * @param	content		the content of the message
	 * @param	toTopic		array of topic names. May or may not include the # character
	 * @return	[int] status code. See the implementation (Model.java)
	 */
	public int sendNotification(String from, String content, String[] toTopic);
	
}
