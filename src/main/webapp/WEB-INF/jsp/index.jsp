<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Users</title></head>
<body>

<h2>Add User</h2>
<form action="add" method="post">
    Name: <input type="text" name="name"/>
    <button type="submit">Add</button>
</form>

<h2>Users List</h2>
<ul>
<% java.util.List users = (java.util.List) request.getAttribute("users");
   for(Object u : users){ %>
   <li><%= ((com.example.mvcjsp.model.User)u).getName() %></li>
<% } %>
</ul>

</body>
</html>
