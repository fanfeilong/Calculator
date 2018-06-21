import java.util.Stack;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Exception;

public class Main {

    private static String[] op = { "+", "-", "*", "/" };// Operation set

    public static void main(String[] args) {
        // 解析命令行参数        
        String[] options = ParseOptions(args);
        String question = options[0];
        String answer = options[1];

        // 计算表达式
        System.out.println("question from commandline:"+question);
        String[] ret = Solve(question);
        

        // 比对计算结果
        CheckResult(question, answer, ret);
    }

    public static String[] ParseOptions(String[] args){
        String[] result = new String[2];
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-q")){
                result[0] = args[i+1];
                i++;
            }else if(args[i].equals("-a")){
                if(args.length>i+1){
                    result[1] = args[i+1];
                    i++;
                }
            }
        }
        return result;
    }

    public static void CheckResult(String question, String answer, String[] ret){
        String errorMsg = ret[0];
        String result = ret[1];

        if(errorMsg!=null){
            System.out.println("[error] solve:"+question + "=" + result+", calc error:"+errorMsg);
            System.exit(1);
        }else{
            if(answer==null){
                System.out.println(question + "=" + result);
            }else{
                if(result.equals(answer)){
                    System.out.println("[info] solve:"+question + "=" + result+", answer matched:"+answer);
                }else{
                    System.out.println("[error] solve:"+question + "=" + result+", answer not matched:"+answer);
                    System.exit(1);
                }
            }
        }
    }

    public static String MakeFormula(){
        StringBuilder build = new StringBuilder();
        int count = (int) (Math.random() * 2) + 1; // generate random count
        int start = 0;
        int number1 = (int) (Math.random() * 99) + 1;
        build.append(number1);
        while (start <= count){
            int operation = (int) (Math.random() * 3); // generate operator
            int number2 = (int) (Math.random() * 99) + 1;
            build.append(op[operation]).append(number2);
            start ++;
        }
        return build.toString();
    }

    public static void DumpStack(String tip, Stack stack){
        // now 
        Date date = new Date();
        long times = date.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(date);

        // file+lineNo
        int lineNo = Thread.currentThread().getStackTrace()[2].getLineNumber();
        String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
        String location = ""+fileName+":"+lineNo;

        System.out.println("["+now+"]"+tip+Arrays.toString(stack.toArray())+","+location);
    }

    public static void Assert(boolean condition, String errorLog) {
        if(!condition){
            // now 
            Date date = new Date();
            long times = date.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = formatter.format(date);

            // file+lineNo
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            int lineNo = elements[2].getLineNumber();
            String fileName = elements[2].getFileName();
            String location = ""+fileName+":"+lineNo;

            System.out.println("["+now+"]"+errorLog+","+location);
            
            for(int i=0; i<elements.length; i++) {
                System.out.println(elements[i]);
            }

            System.exit(1);
        }
    }

    public static String[] Solve(String formula){
        String[] result = new String[2];

        Stack<String> tempStack = new Stack<>();//Store number or operator
        Stack<Character> operatorStack = new Stack<>();//Store operator
        int len = formula.length();
        int k = 0;
        for(int j = -1; j < len - 1; j++){
            char formulaChar = formula.charAt(j + 1);
            if(j == len - 2 || formulaChar == '+' || formulaChar == '-' || formulaChar == '/' || formulaChar == '*') {
                String index = "[j:"+j+",k:"+k+",char:"+formulaChar+"]";
                if (j == len - 2) {
                    tempStack.push(formula.substring(k));
                    DumpStack(index+"tempStack:",tempStack);
                }
                else {
                    Assert(k<=j,"k is not less then j, [k:"+k+",j:"+j+"]");
                    tempStack.push(formula.substring(k, j + 1));
                    DumpStack(index+"tempStack:",tempStack);

                    if(operatorStack.empty()){
                        operatorStack.push(formulaChar); //if operatorStack is empty, store it
                        DumpStack(index+"operatorStack:",operatorStack);
                    }else{
                        char stackChar = operatorStack.peek();
                        if ((stackChar == '+' || stackChar == '-')
                                && (formulaChar == '*' || formulaChar == '/')){
                            operatorStack.push(formulaChar);
                            DumpStack(index+"operatorStack:",operatorStack);
                        }else {
                            tempStack.push(operatorStack.pop().toString());
                            DumpStack(index+"tempStack:",tempStack);

                            operatorStack.push(formulaChar);
                            DumpStack(index+"operatorStack:",operatorStack);
                        }
                    }
                }
                k = j + 2;
            }
        }

        while (!operatorStack.empty()){ // Append remaining operators
            tempStack.push(operatorStack.pop().toString());
            DumpStack("tempStack:",tempStack);
        }

        Stack<String> calcStack = new Stack<>();
        for(String peekChar : tempStack){ // Reverse traversing of stack
            if(!peekChar.equals("+") && !peekChar.equals("-") && !peekChar.equals("/") && !peekChar.equals("*")) {
                calcStack.push(peekChar); // Push number to stack
            }else{
                int a1 = 0;
                int b1 = 0;
                if(!calcStack.empty()){
                    b1 = Integer.parseInt(calcStack.pop());
                }
                if(!calcStack.empty()){
                    a1 = Integer.parseInt(calcStack.pop());
                }
                switch (peekChar) {
                    case "+":
                        calcStack.push(String.valueOf(a1 + b1));
                        break;
                    case "-":
                        calcStack.push(String.valueOf(a1 - b1));
                        break;
                    case "*":
                        calcStack.push(String.valueOf(a1 * b1));
                        break;
                    default:
                        try{
                            calcStack.push(String.valueOf(a1 / b1));
                        }catch(ArithmeticException e){
                            result[0] = "ERROR:"+a1+"/ 0 is not allowed.";
                            return result;
                        }
                        
                        break;
                }
            }
        }
        
        result[1] = calcStack.pop();
        return result;
    }
}
