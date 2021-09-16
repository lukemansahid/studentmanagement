package com.lukish.studentmanagement.service.impl;

import com.lukish.studentmanagement.domain.User;
import com.lukish.studentmanagement.domain.UserPrincipal;
import com.lukish.studentmanagement.domain.dao.UserRepository;
import com.lukish.studentmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.lukish.studentmanagement.constant.UserImplConstant.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl  implements UserService, UserDetailsService {

//    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User  user = userRepository.findUserByUserName(username);
        if (user == null){
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }else {

            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }

    }

}
