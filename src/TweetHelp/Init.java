package TweetHelp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class Init {
	static AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
	public static AmazonDynamoDB dynamo = new AmazonDynamoDBClient(credentialsProvider);
	public static String tableName = "tweet";
	public String key = "";
	public void setKey(String Key)
	{
		key = Key;
	}
	public static TwitterStream Authenticate() throws IOException
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		BufferedReader br = new BufferedReader(new FileReader("twitterCredentials.properties"));

		cb.setDebugEnabled(true)
        	.setOAuthConsumerKey(br.readLine())
        	.setOAuthConsumerSecret(br.readLine())
        	.setOAuthAccessToken(br.readLine())
        	.setOAuthAccessTokenSecret(br.readLine());
		br.close(); 
       TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
       return twitterStream;
	}
	public static String googleKey() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("/googleCredentials.properties"));
	    String APIKEY = "https://maps.googleapis.com/maps/api/js?key=" + "AIzaSyCcsrwleinPdgBpSfsScSLcY-AjRE7qMJA";
	    br.close();
	    return APIKEY;
	}
	/*public static void main(String[] args) throws IOException{
		System.out.println(googleKey());
	}*/
}
