## A Movie Trailer plugin for your SageTV system ##
**Current Version: beta 1.07**


## About ##

This is the homepage for Ortus trailers which allows you to download, store and watch the latest theatrical trailers using your SageTV system.

## Features ##

  * User defined refresh time
  * Queue multiple downloads
  * Multiple view styles (pc only)
  * User defined buffering options
  * Enable/Disable various screen items
  * Keep Max trailers / auto delete oldest
  * Keeps track of orphaned trailers/artwork
  * User defined download resolutions (if available)
    1. 1080p
    1. 720p
    1. 480p
    1. Standard Definition
  * User defined time for Auto downloading of newest trailers (not in beta version)
  * Auto download of all trailer info including poster's during start up for quicker access

## Requirements ##
  * SageTV beta version 7.0.12 or greater
  * Internet Connection

## Providers ##
  * Apple Movie Trailers (Status: beta)
  * You Tube Trailers (Status: Not for default UI)
  * Game Trailers.com (Status: Not for default UI)

## Screenshots ##

<a href='http://img267.imageshack.us/img267/2702/ortustrailershome.jpg'><img src='http://img267.imageshack.us/img267/2702/ortustrailershome.th.jpg' border='0' /></a>
<a href='http://img293.imageshack.us/img293/9232/ortustrailerslist.jpg'><img src='http://img293.imageshack.us/img293/9232/ortustrailerslist.th.jpg' border='0' /></a>
<a href='http://img535.imageshack.us/img535/5066/ortustrailerswall.jpg'><img src='http://img535.imageshack.us/img535/5066/ortustrailerswall.th.jpg' border='0' /></a>
<a href='http://img443.imageshack.us/img443/3269/ortustrailersoptions.jpg'><img src='http://img443.imageshack.us/img443/3269/ortustrailersoptions.th.jpg' border='0' /></a>


## Button Configuration ##
All button press only work when your highlighting a trailer item either on the list or poster view. Various buttons have different actions depending on your preferred setup and if the highlighted trailer has been previously download/stored.


<u>Universal Buttons</u>


**Delete:** Deletes the currently highlighted trailer. <br>
<b>Stop:</b> Cancels the currently highlighted trailer download. <br>
<b>Info:</b> Opens the detailed description panel with Play, Download, Cancel Download and Delete options.<br>
<br>
<br>
<u>Standard Setup</u>


<b>Play/Select/Enter/Mouse Click:</b> Opens the detailed description panel which may contain options to Play, Download, Cancel Download or Delete Trailer.<br>
<br>
<br>
<u>Click 'n' Play</u>


<b>Play/Select/Enter/Mouse Click:</b> Starts playback of pre-downloaded trailers or if the trailer has not yet been downloaded will initiate the download procedure with playback starting a second or two later.<br>
<br>
<br>
<u>Buffered Playback</u>


<b>Play/Select/Enter/Mouse Click:</b> Opens the detailed description panel which may contain options to Play, Download, Cancel Download or Delete Trailer.<br>
<br>
<br>
<br>
<h2>Options Explained</h2>

<h4><u>Resolution</u></h4>
You can change the trailer resolution to suit your broadband speed, if the selected resolution is not available the next resolution will be automatically tried.<br>
<h4><u>Maximum MPAA Rating</u></h4>
Change parental controls for allowing viewing of inappropriate material, default setting is "All". Not Yet Rated (NR) trailers will be treated as MPAA rating Restricted (R), you can further enhance this setting by enabling the "Lock Setup Menu's" option found in the Sage "Parental Controls" options.<br>
<h4><u>Trailer Update Interval</u></h4>
The trailer list will be automatically updated depending on the time limit set (default 360 minutes). The list will update each time you enter the trailer screen if the set time has been exceeded.<br>
<h4><u>Keep Max Trailers</u></h4>
Set this if you don't want to keep every trailer you download (default 10). Once the limit has been reached the oldest trailer will be deleted automatically.<br>
<h4><u>Enable Buffered Playback</u></h4>
If this option is enabled playback will begin automatically once the set buffered limit has been reached. This is handy if you have a slow broadband connection but still want to enjoy 1080p trailers. If this option and Click 'n' Play is disabled all downloads will be queued so you can watch them at a later time.<br>
<h4><u>Click 'n' Play</u></h4>
If you would rather not see the trailer detailed descriptions or if you have a lazy remote pushing finger enable this option, playback of the highlighted trailer will begin within a couple of seconds. This option is also dependant on the trailer resolution setting so if you find playback becomes too intermittent lower the download resolution. If playback is still poor you maybe better off using the Buffered Playback method. If this option and Buffered Playback is disabled all downloads will be queued so you can watch them at a later time.<br>
<h4><u>Force Trailer Update</u></h4>
If you would prefer not to wait for the timed auto update you can manually refresh the list at anytime.<br>
<h4><u>Unfocused Transparency</u></h4>
This option only becomes available when running on a client pc (as the wall view is pc only due to speed issues). Default is set to 0.4 but if you find that you prefer your unfocused posters to be a little bit more visible then increase this value.<br>
<h4><u>Misc Options</u></h4>
Allows you to enable or disable screen items or animations which can improve speed when running on HD extenders. You can also adjust the text size for various screen areas here.<br>
<h4><u>Change Base Path (Download Path)</u></h4>
Alter where trailers, posters & the apple xml file gets downloaded (default is: SageTV\SageTV\STVs\Ortus\Trailers). This option is handy for those running Windows Home Server and dont want to fill their C drive with trailers.<br>
<h4><u>Download All Trailers</u></h4>
Use this option if you would like to download all available trailers. This setting is dependant on the resolution you've set and will automatically adjust the max trailer count to allow for the additional trailers. Once you accept this setting you will be unable to cancel the downloads until all the download links have been located and added to the queue. Once the queue is complete the option will change to allow you to cancel the downloads. Current downloads will be limited to a maximum of 5 at anyone time, once one download completes another will begin. If the trailer already exists on your system it will be skipped.<br>
<h4><u>Trailer Statistics</u></h4>
Here you can find where your current download path is for trailers, posters etc as well as other useful information like disc space used. If you find you have orphaned trailers or posters an option will become available to clean (delete) them.<br>
<br>
<h2>Extender Speed Tips</h2>

<ol><li>Basically disable all animations and screen items.</li></ol>

<h2>Known Issues</h2>

<ol><li>Wont work with any other main menu STVi's which change the default layout.</li></ol>

<h2>Orphaned Trailers & Posters</h2>

Orphaned trailers and posters will happen when Apple update their trailer xml file, so as new trailers get added old ones drop off the list. Once these trailers are no longer available we loose all the details for that trailer as there is no way to store this information....other than adding them to the Sage database\videos section or the Ortus database which we wont be doing for the default UI. Once this happens you will no longer be able to view these trailers in SageTV.<br>
<br>
<h2>Future Enhancements</h2>

Scheduling of trailer downloads.