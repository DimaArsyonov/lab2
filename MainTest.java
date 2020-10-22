package lab2_maven.lab2_maven;

import junit.framework.TestCase;



import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MainTest extends TestCase {
	
	private String expression1[] = {"1+2+3"} ;
	
	private double result;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void globalSetUp() {
		System.out.println("Initial setup...");
		System.out.println("Code executes only once");
	}
	
	@Test
	public void IsRightCalculate() {
		for(String s:expression1){
			try {
				result = EquationParse.calculate(s);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());   
			}
		}
		assertEquals(result, 6);
	}
}