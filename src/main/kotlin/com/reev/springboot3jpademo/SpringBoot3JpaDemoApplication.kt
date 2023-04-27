package com.reev.springboot3jpademo

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.util.*
import kotlin.reflect.KClass

@SpringBootApplication
class SpringBoot3JpaDemoApplication

fun main(args: Array<String>) {
	runApplication<SpringBoot3JpaDemoApplication>(*args)
}

@RestController
class DemoController(val repo: DemoDbCustomerRepo, private val userRepository: DbUserRepository) {

	@GetMapping("/{email}")
	fun demo(@PathVariable email: String): String {

//		userRepository.findAll()
		return "demo ct ${repo.findCustomerByUserEmail(email).first().name}"
	}
}

interface DemoDbCustomerRepo : JpaRepository<DbCustomer, String> {

	@Query("SELECT cu FROM DbCustomer cu " +
			"JOIN DbCustomerUser cuu on cu.id = cuu.customerId " +
			"JOIN DbUser uu on cuu.userId = uu.id " +
			"WHERE uu.email LIKE %:email% and cu.hasRenewalProcess = FALSE ")
//			"WHERE uu.email LIKE %:email% and cu.hasRenewalProcess is FALSE ")  // this one does not work
	fun findCustomerByUserEmail(email: String) : List<DbCustomer>
}

interface DbCustomerCustomRepo {
	fun expectOne(email: String): List<DbCustomer>
}

class DbCustomerCustomImpl(
	@Lazy val repo: DemoDbCustomerRepo,
	entityManager: EntityManager
) : DbCustomerCustomRepo, BaseRepository<DbCustomer, UUID>(DbCustomer::class, entityManager) {
	override fun expectOne(email: String): List<DbCustomer> {
		throw RuntimeException("testing")
	}
}

abstract class BaseRepository<T : Any, ID : Serializable>(
	protected val domainClass: KClass<T>,
	protected val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(domainClass.java, entityManager) {

}


@Entity
@Table(name = "\"customer\"")
class DbCustomer {
	@Id
	lateinit var id: String
	lateinit var name: String
	@Column(name = "has_renewal_process")
	var hasRenewalProcess: Boolean = true
}


@Entity
@Table(name = "\"user\"")
data class DbUser(
	@Id
	var id: String,
	var email: String
)



@Entity
@Table(name = "\"customer_user\"")
data class DbCustomerUser(
	@Id
	var id: String,
	@Column(name = "customer_id", insertable = false, updatable = false)
	var customerId: String,
	@Column(name = "user_id", insertable = false, updatable = false)
	var userId: String,
)