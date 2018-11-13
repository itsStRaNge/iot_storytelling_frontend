package edu.ntnu.iot_storytelling_sensor.Manager;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.ntnu.iot_storytelling_sensor.GlideApp;
import edu.ntnu.iot_storytelling_sensor.R;

public class FileManager extends AppCompatActivity {

    private boolean DATA_SYNCED=false;
    private MediaPlayer m_mediaPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_mediaPlayer = new MediaPlayer();
    }
    /* FIREBASE NETWORKING CALLBACKS*/
    public void playAudio(String file_name){
        if(m_mediaPlayer.isPlaying()) return;
        try {
            File directory = this.getFilesDir();
            File file = new File(directory, file_name);
            m_mediaPlayer.reset();
            m_mediaPlayer.setDataSource(file.getPath());
            m_mediaPlayer.prepare();
            m_mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayText(String file_name){
        TextView text_view = findViewById(R.id.text_view);
        text_view.setText("");
        try {
            File directory = this.getFilesDir();
            File file = new File(directory, file_name);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                text_view.append(st);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

   public void showImage(String file_name){
        //RequestManager requestManager = Glide.with(this);
        File directory = this.getFilesDir();
        File file = new File(directory, file_name); // makes File object with name file
        //RequestBuilder requestBuilder = requestManager.load(file);  // returns a request builder to load file
        //requestBuilder.into(((GifImageView) findViewById(R.id.background_img)));
        GlideApp
                .with(this)
                .load(file)
                .into((ImageView) findViewById(R.id.background_img));
        //Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        //((GifImageView) findViewById(R.id.background_img)).setImageBitmap(bitmap);
    }
/*

  public void showImage(String file_name) {
      File directory = this.getFilesDir();
      File file = new File(directory, file_name);


      try {
          Log.d("showImage", "Trying to view image");
          ImageDecoder.Source source = ImageDecoder.createSource(file);
          Log.d("showImage", "Trying to view image 2");
          Drawable drawable = ImageDecoder.decodeDrawable(source);
          ((GifImageView) findViewById(R.id.background_img)).setImageDrawable(drawable);
            if (drawable instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) drawable).start();

      } catch (IOException e) {
          Log.d("showImage", e.toString());
      } catch (Exception e) {
          Log.d("showImage", "Caught an exception");
          Log.d("showImage", e.toString());
      }
  }
        //((GifImageView) findViewById(R.id.background_img)).setImageDrawable(drawable));


*/


    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
            DATA_SYNCED = false;
        } catch (Exception e) { e.printStackTrace();}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    public void download_finished(Boolean success) {
        if (success){
            DATA_SYNCED = true;
            Toast.makeText(getApplicationContext(), "Data sync complete!",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Failed to Download Components",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean data_synced(){
        if(!DATA_SYNCED){
            Toast.makeText(getApplicationContext(), "Data not synchronized!",
                    Toast.LENGTH_LONG).show();
        }
        return DATA_SYNCED;
    }
}
