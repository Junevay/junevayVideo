package com.junevay.controller.interceptor;

import com.junevay.utils.JSONResult;
import com.junevay.utils.JsonUtils;
import com.junevay.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: junevay
 * @Date: 2019/1/12 14:15
 */
public class MiniInterceptor implements HandlerInterceptor{

    @Autowired
    private RedisOperator redisOperator;

    private static final String USER_REDIS_SESSION="USER_REDIS_SESSION";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String headerUserId = httpServletRequest.getHeader("headerUserId");
        String headerUserToken = httpServletRequest.getHeader("headerUserToken");

        if(StringUtils.isNoneBlank(headerUserId)&&StringUtils.isNoneBlank(headerUserToken)){
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION +":"+ headerUserId);
            if(StringUtils.isEmpty(uniqueToken)&&StringUtils.isBlank(uniqueToken)){

                returnErrorRsponse(httpServletResponse,JSONResult.errorTokenMsg("请先登录..."));
                return false;
            }else{
                if(!headerUserToken.equals(uniqueToken)){

                    returnErrorRsponse(httpServletResponse,JSONResult.errorTokenMsg("账号在其他地方登录！"));
                    return false;
                }
            }

        }else{
                    returnErrorRsponse(httpServletResponse,JSONResult.errorTokenMsg("请先登录..."));
                    return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

   public void  returnErrorRsponse(HttpServletResponse httpServletResponse,JSONResult jsonResult) throws IOException {

        OutputStream outputStream=null;

       try {
           httpServletResponse.setCharacterEncoding("utf-8");
           httpServletResponse.setContentType("json/text");
           outputStream=httpServletResponse.getOutputStream();
           outputStream.write(JsonUtils.objectToJson(jsonResult).getBytes("utf-8"));

       } finally {
           if(outputStream!=null){
               outputStream.close();
           }
       }
   }
}
