package com.example.taskmaster.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("SetTextI18n")
public class AddTaskActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 123;
    public static final String TASK_ID = "TaskID";
    private Task task;

    private static final String TAG = AddTaskActivity.class.getSimpleName();

    private EditText taskTitle;
    private EditText taskDescription;

    private Spinner taskState;

    private TextView totalTask;

    private Button addTaskButton;
    private Button uploadImageButton;
    private String taskImageKey;
    private String taskTitleString;
    private String taskDescriptionString;
    private String taskStateString;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        findAllViewsByIdMethod();

//        saveTeamToLocalDB();

        backToPreviousButton();

        setAdapterToStatesTaskArraySpinner();

        setAdapterToStatesTeamArraySpinner();

        //manuallyInitializeTheTeams();

        buttonsAction();
    }

    private void getAllStringFormEditText() {

        taskTitleString = taskTitle.getText().toString();
        taskDescriptionString = taskDescription.getText().toString();
        taskStateString = taskState.getSelectedItem().toString();
    }

    private void setAdapterToStatesTaskArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */

        Spinner spinner = findViewById(R.id.task_states_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_states_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void setAdapterToStatesTeamArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        List<String> teams = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            teams = SplashActivity.teamsList.stream().map(Team::getName).sorted().collect(Collectors.toList());
        }

        /*
        https://www.codegrepper.com/code-examples/java/android+studio+how+to+fill+spinner
         */
        Spinner spinner = findViewById(R.id.task_team_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    private void addTaskButtonAction() {
        if (TextUtils.isEmpty(taskTitle.getText()) || TextUtils.isEmpty(taskDescription.getText())) {

            taskTitle.setError("Title is Required");
            taskDescription.setError("Description is required");
        } else {
            Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
            saveToCloudDB();
            navigateToDetailsPage();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }

    private void backToPreviousButton() {
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video I learned how to add back button in action bar
        */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void findAllViewsByIdMethod() {

        addTaskButton = findViewById(R.id.create_task_button);
        uploadImageButton = findViewById(R.id.upload_photo_button);
        taskTitle = findViewById(R.id.taskTitleBox);
        taskDescription = findViewById(R.id.task_description_box);
        taskState = findViewById(R.id.task_states_spinner);
        totalTask = findViewById(R.id.tasks_count);
    }

    private void navigateToDetailsPage() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TASK_ID, task.getId());
        startActivity(intent);
        finish();
    }

    private void manuallyInitializeTheTeams() {
//        //Add the team to the task
//        Team team1 = Team.builder()
//                .name("First Team")
//                .build();
//        Amplify.DataStore.save(team1, pass -> {
//                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
//                },
//                failure -> {
//                    Log.e(TAG, "Could not save Team to Task ", failure);
//                });
//        //Add the team to the task
//        Team team2 = Team.builder()
//                .name("Second Team")
//                .build();
//        Amplify.DataStore.save(team2, pass -> {
//                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
//                },
//                failure -> {
//                    Log.e(TAG, "Could not save Team to Task ", failure);
//                });
//        //Add the team to the task
//        Team team3 = Team.builder()
//                .name("Third Team")
//                .build();
//        Amplify.DataStore.save(team3, pass -> {
//                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
//                },
//                failure -> {
//                    Log.e(TAG, "Could not save Team to Task ", failure);
//                });
    }

    private void saveToCloudDB() {

        // Lab 32 \\
        Team team1 = SplashActivity.teamsList.stream().filter(team -> team.getName().equals("First Team")).collect(Collectors.toList()).get(0);

//        Team team2 = Team.builder().name("Second Team").build();
//        Team team3 = Team.builder().name("Third Team").build();

        task = Task.builder()
                .title(taskTitleString)
                .description(taskDescriptionString)
                .status(taskStateString)
                .teamTasksId(team1.getId())
                .taskImageCode(taskImageKey)
                .build();

        // Data store save
        Amplify.DataStore.save(team1,
                success -> {
                    Amplify.DataStore.save(task,
                            savedTask -> {
                                Log.i("Task in add task page ", team1.getId());
                            },
                            failure -> Log.e("ask in add task page", "Task not saved.", failure)
                    );
                    Log.i(TAG, "Team in add task page " + success.item().getName());
                },
                error -> Log.e(TAG, "Could not save item to DataStore ", error)
        );

        // API save to backend
        Amplify.API.mutate(ModelMutation.create(team1),
                success -> Amplify.API.mutate(ModelMutation.create(task),
                        successTask -> {
                            uploadImage();
                            Log.i(TAG, "Task saved to team from API => " + successTask.getData().getId());
                        },
                        failure -> Log.i(TAG, "Task not saved to the team from API => ")),
                error -> Log.e(TAG, "Could not save team to API ", error)
        );
        MainActivity.tasksList.add(task);

        /*
         * https://stackoverflow.com/questions/66576755/io-reactivex-exceptions-undeliverableexception-the-exception-could-not-be-delive
         */
        RxJavaPlugins.setErrorHandler(e -> {
        });
        navigateToDetailsPage();
    }


    private void saveTeamToLocalDB() {


//        Task newTask = new Task(taskTitleString, taskDescriptionString, taskState);
//        TaskDatabase.getInstance(getApplicationContext()).taskDao().insertTask(newTask);

//        com.example.taskmaster.data.Team team1 = new com.example.taskmaster.data.Team("First Team");
//        com.example.taskmaster.data.Team team2 = new com.example.taskmaster.data.Team("Second Team");
//        com.example.taskmaster.data.Team team3 = new com.example.taskmaster.data.Team("Third Team");
//
//        TeamDatabase.getInstance(this).teamDao().insertTeam(team1);
//        TeamDatabase.getInstance(this).teamDao().insertTeam(team2);
//        TeamDatabase.getInstance(this).teamDao().insertTeam(team3);

    }

    private void buttonsAction() {

        addTaskButton.setOnClickListener(view -> {
            getAllStringFormEditText();
            addTaskButtonAction();

        });
        uploadImageButton.setOnClickListener(view -> {
            bringPhotoFromGallery();
        });
    }

    private void bringPhotoFromGallery() {

        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_CODE) {// Get photo picker response for single select.
            Uri currentUri = data.getData();

            // Do stuff with the photo/video URI.
            try {

                Bitmap bitmap = getBitmapFromUri(currentUri);
                if (taskTitleString==null && taskTitle.getText().toString()!=null)
                    taskTitleString = taskTitle.getText().toString();
                else
                    taskTitleString = "Task";
                taskImageKey = taskTitleString.toLowerCase().replace(" ","_") + "_" + generateRandomString(10).toLowerCase();
                file = new File(getApplicationContext().getFilesDir(), taskImageKey+".jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "The URI is => " + currentUri, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void uploadImage() {

        // upload to s3
        // uploads the file
        Amplify.Storage.uploadFile(
                taskImageKey,
                file,
                result -> Log.i(TAG, "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e(TAG, "Upload failed", storageFailure)
        );
    }

    /*
     * https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
     */
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    public static String generateRandomString(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                + "lmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

}
