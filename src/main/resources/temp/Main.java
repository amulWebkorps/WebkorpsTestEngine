public class Main{
  public static void main(String args[]){
//  int n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int
 
  //write your code here
  String str1="";
  for(String str: args) {
    //   System.out.println(str);
      str1=str;
    }
         String nstr="";
        char ch;
        
      System.out.print("Original word: ");
      System.out.println("Geeks"); //Example word
        
      for (int i=0; i<str1.length(); i++)
      {
        ch= str1.charAt(i); //extracts each character
        nstr= ch+nstr; //adds each character in front of the existing string
      }
      System.out.println("Reversed word: "+ nstr);
    }
  
}