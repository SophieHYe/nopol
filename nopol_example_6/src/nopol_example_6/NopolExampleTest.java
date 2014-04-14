package nopol_example_6;

import static org.junit.Assert.*;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1(){
		NopolExample ex = new NopolExample();
		assertFalse(ex.canBeDividedby3(0));
	}
	@Test
	public void test2(){
		NopolExample ex = new NopolExample();
		assertFalse(ex.canBeDividedby3(00000));
	}
	@Test
	public void test3(){
		NopolExample ex = new NopolExample();
		assertTrue(ex.canBeDividedby3(3));
	}
	@Test
	public void test4(){
		NopolExample ex = new NopolExample();
		assertTrue(ex.canBeDividedby3(333));
	}
	@Test
	public void test5(){
		NopolExample ex = new NopolExample();
		assertTrue(ex.canBeDividedby3(81));
	}
	@Test
	public void test6(){
		NopolExample ex = new NopolExample();
		assertTrue(ex.canBeDividedby3(-15));
	}
	@Test
	public void test7(){
		NopolExample ex = new NopolExample();
		assertTrue(ex.canBeDividedby3(-150));
	}
	@Test
	public void test8(){
		NopolExample ex = new NopolExample();
		assertFalse(ex.canBeDividedby3(-14));
	}
	@Test
	public void test9(){
		NopolExample ex = new NopolExample();
		assertFalse(ex.canBeDividedby3(-22));
	}
}
