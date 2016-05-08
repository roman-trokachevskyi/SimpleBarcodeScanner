package com.rodico.simplebarcodescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import gk.android.investigator.Investigator;

/**
 * Created by duke0808 on 26.04.16.
 */
public class ViewModel {
    MainActivity activity;
    private Barcode barcodeResult;
    int scanned = 0;
    StringBuilder otputRes = new StringBuilder("");
    ObservableField<String> prefix = new ObservableField<>("");
    ObservableField<String> showingRes = new ObservableField<>();
    ObservableField<String> showingScanned = new ObservableField<>();
    String qty;
    String fileName;

    public ViewModel(MainActivity activity) {
        this.activity = activity;
        otputRes = new StringBuilder("");
        otputRes.append("\n");
        showingRes.set("");
        EditText fileNameEt = activity.binding.fileNameEt;
        ((EditText) fileNameEt).setSelection(fileNameEt.getText().length());
    }

    public void clear() {
        scanned = 0;
        otputRes = new StringBuilder("");
        otputRes.append("\n");
        showingRes.set("");
        qty = "0";
    }

    public void proposeClear(View view) {
        AlertDialog dialog = new AlertDialog.Builder(activity).setMessage("Очистити данні?")
                .setTitle("Ви впевнені?")
                .setPositiveButton("Очистити", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clear();
                    }
                })
                .setNegativeButton("Відміити", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    private void startScan() {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        prefix.set(activity.binding.prefEt.getText().toString());
                        qty = activity.binding.qtyEt.getText().toString();
                        String str = barcodeResult.rawValue + "-" + prefix.get() + "-" + qty + "\n";
                        otputRes.append(str);
                        Investigator.log(this, "Scanned: --- " + str);
                        showingRes.set(showingRes.get() + str);
                        scanned++;
                        showingScanned.set(String.valueOf(scanned));
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    public void export(View view) {
        Investigator.log(this, otputRes.toString() + "\n\n\n\n");
        Date date = new Date();
        File file = null;
        fileName = activity.binding.fileNameEt.getText().toString();
        try {
            file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName + ".txt");
            file.createNewFile();
//            file.deleteOnExit();
            file.setReadable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            String s = otputRes.toString();
            for (int i = 0; i < s.length(); i++) {
                stream.write(s.charAt(i));
            }
            stream.close();
            Intent shareIntent = new Intent();
            long length = file.length();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType("file/*");
            activity.startActivity(Intent.createChooser(shareIntent, "Share images to.."));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onScanClick(View view) {
        startScan();
    }

    public ObservableField<String> getShowingScanned() {
        return showingScanned;
    }

    public void setShowingScanned(ObservableField<String> showingScanned) {
        this.showingScanned = showingScanned;
    }

    public ObservableField<String> getShowingRes() {
        return showingRes;
    }

    public void setShowingRes(ObservableField<String> showingRes) {
        this.showingRes = showingRes;
    }

    public ObservableField<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(ObservableField<String> prefix) {
        this.prefix = prefix;
    }
}
