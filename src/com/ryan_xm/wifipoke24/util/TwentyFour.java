package com.ryan_xm.wifipoke24.util;

import java.util.HashMap;

public class TwentyFour {

	private static int[] aArray = new int[2];
    private static int[] bArray = new int[2];
    private static int[] cArray = new int[2];
    private static int[] dArray = new int[2];
    
    
    public static int dispose(int a, int b, int c, int d){
        aArray[0] = a;
        aArray[1] = 1;
        bArray[0] = b;
        bArray[1] = 1;
        cArray[0] = c;
        cArray[1] = 1;
        dArray[0] = d;
        dArray[1] = 1;
        return core(aArray, bArray, cArray, dArray);
    }
    
    public static String disposeStr(int a, int b, int c, int d){
        aArray[0] = a;
        aArray[1] = 1;
        bArray[0] = b;
        bArray[1] = 1;
        cArray[0] = c;
        cArray[1] = 1;
        dArray[0] = d;
        dArray[1] = 1;
        return coreStr(aArray, bArray, cArray, dArray);
    }
    
    /**
     * 加
     */
    public static int[] plus(int[] a, int[] b){
        
        if(0 == a[1]){
            return new int[]{0,0};
        }
        if(0 == b[1]){
            return new int[]{0,0};
        }
        int denominator = leaseCommonMultiple(a[1],b[1]);
        
        return new int[]{denominator/a[1]*a[0] + denominator/b[1]*b[0] ,denominator};
    }
    
    /**
     * 减
     * @param a
     * @param b
     * @return
     */
    public static int[] reduce(int[] a, int[] b){
        if(0 == a[1]){
            return new int[]{0,0};
        }
        if(0 == b[1]){
            return new int[]{0,0};
        }
        int denominator = leaseCommonMultiple(a[1],b[1]);
        
        return new int[]{denominator/a[1]*a[0] - denominator/b[1]*b[0] ,denominator};
    }
    
    /**
     * 乘
     * @param a
     * @param b
     * @return
     */
    public static int[] multiply(int[] a, int[] b){
        return new int[]{a[0] * b[0], a[1] * b[1]};
    }
    
    /**
     * 除
     * @param a
     * @param b
     * @return
     */
    public static int[] divide(int[] a, int[] b){
        return new int[]{a[0] * b[1], a[1] * b[0]};
    }
    
    /**
     * 求最小公倍数
     * @param a
     * @param b
     * @return
     */
    public static int leaseCommonMultiple(int a, int b){
        return a*b/greatestCommonDivisor(a,b);
    }
    
    /**
     * 求最大公约数
     * @param a
     * @param b
     * @return
     */
    public static int greatestCommonDivisor(int a, int b) {
        if(0 == a && 0 == b){
            return 1;
        }
        if (0 == a) {
            return b;
        }
        if (0 == b) {
            return a;
        }
        if (a > b) {
            int c = a;
            a = b;
            b = c;
        }
        for (int c = a % b; c > 0; c = a % b) {
            a = b;
            b = c;
        }
        return b;
    }
    
    /**
     * 动态加载符号数
     * @param i
     * @param a
     * @param b
     * @return
     */
    public static int[] dispose(int i, int a[], int[] b){
        if(i == 0){
            return plus(a, b);
        }else if(i == 1){
            return reduce(a, b);
        }else if(i == 2){
            return multiply(a, b);
        }else{
            return divide(a, b);
        }
    }
    
