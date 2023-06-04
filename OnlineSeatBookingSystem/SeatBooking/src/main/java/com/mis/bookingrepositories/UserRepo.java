package com.mis.bookingrepositories;

import com.mis.bookingmodels.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,String> {

    @Modifying
    @Transactional
    @Query(value = "update User u set u.password=?1 where u.userId=?2")
    void updatepassword(String password, String userId);
    @Query(value = "select u from User u where u.userId = ?1 and u.password = ?2")
    Optional<User> getuser(String userId, String password);
}
