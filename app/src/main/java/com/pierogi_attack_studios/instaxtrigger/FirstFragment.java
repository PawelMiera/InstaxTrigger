package com.pierogi_attack_studios.instaxtrigger;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FirstFragment extends Fragment {

    public FirstFragment() {}

    Bluetooth bluetooth;
    MainActivity activity;

    List<BluetoothDevice> discovered_devices = new ArrayList<>();
    List<BluetoothDevice> paired_devices;

    String current_mac;

    boolean registered = false;

    ArrayAdapter<String> discoveredDevicesAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (MainActivity) getActivity();

            if(bluetooth == null)
            {
                bluetooth = new Bluetooth(activity, this);
            }
            else
            {
                bluetooth.activity = activity;
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(bluetooth == null)
        {
            bluetooth = new Bluetooth(activity, this);
        }
        current_mac = dataSaver.loadStringData(activity, dataSaver.MAC);

        if(!current_mac.equals(""))
        {
            connectDevice(current_mac);
        }

        view.findViewById(R.id.button_first).setOnClickListener(view1 -> {

            registered = true;
            paired_devices = bluetooth.bluetoothManager.getPairedDevicesList();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(mReceiver, filter);

            showDevicePickDialog();
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discovered_devices.add(device);
                discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    private void showDevicePickDialog() {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(new View(activity));
        dialog.setContentView(R.layout.layout_bluetooth);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }catch (NullPointerException ex){/*nic*/}


        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);

        ListView pairedView = dialog.findViewById(R.id.pairedDeviceList);
        ListView discoveredView = dialog.findViewById(R.id.discoveredDeviceList);

        discoveredView.setAdapter(discoveredDevicesAdapter);
        pairedView.setAdapter(pairedDevicesAdapter);

        if (paired_devices.size() > 0) {
            for (BluetoothDevice device : paired_devices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        discoveredView.setOnItemClickListener((parent, view, position, id) -> {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            dialog.dismiss();
        });

        pairedView.setOnItemClickListener((adapterView, view, i, l) -> {

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            connectDevice(address);


            dialog.dismiss();
        });

        dialog.setOnKeyListener((arg0, keyCode, event) -> {

            /*if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (bluetoothData.bluetoothAdapter.isDiscovering()) {
                    bluetoothData.bluetoothAdapter.cancelDiscovery();
                }
                bluetoothData.IsMaster=false;
                dialog.dismiss();
            }
            return true;*/
            return true;
        });
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("My Bluetooth App", "closed");
            }
        });
    }

    boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    void connectDevice(String mac) {

        Disposable x = bluetooth.bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {


        bluetooth.deviceInterface = connectedDevice.toSimpleDeviceInterface();

        if(!current_mac.equals(connectedDevice.getMac()))
            dataSaver.saveData(activity, dataSaver.MAC, connectedDevice.getMac());

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, new SecondFragment(bluetooth));
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void onError(Throwable error) {
        Log.d("error", error.toString());
    }


    @Override
    public void onDestroyView() {
        if(registered)
            activity.unregisterReceiver(mReceiver);

        super.onDestroyView();
    }

}