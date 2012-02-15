package timeworks

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import groovy.time.TimeCategory

class GooglebooksController {
	def NETWORK_NAME = "Google"
	def CLIENT_ID = "690667165235.apps.googleusercontent.com"
	def CLIENT_SECRET = "SnAOp_UYXQtrJECyYJJt8ET7"
	def REDIRECT_URI = "http://localhost:8080/TimeWorks/googlebooks/getBooks"
	def SCOPE = "https://www.googleapis.com/auth/books"
	def RESPONSE_TYPE = "code"
	def AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=${SCOPE}&response_type=${RESPONSE_TYPE}";
	def API_KEY = "AIzaSyCmCwkxWuUuSSOSneMPBA3vPF2UWNfwr_E"
	def PROTECTED_RESOURCE_URL = "https://www.googleapis.com/";
	
    def index = { 
		
		System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
		session.googleAccessToken = null
		if(session.googleAccessToken == null) {
			redirect(url:AUTHORIZE_URL)
		}
		else {
			println "redirecting to getBooks"
			redirect(action:getBooks)
		}
		
	}
	
	def getBooks = {
		if(session.googleAccessToken == null)
		{
			println 'werwerwerwerwerwere' + params['code']
			if(params['code'] != null) {
				session.verifierCodeGoogle = params['code']
				
				// get access and refresh tokens
				def http = new HTTPBuilder( 'https://accounts.google.com/o/oauth2/' )
				def postBody = [code:session.verifierCodeGoogle,client_id:CLIENT_ID,client_secret:CLIENT_SECRET,redirect_uri:REDIRECT_URI,grant_type:'authorization_code'] // will be url-encoded
				
				http.post( path: 'token', body: postBody, requestContentType: URLENC ) { resp, json ->
				  println "response status: ${resp.statusLine}"
				  assert resp.statusLine.statusCode == 200
				  session.googleRefreshToken = json.refresh_token
				  session.googleAccessToken = json.access_token
				  use(TimeCategory) {
					  session.googleExpiryDate = json.expires_in.seconds.from.now
				  }
				  println "Token expires at ${session.googleExpiryDate}"
				}
			}
			else
				redirect(action:index)
		}
				  
		println session.googleExpiryDate
		def http2 = new HTTPBuilder(PROTECTED_RESOURCE_URL)
		http2.get(	path:'books/v1/volumes', 
					contentType:JSON, 
					query:[q:'star wars', access_token:session.googleAccessToken]) 
		{ resp2, json ->
			json.items.each {
				def link = it.volumeInfo.infoLink
				render "<img src='${it.volumeInfo.imageLinks.smallThumbnail}' border=1/> <a href='${link}'>${it.volumeInfo.title} - ${it.volumeInfo.subtitle}</a><br/>"
				if(it.volumeInfo.description)
				{
					render "${it.volumeInfo.description}"
				}
				render "<hr/><br/><br/>"
			}
			render json
		}
	}
	
	def getBooksNoAuth = {
		def http2 = new HTTPBuilder(PROTECTED_RESOURCE_URL)
		http2.get(	path:'books/v1/volumes',
					contentType:JSON,
					query:[q:'star wars', key:API_KEY])
		{ resp2, json ->
			/* json.items.each {
				def link = it.volumeInfo.infoLink
				render "<img src='${it.volumeInfo.imageLinks.smallThumbnail}' border=1/> <a href='${link}'>${it.volumeInfo.title} - ${it.volumeInfo.subtitle}</a><br/>"
				if(it.volumeInfo.description)
				{
					render "${it.volumeInfo.description}"
				}
				render "<hr/><br/><br/>"
			} */
			render json.toString()
		}
		
	}
}
