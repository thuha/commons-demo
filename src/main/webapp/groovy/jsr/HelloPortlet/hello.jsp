<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<b>Send a greeting to Sau, she will be very happy :) </b>
</p>
<form method="post" action="<portlet:actionURL> 
								<portlet:param name="javax.portlet.action" value="send"/>
							</portlet:actionURL>
						">					
	<textarea rows="5" cols="50" name="greeting"></textarea>
	<input type="submit" value="Send" />
</form>