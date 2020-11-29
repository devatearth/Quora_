package com.upgrad.quora.service.business;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* app imports */
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;

/* java imports */
import javax.transaction.Transactional;
import java.time.ZonedDateTime;

/** 
  * UserService.Class helps to handle all service details for users
  */

@Service
public class UserService {
  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptor;
  
  @Transactional
  public UserEntity performSignUp(UserEntity newUser) throws SignUpRestrictedException {
    return userDao.RegisterUser(newUser);
  }
  
  /* user sign in service */
  @Transactional
  public UserAuthEntity performSignIn(String userName, String passWord) throws AuthenticationFailedException {
    /* first check if the user name is registered or not */
    UserEntity user = userDao.getUserByUserName(userName);
    if (user == null) {
      throw new AuthenticationFailedException("ATH-001", "This username does not exist");
    }
    
    /* if you are here, it means that we have a registered user name with us in the database */
    String hashedPassword = cryptor.encrypt(passWord.toCharArray(), user.getSalt());
    if (user.getPassword().equals(hashedPassword)) {
      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(8);
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(hashedPassword);

      /* build the user auth token object */
      UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
      userAuthTokenEntity.setUser(user);
      userAuthTokenEntity.setUuid(user.getUuid());
      userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
      userAuthTokenEntity.setLoginAt(now);
      userAuthTokenEntity.setExpiresAt(expiresAt);
      
      userDao.createAuthToken(userAuthTokenEntity);
      userDao.updateUser(user);
      return userAuthTokenEntity;
    }
    else{
      throw new AuthenticationFailedException("ATH-002","Password failed");
    }
  }

  /* user sign out service */
  @Transactional
  public UserEntity performSignOut(String userAuthToken) throws SignOutRestrictedException {
    UserAuthEntity userAuthEntity = userDao.findUserByThisAuthToken(userAuthToken);
    if (userAuthEntity == null) {
      throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
    }
    else {
      userAuthEntity.setLogoutAt(ZonedDateTime.now());
      userDao.updateUser(userAuthEntity.getUser());
      return userAuthEntity.getUser();
    }
  }
  
  /* here we would be generating the salt and password enc only if the credentials are fresh */
  public String[] performValidate(String userName, String email, String password) 
  throws SignUpRestrictedException {
    /* if the username provided already exists in the current database */
    if(userDao.getUserByUserName(userName) != null){
      throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
    }
    /* if the email Id provided by the user already exists in the current database */
    else if(userDao.getUserByEmail(email) != null){
      throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
    }
    else {
      char[] userPassword = password.toCharArray();
      String[] encrytedStuff = cryptor.encrypt(userPassword);
      return encrytedStuff;
    }
  }
}