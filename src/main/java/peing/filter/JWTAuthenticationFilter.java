package peing.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.support.WebApplicationContextUtils;
import peing.constants.SecurityConstants;
import peing.exception.CaptchaException;
import peing.pojo.JwtUser;
import peing.utils.AesUtil;
import peing.vo.LoginVo;
import peing.pojo.ResponseJson;
import peing.service.SysCaptchaService;
import peing.utils.JwtTokenUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 如果用户名和密码正确，那么过滤器将创建一个JWT Token 并在HTTP Response 的header中返回它，格式：token: "Bearer +具体token值"
 */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private SysCaptchaService sysCaptchaService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        // 设置URL，以确定是否需要身份验证
        super.setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 从输入流中获取到登录的信息
            LoginVo loginUser = objectMapper.readValue(request.getInputStream(), LoginVo.class);

            if(sysCaptchaService == null){
                ServletContext context = request.getServletContext();
                ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
                sysCaptchaService = ctx.getBean(SysCaptchaService.class);
            }

            if(!sysCaptchaService.validate(loginUser.getUuid(),loginUser.getCode())){
                throw new CaptchaException("验证码错误");
            }
            try {
                String decryptPwd = AesUtil.aesDecrypt(loginUser.getPassword());
                loginUser.setPassword(decryptPwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 账号密码校验交给authenticationManager
             */
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    loginUser.getUsername(), loginUser.getPassword());
            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 如果验证成功，就生成token并返回
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) {

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        List<String> roles = jwtUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // 创建 Token
        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), roles);
        // Http Response Header 中返回 Token
        response.setHeader(SecurityConstants.TOKEN_HEADER, token);
        try {
            response.setContentType("application/json");
            response.getWriter().print(JSONObject.toJSON(new ResponseJson()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        if(authenticationException instanceof DisabledException){
            authenticationException = new DisabledException("您的账户未激活");
        }
        if(authenticationException instanceof LockedException){
            authenticationException = new LockedException("您的账户已经被封禁");
        }

        if(authenticationException instanceof BadCredentialsException || authenticationException instanceof InternalAuthenticationServiceException){
            authenticationException = new LockedException("用户名或密码错误");
        }
//        response.sendError(HttpServletResponse.SC_OK, authenticationException.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(new ResponseJson<Object>(201,authenticationException.getMessage(),null)));

    }
}
