package org.lowioo.quill;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.speech.tts.TextToSpeech;

public class MainActivity extends Activity {

    private WebView webView;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, status -> {
            ttsReady = status == TextToSpeech.SUCCESS;
        });

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.addJavascriptInterface(new QuillBridge(), "QuillNative");

        setContentView(webView);
        webView.loadUrl("file:///android_asset/quill.html");
    }

    public class QuillBridge {

        @JavascriptInterface
        public void speak(String text) {
            if (ttsReady && text != null) {
                tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "quill"
                );
            }
        }

        @JavascriptInterface
        public void stop() {
            if (tts != null) {
                tts.stop();
            }
        }

        @JavascriptInterface
        public boolean isReady() {
            return ttsReady;
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
