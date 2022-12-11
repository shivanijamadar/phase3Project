package restAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class EndToEndTest {
	RequestSpecification request;
	Response response;
	String baseURI = "http://localhost:3000";
	String responseBody;
	
	@Test
	public void test1(){
		//Get All Employees
		response = GetAllEmployee();
		Assert.assertEquals(200, response.getStatusCode());
		
		//Create an Employee and Fetch Id
		response = CreateEmp("John", 8000);
		Assert.assertEquals(201, response.getStatusCode());
		JsonPath jpath = response.jsonPath();
		int id = jpath.get("id");
		
		//Get Employee created and Validate Name&Status 
		response = GetSingleEmp(id);
		responseBody = response.getBody().asString();	
		jpath = response.jsonPath();
		String name = jpath.get("name");
		Assert.assertEquals(name, "John");
		Assert.assertEquals(200, response.getStatusCode());
		
		//Update Employee created and Validate status
		response = UpdateEmp("Smith", id);
		Assert.assertEquals(200, response.getStatusCode());
		
		//Get Employee updated and Validate Name&Status 
		response = GetSingleEmp(id);
		responseBody = response.getBody().asString();		
		jpath = response.jsonPath();
		name = jpath.get("name");
		Assert.assertEquals(name, "Smith");
		Assert.assertEquals(200, response.getStatusCode());
		
		//Delete employee created and Validate Name&Status 
		response = DeleteEmp(id);
		Assert.assertEquals(200, response.getStatusCode());
		
		//Get Employee deleted and Validate Status 
		response = GetSingleEmp(id);
		Assert.assertEquals(404, response.getStatusCode());
		
		//Get all Employees and Validate employee deleted
		response = GetAllEmployee();
		responseBody = response.getBody().asString();		
		jpath = response.jsonPath();
		List<String> names = jpath.get("name");
		for(int i=0;i<names.size();i++) {
			Assert.assertNotEquals(names.get(i), "Smith");
		}
	}

	
	public Response GetAllEmployee(){
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		response = request.get("employees");
		return response ;
	}
	
	public Response GetSingleEmp(int empId)	{
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
//		response = request.param("id", empId).get("employees");
		response = request.get("employees/"+empId);
		return response ;
	}

	public Response CreateEmp(String name,int salary)	{
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		Map<String, Object> mapObj = new HashMap<String, Object>();
		
		mapObj.put("name", name);
		mapObj.put("salary", salary);

		response = request
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(mapObj)
				.post("employees/create");
		
		return response;
	}
	
	public Response UpdateEmp(String name, int empId)	{
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		Map<String, Object> mapObj = new HashMap<String, Object>();
		
		mapObj.put("name", name);

		response = request
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(mapObj)
				.patch("employees/"+empId);
		
		return response;
	}

	public Response DeleteEmp(int empId)	{
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		 response = request.delete("employees/"+empId);
		
		return response;
	}
	
}
