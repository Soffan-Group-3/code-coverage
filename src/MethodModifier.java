import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MethodModifier {
	
	private static String[] matches = new String[] {"if (","else {", "else if (", "case ", "default:", "for (", "while ("};
	private static String[] types = new String[] {"int", "String", "void", "char", "double", "boolean"};
	private static int id = 0;
	private static String filewriter = "\n\n    // Added by MethodModifier\n" +
            "    private static FileWriter fw;\n" +
            "    private static BufferedWriter bw;\n" +
            "    private static PrintWriter out;\n\n" +
            "    static {\n" +
            "        try {\n" +
            "            fw = new FileWriter(\"branch_results.txt\", true);\n" +
            "            bw = new BufferedWriter(fw);\n" +
            "            out = new PrintWriter(bw);\n\n" +
            "            // Register a shutdown hook to close the writers\n" +
            "            Runtime.getRuntime().addShutdownHook(new Thread(() -> {\n" +
            "                try {\n" +
            "                    if (out != null) out.close();\n" +
            "                    if (bw != null) bw.close();\n" +
            "                    if (fw != null) fw.close();\n" +
            "                    System.out.println(\"Writers closed successfully.\");\n" +
            "                } catch (IOException e) {\n" +
            "                    e.printStackTrace();\n" +
            "                }\n" +
            "            }));\n" +
            "        } catch (IOException e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "    }\n\n";
	
     private static String addBranch = "    public void addBranchResult(int i) {\n" +
            "        out.println(i);\n" +
            "        out.flush(); // Ensure data is written to the file immediately\n" +
            "        //System.out.println(\"Added \" + i + \" to branch_results.txt\");\n" +
            "    }\n\n" +
            "    // Close the writers when they are no longer needed\n";
            
     private static String close_method =  "    public static void closeWriters() {\n" +
								            "        try {\n" +
								            "            if (out != null) out.close();\n" +
								            "            if (bw != null) bw.close();\n" +
								            "            if (fw != null) fw.close();\n" +
								            "        } catch (IOException e) {\n" +
								            "            e.printStackTrace();\n" +
								            "        }\n" +
								            "    }\n";
            
	private static String imports = "import java.io.FileWriter;\n" +
            "import java.io.BufferedWriter;\n" +
            "import java.io.PrintWriter;\n" +
            "import java.io.IOException;\n";

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	
        if (args.length < 1) {
            System.out.println("Usage: java MethodModifier <ClassName>;<MethodName_1>:<MethodName_2> <ClassName_2>;<MethodName_1>:<MethodName_2>\"");
            return;
        }
        List<String> classes = new ArrayList<String>();
        for(String arg: args) {
            // Convert each String arg to char array
        	int endClass = arg.indexOf('-');
        	String classname = arg.substring(0, endClass);
        	String[] methods = arg.substring(endClass+1).split(":");
        	addCoverageDetection(classname,methods);
        	classes.add(classname);
        }
        
        ProcessBuilder pb =
        		   new ProcessBuilder("mvn", "clean", "verify");
        
        //pb.redirectErrorStream(true);
        Process process = pb.start();

        process.waitFor(10, TimeUnit.MINUTES);
        
        for (int i = 0; i < classes.size(); i++) {
        	String className = classes.get(i);
        	String sourceFilePath = className + ".java";
        	String copyFilePath = className + "_Modified.tmp";
        	String content = new String(Files.readAllBytes(Paths.get(sourceFilePath)));
        	
            Files.write(Paths.get(sourceFilePath), content.getBytes());
            Files.deleteIfExists(Paths.get(copyFilePath));
        }
        
        
        File resultFile = new File("branch_results.txt");
        Scanner resultReader = new Scanner(resultFile);
        
        HashMap<Integer, Integer> resultingBranches = new HashMap<>();
        
        while (resultReader.hasNextInt()) {
          int data = resultReader.nextInt();
          resultingBranches.merge(data, 1, Integer::sum);
          
        }
        resultReader.close();
        resultingBranches.forEach((key,value) -> System.out.println(key + " " + value));
        
        Files.deleteIfExists(Paths.get("branch_results.txt"));
        
        for(int i = 0; i < 73; i++) {
        	if (!resultingBranches.containsKey(i)) {
        		System.out.println(i + " has missing tests");
        	}
        }
        

    }
    
    public static void addCoverageDetection(String className,String[] methods) {
    	String sourceFilePath = className + ".java";
    	String copyFilePath = className + "_Modified.tmp";
        try {
            String content = new String(Files.readAllBytes(Paths.get(sourceFilePath)));
            String modifiedContent = content;
            Files.copy(Paths.get(sourceFilePath), Paths.get(copyFilePath), StandardCopyOption.REPLACE_EXISTING);
            for (String method : methods) {
            	modifiedContent = modifyMethod(modifiedContent, method);
                System.out.println("Method '" + method + "' modified successfully in the file: " + sourceFilePath);

            }
            
            modifiedContent = addImport(modifiedContent,imports);
            modifiedContent = addMethodToClass(modifiedContent, filewriter);
            modifiedContent = addMethodToClass(modifiedContent, addBranch);

            //Files.write(Paths.get(copyFilePath), content.getBytes());
            Files.write(Paths.get(sourceFilePath), modifiedContent.getBytes());
            
        } catch (IOException e) {
            e.printStackTrace();	
        }
    }
    
    
    public static String addImport(String content, String imports) {
    	
    	int packageLine = content.indexOf("package");
    	int firstSem = content.substring(packageLine).indexOf(";");
    	int insert = packageLine+firstSem+1;
    	return content.substring(0, insert) + "\n" + imports + content.substring(insert);	
    }
    
    public static String addMethodToClass(String content, String method) {
        int lastBraceIndex = content.lastIndexOf('}');
        if (lastBraceIndex == -1) {
            System.out.println("Could not find the end of the class definition.");
            return content;
        }        // Insert the new method before the last closing brace
        String modifiedContent = content.substring(0, lastBraceIndex) + method + content.substring(lastBraceIndex);

        return modifiedContent;
    }

    public static String modifyMethod(String content, String methodName) {
        // Find the method signature
    	int methodStartIndex = -1;
    	int counter = 0;
    	while(methodStartIndex == -1 && counter < types.length) {
            String methodSignature =  types[counter] + " " + methodName + "()"; 
            System.out.println("Searching for: " + methodSignature);
            methodStartIndex = content.indexOf(methodSignature);
            counter++;
    	}


        if (methodStartIndex == -1) {
            System.out.println("Method '" + methodName + "' not found.");
            return content;
        }

        // Find the start of the method body
        int bodyStartIndex = content.indexOf('{', methodStartIndex);
        if (bodyStartIndex == -1) {
            System.out.println("Method body not found for '" + methodName + "'.");
            return content;
        }
        // Find the end of the method body
        int bodyEndIndex = findClosingBrace(content, bodyStartIndex);
        if (bodyEndIndex == -1) {
            System.out.println("Could not find the end of the method body for '" + methodName + "'.");
            return content;
        }

        // Extract the method body
        String methodBody = content.substring(bodyStartIndex + 1, bodyEndIndex);
        
        String[] splitBody = methodBody.split("\\r?\\n");
        StringBuilder newmethod = new StringBuilder();
        for (int i = 0; i < splitBody.length; i++){
        	newmethod.append(splitBody[i]+"\n");
        	if (isBranch(splitBody[i])) {
        		id += 1;
        		newmethod.append("addBranchResult("+id+");\n");
        	}
            
        }
         
        // Replace the old method body with the modified one
        String newContent = content.substring(0, bodyStartIndex + 1) + newmethod.toString() + content.substring(bodyEndIndex);

        return newContent;
    }
    
    private static boolean isBranch(String input) {  	
    	for (String s : matches) {
    		if(input.contains(s)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    private static int findClosingBrace(String content, int startIndex) {
        int braceCount = 1;
        char prevChar = 'a';
        for (int i = startIndex + 1; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch == '{' && (prevChar != '\'' && prevChar != '\"')) {
                braceCount++;
            } else if (ch == '}' && (prevChar != '\'' && prevChar != '\"')) {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
            prevChar = ch;
        }
        return -1;
    }
    
}