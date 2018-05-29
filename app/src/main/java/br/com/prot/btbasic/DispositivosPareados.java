package br.com.prot.btbasic;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class DispositivosPareados extends ListActivity {

    private ListView listView;
    private LayoutInflater inflater;
    private View header;
    private TextView txtTitulo;

    private BluetoothAdapter btAdapter;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text_header);

        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.listView = getListView();
        this.inflater = getLayoutInflater();
        this.header = this.inflater.inflate(R.layout.text_header, listView, false);
        ((TextView) this.header.findViewById(R.id.txt_titulo_list)).setText(Msg.DISPOSITIVOS_PAREADOS);
        this.listView.addHeaderView(this.header, null, false);

        buscarDispositivosPareados();
    }

    private void buscarDispositivosPareados(){
        Set<BluetoothDevice> dispositivosPareados = btAdapter.getBondedDevices();

        this.arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        setListAdapter(this.arrayAdapter);

        if(dispositivosPareados.size() > 0){
            for(BluetoothDevice device : dispositivosPareados){
                this.arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String item = (String) getListAdapter().getItem(position-1);
        String dispositivoNome = item.substring(0, item.indexOf("\n"));
        String dispositivoMac = item.substring(item.indexOf("\n")-1, item.length());

        //Toast.makeText(this, "MAC " + dispositivoMac, Toast.LENGTH_SHORT).show();
        Intent itReturn = new Intent();
        itReturn.putExtra("nome", dispositivoNome);
        itReturn.putExtra("mac", dispositivoMac);
        setResult(RESULT_OK, itReturn);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
