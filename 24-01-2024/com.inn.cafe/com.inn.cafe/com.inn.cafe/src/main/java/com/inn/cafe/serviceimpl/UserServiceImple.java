package com.inn.cafe.serviceimpl;

import com.inn.cafe.Constant.CafeContstants;
import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.jwt.CustomerUsersDetailsService;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.jwt.JwtUtil;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtil;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImple implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        log.info("Inside singUp {}", requestMap);
        try {
            if (validateSingUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtil.getResponseEntity("SuccessFull Ragister", HttpStatus.OK);
                } else {
                    return CafeUtil.getResponseEntity("Email Alredy Exits.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtil.getResponseEntity(CafeContstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSingUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
                && requestMap.containsKey("password")) {
            return true;
        } else {
            return false;
        }
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }


    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if(customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""
                            +jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                            customerUsersDetailsService.getUserDetail().getRole()) + "\"}"
                            ,HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approvel."+"\"}"
                            ,HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bad Credential."+"\"}"
                ,HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new  ResponseEntity<> (new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin())
            {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty())
                {
                    userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                    return CafeUtil.getResponseEntity("User Status update succssfully.",HttpStatus.OK);
                }
                else
                {
                    return CafeUtil.getResponseEntity("User id dosen't not Exits.",HttpStatus.OK);
                }
            }
            else
            {
                return CafeUtil.getResponseEntity(CafeContstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved", "User :- " + user + "\n ia approved by \n ADMIN:-"+ jwtFilter.getCurrentUser(),allAdmin);
        }
        else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Diseble", "User :- " + user + "\n ia Diseble by \n ADMIN:-"+ jwtFilter.getCurrentUser(),allAdmin);
        }
    }

}

