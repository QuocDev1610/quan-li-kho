package inventory.security;

import com.sun.net.httpserver.HttpExchange;
import inventory.dao.entity.Auth;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import inventory.utils.constant;
import org.aspectj.apache.bcel.classfile.Constant;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;
@Component
public class FilterSystem implements HandlerInterceptor {
Logger logger = Logger.getLogger(FilterSystem.class.getName());


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object Hanlder) throws IOException {
       Logger logger = Logger.getLogger(FilterSystem.class.getName());
       User user = (User)request.getSession().getAttribute(constant.userInf) ;

       if(user==null){
           response.sendRedirect(request.getContextPath()+"/login");
       }
     else {
         String url = request.getServletPath();
           if (url.equals("/") || url.equals("/home") || url.equals("/dashboard")) {
               return true; // Trả về true luôn, mở cửa cho qua!
           }
         if (!hasPermission(url, user)) {
             response.sendRedirect(request.getContextPath() + "/access-denied");
             return false;
         }

     }
        return true;
    }
    public boolean hasPermission(String url, User user) {
        UserRole userRole =(UserRole) user.getUserRoles().iterator().next();
        Set<Auth> auth= userRole.getRole().getAuths();
        for (Auth a : auth) {
            if(url.contains(a.getMenu().getUrl())) {
                return a.getPermission()==1;
            }
        } return  false;

    }

}
