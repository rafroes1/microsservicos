package com.store.user.service.impl;

import com.store.user.domain.User;
import com.store.user.repository.UserRepository;
import com.store.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericServiceImpl<User, Long, UserRepository> implements UserService {
    public UserServiceImpl(UserRepository repository){ super(repository); }
}
