package org.apache.ws.commons.tcpmon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.behaviosec.tree.config.Constants;
import com.behaviosec.tree.restclient.BehavioSecRESTClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	private final BehavioSecRESTClient behavioSecRESTClient;
	private final int operatorFlags = Constants.FLAG_GENERATE_TIMESTAMP + Constants.FINALIZE_DIRECTLY;
	private static final String TAG = Utils.class.getName();
    private final Logger logger = LoggerFactory.getLogger(TAG);
    
    public Utils() {
    	this.behavioSecRESTClient = new BehavioSecRESTClient(TcpMonNoAWT.bsURL); 
    }

    public String decode(String value) {
        try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public boolean checkData(String timingData) {
    	try {
        boolean connectionToServer = behavioSecRESTClient.getHealthCheck();
        logger.warn("Checking health " + connectionToServer);
        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
        String username = "fred";

        // TODO what is the best practice to post fix userid?
        username += "_";
        nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID, username));
 
        if(timingData != null) {
            nameValuePairs.add(new BasicNameValuePair(Constants.TIMING, timingData));
        } else {
            logger.warn("Timing data is null");
            // We check for flag, and we either return deny or success
            if (true) { //) {
                return false;
            } else {
                return true;
            }
        }

		String userAgent="Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3";
		String ip = "1.1.1.1";

        nameValuePairs.add(new BasicNameValuePair(Constants.USER_AGENT, userAgent));
        nameValuePairs.add(new BasicNameValuePair(Constants.IP, ip)); //context.request.clientIp));
        nameValuePairs.add(new BasicNameValuePair(Constants.TIMESTAMP,
                Long.toString(Calendar.getInstance().getTimeInMillis())));
        nameValuePairs.add(new BasicNameValuePair(Constants.SESSION_ID, UUID.randomUUID().toString()));
        nameValuePairs.add(new BasicNameValuePair(Constants.NOTES, "FR-V" + "1.0.0"));
        nameValuePairs.add(new BasicNameValuePair(Constants.REPORT_FLAGS, Integer.toString(0)));
        nameValuePairs.add(new BasicNameValuePair(Constants.OPERATOR_FLAGS,Integer.toString(this.operatorFlags)));

        HttpResponse reportResponse = behavioSecRESTClient.getReport(nameValuePairs);
        int responseCode = reportResponse.getStatusLine().getStatusCode();

        System.out.println("Return " + responseCode);
        if ( responseCode == 200 ) {
            ObjectMapper objectMapper = new ObjectMapper();
 //           BehavioSecReport bhsReport = objectMapper.readValue(EntityUtils.toString(reportResponse.getEntity()), BehavioSecReport.class);
   //         logger.warn("1 - bhs report -> " + bhsReport.toString());

     //       newSharedState.put(Constants.BEHAVIOSEC_REPORT, asList(bhsReport));
            //logger.warn("2 - newSharedState -> " + newSharedState);
            return true;
        } else if ( responseCode == 400 ) {
            logger.warn(TAG + " response 400  ");// + getResponseString(reportResponse));
        } else if ( responseCode == 403 ) {
            logger.warn(TAG + " response 403  ");
            logger.warn(TAG + " response 400  ");// + getResponseString(reportResponse));

        } else if ( responseCode == 500 ) {
            logger.warn(TAG + " response 500  ");
            logger.warn(TAG + " response 400  ");// + getResponseString(reportResponse));
        } else {
            logger.warn(TAG + " response " + responseCode);
        }

    	return false;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }    
}
