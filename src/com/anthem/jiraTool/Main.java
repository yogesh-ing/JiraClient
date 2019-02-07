package com.anthem.jiraTool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.util.concurrent.Promise;

public class Main {

	public static void main(String[] args) throws Exception{
		
		String propertiesFileName=System.getProperty("user.dir")+"/config/config.properties";
		
		System.out.println(propertiesFileName);
		Properties prop=new Properties();
		InputStream input=null;
		input=new FileInputStream(propertiesFileName);
		prop.load(input);
		
		String hostName=prop.getProperty("HOST_NAME");
		String username=prop.getProperty("USERNAME");
		String password=prop.getProperty("PASSWORD");
		String handOffFileName=prop.getProperty("HANDOFF_FILE");
		
		System.out.println(hostName+"\t"+username+"\t"+password+"\t"+handOffFileName);
				
		
		
		JiraConnector jc=new JiraConnector(hostName,username,password);
		
		jc.getAllProject();
		
//		Issue issue=jc.getIssue();
	//	System.out.println(issue.getDescription()+"\n\nSummary:"+issue.getSummary()+"\nStatus:"+issue.getStatus().getName());
		
		
		System.out.println("Example complete.Exiting now");
		System.exit(0);
	}

}
