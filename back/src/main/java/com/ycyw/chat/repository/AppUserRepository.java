package com.ycyw.chat.repository;

import com.ycyw.chat.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

/** Accès à la table {@code app_user}. Spring Data génère l'implémentation. */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {}
