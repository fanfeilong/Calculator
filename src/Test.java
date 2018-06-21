import java.lang.Exception;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] args) {
        String[][] tests = {
            {"0+1","1"},
            {"0-1","-1"},
            {"0*1","0"},
            {"0/1","0"},
            {"1/0",""},
            {"100+20-3/10","119.7"},
            {"100+20-30/10","117"}
        };

        int successCount=0;
        System.out.println("TEST BEGIN");
        System.out.println("----------");
        for(int i=0;i<tests.length;i++){
            String question = tests[i][0];
            String answer = tests[i][1];
            int ret = runTest(question,answer);
            if(ret==0){
                successCount++;
                System.out.println("[SUCCESS], question:"+question+", answer:"+answer);
            }else{
                System.out.println("[FAILED], question:"+question+", answer:"+answer+", ret:"+ret);
            }  
            System.out.println("\n"); 
        }
        int failedCount = tests.length-successCount;

        System.out.println("----------");
        System.out.println("TEST END, "+successCount+" success, "+failedCount+" failed.");
    }

    private static int runTest(String question, String answer) {
		StringBuffer output = new StringBuffer();
        int ret=0;
		Process p;
		try {
			p = Runtime.getRuntime().exec(new String[]{"java","Main","-q",question,"-a",answer});
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

            ret = p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
            ret = -1;
		}

	    System.out.println(output.toString());
        return ret;
	}
}