package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
    
	private static final String PATH = "/";
	
	@RequestMapping(value=PATH,method = RequestMethod.GET)
    public String homepage(){
        //return "index.html";
		return "Hello World!";
    }
}
