package utils;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Utilities {
	
	//method to parse comma-separated string to an array of strings
	//used for multiple topics
	public static String[] StringToTopicsArray(String topics){
		String[] result = new String[]{};
		
		if (!topics.isEmpty()){
			result = topics.split(",");
			for (int i = 0 ; i < result.length ; i++){
				if (result[i].charAt(0) != '#') 
					result[i]="#"+result[i];
				if (!isTopicStringClean(result[i])){
					return null;
				}
			}
		}
		return result;
	}
	
	//check whether the topic/user name only contains allowed characters
	public static boolean isTopicStringClean(String topic){

		if (topic.isEmpty()) 
			return false;
		
		if (topic.contains(" ")){
			return false;
		}

		if (!topic.startsWith("@") && !topic.startsWith("#")){
			return false;
		}
		
		String textWithoutFirstChar = topic.substring(1, topic.length());
		return textWithoutFirstChar.matches("^([A-Za-z]|[0-9])+$");
	}
	
	//method to create a Dialog, prompting for username.
	//Used when the application is executed.
	public static String promptUsername(){
		JTextField txtName = new JTextField();
		TextPrompt hintText = new TextPrompt("username..", txtName);
		hintText.changeAlpha(0.6f);
		hintText.changeStyle(Font.ITALIC);
		final JComponent[] units = new JComponent[]{txtName};
		
		JOptionPane.showMessageDialog(null, units, "Welcome", JOptionPane.PLAIN_MESSAGE);
		return txtName.getText();
	}
	
}
