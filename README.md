[![](https://jitpack.io/v/bluenumberfoundation/humanid-android-sdk.svg)](https://jitpack.io/#bluenumberfoundation/humanid-android-sdk)

# humanID Android SDK



<p align="center">
<img src="https://github.com/bluenumberfoundation/humanid-android-sdk/blob/master/human-id-logo.png" width="200" height="200">
</p>

<p align="center">
<a href="https://github.com/bluenumberfoundation/humanid-documentation/edit/master/README.md">General Documentation</a> •
<a href="https://github.com/bluenumberfoundation/humanid-android-sdk/wiki">Wiki</a> • 
<a href="https://github.com/bluenumberfoundation/humanid-android-sdk/wiki/integration">Integration</a> •
<a href="https://github.com/bluenumberfoundation/humanid-documentation/blob/master/contributing.md">Contributing</a> • 
<a href="https://github.com/bluenumberfoundation/humanid-documentation/blob/master/gallery.md">Gallery</a> • 
<a href="https://github.com/bluenumberfoundation/humanid-documentation/blob/master/faq.md">FAQ</a>

<p align="center">
Meet humanID - An anonymous online identity, enabling platforms to offer the speed and comfort of social logins, while guaranteeing absolute privacy and protecting our communities by permanently blocking bots, spams, and trolls.
</p>


## Requirements
<ul>
	<li>API level 18 or higher</li>
	<li>Kotlin</li>
</ul>

Please update to the latest SDK!

## Downloads

    allprojects {
	    repositories {
	    ...
		    maven { url 'https://jitpack.io' }
	    }
	}
	.
	.
	.
    dependencies {
    	implementation 'com.github.bluenumberfoundation:humanid-android-sdk:0.0.5’
    }


## Credentials Access

Get the appId and appSecret by dropping us an email [developers@human-id.org](mailto:developers@human-id.org).

## Configuration

Add these codes into your AndroidManifest.xml and make sure all meta-data is fulfilled.

    <meta-data  
      android:name="com.humanid.sdk.applicationIcon"  
      android:resource="@drawable/ic_app_icon"/>  
    <meta-data
      android:name="com.humanid.sdk.applicationName"
      android:value="@string/app_name"/>
    <meta-data  
      android:name="com.humanid.sdk.applicationId"
      android:value="YOUR_APP_ID"/>
    <meta-data
      android:name="com.humanid.sdk.applicationSecret" 
      android:value="YOUR_APP_SECRET"/>
      

## Android SDK in Kotlin

Add these codes into your Activity or Fragment file, we recommend you wrap this in a function that handles the login button.

    LoginManager.getInstance(this).registerCallback(object : LoginCallback {  
	      override fun onCancel() {  }
	    
        override fun onSuccess(exchangeToken: String) {  
    		//todo: send the exchangeToken to your server  
	      }  
	    
        override fun onError(errorMessage: String) {  } 
	    })  
	    ...  
	    ..  
	    .  
	    override
	    fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?) { 
		  LoginManager.getInstance(this).onActivityResult(requestCode, resultCode, data) 
		  super.onActivityResult(requestCode, resultCode, data)  
	    }

## Android SDK in Java

Add these codes into your Activity or Fragment file, we recommend you wrap this in a function that handles the login button.

    LoginManager.INSTANCE.getInstance(this).registerCallback(new LoginCallback() {
	    @Override
	    public void onCancel() {}
	    
      @Override
	    public void onSuccess(@NotNull String exchangeToken) {
	      //todo: send the exchangeToken to your server
	    }
	    
      @Override
	    public void onError(@NotNull String errorMessage) {}
    
    });
    ...  
    ..  
    .  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		 LoginManager.INSTANCE.getInstance(this).onActivityResult(requestCode, resultCode, data);
		 super.onActivityResult(requestCode, resultCode, data);
	 }
    
## You're set!

Now you can integrate your Android app to humanID. See the full [sample](https://github.com/bluenumberfoundation/humanid-android-sdk/tree/master/sample) here to learn more.


## License

Copyright 2019-2020 Bluenumber Foundation
Licensed under the GNU General Public License v3.0 [(LICENSE)](client/LICENSE)

