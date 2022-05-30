# Task Master

## Introduction

#### As ASAC required from us we will start building application responsible to distributing, management, creating and editing the tasks for user, so we start this build after midterm project and we will use Android studio to help us build this application.



# Day 10
#### Today I learned how to start work with storage in aws (S3) storage how to start configure it and add it in gradle build file In addition I learned how to upload file to s3 storage and how to delay with the key of image and connect it with the task, also I learned how to convert the the image to bitmap then to file to prepare it to upload process After that I started to applied function the function that responsible on download the files and pass the key for it to get specific file and I used generate random string method to naming the key of files. 
### Here is some screenshot to see  my work today you can see it [here](ScreenShot/10thDay/Day10.md)
### Go directly to the activities package [here](app/src/main/java/com/example/taskmaster/ui)




# Day 9
#### Today I learned how to implement the authorization process using Cognito service from the awa services and enable users to make their own accounts, by implement some coding to enable that and the process I learned today is make sign up function that allow the user to make his or her account, login function to let user enter to they accounts, sign out function to help user to log out from the current device or all device at the same time, reset password function to help the users to reset their passwords, change password method to let the logged in user to change the password if he want and last thing methode to keep user logged in in the application, In addition I learned how to implement the progress bar To remove doubt from the user's imagination.
### The changes included in this release
#### Login Activity
- Here the user can enter the email and the password to log in process.
- Creating amazing UI consist of different color to Attract the user's attention.
- Add keep me signed in checkbox to keep user logged in.
- Add forget password listener to move the user to reset password activity.
- Create new account button to passing on the user to sign up activity.
#### Forget Password Activity
- The activity which the user can enter his\her email to send verification code to that email.
- After click the reset button the user will handing over to reset password activity to enter the sent code and the new password with the confirmation.
#### Sign up Activity
- Here the users can set their information to create new account.
- All prevented logical process implemented here to prevent the user from make mistake in sign info process, like enter short password and enter passwords that are not the same or keep one of important edit text empty...etc.
- When the user click on the sign up button that will direct it to verification code activity to enter the sent code.
- After that the login in process will require from the user.
#### Main activity
- Edit the over flow menu and add the sign out option.
- Improvement the style of the page.
#### Setting Activity
- Add the user name with image on the top of the page To find out his\her identity.
- Make the settings page as list view and add three item are edit profile, change password and delete account.
- Edit profile will handle the user to the edit user information like name and soon the email.
- Change password let user to change the password for security reasons when he know the current password and determine if the user want to sign out from all devices or from this device only.
- Delete account enable the user to delete his\her account but before that an alert dialog will appear to ensure that if the user really want to delete the account or not and the user must enter a random string to ensure that is not a robot.
#### In general
- Make a lot of improvement on all styles in my app and change the font type for entire application.
- Improve some functionality.


### Here is some screenshot to see  my work today you can see it [here](ScreenShot/9thDay/Day9.md)
### Go directly to the authorization package [here](app/src/main/java/com/example/taskmaster/Auth)







# Day 8
#### Today I learned how to make my app ready to publish to the play store and how to create developer account in google console, also I learned how to build my project as an apk file and attached it to this directory.
### To show my apk version please go [here](app/release).



# Day 7
#### Today I learned how to make different relations between the database tables, so in this project I used the one to many relationship, in addition I learned how to query the data from cloud api and and how to filter the result from the api, also I learned how to make splash screen, and the most important thing I learned today is how to connect the results from the query with the recycler view, the last thing I learned today is how to show the tasks that related with the user team only.
### The workflow for my app now is
#### 1- When the user open the app a splash screen activity will launch(This will take 2 sec).
#### 2- During this check process will done to check if the user set his/her team in the setting.
- If yes the main activity will start.
- If no the setting page will appear and request from the user to enter username and the team.
#### 3- Before the setting page launch the process of query for all teams will happen to allow user choose his/her team.
#### 4- Before the main activity launch the process of query of all tasks related to the user team will done to show all the tasks.
### Here is some screenshot to see my work today you can see it [here](ScreenShot/7thDay/Day7.md)






# Day 6
#### Today I learned how to get started create amazon web service account and I started with initialize amplify project by follow the official documentation also at my project level I replace the store the data in data base to store it in the amazon cloud in dynamoDB and I learned how to initialize amplify project and push it to the cloud.
#### Here is some important directories to my project
- The whole local project [here](app/src/main/java/com/example/taskmaster)
- The new amplifyFrameWork directory [here](app/src/main/java/com/amplifyframework/datastore/generated/model)
- The schema.graphql [here](amplify/backend/api/amplifyDatasource/schema.graphql)

### Here is some screenshot to see  my work today you can see it [here](ScreenShot/6thDay/ReturnedItemAwsTable.jpg)


# Day 5

#### Today I learned how to make a full espresso test starting from how to initial the required dependencies to build.gradle file, then I wrote many tests type like, check the hint test, click on button test, recycle view test, adapter view test and so, I do this tests for add task activity, settings activity, recycler view activity and update activity.

### The changes included in this release

- Filter the tasks according to its states.
- Now you can delete the task if you don't want it.
- Adding some style in my application.
- Now you can edit title, description and state of task.
- Add espresso test to a lot of activity.

### Here is some screenshot for my work today you can see it [here](ScreenShot/5thDay/Day5.md)

### Go to test directly from [here](app/src/androidTest/java/com/example/taskmaster/ui)
### My code [here](app/src/main/java/com/example/taskmaster)


## Forth Day

#### Today I learned how to Save data in a local database using Room, first I start with do setup for my room by add dependency in gradle file after that I created a Primary component represented by database class, data entities and data access object (DAO) then I modified my add task page to save the task object to database after that I rendered my database in my home page.

### The changes included in this release
- Refactor the home page to show the tasks list from the database.
- Modify the function of add task button in add task page to store the data in the database.

### Here is some screenshot for my work today you can see it [here](ScreenShot/4thDay/ForthDay.md)

## Third Day

#### Today I learned how to build Recycle view to display the content of tasks list and learn how to set the look of each object inside the list by creating custom list recycler view to show the tasks as a list and set what happen when you click on the each task, that's will direct you to task details page where you can see the description and state of the task.
### The changes included in this release
- Refactor the home page to show the tasks list.
- Move the all task button to over flow menu at the top right.
- Add spinner to add task form represent the states of the task.
- Now the task added by the user (Shared preference) is also added to the list shown in the home screen.
- Add New Task button as a floating point at the home screen.
### Here is some screenshot for my work today you can see it [here](ScreenShot/3rdDay/ThirdDay.md)


## Second Day

#### Today I add many features to my application, lets start talk about it, first of all I created the setting page and let the user to save his/her name and I put it as overflow menu and I make the username rendered to home page also I added the task as button titled with task name by the way I enabled the user to save the task by shared preference class also I added the back button on the navigation bar and open the all tasks section and you will find the user task and you can click on it and see the details of that task.
### Here is some screenshot for my work today you can see it [here](ScreenShot/2ndDay/SecondeDay.md)



## First Day
#### In this day I learned how to initialize android application by Android studio and how to insert image to my app also I explore how to use onClick Listener and linked it with the related activity class and I learned how to style a lot of stuff in Android studio and make my application relative by use the relative linear features in Android studio, And the last thing i learned how to write code inside xml file.
### Here is some screenshot for my work today you can see it [here](ScreenShot/1stDay/FirstDay.md)















