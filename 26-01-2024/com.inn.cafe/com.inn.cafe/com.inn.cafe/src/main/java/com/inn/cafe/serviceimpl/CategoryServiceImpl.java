package com.inn.cafe.serviceimpl;

import com.google.common.base.Strings;
import com.inn.cafe.Constant.CafeContstants;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap, false))
                {
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    return CafeUtil.getResponseEntity("Category Add Successfully..",HttpStatus.OK);
                }

            }
            else {
                return CafeUtil.getResponseEntity(CafeContstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name"))
        {
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId)
            {
                return true;
            }
        }
        return false;
    }
    private Category getCategoryFromMap(Map<String,String> requestMap,Boolean isAdd){
        Category category = new Category();
        if (isAdd)
        {
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside If");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin())
            {
                if(validateCategoryMap(requestMap,true))
                {
                    Optional optional=categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty())
                    {
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return CafeUtil.getResponseEntity("Category Update Successfully..",HttpStatus.OK);
                    }
                    else {
                        return CafeUtil.getResponseEntity("Category id dose not exits..",HttpStatus.OK);
                    }
                }
                return CafeUtil.getResponseEntity(CafeContstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
            else {
                return CafeUtil.getResponseEntity(CafeContstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeContstants.SOMTHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
