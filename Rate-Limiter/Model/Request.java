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