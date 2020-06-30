package br.com.panico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClienteConfig extends AppCompatActivity {
    private static final String FILE_NAME = "ClientConfig.txt";
    private TextView mEditTelefone;
    private TextView mEditServidor;
    private TextView mEditConta;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cliente_config );
        mEditTelefone = findViewById( R.id.editTelefone );
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            mEditTelefone.setBackgroundColor( getResources().getColor( android.R.color.white ) );
            mEditTelefone.setEnabled( true );
        }
        mEditServidor = findViewById( R.id.editServidor );
        mEditConta = findViewById( R.id.editConta );
        btnOk = findViewById( R.id.buttonOk );
        loadFile();
    }

    public void saveFile(View view){
        String telefone = mEditTelefone.getText().toString().trim();
        String servidor = mEditServidor.getText().toString().trim();
        String conta = mEditConta.getText().toString().trim();
        if(!telefone.isEmpty() || !servidor.isEmpty() || !conta.isEmpty()) {
            String text = ( conta + "," +servidor + "," + telefone + "," );
            FileOutputStream fos = null;

            try {
                fos = openFileOutput( FILE_NAME, MODE_PRIVATE );
                fos.write( text.getBytes() );
                mEditTelefone.setText( "" );
                mEditServidor.setText( "" );
                mEditConta.setText( "" );
                MainActivity.conta = conta;
                MainActivity.servidor = servidor;
                MainActivity.phoneNumber = telefone;
                Intent config = new Intent( ClienteConfig.this, MainActivity.class );
                startActivity( config );
                finish();
                //  Toast.makeText( this, "Save to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
                Log.i( "Configuração ", "Save to " + getFilesDir() + "/" + FILE_NAME );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{

            Snackbar.make(view, "Deve ser preenchido todos os campos", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }

    public void loadFile() {
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
                mEditServidor.setText( servidor );
                mEditConta.setText( conta);
                mEditTelefone.setText( telefone );
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
}
