Team: 2145-06-SWEN-261-TEAM-A-Burt Macklin
Document Purpose: This is a “readme” file that gives a synopsis of the overall program with clear instructions on how to install HealthNet from a zipped folder downloaded from the Internet. 
It also includes possible bugs, features that may be missing or not functional, and some basic instructions on how to log into your profile.


Installation instructions:
Target Environment: 
Revision number 
Python 3.2 and Django 1.6.5 for Windows.
User has a utility to unzip Windows folders.
User has downloaded the zipped folder and knows its location.
User has basic knowledge on how to use a web browser.
Steps to follow:
1. Extract the project using a file unzipping utility to a desired location. Create a folder on your desktop called “HealthNetApp” and extract the contents of the zipped folder to the new folder. 
2. Open up a command prompt window by going to Start, typing in “cmd”, and hit Enter.
3. Type the command: “cd C:\Users\userNameHere\Desktop\HealthNetApp\HealthNet”, where “userNameHere” is your user name for your Windows account. Note, the “HealthNet” part at the end is a 
   folder that is at the root of the application, you do not need to create it.
4. Type the command: “ls” with lower case L. This lists the files in your current folder. Make sure there is a file called “manage.py” to double check you are in the right place.
5. Type the command: “python manage.py syncdb”. Some text will appear when the process is finished. Just ignore that.
6. Type the command: “python manage.py runserver” to start the application and press Enter. You will see some text output to the command prompt window, and it gives instructions on how to quit 
   the application. NOTE: Closing the command prompt window will NOT shutdown the server entirely. Should you need port 8000 for a different application, this will cause problems. Pressing CTRL+C 
   per those instructions will shutdown the server correctly. 
7. Be sure to minimize the command prompt window. Closing the window causes issues described in step 6.
8. Open up a web browser and open a new tab. In the address bar, type in “localhost:8000” and hit Enter. This will open up the application’s home page.
9. HealthNet is now installed on your machine! When finished, go back to the command prompt window and press CTRL+C to shutdown the server.
10. Type “exit” into the window and hit Enter, or close the window by hitting the “X” in the upper right hand corner to close the prompt.



Bugs and Disclaimers:
1. Site crashes if user clear cookies and tries to use browser's Back button to go back to previous page or
when user hits Submit to advance to next page. (DO NOT CLEAR COOKIES)
2. Site crashes if user tries to modify a non-existing appointment.


Missing Features: 
2. Calendar view for appointments


Usage Instructions:

Patient Registration:
At home page, user can register for HealthNet service through Patient Registration button at center of the site. Registration process 
requires user to enter account information, profile information, medical
information and emergency contact. If there are missing or incorrect
information format, registration page will reload with error display and
highlights in red where errors occur. Once user has entered all the necessary information and hit Register. User will be directed to 
their profile page.


Patient Interface:

User Login
Once registered, user can now log in to their accounts via Login button
at the top-right corner. If login successful, user is redirected to their
profile page, where they can update their profile, view their ppointments,
prescriptions and either create or remove appointment(s).


Update Profile
At profile page, user has an option to update their profile information.
The update button will direct user to an update-profile page with all
user information pre-loaded. User can then modify their info and hit
Update to save new information. If user decides not to update their 
profile, there is also a Cancel button next to Update to allow user to
cancel the process.


Create/Modify/Cancel Appointment
To the right-most column, user can see their apointments list and above 
the list is a Manage Appointment button that directs user to Appointment
Control Panel. 
At Appointment Control Panel, user can create a new appointment by enter
desired appointment time, doctor and hit Create a New Appointment button
below the form. The middle column will display all appointments that 
a user is currently having. The right-most column is where user can
modify and delete their appointments. Modifying or deleting appointment
requires user to enter the appopintment ID which is unique for each
appointment user has and is shown in the appointment list.


Doctor Interface:

Once logged-in, doctor is directed to their profile page. Doctor can 
make/modify/cancel an appointment in Appointment Panel. To create a new 
appointment, doctor needs to input date, time and a patient for 
appointment and hit Create a New Appointment button below the form. 
Modifying and cancelling appointment also require an appointment ID
which is displayed in appointment list. Similar to appointment panel, 
prescription panel allows doctors to create and delete a prescription.
Deleting a prescription also requires a prescription ID which is displayed
in prescription list.


Nurse Interface:

Once logged-in, nurse is directed to their profile page. Nurse can 
create/modify/cancel appointment of a patient via Appointment Control
Panel, which function the same as patient and doctor Appointment Control
Panel.

Admin Interface:

Current admin interface allows admin either create or delete a user
in the system. With creating a new user, admin can add all types of
user into system such as doctors, nurses, patients or even other admins.
Delete User button allows admin to search for 
a specific user and then deactivate that user.