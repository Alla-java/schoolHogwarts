package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;

@SpringBootApplication(scanBasePackages = "ru.hogwarts.school")
class SchoolApplicationTests {

	//Проверка, что studentController проинициализирован
	@Test
	void contextLoads() throws Exception {
		Assertions.assertThat(studentController).isNotNull();
	}

	//Проверка, что facultyController проинициализирован
	@Test
	void contextLoads2() throws Exception {
		Assertions.assertThat(facultyController).isNotNull();
	}


}
