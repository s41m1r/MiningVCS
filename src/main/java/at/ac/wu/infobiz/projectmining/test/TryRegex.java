package at.ac.wu.infobiz.projectmining.test;

public class TryRegex {

	public static void main(String[] args) {
		
		String pattern = "@@ (.*) @@";
		String input = "--git a/c.jar b/c.jar\n" + 
				"			deleted file mode 100644\n" + 
				"			index b377d9a..0000000\n" + 
				"			Binary files a/activiti-engine-test-api/target/activiti-engine-test-api-5.0.alpha3-SNAPSHOT.jar and /dev/null differ";
		
		String[] split = input.split(pattern);
//		for (int i = 0; i < split.length; i++) {
//			System.out.println(i+":"+split[i]);
//		}
		System.out.println(input.contains("Binary files "));
	}
}
