package br.com.prot.btbasic;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    private static TextView txtStatusMessage;
    private TextView txtPareados, txtBuscarDispositivos;
    private TextView txtDispositivoNome;
    private TextView txtDispositivoMac;

    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkEnableBluettoth();
        this.txtPareados.setOnClickListener(this);
        this.txtBuscarDispositivos.setOnClickListener(this);
    }

    private void initUi(){
        this.txtStatusMessage = (TextView) findViewById(R.id.txt_status_message);
        this.txtPareados = (TextView) findViewById(R.id.txt_buscar_pareados);
        this.txtBuscarDispositivos = (TextView) findViewById(R.id.txt_buscar_dispositivos);
        this.txtDispositivoNome = (TextView) findViewById(R.id.txt_dispositivo_nome);
        this.txtDispositivoMac = (TextView) findViewById(R.id.txt_dispositivo_endereco);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_buscar_pareados:
                buscarDispositivosPareados();
            break;
            case R.id.txt_buscar_dispositivos:
                buscarDispositivosProximos();
            break;
        }
    }

    private void checkEnableBluettoth(){

        if(this.btAdapter != null){
            if(!this.btAdapter.isEnabled()){
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ENABLE_BLUETOOTH);
                this.txtStatusMessage.setText(Msg.AGUARDANDO_USUARIO);
            }else{
                this.txtStatusMessage.setText(Msg.BLUETOOTH_ATIVO);
            }
        }else{
            this.txtStatusMessage.setText(Msg.SUPORTE_BLUETOOTH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ENABLE_BLUETOOTH){
            if(resultCode == RESULT_OK){
                txtStatusMessage.setText(Msg.BLUETOOTH_ON);
            }else{
                txtStatusMessage.setText(Msg.BLUETOOTH_OFF);
            }
        }else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE){
            if(resultCode == RESULT_OK){
                this.txtDispositivoNome.setText(data.getStringExtra("nome"));
                this.txtDispositivoMac.setText(data.getStringExtra("mac"));
            }else{

            }
        }
    }

    public void buscarDispositivosPareados(){
        startActivityForResult(new Intent(this, DispositivosPareados.class), SELECT_PAIRED_DEVICE);
    }

    public void buscarDispositivosProximos(){
        startActivityForResult(new Intent(this, BuscaPorDispositivos.class), SELECT_DISCOVERED_DEVICE);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                txtStatusMessage.setText("Ocorreu um erro durante a conexão");
            else if(dataString.equals("---S"))
                txtStatusMessage.setText("Conectado");
            else {
                txtStatusMessage.setText(new String(data));
            }
        }
    };
}
