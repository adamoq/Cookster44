package com.example.adamo.cookster;

public final class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "MyFontAsset.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "MyFontAsset2.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");
    }
}
