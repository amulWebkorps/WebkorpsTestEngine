let editor;

window.onload = function() {
    editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.session.setMode("ace/mode/c_cpp");
    codeEditor();
}

function changeLanguage() {
debugger
    let language = $("#languages").val(); 
    
  if(language=="python"){
     editor.setValue("def execute(): \n\t for i in range(10):\n\t\t print(i) \nexecute()")
   }
 
  //java
  if(language=="java")
  {
      let javacode = `public class Practise{
  public static void main(String args[]){
    System.out.println("hello");
  }
}`;
     editor.setValue(javacode)
 }
  
//c++  
  if(language=="cpp")
  {
      let cppcode = `#include <iostream>
using namespace std;
  int main() {
      cout<<"Hello World"; \n
      return 0;\n
}`
      editor.setValue(cppcode)
  }
  
//c 
  if(language=="c")
  {
     let c = `#include <stdio.h>
  int main() {
       printf("Hello World"); \n
      return 0;\n
}`
      editor.setValue(c)	
  }
       
if(language == 'c' || language == 'cpp')editor.session.setMode("ace/mode/c_cpp");
else if(language == 'python')editor.session.setMode("ace/mode/python");
else if(language == 'java')editor.session.setMode("ace/mode/java");
}



function executeCode(){

var language = $("#languages").val();
var code     = editor.getSession().getValue();
var questionId = 16;
console.log(language);
console.log(code);

if(language == 'java'){
 
 var d = { 'language': $("#languages").val(), 'code': editor.getSession().getValue(), questionId: parseInt(questionId) };

 $.ajax({
        url: "/java-compiler-api",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        method: "POST",
        data: JSON.stringify(d),

      success: function(response) {
           //console.log(response.status);
           console.log(response.output);
			$(".output1").text(response.output)
        },

      error: function(error){
          console.log(error);
        },

    });
  }
 
 if(language == 'python'){
	
 var d = { 'language': $("#languages").val(), 'code': editor.getSession().getValue(), 'questionId': questionId  };
 
 $.ajax({
        url: "/python-compiler-api",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        method: "POST",
        data: JSON.stringify(d),

      success: function(response) {
           //console.log(response.status);
           console.log(response.output);
			$(".output1").text(response.output)
        },

      error: function(error){
          console.log(error);
        },

    });
   }
   
if(language == 'c'){
 
 var d = { 'language': $("#languages").val(), 'code': editor.getSession().getValue(), 'questionId': questionId };
 
 $.ajax({
        url: "/c-compiler-api",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        method: "POST",
        data: JSON.stringify(d),

      success: function(response) {
           console.log(response.output);
			$(".output1").text(response.output)
        },

      error: function(error){
          console.log(error);
        },

    });
  }
  
if(language == 'cpp'){
 
 var d = { 'language': $("#languages").val(), 'code': editor.getSession().getValue(), 'questionId': questionId  };
 
 $.ajax({
        url: "/cpp-compiler-api",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        method: "POST",
        data: JSON.stringify(d),

      success: function(response) {
           //console.log(response.status);
           console.log(response.output);
			$(".output1").text(response.output)
        },

      error: function(error){
          console.log(error);
        },

    });
   }
}

   
   
function codeEditor(){
 let c = `#include <stdio.h>
  int main() {
       printf("Hello World"); \n
      return 0;\n
}`  
 editorc.setValue(c)	
}