package com.inn.cafe.restimpl;

import com.inn.cafe.Constant.CafeContstants;
import com.inn.cafe.rest.BillRest;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BillRestImple implements BillRest {

    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
