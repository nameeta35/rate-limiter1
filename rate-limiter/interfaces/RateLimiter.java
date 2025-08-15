interface RateLimiter{
  boolean allowRequest(final Request request) throws FullQueueException;

}