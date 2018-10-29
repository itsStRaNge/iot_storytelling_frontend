package edu.ntnu.iot_storytelling_sensor;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ntnu.iot_storytelling_sensor.Manager.FileManager;
import edu.ntnu.iot_storytelling_sensor.Manager.UploadInterface;
import edu.ntnu.iot_storytelling_sensor.Manager.UploadManager;
import pl.droidsonroids.gif.GifImageView;


/* Implements all utilities to display and interact with the User as Sensor App */

public abstract class SensorUtilities extends FileManager implements View.OnDragListener,
                                                            View.OnClickListener,
                                                            View.OnTouchListener,
                                                            UploadInterface {

    public final static int QR_Call = 0;
    public final static int PERMISSION_REQUEST_CAMERA = 1;

    private GifImageView m_bird_in_box;
    private GifImageView m_bird_outside_box;

    private String m_qr_code="code0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Configuration.isSensor()) {
            /* display sensor background */
            findViewById(R.id.camera_button).setVisibility(View.VISIBLE);

            /* Check for permissions */
            check_camera_permission();

            /* Drag and Drop Init */
            findViewById(R.id.guitar_layout).setOnDragListener(this);
            findViewById(R.id.guitar_layout).setOnClickListener(this);
            findViewById(R.id.piano_layout).setOnDragListener(this);
            findViewById(R.id.piano_layout).setOnClickListener(this);
            findViewById(R.id.parent_view).setOnDragListener(this);

            m_bird_in_box = (GifImageView) findViewById(R.id.myimage_fields);
            m_bird_in_box.setOnTouchListener(this);
            m_bird_outside_box = (GifImageView) findViewById(R.id.myimage_rel);
            m_bird_outside_box.setOnTouchListener(this);
        }
    }


    /* PERMISSION REQUEST FOR CAMERS */
    private void check_camera_permission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CAMERA);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    check_camera_permission();
                }
            }
        }
    }

    public void checkBirdVisibility(String state){
        if(state.equals(Configuration.BIRD_STATE_VIS)){
            if(m_bird_in_box.getVisibility() == View.INVISIBLE
                    && m_bird_outside_box.getVisibility() == View.INVISIBLE){
                m_bird_in_box.setVisibility(View.VISIBLE);
            }
        }
        if(state.equals(Configuration.BIRD_STATE_INVIS)){
            m_bird_outside_box.setVisibility(View.INVISIBLE);
            m_bird_in_box.setVisibility(View.INVISIBLE);
        }
    }

    /* ON TOUCH LISTENER */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()){
            case R.id.myimage_fields:
            case R.id.myimage_rel:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.guitar_layout:
            case R.id.piano_layout:
                if(m_bird_outside_box.getVisibility() == View.INVISIBLE
                        && m_bird_in_box.getVisibility() == View.INVISIBLE){
                    create_request();
                }
                break;
        }
    }
    @Override
    public boolean onDrag(View v, DragEvent event) {
        Drawable enterShape = getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getDrawable(R.drawable.shape);

        switch(event.getAction()) {
           /* case DragEvent.ACTION_DRAG_ENTERED:
                // TODO add boarders when hovering over it
                if(v.getId() != R.id.parent_view)
                    v.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                // TODO remove boarders
                if(v.getId() != R.id.parent_view)
                    v.setBackground(normalShape);
                break;*/
            case DragEvent.ACTION_DROP:
                ViewGroup container = (ViewGroup) v;

                if(container.getId() == R.id.parent_view){
                    float x_cord = event.getX() - m_bird_outside_box.getWidth() / 2;
                    float y_cord = event.getY() - m_bird_outside_box.getHeight() / 2;
                    m_bird_outside_box.setX(x_cord);
                    m_bird_outside_box.setY(y_cord);
                    m_bird_outside_box.setVisibility(View.VISIBLE);
                    m_bird_outside_box.bringToFront();
                    m_bird_in_box.setVisibility(View.INVISIBLE);
                }else{
                    ViewGroup owner = (ViewGroup) m_bird_in_box.getParent();
                    owner.removeView(m_bird_in_box);
                    container.addView(m_bird_in_box);
                    v.setBackground(normalShape);
                    m_bird_in_box.setVisibility(View.VISIBLE);
                    m_bird_outside_box.setVisibility(View.INVISIBLE);
                }
                create_request();
                break;
        }
        return true;
    }

    /* QR CODE SCANNER CALLBACK*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_Call) {
            if (resultCode == RESULT_OK) {
                m_qr_code = data.getStringExtra("scanCode");
                Log.d("QR_CODE", m_qr_code);
                create_request();
            } else {
                Log.d("Error", "Could not read QR Code");
            }
        }
    }

    /* NETWORKING */
    private void create_request(){
        int position = 0; // value 3 if m_rel_obj is active
        
        if(m_bird_in_box.getVisibility() == View.VISIBLE){
            ViewGroup parent = (ViewGroup) m_bird_in_box.getParent();
            switch(parent.getId()){
                case R.id.guitar_layout:
                    position = 1;
                    break;
                case R.id.piano_layout:
                    position = 2;
                    break;
                }
        }

        try {
            JSONObject json_pkg = new JSONObject();

            json_pkg.put("position", position);
            json_pkg.put("qr_code", m_qr_code);

            startRequest(json_pkg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* UploadManager CALLBACKS */
    @Override
    public void startRequest(JSONObject packet) {
        if(!data_synced()){
            return;
        }

        UploadManager network = new UploadManager(this);
        network.send(packet);
    }

    @Override
    public void serverResult(String result) {
        if(!result.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
