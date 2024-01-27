package com.inn.cafe.restimpl;

import com.inn.cafe.Constant.CafeContstants;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtil;
import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
public class UserRestImple implements UserRest {

    @Autowired
    UserService userService;
    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        try
        {
            return userService.singUp(requestMap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            return userService.login(requestMap);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            return userService.getAllUser();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>( new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            return userService.update(requestMap);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
