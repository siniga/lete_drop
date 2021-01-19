package com.agnet.lete_drop.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseArray;
import com.agnet.lete_drop.R;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class ScannerActivity extends AppCompatActivity implements BarcodeRetriever {
    private BarcodeCapture barcodeCapture;
    private DatabaseHandler _dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        barcodeCapture.setRetrieval(this);


        _dbHandler = new DatabaseHandler(this);


    /*    .setSupportMultipleScan(supportMultiple.isChecked())
                .setTouchAsCallback(touchBack.isChecked())
                .shouldAutoFocus(autoFocus.isChecked())
                .setShowFlash(flash.isChecked())
                .setBarcodeFormat(Barcode.ALL_FORMATS)
                .setCameraFacing(frontCam.isChecked() ? CameraSource.CAMERA_FACING_FRONT : CameraSource.CAMERA_FACING_BACK)
                .setShouldShowText(drawText.isChecked());*/
    }

    @Override
    public void onRetrieved(final Barcode barcode) {
        // Log.d(TAG, "Barcode read: " + barcode.displayValue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

               // Log.d("BACKCODEREADER", "Barcode read: " + barcode.displayValue);

            }
        });

//        barcodeCapture.stopScanning();

    }

    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Code selected : " + closetToClick.displayValue + "\n\nother " +
                        "codes in frame include : \n";
                for (int index = 0; index < barcodeGraphics.size(); index++) {
                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
                    message += (index + 1) + ". " + barcode.displayValue + "\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this)
                        .setTitle("code retrieved")
                        .setMessage(message);
                builder.show();
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        //alertDialog.cancel();
    }


}
