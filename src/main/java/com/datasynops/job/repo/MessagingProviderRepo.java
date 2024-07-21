package com.datasynops.job.repo;

import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.MessagingProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessagingProviderRepo extends JpaRepository<MessagingProvider, Long> {

     @Query(
             value = "SELECT * FROM MESSAGING_PROVIDER u WHERE u.STATUS = 'ACTIVE' AND provider_name ='rapbooster' ",
             nativeQuery = true)
     public Optional<MessagingProvider> findOneByStatus();

     @Query(
             value = "SELECT count(*) FROM MESSAGING_PROVIDER u WHERE u.STATUS = 'ACTIVE' AND provider_name ='rapbooster' ",
             nativeQuery = true)
     public int findCountByStatus();

     @Query(
             value = "SELECT * \n" +
                     "FROM public.MESSAGING_PROVIDER;",
             nativeQuery = true)
     List<MessagingProvider> findAll();

     @Query(
             value = "SELECT * FROM MESSAGING_PROVIDER u WHERE u.STATUS = 'ACTIVE' AND provider_name ='rapbooster' ",
             nativeQuery = true)
     List<MessagingProvider> findAllByStatus();
}
