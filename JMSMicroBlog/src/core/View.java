package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jms.MessageListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import utils.TextPrompt;

/*
 * 	GUI
 */
public class View extends JPanel{

	private JPanel pnlMessages;
	private JPanel pnlSubscribe;
	private JPanel pnlPublish;
	
	private JTable tblMessages;
	private String[] headers = {"From", "Content", "Topic"};
	private DefaultTableModel tblMdlMessages;
	private JScrollPane scrollPane;
	
	private TitledBorder tbrPublish;
	private JTextArea txtContent;
	private JTextField txtTopic;
	private TextPrompt txpContent, txpTopic;
	private JPanel pnlPubButtons;
	private JButton btnPublish;
	
	private TitledBorder tbrSubscribe;
	private JTextField txtSubsTo;
	private JLabel lblStatus;
	private TextPrompt txpSubsTo;
	private JPanel pnlSubButtons;
	private JButton btnSubscribe, btnViewSubscriptions;
	
	private Controller reference;
	
	public View(Controller controller){
		reference = controller;
		this.setLayout(new BorderLayout());
	
		pnlMessages = new JPanel();
		tblMdlMessages = new DefaultTableModel(headers, 0);
		tblMessages = new JTable(tblMdlMessages);
		tblMessages.getColumnModel().getColumn(0).setPreferredWidth(75);
		tblMessages.getColumnModel().getColumn(1).setPreferredWidth(400);
		tblMessages.getColumnModel().getColumn(2).setPreferredWidth(100);
		Dimension d = tblMessages.getPreferredSize();
		scrollPane = new JScrollPane(tblMessages);
		scrollPane.setPreferredSize(new Dimension(d.width, tblMessages.getRowHeight()*18));
		pnlMessages.add(scrollPane);
		this.add(pnlMessages, BorderLayout.NORTH);
		
		//start of publisher panel GUI
		pnlPublish = new JPanel();
		pnlPublish.setLayout(new BorderLayout());
		tbrPublish = new TitledBorder("Publisher section");
		
		txtContent = new JTextArea(5,5);
		txpContent = new TextPrompt("content of the message...", txtContent);
		txpContent.setVerticalAlignment(JLabel.TOP);
		txpContent.changeAlpha(0.6f);
		txpContent.changeStyle(Font.ITALIC);
		
		txtTopic = new JTextField("", 20);
		txpTopic = new TextPrompt("use (,) to separate between topics.", txtTopic);
		txpTopic.changeAlpha(0.6f);
		txpTopic.changeStyle(Font.ITALIC);
		
		pnlPubButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPublish = new JButton("Publish");
		pnlPubButtons.add(btnPublish);
		
		pnlPublish.setBorder(tbrPublish);
		pnlPublish.add(txtTopic, BorderLayout.NORTH);
		pnlPublish.add(txtContent, BorderLayout.CENTER);
		pnlPublish.add(pnlPubButtons, BorderLayout.SOUTH);
		this.add(pnlPublish, BorderLayout.CENTER);
		//end of publisher panel GUI
		
		//start of subscriber panel
		pnlSubscribe = new JPanel(new BorderLayout());
		tbrSubscribe = new TitledBorder("Subscriber section");
		
		txtSubsTo = new JTextField();
		txpSubsTo = new TextPrompt("Use @ for users (@john) and # for topics (#IT). 1 user/topic at a time. No space!", txtSubsTo);
		txpSubsTo.changeAlpha(0.6f);
		txpSubsTo.changeStyle(Font.ITALIC);

		pnlSubButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnSubscribe = new JButton("Subscribe");
		btnViewSubscriptions = new JButton("View Subscription");
		lblStatus = new JLabel("Status: Idle");
		pnlSubButtons.add(btnViewSubscriptions);
		pnlSubButtons.add(btnSubscribe);
		//pnlSubButtons.add(btnSubscribe);
		
		pnlSubscribe.setBorder(tbrSubscribe);
		pnlSubscribe.add(txtSubsTo, BorderLayout.NORTH);
		pnlSubscribe.add(pnlSubButtons, BorderLayout.CENTER);
		pnlSubscribe.add(lblStatus, BorderLayout.SOUTH);
		this.add(pnlSubscribe, BorderLayout.SOUTH);
		//end of subscriber panel
	}
	
	public void setStatus(String status){
		this.lblStatus.setText("Status: "+status);
	}
	
	public void addEntry(String from, String content, String topics){
		tblMdlMessages.addRow(new Object[]{from, content, topics});
	}
	
	public void setPubButtonListener(ActionListener listener){
		btnPublish.addActionListener(listener);
	}
	
	public void setSubButtonListener(ActionListener listener){
		btnSubscribe.addActionListener(listener);
	}
	
	public void setViewSubsButtonListener(ActionListener listener){
		btnViewSubscriptions.addActionListener(listener);
	}

	public void clearPublisherField(){
		txtContent.setText("");
		txtTopic.setText("");
	}
	
	public String getMessageContent(){
		return txtContent.getText();
	}
	
	public String getTopicsToPublish(){
		return txtTopic.getText();
	}
	
	public String getTopicToSubscribe(){
		return txtSubsTo.getText();
	}
	
}
