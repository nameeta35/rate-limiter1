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
