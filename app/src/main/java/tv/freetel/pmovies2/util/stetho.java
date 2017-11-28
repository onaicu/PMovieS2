package tv.freetel.pmovies2.util;


import android.app.Application;

public class Stetho extends Application {    public void onCreate() {
    super.onCreate();
    com.facebook.stetho.Stetho.initializeWithDefaults(this);
}

    protected void initStetho() {
        com.facebook.stetho.Stetho.initialize(
                com.facebook.stetho.Stetho.newInitializerBuilder(this)
                        .enableDumpapp(com.facebook.stetho.Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(com.facebook.stetho.Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
