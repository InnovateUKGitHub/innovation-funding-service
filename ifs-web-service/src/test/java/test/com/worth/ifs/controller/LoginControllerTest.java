package test.com.worth.ifs.controller; 

import com.worth.ifs.controller.LoginController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

/** 
* LoginController Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 27, 2015</pre> 
* @version 1.0 
*/ 
public class LoginControllerTest { 

@Before
public void before() throws Exception {

} 

@After
public void after() throws Exception {
} 

/** 
* 
* Method: login(@RequestParam(value="name", required=false, defaultValue="World..") String name, Model model) 
* 
*/ 
@Test
public void loginShouldReturnLogin()  throws Exception {
    LoginController loginController = new LoginController();

    Assert.assertEquals("loginController /login request should return login", "login", loginController.login(null,null));
}

} 
