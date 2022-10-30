package com.test.users;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

public class ReadOutLookMail {

	public boolean match(Message message) {
		try {
			if (message.getSubject().contains("Ordretest")) {
				System.out.println("match found");
				return true;
			}
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		
		Timer timer = new Timer(); 
		timer.schedule( new TimerTask() 
		{ 
		    public void run() { 
		    // do your work 
		    	System.out.println("test..1");
		    	triggerReadMail();
		    } 
		}, 0, 60*(1000*2));
		
	}

	public static void triggerReadMail() {
		System.out.println("*** Inside triggerReadMail ***");
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		props.put("mail.imap-mail.outlook.com.ssl.enable", "true");
		props.put("mail.pop3.host", "outlook.com");
		props.put("mail.pop3.port", "995");
		props.put("mail.pop3.starttls.enable", "true");
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect("imap-mail.outlook.com", "kirantandur@outlook.com", "Gogte@2016");
			session.setDebug(true);
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);

			// SearchTerm sender = new FromTerm(new
			// InternetAddress("kiran.tandur@oracle.com"));

			Message[] messages = inbox.getMessages();

			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

			String subject = "test read mail";
			DatabaseDMLOperations dbDmlOper = new DatabaseDMLOperations();
			for (int i = 0; i < messages.length; i++) {
				if (null != messages[i].getSubject() && messages[i].getSubject().contains(subject)) {
					System.out.println("1 match found");
					Message message = messages[i];
					Date mailDate = message.getReceivedDate();
					String stringDate = formatter.format(mailDate);
					System.out.println(stringDate);
					System.out.println("Mail Date -> : " + message.getReceivedDate());
					MimeMultipart mimeMultiPart = (MimeMultipart) messages[i].getContent();
					BufferedReader testBffer = new BufferedReader(
							new InputStreamReader(mimeMultiPart.getBodyPart(0).getInputStream()));
					// BufferedReader testBffer = new BufferedReader(new InputStreamReader(
					// messages[i].getInputStream()));
					String str = null;
					Map<String, String> mapValue = new HashMap<>();
					mapValue.put("subject", subject);
					while ((str = testBffer.readLine()) != null) {
						System.out.println(str);
						if (str.trim().contains("userId")) {
							String[] strArray = str.split(":");
							mapValue.put(strArray[0].trim(), strArray[1].trim());
						}
						if (str.trim().contains("location")) {
							String[] strArray = str.split(":");
							mapValue.put(strArray[0].trim(), strArray[1].trim());
						}
						if (str.trim().contains("ticket number")) {
							String[] strArray = str.split(":");
							mapValue.put(strArray[0].trim(), strArray[1].trim());
						}
					}
					System.out.println(mapValue);
					dbDmlOper.insertValues(mapValue, mailDate);
				}
			}
			Connection connection = GetDbConnection.getConnection();
			if (connection != null) {
				dbDmlOper.saveUserDetailsToExcel(connection);
			} else {
				System.out.println("!! DB Connection returning Null !!");
			}
			System.out.println("-------------------------------------------------------");
			System.out.println("Scheduler is On... Messages will fetch every 5 Minutes");
			store.close();

		} catch (Exception mex) {
			mex.printStackTrace();
		}

	}

}
