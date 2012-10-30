<%@page import="java.util.ArrayList"%>
<%
	ArrayList<String> grs =	(ArrayList<String>) renderRequest.getPortletSession.getAttribute("greetings");	
%>

Sau has <%= grs.size();%> greeting card(s):
<%for (String gr : greetings) < %>
   	<%= gr+"\n" %>
   	
   	