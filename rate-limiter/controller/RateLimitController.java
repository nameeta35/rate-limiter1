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