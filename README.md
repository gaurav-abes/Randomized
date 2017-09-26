What number says?

Create an app which tells facts about numbers. Facts can be received from API and can be displayed to user.

API provides facts for following categories:
	
	1. Trivia
	2. Math
	3. Date
	4. Year

Facts for the above mentioned category can be received in two ways from API:

	1. Random Facts API - API selects a random number and returns a fact related to that category. For eg: http://numbersapi.com/random/math will return a random fact related to mathematics.

	2. Specific Facts API - This returns a fact related to number related to that category. For eg: http://numbersapi.com/3/math will return a fact related to number "3" for category "math"

A) Basic Functionality:

	Random Facts Screen - A screen showing categories which can be selected by user. On clicking a category, it should fetch a fact from API and display it on the screen.

	User Interface:
	       Input: View to select a category (“trivia”, “math”, “date”, “year”) - Mandatory
	       Output: View displaying fact received from API.

	Resources:
	    	    API: 
	    	     Request:
	    	     Endpoint URL: http://numbersapi.com/random/{category}
	    	     Type: GET
	    	     Parameter: "category" - where category can be “trivia”, “math”, “date”, “year”. 
	    	
	    	     Response: 
	    	     String with a fact
	    	
	    	Sample Request URLs:    
	                1. http://numbersapi.com/random/trivia
	                2. http://numbersapi.com/random/year
	                3. http://numbersapi.com/random/date
	                4. http://numbersapi.com/random/math


B) Advance Functionality:

	In addition to basic app, implement functionality mentioned below.

	Quest Facts Screen - A screen showing a view where user can select a category, input a number, on clicking submit button, a fact will be displayed.

	Example: If a user selects category as “math“ and number as “10”, app should fetch data from request http://numbersapi.com/3/math and shows a fact accordingly regarding      mathematical number 10

	User Interface:
	Inputs: 
	View to select a category (“trivia”, “math”, “date”, “year”) - Mandatory
	View to input Number - Mandatory
	Submit Button
	Output:
	View displaying fact recieved from API.

	Resources:
	    	    API: 
	    	      Request:
	    	      Endpoint URL: http://numbersapi.com/{digitOrDate}/{category}
	    	      Type: GET
	    	      Parameter: 
	    	      "category" - where category can be “trivia”, “math”, “date”, “year”. 
	    	      "digitOrDate" - it can be a date "MM/DD" eg "01/09"
	    	      it can be a digit eg "180"
	    	
	    	      Response: 
	    	      String with a fact
	    	
	    	Sample Request URLs:    
	                1. http://numbersapi.com/3/math - Returns a fact related to number 3 for "maths" category
	                2. http://numbersapi.com/180/trivia - Returns a fact related to number 180 for "trivia" category
	                3. http://numbersapi.com/01/09/date - Returns a fact related to data January 09, for "date" category
	                4. http://numbersapi.com/2017/year - Returns a fact related to year 2017 for "year" category

Deliverables:
Minimum viable product: should contain all the necessary source files and app.
A short presentation that should contain a description of your solution.
