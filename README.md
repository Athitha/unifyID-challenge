## Build

1.- Clone the repository

2.- Open Android Studio(2.2.1)

3.- From Android Studio open the cloned project by selecting 'Open an existing Android Studio Project'.

4.- Go to Run menu and press Run app.

5.- Select the device in which the applications will start.



## Further considerations

I had troubles trying to make the camera work on portrait mode as well as selecting the right method to keep the pictures save. Another big problem was find a way to manage the resources, specially the camera, and not to running out of memory.

Since I'm using *steganography* to keep the images safe from bad guys the next is a list of aspects to improve:



## TODO

Execute the *steganography* process in a background task to avoid the Activity life cycle.

Change the way I'm handling resources because it leaks memory.