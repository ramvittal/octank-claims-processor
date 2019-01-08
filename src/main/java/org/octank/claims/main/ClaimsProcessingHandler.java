package org.octank.claims.main;



import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.octank.claims.model.ClaimProcessing;
import org.octank.claims.model.ClaimProcessingId;
import org.octank.claims.model.InsuranceCompany;
import org.octank.claims.model.InsurancePolicy;
import org.octank.claims.model.Log;
import org.octank.claims.model.MedicalProvider;
import org.octank.claims.model.Patient;
import org.octank.claims.model.Staff;

/**
 * @author rvvittal
 */
public class ClaimsProcessingHandler implements RequestHandler<Request, String> {

    @Override
    public String handleRequest(Request request, Context context) {
    	
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        StringBuilder claimsSb = new StringBuilder();
        Session session=null;
        
        try  {
        	
        	session = sessionFactory.openSession();
        	
            session.beginTransaction();
            
            //1. get list of submitted claims

			CriteriaBuilder builder = session.getCriteriaBuilder();
	         CriteriaQuery<Claim> query = builder.createQuery(Claim.class);
	         Root<Claim> root = query.from(Claim.class);
	         query.select(root).where(builder.equal(root.get("claimStatus"), request.getClaimStatus()));
	         
	         Query<Claim> qc = session.createQuery(query);
	         List<Claim> claims=qc.getResultList();
	         for (Claim claim : claims) {
	            System.out.println(claim.getClaimId());
	         }
            
	         
	         //2. for each submitted claim, perform edits, create ClaimProcessing record, update oracle claim status, and send log record to elastic search
	         
	         for (Claim claim : claims) {
		           
	        	 ClaimProcessing cp = new ClaimProcessing();
		           ClaimProcessingId cpi = new ClaimProcessingId();
		           
		           cpi.setClaimId(claim.getClaimId());
		           cpi.setClaimProcessDate(new Date());
		           
		           cp.setId(cpi);
		           cp.setAmountClaimed(claim.getAmountClaimed());
		           
		           
		           //setup claim processing object
		           
		           Patient patient = session.load(Patient.class, claim.getPatientId());
		           
		           cp.setPatientName(patient.getPatientName());
		           cp.setPatientGender(patient.getGender());
		           
		           InsuranceCompany insuranceCo = session.load(InsuranceCompany.class, claim.getInsuranceCompanyId());
		           cp.setInsuranceCompanyName(insuranceCo.getInsuranceCompanyName());
		           
		           MedicalProvider mp = session.load(MedicalProvider.class, claim.getMedicalProviderId());
		           cp.setMedicalProviderName(mp.getMedicalProviderName());
		           
		           Staff staff = session.load(Staff.class, claim.getStaffId());;
		           cp.setPhysicianName(staff.getStaffName());
		           
		           //perform pre-edits, check patient info for completeness
		           
		           if(patient.getPatientAddress() == null || patient.getPatientAddress().isEmpty()
		        		//demo  || patient.getPatientCity() == null || patient.getPatientCity().isEmpty()
		        		   ) {
		        	   cp.setClaimStatus("Incomplete");
		           } else {
		           
			           //perform adjudication
			           
			           InsurancePolicy insurancePolicy = session.load(InsurancePolicy.class, claim.getInsurancePolicyNbr());
			           
			           if(!patient.getGender().equals(insurancePolicy.getInsuredGender())) {
			        	   cp.setClaimStatus("Rejected");
			           } else if (claim.getAmountClaimed().doubleValue() > insurancePolicy.getInsuredAmount().doubleValue()) {
			        	   cp.setClaimStatus("Denied");
			           } else {
			        	   cp.setClaimStatus("Approved");
			           }
			           
		           }
		           
		           System.out.println("***Claim Status:" +cp.getClaimStatus() + " ***");
		           
		           
		           String claimIdSt = claim.getClaimId() +"~" + cp.getClaimStatus();
		           
		           session.save(cp);
		           claimsSb = claimsSb.length() > 0 ? claimsSb.append("," + claimIdSt) : claimsSb.append(claimIdSt);
		           //System.out.println("**** Processed claims: " +claimsSb.toString() + "***End");
		           
		           ObjectMapper mapper = new ObjectMapper();
		            
		            Log log = new Log();
		            
		            log.setEventType("ProcessClaim");
		            log.setEventTmst(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		            log.setObjectType("ClaimProcessing");
		            log.setLevel("INFO");
		            log.setContext("Processed Claim");
		            log.setObject(cp);

					String jsonLog = mapper.writeValueAsString(log);
					System.out.println(jsonLog);
		           
		      }
	         
	         
	         
            session.getTransaction().commit();
        }
        catch(Exception e) {
        	e.printStackTrace(System.out);
        	 ObjectMapper mapper = new ObjectMapper();
	            
	            Log log = new Log();
	            
	            log.setEventType("ProcessClaim");
	            log.setEventTmst(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
	            log.setObjectType("ClaimProcessing");
	            log.setContext(e.getMessage());
	            log.setLevel("ERROR");
	            log.setObject(request);

				
				try {
					String jsonLog = mapper.writeValueAsString(log);
					System.out.println(jsonLog);
				} catch (JsonProcessingException e1) {
					e1.printStackTrace(System.out);
				}
				
        	
        }
        finally {
        	if(session != null) {
				session.close();
			}
        }

        return claimsSb.toString();
    }
}

