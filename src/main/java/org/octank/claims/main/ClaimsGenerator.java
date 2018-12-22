package org.octank.claims.main;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.octank.claims.model.Claim;
import org.octank.claims.model.Log;
import org.octank.claims.model.Patient;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ClaimsGenerator {

	public static void main(String[] args) {
		
		BatchRequest request = new BatchRequest();
		request.setCount(10);
		
		request.setRequestId("CL-102-");
		request.setStatus("Submitted");
		
		Claim claim = new Claim();
		claim.setAmountClaimed(new BigDecimal(500));
		claim.setInsuranceCompanyId("IC101");
		claim.setInsurancePolicyNbr("IC101-101");
		claim.setMedicalProviderId("MP101");
		claim.setPatientId("101");
		claim.setStaffId("S101");
		//claim.setUpdatedDate(updatedDate);
		
		
		request.setClaim(claim);
		
		generateClaims(request);
		

	}
	
	
	 public static String generateClaims(BatchRequest request) {
	       
		 SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		 Session sessionObj = null;
		 String status="success";
		 
		
		 
		 
		 try {
				sessionObj = sessionFactory.openSession();
				sessionObj.beginTransaction();
				

				
				
				for(int i=0; i < request.getCount(); i++ )
				{
					
					Claim c = request.getClaim();
					c.setClaimId(request.getStatus());
					c.setClaimId(request.getRequestId() + i);
					c.setUpdatedDate(new Date());
					
					sessionObj.save(c);
					sessionObj.flush();
			        sessionObj.clear();
				}

				// Committing The Transactions To The Database
				
				sessionObj.getTransaction().commit();
				
				


				
				
				
			} catch(Exception sqlException) {
				if(null != sessionObj.getTransaction()) {
					System.out.println("\n.......Transaction Is Being Rolled Back.......");
					sessionObj.getTransaction().rollback();
					status="failure";
				}
				sqlException.printStackTrace();
			} finally {
				if(sessionObj != null) {
					sessionObj.close();
				}
				
				
			}
		 
		 return status;

	        
	    }

}
