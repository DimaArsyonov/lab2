package lab2_maven.lab2_maven;



/**
 * Класс EquationParse, предназначенный для разбора выражения и вычисления его значения
 * @author Арсёнов Дмитрий, 7 группа, ММИО
 * @version 2.4.589(версия 1.0 была изъята из публичного доступа в связи с нарушением авторских прав :))
 */


public class EquationParse{
	
	private enum OPERATOR{ PLUS, MINUS, MULTIPLY, DIVIDE, LEFT_BRACKET, RIGHT_BRACKET, 
		SIN, COS, TAN,RANDOM,SQRT,POW,X,NUMBER, UNARY_MINUS, END }  /**
		 															 *переычисление констант-действий
		 															 *порядок важен @see Node parse3()
		 															 *используется верхний регистр при перечислении
		 															 *для работы оператора switch и OPERATOR.valueof()
		 															 */
	private Node root=null;
	private byte[] expression;  //выражение, тип byte выбран для работы getBytes()
	private double tokenValue;
	private OPERATOR operator;          
	private int position;
	private double[] argument;
  	private int arguments;
  
	private class Node{    //класс, реализующий двухсвязный список для действий с выражением
		OPERATOR operator; 
		double value;
		Node left,right;
	  
		private void init(OPERATOR operator,double value, Node left){
			this.operator=operator;
			this.value = value; 
			this.left = left; 
		}
		
		Node(OPERATOR operator, Node left){
			init(operator,0, left);
		}
		
		Node(OPERATOR operator){
			init(operator,0,null);
		}
		
		Node(OPERATOR operator,double value){
			init(operator,value, null);
		}
		
		double calculate() throws Exception{             //метод calculate, выполняющий работу операторов, используя метод switch
			
			switch(operator){                   
	  	
			case NUMBER:
				return value;
	
			case PLUS:
				return left.calculate()+right.calculate();

			case MINUS:
				return left.calculate()-right.calculate();

			case MULTIPLY:
				return left.calculate()*right.calculate();

			case DIVIDE:
				return left.calculate()/right.calculate();

			case UNARY_MINUS:
				return -left.calculate();

			case SIN:
				return Math.sin(left.calculate());

			case COS:
				return Math.cos(left.calculate());

			case TAN:
				return Math.tan(left.calculate());
      
			case RANDOM:
				return Math.random();

			case SQRT:
				return Math.sqrt(left.calculate());
      	
			case POW:
				return Math.pow(left.calculate(),right.calculate());
        
			case X:
				return argument[(int)value];
	      	
			default:
				throw new Exception("Node.calculate error");
	  		}
		}	  
	}
	
	private boolean isLetter() { 
		return Character.isLetter(expression[position]);
	}
	  
	private boolean isDigit() { 
		return Character.isDigit(expression[position]); 
	}
	  
	private boolean isPoint(){ 
		return expression[position]=='.'; 
	}
	 
	private boolean isFunctionSymbol(){
		byte c=expression[position];
	 	return Character.isLetterOrDigit(c); 
	}
	
	private void getToken() throws Exception{   //метод getToken, определяющий operator посимвольно из выражения и проверяющий правильно ли записано выражение
		int i;
		
		if(position==expression.length-1){
			operator = OPERATOR.END;
		}
		else if( (i="+-*/(),".indexOf(expression[position]))!=-1 ){
			position++;
			operator=OPERATOR.values()[i];
		}
		else if(isLetter()){
			for(i=position++;isFunctionSymbol();position++);
			String token =new String(expression,i,position-i);
     
			try{
				if(token.charAt(0)=='X' && token.length()==1){
					throw new Exception("unknown keyword");
				}
				else if(token.charAt(0)=='X' && token.length()>1  && Character.isDigit(token.charAt(1))){
					i=Integer.parseInt(token.substring(1));
					if(i<0){
						throw new Exception("index of 'x' should be non-negative integer number");
					}
					if(arguments<i+1){
						arguments=i+1;
					}
					operator=OPERATOR.X;
					tokenValue=i;
				}
				else{
					operator=OPERATOR.valueOf(token);
					i=operator.ordinal();
					if( i<OPERATOR.SIN.ordinal()){
						throw new IllegalArgumentException();
					}
				}		  	
			}
			catch (IllegalArgumentException _ex){
				try{
					operator=OPERATOR.NUMBER;
				}
				catch (IllegalArgumentException ex){
					throw new Exception("unknown keyword");
				}
			}
		}
		else if(isDigit() || isPoint()){
			for(i=position++ ; isDigit() || isPoint(); position++);
			tokenValue =Double.parseDouble(new String(expression,i,position-i));
			operator = OPERATOR.NUMBER;
		}
		else{
			throw new Exception("unknown symbol");
		}

	}      

