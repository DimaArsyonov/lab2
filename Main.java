package lab2_maven.lab2_maven;

/**
 * Класс Main, предназначенный ввода выражений и получения на экран результатов
 * @author Арсёнов Дмитрий, 7 группа, ММИО
 * @version 2.4.589(версия 1.0 была изъята из публичного доступа в связи с нарушением авторских прав :))
 */
import java.util.Arrays;



public class Main {

	public static void main(String[] args) throws Exception{
		String expression[]={"cos(3.14)+sin(3.14)","(1+2)+3*sqrt(4)"};       //ввод выражений
		for(String s:expression){
			System.out.print(s+"=");
			try {
				System.out.println(EquationParse.calculate(s));   //вывод результата
			} 
			catch (Exception e) {
				System.out.println(e.getMessage());         //или ошибок
			}
		}

    EquationParse estimator=new EquationParse();
    expression=new String[]{"3*x0+2*x1*x0","6*x0"};       //ввод выражений с переменными
    double[][]values={ {3,6}, {8,6} };     //ввод значений переменных
    double[]v;
    int i=0;
    for(String s:expression){
    	v=values[i++];
    	System.out.print(s+","+Arrays.toString(v)+"=");
     	try {
     		estimator.compile(s);
     		System.out.println(estimator.calculate(v));   //вывод результата
     	} 
     	catch (Exception e) {
     		System.out.println(e.getMessage());  //или ошибок
     	}
    }
}
	}