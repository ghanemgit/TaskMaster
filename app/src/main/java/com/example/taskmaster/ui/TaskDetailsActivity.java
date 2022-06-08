package com.example.taskmaster.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.predictions.models.LanguageType;
import com.example.taskmaster.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint({"SetTextI18n", "MissingPermission", "WrongThread"})
public class TaskDetailsActivity extends AppCompatActivity{

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();

    public static final String LATITUDE = "Latitude",LONGITUDE = "Longitude";

    private ImageView taskImageView, locationMarkerImage, translateImage, textToSpeechImage;

    private Task currentTask = null, taskFromAddTaskPage = null;

    private String teamName = "", downloadedImagePath;

    private TextView state, body, team, latitudeTextView, longitudeTextView;

    private LoadingDialog loadingDialog;

    private Button deleteButton, editButton;

    private final MediaPlayer mp = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        findAllViewById();

        loadTaskInfoFromMain();

        setTextForTaskView();

        setOnClickListeners();

        Log.i(TAG, "onCreate: Task image code -> "+currentTask.getTaskImageCode());
        if (currentTask.getTaskImageCode() != null) {
            imageDownload();
            showTheImageInThePage();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: Called");
    }



    public void setActionBarTitleButton(String title) {

       /*
       https://stackoverflow.com/questions/10138007/how-to-change-android-activity-label
       */

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share_task_details) {
            shareButtonAction();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void shareButtonAction() {
        // Create the text message with a string.
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I want to share");
        sendIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(sendIntent, "");

        // Verify the original intent will resolve to at least one activity
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    private void findAllViewById() {
        state = findViewById(R.id.task_state_in_details_page);
        body = findViewById(R.id.task_body_in_details_page);
        team = findViewById(R.id.task_team_in_details_page);
        deleteButton = findViewById(R.id.delete_button);
        editButton = findViewById(R.id.edit_button);
        taskImageView = findViewById(R.id.task_image);
//        mapCardView = findViewById(R.id.location_card_view);
        translateImage = findViewById(R.id.translate_image);
        textToSpeechImage = findViewById(R.id.text_to_speech_image);
        locationMarkerImage = findViewById(R.id.location_marker_image);
        loadingDialog = new LoadingDialog(this);
    }

    private void setOnClickListeners() {

        editButton.setOnClickListener(view -> editTask());

        deleteButton.setOnClickListener(view -> deleteTaskButtonAction());

        locationMarkerImage.setOnClickListener(view -> {
            navigateToMapsPage();
        });

        translateImage.setOnClickListener(view -> {
            translateImageListener();
        });

        textToSpeechImage.setOnClickListener(view -> {
            textToSpeech();
        });

    }

    private void loadTaskInfoFromMain() {


        currentTask = MainActivity.tasksList.stream().filter(task1 -> task1.getId().equals(getIntent().getStringExtra("Position"))).collect(Collectors.toList()).get(0);

        Task finalCurrentTask = currentTask;

        teamName = SplashActivity.teamsList.stream().filter(team1 -> team1.getId().equals(finalCurrentTask.getTeamTasksId())).collect(Collectors.toList()).get(0).getName();
    }

    private void showTheImageInThePage() {

        Bitmap bMap = BitmapFactory.decodeFile(downloadedImagePath + currentTask.getTaskImageCode() + ".jpg");
        taskImageView.setImageBitmap(bMap);
    }

    private void setTextForTaskView() {


        state.setText("State => " + currentTask.getStatus());
        body.setText("Description:\n" + currentTask.getDescription());
        team.setText("This task for => " + teamName);
        setActionBarTitleButton(currentTask.getTitle());
    }

    public void deleteTaskButtonAction() {

        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(TaskDetailsActivity.this);
        deleteAlert.setTitle("Warning!");
        deleteAlert.setMessage("Are you sure to delete the task?");
        /*
        How to add alert to my program
        https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        */
        deleteAlert.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
            deleteTaskFromLocalAndApi();
            Toast.makeText(TaskDetailsActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        deleteAlert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        deleteAlert.show();
    }

    private void deleteTaskFromLocalAndApi() {

        Amplify.DataStore.query(Task.class, Where.id(currentTask.getId()),
                matches -> {
                    if (matches.hasNext()) {
                        Task task = matches.next();
                        Amplify.DataStore.delete(task,
                                deleted -> Log.i(TAG, "Deleted a task."),
                                failure -> Log.e(TAG, "Delete failed.", failure)
                        );
                    }
                },
                failure -> Log.e(TAG, "Query failed.", failure)
        );

        Amplify.API.mutate(ModelMutation.delete(currentTask),
                response -> Log.i(TAG, "Todo with id: "),
                error -> Log.e(TAG, "Create failed", error)
        );


    }

    public void editTask() {

        Intent intent = new Intent(TaskDetailsActivity.this, UpdateTaskActivity.class);
        intent.putExtra("Id", currentTask.getId());
        startActivity(intent);
    }

    @SuppressLint("SdCardPath")
    private void imageDownload() {
        downloadedImagePath = "/data/data/com.example.taskmaster/files/";
        File file = new File(downloadedImagePath);
        Log.i(TAG, "imageDownload: is the file exist -> " + file.exists());
        if (!file.exists()) {
            Amplify.Storage.downloadFile(
                    currentTask.getTaskImageCode(),
                    file,
                    result -> {
                        Log.i(TAG, "The root path is: " + getApplicationContext().getFilesDir());
                        Log.i(TAG, "Successfully downloaded: " + result.getFile().getName());

                        downloadedImagePath = result.getFile().getPath();
                    },
                    error -> Log.e(TAG, "Download Failure", error)
            );
        }
    }

    private void translateImageListener() {

        Amplify.Predictions.translateText(
                currentTask.getDescription(),
                LanguageType.ENGLISH,
                LanguageType.ARABIC,
                result -> {
                    runOnUiThread(() -> {
                        AlertDialog.Builder showTranslationAlert = new AlertDialog.Builder(TaskDetailsActivity.this);
                        showTranslationAlert.setTitle("Translate to arabic");
                        showTranslationAlert.setMessage(result.getTranslatedText());
                    /*
                    How to add alert to my program
                    https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
                    */
                        showTranslationAlert.setPositiveButton("Ok", (dialogInterface, i) -> onResume());

                        showTranslationAlert.show();
                    });
                    Log.i(TAG, result.getTranslatedText());
                },
                error -> {
                    Log.e(TAG, "Translation failed", error);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Translation Failed", Toast.LENGTH_SHORT).show();
                    });
                }
        );
    }

    private void textToSpeech() {

        Amplify.Predictions.convertTextToSpeech(
                currentTask.getDescription(),
                result -> playAudio(result.getAudioData()),
                error -> {
                    Log.e(TAG, "Conversion failed", error);

                    runOnUiThread(() -> Toast.makeText(this, "Conversion failed", Toast.LENGTH_SHORT).show());
                }
        );

    }

    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }

    }
    private void navigateToMapsPage(){

        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra(LATITUDE,currentTask.getCoordinates().get(0));
        intent.putExtra(LONGITUDE,currentTask.getCoordinates().get(1));
        startActivity(intent);

    }
}