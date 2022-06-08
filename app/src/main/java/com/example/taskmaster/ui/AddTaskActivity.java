package com.example.taskmaster.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;

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
@SuppressLint({"SetTextI18n", "MissingPermission", "WrongThread"})
public class AddTaskActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = AddTaskActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 123;
    private final int PERMISSION_ID = 44;

    private Task task;

    private List<String> coordinateList = new ArrayList<>();

    private EditText taskTitle, taskDescription;

    private Spinner taskState;

    private Button addTaskButton, uploadImageButton,addLocationButton;

    public static final String TASK_ID = "TaskID";

    private String taskImageKey, taskTitleString, taskDescriptionString, taskStateString;

    private File file;

    private boolean isFromMainActivity;

    private GoogleMap googleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        prepareGoogleMaps();

        findAllViewsByIdMethod();

        backToPreviousButton();

        setAdapterToStatesTaskArraySpinner();

        setAdapterToTeamArraySpinner();

        buttonsAction();

        handleSharedImageAndText();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: Called");
        if (!isFromMainActivity){
        }
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void prepareGoogleMaps() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //method to get the location
        getLastLocation();
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
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            // so the index 0 represent the latitude and the index 1 represent the longitude in coordinate list

                            coordinateList.add(location.getLatitude()+"");
                            coordinateList.add(location.getLongitude()+"");

                        }
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

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());

    }

    private LocationCallback mLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude());
//            longitudeTextView.setText("Longitude : " + mLastLocation.getLongitude());

        }
    };

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


    private void findAllViewsByIdMethod() {

        addTaskButton = findViewById(R.id.create_task_button);
        uploadImageButton = findViewById(R.id.upload_photo_button);
        addLocationButton = findViewById(R.id.add_location_button);
        taskTitle = findViewById(R.id.task_title_box);
        taskDescription = findViewById(R.id.task_description_box);
        taskState = findViewById(R.id.task_states_spinner);
        TextView totalTask = findViewById(R.id.tasks_count);
        Spinner taskTeamSpinner = findViewById(R.id.task_team_spinner);
        taskTeamSpinner.setSelection(0);
        taskTeamSpinner.setEnabled(false);
    }

    private void backToPreviousButton() {
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video I learned how to add back button in action bar
        */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

    private void setAdapterToTeamArraySpinner() {


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

    private void buttonsAction() {

        uploadImageButton.setOnClickListener(view -> {
            bringPhotoFromGallery();
        });

        addTaskButton.setOnClickListener(view -> {
            getAllStringFormEditText();
            addTaskButtonAction();
        });

        addLocationButton.setOnClickListener(view -> {
            //showTheCurrentLocationForUser();
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
            Uri dataUri = data.getData();
            convertBitmapToFile(dataUri);

            // Do stuff with the photo/video URI.
        }
    }

    private void convertBitmapToFile(Uri currentUri) {

        try {

            Bitmap bitmap = getBitmapFromUri(currentUri);
            if (taskTitleString == null && taskTitle.getText().toString() != null)
                taskTitleString = taskTitle.getText().toString();
            else
                taskTitleString = "Task";
            taskImageKey = taskTitleString.toLowerCase().replace(" ", "_") + "_" + generateRandomString(10).toLowerCase();
            file = new File(getApplicationContext().getFilesDir(), taskImageKey + ".jpg");
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "The URI is => " + currentUri, Toast.LENGTH_SHORT).show();
        return;
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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    private void getAllStringFormEditText() {

        taskTitleString = taskTitle.getText().toString();
        taskDescriptionString = taskDescription.getText().toString();
        taskStateString = taskState.getSelectedItem().toString();
    }

    private void addTaskButtonAction() {
        if (TextUtils.isEmpty(taskTitle.getText()) || TextUtils.isEmpty(taskDescription.getText())) {

            taskTitle.setError("Title is Required");
            taskDescription.setError("Description is required");
        } else {
            Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
            saveToCloudDB();
            navigateToMainPage();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }

    private void saveToCloudDB() {

        // Lab 32 \\
        Team team1 = SplashActivity.teamsList.stream().filter(team -> team.getName().equals("First Team")).
                collect(Collectors.toList()).get(0);

//        Team team2 = Team.builder().name("Second Team").build();
//        Team team3 = Team.builder().name("Third Team").build();

        task = Task.builder()
                .title(taskTitleString)
                .description(taskDescriptionString)
                .status(taskStateString)
                .teamTasksId(team1.getId())
                .coordinates(coordinateList)
                .taskImageCode(taskImageKey)
                .build();

        // Data store save
        Amplify.DataStore.save(team1,
                success -> {
                    Amplify.DataStore.save(task,
                            savedTask -> {
                                Log.i(TAG, "Task in add task page " + team1.getId());
                            },
                            failure -> {
                                Log.e(TAG, "Task not saved.", failure);
                                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                    );
                    Log.i(TAG, "Team in add task page " + success.item().getName());
                },
                error -> {
                    Log.e(TAG, "Could not save item to DataStore ", error);
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
        );

        // API save to backend
        Amplify.API.mutate(ModelMutation.create(team1),
                success -> Amplify.API.mutate(ModelMutation.create(task),
                        successTask -> {
                            if (!(taskImageKey == null)) {
                                uploadImage();
                            }
                            Log.i(TAG, "Task saved to team from API => " + successTask.getData().getId());
                        },
                        failure -> {
                            Log.i(TAG, "Task not saved to the team from API => ");
                            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }),
                error -> {
                    Log.e(TAG, "Could not save team to API ", error);
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
        );
        /*
         * https://stackoverflow.com/questions/66576755/io-reactivex-exceptions-undeliverableexception-the-exception-could-not-be-delive
         */
        RxJavaPlugins.setErrorHandler(e -> {
        });
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

    private void navigateToMainPage() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TASK_ID, task.getId());
        startActivity(intent);
        finish();
    }

    /*
     * https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
     */
    private void handleSharedImageAndText() {

        Intent intent = getIntent();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(intent.getAction()) && type != null) {
            if ("text/plain".equals(type)) {
                Log.i(TAG, "handleSendText: Type => " + type);
                isFromMainActivity = false;
                taskDescription.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            } else if (type.startsWith("image/")) {
                Log.i(TAG, "handleSendImage: Type => " + type);
                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                convertBitmapToFile(intent.getParcelableExtra(Intent.EXTRA_STREAM));
                isFromMainActivity = false;
            } else {
                isFromMainActivity = true;
            }
        }
    }

    private void showTheCurrentLocationForUser(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Add Location");
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_maps,null));
        builder.setCancelable(true);

        builder.setPositiveButton("Add", (dialogInterface, i) -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("Is from AddTaskActivity",AddTaskActivity.class.getSimpleName());
            Toast.makeText(this, "Location Added", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> onResume());

        builder.show();
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


}