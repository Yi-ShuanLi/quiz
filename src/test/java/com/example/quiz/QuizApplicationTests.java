package com.example.quiz;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuizApplicationTests {

	@Test
	public void mapTest() {
		Map <String,Integer> answerMap=new HashMap <>();
		answerMap.put("A", 1);
		answerMap.put("A", 2);
		System.out.println(answerMap.toString());
	}

}
