package org.octank.claims.main;

/**
 * @author rvvittal
 */

public class Request {
	
	

	String requestId;
    String claimStatus;

	
    public String getRequestId() {
		return requestId;
	}



	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}



	public String getClaimStatus() {
		return claimStatus;
	}



	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}


    public Request() {
    	
    }
    
  

    

    @Override
    public String toString() {
        return "Request{" + "requestId" + requestId + ", claimStatus=" + claimStatus + '}';
    }
}
