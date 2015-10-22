package pro.khodoian.services;

import pro.khodoian.models.User;

/**
 * Created by eduardkhodoyan on 10/21/15.
 */
public interface UserService {
    User getUser(long id);
    User getUser(String username);
}
