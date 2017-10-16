package tv.freetel.pmovies2.util;


//Create a new class called MyApplication and override its onCreate method:

import android.app.Application;

import com.facebook.stetho.Stetho;

public class AppStethoTest extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
