package com.mis.controllers;
import com.mis.CustException.CustException;
import com.mis.bookingmodels.User;
import com.mis.bookingservices.UserService;
import com.mis.customclasses.PassWord;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/adduser")
    public String adduser(@RequestBody  User user) throws CustException, MessagingException {
        user.setUserType(0);
        userService.add(user);
        return "Added User Successfully";
    }
    @PostMapping("/addAdmin")
    public String addAdmin(@RequestBody  User user) throws CustException, MessagingException {
        user.setUserType(1);
        userService.add(user);
        return "Added User Successfully";
    }
    @GetMapping("/login")
    public String login(@RequestBody User user) throws CustException {
        if(userService.verifyUser(user))
            return "Welcome";
        else
            return "Invalid Id/Password";
    }
    @PostMapping("/forgotpassword")
    public String forgotPassword(@RequestBody User user)throws CustException{
        userService.forgotPassword(user);
        return "We have sent a OTP(One Time Password) to your registered mail id";
    }
    @PutMapping("/resetPassword")
    public String resetPassword(@RequestBody PassWord passWord) throws CustException {
        userService.resetPwd(passWord);
        return "Password Reset Successfully";
    }
}
