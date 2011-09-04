package timeworks

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT
import org.scribe.builder.*
import org.scribe.model.*
import org.scribe.builder.api.*
import org.scribe.oauth.*


class FoursquareController {
	Token EMPTY_TOKEN = null
	def apiKey = 'YQ5EODJXA1HGFNP4QOOFFHJ2BX0Q4FC0KLCGCHPGRUIYSCR3';
	def apiSecret = '3B1AISWL4PXCCP5ZSU4KTWUDCX4R5C2BF3MXQXB1MBTVHPX4';
	def service = new ServiceBuilder()
	.provider(Foursquare2Api.class)
	.apiKey(apiKey)
	.apiSecret(apiSecret)
	.callback("http://localhost:8080/TimeWorks/foursquare/venues")
	.build();
	
    def index = { 
		def PROTECTED_RESOURCE_URL = "https://api.foursquare.com/v2/venues/categories?oauth_token="
		
		println "=== Foursquare2's OAuth Workflow ===";
		System.out.println();
		
		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...123");
		def authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		
		if(session.verifierCode == null)
			redirect(url:authorizationUrl)
		else {
			println "redirecting to venues"
			redirect(action:venues)
		}
		/*
		def http = new HTTPBuilder(authorizationUrl)
		http.request(GET,TEXT) { 
			response.success = { resp, reader ->
				assert resp.status == 200
				println "My response handler got response: ${resp.statusLine}"
				println "Response length: ${resp.headers.'Content-Length'}"
				println "Location:"
				def hs = resp.getAllHeaders()
				for(x in hs) {
					println "Name: " + x.getName() + ", Value: " + x.getValue() + "\n" 
				}
				
				render reader.getText() // print response reader
				
			  }	
		}
		*/

	}
	
	def venues = {
		if(session.verifierCode == null)
			if(params['code'] != null)
				session.verifierCode = params['code']
			else
				redirect(action:index)
			
		def PROTECTED_RESOURCE_URL = "https://api.foursquare.com/v2/venues/categories?oauth_token="
		def verifier = new Verifier(session.verifierCode);
		def accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		def request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL + accessToken.getToken());
		
		service.signRequest(accessToken, request);
		
		def response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());
		
		render response.getBody()

	}
}
