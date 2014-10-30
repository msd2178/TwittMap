package TweetHelp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TweetGet {
    /**
     * Main entry of this application.
     *
     * @param args
     */
	public static AmazonDynamoDB dynamo = Init.dynamo;
	public static String tableName = "tweet";
	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
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
	
    public static void getTweets(TwitterStream twitterStream, int max_count) throws TwitterException, InterruptedException {
    	//just fill this
    		final Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                try{
                	System.out.println(status.getGeoLocation().getLatitude() + " " + status.getGeoLocation().getLongitude());
                	item.put("TweetText", new AttributeValue().withS(status.getText()));
                	item.put("TweetDate", new AttributeValue().withS(dateFormatter.format(status.getCreatedAt())));
                	item.put("Latitude", new AttributeValue().withN(status.getGeoLocation().getLatitude() + ""));
                	item.put("Longitude", new AttributeValue().withN(status.getGeoLocation().getLongitude() + ""));

                	PutItemRequest putItemRequest = new PutItemRequest()
                	  .withTableName(tableName)
                	  .withItem(item);
                	dynamo.putItem(putItemRequest);
                	item.clear();
                }
                catch(Exception e)
                {
                	
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
        Thread.sleep(max_count);
        twitterStream.removeListener(listener);
    }
    
    public static void main(String[] args) throws TwitterException, InterruptedException, IOException{
    	TwitterStream tw = Authenticate();
    	for(int i=0;i<10;i++)
    	{
    		System.out.println(i);
    		getTweets(tw,10*3600);
    	}
    	tw.shutdown();
    }
}