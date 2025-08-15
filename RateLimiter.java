/*
 * Click `Run` to execute the snippet below!
 */

import java.io.*;
import java.util.*;t 

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */
 

class Request{
  private String productName;
  private String userId;

  public Request(final String productName){
    if (null == productName) {
      throw new NullPointerException("productName is null");
    }
    this.productName = productName;
  }

  public Request(final String productName, String userId){
    if (null == productName) {
      throw new NullPointerException("productName is null");
    }
    this.productName = productName;
    this.userId = userId;
  }

  public String getUserId(){
    return userId;
  }
  
}

class FullQueueException extends Exception {
  private static final long serialVersionUID = 1L;
  public FullQueueException(final String message){
    super(message);
  }
}

interface RateLimiter{
  boolean allowRequest(final Request request) throws FullQueueException;

}

// A -> 2

// if request timestamp older than 60 seconds
  //poll from requestTimestamp queue
  //update freq for the user 
// if user request freq > allowedRate
  //deny the user
class UserSpecificRateLimiter implements RateLimiter{
  private int maxAllowedRequestsPerUser;
  private Queue<Long> requestTimestamps;
  private Map<String, Integer> userHits;
  private Map<Long, List<String>> userRequestTimeStamps;

  public UserSpecificRateLimiter(int maxAllowedRequestsPerUser){
    this.maxAllowedRequestsPerUser = maxAllowedRequestsPerUser;
    requestTimestamps = new LinkedList<>();
    userHits = new HashMap<>();
    userRequestTimeStamps = new HashMap<>();
  }
  
  Long currentTime = System.currentTimeMillis();

  public void addUserHitsToMap(String userId){
    if (userHits.containsKey(userId)){
      userHits.put(userId, userHits.get(userId) + 1);
    } else {
      userHits.put(userId, 1);
    }
  }

  public void deleteUserHitsFromMap(String userId){
    if (userHits.containsKey(userId)){
      userHits.put(userId, userHits.get(userId) - 1);
    }
  }

  public void refreshRequestTimestamps(){
    while(!requestTimestamps.isEmpty() && requestTimestamps.peek() >= 60_000){
      Long timestamp = requestTimestamps.poll();
      System.out.println("Current timestamp" + timestamp);
      updateUserHits(timestamp);
    }
  }

  public void updateUserHits(Long timestamp){
    if(userRequestTimeStamps.containsKey(timestamp)){
      List<String> userIds = userRequestTimeStamps.get(timestamp);
      if(!userIds.isEmpty()){
        for (String userId : userIds){
          deleteUserHitsFromMap(userId);
        }
      }
        userRequestTimeStamps.remove(timestamp);
    }
  }


  @Override
  public boolean allowRequest(final Request request) throws FullQueueException{
    refreshRequestTimestamps();
    String userId = request.getUserId();
    if (userHits.containsKey(userId)){
      int numOfRequests = userHits.get(userId);
      if (numOfRequests >= maxAllowedRequestsPerUser){
        return false;
      }
    }
    requestTimestamps.add(currentTime);
    addUserHitsToMap(userId);
    return true;
  }
}

class RequestCountRateLimiter implements RateLimiter{
  private int maxAllowedRequests;
  private Queue<Long> requestTimestamps;
  
  public RequestCountRateLimiter(final int maxAllowedRequests){
    requestTimestamps = new LinkedList<>();
    this.maxAllowedRequests = maxAllowedRequests;
  }

  @Override
  public boolean allowRequest(final Request request) throws FullQueueException{
    if (request == null) {
      throw new RuntimeException("request is null");
    } 

    Long currentTime = System.currentTimeMillis();

    while (!requestTimestamps.isEmpty() && currentTime - requestTimestamps.peek() >= 60_000){
      requestTimestamps.poll();
    }
    if (requestTimestamps.size() >= maxAllowedRequests){
      return false;
    } 
    requestTimestamps.add(currentTime);
    return true;
    
  }

}

class RateLimitController{
  RateLimiter rateLimiter;
  private int capacity;
  private Queue<Request> requestQueue;

  public RateLimitController(){
    requestQueue = new LinkedList<>();
    this.capacity = 500;
  }

  public void createRateLimiter(int maxAllowedRequests, int maxAllowedRequestsPerUser){
    rateLimiter = RateLimiterFactory.createRateLimiter(maxAllowedRequests, maxAllowedRequestsPerUser);  
  }

  public void allowRequest(Request request) throws FullQueueException{
    if (!rateLimiter.allowRequest(request)){
       throw new FullQueueException("Request limit exceeded");
    }
    if (rateLimiter.allowRequest(request)){
      if (requestQueue.size() >= capacity){
        throw new FullQueueException("Queue is full, request denied");
      } else {
        requestQueue.add(request);
        System.out.println("Request is allowed");
      }
  }
}

class RateLimiterFactory{
  public static RateLimiter createRateLimiter(int maxAllowedRequests, int maxAllowedRequestsPerUser){
    if (maxAllowedRequestsPerUser != 0){
      return new UserSpecificRateLimiter(maxAllowedRequestsPerUser);
    } 
    return new RequestCountRateLimiter(maxAllowedRequests);
  }
}

class Solution {
  public static void main(String[] args) throws Exception {
    
    RateLimitController rateLimiter = new RateLimitController();
    rateLimiter.createRateLimiter(0, 2);
    Request request1 = new Request("abc", "userId1");
    Request request2 = new Request("def", "userId1");
    Request request3 = new Request("awe", "userId1");
    rateLimiter.allowRequest(request1);
    rateLimiter.allowRequest(request2);
    rateLimiter.allowRequest(request3);
    rateLimiter.allowRequest(request3);

  }
}
}


//API Rate Limiter
//rate - 10 req/min
//deny -> throw an exception

//Algo
  //are we looking for more steady/fixed rate? 
  //are we okay with allowing bursts of traffic and then not taking traffic for a some time ?

//Leaky bucket
  //10 tokens/minute in a bucket
  //bucket size == queue size
  //put requests on the queue 
  //if reqCount for the minute > 10, throw an exception

//scale
  //we can make it more scalable by having rate limiting using number of requests, ip address 



//main -> Controller -> RequestCountRateLimiter (singleton instance, rate) -> 
