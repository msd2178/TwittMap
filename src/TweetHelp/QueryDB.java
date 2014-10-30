package TweetHelp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;


public class QueryDB {
	public AmazonDynamoDB dynamo = Init.dynamo;
	public String tableName = "tweet";
	
	public QueryDB()
	{
		
	}
	
	public List<Map<String, AttributeValue>> getAllLocations()
	{
		ScanRequest scanRequest = new ScanRequest()
	    .withTableName(tableName)
	    .withProjectionExpression("TweetText,TweetDate,Latitude,Longitude");

		ScanResult result = dynamo.scan(scanRequest);
		return result.getItems();
	}
	
	public List<Map<String, AttributeValue>> getKeyLocations(String key)
	{
		Map<String, Condition> queryFilter = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
	    .withComparisonOperator(ComparisonOperator.CONTAINS)
	    .withAttributeValueList(new AttributeValue().withS(key));
		queryFilter.put("TweetText", hashKeyCondition);
		
		ScanRequest scanRequest = new ScanRequest()
	    .withTableName(tableName)
	    .withScanFilter(queryFilter);

		ScanResult result = dynamo.scan(scanRequest);
		return result.getItems();
		

	}
	
}
