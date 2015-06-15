### Nabsta multi track audio recording application to run on android devices. 
It will  behave similar to a four track machine or track editor used to dub tracks for music creation. 

Nabsta is still in development / testing phases.
 
Below is a screenshot as of 6/4/2015. 

![](https://github.com/samuelsegal/Nabsta/blob/master/nabsta_6_5_15_screenshot.png)

Note: Currently it is required to use headphones to avoid echo from speakers to microphone. Though without headphones an effect is created that may be appealing to some. 

Currently there is a noticable delay in record mode. This will be fixed.

Below is description of current functionality to be tested.

1. Project creation through action bar.
2. Tracks can be added to project. Waveforms are displayed in realtime for tracks with record button turned on.
3. Waveform bitmaps are saved and displayed for tracks already recorded and record button turned off.
4. Mute button for each track can mute the individual trck during playback. 
5. Tracks can be deleted
6. A default project is loaded at first run of app.
7. Song currently in session is reloaded on next run of app.
8. Mix down of selected tracks to one master track. (Currently only .wav is supported)

To be developed:

1. A track bar to be displayed following the playback to share location in time of song.
2. Loading of audio tracks of any format and waveform to be generated for display.
3. Mix down of selected tracks to one master track including pan and surround sound effect and all known file types.
4. Other effects such as reverb to be added.
5. Currently, playback of tracks recording in not so real time is through a separate audioTrack play buffer as it is read in through audioRecord. This causes latency in what is heard while recording. Better solution is needed.
6. Ability to share tracks through Share Intents.
7. Undo / Redo
8. Back up song, like save project state continue with ability to pull up saved state.

More ideas after completion of above.


