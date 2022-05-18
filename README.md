# Task Master

## Introduction

#### As ASAC required from us we will start building application responsible to distributing, management, creating and editing the tasks for user, so we start this build after midterm project and we will use Android studio to help us build this application.

# Day 6
#### Today I learned how to get started create amazon web service account and I started with initialize amplify project by follow the official documentation also at my project level I replace the store the data in data base to store it in the amazon cloud in dynamoDB and I learned how to initialize amplify project and push it to the cloud.
#### Here is some important directories to my project
- The whole local project [here](app/src/main/java/com/example/taskmaster)
- The new amplifyFrameWork directory [here](app/src/main/java/com/amplifyframework/datastore/generated/model)
- The schema.graphql [here](amplify/backend/api/amplifyDatasource/schema.graphql)

### There is some screenshot for to proof my work today you can see it [here](ScreenShot/6thDay/ReturnedItemAwsTable.jpg)


# Day 5

#### Today I learned how to make a full espresso test starting from how to initial the required dependencies to build.gradle file, then I wrote many tests type like, check the hint test, click on button test, recycle view test, adapter view test and so, I do this tests for add task activity, settings activity, recycler view activity and update activity.

### The changes included in this release

- Filter the tasks according to its states.
- Now you can delete the task if you don't want it.
- Adding some style in my application.
- Now you can edit title, description and state of task.
- Add espresso test to a lot of activity.

### There is some screenshot for my work today you can see it [here](ScreenShot/5thDay/Day5.md)

### Go to test directly from [here](app/src/androidTest/java/com/example/taskmaster/ui)
### My code [here](app/src/main/java/com/example/taskmaster)


## Forth Day

#### Today I learned how to Save data in a local database using Room, first I start with do setup for my room by add dependency in gradle file after that I created a Primary component represented by database class, data entities and data access object (DAO) then I modified my add task page to save the task object to database after that I rendered my database in my home page.

### The changes included in this release
- Refactor the home page to show the tasks list from the database.
- Modify the function of add task button in add task page to store the data in the database.

### There is some screenshot for my work today you can see it [here](ScreenShot/4thDay/ForthDay.md)

## Third Day

#### Today I learned how to build Recycle view to display the content of tasks list and learn how to set the look of each object inside the list by creating custom list recycler view to show the tasks as a list and set what happen when you click on the each task, that's will direct you to task details page where you can see the description and state of the task.
### The changes included in this release
- Refactor the home page to show the tasks list.
- Move the all task button to over flow menu at the top right.
- Add spinner to add task form represent the states of the task.
- Now the task added by the user (Shared preference) is also added to the list shown in the home screen.
- Add New Task button as a floating point at the home screen.
### There is some screenshot for my work today you can see it [here](ScreenShot/3rdDay/ThirdDay.md)


## Second Day

#### Today I add many features to my application, lets start talk about it, first of all I created the setting page and let the user to save his/her name and I put it as overflow menu and I make the username rendered to home page also I added the task as button titled with task name by the way I enabled the user to save the task by shared preference class also I added the back button on the navigation bar and open the all tasks section and you will find the user task and you can click on it and see the details of that task.
### There is some screenshot for my work today you can see it [here](ScreenShot/2ndDay/SecondeDay.md)



## First Day
#### In this day I learned how to initialize android application by Android studio and how to insert image to my app also I explore how to use onClick Listener and linked it with the related activity class and I learned how to style a lot of stuff in Android studio and make my application relative by use the relative linear features in Android studio, And the last thing i learned how to write code inside xml file.
### There is some screenshot for my work today you can see it [here](ScreenShot/1stDay/FirstDay.md)















