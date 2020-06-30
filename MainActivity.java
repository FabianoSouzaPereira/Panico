package br.com.panico;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

;import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    public static String servidor = "";
    public static String conta = "";
    public static String phoneNumber = "";
    public static double latitude = 0;
    public static double longitude = 0;
    public static final String evento = "E120";
    public static String status = "0";
    public static int phoneState = -1;
    private static final String FILE_NAME = "ClientConfig.txt";
    public static final int PRIORITY_HIGTH_ACCURACY = 100;
    public static final int PERMISSION_CODE = 3;
    FusedLocationProviderClient client;
    GeofencingClient geofencingClient;
    LocationCallback locationCallback;
    ListView listView;
    ArrayList<View> arrayList = new ArrayList<View>();
    ArrayAdapter<String> arrayAdapter;
    private TextView tvCoodinate;
    private View btnItemView;
    private View itemposition;
    private View itemmap;

    private ImageView btnPanico;
    private TextView txtStatus;
    private TextView txtValor;
    private TextView txtHostPort;
    private String user = "samsung";
    private SocketTask st;
    int Permission_All = 1;
    String[] Permissions = {Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public MainActivity() {
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mainloadFile();
        client = LocationServices.getFusedLocationProviderClient( this );
        geofencingClient = LocationServices.getGeofencingClient( this );
        btnPanico = (ImageView) findViewById( R.id.btnPanico );
        btnPanico.setOnClickListener( btnConnectListener );
        txtStatus = findViewById( R.id.txtStatus );

    }

    @TargetApi(VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            if (!hasPhonePermissions( this, Permissions )) {
                ActivityCompat.requestPermissions( this,Permissions,Permission_All );
            }
        }
        int errorcode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable( MainActivity.this );
        switch (errorcode) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Log.i( "Teste", "Show dialog ==================================" );
                GoogleApiAvailability.getInstance().getErrorDialog( MainActivity.this, errorcode, 0, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                } ).show();
                break;
            case ConnectionResult.SUCCESS:
                Log.i( "Teste", "Google Play Services up-to-date =================================" );
                break;
        }

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        client.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                            Log.i( "Location: ", ""+location.getLatitude());
                            Log.i( "Longitude: ","" + location.getLongitude());
                            Log.i("Bearing: ","" + location.getBearing());
                            Log.i("Altitude: ","" + location.getAltitude());
                            Log.i("Speed: ","" + location.getSpeed());
                            Log.i("Provider: ","" + location.getProvider());
                            Log.i("Accuracy: ","" + location.getAccuracy());
                            Log.i("Hora: ","" + DateFormat.getTimeInstance().format( new Date() ) + "  ======= " );
                } else {
                    Log.i( "location - ", "null" );
                }
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        } );
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval( 60 * 1000 );
        locationRequest.setFastestInterval( 30 * 1000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY ); //uso preciso com gps.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest( locationRequest );
        SettingsClient settingsClient = LocationServices.getSettingsClient( this );
        settingsClient.checkLocationSettings( builder.build() ).addOnSuccessListener( new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i( "teste", locationSettingsResponse.getLocationSettingsStates().isNetworkLocationPresent() + "" );
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult( MainActivity.this, 10 );
                    } catch (IntentSender.SendIntentException el) {

                    }
                }
            }
        } );
        //Listener pega sempre a nova posição do provider. Kill app se local é nulo.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i( "Local position -> ", "local is null" );
                    return;
                }
                //procurar na lista de localição.
                for (Location location : locationResult.getLocations()) {
                    Log.i( "Location pos -> ", location.getLatitude() + " " + location.getLongitude() );
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.i( "locationAvailability : ", locationAvailability.isLocationAvailable() + "" );
            }
        };
        client.requestLocationUpdates( locationRequest, locationCallback, null );
    }


    @SuppressLint("SimpleDateFormat")
    private View.OnClickListener btnConnectListener = new View.OnClickListener() {
        @SuppressLint("StaticFieldLeak")
        public void onClick(View v) {
            try {
                // Recupera host e porta
                String hostPort = String.valueOf( servidor ).trim();
                int idxHost = hostPort.indexOf(":");
                final String host = hostPort.substring(0, idxHost);
                final int port = Integer.parseInt( (hostPort.substring(idxHost+1)) );

                // Instancia a classe de conexão com socket   "179.184.92.101", 5198
                st = new SocketTask( host, port, 10000 ) {
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
                        // Recupera o retorno
                        txtStatus.setText( sdf.format( new Date() ) + " - " + progress[0] );
                    }
                };
                String dados = ("#" + conta + "," + phoneNumber + "," + latitude + "," + longitude + "," + evento + "," + status +"$");
                st.execute( dados );

            } catch (Exception e) {
                Log.i( "Exeption - > ", "" + e );
            }
        }
    };

    public void mainloadFile() {
        File file = new File( getFilesDir() + "/" + FILE_NAME );
        if (file.exists()) {
            FileInputStream fis = null;
            try {

                fis = openFileInput( FILE_NAME );
                InputStreamReader isr = new InputStreamReader( fis );
                BufferedReader br = new BufferedReader( isr );
                StringBuilder sb = new StringBuilder();
                String text;

                while ((text = br.readLine()) != null) {
                    sb.append( text ).append( "\n" );
                }

                String read = sb.toString();
                String [] v = read.split(",");
                String conta = v[0];
                String servidor = v[1];
                String telefone = v[2];
                MainActivity.conta = conta;
                MainActivity.servidor = servidor;
                MainActivity.phoneNumber = telefone;


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (locationCallback != null) {
            stopLocationUpdate();
        }
    }

    /** Remove recursos para liberar memória  */
    private void stopLocationUpdate() {
        client.removeLocationUpdates( locationCallback );
    }


    @SuppressLint("InlinedApi")
    @RequiresApi(api = VERSION_CODES.M) //ver: 23
    private static boolean hasPhonePermissions(Context context, String... permissions) {
        if(Build.VERSION.SDK_INT >= VERSION_CODES.M && context != null && permissions != null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;


    }

    @RequiresApi(api = VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText( MainActivity.this, "Permissão aceita" , Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText( MainActivity.this, "Permissão negada" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Inspection info:This check scans through your code and libraries and looks at the APIs being used,
     *  and checks this against the set of permissions required to access those APIs.
     *  If the code using those APIs is called at runtime, then the program will crash.   */
    //@RequiresApi(api = VERSION_CODES.KITKAT)
    //@TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    public void getPHONElowerVer(){
        String[] permission = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS};
        try {
          //  String phoneNumber = "";
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                    phoneNumber = telephonyMgr.getLine1Number().toString();  Log.i( "PhoneNumber: ","" + phoneNumber );
                    phoneState =  telephonyMgr.getSimState();                Log.i( "PhoneState:A ","" + phoneState );
                } else {
                    if (telephonyMgr != null) {
                        phoneNumber = telephonyMgr.getSubscriberId();        Log.i( "PhoneNumber: ","" + phoneNumber );
                    }

                    phoneState =  telephonyMgr.getSimState();                Log.i( "PhoneState: B","" + phoneState );
                }
            }else{
                ActivityCompat.requestPermissions( MainActivity.this, permission,1);
                Log.i( "Manifest.permission: " ,"NOT PERMISSION_GRANTED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent config = new Intent( MainActivity.this, ClienteConfig.class );
            startActivity( config );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
