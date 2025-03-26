package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Search;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Search entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query("select search from Search search where search.user.login = ?#{authentication.name}")
    List<Search> findByUserIsCurrentUser();
}
