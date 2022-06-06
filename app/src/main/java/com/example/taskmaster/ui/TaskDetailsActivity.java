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
public class TaskDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();

    private ImageView taskImageView, locationMarkerImage, translateImage, textToSpeechImage;

    private Task currentTask = null, taskFromAddTaskPage = null;

    private String teamName = "", downloadedImagePath;

    private TextView state, body, team, latitudeTextView, longitudeTextView;

    private CardView mapCardView;

    private final int PERMISSION_ID = 44;

    private Button deleteButton, editButton;

    private double latitude, longitude;

    private GoogleMap googleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final MediaPlayer mp = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        findAllViewById();

        loadTaskInfoFromMain();

        setTextForTaskView();

        setOnClickListeners();


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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        prepareGoogleMaps();
    }

    private void prepareGoogleMaps() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //method to get the location
        getLastLocation();

        //get a handle to the fragment and register the callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.location_fragment);
        mapFragment.getMapAsync(this);
    }

    private void getLastLocation() {
        //check if the permission is given
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                //get last location from FusedLocationClient object
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Location location = task.getResult();

                        // the index 0 represent the latitude and the index 1 represent the longitude in coordinate list
                        latitude = Double.parseDouble(currentTask.getCoordinates().get(0));
                        longitude = Double.parseDouble(currentTask.getCoordinates().get(1));


                        Log.i(TAG, "onComplete: The latitude and longitude is -> " + location.getLatitude() + "-> " + location.getLongitude());
                        Log.i(TAG, "Google map is -> " + googleMap);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title("Marker"));
                    }
                });

            } else {
                Toast.makeText(this, "Please turn on your location....", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permission arent available request for permission
            requestPermission();
        }
    }

    private boolean checkPermissions() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        /*
         * If you want a background location in android 10 and higher use
         * Manifest.permission.ACCESS_BACKGROUND_LOCATION
         */
    }

    private boolean isLocationEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
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
        mapCardView = findViewById(R.id.location_card_view);
        translateImage = findViewById(R.id.translate_image);
        textToSpeechImage = findViewById(R.id.text_to_speech_image);
    }

    private void setOnClickListeners() {

        editButton.setOnClickListener(view -> editTask());

        deleteButton.setOnClickListener(view -> deleteTaskButtonAction());

        mapCardView.setOnClickListener(view -> {

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
}