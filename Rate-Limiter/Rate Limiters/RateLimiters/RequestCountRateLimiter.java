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