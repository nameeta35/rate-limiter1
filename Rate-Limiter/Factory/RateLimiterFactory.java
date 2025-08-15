class RateLimiterFactory{
  public static RateLimiter createRateLimiter(int maxAllowedRequests, int maxAllowedRequestsPerUser){
    if (maxAllowedRequestsPerUser != 0){
      return new UserSpecificRateLimiter(maxAllowedRequestsPerUser);
    } 
    return new RequestCountRateLimiter(maxAllowedRequests);
  }
}