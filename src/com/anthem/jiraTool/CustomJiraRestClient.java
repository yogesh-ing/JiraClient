package com.anthem.jiraTool;

import java.net.URI;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import com.atlassian.util.concurrent.Promise;




public class CustomJiraRestClient {
	public static void main(String[] args) throws Exception {
		final String JIRA_URL = "https://jira.infostretch.com/";
		final String JIRA_ADMIN_USERNAME = "yogesh.ingale";
		final String JIRA_ADMIN_PASSWORD = "Sweet*123";
		// Construct the JRJC client
		System.out.println(String.format("Logging in to %s with username '%s' and password '%s'", JIRA_URL,
				JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD));
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		URI uri = new URI(JIRA_URL);
		JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME,
				JIRA_ADMIN_PASSWORD);
		// Invoke the JRJC Client
		Promise<User> promise = client.getUserClient().getUser("admin");
		User user = promise.claim();
		for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {
			System.out.println(project.getKey() + ": " + project.getName());
		}
		Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
				"project%20%3D%20QEO%20AND%20status%20in%20(Completed%2C%20Done)");
		for (BasicIssue Bissue : searchJqlPromise.claim().getIssues()) {
			System.out.println(Bissue.getKey());
			final Issue issue = client.getIssueClient().getIssue(Bissue.getKey()).claim();
			System.out.println(issue.getDescription()+"\n\nSummary:"+issue.getSummary()+
					"\nStatus:"+issue.getStatus().getName());
		}
		// Print the result
		System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));
		// Done
		System.out.println("Example complete. Now exiting.");
		System.exit(0);
	}
}