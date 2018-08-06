package szebra.remotespeaker;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  private AudioTrack track;
  private int minBuffer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    /*minBuffer = AudioTrack.getMinBufferSize(
      44100,
      AudioFormat.CHANNEL_OUT_STEREO,
      AudioFormat.ENCODING_PCM_16BIT
    );*/
    minBuffer = 8192;
    
    track = new AudioTrack(
      AudioManager.STREAM_MUSIC,
      44100,
      AudioFormat.CHANNEL_OUT_STEREO,
      AudioFormat.ENCODING_PCM_16BIT,
      minBuffer,
      AudioTrack.MODE_STREAM
    );
    track.play();
    
   /* track = new AudioTrack.Builder()
      .setAudioAttributes(new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
      .setAudioFormat(new AudioFormat.Builder()
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setSampleRate(44100)
        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
        .build())
      .setBufferSizeInBytes(10386)
      .build();*/
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {
        try {
//          InetAddress address = Inet4Address.getByName("127.0.0.1");
          DatagramSocket socket = new DatagramSocket(5000);
          Log.d("MAIN_ACTIVITY", "Socket opened on " + socket.getLocalSocketAddress().toString());
          byte[] buf = new byte[minBuffer];
          while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            track.write(packet.getData(), 0, packet.getLength());
            Log.d("MAIN_ACTIVITY", Arrays.toString(packet.getData()));
          }
        } catch (IOException e) {
          e.printStackTrace();
          finish();
        }
        
        return null;
      }
    }.execute();
  }
}
