# SecureCameraIntent

Android example of:

Taking Photos Simply
https://developer.android.com/training/camera/photobasics.html

Sample code of how to take a picture and storing the image in private data cache. 

I finally got it to work. Turns out that
the line of code:
    mCurrentPhotoPath = "file:/" + image.getAbsolutePath();

and the line of code:
    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

were not compatible.     
	"file:/" build a uri to the file
	where BitmapFactory.decodeFile just wants the path to the file.

When I removed "file:" it worked.

Tested on 
   Nexus 7 (2015) running Android 6

