package com.anthem.jiraTool;

import java.awt.Desktop;
//import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicVotes;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.Attachment;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class JiraConnector {

	private static String JIRA_URI = "";
	private static String USERNAME = "";
	private static String PASSWORD = "";
	private JiraRestClient client = null;

	public JiraRestClient getClient() {
		return client;
	}
	public void setClient(JiraRestClient client) {
		this.client = client;
	}
	public JiraConnector(String javaUri, String username, String password) {
		JIRA_URI = javaUri;
		USERNAME = username;
		PASSWORD = password;
		// Print usage instructions
		StringBuilder intro = new StringBuilder();
		intro.append(
				"**********************************************************************************************\r\n");
		intro.append(
				"* JIRA Java REST Client ('JRJC') example.                                                    *\r\n");
		intro.append(
				"* NOTE: Start JIRA using the Atlassian Plugin SDK before running this example.               *\r\n");
		intro.append(
				"* (for example, use 'atlas-run-standalone --product jira --version 6.0 --data-version 6.0'.) *\r\n");
		intro.append(
				"**********************************************************************************************\r\n");
		System.out.println(intro.toString());

		// Construct the JRJC client
		System.out.println(
				String.format("Logging in to %s with username '%s' and password '%s'", JIRA_URI, USERNAME, PASSWORD));

		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		URI uri = null;
		try {
			uri = new URI(JIRA_URI);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		client = factory.createWithBasicHttpAuthentication(uri, USERNAME, PASSWORD);

		Promise<User> promise = client.getUserClient().getUser("admin");
		User user = promise.claim();


	}

	public void getAllProject() {
		for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {

			//System.out.println(project.getKey() + ": " + project.getName());
			if(project.getName().equalsIgnoreCase("QE Onboarding ")){
				final Issue issue = client.getIssueClient().getIssue("QEO-9817").claim();
//				System.out.println(issue.getDescription()+"\n\nSummary:"+issue.getSummary()+
//						"\nStatus:"+issue.getStatus().getName());
				
				
				for(Transition tran :client.getIssueClient().getTransitions(issue).claim()){
					System.out.println(tran.getName());
				}
				break;
			}
		}	
	}
	
	public Issue getIssue() {
		final Issue issue = client.getIssueClient().getIssue("WFP-25").claim();
		System.out.println(issue.getDescription()+"\n\nSummary:"+issue.getSummary()+
				"\nStatus:"+issue.getStatus().getName());
		return issue;
	}

	public List<Object> getAllComments(String issueKey) {
		return StreamSupport.stream(getIssue(issueKey).getComments().spliterator(), false)
				.collect(Collectors.toList());
	}

	/*public void updateIssueDescription(String issueKey, String newDescription) {
	    IssueInput input = new IssueInputBuilder()
	      .setDescription(newDescription)
	      .build();
	    client.getIssueClient()
	      .updateIssue(issueKey, input)
	      .claim();
	}*/
	public Issue getIssue(String issueKey) {
		return client.getIssueClient().getIssue(issueKey).claim();
	}


	public void voteForAnIssue(Issue issue) {
		client.getIssueClient()
		.vote(issue.getVotesUri())
		.claim();
	}
	public void addComment(Issue issue, String commentBody) {
		client.getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
	}	
	/*public void deleteIssue(String issueKey, boolean deleteSubtasks) {
	    client.getIssueClient().getIssue(issueKey).deleteIssue(issueKey, deleteSubtasks)
	      .claim();
	}*/

	public int getTotalVotesCount(String issueKey) {
		BasicVotes votes = getIssue(issueKey).getVotes();
		return votes == null ? 0 : votes.getVotes();
	}
	public void connect(String issueName) throws Exception {
		// Invoke the JRJC Client
		Promise<User> promise = client.getUserClient().getUser(USERNAME);
		User user = promise.claim();
		// Print the result
		System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));

		ProjectRestClient project = client.getProjectClient();
		Promise<Project> projectDetails = project.getProject("JiraTest");
		System.out.println(projectDetails.isDone());

		IssueRestClient issue = client.getIssueClient();
		Promise<Issue> issueDetails = issue.getIssue(issueName);

		System.out.println(issueDetails.get().getStatus().getName());

		Iterator<Attachment> attachmentIteraor = issueDetails.get().getAttachments().iterator();
		while (attachmentIteraor.hasNext()) {
			Attachment attachment = attachmentIteraor.next();
			System.out.println(attachment.getContentUri().toString());
			URI attachmentURI=attachment.getContentUri();
			System.out.println(attachmentURI.getPath());
			// Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start
			// chrome " + attachment.getContentUri() });
			// Runtime.getRuntime().exec("C:/Program Files
			// (x86)/Google/Chrome/Application/chrome.exe", new
			// String[]{attachment.getContentUri()+""});
			Desktop.getDesktop().browse(attachment.getContentUri());

			/*
			 * URL url=new URL(attachment.getContentUri().toString());
			 * ReadableByteChannel rbc=Channels.newChannel(url.openStream());
			 * fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			 */


			// Make sure that this directory exists
			/* String dirName = "C:\\Users\\ahunjan\\Desktop";
	        try {
	            saveFileFromUrlWithJavaIO(
	                dirName + "\\java_tutorial_12345678.xlsx",attachment.getContentUri().toString());
	            System.out.println("finished");
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			 */
		}

	}


	/*	public static void download(String url, String fileName) throws Exception {
		    try (InputStream in = URI.create(url).toURL().openStream()) {
		        Files.copy(in, Paths.get(fileName));
		    }
		}  
	 */
	public void saveFileFromUrlWithJavaIO(String fileName, String fileUrl) throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(fileUrl).openStream());
			fout = new FileOutputStream(fileName);
			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
	}

	public static void saveFileFromUrlWithCommonsIO(String fileName,
			String fileUrl) throws MalformedURLException, IOException {
		FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
	}
}