    /**
     * 动态加载计算数
     * @param i
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static int[] number(int i, int[] a, int[] b, int[] c, int[] d){
        if(i == 0){
            return a;
        }else if(i == 1){
            return b;
        }else if(i == 2){
            return c;
        }else if(i == 3){
            return d;
        }else{
            return new int[]{0,1};
        }
    }
    /**
     * 核心算法部分
     * @param a
     * @param b
     * @param c
     * @param d
     */
    private static int core(int[] a, int[] b, int[] c, int[] d) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        
        int count = 0;
        String solution = "";
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                for(int k = 0; k < 4; k++){
                    for(int m = 0; m < 4; m++){
                        for(int n = 0; n < 4; n++){
                            if(n == m)
                                continue;
                            for(int o = 0; o < 4; o++){
                                if(o == m || o == n)
                                    continue;
                                for(int p = 0; p < 4; p++){
                                    if(p == m || p == n || p == o)
                                        continue;
                                    //类型1：A(A(A(a,b),c),d)
                                    int[] result1 = dispose(k,dispose(j,dispose(i,number(m,a,b,c,d),number(n,a,b,c,d)),number(o,a,b,c,d)),number(p,a,b,c,d));
                                    //类型2：A(A(a,A(b,c)),d)
                                    int[] result2 = dispose(k,dispose(i,number(m,a,b,c,d),dispose(j,number(n,a,b,c,d),number(o,a,b,c,d))),number(p,a,b,c,d));
                                    //类型3：A(a,A(b,A(c,d)))
                                    int[] result3 = dispose(i,number(m,a,b,c,d),dispose(j,number(n,a,b,c,d),dispose(k,number(o,a,b,c,d),number(p,a,b,c,d))));
                                    //类型4：A(a,A(A(b,c),d))
                                    int[] result4 = dispose(i,number(m,a,b,c,d),dispose(k,dispose(j,number(n,a,b,c,d),number(o,a,b,c,d)),number(p,a,b,c,d)));                                   
                                    //类型5：A(A(a,b),A(c,d))
                                    int[] result5 = dispose(j,dispose(i,number(m,a,b,c,d),number(n,a,b,c,d)),dispose(k,number(o,a,b,c,d),number(p,a,b,c,d)));
                                    if (0 != result1[1]) {
                                        if (result1[0] % result1[1] == 0) {
                                            if (result1[0] / result1[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    //solution += "((" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + printNumber(o) + ")" + printSymbol(k) + printNumber(p) + "\\n\\n";
                                                    //System.out.println("((" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + printNumber(o) + ")" + printSymbol(k) + printNumber(p));
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result2[1]) {
                                        if (result2[0] % result2[1] == 0) {
                                            if (result2[0] / result2[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    //solution += "(" + printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + printNumber(o) + "))" + printSymbol(k) + printNumber(p) + "\\n\\n";
                                                    //System.out.println("(" + printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + printNumber(o) + "))" + printSymbol(k) + printNumber(p));
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result3[1]) {
                                        if (result3[0] % result3[1] == 0) {
                                            if (result3[0] / result3[1] == 24) {
                                                if ((!map.containsKey(new Integer(i))) || (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    //solution += printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + "(" + printNumber(o) +  printSymbol(k) + printNumber(p) + "))" + "\\n\\n";
                                                    //System.out.println(printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + "(" + printNumber(o) +  printSymbol(k) + printNumber(p) + "))");
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result4[1]) {
                                        if (result4[0] % result4[1] == 0) {
                                            if (result4[0] / result4[1] == 24) {
                                                if ((!map
                                                        .containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    //solution += printNumber(m) + printSymbol(i) +"((" + printNumber(n) + printSymbol(j) + printNumber(o) + ")" +  printSymbol(k) + printNumber(p) + ")" + "\\n\\n";
                                                    //System.out.println(printNumber(m) + printSymbol(i) +"((" + printNumber(n) + printSymbol(j) + printNumber(o) + ")" +  printSymbol(k) + printNumber(p) + ")");
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result5[1]) {
                                        if (result5[0] % result5[1] == 0) {
                                            if (result5[0] / result5[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    //solution += "(" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + "(" + printNumber(o) + printSymbol(k) + printNumber(p) + ")";
                                                    //System.out.println("(" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + "(" + printNumber(o) + printSymbol(k) + printNumber(p) + ")");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //总共几种答案，过滤了类似的答案 
        //System.out.println("count: " + count);
        return count;
    }
    
    
    private static String coreStr(int[] a, int[] b, int[] c, int[] d) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        
        int count = 0;
        String solution = "";
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                for(int k = 0; k < 4; k++){
                    for(int m = 0; m < 4; m++){
                        for(int n = 0; n < 4; n++){
                            if(n == m)
                                continue;
                            for(int o = 0; o < 4; o++){
                                if(o == m || o == n)
                                    continue;
                                for(int p = 0; p < 4; p++){
                                    if(p == m || p == n || p == o)
                                        continue;
                                    //类型1：A(A(A(a,b),c),d)
                                    int[] result1 = dispose(k,dispose(j,dispose(i,number(m,a,b,c,d),number(n,a,b,c,d)),number(o,a,b,c,d)),number(p,a,b,c,d));
                                    //类型2：A(A(a,A(b,c)),d)
                                    int[] result2 = dispose(k,dispose(i,number(m,a,b,c,d),dispose(j,number(n,a,b,c,d),number(o,a,b,c,d))),number(p,a,b,c,d));
                                    //类型3：A(a,A(b,A(c,d)))
                                    int[] result3 = dispose(i,number(m,a,b,c,d),dispose(j,number(n,a,b,c,d),dispose(k,number(o,a,b,c,d),number(p,a,b,c,d))));
                                    //类型4：A(a,A(A(b,c),d))
                                    int[] result4 = dispose(i,number(m,a,b,c,d),dispose(k,dispose(j,number(n,a,b,c,d),number(o,a,b,c,d)),number(p,a,b,c,d)));                                   
                                    //类型5：A(A(a,b),A(c,d))
                                    int[] result5 = dispose(j,dispose(i,number(m,a,b,c,d),number(n,a,b,c,d)),dispose(k,number(o,a,b,c,d),number(p,a,b,c,d)));
                                    if (0 != result1[1]) {
                                        if (result1[0] % result1[1] == 0) {
                                            if (result1[0] / result1[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    solution += "((" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + printNumber(o) + ")" + printSymbol(k) + printNumber(p) + "\n\n";
                                                    //System.out.println("((" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + printNumber(o) + ")" + printSymbol(k) + printNumber(p));
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result2[1]) {
                                        if (result2[0] % result2[1] == 0) {
                                            if (result2[0] / result2[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    solution += "(" + printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + printNumber(o) + "))" + printSymbol(k) + printNumber(p) + "\n\n";
                                                    //System.out.println("(" + printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + printNumber(o) + "))" + printSymbol(k) + printNumber(p));
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result3[1]) {
                                        if (result3[0] % result3[1] == 0) {
                                            if (result3[0] / result3[1] == 24) {
                                                if ((!map.containsKey(new Integer(i))) || (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    solution += printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + "(" + printNumber(o) +  printSymbol(k) + printNumber(p) + "))" + "\n\n";
                                                    //System.out.println(printNumber(m) + printSymbol(i) +"(" + printNumber(n) + printSymbol(j) + "(" + printNumber(o) +  printSymbol(k) + printNumber(p) + "))");
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result4[1]) {
                                        if (result4[0] % result4[1] == 0) {
                                            if (result4[0] / result4[1] == 24) {
                                                if ((!map
                                                        .containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    solution += printNumber(m) + printSymbol(i) +"((" + printNumber(n) + printSymbol(j) + printNumber(o) + ")" +  printSymbol(k) + printNumber(p) + ")" + "\n\n";
                                                    //System.out.println(printNumber(m) + printSymbol(i) +"((" + printNumber(n) + printSymbol(j) + printNumber(o) + ")" +  printSymbol(k) + printNumber(p) + ")");
                                                }
                                            }
                                        }
                                    }
                                    if (0 != result5[1]) {
                                        if (result5[0] % result5[1] == 0) {
                                            if (result5[0] / result5[1] == 24) {
                                                if ((!map.containsKey(new Integer(i)))|| (!map.containsKey(new Integer(j)))|| (!map.containsKey(new Integer(k)))) {
                                                    map.put(new Integer(i),new Integer(i));
                                                    map.put(new Integer(j),new Integer(j));
                                                    map.put(new Integer(k),new Integer(k));
                                                    count += 1;
                                                    solution += "(" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + "(" + printNumber(o) + printSymbol(k) + printNumber(p) + ")" + "\n\n";
                                                    //System.out.println("(" + printNumber(m) + printSymbol(i) + printNumber(n) + ")" + printSymbol(j) + "(" + printNumber(o) + printSymbol(k) + printNumber(p) + ")");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //总共几种答案，过滤了类似的答案 
        //System.out.println("count: " + count);
        return solution;
    }
    
    
    
    
    
    
    
    
    
    public static int printNumber(int i){
        if(i == 0){
            return aArray[0];
        }
        if(i == 1){
            return bArray[0];
        }
        if(i == 2){
            return cArray[0];
        }
        if(i == 3){
            return dArray[0];
        }
        return 0;
    }
    
    public static String printSymbol(int i){
        
        if(i == 0){
            return " + ";
        }
        if(i == 1){
            return " - ";
        }
        if(i == 2){
            return " * ";
        }
        if(i == 3){
            return " / ";
        }
        return "";
    }
    
    
    //=====================================================
    
	public static int evaluateExpression(String expression)
    {
        // Create operandStack to store operands
        java.util.Stack<Integer> operandStack = new java.util.Stack<Integer>();
 
        // Create operatorStack to store operators
        java.util.Stack<Character> operatorStack = new java.util.Stack<Character>();
 
        // Extract operands and operators
        java.util.StringTokenizer tokens = new java.util.StringTokenizer(expression, "()+-/*", true);
 
        // Phase 1: Scan tokens
        while (tokens.hasMoreTokens())
        {
            String token = tokens.nextToken().trim(); // Extract a token
            if (token.length() == 0) // Blank space
                continue; // Back to the while loop to extract the next token
            else if (token.charAt(0) == '+' || token.charAt(0) == '-')
            {
                // Process all +, -, *, / in the top of the operator stack
                while (!operatorStack.isEmpty() &&(operatorStack.peek().equals('+') ||operatorStack.peek().equals('-') || operatorStack.peek().equals('*') ||
           operatorStack.peek().equals('/')))
                {
                    processAnOperator(operandStack, operatorStack);
                }
                // Push the + or - operator into the operator stack
                operatorStack.push(new Character(token.charAt(0)));
            }
            else if (token.charAt(0) == '*' || token.charAt(0) == '/')
            {
                // Process all *, / in the top of the operator stack
                while (!operatorStack.isEmpty() && (operatorStack.peek().equals('*') || operatorStack.peek().equals('/')))
                {
                    processAnOperator(operandStack, operatorStack);
                }
 
                // Push the * or / operator into the operator stack
                operatorStack.push(new Character(token.charAt(0)));
            }
            else if (token.trim().charAt(0) == '(')
            {
                operatorStack.push(new Character('(')); // Push '(' to stack
            }
            else if (token.trim().charAt(0) == ')')
            {
                // Process all the operators in the stack until seeing '('
                while (!operatorStack.peek().equals('('))
                {
                     processAnOperator(operandStack, operatorStack);
                }
                operatorStack.pop(); // Pop the '(' symbol from the stack
            }
            else
            {
                // An operand scanned
                // Push an operand to the stack
                operandStack.push(new Integer(token));
            }
        }
 
        // Phase 2: process all the remaining operators in the stack
        while (!operatorStack.isEmpty())
        {
            processAnOperator(operandStack, operatorStack);
        }
 
        // Return the result
        return ((Integer)(operandStack.pop())).intValue();
    }
	
	
	public static void processAnOperator(java.util.Stack<Integer> operandStack,java.util.Stack<Character> operatorStack)
    {
        if (operatorStack.peek().equals('+'))
        {
            operatorStack.pop();
            int op1 = ((Integer)(operandStack.pop())).intValue();
            int op2 = ((Integer)(operandStack.pop())).intValue();
            operandStack.push(new Integer(op2 + op1));
        }
        else if (operatorStack.peek().equals('-'))
        {
            operatorStack.pop();
            int op1 = ((Integer)(operandStack.pop())).intValue();
            int op2 = ((Integer)(operandStack.pop())).intValue();
            operandStack.push(new Integer(op2 - op1));
        }
        else if (operatorStack.peek().equals('*'))
        {
            operatorStack.pop();
            int op1 = ((Integer)(operandStack.pop())).intValue();
            int op2 = ((Integer)(operandStack.pop())).intValue();
            operandStack.push(new Integer(op2 * op1));
        }
        else if (operatorStack.peek().equals('/'))
        {
            operatorStack.pop();
            int op1 = ((Integer)(operandStack.pop())).intValue();
            int op2 = ((Integer)(operandStack.pop())).intValue();
            operandStack.push(new Integer(op2 / op1));
        }
    }
    
    
    
    
    
    
    
    
    
    
}