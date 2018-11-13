package example;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class HelloWorld  extends ActionSupport  {
    public String execute() throws Exception {
        String str = "error";     
        HttpServletRequest request = ServletActionContext.getRequest();
        String passWord = request.getParameter("password");
        if(passWord.equals("admin")){
            str =  "success";
        }
        return str;
    }
}