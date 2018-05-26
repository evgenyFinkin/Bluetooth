package com.example.jonfi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Context mContext = MainActivity.this;
    ListView lvDeviceList;
    Switch swiTurnBTOnOff;
    TextView tvMsg;
    EditText etMsg;
    Button btnBTScan, btnDiscoverability, btnSend;

    ArrayList<String> deviceName = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    int REQUEST_ENABLE_BT = 1000;

    private static final String APP_NAME = "Bluetooth";
    private static final UUID mUUID = UUID.fromString("7f0eb427-7fbc-41da-bdb2-c5445c57035b");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swiTurnBTOnOff = (Switch)findViewById(R.id.swiTurnBTOnOff);
        btnDiscoverability = (Button)findViewById(R.id.btnDiscoverability);
        btnBTScan = (Button)findViewById(R.id.btnBTScan);
        btnSend = (Button)findViewById(R.id.btnSend);
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        etMsg = (EditText)findViewById(R.id.etMsg);
        lvDeviceList = (ListView)findViewById(R.id.lvDeviceList);

        btnDiscoverability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mBluetoothAdapter.isEnabled())    {
                    Toast.makeText(mContext, "Bluetooth must be on first", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                startActivity(intent);
            }
        });


        btnBTScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter.startDiscovery();
            }
        });


        swiTurnBTOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true)    {

                    if (mBluetoothAdapter == null) {
                        Toast.makeText(mContext, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
                    }
                    if(!mBluetoothAdapter.isEnabled())  {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }else {
                    if(mBluetoothAdapter.isEnabled())    {
                        mBluetoothAdapter.disable();
                    }
                    Toast.makeText(mContext, "Bluetooth off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);
        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, deviceName);
        lvDeviceList.setAdapter(arrayAdapter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver()   {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))    {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceName.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(mContext, "Bluetooth on", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}