	public boolean compile(String expression) throws Exception{  //метод компиляции входящего выражения, проверка, обработается ли оно вообще
		position=0;
		arguments=0;
		String s = expression.toUpperCase();  //приводим выражение к виду, удобному для обработки методом calculate
		
		String from[]={" ","\t"};
		for(int i=0;i<from.length;i++){
			s=s.replace(from[i], "");
		}
		this.expression=(s+'\0').getBytes();
		
		getToken();
		if(operator==OPERATOR.END){
			throw new Exception("unexpected end of expression");
		}
		root = parse();
		if(operator!=OPERATOR.END){
			throw new Exception("end of expression expected");
		}

		return true;
	}

	private Node parse() throws Exception{              //методы парсинга выражения, использующие метод рекурсивного спуска
		Node node = parse1();
		while(operator==OPERATOR.PLUS || operator==OPERATOR.MINUS){
      node = new Node(operator, node);
    	getToken();
    	if(operator==OPERATOR.PLUS || operator==OPERATOR.MINUS){
    		throw new Exception("two operators in a row");
    	}
    	node.right=parse1();
		}
		return node;
	}
   
	private Node parse1() throws Exception{
		Node node = parse2();
		while(operator==OPERATOR.MULTIPLY || operator==OPERATOR.DIVIDE){
      node = new Node(operator, node);
			getToken();
    	if(operator==OPERATOR.PLUS || operator==OPERATOR.MINUS){
    		throw new Exception("two operators in a row");
    	}
			node.right=parse2();
		}
		return node;
	}

	private Node parse2() throws Exception{
		Node node;
		
		if(operator==OPERATOR.MINUS){
			getToken();
      node = new Node(OPERATOR.UNARY_MINUS, parse3());
		}
		else{
			if(operator==OPERATOR.PLUS){
				getToken();
			}
			node = parse3();
		}
		return node;      
	}

	private Node parse3() throws Exception{
		Node node;
		OPERATOR open;
		
		if(operator.ordinal() >= OPERATOR.SIN.ordinal() && operator.ordinal()<=OPERATOR.POW.ordinal()){
			int arguments;
  		if( operator.ordinal() >= OPERATOR.POW.ordinal()&& operator.ordinal()<OPERATOR.X.ordinal()){
  			arguments=2;
  		}
  		else{
  			arguments=operator==OPERATOR.RANDOM?0:1;
  		}
  			
  		node = new Node(operator);
  
  		getToken();
  		open = operator;
  		if(operator!=OPERATOR.LEFT_BRACKET){
  			throw new Exception("open bracket expected");
  		}
  		getToken();
      
  		if(arguments>0){
  			node.left = parse();
	      
	  		if( arguments==2 ){
	        
	        getToken();
	        node.right = parse();
	  		}
  		}
  		checkBracketBalance(open);
		}
		else{
			switch(operator){            //самые первые по очереди выполнения действия, находящиеся в самой глубине методов, согласно рекурсивному спуску

	  	case X:
	  	case NUMBER:
	    	node = new Node(operator,tokenValue);
	      break;
	      
	    case LEFT_BRACKET:
	    	open=operator;
	    	getToken();
	      node = parse();
	      checkBracketBalance(open);
	      break;

	    default:
	      throw new Exception("unexpected operator");
			}

		}
		getToken();
		return node;
	}

	private void checkBracketBalance(OPERATOR open) throws Exception {     
		if(open==OPERATOR.LEFT_BRACKET && operator!=OPERATOR.RIGHT_BRACKET){
			throw new Exception("close bracket expected");
		}
	}

	public double calculate(double[] x) throws Exception{     //метод определения переменных
		this.argument=x;
		return calculate(); 
	}
  
	public double calculate() throws Exception{           //метод calculate для выражений с переменными
		if(root==null){
			throw new Exception("using of calculate() without compile()");
		}
		int length = argument==null?0:argument.length;
		if(length!=arguments){
			throw new Exception("invalid number of expression arguments");
		}
		return root.calculate();
	}
  
	public static double calculate(String s) throws Exception {     
		EquationParse estimator=new EquationParse(); 
		estimator.compile(s);
		estimator.argument=null;
		return estimator.calculate();
	}
	
};