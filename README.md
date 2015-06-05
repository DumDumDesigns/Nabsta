Nabsta is a multi track audio recording application to run on android platforms. 
It will  behave similar to a four track machine or track editor used to dub tracks for music creation. 

Nabsta is still in development / testing phases with.
 
Below is a screenshot as of 6/4/2015. 

![](https://github.com/samuelsegal/Nabsta/blob/master/nabsta_6_5_15_screenshot.png)

Note: Currently it is required to use headphones to avoid echo from speakers top microphone. Though without headphones an effect is created that may be appealing to some.

Below is description of current functionality to be tested.

1. Project creation through action bar.
2. Tracks can be added to project. Waveforms are displayed in realtime for tracks with record button turned on.
3. Waveform bitmaps are saved and displayed for tracks already recorded and record button turned off.
4. Mute button for each track can mute the individual trck during playback. (currently only works while tracks already playing, this will be fixed soon.
5. delete track capability will be added very soon.
6. A default project is loaded at first run of app.
7. Song currently in session is reloaded on next run of app.

To be developed:
1. A track bar to be displayed following the playback to share location in time of song.
2. Loading of audio tracks of any format and waveform to be generated for display.
3. Mix down of selected tracks to one master track including pan and surround sound effect.
4. Other effects such as reverb to be added.
5. Currently, playback of tracks recording in realtime is through a seperate audioTrack playing buffer as it is read in through audioRecord. This causes latency in what is heard while recording. Better solution is needed.
6. Ability to share tracks through Share Intents.

More ideas after completion of above.


