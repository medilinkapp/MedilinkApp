package com.jat.medilinkapp.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.jat.medilinkapp.R;

import java.util.HashMap;

public class Effects {

    private static final String TAG = Effects.class.toString();
    private static final Effects INSTANCE = new Effects();

    public enum Sound {
        RIGHT,
        CLICK,
        WRONG,
    }

    private Effects() {
    }

    public static Effects getInstance() {
        return INSTANCE;
    }

    private SoundPool soundPool;
    private HashMap<Sound, Integer> soundMap;
    private int priority = 1;
    private int no_loop = 0;
    private int volume;
    private float normal_playback_rate = 1f;

    private Context context;

    public void init(Context context) {
        this.context = context;
        soundMap = new HashMap<Sound, Integer>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // code greater or equal to API 21 (lollipop)
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build();

            soundPool =
                    new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(audioAttributes).build();
        } else {
            // code for all other versions
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        }

        soundMap.put(Sound.WRONG, soundPool.load(context, R.raw.bad, 1));
        soundMap.put(Sound.CLICK, soundPool.load(context, R.raw.click, 1));
        soundMap.put(Sound.RIGHT, soundPool.load(context, R.raw.good, 1));

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1, 1, priority, no_loop, normal_playback_rate);
    }

    public void playSoundShort(Sound sonido) {
        playSound(soundMap.get(sonido));
    }
}
