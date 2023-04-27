package com.reev.springboot3jpademo;

import org.springframework.data.jpa.repository.JpaRepository

interface DbUserRepository : JpaRepository<DbUser, String> {
}