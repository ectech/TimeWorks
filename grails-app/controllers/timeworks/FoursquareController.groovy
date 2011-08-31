package timeworks

import org.scribe.builder.ServiceBuilder
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Token
import org.scribe.model.Verifier

class FoursquareController {

    def index = { 
		
		def apiKey = '';
		def apiSecret = '';
		def service = new ServiceBuilder()
		.provider(Foursquare2Api.class)
		.apiKey(apiKey)
		.apiSecret(apiSecret)
		.callback("http://localhost:8080/")
		.build();
		
		def input = new Scanner(System.in)
		
		println "=== Foursquare2's OAuth Workflow ===";
		System.out.println();
		
		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...");
		def authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		System.out.println("Got the Authorization URL!");
		System.out.println("Now go and authorize Scribe here:");
		System.out.println(authorizationUrl);
		System.out.println("And paste the authorization code here");
		System.out.print(">>");
		def verifier = new Verifier(input.nextLine());
		System.out.println();
		
		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		def accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: " + accessToken + " )");
		System.out.println();
	
		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		def request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL + accessToken.getToken());
		service.signRequest(accessToken, request);
		def response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());
	
		System.out.println();
		System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
	
	
	
		
		
	}
}
